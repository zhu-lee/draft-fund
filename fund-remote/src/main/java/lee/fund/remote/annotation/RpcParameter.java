package lee.fund.remote.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Inherited
@Documented
public @interface RpcParameter {
    String name() default "";//方法参数或返回值名称
    String description() default "";//方法参数或返回值描述
}
