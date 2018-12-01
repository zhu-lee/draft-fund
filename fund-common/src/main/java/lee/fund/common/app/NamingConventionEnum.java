package lee.fund.common.app;

import java.util.Objects;

/**
 * Created by noname on 15/12/6.
 */
public enum NamingConventionEnum {
    CAMEL("camel"), PASCAL("pascal");

    String name;

    NamingConventionEnum(String name) {
        this.name = name;
    }

    public static NamingConventionEnum of(String name) {
        Objects.requireNonNull(name, "name");
        switch (name.toLowerCase()) {
            case "camel":
                return CAMEL;
            case "pascal":
                return PASCAL;
            default:
                throw new IllegalArgumentException("Undefined NameStyle: " + name);
        }
    }

    public static String transform(String name, NamingConventionEnum convention) {
        Character firstLetter = name.charAt(0);
        if (convention == NamingConventionEnum.PASCAL) {
            firstLetter = Character.toUpperCase(firstLetter);
        } else {
            firstLetter = Character.toLowerCase(firstLetter);
        }
        return firstLetter.toString() + name.substring(1);
    }
}
