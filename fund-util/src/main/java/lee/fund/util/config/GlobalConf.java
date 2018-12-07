package lee.fund.util.config;

import lombok.Getter;
import lombok.Setter;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/6 15:48
 * Desc:
 */
@Setter
@Getter
public class GlobalConf {
    private String etcdAdress;
    private String logPath;
    private RtType rpcRegisterType;
    private boolean rpcRegisterEnabled;
    private String rpcRegisterIp;
    private boolean rpcDiscoveryEnabled;
    enum RtType{
        ETCD;
    }
}