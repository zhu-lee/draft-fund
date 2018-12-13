package lee.fund.remote.netty.client;

import io.netty.channel.pool.ChannelPool;
import io.netty.util.internal.ObjectUtil;

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

    public RemoteInvoker(ClientConfig clientConfig, ChannelPool channelPool) {
        ObjectUtil.checkNotNull(clientConfig.getAddress(), "net address");
        this.clientConfig = clientConfig;
        this.channelPool = channelPool;
    }

    public static RemoteInvoker getRemoteInvoker(ClientConfig clientConfig, ChannelPool channelPool) {
        return invokersMap.computeIfAbsent(clientConfig.getAddress().toString(), k -> new RemoteInvoker(clientConfig, channelPool));
    }


    @Override
    public Object invoke(String service, String method, Object[] args, Class<?> returnType) {
        return null;
    }
}
