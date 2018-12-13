package lee.fund.remote.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.Getter;

import java.util.List;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/13 19:31
 * Desc:
 */
@Getter
public class CodecAdapter {
    private final CodecEncoder codecEncoder = new CodecEncoder();
    private final CodecDecoder codecDecoder = new CodecDecoder();

    private static class CodecEncoder extends MessageToByteEncoder<ResponseMessage> {

        @Override
        protected void encode(ChannelHandlerContext ctx, ResponseMessage msg, ByteBuf byteBuf) throws Exception {

        }
    }

    private class CodecDecoder extends ByteToMessageDecoder{

        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {

        }
    }
}
