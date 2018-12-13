package lee.fund.remote.netty.client;

import lee.fund.remote.app.client.ClientConfiguration;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/13 20:17
 * Desc:
 */
public class RemoteInvokerFactory {
    public static RemoteInvoker getRemoteInvoker(ClientConfiguration conf) {
        ClientConfig clientConfig = new ClientConfig(conf);
        ClientChannelPool pool = ClientChannelPool.getClientChannelPool(clientConfig);
        return RemoteInvoker.getRemoteInvoker(clientConfig, pool);
    }
}
