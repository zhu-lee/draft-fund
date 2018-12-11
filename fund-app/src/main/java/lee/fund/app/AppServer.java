package lee.fund.app;

import lee.fund.remote.app.server.RemoteServer;
import lee.fund.remote.app.server.ServerConfiguration;


/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/23 17:20
 * Desc:
 */
public class AppServer extends RemoteServer {

    public AppServer(ServerConfiguration configuration){
        super(configuration);
    }
}
