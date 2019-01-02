package lee.fund.remote.netty.client;

import com.google.common.base.Strings;
import io.netty.channel.pool.ChannelPool;
import io.netty.util.internal.ObjectUtil;
import lee.fund.remote.context.Guid;
import lee.fund.remote.context.RpcContext;
import lee.fund.remote.exception.RpcError;
import lee.fund.remote.exception.RpcException;
import lee.fund.remote.protocol.RequestMessage;
import lee.fund.remote.protocol.SimpleValue;
import lee.fund.util.config.AppConf;
import lee.fund.util.lang.FaultException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/12 16:13
 * Desc:
 */
public class RemoteInvoker implements Invoker {
    private static final ConcurrentMap<String, RemoteInvoker> invokersMap = new ConcurrentHashMap<>();
    private ClientConfig clientConfig;
    private ChannelPool channelPool;
    private static String appName;

    static {
        appName = Strings.nullToEmpty(AppConf.instance().getServerConf().getName());
    }

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
        try {
            try {
                requestMessage = this.createRequestMessage(serviceName, method, args);
            } catch (Exception e) {
                throw fault(RpcError.CLIENT_UNKNOWN_ERROR, e.getMessage());
            }

        } catch (Exception e) {

        }

        return null;
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
            List<SimpleValue> parameters = new ArrayList<>(args.length);
            for (Object arg : args) {
                //TODO 编码？？？？
//                SimpleValue value = SimpleEncoder.encode(arg);
//                parameters.add(value);
            }
            requestMessage.setParameters(parameters);
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

    public static void putRemoteServer(FaultException fault, ClientConfig clientConfig) {
        fault.getData().put(CustomErrorDataKeys.SERVER_NAME, clientConfig.getName());
        fault.getData().put(CustomErrorDataKeys.SERVER_ADDRESS, clientConfig.getAddress().toString());
    }
}
