package lee.fund.common.monitor;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/3 16:19
 * Desc:
 */
public class HttpMonitor {
    private final HttpServer httpServer;
    private final boolean daemon;
    private final ExecutorService executorService;
    private final LocalDateTime startTime = LocalDateTime.now();
    private final int nThread = 10;
    public HttpMonitor(InetSocketAddress address,boolean daemon) throws IOException {
        this.httpServer = HttpServer.create(address, nThread);
        this.daemon = daemon;
        this.executorService = Executors.newFixedThreadPool(nThread, new ThreadFactory() {
            private AtomicInteger threadIndex = new AtomicInteger(0);
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "HttpMonitor_" + threadIndex.incrementAndGet());
                t.setDaemon(daemon);
                return t;
            }
        });
        this.httpServer.setExecutor(executorService);
        initMonitor();
    }

    public void initMonitor() {
        // register default handler
//        register("/", new SimpleHandler(e -> String.format("start time: %s, process: %s",
//                DateConverter.toString(this.startTime),
//                MXUtil.getPID())));
//        // register dependency handler
//        this.register("/$dep", new SimpleHandler(e -> Dependency.print()));
//        // register Prometheus metrics handler
//        register("/metrics", new MetricHandler(CollectorRegistry.defaultRegistry));
    }
}
