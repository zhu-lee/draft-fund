package lee.fund.common.app;

import lee.fund.common.Server;
import lee.fund.common.config.Configuration;
import lee.fund.common.container.ServiceContainer;
import lee.fund.common.netty.server.NettyServer;
import lee.fund.common.netty.server.ServerConfig;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/28 16:47
 * Desc:
 */
public abstract class AbstractServer implements Server{
    private NettyServer nettyServer;
    private ServiceContainer serviceContainer;

    public AbstractServer(ServerConfig serverConfig) {
        this.nettyServer = new NettyServer(serverConfig);
        this.serviceContainer = new ServiceContainer();
    }

    @Override
    public void start() {
        nettyServer.start();
    }

    @Override
    public void shutdown() {
        nettyServer.shutdown();
    }

    @Override
    public void exposeService(Class<?> clazz, Object instance) {
//        if (RpcClient.isProxy(instance)) {
//            throw new RuntimeException(String.format("can't register a proxy object as service [%s], this will cause dead circulation", clazz.getName()));
//        }
//        RpcService rpcService = clazz.getAnnotation(RpcService.class);
//        String description = rpcService == null ? "" : rpcService.description();
//        NamingConvention convention = rpcService == null ? NamingConvention.PASCAL : rpcService.convention();
//
//        String name = null;
//        if (rpcService != null) {
//            name = rpcService.name();
//        }
//        if (StrKit.isBlank(name)) {
//            name = clazz.getSimpleName();
//        }
//
//        this.registerService(clazz, instance, name, description, convention);

        this.nettyServer.setServiceContainer(this.serviceContainer);
    }

    @Override
    public void register() {

    }
}
