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
        this.name = csumConf.getName();
        requireNonNull(csumConf.getAddress(), "address");
        this.address = csumConf.getAddress();
        if (!this.address.contains(":")) {
            throw new IllegalArgumentException("invalid net address in consumer config file: " + this.address);
        }

        this.discovery = csumConf.isDiscovery();
        this.desc = Strings.isNullOrEmpty(csumConf.getDesc()) ? csumConf.getName() : csumConf.getDesc();
        if (csumConf.getOption().getMaxConnections() > 0) {
            this.maxConnections = csumConf.getOption().getMaxConnections();
        }
        if (csumConf.getOption().getMaxRetry() > 0) {
            this.maxRetry = csumConf.getOption().getMaxRetry();
        }
    }

    public ClientConfiguration(Provider provider) {
        this.name = provider.getName();
        this.address = provider.getAddress();
        this.desc = provider.getDesc();
    }

    public ClientConfiguration(ClientConfiguration clientConf, Provider provider) {
        this(provider);
        this.maxConnections = clientConf.getMaxConnections();
        this.maxRetry = clientConf.getMaxRetry();
    }

    private void requireNonNull(Object va, String str) {
        Objects.requireNonNull(va, String.format("not found %s in consumer config", str));
    }
}
