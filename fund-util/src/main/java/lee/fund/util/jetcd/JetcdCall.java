package lee.fund.util.jetcd;

import com.alibaba.fastjson.JSON;
import com.coreos.jetcd.Watch;
import com.coreos.jetcd.data.KeyValue;
import com.coreos.jetcd.watch.WatchEvent;
import lee.fund.util.execute.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/9 11:50
 * Desc:
 */
public class JetcdCall {
    private final Logger logger = LoggerFactory.getLogger(JetcdCall.class);
    public static final JetcdCall JETCD_CALL = new JetcdCall();

    public void setNode(String key, String value) {
        JetcdClient.getInstance().setNode(key, value);
    }

    public void setNodeWithLease(String key, String value) {
        JetcdClient.getInstance().setNodeWithLease(key, value);
    }

    public List<KeyValue> getNodes(String key) {
        return JetcdClient.getInstance().getNodes(key);
    }

    public List<KeyValue> getNodesWithPrefix(String prefix) {
        return JetcdClient.getInstance().getNodesWithPrefix(prefix);
    }

    public void delNode(String key) {
        JetcdClient.getInstance().delNode(key);
    }

    public void delNodesWithPrefix(String prefix) {
        JetcdClient.getInstance().delNodesWithPrefix(prefix);
    }

    public Watch.Watcher getWatcher(String key) {
        return JetcdClient.getInstance().getWatcher(key);
    }

    public Watch.Watcher getWatcherWithPrefix(String prefix) {
        return JetcdClient.getInstance().getWatcherWithPrefix(prefix);
    }

    public List<Provider> lookup(String name) {
        String nodePath = this.getNodesPath(name);
        List<KeyValue> keyValues = JetcdClient.getInstance().getNodesWithPrefix(nodePath);
        List<Provider> providers = keyValues.stream().map(t -> JSON.parseObject(t.getValue().toStringUtf8(), Provider.class)).collect(Collectors.toList());
        return providers;
    }

    public void watchKey(String key, Boolean usePrefix, Consumer<List<Provider>> callback) {
        Executors.newSingleThreadExecutor(new NamedThreadFactory("WatchKey")).execute(() -> {
            String nodePath = this.getNodesPath(key);
            try {
                Watch.Watcher watcher;
                if (usePrefix) {
                    watcher = this.getWatcherWithPrefix(nodePath);
                } else {
                    watcher = this.getWatcher(nodePath);
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
                logger.error(String.format("watch etcd key:{}，error", key), e);
            }
        });
    }

    public String getOfflineNodePath(String name, String address) {
        return String.format("/service/%s/offlines/%s", name, address);
    }

    /**
     * 服务节点路径
     *
     * @param name
     * @param address
     * @return
     */
    public String getNodePath(String name, String address) {
        return String.format("/service/%s/providers/%s", name, address);
    }

    /**
     * 服务节点目录
     *
     * @param name
     * @return
     */
    public String getNodesPath(String name) {
        return String.format("/service/%s/providers", name);
    }
}
