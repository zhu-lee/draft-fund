package lee.fund.mq;

import lee.fund.mq.nsq.SubscriberInfo;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/7 16:59
 * Desc:
 */
public interface Subscriber {
    void subscribe(SubscriberInfo info);
}
