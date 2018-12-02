package lee.fund.util.lang;

public class UncheckedException extends RuntimeException {
    public UncheckedException() {
        super();
    }

    public UncheckedException(String msg) {
        super(msg);
    }

    public UncheckedException(Throwable inner) {
        super(inner);
    }

    public UncheckedException(String msg, Throwable inner) {
        super(msg, inner);
    }
}
