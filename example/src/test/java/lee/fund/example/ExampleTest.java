package lee.fund.example;

import lee.fund.example.iface.ExampleService;
import lee.fund.util.ioc.ServiceLocator;
import org.junit.Test;

import java.util.List;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/3/7 14:24
 * Desc:
 */
public class ExampleTest extends AbstractTest{
    ExampleService service = ServiceLocator.INSTANCE.getBean(ExampleService.class);
    @Test
    public void testExampleService() throws Exception {
        List<String> list = service.queryList(1, "a");
        System.out.println(list.size());
    }
}
