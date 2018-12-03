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
}
