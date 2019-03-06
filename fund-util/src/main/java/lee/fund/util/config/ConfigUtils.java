package lee.fund.util.config;

import com.google.common.base.Strings;
import lee.fund.util.log.ConsoleLogger;
import lee.fund.util.sys.SysUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/27 9:32
 * Desc:
 */
public class ConfigUtils {
    private static String etcFolder;
    private static String globalFolder;

    private ConfigUtils() {
    }

    public static String searchConf(String fileName) {
        String filePath = getEtcFolder() + fileName;
        if (new File(filePath).exists()) {
            return filePath;
        }
        return null;
    }

    public static String searchGlobalConf(String fileName) {
        String filePath = getGlobalConfigDir() + fileName;
        if (new File(filePath).exists()) {
            return filePath;
        }
        return null;
    }

    public static String getEtcFolder() {
        if (Strings.isNullOrEmpty(etcFolder)) {
            URL etcUrl = Thread.currentThread().getContextClassLoader().getResource("etc");
            if (etcUrl == null) {
                ConsoleLogger.info("config > there are no configuration files under the etc folder");
                return null;
            }
            try {
                etcFolder = URLDecoder.decode(etcUrl.getPath(), "UTF-8") + "/";
                ConsoleLogger.info("config > etc folder found: %s", etcFolder);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return etcFolder;
    }

    public static String getGlobalConfigDir() {
        if (globalFolder == null) {
            if (SysUtils.isLinuxOS()) {
                globalFolder = "/home/fund/etc/";
            } else if (SysUtils.isWindowOS()) {
                globalFolder = "D:\\etc\\";
            } else if (SysUtils.isMacOS()) {
                globalFolder = "/etc/fund/";
            } else {
                throw new IllegalStateException("unsupported os: " + SysUtils.OS_NAME);
            }
        }
        return globalFolder;
    }

    public static String getConfPath(String filename) {
        return getEtcFolder() + filename;
    }

    /**
     * 设置配置目录  etc  所在路径
     *
     * @param configPath
     */
    public static void setConfigDir(String configPath) {
        if (configPath == null || configPath.trim().length() == 0) {
            throw new IllegalArgumentException("arg configPath is null or empty");
        }
        ConsoleLogger.info("config > change config dir to " + configPath);
        etcFolder = configPath;
    }
}