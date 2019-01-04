package lee.fund.remote.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lee.fund.remote.container.ServiceContainer;
import lee.fund.util.execute.NamedThreadFactory;
import lee.fund.util.sys.SysUtils;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/23 16:27
 * Desc:
 */
public class NettyServer extends ServerBootstrap {
    private final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private NioEventLoopGroup serverBossGroup;
    private NioEventLoopGroup serverWorkerGroup;
    @Getter
    private ServerConfig serverConfig;
    @Getter
    private LocalDateTime startTime;

    @Setter
    @Getter
    private ServiceContainer serviceContainer;
    @Getter
    private ThreadPoolExecutor threadPool;
    @Getter
    private ServerHandler serverHandler;

    public NettyServer(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
        this.serverHandler = new ServerHandler(this);
        this.serverBossGroup = new NioEventLoopGroup(1, new NamedThreadFactory("ServerBossGroup"));
        this.serverWorkerGroup = new NioEventLoopGroup(serverConfig.getWorkThreads(), new NamedThreadFactory("ServerWorkerGroup"));

        this.group(serverBossGroup, serverWorkerGroup);
        this.channel(enableEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class);
        this.localAddress(serverConfig.getBindAddress());
        //child
        this.childOption(ChannelOption.SO_KEEPALIVE, false);
        this.childOption(ChannelOption.SO_REUSEADDR, true);
        this.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        this.childOption(ChannelOption.TCP_NODELAY, true);
        this.childOption(ChannelOption.SO_LINGER, serverConfig.getLinger());
        this.childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, serverConfig.getConnectTimeout());
        this.childOption(ChannelOption.SO_RCVBUF, serverConfig.getReceiveBufSize());
        this.childOption(ChannelOption.SO_SNDBUF, serverConfig.getSendBufSize());
        this.childHandler(new ServerChannelInitializer(this.serverHandler, serverConfig));

        this.initPoolExecutor();
    }

    private void initPoolExecutor() {
        threadPool = new ThreadPoolExecutor(this.serverConfig.getWorkThreads(), this.serverConfig.getMaxThreads(), 60, TimeUnit.SECONDS, new SynchronousQueue<>(), new NamedThreadFactory("Biz"));
        threadPool.allowCoreThreadTimeOut(true);
    }

    private boolean enableEpoll() {
        return Epoll.isAvailable() && SysUtils.isLinuxOS();
    }

    public void start() {
        this.startTime = LocalDateTime.now();
        this.bind().addListener(f -> {
            if (f.isSuccess()) {
                logger.info("server {} start success", serverConfig.getBindAddress());
            } else {
                logger.info("server {} start failed：", serverConfig.getBindAddress(), f.cause());
            }
        });
    }

    public void shutdown() {
        try {
            serverBossGroup.shutdownGracefully();
            serverWorkerGroup.shutdownGracefully();
        } catch (Exception e) {
            logger.error("server shutdown error，", e);
        }
    }
}
