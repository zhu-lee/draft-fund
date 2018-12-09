package lee.fund.util.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 轻量级多任务调度
 *
 * @author noname
 */
public class Clock {
    private static final Logger logger = LoggerFactory.getLogger(Clock.class);
    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    private Clock() {
        // 防止实例化
    }

    /**
     * 添加一个延迟执行的任务
     *
     * @param task  待执行任务
     * @param delay 延迟时间（毫秒）
     */
    public static void set(Runnable task, long delay) {
        executor.schedule(new RunnableWrapper(task), delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 添加一个周期性任务
     *
     * @param task     待执行任务
     * @param delay    第一次执行的延迟时间（毫秒）
     * @param interval 执行间隔（毫秒）
     */
    public static void set(Runnable task, long delay, long interval) {
        RunnableWrapper wrapper = new RunnableWrapper(task);
        if (interval > 0) {
            executor.scheduleWithFixedDelay(wrapper, delay, interval, TimeUnit.MILLISECONDS);
        } else {
            executor.schedule(wrapper, delay, TimeUnit.MILLISECONDS);
        }
    }

    private static class RunnableWrapper implements Runnable {
        private final Runnable inner;

        private RunnableWrapper(Runnable inner) {
            this.inner = inner;
        }

        @Override
        public void run() {
            try {
                this.inner.run();
            } catch (Exception e) {
                logger.error("failed to execute action", e);
            }
        }
    }
}