package lee.fund.task.data;

import lee.fund.pbf.a3.ProtoField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/5 12:59
 * Desc:
 */
@Setter
@Getter
public class ExecuteParam {
    @ProtoField(order = 1, required = true, description = "执行类型")
    private ExecuteType executeType;

    @ProtoField(order = 2, description = "任务唯一标识")
    private String id;

    @ProtoField(order = 3, required = true, description = "任务名称")
    private String name;

    @ProtoField(order = 4, description = "任务别名")
    private String alias;

    @ProtoField(order = 5, description = "任务参数")
    private List<Arg> args;
}
