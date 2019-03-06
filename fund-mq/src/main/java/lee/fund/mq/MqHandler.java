package lee.fund.mq;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/6 11:56
 * Desc:
 */
public interface MqHandler {
    void handler(Msg msg);
}
