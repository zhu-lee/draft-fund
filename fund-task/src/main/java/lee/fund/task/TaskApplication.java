package lee.fund.task;

import lee.fund.remote.app.server.RemoteApplication;
import lee.fund.remote.app.server.RemoteServer;
import lee.fund.remote.app.server.ServerConfiguration;
import lee.fund.task.service.TaskService;
import lee.fund.task.service.TaskServiceImp;
import lee.fund.util.lang.StrUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/25 20:33
 * Desc:
 */
public class TaskApplication extends RemoteApplication {
    private static final Logger logger = LoggerFactory.getLogger(RemoteServer.class);
    protected TaskServer server;

    public TaskApplication(Class<?> bootStrap, ServerConfiguration configuration, String[] args) {
        super(bootStrap, args, configuration);
        this.server = new TaskServer(configuration);
    }

    @Override
    protected void beforeSetProperties(Map<String, Object> properties) {
        properties.put("spring.main.web_environment", false);
    }

    @Override
    protected void load() {
        this.scanTasks();
        this.server.start();
    }

    private void scanTasks() {
        Map<String, TaskExecutor> executors = this.applicationContext.getBeansOfType(TaskExecutor.class);
        logger.info("find {} task", executors.size());
        Map<String, TaskExecutor> executorMap = new HashMap<>();
        executors.forEach((t,executor)->{
            Class<?> cls = executor.getClass();
            Task task = cls.getAnnotation(Task.class);
            String name = ((task == null || StrUtils.isBlank(task.name())) ? cls.getSimpleName() : task.name());
            executorMap.put(name, executor);
        });
        server.exposeService(TaskService.class, new TaskServiceImp(executors));
    }
}
