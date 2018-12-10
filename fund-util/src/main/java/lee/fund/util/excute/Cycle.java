package lee.fund.util.excute;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/10 9:26
 * Desc:
 */
public class Cycle {
    private static AtomicInteger threadIndex = new AtomicInteger(0);
    private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(5, r -> new Thread(r, String.format("Cycle_%s", threadIndex.incrementAndGet())));

    private Cycle() {
    }

    public static void set(Runnable r, long delay) {
        executor.schedule(r, delay, TimeUnit.MILLISECONDS);
    }

    public static void set(Runnable r,long initialDelay, long delay) {
        if (delay > 0) {
            executor.scheduleWithFixedDelay(r, initialDelay, delay, TimeUnit.MILLISECONDS);
        } else {
            executor.schedule(r, delay, TimeUnit.MILLISECONDS);
        }
    }
}
