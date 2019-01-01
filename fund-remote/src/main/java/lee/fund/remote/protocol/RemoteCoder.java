package lee.fund.remote.protocol;

import com.google.protobuf.GeneratedMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lee.fund.pbf.a3.Codec;
import lee.fund.pbf.build.CodecFactory;
import lee.fund.util.lang.UncheckedException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/30 20:09
 * Desc:
 */
public class RemoteCoder {
    private static final int COMPRESSION_THRESHOLD = 100 * 1024; //100K
    private static final Charset CHARSET = StandardCharsets.UTF_8;
    private static final int BUFFER_SIZE = 1024;

    private static final int DT_NULL = 0;
    private static final int DT_BYTE_ARRAY = 1;
    private static final int DT_STRING = 3;
    private static final int DT_INT32 = 4;
    private static final int DT_INT64 = 5;
    private static final int DT_BOOL = 10;
    private static final int DT_FLOAT = 18;
    private static final int DT_DOUBLE = 19;
    private static final int DT_PROTOBUF = 21;
    private static final int DT_BOOL_ARRAY = 100;
    private static final int DT_STRING_ARRAY = 103;
    private static final int DT_INT32_ARRAY = 104;
    private static final int DT_INT64_ARRAY = 105;
    private static final int DT_FLOAT_ARRAY = 118;
    private static final int DT_DOUBLE_ARRAY = 119;
    private static final int DT_COMPRESSED_BOOL_ARRAY = 200;
    private static final int DT_COMPRESSED_STRING_ARRAY = 203;
    private static final int DT_COMPRESSED_INT32_ARRAY = 204;
    private static final int DT_COMPRESSED_INT64_ARRAY = 205;
    private static final int DT_COMPRESSED_FLOAT_ARRAY = 218;
    private static final int DT_COMPRESSED_DOUBLE_ARRAY = 219;
    private static final int DT_COMPRESSED_PROTOBUF = 251;
    private static final int DT_COMPRESSED_STRING = 254;
    private static final int DT_COMPRESSED_BYTE_ARRAY = 255;

    private RemoteCoder() {
    }

    public static RemoteValue encode(Object obj) {
        if (obj == null) {//null
            return new RemoteValue(DT_NULL, new byte[]{0});
        } else if (obj instanceof byte[]) {//byte[]
            return encode((byte[]) obj);
        } else if (obj instanceof String) {//String
            return encode((String) obj);
        } else if (obj instanceof Integer) {//Integer
            return encode((Integer) obj);
        } else if (obj instanceof Long) {//Long
            return encode((Long) obj);
        } else if (obj instanceof Boolean) {//Boolean
            return encode((Boolean) obj);
        } else if (obj instanceof Float) {//Float
            return encode((Float) obj);
        } else if (obj instanceof Double) {//Double
            return encode((Double) obj);
        } else if (obj instanceof GeneratedMessage) {//TODO where to use
            GeneratedMessage message = (GeneratedMessage) obj;
            byte[] bytes = message.toByteArray();
            if (bytes.length > COMPRESSION_THRESHOLD) {
                return new RemoteValue(DT_COMPRESSED_PROTOBUF, compress(bytes, bytes.length));
            } else {
                return new RemoteValue(DT_PROTOBUF, bytes);
            }
        } else if (obj instanceof boolean[]) {//boolean[]
            return encode((boolean[]) obj);
        } else if (obj instanceof Boolean[]) {//Boolean[]
            return encode((Boolean[]) obj);
        } else if (obj instanceof String[]) {//String[]
            return encode((String[]) obj);
        } else if (obj instanceof int[]) {//int[]
            return encode((int[]) obj);
        } else if (obj instanceof Integer[]) {//Integer[]
            return encode((Integer[]) obj);
        } else if (obj instanceof long[]) {//long[]
            return encode((long[]) obj);
        } else if (obj instanceof Long[]) {//Long[]
            return encode((Long[]) obj);
        } else if (obj instanceof float[]) {//float[]
            return encode((float[]) obj);
        } else if (obj instanceof Float[]) {//Float[]
            return encode((Float[]) obj);
        } else if (obj instanceof double[]) {//double[]
            return encode((double[]) obj);
        } else if (obj instanceof Double[]) {//Double[]
            return encode((Double[]) obj);
        } else {
            try {
                Codec codec = CodecFactory.get(obj.getClass());
                byte[] bytes = codec.encode(obj);
                return new RemoteValue(DT_PROTOBUF, bytes);
            } catch (Exception e) {
                throw new UncheckedException(e);
            }
        }
    }

