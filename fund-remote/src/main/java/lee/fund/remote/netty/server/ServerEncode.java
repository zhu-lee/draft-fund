package lee.fund.remote.netty.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lee.fund.remote.protocol.CodecAdapter;
import lee.fund.remote.protocol.ResponseMessage;

import java.nio.charset.StandardCharsets;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/2 19:46
 * Desc:
 */
public class ServerEncode extends MessageToByteEncoder<ResponseMessage> implements CodecAdapter {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, ResponseMessage msg, ByteBuf out) throws Exception {
        //"ResponseMessage" + " " + [length] + "\r\n" + [data]
        byte[] bytes = RESPONSE_MESSAGE_CODEC.encode(msg);
        out.writeBytes(RESPONSE_HEADER);
        out.writeBytes(Integer.toString(bytes.length).getBytes(StandardCharsets.US_ASCII));
        out.writeBytes(CRLF);
        out.writeBytes(bytes);
    }
}
