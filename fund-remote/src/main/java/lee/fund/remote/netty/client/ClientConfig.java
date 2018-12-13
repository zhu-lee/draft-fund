package lee.fund.remote.netty.client;

import lee.fund.remote.app.client.ClientConfiguration;
import lombok.Getter;

import java.net.InetSocketAddress;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/23 17:22
 * Desc:
 */
@Getter
public class ClientConfig {
    private String name;
    private InetSocketAddress address;
    private int workThreads = Runtime.getRuntime().availableProcessors() * 2;
    private int connectTimeout = 10 * 1000;
    private int acquireTimeout = 10 * 1000;
    private int readTimeout = 30 * 1000;
    private int writeTimeout = 30 * 1000;
    private int maxConnections = 500;
    private int maxPendingAcquires = 100;
    private int receiveBufferSize = 1024 * 64;
    private int sendBufferSize = 1024 * 64;
    private int keepAliveTime = 30 * 60;  // 单位秒, 默认 30 分钟

    public ClientConfig(ClientConfiguration conf) {
        this.name = conf.getName();
        if (!conf.getAddress().contains(":")) {
            throw new IllegalArgumentException("invalid net address: " + conf.getAddress());
        }
        String[] ad = conf.getAddress().split(":");
        InetSocketAddress inetSocketAddress = new InetSocketAddress(ad[0], Integer.parseInt(ad[1]));
        this.address = inetSocketAddress;
        this.maxConnections = conf.getMaxConnections() > 0 ? conf.getMaxConnections() : this.maxConnections;
    }
}
