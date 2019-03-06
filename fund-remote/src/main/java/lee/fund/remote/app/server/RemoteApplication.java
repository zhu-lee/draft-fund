package lee.fund.remote.app.server;

import lee.fund.remote.monitor.HttpMonitor;
import lee.fund.util.config.ConfProperties;
import lee.fund.util.config.ConfigUtils;
import lee.fund.util.ioc.SpringContextHolder;
import lee.fund.util.lang.StrUtils;
import lee.fund.util.log.ConsoleLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import java.net.InetSocketAddress;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/29 9:19
 * Desc:
 */
public abstract class RemoteApplication {
    protected SpringApplication springApplication;
    protected Class<?> bootStrap;
    protected String[] args;
    protected ServerConfiguration serverConfiguration;
    protected Logger logger;
    protected ApplicationContext applicationContext;
    protected LocalDateTime startTime;

    public RemoteApplication(Class<?> bootStrap, String[] args, ServerConfiguration serverConfiguration) {
        Objects.requireNonNull(bootStrap, "bootClass can't be null");
        Objects.requireNonNull(args, "args can't be null");

        this.springApplication = new SpringApplication(SpringContextHolder.class, bootStrap);//TODO serviceLocatorAutoConfig 装载
        this.bootStrap = bootStrap;
        this.args = args;
        this.serverConfiguration = serverConfiguration;
        this.logger = LoggerFactory.getLogger(bootStrap);

        this.init();
    }

    private void init() {
        //加载spring.xml
        String filePath = ConfigUtils.searchConf("spring.xml");
        if (filePath != null) {
            setSources("file:" + filePath);
        }

        //设置properties
        this.setProperties();
    }

    private void setSources(Object... sources) {
        if (sources != null) {
            Set<String> sourceSet = Arrays.stream(sources).map(t -> t.toString()).collect(Collectors.toSet());
            this.springApplication.setSources(sourceSet);
        }
    }

    private void setProperties() {
        Map<String, Object> properties = new HashMap<>();
        String etcDir = ConfigUtils.getEtcFolder();
        properties.put("spring.main.banner-mode", "off");
        properties.put("spring.config.location", etcDir);
        properties.put("spring.config.name", "app");
        properties.put("spring.profiles.active", ConfProperties.INSTANCE.getActiveProfile());
        String logConfigPath = ConfigUtils.searchConf("log4j2.xml");
        if (logConfigPath == null) {
            logConfigPath = ConfigUtils.searchGlobalConf("log4j2.xml");
        }
        properties.put("logging.config", logConfigPath);
        String[] excludes = {
                "org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration",
                "org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration",
                "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration",
                "org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration"
        };
        properties.put("spring.autoconfigure.exclude", excludes);
        this.beforeSetProperties(properties);
        this.springApplication.setDefaultProperties(properties);
    }

    protected void beforeSetProperties(Map<String, Object> properties) {
        //subclass to inject
    }

    public void run() {
        this.startTime = LocalDateTime.now();
        this.applicationContext = this.springApplication.run(args);
        this.load();
        this.startMonitor();
    }

    protected void load() {
        //subclass to inject
    }

    private void startMonitor() {
        try {
            if (serverConfiguration != null && serverConfiguration.isMonitorEnabled() && serverConfiguration.getMonitorPort() > 0) {
                HttpMonitor monitor = new HttpMonitor(new InetSocketAddress(serverConfiguration.getMonitorPort()), false);
                setMonitor(monitor);
                monitor.start();
                logger.info("monitor start at {}", serverConfiguration.getMonitorPort());
            }
        } catch (Exception e) {
            logger.info("monitor start failed");
        }
    }

    protected void setMonitor(HttpMonitor monitor) {
        //TODO subclass to inject
    }
}