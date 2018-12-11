package lee.fund.util.config;

import lee.fund.util.log.ConsoleLogger;
import lee.fund.util.xml.XmlUtils;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/6 15:16
 * Desc:
 */
@Getter
public class AppConf {
    private ServerConf serConf;
    private GlobalConf glabConf;
    private Map<String, CsumConf> csumConfs = new HashMap<>();

    private AppConf() {
        this.loadAppConf();
        this.loadGolobalConf();
        this.loadCsumConf();
    }

    private void loadAppConf() {
        String fileName = "app-config.xml";
        String filePath = ConfigUtils.searchConf(fileName);
        if (filePath == null) {
            ConsoleLogger.info("config > not found %s", fileName);
            System.exit(1);
        }
        ConsoleLogger.info("config > found %s", filePath);

        serConf = new ServerConf();
        Map<String, Object> xmlMap = XmlUtils.parseMultiMap(filePath);
        Optional.ofNullable(xmlMap).ifPresent(t -> {
            Map<String, Object> serMap = (Map<String, Object>) t.get("server");
            Optional.ofNullable(serMap.get("name")).ifPresent(o -> serConf.setName(o.toString()));
            Optional.ofNullable(serMap.get("port")).ifPresent(o -> serConf.setPort(Integer.parseInt(o.toString())));
            Optional.ofNullable(serMap.get("desc")).ifPresent(o -> serConf.setDesc(o.toString()));

            Optional.ofNullable(serMap.get("option")).ifPresent(s -> {
                Map<String, Object> opMap = (Map<String, Object>) s;
                Optional.ofNullable(opMap.get("connections")).ifPresent(o -> serConf.getOption().setConnections(Integer.parseInt(o.toString())));
                Optional.ofNullable(opMap.get("debug")).ifPresent(o -> serConf.getOption().setDebug(Boolean.parseBoolean(o.toString())));
                Optional.ofNullable(opMap.get("monitor_enabled")).ifPresent(o -> serConf.getOption().setMonitorEnabled(Boolean.parseBoolean(o.toString())));
                Optional.ofNullable(opMap.get("monitor_port")).ifPresent(o -> serConf.getOption().setMonitorPort(Integer.parseInt(o.toString())));
            });

            Optional.ofNullable(xmlMap.get("custom")).ifPresent(o -> serConf.setCustoms((Map<String, Object>) o));
        });
    }

    private void loadGolobalConf() {
        String fileName = "global-config.xml";
        String filePath = ConfigUtils.searchGlobalConf(fileName);
        if (filePath == null) {
            ConsoleLogger.info("config > not found %s", fileName);
            System.exit(1);
        }
        ConsoleLogger.info("config > found %s", filePath);

        glabConf = new GlobalConf();
        Map<String, Object> xmlMap = XmlUtils.parseMap(filePath);
        Optional.ofNullable(xmlMap).ifPresent(t -> {
            Optional.ofNullable(t.get("etcd.address")).ifPresent(o -> glabConf.setEtcdAdress(o.toString()));
            Optional.ofNullable(t.get("log.path")).ifPresent(o -> glabConf.setLogPath(o.toString()));
            Optional.ofNullable(t.get("rpc.register.type")).ifPresent(o -> glabConf.setRpcRegisterType(GlobalConf.RtType.ETCD));
            Optional.ofNullable(t.get("rpc.register.enabled")).ifPresent(o -> glabConf.setRpcRegisterEnabled(Boolean.parseBoolean(o.toString())));
            Optional.ofNullable(t.get("rpc.register.ip")).ifPresent(o -> glabConf.setRpcRegisterIp(o.toString()));
            Optional.ofNullable(t.get("rpc.discovery.enabled")).ifPresent(o -> glabConf.setRpcDiscoveryEnabled(Boolean.parseBoolean(o.toString())));
        });
    }

    private void loadCsumConf() {
        String fileName = "consumer-config.xml";
        String filePath = ConfigUtils.searchConf(fileName);
        if (filePath == null) {
            ConsoleLogger.info("config > not found %s", fileName);
            return;
        }

        ConsoleLogger.info("config > found %s", filePath);
        Map<String, Object> xmlMap = XmlUtils.parseMultiMap(filePath);
        Optional.ofNullable(xmlMap).ifPresent(t ->
                csumConfs = t.entrySet().stream().map(e -> {
                    Map<String, Object> serMap = (Map<String, Object>) e.getValue();
                    CsumConf csumConf = new CsumConf();
                    Optional.ofNullable(serMap.get("name")).ifPresent(o -> csumConf.setName(o.toString()));
                    Optional.ofNullable(serMap.get("address")).ifPresent(o -> csumConf.setAddress(o.toString()));
                    Optional.ofNullable(serMap.get("discovery")).ifPresent(o -> csumConf.setDiscovery(Boolean.parseBoolean(o.toString())));
                    Optional.ofNullable(serMap.get("desc")).ifPresent(o -> csumConf.setDesc(o.toString()));

                    Optional.ofNullable(serMap.get("option")).ifPresent(s -> {
                        Map<String, Object> opMap = (Map<String, Object>) s;
                        Optional.ofNullable(opMap.get("maxConnections")).ifPresent(o -> csumConf.getOption().setMaxConnections(Integer.parseInt(o.toString())));
                        Optional.ofNullable(opMap.get("maxRetry")).ifPresent(o -> csumConf.getOption().setMaxRetry(Integer.parseInt(o.toString())));
                    });
                    return csumConf;
                }).collect(Collectors.toMap(m -> ((CsumConf) m).getName(), m -> (CsumConf) m))
        );
    }

    public static AppConf instance() {
        return Holder.instance;
    }

    private static class Holder {
        private static AppConf instance;

        static {
            instance = new AppConf();
        }
    }
}