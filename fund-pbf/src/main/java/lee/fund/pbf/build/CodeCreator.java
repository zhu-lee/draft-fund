package lee.fund.pbf.build;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors;
import lee.fund.pbf.a3.ProtoField;
import lee.fund.pbf.a3.utils.CodedConstant;
import lee.fund.pbf.a3.utils.FieldInfo;
import lee.fund.pbf.a3.utils.FieldUtils;
import lee.fund.pbf.a3.utils.ProtobufProxyUtils;
import lee.fund.pbf.code.*;
import lee.fund.pbf.test.ProtobufBean;
import lee.fund.pbf.utils.BeanUtils;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import static org.objectweb.asm.ClassWriter.COMPUTE_FRAMES;
import static org.objectweb.asm.ClassWriter.COMPUTE_MAXS;
import static org.objectweb.asm.Opcodes.*;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/23 17:01
 * Desc:
 */
public class CodeCreator {
    public static void main(String[] args) {
        Class<?> targetClass = ProtobufBean.class;
        create(targetClass);
    }

    public static byte[] create(Class<?> cls) {
        BeanContext ctx = new BeanContext(cls);
        ClassWriter cw = createClassWriter(ctx);
        List<FieldInfo> fields = getFields(cls);

        // ctor
        buildConstructor(cw);

        // size
        buildSize(cw, ctx, fields);

        // writeTo
        buildWriteTo(cw, ctx, fields);

        // readFrom
        buildReadFrom(cw, ctx, fields);

        // getDescriptor
        buildGetDescriptor(cw, ctx);

        buildSizeEntry(cw, ctx);

        buildWriteToEntry(cw, ctx);

        buildReadFromEntry(cw, ctx);

        cw.visitEnd();

        try {
            // 获取生成的class文件对应的二进制流
            byte[] code = cw.toByteArray();

            //将二进制流写到本地磁盘上
            FileOutputStream fos = new FileOutputStream("E:\\work\\workspace-fund\\draft-fund2\\fund-pbf\\src\\main\\java\\lee\\fund\\pbf\\test\\Example.class");
            fos.write(code);
            fos.close();
        } catch (Exception e) {

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

    private static List<FieldInfo> getFields(Class<?> clazz) {
        List<Field> fields = FieldUtils.findMatchedFields(clazz, ProtoField.class);
        return ProtobufProxyUtils.processDefaultValue(clazz, fields, false);
    }

    private static void buildConstructor(ClassWriter cw) {
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, Internal.CODEC, "<init>", "()V", false);
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

        boolean first = true;
        for (FieldInfo f : fields) {
            Code code = SelectCase.get(f);
            if (f.getField().getType().isPrimitive()) {
                code.size(mv, f, ctx);
            } else {
                mv.visitVarInsn(ALOAD, 1);
                readField(mv, f.getField(), ctx.getBeanClassName());

                Label label = new Label();
                mv.visitJumpInsn(IFNULL, label);

                code.size(mv, f, ctx);

                mv.visitLabel(label);
                if (first) {
                    mv.visitFrame(F_APPEND, 1, new Object[]{INTEGER}, 0, null);
                    first = false;
                } else {
                    mv.visitFrame(F_SAME, 0, null, 0, null);
                }
            }
        }
        mv.visitVarInsn(ILOAD, 2);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private static void readField(MethodVisitor mv, Field field, String className) {
        if (field.getModifiers() == Modifier.PUBLIC) {
            mv.visitFieldInsn(GETFIELD, className, field.getName(), Type.getDescriptor(field.getType()));
        } else {
            String descriptor = String.format("()%s", Type.getDescriptor(field.getType()));
            mv.visitMethodInsn(INVOKEVIRTUAL, className, BeanUtils.getGetterName(field), descriptor, false);
        }
    }

    private static void buildWriteTo(ClassWriter cw, BeanContext ctx, List<FieldInfo> fields) {
        String descriptor = String.format("(%s%s)V", ctx.getBeanDescriptor(), Type.getDescriptor(CodedOutputStream.class));
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "writeTo", descriptor, null, new String[]{Internal.IOEXCEPTION});
        mv.visitCode();

        for (FieldInfo f : fields) {
            //check required null
            Class<?> typeClass = f.getField().getType();
            if (f.isRequired() && !typeClass.isPrimitive()) {
                checkNull(mv, ctx, f.getField(), 1);
            }

            Code code = SelectCase.get(f);
            if (f.getField().getType().isPrimitive()) {
                code.writ(mv, f, ctx);
            } else {
                mv.visitVarInsn(ALOAD, 1);
                readField(mv, f.getField(), ctx.getBeanClassName());

                Label label = new Label();
                mv.visitJumpInsn(IFNULL, label);

                code.writ(mv, f, ctx);

                mv.visitLabel(label);
                mv.visitFrame(F_SAME, 0, null, 0, null);
            }
        }
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    public static void buildReadFrom(ClassWriter cw, BeanContext ctx, List<FieldInfo> fields) {
        String descriptor = String.format("(%s)%s", Type.getDescriptor(CodedInputStream.class), ctx.getBeanDescriptor());
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "readFrom", descriptor, null, new String[]{Internal.IOEXCEPTION});
        mv.visitCode();

        mv.visitTypeInsn(NEW, ctx.getBeanClassName());
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, ctx.getBeanClassName(), "<init>", "()V", false);
        mv.visitVarInsn(ASTORE, 2);

        //while
        Label l1 = new Label();
        mv.visitLabel(l1);
        mv.visitFrame(F_APPEND, 1, new Object[]{ctx.getBeanClassName()}, 0, null);

        //比较
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, Internal.CODEDINPUTSTREAM, "readTag", "()I", false);
        mv.visitVarInsn(ISTORE, 3);
        mv.visitVarInsn(ILOAD, 3);
        Label l3 = new Label();
        mv.visitJumpInsn(IFNE, l3);//>0

