package lee.fund.remote.app.client;

import lee.fund.remote.container.ServiceInfo;
import lee.fund.remote.container.ServiceMeta;
import lee.fund.remote.netty.client.Invoker;
import lee.fund.util.config.AppConf;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/10 18:43
 * Desc:
 */
public final class RemoteClient {
    private static final ConcurrentHashMap<String, Object> remoteProxyMap = new ConcurrentHashMap<>();

    public static <T> T get(String server, Class<T> clazz) {
        //创建代理类
        //代理类里有执行器缓存invokers，如果没有则从配置中心或本地取(定时更新invokers)，取完后缓存
        //代理器里serverInfo
        ServiceInfo svcInfo = ServiceMeta.instance().get(clazz);
        String key = String.format("%s.%s", server, svcInfo.getName());
        if (remoteProxyMap.containsKey(key)) {
            return (T) remoteProxyMap.get(clazz);
        }
        RemoteProxy remoteProxy = new RemoteProxy(server, clazz, svcInfo).instance();
        remoteProxyMap.put(key, remoteProxy);
        return (T) remoteProxy;
    }

    private static class RemoteProxy implements InvocationHandler {
        private final String server;
        private final Class<?> clazz;
        private final String srvName;
        private final Map<String, ServiceInfo.MethodInfo> methodMap;

        public RemoteProxy(String server, Class<?> clazz, ServiceInfo svcInfo) {
            this.server = server;
            this.clazz = clazz;
            this.srvName = svcInfo.getName();
            this.methodMap = svcInfo.getMethodMap();
        }

        public <T> T instance() {
            return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (Object.class == method.getDeclaringClass()) {
                return method.invoke(this, args);
            }
            if (!methodMap.containsKey(method.getName())) {
                return method.invoke(this, args);
            }

            return null;
        }
    }

    private static class RemoteExctContainer {
        private static ConcurrentHashMap<String, RemoteExctContainer> recMap = new ConcurrentHashMap<>();
        private List<Invoker> invokers = new ArrayList<>();
        private String server;
        private ClientConfiguration clientConf;
        private boolean isProvider;

        public RemoteExctContainer(String server) {
            this.server = server;
            if (AppConf.instance().getCsumConfs().containsKey(server)) {
                this.clientConf = new ClientConfiguration(AppConf.instance().getCsumConfs().get(server));
            }
        }

        public static RemoteExctContainer get(String server) {
            return recMap.computeIfAbsent(server, RemoteExctContainer::new);
        }

        public List<Invoker> getInvokers() {
            if (!isProvider) {

            }
            return invokers;
        }
    }
}
