package lee.fund.remote.app.client;

import lee.fund.remote.app.FailModeEnum;
import lee.fund.remote.container.ServiceInfo;
import lee.fund.remote.container.ServiceMeta;
import lee.fund.remote.exception.RpcError;
import lee.fund.remote.exception.RpcException;
import lee.fund.remote.exception.RpcExceptions;
import lee.fund.remote.netty.client.Invoker;
import lee.fund.remote.netty.client.RemoteInvoker;
import lee.fund.remote.netty.client.RemoteInvokerFactory;
import lee.fund.remote.registry.JetcdRegistry;
import lee.fund.util.config.ClientConf;
import lee.fund.util.config.ClientConfHandler;
import lee.fund.util.jetcd.Provider;
import lee.fund.util.lang.FaultException;
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
        return (T) remoteProxyMap.computeIfAbsent(key, k -> new RemoteProxy(RemoteCallExecutor.get(server), clazz, serviceInfo).instance());
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
            if (methodInfo == null) {
                return method.invoke(this, args);
            }
            return callExecutor.doInvoke(serviceName, methodInfo, args, method.getReturnType());
        }
    }

    //remote call executor
    private static class RemoteCallExecutor {
        private static final ConcurrentMap<String, RemoteCallExecutor> remoteCallExecutorMap = new ConcurrentHashMap<>();
        private static final JetcdRegistry jetcdRegistry = JetcdRegistry.getInstance();
        private List<Invoker> invokers = new ArrayList<>();
        private final String server;
        private ClientConfiguration clientConf;
        private boolean obtainInvokers;
        private final InvokerBalancer invokerBalancer;
        private int maxRetry = 2;

        public RemoteCallExecutor(String server) {
            this.server = server;
            Map<String, ClientConf> clientConfMap = ClientConfHandler.instance().getClientConfMap();
            if (clientConfMap.containsKey(server)) {
                this.clientConf = new ClientConfiguration(clientConfMap.get(server));
                this.maxRetry = this.clientConf.getMaxRetry();
            }
            this.invokerBalancer = InvokerBalancer.get(null);
        }

        public static RemoteCallExecutor get(String server) {
            return remoteCallExecutorMap.computeIfAbsent(server, RemoteCallExecutor::new);
        }

        public Object doInvoke(String serviceName, ServiceInfo.MethodInfo methodInfo, Object[] args, Class<?> returnType) {
            List<Invoker> invokers = this.getInvokers();
            Invoker selInvoke = this.invokerBalancer.select(invokers);
            List<FaultException> faults;
            try {
                return selInvoke.invoke(serviceName, methodInfo.getName(), args, returnType);
            } catch (FaultException e) {
                if (methodInfo.getFailMode() == FailModeEnum.FailFast
                        || invokers.size() == 1 || this.maxRetry == 0) {
                    throw e;
                }
                faults = new ArrayList<>(invokers.size());
                faults.add(e);
            }

            int i = 1;
            for (Invoker invoker : invokers) {
                if (invoker == selInvoke) {
                    continue;
                }
                i++;
                try {
                    return invoker.invoke(serviceName, methodInfo.getName(), args, returnType);
                } catch (FaultException e) {
                    faults.add(e);
                }
                if (i > maxRetry) {
                    break;
                }
            }

            RpcError rpcError = RpcError.CLIENT_ALL_NODES_FAILED;
            FaultException fex = new FaultException(rpcError.getCode(), String.format(rpcError.description(), serviceName, methodInfo.getName()));
            RpcExceptions.putNodeFaults(fex, faults);
            throw fex;
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

        /**
         * providers有变时，更新
         *
         * @param providers
         */
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

    //is rpc proxy
    public static boolean isProxy(Object instance) {
        if (Proxy.isProxyClass(instance.getClass())) {
            return Proxy.getInvocationHandler(instance) instanceof RemoteInvoker;
        }
        return false;
    }
}
