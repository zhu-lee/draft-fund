package lee.fund.common.netty.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/23 19:58
 * Desc:
 */
public class ServerChannelInitializer extends ChannelInitializer<Channel> {
    private final Logger logger = LoggerFactory.getLogger(ServerChannelInitializer.class);
    private static ServerIdleHandler serverIdleHandler = new ServerIdleHandler();
    private final NettyServer server;
    private final ServerConfig config;

    public ServerChannelInitializer(NettyServer server, ServerConfig config) {
        this.server = server;
        this.config = config;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        SessionHandler siHandler = this.server.getSessionHandler();
        if (siHandler.getChannelGroup().size() >= this.config.getMaxConnections()) {
            logger.info("reach the max connections{}, close the connection", this.config.getMaxConnections());
            ch.close();
            return;
        }

        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast("encode", new ServerEncoder());//write
        pipeline.addLast("idle_state", new IdleStateHandler(this.config.getKeepAliveTime(), 0, 0, TimeUnit.SECONDS));
        pipeline.addLast("idle_process", serverIdleHandler);
        pipeline.addLast("session", server.getSessionHandler());
        pipeline.addLast("decode", new ServerDecoder());
        pipeline.addLast("process", new ServerHandler(this.server));
    }
}
