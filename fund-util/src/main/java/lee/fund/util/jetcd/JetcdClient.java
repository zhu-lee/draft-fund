package lee.fund.util.jetcd;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.Lease;
import com.coreos.jetcd.Watch;
import com.coreos.jetcd.data.ByteSequence;
import com.coreos.jetcd.data.KeyValue;
import com.coreos.jetcd.options.DeleteOption;
import com.coreos.jetcd.options.GetOption;
import com.coreos.jetcd.options.PutOption;
import com.coreos.jetcd.options.WatchOption;
import com.google.common.base.Strings;
import lee.fund.util.config.GlobalConf;
import lee.fund.util.execute.NamedThreadFactory;
import lee.fund.util.execute.Schedule;
import lee.fund.util.lang.UncheckedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/7 15:32
 * Desc:
 */
public class JetcdClient {
    private static final Logger logger = LoggerFactory.getLogger(JetcdClient.class);
    private static volatile JetcdClient instance;
    private static volatile boolean isInitSuccess;
    private Client client;
    private long leaseId;//租约id，注册服务到etcd上时，使用该租约
    private final int TTL = 15;

    private JetcdClient() {
        this.connectEtcd();
        Schedule.set(this::connectEtcd, 2000, 2000);
    }

    public static JetcdClient getInstance() {
        if (instance == null) {
            synchronized (JetcdClient.class) {
                if (instance == null) {
                    instance = new JetcdClient();
                }
            }
        }
        return instance;
    }

    private void connectEtcd() {
        if (!isInitSuccess) {
            String address = GlobalConf.instance().getEtcdAdress();
            if (Strings.isNullOrEmpty(address)) {
                throw new UncheckedException("etcd.address can't be null or empty");
            }
            try {
                this.client = Client.builder().endpoints(Arrays.asList(address.split(","))).build();
                this.client.getKVClient().get(ByteSequence.fromString("test")).get().getKvs();
                logger.info("etcd client endpoints={} setting success", address);
                isInitSuccess = true;
            } catch (Exception e) {
                this.checkException(e);
                logger.error("etcd client connection endpoints[{}] error，will try again...", address);
            }
        }
    }

    private void checkException(Exception e) {
        if (e.getMessage().contains("io.grpc.StatusRuntimeException: UNAVAILABLE")) {
            isInitSuccess = false;
        }
    }

    public void setNode(String key, String value) {
        try {
            this.client.getKVClient().put(ByteSequence.fromString(key), ByteSequence.fromString(value)).get();
        } catch (Exception e) {
            this.checkException(e);
            throw new RuntimeException(String.format("set node for key = [%s]，value=%s error", key, value), e);
        }
    }

    public void setNodeWithLease(String key, String value) {
        try {
            this.setLeaseId();//注册临时节点，设置租约
            ByteSequence bsKey = ByteSequence.fromString(key);
            ByteSequence bsVa = ByteSequence.fromString(value);
            this.client.getKVClient().put(bsKey, bsVa, PutOption.newBuilder().withLeaseId(leaseId).withPrevKV().build()).get();
        } catch (Exception e) {
            this.checkException(e);
            throw new RuntimeException(String.format("set nodes for key = [%s]，value=%s error", key, value), e);
        }
    }

    public List<KeyValue> getNodes(String key) {
        List<KeyValue> keyValues;
        try {
            keyValues = this.client.getKVClient().get(ByteSequence.fromString(key)).get().getKvs();
        } catch (Exception e) {
            this.checkException(e);
            throw new RuntimeException(String.format("get nodes by key = [%s] error", key), e);
        }
        return keyValues;
    }

    public List<KeyValue> getNodesWithPrefix(String prefix) {
        List<KeyValue> keyValues;
        try {
            GetOption getOption = GetOption.newBuilder().withPrefix(ByteSequence.fromString(prefix)).build();
            keyValues = this.client.getKVClient().get(ByteSequence.fromString(prefix), getOption).get().getKvs();
        } catch (Exception e) {
            this.checkException(e);
            throw new RuntimeException(String.format("get nodes with prefix = [%s] error", prefix), e);
        }
        return keyValues;
    }

    public void delNode(String key) {
        try {
            this.client.getKVClient().delete(ByteSequence.fromString(key)).get().getDeleted();
        } catch (Exception e) {
            this.checkException(e);
            throw new RuntimeException(String.format("del node by key = [%s] error", key), e);
        }
    }

    public void delNodesWithPrefix(String prefix) {
        try {
            DeleteOption deleteOption = DeleteOption.newBuilder().withPrefix(ByteSequence.fromString(prefix)).build();
            this.client.getKVClient().delete(ByteSequence.fromString(prefix), deleteOption);
        } catch (Exception e) {
            this.checkException(e);
            throw new RuntimeException(String.format("del nodes with prefix = [%s] error", prefix), e);
        }
    }

    public Watch.Watcher getWatcher(String key) {
        Watch.Watcher watcher;
        try {
            watcher = this.client.getWatchClient().watch(ByteSequence.fromString(key));
        } catch (Exception e) {
            this.checkException(e);
            throw new RuntimeException(String.format("get watcher for [%s] error", key), e);
        }
        return watcher;
    }

    public Watch.Watcher getWatcherWithPrefix(String prefix) {
        Watch.Watcher watcher;
        try {
            WatchOption watchOption = WatchOption.newBuilder().withPrefix(ByteSequence.fromString(prefix)).build();
            watcher = this.client.getWatchClient().watch(ByteSequence.fromString(prefix), watchOption);
        } catch (Exception e) {
            this.checkException(e);
            throw new RuntimeException(String.format("get watcher for [%s] error", prefix), e);
        }
        return watcher;
    }

    private void setLeaseId() {
        try {
            if (this.leaseId <= 0) {
                synchronized (this) {
                    if (this.leaseId <= 0) {
                        this.leaseId = this.client.getLeaseClient().grant(this.TTL).get(this.TTL, TimeUnit.SECONDS).getID();
                        logger.info("setting etcd client leaseId={} success", this.leaseId);
                        this.keepAlive();
                    }
                }
            }
        } catch (Exception e) {
            this.checkException(e);
            throw new UncheckedException("setting etcd client leaseId error", e);
        }
    }

    /**
     * TODO 租约设置长点，减少频繁续约
     * 发送心跳到ETCD,表明该host是活着的
     * 当户端掉线或者关闭后，keepAlive将不会继续续租，TTL后，租约到期，节点就会被删除
     */
    private void keepAlive() {
        Executors.newSingleThreadExecutor(new NamedThreadFactory("KeepAlive")).execute(() -> {
            try {
                Lease.KeepAliveListener listener = this.client.getLeaseClient().keepAlive(this.leaseId);
                listener.listen();
                logger.info("keepAlive lease, leaseId={}", this.leaseId);
            } catch (Exception e) {
                this.checkException(e);
                logger.info("keepAlive lease, format leaseId={}，error", this.leaseId, e);
            }
        });
    }
}