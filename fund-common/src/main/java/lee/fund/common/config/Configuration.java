package lee.fund.common.config;

import lee.fund.common.util.ConfigUtils;
import lee.fund.util.xml.XmlUtils;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/25 20:21
 * Desc:
 */
@Setter
@Getter
public class Configuration {
    private final Logger logger = LoggerFactory.getLogger(Configuration.class);
    private String name;
    private int port;
    private boolean registry;
    private int connections;
    private String desc;
    private boolean debug;
    private boolean monitorEnabled;
    private int monitorPort;

    public Configuration(){
        loadConfiguration();
    }

    private void loadConfiguration(){
        String fileName = "remote.server.xml";
        String filePath = ConfigUtils.searchConfig(fileName);
        if (filePath == null) {
            logger.error("config > not found {}",fileName);
            System.exit(1);
        }

        logger.info("config > found {}",filePath);
        Map<String, String> xmlMap = XmlUtils.parseXml2Map(filePath);
        logger.info("config > {}={}",fileName, xmlMap);
        if (xmlMap.isEmpty()) {
            logger.error("config > parse {} failed",fileName);
            System.exit(1);
        }

        String name = XmlUtils.getXmlString(xmlMap, "name");
        requireNonNull(name, "name", fileName);
        this.setName(name);

        Integer port = XmlUtils.getXmlInt(xmlMap, "port");
        requireNonNull(port, "port", fileName);
        this.setPort(port);

        Boolean registry = XmlUtils.getXmlBoolean(xmlMap, "registry");
        requireNonNull(registry, "registry", fileName);
        this.setRegistry(registry);

        Integer connections = XmlUtils.getXmlInt(xmlMap, "connections");
        requireNonNull(connections, "connections", fileName);
        this.setConnections(connections);

        String desc = XmlUtils.getXmlString(xmlMap, "desc");
        requireNonNull(desc, "desc", fileName);
        this.setDesc(desc);

        Optional<Boolean> debugOptional = Optional.ofNullable(XmlUtils.getXmlBoolean(xmlMap, "debug"));
        debugOptional.ifPresent(o->this.setDebug(o));

        Optional<Boolean> monitorEnabledOptional = Optional.ofNullable(XmlUtils.getXmlBoolean(xmlMap, "monitor_enabled"));
        monitorEnabledOptional.ifPresent(o->this.setMonitorEnabled(o));

        Optional<Integer> monitorPortOptional = Optional.ofNullable(XmlUtils.getXmlInt(xmlMap, "monitor_port"));
        monitorPortOptional.ifPresent(o -> this.setMonitorPort(o));
    }

    private void requireNonNull(Object va,String filed,String fileName) {
        Objects.requireNonNull(va, String.format("config > %s->%s",fileName,filed));
    }
}
