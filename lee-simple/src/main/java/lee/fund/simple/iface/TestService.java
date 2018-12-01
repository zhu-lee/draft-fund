package lee.fund.simple.iface;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/30 10:56
 * Desc:
 */
public interface TestService {
    List<String> queryList(int id);
}
