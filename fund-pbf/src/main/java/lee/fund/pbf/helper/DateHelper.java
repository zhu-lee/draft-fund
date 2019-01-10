package lee.fund.pbf.helper;

import com.google.protobuf.CodedOutputStream;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by guohua.cui on 2016/12/2.
 */
public final class DateHelper {
    public static long from(Date value) {
        return value.getTime();
    }

    public static Date to(long value) {
        return new Date(value);
    }

    public static int sizeList(final int fieldNumber, final List<Date> value) {
        int size = 0;
        for (Date t : value) {
            if (t != null) {
                size += CodedOutputStream.computeInt64SizeNoTag(from(t));
            }
        }
        size += value.size() * CodedOutputStream.computeTagSize(fieldNumber);
        return size;
    }

    public static void writeList(CodedOutputStream stream, int fieldNumber, List<Date> values) throws IOException {
        if (!values.isEmpty()) {
            for (Date value : values) {
                stream.writeInt64(fieldNumber, from(value));
            }
        }
    }
}
