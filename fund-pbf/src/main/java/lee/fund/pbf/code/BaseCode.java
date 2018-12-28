package lee.fund.pbf.code;

import lee.fund.pbf.a3.utils.FieldInfo;
import lee.fund.pbf.build.BeanContext;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/25 8:52
 * Desc:
 */
public class BaseCode extends Code {
    protected String sizeMethod;
    protected String sizeDescriptor;
    protected String writeMethod;
    protected String writeDescriptor;
    protected String readMethod;
    protected String readDescriptor;

    public BaseCode(String realType) {
        super(realType);
    }

    @Override
    protected void buildSizeCode(MethodVisitor mv, FieldInfo f, BeanContext ctx) {
        convertTo(mv, f);
        mv.visitMethodInsn(INVOKESTATIC, Internal.CODEDINPUTSTREAM, this.sizeMethod, this.sizeDescriptor, false);
    }

    @Override
    protected void buildListSizeCode(MethodVisitor mv, FieldInfo f, BeanContext ctx) {
        mv.visitMethodInsn(INVOKESTATIC, getHelpType(), "sizeList", "(ILjava.util.List;)I", false);
    }

    @Override
    protected void buildWriteCode(MethodVisitor mv, FieldInfo f, BeanContext ctx) {
        convertFrom(mv, f);
        mv.visitMethodInsn(INVOKEVIRTUAL, Internal.CODEDOUTPUTSTREAM, this.writeMethod, this.writeDescriptor, false);
    }

    @Override
    protected void buildListWriteCode(MethodVisitor mv, FieldInfo f, BeanContext ctx) {
        String desc = String.format("(%s%s)V", "Lcom.google.protobuf.CodedOutputStream;", "ILjava.util.List;");
        mv.visitMethodInsn(INVOKESTATIC, getHelpType(), "writeList", desc, false);
    }

    @Override
    protected void buildReadCode(MethodVisitor mv, FieldInfo f, BeanContext ctx) {

    }

    @Override
    protected void buildListReadCode(MethodVisitor mv, FieldInfo f, BeanContext ctx) {

    }

    /**
     * 转为简单类型指令
     *
     * @param mv
     * @param f
     */
    protected void convertTo(MethodVisitor mv, FieldInfo f) {
    }

    /**
     * 转为指定类型指令
     *
     * @param mv
     * @param f
     */
    protected void convertFrom(MethodVisitor mv, FieldInfo f) {
    }
}
