package lee.fund.task.service;

import lee.fund.remote.annotation.RpcMethod;
import lee.fund.remote.annotation.RpcParameter;
import lee.fund.remote.annotation.RpcService;
import lee.fund.task.data.ExecuteParam;
import lee.fund.task.data.Result;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/5 11:27
 * Desc:
 */
@RpcService(description = "任务服务")
public interface TaskService {
    @RpcMethod(name = "Execute", description = "执行任务")
    @RpcParameter(description = "任务执行结果")
    Result execute(@RpcParameter(description = "任务参数") ExecuteParam param);
}
