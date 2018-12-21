/*
 * Copyright 2002-2007 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package lee.fund.pbf.test.lib;

import lee.fund.util.lang.UncheckedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Utility class for probuf proxy.
 *
 * @author xiemalin
 * @since 1.0.7
 */
public class ProtobufProxyUtils {

    public static final Map<Class<?>, FieldType> TYPE_MAPPING;

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(ProtobufProxyUtils.class.getName());

    static {
        TYPE_MAPPING = new HashMap<>();

        TYPE_MAPPING.put(int.class, FieldType.INT32);
        TYPE_MAPPING.put(Integer.class, FieldType.INT32);
        TYPE_MAPPING.put(short.class, FieldType.INT32);
        TYPE_MAPPING.put(Short.class, FieldType.INT32);
        TYPE_MAPPING.put(Byte.class, FieldType.INT32);
        TYPE_MAPPING.put(byte.class, FieldType.INT32);
        TYPE_MAPPING.put(long.class, FieldType.INT64);
        TYPE_MAPPING.put(Long.class, FieldType.INT64);
        TYPE_MAPPING.put(String.class, FieldType.STRING);
        TYPE_MAPPING.put(byte[].class, FieldType.BYTES);
        TYPE_MAPPING.put(Byte[].class, FieldType.BYTES);
        TYPE_MAPPING.put(Float.class, FieldType.FLOAT);
        TYPE_MAPPING.put(float.class, FieldType.FLOAT);
        TYPE_MAPPING.put(double.class, FieldType.DOUBLE);
        TYPE_MAPPING.put(Double.class, FieldType.DOUBLE);
        TYPE_MAPPING.put(Boolean.class, FieldType.BOOL);
        TYPE_MAPPING.put(boolean.class, FieldType.BOOL);
    }

    public static FieldType getFieldType(Class<?> cls) {
        return TYPE_MAPPING.get(cls);
    }

    /**
     * Fetch field infos.
     *
     * @return the list
     */
    public static List<FieldInfo> fetchFieldInfos(Class cls, boolean validate) {
        List<Field> fields = FieldUtils.findMatchedFields(cls, ProtoField.class);

        //TODO fieldList 校验空出错？
        if (fields == null || fields.isEmpty()) {
            throw new IllegalArgumentException("Invalid class [" + cls.getName() + "] no field use annotation @ " + ProtoField.class.getName() + " at class " + cls.getName());
        }
        return ProtobufProxyUtils.processDefaultValue(cls, fields, validate);
    }

    /**
     * to process default value of <code>@Protobuf</code> value on field.
     *
     * @param fields all field to process
     * @return list of fields
     */
    public static List<FieldInfo> processDefaultValue(Class cls, List<Field> fields, boolean validate) {
        if (fields == null) {
            return null;
        }

        List<FieldInfo> ret = new ArrayList<>(fields.size());

        int maxOrder = 0;
        List<FieldInfo> unOrderFields = new ArrayList<>(fields.size());
        for (Field field : fields) {
            ProtoField annotation = field.getAnnotation(ProtoField.class);
            if (annotation == null) {
                throw new RuntimeException("Field '" + field.getName() + "' has no @ProtoField annotation");
            }

            // check field is support for protocol buffer
            // any array except byte array is not support
            String simpleName = field.getType().getName();
            if (simpleName.startsWith("[")) {
                if ((!simpleName.equals(byte[].class.getName())) && (!simpleName.equals(Byte[].class.getName()))) {
                    throw new UncheckedException("Array type of field '" + field.getName() + "' on class '"
                            + field.getDeclaringClass().getName() + "' is not support,  please use List instead.");
                }
            }

            FieldInfo fieldInfo = new FieldInfo(cls, field);
            fieldInfo.setRequired(annotation.required());
            fieldInfo.setDescription(annotation.description());

            // process type
            if (annotation.type() == FieldType.DEFAULT) {
                FieldType fieldType = TYPE_MAPPING.get(field.getType());
                if (fieldType == null) {
                    // check if type is enum
                    if (Enum.class.isAssignableFrom(field.getType())) {
                        fieldType = FieldType.ENUM;
                    } else {
                        fieldType = FieldType.OBJECT;
                    }
                }
                fieldInfo.setType(fieldType);
            } else {
                fieldInfo.setType(annotation.type());
            }

            int order = annotation.order();
            if (order > 0) {
                fieldInfo.setOrder(order);
                if (order > maxOrder) {
                    maxOrder = order;
                }
            } else {
                unOrderFields.add(fieldInfo);
            }

            if (validate) {
                ProtobufProxyUtils.validateField(fieldInfo);
            }
            ret.add(fieldInfo);
        }

        if (unOrderFields.isEmpty()) {
            return ret;
        }

        for (FieldInfo fieldInfo : unOrderFields) {
            maxOrder++;
            fieldInfo.setOrder(maxOrder);
            if (logger.isDebugEnabled()) {
                logger.debug("Field '{}' from {} with @ProtoField annotation but not set order or order is 0, " +
                        "It will be set order value to {}",fieldInfo.getField().getName(), fieldInfo.getField().getDeclaringClass().getName(), maxOrder);
            }
        }
        return ret;
    }

    /**
     * TODO 这个验证还城要嘛？
     * Checks that the property field type is consistent with the declared protoField field type
     * @param fi
     */
    private static void validateField(FieldInfo fi) {
        if (fi.isMap()) {
            return;
        }

        boolean invalid = false;
        Class<?> type = fi.isList() ? fi.getGenericKeyType() : fi.getField().getType();
        switch (fi.type) {
            case OBJECT:
                if (type.isPrimitive() || type.isEnum() || type.isAnnotation() || type.isInterface()) {
                    invalid = true;
                }
                break;
            case DOUBLE:
                if (type != double.class && type != Double.class) {
                    invalid = true;
                }
                break;
            case FLOAT:
                if (type != float.class && type != Float.class) {
                    invalid = true;
                }
                break;
            case INT64:
            case UINT64:
            case FIXED64:
            case SFIXED64:
            case SINT64:
                if (type != long.class && type != Long.class && type != Date.class && type != LocalDateTime.class && type != ZonedDateTime.class && type != LocalDate.class && type != LocalTime.class) {
                    invalid = true;
                }
                break;
            case INT32:
            case FIXED32:
            case UINT32:
            case SFIXED32:
            case SINT32:
                if (type != int.class && type != Integer.class) {
                    invalid = true;
                }
                break;
            case BOOL:
                if (type != boolean.class && type != Boolean.class) {
                    invalid = true;
                }
                break;
            case STRING:
                if (type != String.class && type != BigDecimal.class) {
                    invalid = true;
                }
                break;
            case BYTES:
                if (type != byte[].class) {
                    invalid = true;
                }
                break;
            case ENUM:
                if (!type.isEnum()) {
                    invalid = true;
                }
        }
        if (invalid) {
            throw new IllegalArgumentException(String.format("can't convert %s to %s(field: %s)", type.getName(), fi.getType(), fi.getField().getName()));
        }
    }
}
