package lee.fund.util.config;

import com.google.common.base.Strings;

import java.util.Properties;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/29 22:17
 * Desc:   app dev qa stg prd; get profile from env; e.g:app-xx-xx.profile
 */
public class ConfProperties {
    public static final ConfProperties INSTANCE = new ConfProperties();
    private String ACTIVE_PROFILE_ENV = "FUND-PROFILE-ACTIVE";
    private Properties prop = new Properties();

    private ConfProperties() {
        init();
    }
    private void init() {
        prop.put(ACTIVE_PROFILE_ENV, this.getActiveProfileEnv(ACTIVE_PROFILE_ENV));
    }

    private String getActiveProfileEnv(String p) {
        return Strings.nullToEmpty(System.getenv(p));
    }

    public String getActiveProfile() {
        return prop.get(ACTIVE_PROFILE_ENV).toString();
    }
}
