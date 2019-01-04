package lee.fund.remote.exception;

public class ProtocolException extends RuntimeException {
    public ProtocolException() {
        super();
    }

    public ProtocolException(String message) {
        super(message);
    }

    public ProtocolException(Throwable cause) {
        super(cause);
    }

    public ProtocolException(String message, Throwable cause) {
        super(message, cause);
    }
}