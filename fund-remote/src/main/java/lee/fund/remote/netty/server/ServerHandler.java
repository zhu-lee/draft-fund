package lee.fund.remote.netty.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.concurrent.GlobalEventExecutor;
import lee.fund.remote.container.MethodExecutor;
import lee.fund.remote.container.ServiceContainer;
import lee.fund.remote.exception.RpcError;
import lee.fund.remote.exception.RpcException;
import lee.fund.remote.protocol.RequestMessage;
import lee.fund.remote.protocol.ResponseMessage;
import lee.fund.remote.protocol.SimpleValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/4 18:47
 * Desc:
 */
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<RequestMessage> {
    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);
    @Getter
    private static final DefaultChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private final NettyServer server;

    public ServerHandler(NettyServer server) {
        this.server = server;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestMessage requestMessage) throws Exception {
        //TODO 添加诊断用的依赖关系

        ChannelState state = ctx.channel().attr(ChannelState.KEY).get();
        if (state != null) {
            state.setActiveTime(LocalDateTime.now());
            state.setId(requestMessage.getClientName());
            state.setService(requestMessage.getServiceName());
            state.setMethod(requestMessage.getMethodName());
        }

        try {
            this.server.getThreadPool().execute(()-> new InnerTask(requestMessage,ctx.channel(),this.server.getServiceContainer()));
        } catch (RejectedExecutionException e) {
            ResponseMessage responseMessage = ResponseMessage.failed(RpcError.SERVER_BUSY);
            ctx.writeAndFlush(responseMessage);
            logger.error("biz thread pools is full，MaxThreads:{}",this.server.getServerConfig().getMaxThreads());
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        channel.attr(ChannelState.KEY).set(new ChannelState());
        channelGroup.add(channel);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                logger.info("channel {} is closed because of idle timeout.", ctx.channel());
                ctx.close();
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("server error",cause);
        ctx.close();
    }

    @RequiredArgsConstructor
    private class InnerTask implements Runnable{
        private final RequestMessage requestMessage;
        private final Channel channel;
        private final ServiceContainer container;

        @Override
        public void run() {
            ResponseMessage responseMessage;
            try {
                MethodExecutor executor = container.getExecutor(requestMessage.getServiceName(), requestMessage.getMethodName());
                if (executor == null) {
                    throw new RpcException(RpcError.SERVER_SERVICE_NOT_FOUND, requestMessage.getServiceName(), requestMessage.getMethodName());
                }
                Object[] args = buildArgs(executor.getParameterTypes(), requestMessage.getParameters());
                Object result = executor.invoke(args);
                responseMessage = ResponseMessage.success(result);
                responseMessage.setMessageID(requestMessage.getMessageID());
            } catch (Exception e) {
                responseMessage = ResponseMessage.failed(e);
                logger.error("request processing failed", e);
            }
            channel.writeAndFlush(responseMessage);
        }

        private Object[] buildArgs(Class[] types, List<SimpleValue> values) {
            if (values == null) {
                return null;
            }
            Object[] args = new Object[values.size()];
            for (int i = 0; i < args.length; i++) {
                SimpleValue value = values.get(i);
                //TODO 实现decode
//                args[i] = SimpleEncoder.decode(value.getDataType(), value.getData(), types[i]);
            }
            return args;
        }
    }
}
