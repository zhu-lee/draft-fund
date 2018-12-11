package lee.fund.remote.app.client;

import com.google.common.base.Strings;
import lee.fund.remote.registry.Provider;
import lee.fund.util.config.CsumConf;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/11 14:10
 * Desc:
 */
@Setter(AccessLevel.PROTECTED)
@Getter
public class ClientConfiguration {
    private String name;
    private boolean discovery = true;
    private String address;
    private String desc;
    private int maxConnections;
    private int maxRetry = 2;

    public ClientConfiguration(CsumConf csumConf) {
        requireNonNull(csumConf.getName(), "name");
        this.setName(csumConf.getName());
        requireNonNull(csumConf.getAddress(), "address");
        this.setAddress(csumConf.getAddress());

        this.setDiscovery(csumConf.isDiscovery());
        this.setDesc(Strings.isNullOrEmpty(csumConf.getDesc())?csumConf.getName():csumConf.getDesc());
        if (csumConf.getOption().getMaxConnections() > 0) {
            this.setMaxRetry(csumConf.getOption().getMaxConnections());
        }
        if (csumConf.getOption().getMaxRetry() > 0) {
            this.setMaxRetry(csumConf.getOption().getMaxRetry());
        }
    }

    public ClientConfiguration(Provider provider) {

    }

    private void requireNonNull(Object va, String str) {
        Objects.requireNonNull(va, String.format("not found %s in consumer config", str));
    }
}
