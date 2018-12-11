package lee.fund.remote.annotation;

import lee.fund.remote.app.FailModeEnum;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
@Documented
public @interface RpcMethod {
    //方法名称
    String name() default "";

    //方法描述
    String description() default "";

    //失败处理模式
    FailModeEnum fail() default FailModeEnum.FailOver;
}
