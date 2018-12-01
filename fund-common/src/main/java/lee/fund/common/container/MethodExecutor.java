package lee.fund.common.container;

import com.esotericsoftware.reflectasm.MethodAccess;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/1 12:57
 * Desc:
 */
public class MethodExecutor {
    private MethodAccess access;
    private int index;
    private Object obj;

    public MethodExecutor(MethodAccess access,Object obj,int index) {
        this.access = access;
        this.obj = obj;
        this.index = index;
    }

    public Object invoke(Object[] args) {
        return access.invoke(obj, index, args);
    }

//    public Class[] getParameterTypes() {
//        return access.getParameterTypes()[index];
//    }
}
