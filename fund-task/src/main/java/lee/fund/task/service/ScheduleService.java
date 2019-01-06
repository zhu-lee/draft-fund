package lee.fund.task.service;

import lee.fund.remote.annotation.RpcMethod;
import lee.fund.remote.annotation.RpcService;
import lee.fund.task.data.NotifyRequest;
import lee.fund.task.data.Result;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/5 13:16
 * Desc:
 */
@RpcService(description = "任务调度服务")
public interface ScheduleService {
    @RpcMethod(description = "通知任务执行结查")
    Result notify(NotifyRequest param);
}
