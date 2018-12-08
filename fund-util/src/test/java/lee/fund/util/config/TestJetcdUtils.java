package lee.fund.util.config;

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
    public void testAction() {
        JetcdUtils.getClient();
        logger.info("连接成功...");

    }
}
