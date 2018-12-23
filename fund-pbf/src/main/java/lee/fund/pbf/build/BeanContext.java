package lee.fund.pbf.build;

import lee.fund.pbf.utils.CodecUtil;
import lombok.Getter;
import org.objectweb.asm.Type;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/21 14:19
 * Desc:
 */
@Getter
public class BeanContext {
    private static final ConcurrentMap<String, BeanContext> map = new ConcurrentHashMap<>();
    private Class<?> beanClass;
    private String beanClassName;
    private String codecClassName;
    private String beanDescriptor;

    private BeanContext(Class<?> cls) {
        this.beanClass=cls;
        this.beanClassName = Type.getInternalName(cls);
        this.beanDescriptor = Type.getDescriptor(cls);
        this.codecClassName = CodecUtil.getCodecTypeName(cls).replace(".","/");
    }

    public static BeanContext get(Class<?> cls) {
        return map.computeIfAbsent(Type.getInternalName(cls), k -> new BeanContext(cls));
    }
}