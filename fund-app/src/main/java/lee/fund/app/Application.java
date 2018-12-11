package lee.fund.app;


import lee.fund.remote.app.server.RemoteApplication;
import lee.fund.remote.app.server.ServerConfiguration;
import lee.fund.util.lang.ClassesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/25 20:22
 * Desc:
 */
public class Application extends RemoteApplication {
    private final Logger logger = LoggerFactory.getLogger(ClassesUtils.class);
    private AppServer rpcServer;

    public Application(Class<?> bootStrap, ServerConfiguration configuration, String[] args) {
        super(bootStrap, args, configuration);
        this.rpcServer = new AppServer(configuration);
    }

    @Override
    protected void load() {
        this.scanService();
        this.rpcServer.start();
    }

    public void scanService(){
        String[] packages = new String[]{this.bootStrap.getPackage().getName() + ".iface"};
        if (packages.length == 0) {
            logger.info("there are no class files under the iface folder");
        }

        Arrays.stream(packages).forEach(t->{
            List<String> serviceList = ClassesUtils.getClassListByPackage(t);
            logger.info("there are {} class files under the iface folder",serviceList.size());
            serviceList.forEach(this::doScanService);
        });
    }

    private void doScanService(String serviceName) {
        try {
            Class<?> clazz = Class.forName(serviceName);
            if (clazz.isInterface()) {
                Object instance = getBean(clazz, true);
                if (instance == null) {
                    logger.warn("not found the bean instance：[{}]", serviceName);
                } else {
                    this.rpcServer.exposeService(clazz, instance);
                }
            }
        } catch (Exception e) {
            logger.error("failed to load the class [{}], perhaps no implementation class", serviceName, e);
        }
    }

    public <T> T getBean(Class<T> clazz, boolean autowire) {
        if (this.springContext == null) {
            throw new RuntimeException("You have to wait until the application starts running");
        }

        T bean = springContext.getBean(clazz);
        if (bean == null && !clazz.isInterface()) {
            try {
                bean = clazz.newInstance();
                if (autowire) {
                    springContext.getAutowireCapableBeanFactory().autowireBean(bean);
                }
            } catch (Exception e) {
                String error = String.format("create bean instance of [%s] failed", clazz.getName());
                throw new RuntimeException(error, e);
            }
        }
        return bean;
    }
}