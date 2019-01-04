package lee.fund.remote.netty.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lee.fund.remote.protocol.CodecAdapter;
import lee.fund.remote.protocol.RequestMessage;

import java.nio.charset.StandardCharsets;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/2 19:47
 * Desc:
 */
public class ClientEncode extends MessageToByteEncoder<RequestMessage> implements CodecAdapter {

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RequestMessage msg, ByteBuf out) throws Exception {
        //"RequestMessage" + " " + [length] + "\r\n" + [data]
        byte[] bytes = REQUEST_MESSAGE_CODEC.encode(msg);
        out.writeBytes(REQUEST_HEADER);
        out.writeBytes(Integer.toString(bytes.length).getBytes(StandardCharsets.US_ASCII));
        out.writeBytes(CRLF);
        out.writeBytes(bytes);
    }
}
