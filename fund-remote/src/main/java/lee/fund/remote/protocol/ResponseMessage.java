package lee.fund.remote.protocol;

import lee.fund.remote.exception.RpcError;
import lee.fund.remote.exception.RpcException;
import lee.fund.util.lang.FaultException;
import lee.fund.util.lang.StrUtils;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/5 9:34
 * Desc:
 */
@Setter
@Getter
public class ResponseMessage {
    private boolean success;
    private RemoteValue result;
    private String errorInfo;
    private long serverTime;
    private List<RemoteCookie> cookies;
    private int errorCode;//错误代码
    private String errorDetail;//错误详情，如异常堆栈，一般只应该在 DEBUG 模式传播给客户端，便于快速调试
    private String messageID;//消息 ID, 每个请求唯一

    public ResponseMessage() {
    }

    public static ResponseMessage success(Object result) {
        ResponseMessage message = new ResponseMessage();
        message.setServerTime(System.currentTimeMillis());
        message.setSuccess(true);
        message.setResult(RemoteCoder.encode(result));
        return message;
    }

    public static ResponseMessage failed(Throwable e) {
        ResponseMessage message = new ResponseMessage();
        message.setServerTime(System.currentTimeMillis());

        if (e instanceof RpcException) {
            message.setErrorCode(((RpcException) e).getErrorCode());
        } else if (e instanceof FaultException) {
            message.setErrorCode(((FaultException) e).getErrorCode());
        } else {
            message.setErrorCode(RpcError.SERVER_UNKNOWN_ERROR.value());
        }

        message.setErrorInfo(StrUtils.isBlank(e.getMessage()) ? e.toString() : e.getMessage());
        //TODO setErrorDetail
//        if (AppConfig.getDefault().isDebugEnabled()) {
//            message.setErrorDetail(Exceptions.getStackTrace(e));
//        }
        return message;
    }

    public static ResponseMessage failed(RpcError ec) {
        ResponseMessage message = new ResponseMessage();
        message.setServerTime(System.currentTimeMillis());
        message.setErrorCode(ec.value());
        message.setErrorInfo(ec.description());
        return message;
    }
}
