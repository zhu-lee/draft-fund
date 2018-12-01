package lee.fund.common.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelOption;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import lee.fund.common.Server;
import lee.fund.common.container.ServiceContainer;
import lee.fund.util.remote.RemotingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/23 16:27
 * Desc:
 */
public class NettyServer extends ServerBootstrap{
    private final Logger logger = LoggerFactory.getLogger(NettyServer.class);
    private NioEventLoopGroup serverBossGroup;
    private NioEventLoopGroup serverWorkerGroup;
    private ServerConfig serverConfig;
    private ServiceContainer serviceContainer;

    public NettyServer(ServerConfig serverConfig){
        this.serverConfig = serverConfig;
        serverBossGroup = new NioEventLoopGroup(1, new DefaultThreadFactory("serverBossGroup"));
        serverWorkerGroup = new NioEventLoopGroup(serverConfig.getWorkThreads(), new DefaultThreadFactory("serverWorkerGroup"));

        this.group(serverBossGroup, serverWorkerGroup);
        this.channel(enableEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class);
        this.localAddress(serverConfig.getBindAddress());
        this.childOption(ChannelOption.SO_KEEPALIVE, false);
        this.childOption(ChannelOption.SO_REUSEADDR, true);
        this.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
        this.childOption(ChannelOption.TCP_NODELAY, true);
        this.childOption(ChannelOption.SO_LINGER, serverConfig.getLinger());
        this.childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS, serverConfig.getConnectTimeout());
        this.childOption(ChannelOption.SO_RCVBUF, serverConfig.getReceiveBufSize());
        this.childOption(ChannelOption.SO_SNDBUF, serverConfig.getSendBufSize());
        this.childHandler(new ServerChannelInitializer());
    }

    private boolean enableEpoll(){
        return Epoll.isAvailable() && RemotingUtils.isLinuxOS();
    }

    public void start() {
        this.bind().addListener(f->{
            if (f.isSuccess()) {
                logger.info("server {} start success",serverConfig.getBindAddress());
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
            logger.error("server shutdown error，",e);
        }
    }

    public void setServiceContainer(ServiceContainer serviceContainer) {
        this.serviceContainer = serviceContainer;
    }
}
