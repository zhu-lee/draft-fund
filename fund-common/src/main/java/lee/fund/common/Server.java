package lee.fund.common;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/23 16:35
 * Desc:
 */
public interface Server {
    void start();

    void shutdown();

    void exposeService(Class<?> clazz, Object instance);

    void register();

    Object getData(String key);
}
