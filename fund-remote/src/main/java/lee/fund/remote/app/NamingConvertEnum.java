package lee.fund.remote.app;

import java.util.Objects;

public enum NamingConvertEnum {
    CAMEL("camel"), PASCAL("pascal");

    String name;

    NamingConvertEnum(String name) {
        this.name = name;
    }

    public static NamingConvertEnum of(String name) {
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

    public static String transform(String name, NamingConvertEnum convention) {
        Character firstLetter = name.charAt(0);
        if (convention == NamingConvertEnum.PASCAL) {
            firstLetter = Character.toUpperCase(firstLetter);
        } else {
            firstLetter = Character.toLowerCase(firstLetter);
        }
        return firstLetter.toString() + name.substring(1);
    }
}
