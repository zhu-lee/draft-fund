package lee.fund.pbf.test.lib;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.UninitializedMessageException;

import java.io.IOException;
import java.util.ArrayList;

public abstract class BaseCodec<T> implements Codec<T> {
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

    /**********
     * checkNull
     **********/

    protected static void checkNull(String field, Object value) {
        if (value == null) {
            ArrayList<String> missingFields = new ArrayList<>();
            missingFields.add(field);
            throw new UninitializedMessageException(missingFields);
        }
    }
}
