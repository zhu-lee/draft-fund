package lee.fund.util.config;

import lee.fund.util.log.ConsoleLogger;
import lee.fund.util.xml.XmlUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/3/8 18:11
 * Desc:
 */
@Setter(AccessLevel.PROTECTED)
@Getter
public class ClientConfHandler {
    private Map<String, ClientConf> clientConfMap = new HashMap<>(0);

    private ClientConfHandler() {
        this.init();
    }

    public static ClientConfHandler instance() {
        return Holder.instance;
    }

    private void init() {
        String fileName = "client-config.xml";
        String filePath = ConfigUtils.searchConf(fileName);
        if (filePath == null) {
            ConsoleLogger.info("config > not found %s", fileName);
            return;
        }

        ConsoleLogger.info("config > found %s", filePath);
        Map<String, Object> xmlMap = XmlUtils.parseMultiMap(filePath);
        Optional.ofNullable(xmlMap).ifPresent(t ->
                this.clientConfMap = t.entrySet().stream().map(e -> {
                    Map<String, Object> serMap = (Map<String, Object>) e.getValue();
                    ClientConf clientConf = new ClientConf();
                    Optional.ofNullable(serMap.get("name")).ifPresent(o -> clientConf.setName(o.toString()));
                    Optional.ofNullable(serMap.get("address")).ifPresent(o -> clientConf.setAddress(o.toString()));
                    Optional.ofNullable(serMap.get("discovery")).ifPresent(o -> clientConf.setDiscovery(Boolean.parseBoolean(o.toString())));
                    Optional.ofNullable(serMap.get("desc")).ifPresent(o -> clientConf.setDesc(o.toString()));

                    Optional.ofNullable(serMap.get("option")).ifPresent(s -> {
                        Map<String, Object> opMap = (Map<String, Object>) s;
                        Optional.ofNullable(opMap.get("maxConnections")).ifPresent(o -> clientConf.getOption().setMaxConnections(Integer.parseInt(o.toString())));
                        Optional.ofNullable(opMap.get("maxRetry")).ifPresent(o -> clientConf.getOption().setMaxRetry(Integer.parseInt(o.toString())));
                    });
                    return clientConf;
                }).collect(Collectors.toMap(m -> ((ClientConf) m).getName(), m -> (ClientConf) m))
        );
    }

    private static class Holder{
        private static ClientConfHandler instance;
        static {
            instance = new ClientConfHandler();
        }
    }
}
