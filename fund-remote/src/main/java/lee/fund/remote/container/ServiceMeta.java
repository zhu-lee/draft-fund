package lee.fund.remote.container;

import lee.fund.remote.annotation.RpcService;
import lee.fund.remote.app.NamingConvertEnum;
import lee.fund.util.lang.StrKit;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/11 11:33
 * Desc:
 */
public class ServiceMeta {
    private static final ConcurrentHashMap<Class<?>, ServiceInfo> svcMetaMap = new ConcurrentHashMap<>();
    private static ServiceMeta instance = new ServiceMeta();
    private ServiceMeta() {
    }

    public static ServiceMeta instance() {
        return instance;
    }

    public ServiceInfo get(Class<?> clazz) {
        if (!svcMetaMap.containsKey(clazz)) {
            Optional<RpcService> rpcSrOptional = Optional.ofNullable(clazz.getAnnotation(RpcService.class));
            String description = rpcSrOptional.map(o -> o.description()).orElse(StringUtils.EMPTY);
            NamingConvertEnum convert = rpcSrOptional.map(o -> o.convention()).orElse(NamingConvertEnum.PASCAL);
            String name = rpcSrOptional.map(o -> o.name()).orElse(null);
            if (StrKit.isBlank(name)) {
                name = clazz.getSimpleName();
            }
            svcMetaMap.put(clazz, new ServiceInfo(clazz, name, description, convert));
        }
        return svcMetaMap.get(clazz);
    }
}
