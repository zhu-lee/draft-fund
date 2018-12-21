package lee.fund.pbf.test.lib;

public final class CodecUtil {
    private static final int TAG_TYPE_BITS = 3;
    private static final String SUFFIX = "$$Codec";

    public static int makeTag(final int fieldNumber, final int wireType) {
        return (fieldNumber << TAG_TYPE_BITS) | wireType;
    }

    public static String getCodecTypeName(final Class<?> cls) {
        return cls.getName() + SUFFIX;
    }
}
