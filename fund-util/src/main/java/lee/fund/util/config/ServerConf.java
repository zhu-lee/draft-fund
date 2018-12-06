package lee.fund.util.config;


import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/6 15:47
 * Desc:
 */
@Setter
@Getter
public class ServerConf {
    private String name;
    private int port;
    private String desc;
    private Option option;
    private Map<String, Object> customs;

    @Setter
    @Getter
    private static class Option {
        private int connections;
        private boolean debug;
        private boolean monitorEnabled;
        private int monitorPort;
    }
}
