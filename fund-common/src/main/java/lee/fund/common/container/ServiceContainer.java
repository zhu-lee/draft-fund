package lee.fund.common.container;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/30 17:44
 * Desc:
 */
public class ServiceContainer {
    private final Logger logger = LoggerFactory.getLogger(ServiceContainer.class);
    private Map<String, ServiceInfo> containerMap = new HashMap<>();
    private Map<String, MethodExecutor> executorMap = new HashMap<>();




}
