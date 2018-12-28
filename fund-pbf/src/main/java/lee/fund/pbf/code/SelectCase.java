package lee.fund.pbf.code;

import lee.fund.pbf.a3.utils.FieldInfo;
import lee.fund.util.lang.UncheckedException;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/25 8:47
 * Desc:
 */
public class SelectCase {
    public static Code get(FieldInfo f) {
        BasicCode basicCode = null;
        switch (f.getType()) {
            case DOUBLE:
                basicCode = DoubleCode.INSTANCE;
                break;
            case FLOAT   :
                break;
            case INT32   :
                break;
            case INT64   :
                break;
            case FIXED32 :
                break;
            case FIXED64 :
                break;
            case BOOL    :
                break;
            case STRING  :
                break;
            case BYTES   :
                break;
            case UINT32  :
                break;
            case UINT64  :
                break;
            case SFIXED32:
                break;
            case SFIXED64:
                break;
            case SINT32  :
                break;
            case SINT64  :
                break;
            case OBJECT  :
                basicCode = ObjectCode.INSTANCE;
                break;
            case ENUM    :
                break;
            default:
                throw new UncheckedException(String.format("not found FieldType on field: %",f.getField().getName()));
        }

        Class<?> fieldTypeClass = f.isList()?f.getGenericKeyType() : f.getType().getClass();
        Code code = basicCode.get(fieldTypeClass);
        if (code == null) {
            throw new UncheckedException(String.format("can't get FieldType[%s] for [%s]",
                    f.getType().toString(), f.getField().getType().getName()));
        }
        return code;
    }
}
