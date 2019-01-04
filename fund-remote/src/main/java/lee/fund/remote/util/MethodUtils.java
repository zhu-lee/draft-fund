package lee.fund.remote.util;

import lee.fund.remote.annotation.RpcMethod;
import lee.fund.remote.app.NamingConvertEnum;
import lee.fund.util.lang.StrUtils;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/2 17:42
 * Desc:
 */
public class MethodUtils {
    private MethodUtils(){}

    public static String getMethodName(Method m, NamingConvertEnum convert) {
        return getMethodName(m, convert, Optional.ofNullable(m.getAnnotation(RpcMethod.class)));
    }

    public static String getMethodName(Method method, NamingConvertEnum convert, Optional<RpcMethod> mdOptional) {
        String name = mdOptional.map(o->o.name()).orElse(null);
        if (StrUtils.isBlank(name)) {
            name = NamingConvertEnum.transform(method.getName(), convert);
        }
        return name;
    }
}