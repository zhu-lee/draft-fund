package lee.fund.app;

import lee.fund.remote.app.RemoteServer;
import lee.fund.remote.config.Configuration;


/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/23 17:20
 * Desc:
 */
public class AppServer extends RemoteServer {

    public AppServer(Configuration configuration){
        super(configuration);
    }
}
