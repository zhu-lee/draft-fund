package lee.fund.pbf.code;

import lee.fund.pbf.a3.utils.FieldInfo;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import lee.fund.pbf.build.BeanContext;
import lee.fund.pbf.utils.BeanUtils;
import lee.fund.pbf.utils.CodecUtil;
import lombok.Getter;
import org.objectweb.asm.MethodVisitor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static org.objectweb.asm.Opcodes.*;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/25 8:43
 * Desc:
 */
public abstract class Code {
    @Getter
    private String helpType;

    public Code(String realType) {
        this.helpType = String.format("lee/fund/pbf/%helper", realType);
    }

    public void size(MethodVisitor mv, FieldInfo fi, BeanContext ctx) {
        mv.visitVarInsn(ILOAD, 2);
        buildVisitInt(mv, fi.getOrder());
        mv.visitVarInsn(ALOAD, 1);
        readField(mv, fi.getField(), ctx.getBeanClassName());
        if (fi.isList()) {
            buildListSizeCode(mv, fi, ctx);
        } else {
            buildSizeCode(mv, fi, ctx);
        }
        mv.visitInsn(IADD);
        mv.visitVarInsn(ISTORE, 2);
    }

    public void writ(MethodVisitor mv, FieldInfo f, BeanContext ctx) {
        mv.visitVarInsn(ALOAD, 2);
        buildVisitInt(mv, f.getOrder());
        mv.visitVarInsn(ALOAD, 1);
        readField(mv, f.getField(), ctx.getBeanClassName());
        if (f.isList()) {
            buildListWriteCode(mv, f, ctx);
        } else {
            buildWriteCode(mv, f, ctx);
        }
    }

    public void read(MethodVisitor mv, FieldInfo f, BeanContext ctx, Label label) {
        mv.visitVarInsn(ILOAD, 3);
        int tag = CodecUtil.makeTag(f.getOrder(), f.getType().getInternalFieldType().getWireType());

        buildVisitInt(mv, tag);
        Label lb = new Label();
        mv.visitJumpInsn(IF_ICMPNE, lb);

        mv.visitVarInsn(ALOAD, 2);
        if (f.isList()) {
            //whether property is initialized
            readField(mv, f.getField(), ctx.getBeanClassName());
            Label nwLable = new Label();
            mv.visitJumpInsn(IFNONNULL, nwLable);
            mv.visitVarInsn(ALOAD, 2);
            mv.visitTypeInsn(NEW, Internal.ARRAYLIST);
            mv.visitInsn(DUP);
            mv.visitMethodInsn(INVOKESPECIAL, Internal.ARRAYLIST, "<init>", "()V", false);
            writField(mv, f.getField(), ctx.getBeanClassName());
            mv.visitLabel(nwLable);
            mv.visitFrame(F_SAME, 0, null, 0, null);
            buildListReadCode(mv, f, ctx);
        } else {
            buildReadCode(mv, f, ctx);
        }
        mv.visitJumpInsn(GOTO, label);
        mv.visitLabel(lb);
        mv.visitFrame(F_SAME, 0, null, 0, null);
    }

    private void buildVisitInt(MethodVisitor mv, int value) {
        if (value >= 0 && value <= 5) {
            mv.visitInsn(ICONST_0 + value);
        } else if (value >= -128 && value <= 127) {
            mv.visitIntInsn(BIPUSH, value);
        } else if (value >= -32768 && value <= 32767) {
            mv.visitIntInsn(SIPUSH, value);
        } else {
            mv.visitLdcInsn(value);
        }
    }

    public void readField(MethodVisitor mv, Field field, String className) {
        if (field.getModifiers() == Modifier.PUBLIC) {
            mv.visitFieldInsn(GETFIELD, className, field.getName(), Type.getDescriptor(field.getType()));
        } else {
            String descriptor = String.format("()%s", field.getType());
            mv.visitMethodInsn(INVOKEVIRTUAL, className, BeanUtils.getGetterName(field), descriptor, false);
        }
    }

    public void writField(MethodVisitor mv, Field field, String className) {
        if (field.getModifiers() == Modifier.PUBLIC) {
            mv.visitFieldInsn(PUTFIELD, className, field.getName(), Type.getDescriptor(field.getType()));
        } else {
            String descriptor = String.format("(%s)V", Type.getDescriptor(field.getType()));
            mv.visitMethodInsn(INVOKEVIRTUAL, className, BeanUtils.getSetterName(field), descriptor, false);
        }
    }


    abstract void buildSizeCode(MethodVisitor mv, FieldInfo f, BeanContext ctx);

    abstract void buildListSizeCode(MethodVisitor mv, FieldInfo f, BeanContext ctx);

    abstract void buildWriteCode(MethodVisitor mv, FieldInfo f, BeanContext ctx);

    abstract void buildListWriteCode(MethodVisitor mv, FieldInfo f, BeanContext ctx);

    abstract void buildReadCode(MethodVisitor mv, FieldInfo f, BeanContext ctx);

    abstract void buildListReadCode(MethodVisitor mv, FieldInfo f, BeanContext ctx);
}
