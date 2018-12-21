package lee.fund.util.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/11 14:33
 * Desc:
 */
@Setter(AccessLevel.PROTECTED)
@Getter
public class ConSumerConf {
    private String name = StringUtils.EMPTY;
    private String address = StringUtils.EMPTY;
    private boolean discovery;
    private String desc = StringUtils.EMPTY;
    private Option option;

    public ConSumerConf() {
        this.option = new Option();
    }

    @Setter(AccessLevel.PROTECTED)
    @Getter
    public class Option {
        private int maxConnections;
        private int maxRetry;
    }
}
