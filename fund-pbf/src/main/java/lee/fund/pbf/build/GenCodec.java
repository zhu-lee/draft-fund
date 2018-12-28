package lee.fund.pbf.build;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.UninitializedMessageException;
import lee.fund.pbf.a3.Codec;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/23 11:57
 * Desc:   Codec implementation of the base class
 */
public abstract class GenCodec<T> implements Codec<T> {
    protected Descriptors.Descriptor descriptor;

    @Override
    public byte[] encode(T t) throws IOException {
        int size = size(t);
        final byte[] result = new byte[size];
        final CodedOutputStream output = CodedOutputStream.newInstance(result);
        writeTo(t, output);
        return result;
    }

    @Override
    public T decode(byte[] bytes) throws IOException {
        CodedInputStream input = CodedInputStream.newInstance(bytes, 0, bytes.length);
        return readFrom(input);
    }

    protected static void checkNull(String field, Object value) {
        if (value == null) {
            ArrayList<String> missingFields = new ArrayList<>();
            missingFields.add(field);
            throw new UninitializedMessageException(missingFields);
        }
    }
}
