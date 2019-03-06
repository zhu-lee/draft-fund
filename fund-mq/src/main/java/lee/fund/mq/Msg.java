package lee.fund.mq;

import java.util.Date;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/6 11:58
 * Desc:   get message data
 */
public interface Msg {
    /**
     * 获取消息ID
     *
     * @return
     */
    String getId();

    /**
     * 获取消息内容
     *
     * @return
     */
    String getBody();

    /**
     * 获取消息原始创建时间
     *
     * @return
     */
    Date getTime();

    /**
     * 获取浙消息接收时间
     *
     * @return
     */
    Date getReceiveTime();

    /**
     * 获取消息发送次数
     *
     * @return
     */
    int getAttempts();

    /**
     * 设置消息处理完成
     *
     * @return
     */
    void finished();

    /**
     * 消息重入队列
     *
     * @return
     */
    void requeue(int timeoutMillis);

    /**
     * 消息重入队列
     *
     * @return
     */
    void requeue();
}
