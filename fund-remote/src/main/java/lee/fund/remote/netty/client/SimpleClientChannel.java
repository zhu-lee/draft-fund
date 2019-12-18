package lee.fund.remote.netty.client;

import io.netty.channel.socket.nio.NioSocketChannel;
import lee.fund.remote.protocol.ResponseMessage;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/13 11:50
 * Desc:
 */
public class SimpleClientChannel extends NioSocketChannel {
    private ResponseMessage responseMessage;
    private ReentrantLock lock = new ReentrantLock();
    private Condition hasMessage = lock.newCondition();

    public void reset() {
        this.responseMessage = null;
    }

    public ResponseMessage get(long timeout) throws InterruptedException {
        lock.lock();
        try {
            long end = System.currentTimeMillis() + timeout;
            long time = timeout;
            while (responseMessage == null) {
                boolean ok = hasMessage.await(time, TimeUnit.MILLISECONDS);
                if (ok || (time = end - System.currentTimeMillis()) <= 0) {
                    break;
                }
            }
        } finally {
            lock.unlock();
        }
        return responseMessage;
    }

    public void set(ResponseMessage msg) {
        lock.lock();
        try {
            this.responseMessage = msg;
            hasMessage.signal();
        } finally {
            lock.unlock();
        }
    }
}