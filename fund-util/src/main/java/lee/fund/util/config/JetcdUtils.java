package lee.fund.util.config;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.KV;
import com.coreos.jetcd.Lease;
import com.coreos.jetcd.Watch;
import com.coreos.jetcd.data.ByteSequence;
import com.coreos.jetcd.data.KeyValue;
import com.coreos.jetcd.options.DeleteOption;
import com.coreos.jetcd.options.GetOption;
import com.coreos.jetcd.options.PutOption;
import com.coreos.jetcd.options.WatchOption;
import com.google.common.base.Strings;
import lee.fund.util.lang.UncheckedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
public class JetcdUtils {
    private static final Logger logger = LoggerFactory.getLogger(JetcdUtils.class);
    private static Client client;
    private static KV kvClient;
    private static Lease leaseClient;
    private static long leaseId;
    private static final int TTL = 15;

    static {
        loadJetcd();
    }

    private JetcdUtils() {
    }

    private static void loadJetcd() {
        if (kvClient == null) {
            String etcdAdress = AppConf.instance().getGlabConf().getEtcdAdress();
            if (Strings.isNullOrEmpty(etcdAdress)) {
                throw new UncheckedException("etcd.address can't be null or empty");
            }
            try {
                client = Client.builder().endpoints(Arrays.asList(etcdAdress.split(","))).build();
                kvClient = client.getKVClient();
                leaseClient = client.getLeaseClient();
                leaseId = client.getLeaseClient().grant(TTL).get(TTL, TimeUnit.SECONDS).getID();
                keepAlive();
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
            logger.info("etcd client initialized successfully");
        }
    }

    public static Client getClient() {
        return client;
    }

    public static void setNode(String key, String value) {
        try {
            kvClient.put(ByteSequence.fromString(key), ByteSequence.fromString(value)).get();
        } catch (Exception e) {
            throw new UncheckedException(e);
        }
    }

    public static void setNodeWithLease(String key, String value) {
        try {
            ByteSequence bsKey = ByteSequence.fromString(key);
            ByteSequence bsVa = ByteSequence.fromString(value);
            kvClient.put(bsKey, bsVa, PutOption.newBuilder().withLeaseId(leaseId).withPrevKV().build()).get();
        } catch (Exception e) {
            throw new UncheckedException(e);
        }
    }

    public static List<KeyValue> getNodes(String key) {
        List<KeyValue> keyValues;
        try {
            keyValues = kvClient.get(ByteSequence.fromString(key)).get().getKvs();
        } catch (Exception e) {
            throw new UncheckedException(e);
        }
        return keyValues;
    }

    public static List<KeyValue> getNodesWithPrefix(String prefix) {
        List<KeyValue> keyValues = new ArrayList<>();
        try {
            GetOption getOption = GetOption.newBuilder().withPrefix(ByteSequence.fromString(prefix)).build();
            keyValues = kvClient.get(ByteSequence.fromString(prefix), getOption).get().getKvs();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return keyValues;
    }

    public static void delNode(String key) {
        try {
            kvClient.delete(ByteSequence.fromString(key)).get().getDeleted();
        } catch (Exception e) {
            throw new UncheckedException(e);
        }
    }

    public static void delNodesWithPrefix(String prefix) {
        try {
            DeleteOption deleteOption = DeleteOption.newBuilder().withPrefix(ByteSequence.fromString(prefix)).build();
            kvClient.delete(ByteSequence.fromString(prefix), deleteOption);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Watch.Watcher getWatcher(String key) {
        Watch.Watcher watcher;
        try {
            watcher = client.getWatchClient().watch(ByteSequence.fromString(key));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return watcher;
    }

    public static Watch.Watcher getWatcherWithPrefix(String prefix) {
        Watch.Watcher watcher;
        try {
            WatchOption watchOption = WatchOption.newBuilder().withPrefix(ByteSequence.fromString(prefix)).build();
            watcher = client.getWatchClient().watch(ByteSequence.fromString(prefix), watchOption);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return watcher;
    }

    private static void keepAlive() {
        Executors.newSingleThreadExecutor(r -> new Thread(r, "keep_alive_lease")).execute(
                () -> {
                    try {
                        Lease.KeepAliveListener listener = leaseClient.keepAlive(leaseId);
                        listener.listen();
                        logger.info("keepAlive lease, format leaseId{}", Long.toHexString(leaseId));
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
        );
    }
}
