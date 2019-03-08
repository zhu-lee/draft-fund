package lee.fund.util.nsq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.github.brainlag.nsq.NSQConfig;
import com.github.brainlag.nsq.NSQProducer;
import com.github.brainlag.nsq.ServerAddress;
import com.google.common.base.Charsets;
import lee.fund.util.config.GlobalConf;
import lee.fund.util.config.ServerConf;
import lee.fund.util.execute.Schedule;
import lee.fund.util.lang.UncheckedException;
import lee.fund.util.remote.RemoteCall;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/7 18:58
 * Desc:
 */
public class NsqPublisher implements Publisher {
    private static Logger logger = LoggerFactory.getLogger(NsqPublisher.class);
    private static final NSQConfig NSQ_CONFIG = new NSQConfig(ServerConf.instance().getName(), GlobalConf.instance().getRpcRegisterIp());
    public static final NsqPublisher INSTANCE = new NsqPublisher();
    private final ConcurrentMap<String, ProducerHolder> PRODUCERS = new ConcurrentHashMap<>();
    private final Random RANDOM = new Random();

    private NsqPublisher() {
    }

    @Override
    public void publish(String topic, Object msg) {
        List<NSQProducer> list = this.getProducer(topic);
        String str = JSON.toJSONString(msg);
        byte[] bytes = str.getBytes(Charsets.UTF_8);
        NSQProducer nsq = list.get(this.RANDOM.nextInt(list.size()));
        if (this.publish(nsq, topic, bytes)) {
            return;
        }

        for (NSQProducer p : list) {
            if (p != nsq && this.publish(p, topic, bytes)) {
                return;
            }
        }

        throw new UncheckedException(String.format("publish failed，topic：[%s]，msg：[%s]", topic, str));
    }

    @Override
    public void publish(String topic, Object msg, int deferMillis) {
        List<NSQProducer> list = this.getProducer(topic);
        String str = JSON.toJSONString(msg);
        byte[] bytes = str.getBytes(Charsets.UTF_8);
        NSQProducer nsq = list.get(RANDOM.nextInt(list.size()));
        if (this.publish(nsq, topic, bytes, deferMillis)) {
            return;
        }

        for (NSQProducer p : list) {
            if (p != nsq && this.publish(p, topic, bytes, deferMillis)) {
                return;
            }
        }

        throw new UncheckedException(String.format("publish failed，topic：[%s]，msg：[%s]", topic, str));
    }

    @Override
    public <T> void publish(String topic, List<T> msgList) {
        List<NSQProducer> list = this.getProducer(topic);
        List<byte[]> bytesList = msgList.stream().map(msg -> JSON.toJSONString(msg).getBytes(Charsets.UTF_8)).collect(Collectors.toList());
        NSQProducer nsq = list.get(RANDOM.nextInt(list.size()));

        if (this.publish(nsq, topic, bytesList)) {
            return;
        }

        for (NSQProducer p : list) {
            if (p != nsq && this.publish(p, topic, bytesList)) {
                return;
            }
        }

        throw new UncheckedException(String.format("publish failed，topic：[%s]，msg：[%s]", topic, JSON.toJSONString(msgList)));
    }

    private List<NSQProducer> getProducer(String topic) {
        ProducerHolder holder = this.PRODUCERS.computeIfAbsent(topic, ProducerHolder::new);
        if (holder.producerList.isEmpty()) {
            throw new UncheckedException("no producer nodes found for topic：" + topic);
        }
        return holder.producerList;
    }

    private boolean publish(NSQProducer nsq, String topic, byte[] bytes) {
        try {
            nsq.produce(topic, bytes);
            return true;
        } catch (Exception e) {
            logger.error(String.format("failed to send message to topic：[%s]", topic), e);
            return false;
        }
    }

    private boolean publish(NSQProducer nsq, String topic, byte[] bytes, int deferMillis) {
        try {
            nsq.produceDeferred(topic, bytes, deferMillis);
            return true;
        } catch (Exception e) {
            logger.error(String.format("failed to send message to topic：[%s]", topic), e);
            return false;
        }
    }

    private boolean publish(NSQProducer nsq, String topic, List<byte[]> bytes) {
        try {
            nsq.produceMulti(topic, bytes);
            return true;
        } catch (Exception e) {
            logger.error(String.format("failed to send message to topic：[%s]，msg：[%s]", topic), e);
            return false;
        }
    }

    private static class ProducerHolder {
        private String topic;
        private Map<ServerAddress, NSQProducer> snmap = new HashMap<>();
        private List<NSQProducer> producerList = new ArrayList<>();

        public ProducerHolder(String topic) {
            this.topic = topic;
            Set<ServerAddress> addressSet = this.getAddressSetViaConfigCenter();
            addressSet.forEach(t -> {
                NSQProducer nsqProducer = new NSQProducer(NSQ_CONFIG).addAddress(t.getHost(), t.getPort()).start();
                this.producerList.add(nsqProducer);
                this.snmap.put(t, nsqProducer);
            });

            Schedule.set(this::updateProduces, 60 * 1000L, 60 * 1000L);//time to refresh
        }

        private void updateProduces() {
            Set<ServerAddress> newAddressSet = this.getAddressSetViaConfigCenter();
            if (newAddressSet.isEmpty()) {
                return;
            }

            if (newAddressSet.size() == this.snmap.keySet().size()
                    && newAddressSet.containsAll(this.snmap.keySet())) {
                return;
            }

            Map<ServerAddress, NSQProducer> newSnmap = new HashMap<>();
            List<NSQProducer> newProducerList = new ArrayList<>();

            //拷贝已有的节点
            this.snmap.forEach((s, p) -> {
                if (newAddressSet.contains(s)) {
                    newSnmap.put(s, p);
                    newProducerList.add(p);
                } else {
                    p.shutdown();
                }
            });

            //增加新节点
            newAddressSet.forEach(t -> {
                if (!this.snmap.containsKey(t)) {
                    NSQProducer nsqProducer = new NSQProducer(NSQ_CONFIG).addAddress(t.getHost(), t.getPort()).start();
                    newProducerList.add(nsqProducer);
                    newSnmap.put(t, nsqProducer);
                }
            });

            this.snmap = newSnmap;
            this.producerList = newProducerList;
        }

        private Set<ServerAddress> getAddressSetViaConfigCenter() {
            try {
                RemoteCall.Result result = RemoteCall.getInstance().getNsqNodes("/msg/pub", Collections.singletonMap("topic", topic));
                if (!result.isSuccess()) {
                    logger.error("config > get the publish nodes of the topic：[{}] from the config center failed：{}", this.topic, result.getInfo());
                } else {
                    List<PubNode> pubNodes = JSON.parseObject(result.getValue(), new TypeReference<List<PubNode>>() {
                    });

                    if (pubNodes == null || pubNodes.isEmpty()) {
                        logger.error("config > get the publish nodes of the topic[{}] from the config center failed", this.topic);
                        return new HashSet<>(1);
                    }

                    Set<ServerAddress> set = pubNodes.stream().map(t -> {
                        String[] s = t.getTcp().split(":");
                        ServerAddress adr = new ServerAddress(s[0], Integer.parseInt(s[1]));
                        return adr;
                    }).collect(Collectors.toSet());

                    return set;
                }
            } catch (Exception e) {
                logger.error("config > get the publish nodes of the {} from the config center failed：{}", this.topic, e);
            }
            return new HashSet<>(1);
        }

        @Getter
        @Setter
        private static class PubNode {
            private String tcp;
            private String http;
            private String note;
        }
    }
}
