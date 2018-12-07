package lee.fund.util.config;

import com.coreos.jetcd.Client;
import com.coreos.jetcd.KV;
import com.coreos.jetcd.Lease;
import com.coreos.jetcd.data.ByteSequence;
import com.coreos.jetcd.options.PutOption;
import com.google.common.base.Strings;
import lee.fund.util.lang.UncheckedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

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
                leaseId = client.getLeaseClient().grant(5).get().getID();
                keepAlive();
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
            logger.info("etcd client initialized successfully");
        }
    }

    public static Client getClient() {
//        loadJetcd(); TODO 需不需要，测试下etcd挂了情况
        return client;
    }

    private static KV getKvClient() {
        return kvClient;
    }

    private static Lease getLeaseClient() {
        return leaseClient;
    }

    public static void setNode(String key, String value) {
        try {
            ByteSequence bsKey = ByteSequence.fromString(key);
            ByteSequence bsVa = ByteSequence.fromString(value);
            getKvClient().put(bsKey, bsVa, PutOption.newBuilder().withLeaseId(leaseId).withPrevKV().build()).get();
            getKvClient().txn();
        } catch (Exception e) {
            throw new UncheckedException(e);
        }
    }

    public static String getNode(String key){
        try
        getKvClient().get(ByteSequence.fromString(key)).get().getKvs();
//        List<KeyValue> kvs = EtcdUtil.getEtclClient().getKVClient().get(ByteSequence.fromString(key)).get().getKvs();
        if(kvs.size()>0){
            String value = kvs.get(0).getValue().toStringUtf8();
            return value;
        }
        else {
            return null;
        }
    }

    private static void keepAlive() {
        Executors.newSingleThreadExecutor().execute(
                () -> {
                    try {
                        Lease.KeepAliveListener listener = getLeaseClient().keepAlive(leaseId);
                        listener.listen();
                        logger.info("keepAlive leaseClient:{}, format:" + Long.toHexString(leaseId));
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
        );
    }
}
