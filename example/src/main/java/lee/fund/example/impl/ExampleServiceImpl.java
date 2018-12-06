package lee.fund.example.impl;

import lee.fund.example.iface.ExampleService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/4 14:36
 * Desc:
 */
@Service
public class ExampleServiceImpl implements ExampleService{
    @Override
    public List<String> queryList(int id, String name) {
        return new ArrayList<String>() {{
            add("id");
            add("name");
        }};
    }

    @Override
    public List<Integer> getList(int id, Integer code) {
        return new ArrayList<Integer>() {{
            add(1);
            add(2);
        }};
    }
}
