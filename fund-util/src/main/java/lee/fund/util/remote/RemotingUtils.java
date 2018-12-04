package lee.fund.util.remote;

import org.apache.logging.log4j.util.Strings;

import java.lang.management.ManagementFactory;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/23 15:07
 * Desc:
 */
public class RemotingUtils {
    private static Lock lock = new ReentrantLock();
    private static final String OS_NAME = System.getProperty("os.name");
    private final static Object obj = new Object();
    private static String pid;

    public static boolean isLinuxOS(){
        return OS_NAME != null && OS_NAME.contains("linux");
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
                    pid = Strings.EMPTY;
                }
            }
        }
        return pid;
    }
}
