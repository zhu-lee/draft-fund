package lee.fund.util.ioc;

import lombok.Setter;
import org.springframework.context.ApplicationContext;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/3/7 17:54
 * Desc:
 */
public class ServiceLocator {
    @Setter
    private ApplicationContext ctx;
    public static final ServiceLocator INSTANCE = new ServiceLocator();
    private ServiceLocator(){}

    public <T> T getBean(Class<T> clsType) {
        return ctx.getBean(clsType);
    }
}
