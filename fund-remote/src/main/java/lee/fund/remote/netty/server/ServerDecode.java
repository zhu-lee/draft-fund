package lee.fund.remote.netty.server;

import lee.fund.remote.protocol.CodecAdapter;
import lee.fund.remote.protocol.RequestMessage;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/2 19:47
 * Desc:
 */
public class ServerDecode extends Decoder implements CodecAdapter {
    //"RequestMessage" + " " + [length] + "\r\n" + [data]
    public ServerDecode() {
        super(RequestMessage.class, REQUEST_HEADER);
    }
}
