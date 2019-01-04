package lee.fund.remote.netty.client;

import lee.fund.remote.netty.server.Decoder;
import lee.fund.remote.protocol.CodecAdapter;
import lee.fund.remote.protocol.ResponseMessage;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/2 19:47
 * Desc:
 */
public class ClientDecode extends Decoder implements CodecAdapter {
    //"ResponseMessage" + " " + [length] + "\r\n" + [data]
    public ClientDecode() {
        super(ResponseMessage.class, RESPONSE_HEADER);
    }
}
