package lee.fund.common.netty.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/4 18:44
 * Desc:
 */
@Sharable
public class SessionHandler extends ChannelInboundHandlerAdapter {
    private DefaultChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public DefaultChannelGroup getChannelGroup() {
        return channelGroup;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        Channel channel = ctx.channel();
//        channel.attr(ChannelState.KEY).set(new ChannelState());
        channelGroup.add(channel);
    }
}
