package lee.fund.task.data;

import lee.fund.pbf.a3.ProtoField;
import lee.fund.util.lang.EnumValueSupport;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by noname on 16/1/7.
 */
@Getter
@Setter
public class ExecuteParam {
    @ProtoField(order = 1, required = true, description = "执行类型, 0-自动, 1-手动")
    private ExecuteType type;

    @ProtoField(order = 2, required = false, description = "任务唯一标识")
    private String id;

    @ProtoField(order = 3, required = true, description = "任务名称")
    private String name;

    @ProtoField(order = 4, required = false, description = "任务别名")
    private String alias;

    @ProtoField(order = 5, required = false, description = "参数")
    private List<Arg> args;

    public enum ExecuteType implements EnumValueSupport {
        AUTO(0), MANUAL(1);

        private int value;

        ExecuteType(int value) {
            this.value = value;
        }

        @Override
        public int value() {
            return this.value;
        }
    }
}

