package lee.fund.mq;

import com.github.brainlag.nsq.NSQMessage;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/6 21:51
 * Desc:
 */
public class MsgImpl implements Msg{
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
        return null;
    }

    @Override
    public Date getTime() {
        return null;
    }

    @Override
    public Date getReceiveTime() {
        return null;
    }

    @Override
    public int getAttempts() {
        return 0;
    }

    @Override
    public void finished() {

    }

    @Override
    public void requeue(int timeoutMillis) {

    }

    @Override
    public void requeue() {

    }
}
