package lee.fund.pbf.helper;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.WireFormat;
import lee.fund.pbf.a3.Codec;
import lee.fund.pbf.build.CodecFactory;
import lee.fund.pbf.code.SelectCase;
import lee.fund.pbf.utils.CodecUtil;

import java.io.IOException;
import java.util.List;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/27 20:28
 * Desc:
 */
public class ObjectHelper {
    public static <T> int size(final int order, final T value, final Class<T> cls) {
        Codec<T> codec = CodecFactory.get(cls);
        int size = codec.size(value);
        size += CodedOutputStream.computeUInt32SizeNoTag(size);
        size += CodedOutputStream.computeTagSize(order);
        return size;
    }

    public static <T> int sizeList(final int order, final List<T> value, final Class<T> cls) {
        if (value.isEmpty()) {
            return 0;
        }

        Codec<T> codec = CodecFactory.get(cls);
        int totalSize = 0;
        for (T t : value) {
            if (t != null) {
                int size = codec.size(t);
                totalSize += CodedOutputStream.computeTagSize(order) + CodedOutputStream.computeUInt32SizeNoTag(size) + size;
            }
        }
        return totalSize;
    }

    public static <T> void writeList(final CodedOutputStream outputStream, final int order, final List<T> values, final Class<T> cls) throws IOException {
        if (!values.isEmpty()) {
            Codec<T> codec = CodecFactory.get(cls);
            for (T value : values) {
                writ(outputStream, order, value, codec);
            }
        }
    }

    public static <T> void writ(final CodedOutputStream outputStream, final int order, final T value, final Class<T> cls) throws IOException {
        Codec<T> codec = CodecFactory.get(cls);
        writ(outputStream, order, value, codec);
    }

    public static <T> void writ(final CodedOutputStream outputStream, final int order, final T value, final Codec<T> codec) throws IOException {
        outputStream.writeUInt32NoTag(CodecUtil.makeTag(order, WireFormat.WIRETYPE_LENGTH_DELIMITED));
        outputStream.writeUInt32NoTag(codec.size(value));
        codec.writeTo(value, outputStream);
    }

    public static <T> T read(final CodedInputStream inputStream, final Class<T> cls) throws IOException {
        int length = inputStream.readRawVarint32();
        final int oldLimit = inputStream.pushLimit(length);
        Codec<T> codec = CodecFactory.get(cls);
        T value = codec.readFrom(inputStream);//recursively read the object
        inputStream.checkLastTagWas(0);
        inputStream.popLimit(oldLimit);
        return value;
    }
}
