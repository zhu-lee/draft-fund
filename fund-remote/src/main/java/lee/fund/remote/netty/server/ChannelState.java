package lee.fund.remote.netty.server;

import io.netty.util.AttributeKey;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/5 12:45
 * Desc:
 */
@NoArgsConstructor
public class ChannelState {
    public static final AttributeKey<ChannelState> KEY = AttributeKey.newInstance("ChannelState");
    @Setter @Getter private final LocalDateTime createTime = LocalDateTime.now();
    @Setter @Getter private LocalDateTime activeTime = LocalDateTime.now();
    @Setter @Getter private String id = "";
    @Setter @Getter private String service;
    @Setter @Getter private String method;
}
