package lee.fund.pbf.build;

import lee.fund.pbf.a3.FieldInfo;
import lee.fund.pbf.a3.ProtobufProxyUtils;
import lee.fund.pbf.base.ClassMapping;
import lee.fund.pbf.base.GenCodec;
import lee.fund.pbf.test.ProtobufBean;
import org.objectweb.asm.ClassWriter;
import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.io.FileOutputStream;
import java.util.List;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/23 17:01
 * Desc:
 */
public class CodeCreator {
    public static void main(){
        Class<?> targetClass = ProtobufBean.class;
        create(targetClass);
    }

    public static byte[] create(Class<?> cls) {
        BeanContext ctx = BeanContext.get(cls);
        ClassWriter cw = createClassWriter(ctx);
        List<FieldInfo> fields = getFields(cls);

        // ctor
        buildConstructor(cw);

        // size
        buildSize(cw, ctx, fields);

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

        try{
            // 获取生成的class文件对应的二进制流
            byte[] code = cw.toByteArray();


            //将二进制流写到本地磁盘上
            FileOutputStream fos = new FileOutputStream("G:\\work\\workspace-fund\\draft-fund1\\fund-pbf\\src\\main\\java\\lee\\fund\\pbf\\test\\Example.class");
            fos.write(code);
            fos.close();
        }catch(Exception e){

        }

        return cw.toByteArray();
    }

    private static ClassWriter createClassWriter(BeanContext ctx) {
        ClassWriter cw = new ClassWriter(COMPUTE_MAXS | COMPUTE_FRAMES);
        String superName = Type.getInternalName(GenCodec.class);
        String signature = String.format("Llee/fund/pbf/test/lib/BaseCodec<%s>;", ctx.getBeanDescriptor());
        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, ctx.getCodecClassName(), signature, superName, null);
        return cw;
    }

    private static List<FieldInfo> getFields(Class<?> cls) {
        return ProtobufProxyUtils.fetchFieldInfos(cls, false);
    }

    private static void buildConstructor(ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, ClassMapping.GENCODEC.fullName(),"<init>","()V",false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private static void buildSize(ClassWriter cw, BeanContext ctx, List<FieldInfo> fields) {
        String descriptor = String.format("(%s)I", ctx.getBeanDescriptor());
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "size", descriptor, null, null);
        mv.visitCode();
        mv.visitInsn(ICONST_0);
        mv.visitVarInsn(ISTORE, 2);

        boolean first=false;
        for (FieldInfo f : fields) {

            List.class.getName()
        }

        mv.visitVarInsn(ALOAD,0);
        mv.visitMethodInsn();
    }
}
