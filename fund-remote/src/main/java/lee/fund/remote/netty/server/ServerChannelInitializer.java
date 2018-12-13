package lee.fund.remote.netty.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.IdleStateHandler;
import lee.fund.remote.protocol.CodecAdapter;
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
    private final ServerConfig config;
    private final ServerHandler serverHandler;
    private final CodecAdapter codecAdapter;

    public ServerChannelInitializer(ServerHandler serverHandler,ServerConfig config) {
        this.config = config;
        this.serverHandler = serverHandler;
        this.codecAdapter = new CodecAdapter();
    }

    @Override
    protected void initChannel(Channel ch) throws Exception {
        if (serverHandler.getChannelGroup().size() >= this.config.getMaxConnections()) {
            logger.info("reach the max connections{}, close the connection", this.config.getMaxConnections());
            ch.close();
            return;
        }

        ChannelPipeline pipeline = ch.pipeline();
        //write
        pipeline.addLast("encode", codecAdapter.getCodecEncoder());
        //read
        pipeline.addLast("idle_state", new IdleStateHandler(this.config.getKeepAliveTime(), 0, 0, TimeUnit.SECONDS));
        pipeline.addLast("decode", codecAdapter.getCodecDecoder());
        pipeline.addLast("process", serverHandler);
    }
}
