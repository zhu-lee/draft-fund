package lee.fund.util.remote;

import com.alibaba.fastjson.JSON;
import com.github.kevinsawicki.http.HttpRequest;
import lee.fund.util.execute.Schedule;
import lee.fund.util.jetcd.JetcdCall;
import lee.fund.util.jetcd.Provider;
import lee.fund.util.lang.UncheckedException;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.Line;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/9 16:15
 * Desc:
 */
public class RemoteCall {
    private static Logger logger = LoggerFactory.getLogger(RemoteCall.class);
    private static final String CFG_SVR_NAME = "config-service";
    private static RemoteCall instance;
    private static Object obj = new Object();
    private static List<String> cfgSvProvers;
    private static final int CONNECT_TIMEOUT = 5 * 1000;
    private static final int READ_TIMEOUT = 5 * 1000;

    public static RemoteCall getInstance() {
        if (instance == null) {
            synchronized (obj) {
                if (instance == null) {
                    instance = new RemoteCall();
                }
            }
        }
        return instance;
    }

    private String getCfgSvProvider() {
        if (cfgSvProvers == null) {
            synchronized (this) {
                if (cfgSvProvers == null) {
                    updateCfgSvProviders(lookUpProvider(CFG_SVR_NAME));
                    if (cfgSvProvers != null) {
                        JetcdCall.JETCD_CALL.watchKey(CFG_SVR_NAME,true,this::updateCfgSvProviders);
                    }
                }
            }
        }

        if (cfgSvProvers == null || cfgSvProvers.isEmpty()) {
            throw new UncheckedException(String.format("can't find providers for [%s]", CFG_SVR_NAME));
        }

        return cfgSvProvers.get(new Random().nextInt(cfgSvProvers.size()));
    }

    private void updateCfgSvProviders(List<Provider> providerList) {
        if (providerList != null || !providerList.isEmpty()) {
            cfgSvProvers = providerList.stream().map(t -> "http://" + t.getAddress()).collect(Collectors.toList());
        }
    }

    private List<Provider> lookUpProvider(String serverName) {
        List<Provider> providerList = null;
        try {
            providerList = JetcdCall.JETCD_CALL.lookup(serverName);
            logger.debug("find the providers for [{}]，result：{}", serverName, JSON.toJSONString(providerList));
        } catch (Exception e) {
            logger.error("find the providers for [{}]，error", serverName, e);
        }
        return providerList;
    }

    public Result getNsqNodes(String path, Map<String, String> args) {
        Result result = new Result();
        HttpRequest request;
        String url = this.getCfgSvProvider() + path;
        try {
            request = (args == null) ? HttpRequest.get(url) : HttpRequest.get(url, args, true);
            request.connectTimeout(CONNECT_TIMEOUT).readTimeout(READ_TIMEOUT).useCaches(false);
            if (!request.ok()) {
                logger.error("request url=[{}],args={}, failed", url, JSON.toJSONString(args));
            }
            result.success = request.ok();
            result.value = request.body();
        } catch (Exception e) {
            result.info = String.format("request url=[%s],args=%s, error", url, JSON.toJSONString(args));
            logger.error(result.info, e);
        }
        return result;
    }

    @Getter
    public static class Result {
        private boolean success;
        private String info;
        private String value;
    }
}