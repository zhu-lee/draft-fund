package lee.fund.util.log;

import lee.fund.util.config.ConfigUtils;
import lee.fund.util.lang.Exceptions;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/3/6 17:04
 * Desc:   首先取项目etc下的配置，其次取global-config.xml同目录下的日志文件
 */
public class LoggerManager {
    static {
        String fileName = "log4j2.xml";
        String logConfigPath = ConfigUtils.searchConf(fileName);
        if (logConfigPath == null) {
            ConsoleLogger.info("config > %s can't be found in [%s]，continue to search in disk.", fileName, ConfigUtils.getEtcFolder());
            logConfigPath = ConfigUtils.searchGlobalConf(fileName);
            if (logConfigPath == null) {
                ConsoleLogger.info("config > %s can't be found in [%s], all logs will output to the console.", fileName, ConfigUtils.getGlobalConfigDir());
            }
        }
        if (logConfigPath != null) {
            ConsoleLogger.info("config > %s found [%s]", fileName, logConfigPath);
            try (FileInputStream f = new FileInputStream(logConfigPath)) {
                ConfigurationSource source = new ConfigurationSource(f);
                Configurator.initialize(null, source);
                System.out.println("111");
            } catch (Exception ex) {
                ConsoleLogger.warn("config > failed to load [%s] at %s: %s-%s", fileName, logConfigPath, ex.getMessage(), Exceptions.getStackTrace(ex));
            }
        }
    }

    public static Logger getLogger(Class clazz) {
        return LoggerFactory.getLogger(clazz);
    }

    public static Logger getLogger(String name) {
        return LoggerFactory.getLogger(name);
    }
}
