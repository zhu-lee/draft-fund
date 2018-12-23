package lee.fund.pbf.utils;

import lombok.Getter;

public final class CodecUtil {
    private static final int TAG_TYPE_BITS = 3;
    @Getter
    private static final String SUFFIX = "$$Codec";

    public static int makeTag(final int fieldNumber, final int wireType) {
        return (fieldNumber << TAG_TYPE_BITS) | wireType;
    }

    public static String getCodecTypeName(final Class<?> cls) {
        return cls.getName() + SUFFIX;
    }
}
