package lee.fund.remote.container;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.base.Joiner;
import lee.fund.remote.annotation.RpcService;
import lee.fund.remote.app.FailModeEnum;
import lee.fund.remote.app.NamingConvertEnum;
import lee.fund.remote.util.MethodUtils;
import lee.fund.util.lang.StrUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/30 17:44
 * Desc:
 */
public class ServiceContainer {
    private final Logger logger = LoggerFactory.getLogger(ServiceContainer.class);
    private Map<String, ServiceInfo> servicesMap = new HashMap<>();
    private Map<String, MethodExecutor> executorsMap = new HashMap<>();

    public void storeService(Class<?> clazz, Object instance) {
        Optional<RpcService> rpcSrOptional = Optional.ofNullable(clazz.getAnnotation(RpcService.class));
        String description = rpcSrOptional.map(o -> o.description()).orElse(StringUtils.EMPTY);
        NamingConvertEnum convert = rpcSrOptional.map(o -> o.convention()).orElse(NamingConvertEnum.PASCAL);
        FailModeEnum failMode = rpcSrOptional.map(o -> o.failMode()).orElse(FailModeEnum.FailOver);
        String serviceName = rpcSrOptional.map(o -> o.name()).orElse(null);
        if (StrUtils.isBlank(serviceName)) {
            serviceName = clazz.getSimpleName();
        }
        this.storeService(clazz, instance, serviceName, description, convert, failMode);
        this.storeMethodExecutor(clazz, convert, serviceName, instance);
    }

    private void storeService(Class<?> clazz, Object instance, String serviceName, String description, NamingConvertEnum convert, FailModeEnum failMode) {
        logger.info("expose service: " + serviceName);
        ServiceInfo serviceInfo = new ServiceInfo(clazz, serviceName, description, convert, failMode);
        this.servicesMap.put(serviceInfo.getName(), serviceInfo);
    }

    private void storeMethodExecutor(Class<?> clazz, NamingConvertEnum convert, String serviceName, Object instance) {
        MethodAccess access = MethodAccess.get(clazz);
        Method[] methods = clazz.getMethods();
        Arrays.stream(methods).filter(m -> m.getDeclaringClass() != Object.class).forEach(m -> {
            try {
                int index = access.getIndex(m.getName());//方法索引
                String methodName = MethodUtils.getMethodName(m, convert);
                MethodExecutor methodExecutor = new MethodExecutor(access, instance, index);
                String smKey = buildKey(serviceName, methodName);
                executorsMap.put(smKey, methodExecutor);
                logger.info("expose service.methodName: {}.{}", serviceName, methodName);
            } catch (IllegalArgumentException e) {
                logger.warn("find method index failed: {}", e);
            }
        });
    }

    private String buildKey(String serviceName, String methodName) {
        return Joiner.on(".").join(serviceName, methodName);
    }

    public MethodExecutor getExecutor(String serviceName, String methodName) {
        return executorsMap.get(buildKey(serviceName, methodName));
    }
}