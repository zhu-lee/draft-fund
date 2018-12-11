package lee.fund.remote.app.server;

import lee.fund.remote.monitor.HttpMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/29 9:19
 * Desc:
 */
public abstract class RemoteApplication {
    protected SpringApplication springApp;
    protected Class<?> bootStrap;
    protected Logger logger;
    protected ApplicationContext springContext;
    protected LocalDateTime startTime;
    protected String[] args;
    protected ServerConfiguration configuration;

    public RemoteApplication(Class<?> bootStrap, String[] args, ServerConfiguration configuration) {
        this.springApp = new SpringApplication(bootStrap);
        this.bootStrap = bootStrap;
        this.args = args;
        this.configuration = configuration;
        this.logger = LoggerFactory.getLogger(bootStrap);
        //TODO 加载(如果有spring.xml)  spring.config.location这个关键字指定配置文件的路径 spring.config.name”等于{application}参数加载配置文件
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
        //TODO custom monitor
    }
}
