package lee.fund.remote.protocol;

import lee.fund.pbf.a3.ProtoField;
import lombok.Getter;
import lombok.Setter;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/30 20:13
 * Desc:
 */
@Setter
@Getter
public class RemoteValue {
    @ProtoField(order = 1, required = true)
    private int dataType;

    @ProtoField(order = 2, required = true)
    private byte[] data;

    public RemoteValue(int dataType, byte[] data) {
        this.dataType = dataType;
        this.data = data;
    }
}
