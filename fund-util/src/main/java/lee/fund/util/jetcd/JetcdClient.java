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
    private static JetcdClient instance;
    private static Object obj = new Object();
    private Client client;
    private long leaseId;//租约id，注册服务到etcd上时，使用该租约
    private final int TTL = 15;

    private JetcdClient() {
        /**
         * TODO etcd挂了，判断什么异常表示挂了，etcd重启了如何再初始化设置
         * TODO 重启后，注册的服如何自动注册到etcd，最好不要手工启动服务来注册
         */
        init();
    }

    public static JetcdClient getInstance() {
        if (instance == null) {
            synchronized (obj) {
                if (instance == null) {
                    instance = new JetcdClient();
                }
            }
        }
        return instance;
    }

    private void init() {
        if (this.client == null) {
            String address = GlobalConf.instance().getEtcdAdress();
            if (Strings.isNullOrEmpty(address)) {
                throw new UncheckedException("etcd.address can't be null or empty");
            }
            try {
                this.client = Client.builder().endpoints(Arrays.asList(address.split(","))).build();
                this.getNodes("test");
                logger.info("etcd client endpoints={} setting success", address);
            } catch (Exception e) {
                logger.error(String.format("etcd client endpoints=%s setting error", address), e);
            }
        }
    }

    private Client getClient() {
        //TODO 挂了之后重启，要验证.这里如何判断呢
        if (this.client == null) {
            synchronized (this) {
                this.init();
            }
        }
        return this.client;
    }

    public void setNode(String key, String value) {
        try {
            this.getClient().getKVClient().put(ByteSequence.fromString(key), ByteSequence.fromString(value)).get();
        } catch (Exception e) {
            throw new RuntimeException(String.format("set node for key = [%s]，value=%s error", key, value), e);
        }
    }

    public void setNodeWithLease(String key, String value) {
        try {
            this.setLeaseId();//注册临时节点，设置租约
            ByteSequence bsKey = ByteSequence.fromString(key);
            ByteSequence bsVa = ByteSequence.fromString(value);
            this.getClient().getKVClient().put(bsKey, bsVa, PutOption.newBuilder().withLeaseId(leaseId).withPrevKV().build()).get();
        } catch (Exception e) {
            throw new RuntimeException(String.format("set nodes for key = [%s]，value=%s error", key, value), e);
        }
    }

    public List<KeyValue> getNodes(String key) {
        List<KeyValue> keyValues;
        try {
            keyValues = this.getClient().getKVClient().get(ByteSequence.fromString(key)).get().getKvs();
        } catch (Exception e) {
            throw new RuntimeException(String.format("get nodes by key = [%s] error", key), e);
        }
        return keyValues;
    }

    public List<KeyValue> getNodesWithPrefix(String prefix) {
        List<KeyValue> keyValues;
        try {
            GetOption getOption = GetOption.newBuilder().withPrefix(ByteSequence.fromString(prefix)).build();
            keyValues = this.getClient().getKVClient().get(ByteSequence.fromString(prefix), getOption).get().getKvs();
        } catch (Exception e) {
            throw new RuntimeException(String.format("get nodes with prefix = [%s] error", prefix), e);
        }
        return keyValues;
    }

    public void delNode(String key) {
        try {
            this.getClient().getKVClient().delete(ByteSequence.fromString(key)).get().getDeleted();
        } catch (Exception e) {
            throw new RuntimeException(String.format("del node by key = [%s] error", key), e);
        }
    }

    public void delNodesWithPrefix(String prefix) {
        try {
            DeleteOption deleteOption = DeleteOption.newBuilder().withPrefix(ByteSequence.fromString(prefix)).build();
            this.getClient().getKVClient().delete(ByteSequence.fromString(prefix), deleteOption);
        } catch (Exception e) {
            throw new RuntimeException(String.format("del nodes with prefix = [%s] error", prefix), e);
        }
    }

    public Watch.Watcher getWatcher(String key) {
        Watch.Watcher watcher;
        try {
            watcher = this.getClient().getWatchClient().watch(ByteSequence.fromString(key));
        } catch (Exception e) {
            throw new RuntimeException(String.format("get watcher for [%s] error", key), e);
        }
        return watcher;
    }

    public Watch.Watcher getWatcherWithPrefix(String prefix) {
        Watch.Watcher watcher;
        try {
            WatchOption watchOption = WatchOption.newBuilder().withPrefix(ByteSequence.fromString(prefix)).build();
            watcher = this.getClient().getWatchClient().watch(ByteSequence.fromString(prefix), watchOption);
        } catch (Exception e) {
            throw new RuntimeException(String.format("get watcher for [%s] error", prefix), e);
        }
        return watcher;
    }

    private void setLeaseId() {
        try {
            if (this.leaseId <= 0) {
                synchronized (this) {
                    if (this.leaseId <= 0) {
                        this.leaseId = this.getClient().getLeaseClient().grant(this.TTL).get(this.TTL, TimeUnit.SECONDS).getID();
                        logger.info("setting etcd client leaseId={} success", this.leaseId);
                        this.keepAlive();
                    }
                }
            }
        } catch (Exception e) {
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
                Lease.KeepAliveListener listener = this.getClient().getLeaseClient().keepAlive(this.leaseId);
                listener.listen();
                logger.info("keepAlive lease, leaseId={}", this.leaseId);
            } catch (Exception e) {
                logger.info("keepAlive lease, format leaseId={}，error", this.leaseId, e);
            }
        });
    }
}