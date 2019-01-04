package lee.fund.task;

import lee.fund.task.data.Arg;
import lee.fund.task.data.ExecuteParam;
import lee.fund.util.config.SettingMap;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/4 20:41
 * Desc:
 */
public class TaskContext {
    private String name;
    private String id;
    private String alias;
    private SettingMap settingMap=new SettingMap();

    public TaskContext(ExecuteParam param) {
        this.name = param.getName();
        this.id = param.getId();
        this.alias = param.getAlias();
        List<Arg> list = param.getArgs();
        if (list != null) {
            list.forEach(t->settingMap.put(t.Name,t.Value));
        }
    }
}
