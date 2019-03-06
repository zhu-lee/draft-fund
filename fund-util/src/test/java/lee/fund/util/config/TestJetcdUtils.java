package lee.fund.util.config;

import lee.fund.util.jetcd.JetcdClient;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/8 12:59
 * Desc:
 */
public class TestJetcdUtils {
    private final Logger logger = LoggerFactory.getLogger(TestJetcdUtils.class);

    @Test
    public void testAction() throws Exception {
//        JetcdUtils.getClient();
//        JetcdClient.setNodeWithLease("lizhu","dddddd");
        logger.info("连接成功...");
//        Thread.currentThread().sleep(20000);
        System.in.read();
    }
}
