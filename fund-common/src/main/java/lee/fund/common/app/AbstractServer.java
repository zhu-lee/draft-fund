package lee.fund.common.app;

import lee.fund.common.Server;
import lee.fund.common.annotation.RpcService;
import lee.fund.common.container.ServiceContainer;
import lee.fund.common.netty.server.NettyServer;
import lee.fund.common.netty.server.ServerConfig;
import lee.fund.util.lang.StrKit;

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
        this.appExposeService();
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
        this.serviceContainer.storeService(clazz, instance);
        this.nettyServer.setServiceContainer(this.serviceContainer);
    }

    @Override
    public void register() {

    }

    protected void appExposeService() {
        //TODO 初始化暴露些系统服务
    }
}
