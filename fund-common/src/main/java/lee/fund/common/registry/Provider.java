package lee.fund.common.registry;

import lombok.Getter;
import lombok.Setter;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/9 17:52
 * Desc:
 */
@Setter
@Getter
public class Provider {
    private String name;
    private String type;
    private String address;
    private String version;
    private String desc;
    private int clients;
    private int flag;
}
