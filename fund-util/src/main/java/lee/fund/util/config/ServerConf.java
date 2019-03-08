package lee.fund.util.config;

import lee.fund.util.log.ConsoleLogger;
import lee.fund.util.xml.XmlUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/6 15:47
 * Desc:
 */
@Setter(AccessLevel.PROTECTED)
@Getter
public class ServerConf {
    private String name = StringUtils.EMPTY;
    private boolean register;
    private Integer port;
    private String desc = StringUtils.EMPTY;
    private Option option;
    private Map<String, Object> customs;

    private ServerConf() {
        this.option = new Option();
        this.customs = new HashMap<>();
        this.init();
    }

    @Setter(AccessLevel.PROTECTED)
    @Getter
    public class Option {
        private int connections;
        private boolean debug;
        private boolean monitorEnabled;
        private int monitorPort;
    }

    public static ServerConf instance() {
        return Handler.instance;
    }

    private void init() {
        String fileName = "server-config.xml";
        String filePath = ConfigUtils.searchConf(fileName);
        if (filePath == null) {
            ConsoleLogger.info("config > not found %s", fileName);
            System.exit(1);
        }
        ConsoleLogger.info("config > found %s", filePath);

        Map<String, Object> xmlMap = XmlUtils.parseMultiMap(filePath);
        Optional.ofNullable(xmlMap).ifPresent(t -> {
            Map<String, Object> serMap = (Map<String, Object>) t.get("server");
            Optional.ofNullable(serMap.get("name")).ifPresent(o -> this.setName(o.toString()));
            Optional.ofNullable(serMap.get("port")).ifPresent(o -> this.setPort(Integer.parseInt(o.toString())));
            Optional.ofNullable(serMap.get("desc")).ifPresent(o -> this.setDesc(o.toString()));
            Optional.ofNullable(serMap.get("register")).ifPresent(o -> this.setRegister(Boolean.parseBoolean(o.toString())));

            Optional.ofNullable(serMap.get("option")).ifPresent(s -> {
                Map<String, Object> opMap = (Map<String, Object>) s;
                Optional.ofNullable(opMap.get("connections")).ifPresent(o -> this.getOption().setConnections(Integer.parseInt(o.toString())));
                Optional.ofNullable(opMap.get("debug")).ifPresent(o -> this.getOption().setDebug(Boolean.parseBoolean(o.toString())));
                Optional.ofNullable(opMap.get("monitor_enabled")).ifPresent(o -> this.getOption().setMonitorEnabled(Boolean.parseBoolean(o.toString())));
                Optional.ofNullable(opMap.get("monitor_port")).ifPresent(o -> this.getOption().setMonitorPort(Integer.parseInt(o.toString())));
            });

            Optional.ofNullable(xmlMap.get("custom")).ifPresent(o -> this.setCustoms((Map<String, Object>) o));
        });
    }

    private static class Handler {
        private static ServerConf instance;
        static {
            instance = new ServerConf();
        }
    }
}
