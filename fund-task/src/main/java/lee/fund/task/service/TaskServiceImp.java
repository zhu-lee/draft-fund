package lee.fund.task.service;

import com.alibaba.fastjson.JSON;
import lee.fund.remote.app.client.RemoteClient;
import lee.fund.task.TaskContext;
import lee.fund.task.TaskExecutor;
import lee.fund.task.data.ExecuteParam;
import lee.fund.task.data.ExecuteType;
import lee.fund.task.data.NotifyRequest;
import lee.fund.task.data.Result;
import lee.fund.util.lang.StrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/5 11:27
 * Desc:
 */
public class TaskServiceImp implements TaskService {
    private Logger logger = LoggerFactory.getLogger(TaskServiceImp.class);
    private Map<String, TaskExecutor> executors;
    private ExecutorService pool = Executors.newCachedThreadPool();
    private ScheduleService notifyService = RemoteClient.get("skynet", ScheduleService.class);
    private ConcurrentMap<String, String> runingTaskMap = new ConcurrentHashMap<>();

    public TaskServiceImp(Map<String, TaskExecutor> executors) {
        this.executors = executors;
    }

    @Override
    public Result execute(ExecuteParam param) {
        logger.info("接收任务：{}", JSON.toJSONString(param));

        if (param.getExecuteType() == ExecuteType.AUTO
                && runingTaskMap.containsKey(param.getName())) {
            pool.execute(() -> {
                String errorInfo = String.format("任务%s正在执行，跳过此次调度（如果多次发生此类型情况，请检查调度时间是否合理");
                logger.warn(errorInfo);
                Date start = new Date();
                this.notify(param.getName(), param.getId(), new Result(true, null), start, new Date());
            });
            return new Result(true, null);
        }

        String name = StrUtils.isBlank(param.getAlias()) ? param.getName() : param.getAlias();
        TaskExecutor taskExecutor = executors.get(name);
        if (taskExecutor == null) {
            String errorInfo = String.format("找不到任务：%s", name);
            logger.error(errorInfo);
            return new Result(false, errorInfo);
        }

        try {
            if (param.getExecuteType() == ExecuteType.AUTO) {
                runingTaskMap.put(param.getName(), name);
            }
            pool.execute(() -> this.doExecute(taskExecutor, param));
        } catch (Exception e) {
            logger.error("线程池执行任务异常", e);
            return new Result(false, "线程池执行任务异常：" + e.getMessage());
        }
        return new Result(true, null);
    }

    private void doExecute(TaskExecutor executor, ExecuteParam param) {
        Date start = new Date();
        try {
            logger.info("开始执行任务：{}，执行方式：{}", param.getName(), param.getExecuteType());
            TaskContext context = new TaskContext(param);
            executor.execute(context);
            this.notify(param.getName(), param.getId(), new Result(true, null), start, new Date());
            logger.info("任务：{}执行成功，耗时：{} ms", param.getName(), Duration.ofMillis(new Date().getTime() - start.getTime()));
        } catch (Exception e) {
            String errorInfo = StrUtils.isBlank(e.getMessage()) ? e.toString() : e.getMessage();
            this.notify(param.getName(), param.getId(), new Result(false, errorInfo), start, new Date());
            logger.info("任务：{}执行异常，耗时：{} ms,错误信息：{}", param.getName(), Duration.ofMillis(new Date().getTime() - start.getTime()), errorInfo);
        } finally {
            if (param.getExecuteType() == ExecuteType.AUTO) {
                runingTaskMap.remove(param.getName());
            }
        }
    }

    private void notify(String name, String id, Result result, Date start, Date end) {
        try {
            NotifyRequest param = new NotifyRequest();
            param.setId(id);
            param.setName(name);
            param.setResult(result);
            param.setStartTime(start);
            param.setEndTime(end);

            Result returnResult = notifyService.notify(param);
            if (!returnResult.Success) {
                logger.error("通知任务状态错误，id：{}，name：{}，error：{}", id, name, returnResult.ErrorInfo);
            }
        } catch (Exception e) {
            logger.error("通知任务状态异常，id：{}，name：{}，error：{}", id, name, e);
        }
    }


}
