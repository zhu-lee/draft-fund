package lee.fund.common.monitor;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lee.fund.util.convert.DateConverter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

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
        this.executorService = Executors.newFixedThreadPool(5, new ThreadFactory() {
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
        this.register("/", new SimpleHandler(e -> String.format("start time: %s, process: %s",
                DateConverter.toString(this.startTime),
                MXUtil.getPID())));
        this.register("/$dep", new SimpleHandler(e -> Dependency.print()));
        register("/metrics", new MetricHandler(CollectorRegistry.defaultRegistry));
    }

    public void register(String path, HttpHandler handler) {
        this.httpServer.createContext(path, handler);
    }

    public void start() {

    }

    static class SimpleHandler implements HttpHandler{
        Function<HttpExchange, String> render;
        public SimpleHandler(Function<HttpExchange, String> render) {
            this.render = render;
        }

        @Override
        public void handle(HttpExchange httpExchange) throws IOException {
            String content = this.render.apply(httpExchange);
            byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
            httpExchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, bytes.length);
            OutputStream out = httpExchange.getResponseBody();
            out.write(bytes);
            out.flush();
            httpExchange.close();
        }
    }
}
