package lee.fund.mq.annotation;

import java.lang.annotation.*;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/6 11:58
 * Desc:
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Mq {
    String topic() default "";

    String channel() default "";

    int threads() default 4;

    String options() default "";

    boolean monitor() default false;
}
