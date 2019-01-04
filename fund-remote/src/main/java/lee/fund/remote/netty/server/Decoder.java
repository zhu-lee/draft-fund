package lee.fund.remote.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lee.fund.pbf.a3.Codec;
import lee.fund.pbf.build.CodecFactory;
import lee.fund.remote.exception.ProtocolException;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/2 20:20
 * Desc:
 */
public class Decoder extends ByteToMessageDecoder {
    private Codec codec;
    private byte[] header;
    private int headerLength;
    private State state = State.HEADER;
    private int length;

    public Decoder(Class<?> cls, byte[] header) {
        this.codec = CodecFactory.get(cls);
        this.header = header;
        this.headerLength = header.length;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        this.decode(in, out);
    }

    private void decode(ByteBuf in, List<Object> out) throws Exception {
        switch (state) {
            case HEADER:
                this.decodeHeader(in, out);
                break;
            case LENGTH:
                this.decodeLength(in, out);
                break;
            case CRLF:
                this.decodeCRLF(in, out);
                break;
            case BODY:
                this.decodeBody(in, out);
                break;
            default:
                throw new ProtocolException("invalid Decoder state：" + state);
        }
    }

    private void decodeHeader(ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() > headerLength) {
            byte[] bytes = new byte[headerLength];
            in.readBytes(bytes);
            if (!Arrays.equals(bytes, header)) {
                throw new ProtocolException("invalid header of data，expect header：" + new String(header, StandardCharsets.US_ASCII));
            }
            state = State.LENGTH;
            this.decode(in, out);
        }
    }

    private void decodeLength(ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() > 0) {
            int n = in.bytesBefore((byte) '\r');
            if (n <= -1) {
                throw new ProtocolException("invalid length of data");
            }
            byte[] bytes = new byte[n];
            in.readBytes(bytes);
            length = Integer.parseInt(new String(bytes, StandardCharsets.US_ASCII));

            state = State.CRLF;
            this.decode(in, out);
        }
    }

    private void decodeCRLF(ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() >= 2) {
            if (in.readByte() != '\r' || in.readByte() != '\n') {
                throw new ProtocolException("invalid '\\r\\n' of data");
            }
            state = State.BODY;
            this.decode(in, out);
        }
    }

    private void decodeBody(ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() > length) {
            byte[] bytes = new byte[length];
            in.readBytes(bytes);
            Object obj = codec.decode(bytes);
            out.add(obj);

            state = State.HEADER;
            if (in.readableBytes() > 0) {
                this.decode(in, out);
            }
        }
    }

    private enum State {
        HEADER, LENGTH, CRLF, BODY
    }
}
