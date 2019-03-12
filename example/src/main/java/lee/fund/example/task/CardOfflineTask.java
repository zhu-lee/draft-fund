package lee.fund.example.task;

import lee.fund.task.Task;
import lee.fund.task.TaskContext;
import lee.fund.task.TaskExecutor;
import org.springframework.stereotype.Component;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/3/9 23:39
 * Desc:
 */
@Component
@Task
public class CardOfflineTask implements TaskExecutor {
    @Override
    public void execute(TaskContext context) {

    }
}
