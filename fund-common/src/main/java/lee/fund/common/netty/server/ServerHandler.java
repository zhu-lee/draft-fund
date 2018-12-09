package lee.fund.common.netty.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lee.fund.common.container.MethodExecutor;
import lee.fund.common.container.ServiceContainer;
import lee.fund.common.exception.RpcError;
import lee.fund.common.exception.RpcException;
import lee.fund.common.protocol.RequestMessage;
import lee.fund.common.protocol.ResponseMessage;
import lee.fund.common.protocol.SimpleEncoder;
import lee.fund.common.protocol.SimpleValue;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/4 18:47
 * Desc:
 */
@RequiredArgsConstructor
public class ServerHandler extends SimpleChannelInboundHandler<RequestMessage> {
    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);
    private final NettyServer server;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestMessage requestMessage) throws Exception {
        //TODO 添加诊断用的依赖关系

        this.setChannelState(ctx, requestMessage);
        try {
            this.server.getThreadPool().execute(()-> new InnerTask(requestMessage,ctx.channel(),this.server.getServiceContainer()));
        } catch (RejectedExecutionException e) {
            ResponseMessage responseMessage = ResponseMessage.failed(RpcError.SERVER_BUSY);
            ctx.writeAndFlush(responseMessage);
            logger.error("biz thread pools is full，MaxThreads:{}",this.server.getServerConfig().getMaxThreads());
        }
    }

    private void setChannelState(ChannelHandlerContext ctx, RequestMessage requestMessage) {
        ChannelState state = ctx.channel().attr(ChannelState.KEY).get();
        if (state != null) {
            state.setActiveTime(LocalDateTime.now());
            state.setId(requestMessage.getClientName());
            state.setService(requestMessage.getServiceName());
            state.setMethod(requestMessage.getMethodName());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        logger.error("server error",cause);
        ctx.close();
    }

    @RequiredArgsConstructor
    private static class InnerTask implements Runnable{
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

        private static Object[] buildArgs(Class[] types, List<SimpleValue> values) {
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
