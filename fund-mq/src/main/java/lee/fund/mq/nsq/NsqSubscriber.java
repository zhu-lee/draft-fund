package lee.fund.mq.nsq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.github.brainlag.nsq.NSQConfig;
import com.github.brainlag.nsq.NSQConsumer;
import com.github.brainlag.nsq.NSQMessage;
import com.github.brainlag.nsq.lookup.NSQLookup;
import lee.fund.mq.Msg;
import lee.fund.mq.Subscriber;
import lee.fund.util.config.AppConf;
import lee.fund.util.execute.Schedule;
import lee.fund.util.remote.RemoteCall;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/7 18:57
 * Desc:
 */
public class NsqSubscriber implements Subscriber {
    private Logger logger = LoggerFactory.getLogger(NsqSubscriber.class);
    private static final NSQConfig NSQ_CONFIG = new NSQConfig(AppConf.INSTANCE.getServerConf().getName(), AppConf.INSTANCE.getGlobalConf().getRpcRegisterIp());
    public static final NsqSubscriber INSTANCE = new NsqSubscriber();
    private final int INTERVAL = 60 * 1000;
    private NSQLookup lookup;

    private NsqSubscriber() {
    }

    @Override
    public void subscribe(SubscriberInfo info) {
        NSQConsumer consumer = new NSQConsumer(lookup(info.getTopic()),
                info.getTopic(),
                info.getChannel(),
                m -> info.getMqHandler().handler(new MsgImpl(m)),
                NSQ_CONFIG,
                e -> logger.error("{}", e.getMessage()))
                .setThreads(info.getThreads());
        consumer.setLookupPeriod(INTERVAL);
        consumer.start();

        Schedule.set(() -> {
            int queueSize = consumer.getQueueSize();
            if (queueSize > 0) {
                logger.warn("订阅 {} - {} 的缓存队列中还有 {} 个消息待处理", info.getTopic(), info.getChannel(), queueSize);
            }
        }, 60 * 1000L, 60 * 1000L);
    }

    private NSQLookup lookup(String topic) {
        if (this.lookup == null) {
            synchronized (this) {
                if (this.lookup == null) {
                    this.lookup = this.getLookupViaConfigCenter(topic);
                }
            }
        }
        return this.lookup;
    }

    private NSQLookup getLookupViaConfigCenter(String topic) {
        try {
            RemoteCall.Result result = RemoteCall.getInstance().getNsqNodes("/msg/sub", null);
            if (!result.isSuccess()) {
                logger.error("config > get the subscribe nodes for topic=[{}] from the config center failed：{}", topic, result.getInfo());
                return null;
            } else {
                List<SubNode> subNodes = JSON.parseObject(result.getValue(), new TypeReference<List<SubNode>>() {
                });

                if (subNodes == null || subNodes.isEmpty()) {
                    logger.error("config > get the subscribe nodes for topic=[{}] from the config center failed：not any subscribe node", topic, result.getInfo());
                    return null;
                }

                NSQLookup nsqLookup = new FundNsqLookup();
                subNodes.forEach(t -> {
                    String[] s = t.getAddress().split(":");
                    nsqLookup.addLookupAddress(s[0], Integer.parseInt(s[1]));
                });

                return nsqLookup;
            }
        } catch (Exception e) {
            logger.error("config > get the subscribe nodes for topic=[{}] from the config center failed：not any subscribe node", topic, e);
        }
        return null;
    }

    private static class MsgImpl implements Msg {
        private NSQMessage nsqMessage;

        public MsgImpl(NSQMessage nsqMessage) {
            this.nsqMessage = nsqMessage;
        }

        @Override
        public String getId() {
            return new String(this.nsqMessage.getId(), StandardCharsets.US_ASCII);
        }

        @Override
        public String getBody() {
            return new String(this.nsqMessage.getMessage(), StandardCharsets.UTF_8);
        }

        @Override
        public Date getTime() {
            return nsqMessage.getTimestamp();
        }

        @Override
        public Date getReceiveTime() {
            return nsqMessage.getReceiveTime();
        }

        @Override
        public int getAttempts() {
            return nsqMessage.getAttempts();
        }

        @Override
        public void finished() {
            nsqMessage.finished();
        }

        @Override
        public void requeue(int timeoutMillis) {
            nsqMessage.requeue(timeoutMillis);
        }

        @Override
        public void requeue() {
            nsqMessage.requeue();
        }
    }

    @Setter
    @Getter
    private static class SubNode {
        private String address;
        private String note;
    }
}