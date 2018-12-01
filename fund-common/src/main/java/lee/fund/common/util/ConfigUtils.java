package lee.fund.common.util;

import com.google.common.base.Strings;
import lee.fund.util.log.ConsoleLogger;

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

    private ConfigUtils() {}

    public static String searchConfig(String fileName){
        String filePath = getEtcFolder() + fileName;
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
                ConsoleLogger.info("config > etc folder found: %s",etcFolder);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return etcFolder;
    }
}