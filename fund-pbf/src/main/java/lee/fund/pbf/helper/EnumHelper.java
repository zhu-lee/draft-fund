package lee.fund.pbf.helper;

import com.google.protobuf.CodedOutputStream;

import java.io.IOException;
import java.util.List;

/**
 * Created by guohua.cui on 2016/12/2.
 */
public final class EnumHelper {
    public static int from(Enum value) {
        return value.ordinal();
    }

    public static <T extends Enum> T to(int value, T[] values) {
        return values[value];
    }

    public static int sizeList(final int fieldNumber, final List<Enum> value) {
        int size = 0;
        for (Enum t : value) {
            if (t != null) {
                size += CodedOutputStream.computeEnumSizeNoTag(t.ordinal());
            }
        }
        size += value.size() * CodedOutputStream.computeTagSize(fieldNumber);
        return size;
    }

    public static void writeList(CodedOutputStream stream, int fieldNumber, List<Enum> values) throws IOException {
        if (!values.isEmpty()) {
            for (Enum value : values) {
                stream.writeEnum(fieldNumber, value.ordinal());
            }
        }
    }
}
