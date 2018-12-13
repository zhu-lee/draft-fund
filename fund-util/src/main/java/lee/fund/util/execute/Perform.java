package lee.fund.util.execute;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/10 11:30
 * Desc:
 */
public class Perform {
    private static ExecutorService executor = Executors.newFixedThreadPool(5, new NamedThreadFactory("Perform"));

    private Perform() {
    }

    public static void execute(Runnable r) {
        executor.execute(r);
    }
}
