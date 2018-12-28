package lee.fund.pbf.build;

import lee.fund.pbf.build.CodeCreator;
import lombok.Setter;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/23 19:36
 * Desc:
 */
public class CodecClassLoader extends ClassLoader {
    @Setter
    private Class<?> cls;

    public CodecClassLoader(final ClassLoader parent) {
        super(parent);
    }

    @Override
    protected Class<?> findClass(final String name) throws ClassNotFoundException {
        if (cls == null) {
            return super.findClass(name);
        }
        byte[] b = CodeCreator.create(cls);
        return defineClass(name, b, 0, b.length);
    }
}
