package lee.fund.pbf.code;

import lee.fund.pbf.a3.utils.FieldInfo;
import lee.fund.pbf.build.BeanContext;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import static org.objectweb.asm.Opcodes.*;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/27 19:35
 * Desc:
 */
public class ObjectCode implements BasicCode {
    public static final ObjectCode INSTANCE = new ObjectCode();

    private ObjectCode() {
    }

    @Override
    public Code get(Class<?> cls) {
        if (cls.isPrimitive() || cls.isAnnotation() || cls.isInterface() || cls.isEnum()) {
            return null;
        }
        return ObjectWrapper.INSTANCE;
    }

    private static class ObjectWrapper extends Code {
        private static final ObjectWrapper INSTANCE = new ObjectWrapper();

        public ObjectWrapper() {
            super("Object");
        }

        @Override
        void buildSizeCode(MethodVisitor mv, FieldInfo f, BeanContext ctx) {
            String descriptor = Type.getDescriptor(f.getField().getType());
            mv.visitLdcInsn(Type.getType(descriptor));

            descriptor = String.format("(I%s%s)I", Desc.LOBJECT, Desc.LCLASS);
            mv.visitMethodInsn(INVOKESTATIC, getHelpType(), "size", descriptor, false);
        }

        @Override
        void buildListSizeCode(MethodVisitor mv, FieldInfo f, BeanContext ctx) {
            String descriptor = Type.getDescriptor(f.getGenericKeyType());
            mv.visitLdcInsn(Type.getType(descriptor));

            descriptor = String.format("(I%s%s)I", Desc.LLIST, Desc.LCLASS);
            mv.visitMethodInsn(INVOKESTATIC, getHelpType(), "sizeList", descriptor, false);
        }

        @Override
        void buildWriteCode(MethodVisitor mv, FieldInfo f, BeanContext ctx) {
            String descriptor = Type.getDescriptor(f.getField().getType());
            mv.visitLdcInsn(Type.getType(descriptor));
            descriptor = String.format("(%sI%s%s)V", Desc.LCODEDOUTPUTSTREAM, Desc.LOBJECT, Desc.LCLASS);
            mv.visitMethodInsn(INVOKESTATIC, getHelpType(), "write", descriptor, false);
        }

        @Override
        void buildListWriteCode(MethodVisitor mv, FieldInfo f, BeanContext ctx) {
            String descriptor = Type.getDescriptor(f.getField().getType());
            mv.visitLdcInsn(Type.getType(descriptor));
            descriptor = String.format("(%sI%s%s)V", Desc.LCODEDOUTPUTSTREAM, Desc.LLIST, Desc.LCLASS);
            mv.visitMethodInsn(INVOKESTATIC, getHelpType(), "writeList", descriptor, false);
        }

        @Override
        void buildReadCode(MethodVisitor mv, FieldInfo f, BeanContext ctx) {
            String type = Type.getInternalName(f.getField().getType());
            String descriptor = "L" + type + ";";

            mv.visitVarInsn(ALOAD, 1);
            mv.visitLdcInsn(Type.getType(descriptor));
            descriptor = String.format("(%s%s)%s", Desc.LCodedInputStream, Desc.LCLASS, Desc.LOBJECT);
            mv.visitMethodInsn(INVOKESTATIC, getHelpType(), "read", descriptor, false);
            mv.visitTypeInsn(CHECKCAST, type);
            writField(mv, f.getField(), ctx.getBeanClassName());
        }

        @Override
        void buildListReadCode(MethodVisitor mv, FieldInfo f, BeanContext ctx) {
            mv.visitVarInsn(ALOAD, 2);
            readField(mv, f.getField(), ctx.getBeanClassName());

            mv.visitVarInsn(ALOAD, 1);
            String descriptor = Type.getDescriptor(f.getField().getType());
            mv.visitLdcInsn(Type.getType(descriptor));
            descriptor = String.format("(%s%s)%s", Desc.LCodedInputStream, Desc.LCLASS, Desc.LOBJECT);
            mv.visitMethodInsn(INVOKESTATIC, getHelpType(), "read", descriptor, false);

            descriptor = String.format("(%s)Z", Desc.LOBJECT);
            mv.visitMethodInsn(INVOKEINTERFACE, Internal.LIST, "add", descriptor, true);
            mv.visitInsn(POP);
        }
    }
}
