package lee.fund.pbf.test;

import lee.fund.pbf.test.lib.BaseCodec;
import lee.fund.pbf.test.lib.BeanContext;
import lee.fund.pbf.test.lib.FieldInfo;
import lee.fund.pbf.test.lib.ProtobufProxyUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.FileOutputStream;
import java.util.List;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/20 17:10
 * Desc:
 */
public class AllTypeTest extends ClassLoader implements Opcodes {
    public static void main(String[] args) {
        MethodVisitor mv;
        Class<?> targetClass = ProtobufBean.class;
        BeanContext ctx = new BeanContext(targetClass);
        ClassWriter cw = AllTypeTest.createClassWriter(ctx);
        List<FieldInfo> fields = getFields(targetClass);

        // ctor
//        buildConstructor(cw);
//
//        // size
//        buildSize(cw, ctx, fields);
//
//        // writeTo
//        buildWriteTo(cw, ctx, fields);
//
//        // readFrom
//        buildReadFrom(cw, ctx, fields);
//
//        // getDescriptor
//        buildGetDescriptor(cw, ctx);
//
//        buildSizeEntry(cw, ctx);
//
//        buildWriteToEntry(cw, ctx);
//
//        buildReadFromEntry(cw, ctx);

        cw.visitEnd();

        try {
            // 获取生成的class文件对应的二进制流
            byte[] code = cw.toByteArray();


            //将二进制流写到本地磁盘上
            FileOutputStream fos = new FileOutputStream("E:\\Example.class");
            fos.write(code);
            fos.close();
        } catch (Exception e) {

        }

    }

    private static ClassWriter createClassWriter(BeanContext ctx) {
        ClassWriter cw = new ClassWriter(COMPUTE_MAXS | COMPUTE_FRAMES);
        String superName = Type.getInternalName(BaseCodec.class);
        String signature = String.format("Llee/fund/pbf/test/lib/BaseCodec<%s>;", ctx.getBeanDescriptor());
        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, ctx.getCodecClassName(), signature, superName, null);
        return cw;
    }

    private static List<FieldInfo> getFields(Class<?> cls) {
        return ProtobufProxyUtils.fetchFieldInfos(cls, false);
    }

    private static void buildConstructor() {

    }
}
