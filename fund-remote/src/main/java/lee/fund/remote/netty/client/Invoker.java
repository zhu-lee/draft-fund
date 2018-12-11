package lee.fund.remote.netty.client;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/11 17:21
 * Desc:
 */
public interface Invoker {
    Object invoke(String service, String method, Object[] args, Class<?> returnType);
}
