package lee.fund.remote.app.server;

import com.google.common.base.Strings;
import lee.fund.util.config.AppConf;
import lee.fund.util.config.GlobalConf;
import lee.fund.util.config.ServerConf;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/25 20:21
 * Desc:
 */
@Setter(AccessLevel.PROTECTED)
@Getter
public class ServerConfiguration {
    private final Logger logger = LoggerFactory.getLogger(ServerConfiguration.class);
    private String name;
    private int port;
    private boolean registry;
    private int connections;
    private String desc;
    private boolean debug;
    private boolean monitorEnabled;
    private int monitorPort;
    private String registerIp;

    public ServerConfiguration() {
        ServerConf serConf = AppConf.instance().getServerConf();
        GlobalConf glabConf = AppConf.instance().getGlobalConf();

        requireNonNull(serConf.getName(),"server name is empty");
        this.name = serConf.getName();
        requireNonNull(serConf.getPort(), "server port is empty");
        this.port = serConf.getPort();
        requireNonNull(glabConf.getRpcRegisterIp(), "register ip is empty");
        this.registerIp = glabConf.getRpcRegisterIp();

        this.registry = glabConf.isRpcRegisterEnabled();

        this.desc = Strings.isNullOrEmpty(serConf.getDesc()) ? serConf.getName() : serConf.getDesc();
        if (serConf.getOption().getConnections() > 0) {
            this.connections = serConf.getOption().getConnections();
        }
        this.debug = serConf.getOption().isDebug();
        this.monitorEnabled = serConf.getOption().isMonitorEnabled();
        this.monitorPort = serConf.getOption().getMonitorPort();
    }

    private void requireNonNull(Object va, String str) {
        Objects.requireNonNull(va, String.format("config > %s", str));
    }
}
