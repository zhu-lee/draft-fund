package lee.fund.util.excute;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/10 11:30
 * Desc:
 */
public class Perform {
    private static AtomicInteger threadIndex = new AtomicInteger(0);
    private static ExecutorService executor = Executors.newFixedThreadPool(5,r -> new Thread(r, String.format("Perform_%s", threadIndex.incrementAndGet())));

    private Perform() {
    }

    public static void execute(Runnable r) {
        executor.execute(r);
    }
}
