package lee.fund.util.config;

import lee.fund.util.log.ConsoleLogger;
import lee.fund.util.xml.XmlUtils;

import java.util.List;
import java.util.Map;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/6 15:16
 * Desc:
 */
public class AppConf {
    private ServerConf serverConf;
    private GlobalConf globalConf;

    public static AppConf getInstance(){
        return Holder.instance;
    }

    private static class Holder{
        private static AppConf instance;
        static {
            String fileName = "app-config.xml";
            String filePath = ConfigUtils.searchConfig(fileName);
            if (filePath == null) {
                ConsoleLogger.info("config > not found {}",fileName);
                System.exit(1);
            }

            ConsoleLogger.info("config > found {}",filePath);
            List<Map<String, String>> xmlMap = XmlUtils.parseXml2MapList(filePath);


//            String filePath = ConfigManager.findConfigPath("app", ".conf", ".xml");
//            if (filePath == null) {
//                ConsoleLogger.info("config > Warning: app.conf/app.xml can't be found, will use default settings");
//                defaultInstance = new AppConfig();
//            } else {
//                defaultInstance = new AppConfig(filePath);
//            }
        }
    }
}
