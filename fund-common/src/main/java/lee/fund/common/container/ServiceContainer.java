package lee.fund.common.container;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.base.Joiner;
import lee.fund.common.annotation.RpcMethod;
import lee.fund.common.annotation.RpcService;
import lee.fund.common.app.NamingConvertEnum;
import lee.fund.common.util.MethodUtils;
import lee.fund.util.lang.StrKit;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/30 17:44
 * Desc:
 */
public class ServiceContainer {
    private final Logger logger = LoggerFactory.getLogger(ServiceContainer.class);
    private Map<String, ServiceInfo> containerMap = new HashMap<>();
    private Map<String, MethodExecutor> executorMap = new HashMap<>();

    public void storeService(Class<?> clazz, Object instance) {
        Optional<RpcService> rpcSrOptional = Optional.ofNullable(clazz.getAnnotation(RpcService.class));
        String description = rpcSrOptional.map(o -> o.description()).orElse(Strings.EMPTY);
        NamingConvertEnum convert = rpcSrOptional.map(o -> o.convention()).orElse(NamingConvertEnum.PASCAL);
        String name = rpcSrOptional.map(o -> o.name()).orElse(null);
        if (StrKit.isBlank(name)) {
            name = clazz.getSimpleName();
        }
        this.storeService(clazz, instance, name, description, convert);
    }

    private void storeService(Class<?> clazz, Object instance, String name, String description, NamingConvertEnum convert) {
        logger.info("expose service: " + name);
        MethodAccess access = MethodAccess.get(clazz);
        Method[] methods = clazz.getMethods();
        Arrays.stream(methods).filter(m->m.getDeclaringClass()!=Object.class).forEach(m -> {
            try {
                int index = access.getIndex(m.getName());//方法索引
                String methodName = MethodUtils.getMethodName(m, convert);
                MethodExecutor methodExecutor = new MethodExecutor(access, instance, index);
                String smKey = buildKey(name, methodName);
                executorMap.put(smKey, methodExecutor);
                logger.info("expose service: {}.{}", name, methodName);
            } catch (IllegalArgumentException e) {
                logger.warn("find method index failed: {}", e);
            }
        });

        ServiceInfo serviceInfo = new ServiceInfo(clazz, name, description, convert);
        this.containerMap.put(serviceInfo.getName(), serviceInfo);
    }

    private String buildKey(String serviceName, String methodName) {
        return Joiner.on(".").join(serviceName, methodName);
    }

    public MethodExecutor getExecutor(String service, String method) {
        return executorMap.get(buildKey(service, method));
    }
}