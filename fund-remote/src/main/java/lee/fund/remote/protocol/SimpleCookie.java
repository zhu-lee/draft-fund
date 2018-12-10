package lee.fund.remote.protocol;

import lombok.Getter;
import lombok.Setter;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/5 9:39
 * Desc:
 */
@Setter
@Getter
public class SimpleCookie {
    private String name;
    private String value;
    private long expireTime;
}
