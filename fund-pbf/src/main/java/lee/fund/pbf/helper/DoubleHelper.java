package lee.fund.pbf.helper;

import com.google.protobuf.CodedOutputStream;

import java.io.IOException;
import java.util.List;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/26 13:38
 * Desc:
 */
public class DoubleHelper {
    public static int sizeList(final int order, final List<Double> value) {
        int size = 0;
        for (Double t : value) {
            if (t != null) {
                size += CodedOutputStream.computeDoubleSizeNoTag(t);
            }
        }
        size += value.size() * CodedOutputStream.computeTagSize(order);
        return size;
    }

    public static void writeList(CodedOutputStream stream, int order, List<Double> values) throws IOException {
        if (!values.isEmpty()) {
            for (Double value : values) {
                stream.writeDouble(order, value);
            }
        }
    }
}
