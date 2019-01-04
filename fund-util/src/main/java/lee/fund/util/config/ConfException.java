package lee.fund.util.config;

/**
 * Created by guohua.cui on 15/4/27.
 */
public class ConfException extends RuntimeException {
    public ConfException() {
        super();
    }

    public ConfException(String msg) {
        super(msg);
    }

    public ConfException(Exception inner) {
        super(inner);
    }

    public ConfException(String msg, Exception inner) {
        super(msg, inner);
    }
}
