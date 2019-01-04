package lee.fund.task;

import lee.fund.remote.app.server.RemoteApplication;
import lee.fund.remote.app.server.ServerConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.Executor;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/25 20:33
 * Desc:
 */

/**
 * 1、创建application
 * 2、创建server
 * 3、扫描task的暴露方法
 * 4、启动task服务
 * 5、注册task->provider in etcd
 */
public class TaskApplication extends RemoteApplication{
    protected Logger logger;
    protected TaskServer server;

    public TaskApplication(Class<?> bootStrap, String[] args, ServerConfiguration serverConfiguration) {
        super(bootStrap, args, serverConfiguration);
        this.logger = LoggerFactory.getLogger(bootStrap);
        this.server = new TaskServer(serverConfiguration);
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


        server.registerService(TaskService.class, new TaskServiceImp(executors));
    }
}
