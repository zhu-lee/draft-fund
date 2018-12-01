package lee.fund.remoting.application;

import lee.fund.common.app.AbstractServer;
import lee.fund.common.config.Configuration;
import lee.fund.common.netty.server.ServerConfig;


/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/23 17:20
 * Desc:
 */
public class RpcServer extends AbstractServer{

    public RpcServer(Configuration configuration){
        super(new ServerConfig(configuration));
    }

    @Override
    public void exposeService(Class<?> clazz, Object instance) {
        super.exposeService(clazz,instance);
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    @Override
    public void register() {

    }
}
