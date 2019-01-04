package lee.fund.remote.registry;

import com.alibaba.fastjson.JSON;
import com.coreos.jetcd.Watch;
import com.coreos.jetcd.data.KeyValue;
import com.coreos.jetcd.watch.WatchEvent;
import lee.fund.util.config.JetcdUtils;
import lee.fund.util.execute.Cycle;
import lee.fund.util.execute.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
    private AtomicInteger threadIndex = new AtomicInteger(0);

    private JetcdRegistry() {
    }

    public static JetcdRegistry getInstance() {
        return instance;
    }

    public void register(Supplier<Provider> supplier) {
        //TODO 这里用定时更新呢，无意义
//        Cycle.set(() -> {
            Provider provider = supplier.get();
            if (provider == null) {
                logger.error("register failed: supplier return null");
            } else {
                this.register(provider);
            }
//        }, 0, TTL_SECONDS * 1000L);
    }

    private void register(Provider provider) {
        try {
            String offlineNodePath = this.getOfflineNodePath(provider.getName(), provider.getAddress());
            logger.info(offlineNodePath);
            List<KeyValue> keyValues = JetcdUtils.getNodes(offlineNodePath);
            if (keyValues != null && !keyValues.isEmpty()) {
                logger.info("register failed, node [{} - {}] is offline", provider.getName(), provider.getAddress());
                return;
            }

            String nodePath = this.getNodePath(provider.getName(), provider.getAddress());
            logger.info(nodePath);
            String value = JSON.toJSONString(provider);
            JetcdUtils.setNodeWithLease(nodePath, value);
            logger.info("register success: {}", value);
        } catch (Exception e) {
            logger.error("register failed", e);
            return;
        }
    }

    public List<Provider> lookup(String name) {
        String nodePath = this.getNodesPath(name);
        List<KeyValue> keyValues = JetcdUtils.getNodesWithPrefix(nodePath);
        List<Provider> providers = keyValues.stream().map(t -> {
            Provider provider = JSON.parseObject(t.getValue().toStringUtf8(), Provider.class);
            return provider;
        }).collect(Collectors.toList());
        return providers;
    }

    private String getOfflineNodePath(String name, String address) {
        return String.format("/service/%s/offlines/%s", name, address);
    }

    /**
     * 服务节点路径
     *
     * @param name
     * @param address
     * @return
     */
    private String getNodePath(String name, String address) {
        return String.format("/service/%s/providers/%s", name, address);
    }

    /**
     * 服务节点目录
     *
     * @param name
     * @return
     */
    private String getNodesPath(String name) {
        return String.format("/service/%s/providers", name);
    }

    public void watchKey(String key, Boolean usePrefix, Consumer<List<Provider>> callback) {
        Executors.newSingleThreadExecutor(new NamedThreadFactory("WatchKey")).execute(() -> {
            String nodePath = this.getNodesPath(key);
            try {
                Watch.Watcher watcher;
                if (usePrefix) {
                    watcher = JetcdUtils.getWatcherWithPrefix(nodePath);
                } else {
                    watcher = JetcdUtils.getWatcher(nodePath);
                }
                List<WatchEvent> events = watcher.listen().getEvents();
                if (events == null || events.isEmpty()) {
                    logger.error("watch etcd key={} error", key);
                } else {
                    //TODO 校地址变更 ，其实服务端不用将变更数据更新到etcd。服务端变更数据可单独拉接口
                    List<Provider> providers = events.stream().map(t -> {
                        Provider provider = JSON.parseObject(t.getKeyValue().getValue().toStringUtf8(), Provider.class);
                        return provider;
                    }).collect(Collectors.toList());
                    callback.accept(providers);
                    logger.info("providers of [{}] refreshed: {}", key, JSON.toJSONString(providers));
                }
            } catch (Exception e) {
                logger.error(String.format("Watch etcd key:{}，error", key), e);
            }
        });
    }
}
