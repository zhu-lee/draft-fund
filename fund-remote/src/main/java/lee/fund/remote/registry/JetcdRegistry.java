package lee.fund.remote.registry;

import com.alibaba.fastjson.JSON;
import com.coreos.jetcd.data.KeyValue;
import lee.fund.util.jetcd.JetcdCall;
import lee.fund.util.jetcd.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Supplier;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/7 15:00
 * Desc:
 */
public class JetcdRegistry extends JetcdCall {
    private final Logger logger = LoggerFactory.getLogger(JetcdRegistry.class);
    private static JetcdRegistry instance;
    private static Object obj = new Object();

    private JetcdRegistry() {
    }

    public static JetcdRegistry getInstance() {
        if (instance == null) {
            synchronized (obj) {
                if (instance == null) {
                    instance = new JetcdRegistry();
                }
            }
        }
        return instance;
    }

    public void register(Supplier<Provider> supplier) {
        Provider provider = supplier.get();
        if (provider == null) {
            logger.error("register failed: supplier return null");
        } else {
            this.register(provider);
        }
    }

    private void register(Provider provider) {
        try {
            String offlineNodePath = this.getOfflineNodePath(provider.getName(), provider.getAddress());
//            logger.info("query offlineNodePath={}",offlineNodePath);
            List<KeyValue> keyValues = this.getNodes(offlineNodePath);
            if (keyValues != null && !keyValues.isEmpty()) {
                logger.info("register failed，node [{} - {}] is offline", provider.getName(), provider.getAddress());
                return;
            }

            String nodePath = this.getNodePath(provider.getName(), provider.getAddress());
            logger.info("query nodePath={}",nodePath);
            String JsonValue = JSON.toJSONString(provider);
            this.setNodeWithLease(nodePath, JsonValue);
            logger.info("register success：{}", JsonValue);
        } catch (Exception e) {
            logger.error("register error：node [{} - {}]", provider.getName(), provider.getAddress(), e);
            return;
        }
    }
}
