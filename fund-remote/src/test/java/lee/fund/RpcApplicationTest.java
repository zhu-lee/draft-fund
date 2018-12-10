package lee.fund;

import lee.fund.common.config.Configuration;
import lee.fund.remote.application.RemoteApplication;
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
        Configuration configuration = new Configuration();
        RemoteApplication rpcApplication = new RemoteApplication(RpcApplicationTest.class, configuration, null);
        rpcApplication.run();
    }
}
