package lee.fund.remote.exception;

import lee.fund.util.lang.EnumValueSupport;
import lee.fund.util.lang.ErrorInfo;
import lee.fund.util.lang.FaultException;

public enum RpcError implements EnumValueSupport, ErrorInfo {
    // server 端异常, 1-50
    SERVER_UNKNOWN_ERROR(1, "未知错误"),
    SERVER_INVALID_SERVER_TYPE(2, "无效的服务类型: %s - %s"),
    SERVER_SERVICE_NOT_FOUND(3, "服务未找到: %s.%s"),
    SERVER_BUSY(4, "服务器繁忙(业务处理线程池已满)"),
    SERVER_RETRY_OTHER_NODES(5, "请重试其它服务节点"),
    SERVER_OK(50, "OK"),

    // client 端异常, 51-100
    CLIENT_NO_PROVIDER(51, "找不到服务节点: %s"),
    CLIENT_INVALID_SERVER_TYPE(52, "无效的服务类型: %s - %s"),
    CLIENT_ACQUIRE_FAILED(53, "从连接池中获取连接失败: %s"),
    CLIENT_WRITE_FAILED(54, "发送请求到服务器失败: %s"),
    CLIENT_READ_FAILED(55, "读取服务器响应失败: %s"),
    CLIENT_ALL_NODES_FAILED(56, "尝试了所有服务节点均失败: %s, %s"),
    CLIENT_UNKNOWN_ERROR(57, "未知错误: %s"),
    CLIENT_OK(100, "OK");

    private int value;
    private String description;

    RpcError(int value, String description) {
        this.value = value;
        this.description = description;
    }

    @Override
    public int value() {
        return this.value;
    }

    public String description() {
        return this.description;
    }

    @Override
    public int getCode() {
        return this.value;
    }

    @Override
    public String getMessage() {
        return this.description;
    }

    public String formatMessage(Object... args) {
        return String.format(this.description, args);
    }

    /**
     * 转换为 FaultException
     *
     * @return
     */
    public FaultException toFault() {
        return FaultException.of(this);
    }

    public FaultException toFault(Object... messageArgs) {
        return new FaultException(this.getCode(), String.format(this.description, messageArgs));
    }

    /**
     * 是否是业务层异常
     *
     * @param code
     * @return
     */
    public static boolean isBusinessError(int code) {
        // -1 暂且认为应用级错误，避免无谓的重试，因为 -1 是 FaultException.errorCode 缺省值，既然抛出 FaultException 认为应用层处理了
        // 0 是 SimpleResponseMessage.ErrorCode 默认值, 其它语言平台和旧的 SOA 框架都会返回 0, 暂时认为是应用层异常
        return code <= 0 || code > 100;
    }

}
