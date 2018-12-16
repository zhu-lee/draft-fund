package lee.fund.remote.app.client;

import lee.fund.remote.container.ServiceInfo;
import lee.fund.remote.container.ServiceMeta;
import lee.fund.remote.exception.RpcError;
import lee.fund.remote.exception.RpcException;
import lee.fund.remote.netty.client.Invoker;
import lee.fund.remote.netty.client.RemoteInvokerFactory;
import lee.fund.remote.registry.JetcdRegistry;
import lee.fund.remote.registry.Provider;
import lee.fund.util.config.AppConf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/10 18:43
 * Desc:
 */
public final class RemoteClient {
    private final static Logger logger = LoggerFactory.getLogger(RemoteClient.class);
    private final static ConcurrentMap<String, Object> remoteProxyMap = new ConcurrentHashMap<>();

    public static <T> T get(String server, Class<T> clazz) {
        ServiceInfo serviceInfo = ServiceMeta.instance().get(clazz);
        String key = String.format("%s.%s", server, serviceInfo.getName());
        if (remoteProxyMap.containsKey(key)) {
            return (T) remoteProxyMap.get(clazz);
        }
        RemoteProxy remoteProxy = new RemoteProxy(RemoteCallExecutor.get(server), clazz, serviceInfo).instance();
        remoteProxyMap.put(key, remoteProxy);
        return (T) remoteProxy;
    }

    private static class RemoteProxy implements InvocationHandler {
        private final RemoteCallExecutor callExecutor;
        private final Class<?> clazz;
        private final String serviceName;
        private final Map<String, ServiceInfo.MethodInfo> methodMap;

        public RemoteProxy(RemoteCallExecutor callExecutor, Class<?> clazz, ServiceInfo serviceInfo) {
            this.callExecutor = callExecutor;
            this.clazz = clazz;
            this.serviceName = serviceInfo.getName();
            this.methodMap = serviceInfo.getMethodMap();
        }

        public <T> T instance() {
            return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (Object.class == method.getDeclaringClass()) {
                return method.invoke(this, args);
            }
            ServiceInfo.MethodInfo methodInfo = methodMap.get(method.getName());
            if (methodInfo==null) {
                return method.invoke(this, args);
            }
            return callExecutor.doInvoke(serviceName, method.getName(), args, method.getReturnType());
        }
    }

    private static class RemoteCallExecutor {
        private static final ConcurrentMap<String, RemoteCallExecutor> recMap = new ConcurrentHashMap<>();
        private static final JetcdRegistry jetcdRegistry = JetcdRegistry.getInstance();
        private List<Invoker> invokers = new ArrayList<>();
        private final String server;
        private ClientConfiguration clientConf;
        private boolean obtainInvokers;
        private final InvokerBalancer invokerBalancer;

        public RemoteCallExecutor(String server) {
            this.server = server;
            if (AppConf.instance().getCsumConfs().containsKey(server)) {
                this.clientConf = new ClientConfiguration(AppConf.instance().getCsumConfs().get(server));
            }
            this.invokerBalancer = InvokerBalancer.get(null);
        }

        public static RemoteCallExecutor get(String server) {
            return recMap.computeIfAbsent(server, RemoteCallExecutor::new);
        }

        public Object doInvoke(String service, String method, Object[] args, Class<?> returnType) {
            List<Invoker> invokers = this.getInvokers();
            Invoker invoker = this.invokerBalancer.select(invokers);
            try {
                return invoker.invoke(service, method, args, returnType);
            } catch (Exception e) {

            }

        }

        public List<Invoker> getInvokers() {
            if (!obtainInvokers) {
                synchronized (this) {
                    if (!obtainInvokers) {
                        this.obtainInvokers();
                        obtainInvokers = true;
                    }
                }
            }
            return invokers;
        }

        private void obtainInvokers() {
            if (clientConf == null || clientConf.isDiscovery()) {
                List<Provider> providers = jetcdRegistry.lookup(server);
                if (providers != null && !providers.isEmpty()) {
                    this.genInvokers(providers);
                    jetcdRegistry.watchKey(server, true, this::genInvokers);
                } else {
                    logger.info("从注册中心未获取任何属于服务 {} 的节点", server);
                }
            }

            if (invokers.isEmpty()) {
                if (clientConf == null) {
                    logger.error("尝试直连，但没找到名为 {} 的服务配置", server);
                    throw new RpcException(RpcError.CLIENT_NO_PROVIDER, server);
                } else {
                    logger.info("尝试直连直连服务 {} {} ", server, clientConf.getAddress());
                    invokers.add(this.createInvoke(clientConf));
                }
            }
        }

        private void genInvokers(List<Provider> providers) {
            invokers = providers.stream().map(t -> {
                ClientConfiguration clientConfiguration;
                if (clientConf == null) {
                    clientConfiguration = new ClientConfiguration(t);
                } else {
                    clientConfiguration = new ClientConfiguration(clientConf, t);
                }
                return this.createInvoke(clientConfiguration);
            }).collect(Collectors.toList());
        }

        private Invoker createInvoke(ClientConfiguration clientConf) {
            return RemoteInvokerFactory.getRemoteInvoker(clientConf);
        }
    }
}
