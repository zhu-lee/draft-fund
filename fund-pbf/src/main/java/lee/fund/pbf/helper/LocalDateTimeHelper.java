package lee.fund.pbf.helper;

import com.google.protobuf.CodedOutputStream;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * Created by guohua.cui on 2016/12/2.
 */
public final class LocalDateTimeHelper {
    public static long from(LocalDateTime value) {
        return value.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static LocalDateTime to(long value) {
        return Instant.ofEpochMilli(value).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static int sizeList(final int fieldNumber, final List<LocalDateTime> value) {
        int size = 0;
        for (LocalDateTime t : value) {
            if (t != null) {
                size += CodedOutputStream.computeInt64SizeNoTag(from(t));
            }
        }
        size += value.size() * CodedOutputStream.computeTagSize(fieldNumber);
        return size;
    }

    public static void writeList(CodedOutputStream stream, int fieldNumber, List<LocalDateTime> values) throws IOException {
        if (!values.isEmpty()) {
            for (LocalDateTime value : values) {
                stream.writeInt64(fieldNumber, from(value));
            }
        }
    }
}
