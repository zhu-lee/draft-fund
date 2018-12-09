package lee.fund.common.config;

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
    private String registerIp;

    public Configuration() {
        this.loadConfiguration();
    }

    private void loadConfiguration() {
        ServerConf serConf = AppConf.instance().getSerConf();
        GlobalConf glabConf = AppConf.instance().getGlabConf();

        requireNonNull(serConf.getName(),"server name is empty");
        this.setName(serConf.getName());
        requireNonNull(serConf.getPort(), "server port is empty");
        this.setPort(serConf.getPort());
        requireNonNull(glabConf.getRpcRegisterIp(), "register ip is empty");
        this.setRegisterIp(glabConf.getRpcRegisterIp());

        this.setRegistry(glabConf.isRpcRegisterEnabled());

        this.setDesc(Strings.isNullOrEmpty(serConf.getDesc())?serConf.getName():serConf.getDesc());
        this.setConnections(serConf.getOption().getConnections());
        this.setDebug(serConf.getOption().isDebug());
        this.setMonitorEnabled(serConf.getOption().isMonitorEnabled());
        this.setMonitorPort(serConf.getOption().getMonitorPort());
    }

    private void requireNonNull(Object va, String str) {
        Objects.requireNonNull(va, String.format("config > %s", str));
    }
}
