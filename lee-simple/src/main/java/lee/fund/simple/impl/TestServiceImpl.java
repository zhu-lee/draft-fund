package lee.fund.simple.impl;

import lee.fund.simple.iface.TestService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/30 16:55
 * Desc:
 */
@Service
public class TestServiceImpl implements TestService{
    @Override
    public List<String> queryList(int id) {
        return new ArrayList<String>() {{
            add("aa");
            add("bb");
        }};
    }
}
