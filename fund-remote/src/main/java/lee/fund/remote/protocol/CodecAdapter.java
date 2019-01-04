package lee.fund.remote.protocol;

import lee.fund.pbf.a3.Codec;
import lee.fund.pbf.build.CodecFactory;

import java.nio.charset.StandardCharsets;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/13 19:31
 * Desc:
 */
public interface CodecAdapter {
    byte[] RESPONSE_HEADER = "ResponseMessage ".getBytes(StandardCharsets.US_ASCII);
    byte[] CRLF = new byte[]{'\r', '\n'};
    Codec<ResponseMessage> RESPONSE_MESSAGE_CODEC = CodecFactory.get(ResponseMessage.class);

    byte[] REQUEST_HEADER = "RequestMessage ".getBytes(StandardCharsets.US_ASCII);
    Codec<RequestMessage> REQUEST_MESSAGE_CODEC = CodecFactory.get(RequestMessage.class);
}
