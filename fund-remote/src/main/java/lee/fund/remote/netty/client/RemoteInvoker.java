package lee.fund.remote.netty.client;

import com.google.common.base.Strings;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.pool.ChannelPool;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.ObjectUtil;
import lee.fund.remote.context.Guid;
import lee.fund.remote.context.RpcContext;
import lee.fund.remote.exception.RpcError;
import lee.fund.remote.exception.RpcException;
import lee.fund.remote.protocol.RemoteCoder;
import lee.fund.remote.protocol.RemoteValue;
import lee.fund.remote.protocol.RequestMessage;
import lee.fund.remote.protocol.ResponseMessage;
import lee.fund.util.config.AppConf;
import lee.fund.util.lang.FaultException;
import lee.fund.util.lang.StrUtils;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static lee.fund.remote.exception.RpcExceptions.*;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/12 16:13
 * Desc:
 */
public class RemoteInvoker implements Invoker {
    private static final ConcurrentMap<String, RemoteInvoker> invokersMap = new ConcurrentHashMap<>();
    private final ClientConfig clientConfig;
    private final ChannelPool channelPool;
    private static final String appName = Strings.nullToEmpty(AppConf.INSTANCE.getServerConf().getName());

    public RemoteInvoker(ClientConfig clientConfig, ChannelPool channelPool) {
        ObjectUtil.checkNotNull(clientConfig.getAddress(), "net address");
        this.clientConfig = clientConfig;
        this.channelPool = channelPool;
    }

    public static RemoteInvoker getRemoteInvoker(ClientConfig clientConfig, ChannelPool channelPool) {
        return invokersMap.computeIfAbsent(clientConfig.getAddress().toString(), k -> new RemoteInvoker(clientConfig, channelPool));
    }

    @Override
    public Object invoke(String serviceName, String method, Object[] args, Class<?> returnType) {
        RequestMessage requestMessage;
        ResponseMessage responseMessage;
        String error;
        SimpleClientChannel channel = null;

        try {
            try {
                requestMessage = this.createRequestMessage(serviceName, method, args);
            } catch (Exception e) {
                error = e.getMessage();
                throw fault(RpcError.CLIENT_UNKNOWN_ERROR, e);
            }

            //get connection
            Future<Channel> future = channelPool.acquire().awaitUninterruptibly();
            if (!future.isSuccess()) {
                error = future.cause().getMessage();
                throw fault(RpcError.CLIENT_ACQUIRE_FAILED, future.cause());
            }

            //send request
            channel = (SimpleClientChannel) future.getNow();
            channel.reset();
            ChannelFuture channelFuture = channel.writeAndFlush(requestMessage).awaitUninterruptibly();
            if (!channelFuture.isSuccess()) {
                error = channelFuture.cause().getMessage();
                throw fault(RpcError.CLIENT_WRITE_FAILED, channelFuture.cause());
            }

            //waiting for response
            try {
                responseMessage = channel.get(clientConfig.getReadTimeout());
            } catch (Exception e) {
                error = e.getMessage();
                throw fault(RpcError.CLIENT_UNKNOWN_ERROR, e);
            }

            if (responseMessage == null) {
                channel.close();
                error = String.format("服务器响应超时(%dms)", this.clientConfig.getReadTimeout());
                throw RpcError.CLIENT_READ_FAILED.toFault(error);
            } else if (responseMessage.isSuccess()) {
                try {
                    RemoteValue value = responseMessage.getResult();
                    return RemoteCoder.decode(value.getDataType(), value.getData(), returnType);
                } catch (Exception e) {
                    error = e.getMessage();
                    throw fault(RpcError.CLIENT_UNKNOWN_ERROR, e);
                }
            } else {
                error = responseMessage.getErrorInfo();
                throw fault(responseMessage);
            }
        } finally {
            if (channel != null) {
                channelPool.release(channel);
            }
            //TODO report log
        }
    }

    private RequestMessage createRequestMessage(String serviceName, String methodName, Object[] args) {
        RequestMessage requestMessage = new RequestMessage();
        requestMessage.setClientName(appName);
        requestMessage.setServerName(clientConfig.getName());
        requestMessage.setServiceName(serviceName);
        requestMessage.setMethodName(methodName);
        requestMessage.setContextID(RpcContext.getContextID());
        requestMessage.setMessageID(Guid.get());
        if (args != null && args.length > 0) {
            requestMessage.setParameters(Arrays.stream(args).map(arg -> {
                RemoteValue value = RemoteCoder.encode(arg);
                return value;
            }).collect(Collectors.toList()));
        }
        return requestMessage;
    }

    private FaultException fault(RpcError error, Throwable e) {
        int code;
        String msg;
        if (e instanceof RpcException) {
            RpcException re = (RpcException) e;
            code = re.getErrorCode();
            msg = re.getMessage();
        } else {
            code = error.value();
            msg = String.format(error.description(), e.getMessage());
        }
        FaultException fe = new FaultException(code, msg, e);
        putRemoteServer(fe, clientConfig);
        putProfile(fe);
        return fe;
    }

    private FaultException fault(ResponseMessage m) {
        FaultException fe = new FaultException(m.getErrorCode(), m.getErrorInfo());
        if (!StrUtils.isBlank(m.getErrorDetail())) {
            putRemoteError(fe, m.getErrorDetail());
        }
        putRemoteServer(fe, clientConfig);
        putProfile(fe);
        return fe;
    }
}
