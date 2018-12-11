package lee.fund;

import lee.fund.remote.app.server.ServerConfiguration;
import lee.fund.app.Application;
import org.junit.Test;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/26 9:21
 * Desc:
 */
public class RpcApplicationTest {
    @Test
    public void testRpcApplication() {
        //port range 1～65535
        ServerConfiguration configuration = new ServerConfiguration();
        Application rpcApplication = new Application(RpcApplicationTest.class, configuration, null);
        rpcApplication.run();
    }
}
