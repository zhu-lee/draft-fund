package lee.fund.mq.nsq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.brainlag.nsq.ServerAddress;
import com.github.brainlag.nsq.lookup.NSQLookup;
import com.github.kevinsawicki.http.HttpRequest;
import com.google.common.base.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Set;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/7 18:52
 * Desc:
 */
public class FundNsqLookup implements NSQLookup {
    private static final Logger logger = LoggerFactory.getLogger(FundNsqLookup.class);
    private static final int CONNECT_TIMEOUT = 5 * 1000;
    private static final int READ_TIMEOUT = 5 * 1000;
    private Set<String> loopUpAddr = new HashSet<>();

    @Override
    public void addLookupAddress(String address, int port) {
        StringBuilder sbu = new StringBuilder();
        if (!address.startsWith("http")) {
            sbu.append("http://").append(address).append(":").append(port);
        } else {
            sbu.append(address).append(":").append(port);
        }
        loopUpAddr.add(sbu.toString());
    }

    @Override
    public Set<ServerAddress> lookup(String topic) {
        Set<ServerAddress> set = new HashSet<>();
        loopUpAddr.forEach(addr -> {
            HttpRequest httpRequest = null;
            try {
                String topicEncode = URLEncoder.encode(topic, Charsets.UTF_8.name());
                httpRequest = HttpRequest.get(addr + "/lookup", true, "topic", topicEncode)
                        .connectTimeout(CONNECT_TIMEOUT).readTimeout(READ_TIMEOUT).useCaches(false);

                if (httpRequest.ok()) {
                    JSONObject jsonObject = JSON.parseObject(httpRequest.body());
                    JSONArray jsonArray = jsonObject.getJSONArray("producers");
                    for (int i = 0; i < jsonArray.size(); i++) {
                        JSONObject jsn = jsonArray.getJSONObject(i);
                        String host = jsn.getString("broadcast_address");
                        int port = jsn.getIntValue("tcp_port");
                        ServerAddress address = new ServerAddress(host, port);
                        set.add(address);
                    }
                }

            } catch (Exception e) {
                logger.error("nsq > lookup failed，server：{}，topic：{}，error：{}", addr, topic, e.getMessage());
            } finally {
                if (httpRequest != null) {
                    httpRequest.disconnect();
                }
            }
        });
        return set;
    }
}
