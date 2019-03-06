package lee.fund.util.nsq;

import java.util.List;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/7 17:00
 * Desc:
 */
public interface Publisher {
    /**
     * 发送单条消息
     *
     * @param topic
     * @param msg
     */
    void publish(String topic, Object msg);

    /**
     * 发送延迟消息，延迟 deferMillis 后发送消息
     *
     * @param topic
     * @param msg
     * @param deferMillis 延迟毫秒数, 注意延迟最大值为服务器端设置的 max-req-timeout(默认 60 分钟)
     */
    void publish(String topic, Object msg, int deferMillis);

    /**
     * 批量发送消息
     *
     * @param topic
     * @param msgList
     * @param <T>
     */
    <T> void publish(String topic, List<T> msgList);

    /**
     * 获取实例
     *
     * @return
     */
    static Publisher get() {
        return NsqPublisher.INSTANCE;
    }
}
