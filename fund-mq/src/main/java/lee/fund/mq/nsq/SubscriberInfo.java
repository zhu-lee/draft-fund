package lee.fund.mq.nsq;

import lee.fund.mq.MqHandler;
import lee.fund.mq.annotation.Mq;
import lee.fund.util.config.SettingMap;
import lombok.Getter;

import java.util.Arrays;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/7 19:13
 * Desc:
 */
@Getter
public class SubscriberInfo {
    private String topic;
    private String channel;
    private int threads;
    private MqHandler mqHandler;
    private SettingMap options;

    public SubscriberInfo(Mq mq, MqHandler mqHandler) {
        this.topic = mq.topic();
        this.channel = mq.channel();
        this.threads = mq.threads();
        this.options = new SettingMap();
        Arrays.stream(mq.options().split("&")).map(t -> t.split("="))
                .forEach(t -> this.options.put(t[0], t[1]));
        this.mqHandler = mqHandler;
    }
}
