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

    public void set(ResponseMessage msg) {
        lock.lock();
        try {
            this.responseMessage = msg;
        } finally {
            lock.unlock();
        }
    }

    public ResponseMessage get(long timeout) throws InterruptedException {
        lock.lock();
        try {
            long now = System.currentTimeMillis();
            while (responseMessage == null) {
                boolean ok = hasMessage.await(timeout, TimeUnit.MILLISECONDS);
                if (ok || (System.currentTimeMillis() - now >= timeout)) {
                    break;
                }
            }
        } finally {
            lock.unlock();
        }
        return responseMessage;
    }
}