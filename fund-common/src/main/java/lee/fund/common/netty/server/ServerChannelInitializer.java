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
    private static SessionHandler sessionHandler = new SessionHandler();
    private static ServerIdleHandler serverIdleHandler = new ServerIdleHandler();
    private NettyServer server;
    private ServerConfig config;

    public ServerChannelInitializer(NettyServer server, ServerConfig config) {
        this.server = server;
        this.config = config;
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        // read
        IdleStateHandler idleStateHandler = new IdleStateHandler(this.config.getKeepAliveTime(), 0, 0, TimeUnit.SECONDS);
//        pipeline.addLast("encode")
//        pipeline.addLast("decode")
        pipeline.addLast("idle_state", idleStateHandler);
        pipeline.addLast("idle_process", serverIdleHandler);
        pipeline.addFirst("session", sessionHandler);
        pipeline.addLast("process",new ServerHandler());
        System.out.println("1---" + Thread.currentThread().getName());


    }
}
