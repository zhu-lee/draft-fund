package lee.fund.remote.netty.server;

import lee.fund.remote.app.server.ServerConfiguration;
import lombok.Getter;

import java.net.InetSocketAddress;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/23 17:22
 * Desc:
 */
@Getter
public class ServerConfig {
    private int workThreads = Runtime.getRuntime().availableProcessors() * 2;
    private InetSocketAddress bindAddress;
    private int sendBufSize = 64 * 1024;
    private int receiveBufSize = 64 * 1024;
    private int linger = 5;
    private int maxConnections = 2000;
    private int maxThreads = 2000;
    private int connectTimeout = 10 * 1000;
    private int keepAliveTime = 30 * 60;//idle time

    public ServerConfig(ServerConfiguration cfg) {
        this.bindAddress = new InetSocketAddress(cfg.getPort());
        this.maxConnections = cfg.getConnections() > 0 ? cfg.getConnections() : this.maxConnections;
    }
}