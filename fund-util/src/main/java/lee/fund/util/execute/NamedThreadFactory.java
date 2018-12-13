package lee.fund.util.execute;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/13 14:19
 * Desc:
 */
public class NamedThreadFactory implements ThreadFactory{
    private static final AtomicInteger poolId = new AtomicInteger(0);
    private final AtomicInteger threadIndex = new AtomicInteger(0);
    private final String prefix;
    private boolean daemon;

    public NamedThreadFactory() {
        this("pool", false);
    }

    public NamedThreadFactory(String prefix) {
        this(String.format("%s-%s", prefix, poolId.incrementAndGet()), false);
    }

    public NamedThreadFactory(String prefix, boolean daemon) {
        this.prefix = String.format("%s-%s", prefix, poolId.incrementAndGet());
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r,String.format("%s-%s",this.prefix,threadIndex.incrementAndGet()));
        thread.setDaemon(daemon);
        return thread;
    }
}
