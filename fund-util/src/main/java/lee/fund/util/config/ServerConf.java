package lee.fund.util.config;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

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
    private String name = StringUtils.EMPTY;
    private Integer port;
    private String desc = StringUtils.EMPTY;
    private Option option;
    private Map<String, Object> customs;

    public ServerConf() {
        this.option = new Option();
        this.customs = new HashMap<>();
    }

    @Setter(AccessLevel.PROTECTED)
    @Getter
    public class Option {
        private int connections;
        private boolean debug;
        private boolean monitorEnabled;
        private int monitorPort;
    }
}
