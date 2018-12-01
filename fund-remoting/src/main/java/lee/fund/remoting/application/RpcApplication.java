package lee.fund.remoting.application;


import lee.fund.common.app.AbstractApplication;
import lee.fund.common.config.Configuration;
import lee.fund.util.lang.ClassesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/25 20:22
 * Desc:
 */
public class RpcApplication extends AbstractApplication {
    private final Logger logger = LoggerFactory.getLogger(ClassesUtils.class);

    private RpcServer rpcServer;

    public RpcApplication(Class<?> bootStrap, Configuration configuration, String[] args) {
        super(bootStrap, args);
        this.rpcServer = new RpcServer(configuration);
    }

    @Override
    protected void load() {
        this.scanService();
        this.rpcServer.openServer();
    }

    public void scanService(){
        String[] packages = new String[]{this.bootStrap.getPackage().getName() + ".iface"};
        if (packages.length == 0) {
            logger.info("there are no class files under the iface folder");
        }
        for (String pkg : packages) {
            List<String> serviceList = ClassesUtils.getClassListByPackage(pkg);
            logger.info("there are {} class files under the iface folder",serviceList.size());
            serviceList.forEach(this::doScanService);
        }
    }

    private void doScanService(String serviceName) {
        try {
            Class<?> clazz = Class.forName(serviceName);
            if (clazz.isInterface()) {
                Object instance = getBean(clazz);
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

    public <T> T getBean(Class<T> clazz) {
        return getBean(clazz, true);
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