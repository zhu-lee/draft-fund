package lee.fund.remote.container;

import lee.fund.remote.annotation.RpcService;
import lee.fund.remote.app.FailModeEnum;
import lee.fund.remote.app.NamingConvertEnum;
import lee.fund.util.lang.StrUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/11 11:33
 * Desc:
 */
public class ServiceMeta {
    private static final ConcurrentMap<Class<?>, ServiceInfo> svcMetaMap = new ConcurrentHashMap<>();
    private static ServiceMeta instance = new ServiceMeta();
    private ServiceMeta() {
    }

    public static ServiceMeta instance() {
        return instance;
    }

    public ServiceInfo get(Class<?> clazz) {
        return svcMetaMap.computeIfAbsent(clazz,k->{
            Optional<RpcService> rpcSrOptional = Optional.ofNullable(clazz.getAnnotation(RpcService.class));
            String description = rpcSrOptional.map(o -> o.description()).orElse(StringUtils.EMPTY);
            NamingConvertEnum convert = rpcSrOptional.map(o -> o.convention()).orElse(NamingConvertEnum.PASCAL);
            String name = rpcSrOptional.map(o -> o.name()).orElse(null);
            if (StrUtils.isBlank(name)) {
                name = clazz.getSimpleName();
            }
            FailModeEnum failMode = rpcSrOptional.map(o -> o.failMode()).orElse(FailModeEnum.FailOver);
            return new ServiceInfo(clazz, name, description, convert, failMode);
        });
    }
}
