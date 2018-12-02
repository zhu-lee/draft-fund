package lee.fund.simple;

import lee.fund.common.config.Configuration;
import lee.fund.remoting.application.RpcApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/29 21:17
 * Desc:
 */
@SpringBootApplication
public class TestApplication {
    private final static Logger logger = LoggerFactory.getLogger(TestApplication.class);
    public static void main(String[] args) throws Exception{
        Configuration configuration=new Configuration();
        RpcApplication app = new RpcApplication(TestApplication.class, configuration, args);
        app.run();
        logger.info("启动完成");
        System.in.read();
    }
}
