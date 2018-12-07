package lee.fund.util.config;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.util.Strings;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/6 15:47
 * Desc:
 */
@Setter(AccessLevel.PROTECTED)
@Getter
public class ServerConf {
    private String name = Strings.EMPTY;
    private Integer port;
    private String desc = Strings.EMPTY;
    private Option option;
    private Map<String, Object> customs;

    public ServerConf() {
        this.option = new Option();
        this.customs = new HashMap<>();
    }

    @Setter(AccessLevel.PROTECTED)
    @Getter
    public static class Option {
        private int connections;
        private boolean debug;
        private boolean monitorEnabled;
        private int monitorPort;
    }
}
