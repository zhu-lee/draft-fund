package lee.fund.app;


import lee.fund.remote.app.server.RemoteApplication;
import lee.fund.remote.app.server.ServerConfiguration;
import lee.fund.util.lang.ClassesUtils;

import java.util.List;
import java.util.Map;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/25 20:22
 * Desc:
 */
public class Application extends RemoteApplication {
    private AppServer appServer;

    public Application(Class<?> bootStrap, ServerConfiguration configuration, String[] args) {
        super(bootStrap, args, configuration);
        this.appServer = new AppServer(configuration);
    }

    @Override
    protected void beforeSetProperties(Map<String, Object> properties) {
        properties.put("spring.main.web_environment", false);
    }

    @Override
    protected void load() {
        this.scanService();
        this.appServer.start();
    }

    public void scanService() {
        String ifacePkg = this.bootStrap.getPackage().getName() + ".iface";
        List<String> serviceList = ClassesUtils.getClassListByPackage(ifacePkg);
        logger.info("there are {} class files under the iface folder", serviceList.size());
        serviceList.forEach(this::doScanService);
    }

    private void doScanService(String serviceName) {
        try {
            Class<?> clazz = Class.forName(serviceName);
            if (clazz.isInterface()) {
                Object instance = getBean(clazz, true);
                if (instance == null) {
                    //TODO 测试该情况
                    logger.warn("not found the bean instance：[{}]", serviceName);
                } else {
                    this.appServer.exposeService(clazz, instance);
                }
            }
        } catch (Exception e) {
            logger.error("failed to load the class [{}], perhaps no implementation class", serviceName, e);
        }
    }

    public <T> T getBean(Class<T> clazz, boolean autowire) {
        if (this.applicationContext == null) {
            throw new RuntimeException("you have to wait until the application starts running");
        }

        T bean = applicationContext.getBean(clazz);
        if (bean == null && !clazz.isInterface()) {//TODO Maybe there's no need to judge
            try {
                bean = clazz.newInstance();
                if (autowire) {
                    applicationContext.getAutowireCapableBeanFactory().autowireBean(bean);
                }
            } catch (Exception e) {
                String error = String.format("create bean instance of [%s] failed", clazz.getName());
                throw new RuntimeException(error, e);
            }
        }
        return bean;
    }
}