package lee.fund.pbf.code;

import lee.fund.pbf.a3.utils.FieldInfo;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/25 8:45
 * Desc:
 */
public class DoubleCode implements BasicCode {
    public static final DoubleCode INSTANCE = new DoubleCode();

    private DoubleCode() {
    }

    @Override
    public Code get(Class<?> fieldTypeClass) {
        if (fieldTypeClass == double.class) {
            return DoubleBasic.INSTANCE;
        } else if (fieldTypeClass == Double.class) {
            return DoubleWrapper.INSTANCE;
        }
        return null;
    }

    private static class DoubleBasic extends BaseCode {
        private static final DoubleBasic INSTANCE = new DoubleBasic();

        private DoubleBasic() {
            super("Double");
            this.sizeMethod = "computeDoubleSize";
            this.sizeDescriptor = "(ID)I";
            this.writeMethod = "writeDouble";
            this.writeDescriptor = "(ID)V";
            this.readMethod = "readDouble";
            this.readDescriptor = "()D";
        }

    }

    private static class DoubleWrapper extends DoubleBasic {
        private static final DoubleWrapper INSTANCE = new DoubleWrapper();

        @Override
        protected void convertTo(MethodVisitor mv, FieldInfo f) {
            mv.visitMethodInsn(INVOKEVIRTUAL, Internal.DOUBLE, "doubleValue", "()D", false);
        }

        @Override
        protected void convertFrom(MethodVisitor mv, FieldInfo f) {
            mv.visitMethodInsn(INVOKESTATIC, Internal.DOUBLE, "valueOf", "(D)Ljava.lang.Double;", false);
        }
    }
}
