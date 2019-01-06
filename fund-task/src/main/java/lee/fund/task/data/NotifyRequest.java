package lee.fund.task.data;

import lee.fund.pbf.a3.ProtoField;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/5 17:09
 * Desc:
 */
@Setter
@Getter
public class NotifyRequest {
    @ProtoField(order = 1, required = true, description = "任务唯一标识")
    private String id;

    @ProtoField(order = 2, required = true, description = "任务名称")
    private String name;

    @ProtoField(order = 3, required = true, description = "执行结果")
    private Result result;

    @ProtoField(order = 4, required = true, description = "执行开始时间")
    private Date startTime;

    @ProtoField(order = 5, required = true, description = "执行结束时间")
    private Date endTime;
}
