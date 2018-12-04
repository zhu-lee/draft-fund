package lee.fund.common.app;

import lee.fund.common.config.Configuration;
import lee.fund.common.monitor.HttpMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;

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
    protected Configuration configuration;

    public AbstractApplication(Class<?> bootStrap,String[] args,Configuration configuration) {
        this.springApp = new SpringApplication(bootStrap);
        this.bootStrap = bootStrap;
        this.args = args;
        this.configuration = configuration;
        this.logger = LoggerFactory.getLogger(bootStrap);
    }

    public void run(){
        this.startTime = LocalDateTime.now();
        this.springContext = this.springApp.run(args);
        this.load();
        this.startMonitor();
    }

    protected abstract void load();

    private void startMonitor() {
        try {
            if (configuration.isMonitorEnabled() && configuration.getMonitorPort() > 0) {
                HttpMonitor monitor = new HttpMonitor(new InetSocketAddress(configuration.getMonitorPort()),false);
                setMonitor(monitor);
                monitor.start();
                logger.info("monitor start at {}",configuration.getMonitorPort());
            }
        } catch (Exception e) {
            logger.info("monitor start failed");
        }
    }

    protected void setMonitor(HttpMonitor monitor) {

    }
}
