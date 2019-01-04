package lee.fund.remote.container;

import lee.fund.remote.annotation.RpcMethod;
import lee.fund.remote.annotation.RpcParameter;
import lee.fund.remote.app.FailModeEnum;
import lee.fund.remote.app.NamingConvertEnum;
import lee.fund.remote.util.MethodUtils;
import lee.fund.util.lang.StrUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/30 17:44
 * Desc:
 */
@Setter(AccessLevel.PROTECTED)
@Getter
public class ServiceInfo {
    private String name;// 名称
    private String description;// 描述
    private FailModeEnum failMode;//失败处理模式
    private Map<String, MethodInfo> methodMap;// 方法列表

    public ServiceInfo(Class<?> clazz, String name, String description, NamingConvertEnum convert, FailModeEnum failMode) {
        this.name = name;
        this.description = description;
        this.methodMap = new HashMap<>();
        this.failMode = failMode;
        Arrays.stream(clazz.getMethods()).filter(m -> m.getDeclaringClass() != Object.class).forEach(m -> {
            Optional<RpcMethod> mdOptional = Optional.ofNullable(m.getAnnotation(RpcMethod.class));
            MethodInfo mi = new MethodInfo();
            mi.name = MethodUtils.getMethodName(m, convert, mdOptional);
            mi.description = mdOptional.map(o -> o.description()).orElse(StringUtils.EMPTY);
            mi.returnType = getReturn(m);
            mi.parameters = getParameters(m);
            mi.failMode = mdOptional.map(o -> o.failMode()).orElse(FailModeEnum.FailOver);
            this.methodMap.put(mi.name, mi);
        });
    }

    private static List<ParameterInfo> getParameters(Method method) {
        List<ParameterInfo> list = Arrays.stream(method.getParameters()).map(p -> {
            ParameterInfo pi = getParameter(p.getType(), p.getAnnotation(RpcParameter.class));
            if (StrUtils.isBlank(pi.name)) {
                pi.name = p.getName();
            }
            return pi;
        }).collect(Collectors.toList());
        return list;
    }

    private static ParameterInfo getReturn(Method method) {
        Class<?> returnType = method.getReturnType();
        if (returnType == null || returnType == void.class) {
            return null;
        }
        return getParameter(returnType, method.getAnnotation(RpcParameter.class));
    }

    private static ParameterInfo getParameter(Class<?> paramType, RpcParameter rpcParameter) {
        Optional<RpcParameter> rpmOptional = Optional.ofNullable(rpcParameter);
        ParameterInfo pi = new ParameterInfo();
        pi.type = paramType;
        pi.name = rpmOptional.map(o -> o.name()).orElse(null);
        pi.description = rpmOptional.map(o -> o.description()).orElse(null);
        return pi;
    }

    @Getter
    @Setter
    public class MethodInfo {
        private String name;// 名称
        private String description;// 描述
        private List<ParameterInfo> parameters;// 参数列表
        private ParameterInfo returnType;// 返回值
        private FailModeEnum failMode;//失败处理模式
    }

    @Getter
    @Setter
    public static class ParameterInfo {
        private String name;// 名称
        private Class<?> type;// 类型
        private String description;// 描述
    }
}
