package lee.fund.pbf.helper;

import com.google.protobuf.CodedOutputStream;

import java.io.IOException;
import java.util.List;

/**
 * Created by guohua.cui on 2016/12/2.
 */
public final class SFixed64Helper {
    public static int sizeList(final int fieldNumber, final List<Long> value) {
        int size = 0;
        for (Long t : value) {
            if (t != null) {
                size += CodedOutputStream.computeSFixed64SizeNoTag(t);
            }
        }
        size += value.size() * CodedOutputStream.computeTagSize(fieldNumber);
        return size;
    }

    public static void writeList(CodedOutputStream stream, int fieldNumber, List<Long> values) throws IOException {
        if (!values.isEmpty()) {
            for (Long value : values) {
                stream.writeSFixed64(fieldNumber, value);
            }
        }
    }
}
