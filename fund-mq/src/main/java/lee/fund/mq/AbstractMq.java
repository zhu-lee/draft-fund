package lee.fund.mq;

import com.alibaba.fastjson.JSON;
import lee.fund.util.lang.UncheckedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ResolvableType;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/6 11:57
 * Desc:
 */
public abstract class AbstractMq<T> implements MqHandler {
    private static final Logger logger = LoggerFactory.getLogger(AbstractMq.class);
    private static final int MAX_DELAY = 10 * 60 * 1000;
    protected Class<T> msgClass;
    protected int maxAttempts = 3;

    public AbstractMq() {
        ResolvableType rlvType = ResolvableType.forClass(this.getClass());
        this.msgClass = (Class<T>) rlvType.getSuperType().getGenerics()[0].resolve();
    }

    @Override
    public void handler(Msg msg) {
        try {
            T bean = this.decodeMsg(msg.getBody());
            this.process(bean, msg);
            logger.info("消息处理成功，id：{}，内容：{}，接收时间：{}", msg.getId(), msg.getBody(), msg.getReceiveTime());
        } catch (Exception e) {
            if (msg.getAttempts() < this.maxAttempts) {
                int delay = this.getDelay(msg.getAttempts());
                logger.error("第{}次处理失败，延迟{}毫秒重发消息，id：{}，内容：{}，错误：", msg.getAttempts(), delay, msg.getId(), msg.getBody(), e);
                msg.requeue(delay);
            }else{
                logger.error("第{}次处理失败，最大重发次数{}，丢弃消息，id：{}，内容：{}，错误：", msg.getAttempts(), this.maxAttempts, msg.getId(), msg.getBody(), e);
                msg.finished();
            }
        }finally {
            //TODO 1、监控消息信息，包括id、内容、时间，host  2、监控remoteClient状态信息
        }
    }

    private T decodeMsg(String msgBody) {
        T bean;
        try {
            if (this.msgClass == String.class) {
                bean = (T) msgBody;
            } else {
                bean = JSON.parseObject(msgBody, this.msgClass);
            }
        } catch (Exception e) {
            throw new UncheckedException(e);
        }
        return bean;
    }

    private int getDelay(int attempts) {
        return Math.min(attempts * 60 * 100, MAX_DELAY);
    }

    protected abstract void process(T bean, Msg rawMsg);
}
