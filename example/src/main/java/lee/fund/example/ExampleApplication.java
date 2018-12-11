package lee.fund.example;

import lee.fund.remote.app.server.ServerConfiguration;
import lee.fund.app.Application;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/4 14:33
 * Desc:
 */
@SpringBootApplication
public class ExampleApplication {
    public static void main(String[] args) {
        ServerConfiguration configuration=new ServerConfiguration();
        Application app = new Application(ExampleApplication.class, configuration, args);
        app.run();
    }
}
