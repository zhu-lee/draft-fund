package lee.fund.common.registry;

import com.alibaba.fastjson.JSON;
import com.coreos.jetcd.Watch;
import com.coreos.jetcd.data.KeyValue;
import com.coreos.jetcd.watch.WatchEvent;
import lee.fund.util.config.JetcdUtils;
import lee.fund.util.excute.Cycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/7 15:00
 * Desc:
 */
public class JetcdRegistry {
    private final Logger logger = LoggerFactory.getLogger(JetcdRegistry.class);
    private static JetcdRegistry instance = new JetcdRegistry();
    private final int TTL_SECONDS = 60;

    private JetcdRegistry() {
    }

    public static JetcdRegistry getInstance() {
        return instance;
    }

    public void register(Supplier<Provider> supplier) {
        Cycle.set(() -> {
            Provider provider = supplier.get();
            if (provider == null) {
                logger.error("register failed: supplier return null");
            } else {
                this.register(provider);
            }
        }, 0, TTL_SECONDS * 1000L);
    }

    private void register(Provider provider) {
        // 检查节点是否下线
        try {
            String key = this.getOfflineNodePath(provider);
            logger.info(key);
            List<KeyValue> keyValues = JetcdUtils.getNodes(key);
            if (keyValues != null && !keyValues.isEmpty()) {
                logger.info("register failed, node [{} - {}] is offline", provider.getName(), provider.getAddress());
                return;
            }
        } catch (Exception e) {
            logger.error("register failed", e);
            return;
        }

        // 注册
        try {
            String key = this.getNodePath(provider.getName(), provider.getAddress());
            logger.info(key);
            String value = JSON.toJSONString(provider);
            JetcdUtils.setNodeWithLease(key, value);
            logger.info("register success: {}", value);
        } catch (Exception e) {
            logger.error("register failed", e);
        }
    }

    // 下线节点路径
    private String getOfflineNodePath(Provider provider) {
        return String.format("/service/%s/offlines/%s", provider.getName(), provider.getAddress());
    }

    // 服务节点路径
    private String getNodePath(String name, String address) {
        return String.format("/service/%s/providers/%s", name, address);
    }

    public void watchKey(String key, Boolean usePrefix, BiConsumer<String, List<WatchEvent>> callback) {
        Executors.newSingleThreadExecutor(r -> new Thread(r, "Watch_Key")).execute(() -> {
            try {
                Watch.Watcher watcher;
                if (usePrefix) {
                    watcher = JetcdUtils.getWatcherWithPrefix(key);
                } else {
                    watcher = JetcdUtils.getWatcher(key);
                }
                List<WatchEvent> events = watcher.listen().getEvents();
                callback.accept(key, events);
            } catch (Exception e) {
                logger.error(String.format("Watch etcd key:{}，error", key), e);
            }
        });
    }

    public void updateProviders(String server, List<WatchEvent> events) {

    }

    public interface WatchCallback {
        void callback(String server, List<WatchEvent> events);
    }
}
