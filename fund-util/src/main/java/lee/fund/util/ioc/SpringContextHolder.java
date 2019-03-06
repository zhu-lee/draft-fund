package lee.fund.util.ioc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/3/6 19:54
 * Desc:
 */
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SpringContextHolder {
    @Autowired
    private static ApplicationContext ctx;

//    private SpringContextHolder(){}
//
//    @Bean
//    @ConditionalOnMissingBean
//    public SpringContextHolder getInstance() {
//        return new SpringContextHolder();
//    }

    public static <T> T getBean(Class<T> classType) {
        return ctx.getBean(classType);
    }
}
