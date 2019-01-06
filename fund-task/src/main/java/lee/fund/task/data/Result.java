package lee.fund.task.data;

import lee.fund.pbf.a3.ProtoField;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2019/1/5 11:43
 * Desc:   调用结果
 */
public class Result {
    @ProtoField(order = 1, required = true, description = "是否成功")
    public boolean Success;

    @ProtoField(order = 2, description = "错误信息")
    public String ErrorInfo;

//    public Result() {
//        // for encode/decode
//    }

    public Result(boolean success, String error) {
        this.Success = success;
        this.ErrorInfo = error;
    }
}
