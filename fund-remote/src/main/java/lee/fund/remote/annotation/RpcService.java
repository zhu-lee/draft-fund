package lee.fund.remote.annotation;

import lee.fund.remote.app.FailModeEnum;
import lee.fund.remote.app.NamingConvertEnum;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
@Documented
public @interface RpcService {
    //服务名称
    String name() default "";

    //服务描述
    String description() default "";

    //服务方法命名约定
    NamingConvertEnum convention() default NamingConvertEnum.PASCAL;

    //失败处理模式
    FailModeEnum failMode() default FailModeEnum.FailOver;
}
