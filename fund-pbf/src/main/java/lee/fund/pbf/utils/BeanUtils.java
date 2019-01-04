package lee.fund.pbf.utils;

import lee.fund.util.lang.StrUtils;

import java.lang.reflect.Field;

public final class BeanUtils {
    public static String getGetterName(Field f) {
        String getter = f.getName();
        if (f.getType() != boolean.class) {
            getter = "get" + StrUtils.firstCharToUpperCase(getter);
        } else if (!startWithIs(getter)) {
            getter = "is" + StrUtils.firstCharToUpperCase(getter);
        }
        return getter;
    }

    public static String getSetterName(Field f) {
        String name = f.getName();
        if (f.getType() == boolean.class && startWithIs(name)) {
            name = name.substring(2);
        }
        return "set" + StrUtils.firstCharToUpperCase(name);
    }

    private static boolean startWithIs(String name) {
        return name.startsWith("is") && name.length() > 2 && Character.isUpperCase(name.charAt(2));
    }
}
