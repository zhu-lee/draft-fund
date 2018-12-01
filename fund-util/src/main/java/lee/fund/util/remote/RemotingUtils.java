package lee.fund.util.remote;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/23 15:07
 * Desc:
 */
public class RemotingUtils {
    public static final String OS_NAME = System.getProperty("os.name");

    public static boolean isLinuxOS(){
        return OS_NAME != null && OS_NAME.contains("linux");
    }
}
