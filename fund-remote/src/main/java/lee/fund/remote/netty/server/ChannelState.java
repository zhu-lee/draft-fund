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
@Setter
@Getter
public class ChannelState {
    public static final AttributeKey<ChannelState> KEY = AttributeKey.newInstance("ChannelState");
    private final LocalDateTime createTime = LocalDateTime.now();
    private LocalDateTime activeTime = LocalDateTime.now();
    private String id = "";
    private String service;
    private String method;
}
