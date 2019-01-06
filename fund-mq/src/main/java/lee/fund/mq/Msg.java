package lee.fund.mq;

import java.util.Date;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/6 11:58
 * Desc:   get message data
 */
public interface Msg {
    String getId();//获取消息ID

    String getBody();//获取消息内容

    Date getTime();//获取消息原始创建时间

    Date getReceiveTime();//获取浙消息接收时间

    int getAttempts();//获取消息发送次数

    void finished();//设置消息处理完成

    void requeue(int timeoutMillis);//消息重入队列

    void requeue();//消息重入队列
}
