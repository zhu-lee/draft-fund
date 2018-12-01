package lee.fund.common.annotation;

import lee.fund.common.app.FailModeEnum;
import lee.fund.common.app.NamingConventionEnum;

import java.lang.annotation.*;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/1 15:40
 * Desc:
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
@Documented
public @interface RpcService {
    /**
     * 服务名称
     * @return
     */
    String name() default "";

    /**
     * 服务描述
     * @return
     */
    String description() default "";

    /**
     * 服务方法命名约定
     * @return
     */
    NamingConventionEnum convention() default NamingConventionEnum.PASCAL;

    /**
     * 失败处理模式
     * @return
     */
    FailModeEnum fail() default FailModeEnum.FailOver;
}
