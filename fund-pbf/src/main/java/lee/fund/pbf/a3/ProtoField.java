package lee.fund.pbf.a3;

import java.lang.annotation.*;

/**
 * A mapped annotation for protobuf
 *
 * @author xiemalin
 * @since 1.0.0
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface ProtoField {

    /**
     * <pre>
     * Specifying Field Rules to <code>required</code> or <code>optional</code>
     * default is false. <code>optional</code>
     * </pre>
     *
     * @return Specifying Field Rules
     */
    boolean required() default false;

    /**
     * Set field order. It starts at 1;
     *
     * @return field order.
     */
    int order() default 0;

    /**
     * @return field type
     */
    FieldType type() default FieldType.DEFAULT;

    /**
     * @return description to the field
     */
    String description() default "";
}