package lee.fund.pbf.test;

import com.coreos.jetcd.api.User;
import lee.fund.pbf.test.lib.FieldType;
import lee.fund.pbf.test.lib.ProtoField;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.util.Date;
import java.util.List;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/20 18:44
 * Desc:
 */
@Setter
@Getter
public class ProtobufBean {
    @ProtoField(order = 1, type = FieldType.INT32, required = true)
    private int id1;
    @ProtoField(order = 2, type = FieldType.INT32)
    private Integer id2;
    @ProtoField(order = 3, type = FieldType.INT64, required = true)
    private long id3;
    @ProtoField(order = 4, type = FieldType.INT64, required = true)
    private Long id4;
    @ProtoField(order = 5, type = FieldType.INT64, required = true)
    private Date f5;
    @ProtoField(order = 6, type = FieldType.ENUM)
    private BeanStatus f6;
    @ProtoField(order = 7, type = FieldType.OBJECT)
    private User f7;
    @ProtoField(order = 8, type = FieldType.INT32)
    private List<Integer> f8;
    @ProtoField(order = 9, type = FieldType.OBJECT)
    private List<User> f9;
    @ProtoField(order = 10, type = FieldType.INT64)
    private List<Long> f10;
    @ProtoField(order = 11, type = FieldType.ENUM)
    private DayOfWeek f11;

    public enum BeanStatus {
        VALID(1), INVALID(2);

        private int value;

        BeanStatus(int value) {
            this.value = value;
        }

        public int value() {
            return this.value;
        }
    }
}