        Label l2 = new Label();
        mv.visitJumpInsn(GOTO, l2);

        mv.visitLabel(l3);
        mv.visitFrame(F_APPEND, 1, new Object[]{INTEGER}, 0, null);

        //read field
        for (FieldInfo f : fields) {
            Code code = SelectCase.get(f);
            code.read(mv, f, ctx, l1);
        }

        //skip field
        mv.visitVarInsn(ALOAD, 1);
        mv.visitVarInsn(ILOAD, 3);
        mv.visitMethodInsn(INVOKEVIRTUAL, Internal.CODEDOUTPUTSTREAM, "skipField", "(I)Z", false);
        mv.visitInsn(POP);
        mv.visitJumpInsn(GOTO, l1);

        mv.visitLabel(l2);
        mv.visitFrame(F_CHOP, 1, null, 0, null);//**

        //check null
        buildCheckNullBlock(mv, ctx, fields, 2);

        mv.visitVarInsn(ALOAD, 2);
        mv.visitInsn(ARETURN);//return bean
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private static void buildCheckNullBlock(MethodVisitor mv, BeanContext ctx, List<FieldInfo> fields, int varIndex) {
        for (FieldInfo f : fields) {
            Class<?> typeClass = f.getField().getType();
            if (f.isRequired() && !typeClass.isPrimitive()) {
                checkNull(mv, ctx, f.getField(), varIndex);
            }
        }
    }

    private static void checkNull(MethodVisitor mv, BeanContext ctx, Field field, int varIndex) {
        mv.visitLdcInsn(field.getName());
        mv.visitVarInsn(ALOAD, varIndex);
        readField(mv, field, ctx.getBeanClassName());
        String desc = String.format("(%s%s)V", Desc.LSTRING, Desc.LOBJECT);
        mv.visitMethodInsn(INVOKESTATIC, ctx.getCodecClassName(), "checkNull", desc, false);
    }

    private static void buildGetDescriptor(ClassWriter cw, BeanContext ctx) {
        String descriptor = String.format("()%s", Desc.LDESCRIPTOR);
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "getDescriptor", descriptor, null, new String[]{Internal.IOEXCEPTION});
        mv.visitCode();

        mv.visitVarInsn(ALOAD, 0);
        String fdesc = Type.getDescriptor(Descriptors.Descriptor.class);
        mv.visitFieldInsn(GETFIELD, ctx.getBeanClassName(), "descriptor", fdesc);

        Label label = new Label();
        mv.visitJumpInsn(IFNONNULL, label);

        mv.visitVarInsn(ALOAD, 0);
        mv.visitLdcInsn(Type.getType(ctx.getBeanDescriptor()));
        String methodDesc = String.format("(%s)%s", Desc.LCLASS, Desc.LDESCRIPTOR);
        mv.visitMethodInsn(INVOKESTATIC, Type.getInternalName(CodedConstant.class), "getDescriptor", methodDesc, false);
        mv.visitFieldInsn(PUTFIELD, ctx.getCodecClassName(), "descriptor", fdesc);

        mv.visitLabel(label);
        mv.visitFrame(F_SAME, 0, null, 0, null);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, ctx.getBeanClassName(), "descriptor", fdesc);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private static void buildSizeEntry(ClassWriter cw, BeanContext ctx) {
        String descriptor = String.format("(%s)I", Desc.LOBJECT);
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "size", descriptor, null, new String[]{Internal.IOEXCEPTION});
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, ctx.getBeanClassName());
        mv.visitMethodInsn(INVOKEVIRTUAL, ctx.getCodecClassName(), "size", String.format("(%s)I", ctx.getBeanDescriptor()), false);
        mv.visitInsn(IRETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private static void buildWriteToEntry(ClassWriter cw, BeanContext ctx) {
        String descriptor = String.format("(%s%s)V", Desc.LOBJECT, Desc.LCODEDOUTPUTSTREAM);
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "writeTo", descriptor, null, new String[]{Internal.IOEXCEPTION});
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitTypeInsn(CHECKCAST, ctx.getBeanClassName());
        mv.visitVarInsn(ALOAD, 2);
        descriptor = String.format("(%s%s)V", ctx.getBeanDescriptor(), Desc.LCODEDOUTPUTSTREAM);
        mv.visitMethodInsn(INVOKEVIRTUAL, ctx.getCodecClassName(), "writeTo", descriptor, false);
        mv.visitInsn(RETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }

    private static void buildReadFromEntry(ClassWriter cw, BeanContext ctx) {
        String descriptor = String.format("(%s)%s", Desc.LCodedInputStream, Desc.LOBJECT);
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "readFrom", descriptor, null, new String[]{Internal.IOEXCEPTION});
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        descriptor = String.format("(%s)%s", Desc.LCodedInputStream, ctx.getBeanDescriptor());
        mv.visitMethodInsn(INVOKEVIRTUAL, ctx.getCodecClassName(), "readFrom", descriptor, false);
        mv.visitInsn(ARETURN);
        mv.visitMaxs(0, 0);
        mv.visitEnd();
    }
}
