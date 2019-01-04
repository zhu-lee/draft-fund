package lee.fund.task.data;

import lee.fund.pbf.a3.ProtoField;

public class Arg {
    @ProtoField(order = 1, required = true, description = "参数名称")
    public String Name;
    @ProtoField(order = 2, required = true, description = "参数值")
    public String Value;
}
