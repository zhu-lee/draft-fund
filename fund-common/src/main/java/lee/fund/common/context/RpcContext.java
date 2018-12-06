package lee.fund.common.context;

public class RpcContext {
    private RpcContext() {
        // 防止实例化
    }

    /**
     * 设置上下文信息
     *
     * @param contextID
     * @param parentID
     */
    public static void set(String contextID, String parentID) {
        Context.set(contextID, parentID);
    }

    /**
     * 清除上下文信息
     */
    public static void remove() {
        Context.remove();
    }

    /**
     * 获取上下文 ID
     *
     * @return
     */
    public static String getContextID() {
        return Context.getContextID();
    }

    /**
     * 设置上下文 ID
     *
     * @param id
     */
    public static void setContextID(String id) {
        Context.setContextID(id);
    }

    /**
     * 清除上下文 ID
     */
    public static void removeContextID() {
        Context.removeContextID();
    }

    /**
     * 获取父消息 ID
     *
     * @return
     */
    public static String getParentID() {
        return Context.getParentID();
    }

    /**
     * 设置父消息 ID
     *
     * @param id
     */
    public static void setParentID(String id) {
        Context.setParentID(id);
    }

    /**
     * 清除父消息 ID
     */
    public static void removeParentID() {
        Context.removeParentID();
    }
}
