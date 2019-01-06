package lee.fund.task.data;

import lee.fund.util.lang.EnumValueSupport;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/5 18:12
 * Desc:
 */
public enum ExecuteType implements EnumValueSupport {
    AUTO(0), MANUAL(1);//0-自动，1-手动
    private int value;

    ExecuteType(int value) {
        this.value = value;
    }

    @Override
    public int value() {
        return this.value;
    }
}