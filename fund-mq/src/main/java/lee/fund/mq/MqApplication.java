package lee.fund.mq;

import lee.fund.remote.app.server.RemoteApplication;
import lee.fund.remote.app.server.ServerConfiguration;

import java.util.Map;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/5 21:12
 * Desc:
 */
public class MqApplication extends RemoteApplication {
    public MqApplication(Class<?> bootStrap, String[] args, ServerConfiguration serverConfiguration) {
        super(bootStrap, args, serverConfiguration);
    }

    @Override
    protected void beforeSetProperties(Map<String, Object> properties) {
        properties.put("spring.main.web_environment", false);
    }

    @Override
    protected void load() {
        this.scanMqHandler();
    }

    private void scanMqHandler() {
        this.applicationContext.getBeansOfType(MqHandler.class);
    }
}
