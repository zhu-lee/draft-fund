package lee.fund.util.sys;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;

import java.lang.management.ManagementFactory;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/7 9:07
 * Desc:
 */
public class SysUtils {
    public static final String OS_NAME = System.getProperty("os.name");
    private final static Object obj = new Object();
    private static String pid;

    public static boolean isLinuxOS(){
        return OS_NAME != null && OS_NAME.toLowerCase().contains("linux");
    }

    public static boolean isWindowOS(){
        return OS_NAME != null && OS_NAME.toLowerCase().contains("windows");
    }

    public static boolean isMacOS(){
        return OS_NAME != null && OS_NAME.toLowerCase().contains("mac os");
    }

    public static String getPid() {
        if (pid == null) {
            synchronized (obj) {
                String jvmName = ManagementFactory.getRuntimeMXBean().getName();
                if (Strings.isNotBlank(jvmName)) {
                    int indexOf = jvmName.indexOf('@');
                    if (indexOf > 0) {
                        pid = jvmName.substring(0, indexOf);
                    }
                }
                if (pid == null) {
                    pid = StringUtils.EMPTY;
                }
            }
        }
        return pid;
    }
}
