package lee.fund.task;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/4 21:17
 * Desc:
 */
public interface TaskExecutor {
    void execute(TaskContext context);
}
