package lee.fund.util.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

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
    private boolean rpcRegisterEnabled;
    private String rpcRegisterIp = StringUtils.EMPTY;
    private boolean rpcDiscoveryEnabled;//TODO unused

    enum RtType {
        ETCD;
    }
}