    public static Object decode(int dataType, byte[] data, Class<?> cls) {
        switch (dataType) {
            case DT_NULL:
                return null;
            case DT_BYTE_ARRAY:
                return data;
            case DT_STRING:
                return new String(data, CHARSET);
            case DT_INT32:
                return toIntLE(data);
            case DT_INT64:
                return toLongLE(data);
            case DT_BOOL:
                return data[0] == 1 ? Boolean.TRUE : Boolean.FALSE;
            case DT_FLOAT:
                return Float.intBitsToFloat(toIntLE(data));
            case DT_DOUBLE:
                return Double.longBitsToDouble(toLongLE(data));
            case DT_PROTOBUF:
                return decodeProto(data, cls);
            case DT_BOOL_ARRAY:
            case DT_STRING_ARRAY:
            case DT_INT32_ARRAY:
            case DT_INT64_ARRAY:
            case DT_FLOAT_ARRAY:
            case DT_DOUBLE_ARRAY:
                return decodeArry(data, false, cls);
            case DT_COMPRESSED_BOOL_ARRAY:
            case DT_COMPRESSED_STRING_ARRAY:
            case DT_COMPRESSED_INT32_ARRAY:
            case DT_COMPRESSED_INT64_ARRAY:
            case DT_COMPRESSED_FLOAT_ARRAY:
            case DT_COMPRESSED_DOUBLE_ARRAY:
                return decodeArry(data, false, cls);
            case DT_COMPRESSED_PROTOBUF:
                return decodeProto(decompress(data), cls);
            case DT_COMPRESSED_STRING:
            case DT_COMPRESSED_BYTE_ARRAY:
                default:
                    throw new UncheckedException("Unknown object type：" + dataType);
        }
    }

    private static Object decodeProto(byte[] data, Class<?> cls) {
        try {
            if (GeneratedMessage.class.isAssignableFrom(cls)) {
                Method method = cls.getMethod("parseFrom", byte[].class);
                return method.invoke(null, data);
            } else {
                Codec codec = CodecFactory.get(cls);
                return codec.decode(data);
            }
        } catch (Exception e) {
            throw new UncheckedException();
        }
    }

    private static byte[] decompress(byte[] data) {
        try (ByteArrayInputStream is = new ByteArrayInputStream(data, 4, data.length - 4);
             ByteArrayOutputStream os = new ByteArrayOutputStream();
             GZIPInputStream gis = new GZIPInputStream(is)) {
            int count;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((count = gis.read(buffer)) >= 0) {
                os.write(buffer, 0, count);
            }

            os.flush();
            return os.toByteArray();
        } catch (Exception e) {
            throw new UncheckedException(e);
        }
    }

    private static <T> T decodeArry(byte[] data, boolean compressed, Class<T> cls) {
        byte[] bytes = compressed ? deCompress(data) : data;
        ByteBuf buf = Unpooled.wrappedBuffer(bytes);
        int length = buf.readIntLE();
        T array = (T) Array.newInstance(cls.getComponentType(), length);
        for (int i = 0; i < length; i++) {
            int itemType = buf.readIntLE();
            int itemLength = buf.readIntLE();
            byte[] itemData = new byte[itemLength];
            buf.readBytes(itemData);
            Object item = decode(itemType, itemData, null);
            Array.set(array, i, item);
        }
        return array;
    }

