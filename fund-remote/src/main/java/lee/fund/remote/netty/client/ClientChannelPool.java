package lee.fund.remote.netty.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultMessageSizeEstimator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.ChannelHealthChecker;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import lee.fund.util.execute.NamedThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/12 17:08
 * Desc:
 */
public class ClientChannelPool implements ChannelPool {
    private static final ConcurrentMap<String, ClientChannelPool> clientChannelPoolMap = new ConcurrentHashMap<>();
    private static final ClientHandler clientHandler = new ClientHandler();
    private final FixedChannelPool pool;

    public ClientChannelPool(ClientConfig config) {
        Bootstrap bootstrap = this.createNettyClient(config);
        this.pool = new FixedChannelPool(bootstrap,
                new ClientChannelPoolHandler(config),
                ChannelHealthChecker.ACTIVE,
                FixedChannelPool.AcquireTimeoutAction.FAIL,
                config.getAcquireTimeout(),
                config.getMaxConnections(),
                config.getMaxPendingAcquires());
    }

    public static ClientChannelPool getClientChannelPool(ClientConfig clientConfig) {
        ObjectUtil.checkNotNull(clientConfig.getAddress(), "net address");
        return clientChannelPoolMap.computeIfAbsent(clientConfig.getAddress().toString(), k -> new ClientChannelPool(clientConfig));
    }

    @Override
    public Future<Channel> acquire() {
        return pool.acquire();
    }

    @Override
    public Future<Channel> acquire(Promise<Channel> promise) {
        return pool.acquire(promise);
    }

    @Override
    public Future<Void> release(Channel channel) {
        return pool.release(channel);
    }

    @Override
    public Future<Void> release(Channel channel, Promise<Void> promise) {
        return pool.release(channel, promise);
    }

    @Override
    public void close() {
        pool.close();
    }

    private Bootstrap createNettyClient(ClientConfig config) {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup(config.getWorkThreads(), new NamedThreadFactory("ClientWorkerGroup"));
        bootstrap.group(group);
        bootstrap.channel(SimpleClientChannel.class);
        bootstrap.remoteAddress(config.getAddress());

        // 设置传输设置
        bootstrap.option(ChannelOption.SO_REUSEADDR, true);
        bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, config.getConnectTimeout());
        bootstrap.option(ChannelOption.SO_SNDBUF, config.getSendBufferSize());
        bootstrap.option(ChannelOption.SO_RCVBUF, config.getReceiveBufferSize());
        bootstrap.option(ChannelOption.SO_KEEPALIVE, false);
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.MESSAGE_SIZE_ESTIMATOR, new DefaultMessageSizeEstimator(config.getReceiveBufferSize()));
        return bootstrap;
    }

    private class ClientChannelPoolHandler implements ChannelPoolHandler {
        private final Logger logger = LoggerFactory.getLogger(ClientChannelPoolHandler.class);
        private final ClientConfig config;

        public ClientChannelPoolHandler(ClientConfig config) {
            this.config = config;
        }

        @Override
        public void channelReleased(Channel ch) throws Exception {
            if (logger.isTraceEnabled()) {
                logger.trace("channelReleased");
            }
        }

        @Override
        public void channelAcquired(Channel ch) throws Exception {
            if (logger.isTraceEnabled()) {
                logger.trace("channelAcquired");
            }
        }

        @Override
        public void channelCreated(Channel channel) throws Exception {
            if (logger.isTraceEnabled()) {
                logger.trace("channelCreated");
            }
            ChannelPipeline channelPipe = channel.pipeline();

            // write
            channelPipe.addLast("encode", new ClientEncode());
            channelPipe.addLast("idle_state", new IdleStateHandler(0, this.config.getKeepAliveTime(), 0));
            // read
            channelPipe.addLast("decode", new ClientDecode());
            channelPipe.addLast("process", clientHandler);
        }
    }
}
