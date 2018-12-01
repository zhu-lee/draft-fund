package lee.fund.common.app;

import lee.fund.util.lang.EnumValueSupport;
import lee.fund.util.lang.Enums;

public enum	FailModeEnum implements EnumValueSupport {
    /**
     * unknown
     */
    UNKNOW(-1),
    /**
     * Failure to switch to another service
     */
    FailOver(1),
    /**
     * Return immediately after failure
     */
    FailFast(2);

    private int value;

    private FailModeEnum(int value) {
        this.value = value;
    }

    @Override
    public int value() {
        return this.value;
    }

    public int getValue() {
        return value;
    }

    public static FailModeEnum valueOf(int value) {
        FailModeEnum anEnum = Enums.valueOf(FailModeEnum.class, value);
        return anEnum == null ? UNKNOW : anEnum;
    }
}
