package lee.fund.mq;

import lee.fund.mq.annotation.Mq;
import lee.fund.mq.nsq.SubscriberInfo;
import lee.fund.remote.app.server.RemoteApplication;
import lee.fund.remote.app.server.ServerConfiguration;
import lee.fund.mq.nsq.NsqSubscriber;

import java.util.Map;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/5 21:12
 * Desc:
 */
public class MqApplication extends RemoteApplication {
    private Subscriber subscriber = NsqSubscriber.INSTANCE;

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
        Map<String, MqHandler> handlerBans = this.applicationContext.getBeansOfType(MqHandler.class);
        logger.info("find {} MqHandler beans", handlerBans.size());

        handlerBans.forEach((k, handler) -> {
            Class<?> cls = handler.getClass();
            Mq mq = cls.getAnnotation(Mq.class);
            if (mq == null) {
                logger.warn("[{}] are not marked with @Mq，skip auto subscribe", cls);
                return;
            }
            this.subscriber.subscribe(new SubscriberInfo(mq, handler));
        });
    }
}