package lee.fund.remote.annotation;

import lee.fund.remote.app.FailModeEnum;
import lee.fund.remote.app.NamingConvertEnum;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
@Documented
public @interface RpcService {
    String name() default "";//服务名称
    String description() default "";//服务描述
    NamingConvertEnum convention() default NamingConvertEnum.PASCAL;//服务方法命名约定
    FailModeEnum fail() default FailModeEnum.FailOver;//失败处理模式
}
