package lee.fund.util.config;

import com.coreos.jetcd.data.KeyValue;
import lee.fund.util.jetcd.JetcdClient;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
        JetcdClient instance = JetcdClient.getInstance();
        List<KeyValue> list =  instance.getNodesWithPrefix("/service/fund-example/providers/192.168.210.31:9268");
        List<KeyValue> list2 =  instance.getNodesWithPrefix("/service/fund-example/providers");
//        instance.setNodeWithLease("lizhu","dddddd");
        logger.info("连接成功...");
//        Thread.currentThread().sleep(20000);
        System.in.read();
    }
}
