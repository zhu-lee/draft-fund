package lee.fund.remote.exception;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/5 9:46
 * Desc:
 */
public class RpcException extends RuntimeException{
    private final int errorCode;

    public RpcException() {
        super();
        this.errorCode = 0;
    }

    public RpcException(RpcError error, Object... args) {
        super(String.format(error.description(), args));
        this.errorCode = error.value();
    }

    public RpcException(String message) {
        this(0, message);
    }

    public RpcException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public RpcException(Throwable cause) {
        super(cause);
        this.errorCode = 0;
    }

    public RpcException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
