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
    private boolean isRpcRegisterEnabled;
    private int connections;
    private String desc;
    private boolean debug;
    private boolean monitorEnabled;
    private int monitorPort;
    private String registerIp;

    public ServerConfiguration() {
        ServerConf serverConf = AppConf.INSTANCE.getServerConf();
        GlobalConf globalConf = AppConf.INSTANCE.getGlobalConf();

        requireNonNull(serverConf.getName(), "server name is empty");
        this.name = serverConf.getName();
        requireNonNull(serverConf.getPort(), "server port is empty");
        this.port = serverConf.getPort();
        requireNonNull(globalConf.getRpcRegisterIp(), "register ip is empty");
        this.registerIp = globalConf.getRpcRegisterIp();

        this.isRpcRegisterEnabled = globalConf.isRpcRegisterEnabled();

        this.desc = Strings.isNullOrEmpty(serverConf.getDesc()) ? serverConf.getName() : serverConf.getDesc();
        if (serverConf.getOption().getConnections() > 0) {
            this.connections = serverConf.getOption().getConnections();
        }
        this.debug = serverConf.getOption().isDebug();
        this.monitorEnabled = serverConf.getOption().isMonitorEnabled();
        this.monitorPort = serverConf.getOption().getMonitorPort();
    }

    private void requireNonNull(Object va, String str) {
        Objects.requireNonNull(va, String.format("config > %s", str));
    }
}
