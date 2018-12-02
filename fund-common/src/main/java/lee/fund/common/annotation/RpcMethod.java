package lee.fund.common.annotation;

import lee.fund.common.app.FailModeEnum;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Inherited
@Documented
public @interface RpcMethod {
    /**
     * 方法名称
     * @return
     */
    String name() default "";

    /**
     * 方法描述
     * @return
     */
    String description() default "";

    /**
     * 失败处理模式
     * @return
     */
    FailModeEnum fail() default FailModeEnum.FailOver;
}