    private static byte[] deCompress(byte[] bytes) {
        try (ByteArrayInputStream input = new ByteArrayInputStream(bytes, 4, bytes.length - 4);
             GZIPInputStream gzip = new GZIPInputStream(input);
             ByteArrayOutputStream out = new ByteArrayOutputStream()
        ) {
            int count;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((count = gzip.read(buffer)) >= 0) {
                out.write(buffer, 0, count);
            }
            out.flush();
            return out.toByteArray();
        } catch (Exception e) {
            throw new UncheckedException(e);
        }
    }

    private static RemoteValue encode(Double[] array) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeIntLE(array.length);
        for (Double s : array) {
            RemoteValue value = encode(s);
            writeValue(buf, value);
        }
        return buildResult(buf, DT_DOUBLE_ARRAY, DT_COMPRESSED_DOUBLE_ARRAY);
    }

    private static RemoteValue encode(double[] array) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeIntLE(array.length);
        for (double s : array) {
            RemoteValue value = encode(s);
            writeValue(buf, value);
        }
        return buildResult(buf, DT_DOUBLE_ARRAY, DT_COMPRESSED_DOUBLE_ARRAY);
    }

    private static RemoteValue encode(Float[] array) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeIntLE(array.length);
        for (Float s : array) {
            RemoteValue value = encode(s);
            writeValue(buf, value);
        }
        return buildResult(buf, DT_FLOAT_ARRAY, DT_COMPRESSED_FLOAT_ARRAY);
    }

    private static RemoteValue encode(float[] array) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeIntLE(array.length);
        for (float s : array) {
            RemoteValue value = encode(s);
            writeValue(buf, value);
        }
        return buildResult(buf, DT_FLOAT_ARRAY, DT_COMPRESSED_FLOAT_ARRAY);
    }

    private static RemoteValue encode(Long[] array) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeIntLE(array.length);
        for (Long s : array) {
            RemoteValue value = encode(s);
            writeValue(buf, value);
        }
        return buildResult(buf, DT_INT64_ARRAY, DT_COMPRESSED_INT64_ARRAY);
    }

    private static RemoteValue encode(long[] array) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeIntLE(array.length);
        for (long s : array) {
            RemoteValue value = encode(s);
            writeValue(buf, value);
        }
        return buildResult(buf, DT_INT64_ARRAY, DT_COMPRESSED_INT64_ARRAY);
    }

    private static RemoteValue encode(Integer[] array) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeIntLE(array.length);
        for (Integer s : array) {
            RemoteValue value = encode(s);
            writeValue(buf, value);
        }
        return buildResult(buf, DT_INT32_ARRAY, DT_COMPRESSED_INT32_ARRAY);
    }

    private static RemoteValue encode(int[] array) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeIntLE(array.length);
        for (int s : array) {
            RemoteValue value = encode(s);
            writeValue(buf, value);
        }
        return buildResult(buf, DT_INT32_ARRAY, DT_COMPRESSED_INT32_ARRAY);
    }

    private static RemoteValue encode(String[] array) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeIntLE(array.length);
        for (String s : array) {
            RemoteValue value = encode(s);
            writeValue(buf, value);
        }
        return buildResult(buf, DT_STRING_ARRAY, DT_COMPRESSED_STRING_ARRAY);
    }

    private static RemoteValue encode(Boolean[] array) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeIntLE(array.length);
        for (Boolean b : array) {
            RemoteValue value = encode(b);
            writeValue(buf, value);
        }
        return buildResult(buf, DT_BOOL_ARRAY, DT_COMPRESSED_BOOL_ARRAY);
    }

    private static RemoteValue encode(boolean[] array) {
        ByteBuf buf = Unpooled.buffer();
        buf.writeIntLE(array.length);
        for (boolean b : array) {
            RemoteValue value = encode(b);
            writeValue(buf, value);
        }
        return buildResult(buf, DT_BOOL_ARRAY, DT_COMPRESSED_BOOL_ARRAY);
    }

    private static RemoteValue buildResult(ByteBuf buf, int type, int compressedType) {
        int length = buf.readableBytes();
        if (length > COMPRESSION_THRESHOLD) {
            return new RemoteValue(compressedType, compress(buf.array(), length));
        } else {
            byte[] data = new byte[length];
            buf.readBytes(data);
            return new RemoteValue(type, data);
        }
    }

    private static void writeValue(ByteBuf buf, RemoteValue value) {
        buf.writeIntLE(value.getDataType());
        buf.writeIntLE(value.getData().length);
        buf.writeBytes(value.getData());
    }

    private static RemoteValue encode(Double v) {
        return new RemoteValue(DT_DOUBLE, toBytesLE(Double.doubleToLongBits(v)));
    }

    private static RemoteValue encode(Float v) {
        return new RemoteValue(DT_FLOAT, toBytesLE(Float.floatToIntBits(v)));
    }

    private static RemoteValue encode(boolean v) {
        return new RemoteValue(DT_BOOL, v ? new byte[]{1} : new byte[]{0});
    }

    private static RemoteValue encode(Boolean v) {
        return new RemoteValue(DT_BOOL, v ? new byte[]{1} : new byte[]{0});
    }

    private static RemoteValue encode(Long value) {
        return new RemoteValue(DT_INT64, toBytesLE(value));
    }

    private static RemoteValue encode(Integer value) {
        return new RemoteValue(DT_INT32, toBytesLE(value));
    }

    private static RemoteValue encode(String value) {
        byte[] bytes = value.getBytes(CHARSET);
        if (bytes.length > COMPRESSION_THRESHOLD) {
            return new RemoteValue(DT_COMPRESSED_STRING, compress(bytes, bytes.length));
        } else {
            return new RemoteValue(DT_STRING, bytes);
        }
    }

    private static RemoteValue encode(byte[] value) {
        if (value.length > COMPRESSION_THRESHOLD) {
            return new RemoteValue(DT_COMPRESSED_BYTE_ARRAY, compress(value, value.length));
        } else {
            return new RemoteValue(DT_BYTE_ARRAY, value);
        }
    }

    private static byte[] compress(byte[] data, int count) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream(); GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(data, 0, count);
            gzip.close();

            byte[] bytes = out.toByteArray();//data
            byte[] result = new byte[data.length + 4];//save data and data length
            System.arraycopy(toBytesLE(data.length), 0, result, 0, 4);
            System.arraycopy(bytes, 0, result, 4, bytes.length);
            return result;
        } catch (Exception e) {
            throw new UncheckedException(e);
        }
    }

    private static byte[] toBytesLE(int v) {
        return new byte[]{(byte) (v & 0xff),
                (byte) ((v >> 8) & 0xff),
                (byte) ((v >> 16) & 0xff),
                (byte) ((v >> 24) & 0xff)};
    }

    private static byte[] toBytesLE(long v) {
        return new byte[]{(byte) (v & 0xff),
                (byte) ((v >> 8) & 0xff),
                (byte) ((v >> 16) & 0xff),
                (byte) ((v >> 24) & 0xff),
                (byte) ((v >> 32) & 0xff),
                (byte) ((v >> 40) & 0xff),
                (byte) ((v >> 48) & 0xff),
                (byte) ((v >> 56) & 0xff)};
    }

    private static int toIntLE(byte[] bytes) {
        return (bytes[3]) << 24
                | (bytes[2] & 0xff) << 16
                | (bytes[1] & 0xff) << 8
                | (bytes[0] & 0xff);
    }

    private static long toLongLE(byte[] bytes) {
        return (long) (bytes[7]) << 56
                | (long) (bytes[6] & 0xff) << 48
                | (long) (bytes[5] & 0xff) << 40
                | (long) (bytes[4] & 0xff) << 32
                | (long) (bytes[3] & 0xff) << 24
                | (long) (bytes[2] & 0xff) << 16
                | (long) (bytes[1] & 0xff) << 8
                | (long) (bytes[0] & 0xff);
    }
}
