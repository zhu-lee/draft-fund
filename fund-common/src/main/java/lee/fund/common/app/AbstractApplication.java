package lee.fund.common.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.time.LocalDateTime;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/29 9:19
 * Desc:
 */
public abstract class AbstractApplication {
    protected SpringApplication springApp;
    protected Class<?> bootStrap;
    protected Logger logger;
    protected ApplicationContext springContext;
    protected LocalDateTime startTime;
    protected String[] args;

    public AbstractApplication(Class<?> bootStrap,String[] args){
        this.springApp = new SpringApplication(bootStrap);
        this.bootStrap = bootStrap;
        this.args = args;
        this.logger = LoggerFactory.getLogger(bootStrap);
    }

    public void run(){
        this.startTime = LocalDateTime.now();
        this.springContext = this.springApp.run(args);
        this.load();
    }

    protected abstract void load();

    private void startMonitor() {

    }
}
