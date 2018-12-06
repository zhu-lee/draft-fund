package lee.fund.common.protocol;

import lombok.Getter;
import lombok.Setter;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/5 9:38
 * Desc:
 */
@Setter
@Getter
public class SimpleValue {
    private int dataType;
    private byte[] data;

    public SimpleValue() {
        // for decode
    }

    public SimpleValue(int dataType, byte[] data) {
        this.dataType = dataType;
        this.data = data;
    }
}
