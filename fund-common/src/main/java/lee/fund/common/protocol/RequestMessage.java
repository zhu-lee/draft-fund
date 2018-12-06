package lee.fund.common.protocol;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/5 9:32
 * Desc:
 */
@Setter
@Getter
public class RequestMessage {
    private String clientName;
    private String userToken;
    private String serviceName;
    private String methodName;
    private List<SimpleValue> parameters;
    private List<SimpleCookie> cookies;
    private String contextID;//上下文 ID, 在整个请求链中保持不变
    private String messageID;//消息 ID, 每个请求唯一
    private String serverName;//Server名
}
