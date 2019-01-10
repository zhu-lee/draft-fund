package lee.fund.util.execute;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/10 9:26
 * Desc:
 */
public class Schedule {
    private static ScheduledExecutorService executor = Executors.newScheduledThreadPool(5, new NamedThreadFactory("Schedule"));

    private Schedule() {
    }

    public static void set(Runnable r, long delay) {
        executor.schedule(r, delay, TimeUnit.MILLISECONDS);
    }

    public static void set(Runnable r, long initialDelay, long delay) {
        if (delay > 0) {
            executor.scheduleWithFixedDelay(r, initialDelay, delay, TimeUnit.MILLISECONDS);
        } else {
            executor.schedule(r, delay, TimeUnit.MILLISECONDS);
        }
    }
}
