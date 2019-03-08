package lee.fund.util.config;

import lee.fund.util.log.ConsoleLogger;
import lee.fund.util.xml.XmlUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Optional;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/6 15:48
 * Desc:
 */
@Setter(AccessLevel.PROTECTED)
@Getter
public class GlobalConf {
    private String etcdAdress = StringUtils.EMPTY;
    private String logPath = StringUtils.EMPTY;//TODO unused
    private RtType rpcRegisterType;//TODO unused
    private boolean rpcRegisterEnabled;//TODO unused
    private String rpcRegisterIp = StringUtils.EMPTY;
    private boolean rpcDiscoveryEnabled;//TODO unused
    enum RtType {
        ETCD;
    }

    private GlobalConf(){
        this.init();
    }

    public static GlobalConf instance() {
        return Handler.instance;
    }

    private void init() {
        String fileName = "global-config.xml";
        String filePath = ConfigUtils.searchGlobalConf(fileName);
        if (filePath == null) {
            ConsoleLogger.info("config > not found %s", fileName);
            System.exit(1);
        }
        ConsoleLogger.info("config > found %s", filePath);

        Map<String, Object> xmlMap = XmlUtils.parseMap(filePath);
        Optional.ofNullable(xmlMap).ifPresent(t -> {
            Optional.ofNullable(t.get("etcd.address")).ifPresent(o -> this.setEtcdAdress(o.toString()));
            Optional.ofNullable(t.get("log.path")).ifPresent(o -> this.setLogPath(o.toString()));
            Optional.ofNullable(t.get("rpc.register.type")).ifPresent(o -> this.setRpcRegisterType(GlobalConf.RtType.ETCD));
            Optional.ofNullable(t.get("rpc.register.enabled")).ifPresent(o -> this.setRpcRegisterEnabled(Boolean.parseBoolean(o.toString())));
            Optional.ofNullable(t.get("rpc.register.ip")).ifPresent(o -> this.setRpcRegisterIp(o.toString()));
            Optional.ofNullable(t.get("rpc.discovery.enabled")).ifPresent(o -> this.setRpcDiscoveryEnabled(Boolean.parseBoolean(o.toString())));
        });
    }

    private static class Handler {
        private static GlobalConf instance;
        static {
            instance = new GlobalConf();
        }
    }
}