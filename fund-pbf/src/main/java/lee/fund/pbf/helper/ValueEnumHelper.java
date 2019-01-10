package lee.fund.pbf.helper;

import com.google.protobuf.CodedOutputStream;
import lee.fund.util.lang.EnumValueSupport;
import lee.fund.util.lang.Enums;

import java.io.IOException;
import java.util.List;

/**
 * Created by guohua.cui on 2016/12/2.
 */
public final class ValueEnumHelper {
    public static int from(EnumValueSupport value) {
        return value.value();
    }

    @SuppressWarnings("unchecked")
    public static <T extends EnumValueSupport> T to(int value, Class<T> clazz) {
        return Enums.valueOf(clazz, value);
    }

    public static int sizeList(final int fieldNumber, final List<EnumValueSupport> values) {
        int size = 0;
        for (EnumValueSupport v : values) {
            if (v != null) {
                size += CodedOutputStream.computeEnumSizeNoTag(v.value());
            }
        }
        size += values.size() * CodedOutputStream.computeTagSize(fieldNumber);
        return size;
    }

    public static void writeList(CodedOutputStream stream, int fieldNumber, List<EnumValueSupport> values) throws IOException {
        if (!values.isEmpty()) {
            for (EnumValueSupport v : values) {
                stream.writeEnum(fieldNumber, v.value());
            }
        }
    }
}
