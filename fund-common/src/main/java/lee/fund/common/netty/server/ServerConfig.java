package lee.fund.common.netty.server;

import lee.fund.common.config.Configuration;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/23 17:22
 * Desc:
 */
@Setter
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

    public ServerConfig(Configuration cfg) {
        this.bindAddress = new InetSocketAddress(cfg.getPort());
        //TODO empty check
        this.maxConnections = cfg.getConnections() > 0 ? cfg.getConnections() : this.maxConnections;
    }
}