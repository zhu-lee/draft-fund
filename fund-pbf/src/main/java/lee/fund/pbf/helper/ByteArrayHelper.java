package lee.fund.pbf.helper;

import com.google.protobuf.CodedOutputStream;

import java.io.IOException;
import java.util.List;

/**
 * Created by guohua.cui on 2016/12/2.
 */
public final class ByteArrayHelper {
    public static int sizeList(final int fieldNumber, final List<byte[]> value) {
        int size = 0;
        for (byte[] t : value) {
            if (t != null) {
                size += CodedOutputStream.computeByteArraySizeNoTag(t);
            }
        }
        size += value.size() * CodedOutputStream.computeTagSize(fieldNumber);
        return size;
    }

    public static void writeList(CodedOutputStream stream, int fieldNumber, List<byte[]> values) throws IOException {
        if (!values.isEmpty()) {
            for (byte[] value : values) {
                stream.writeByteArray(fieldNumber, value);
            }
        }
    }
}
