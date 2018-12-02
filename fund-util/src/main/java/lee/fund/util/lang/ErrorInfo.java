package lee.fund.util.lang;

/**
 * 定义错误信息接口。
 */
public interface ErrorInfo {
    /**
     * 错误代码。
     *
     * @return
     */
    int getCode();

    /**
     * 错误信息。
     *
     * @return
     */
    String getMessage();

    static ErrorInfo of(int code, String message) {
        return new SimpleErrorInfo(code, message);
    }

    class SimpleErrorInfo implements ErrorInfo {
        private int code;
        private String message;

        private SimpleErrorInfo(int code, String message) {
            this.code = code;
            this.message = message;
        }

        @Override
        public int getCode() {
            return this.code;
        }

        @Override
        public String getMessage() {
            return this.message;
        }
    }
}
