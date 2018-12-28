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
package lee.fund.pbf.a3.utils;

import lee.fund.pbf.a3.FieldType;
import lee.fund.pbf.a3.ProtoField;
import lee.fund.util.lang.UncheckedException;

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
    private static Map<Class<?>, FieldType> typeMapping = new HashMap<>();

    static {
        typeMapping.put(int.class, FieldType.INT32);
        typeMapping.put(Integer.class, FieldType.INT32);
        typeMapping.put(short.class, FieldType.INT32);
        typeMapping.put(Short.class, FieldType.INT32);
        typeMapping.put(Byte.class, FieldType.INT32);
        typeMapping.put(byte.class, FieldType.INT32);
        typeMapping.put(long.class, FieldType.INT64);
        typeMapping.put(Long.class, FieldType.INT64);
        typeMapping.put(String.class, FieldType.STRING);
        typeMapping.put(byte[].class, FieldType.BYTES);
        typeMapping.put(Byte[].class, FieldType.BYTES);
        typeMapping.put(Float.class, FieldType.FLOAT);
        typeMapping.put(float.class, FieldType.FLOAT);
        typeMapping.put(double.class, FieldType.DOUBLE);
        typeMapping.put(Double.class, FieldType.DOUBLE);
        typeMapping.put(Boolean.class, FieldType.BOOL);
        typeMapping.put(boolean.class, FieldType.BOOL);
        typeMapping.put(Date.class, FieldType.TIME);
        typeMapping.put(LocalDateTime.class, FieldType.LTIME);
    }

    public static FieldType getFieldType(Class<?> type) {
        return typeMapping.get(type);
    }

    /**
     * Test if target type is from protocol buffer default type
     *
     * @param cls target type
     * @return true if is from protocol buffer default type
     */
    public static boolean isScalarType(Class<?> cls) {
        return typeMapping.containsKey(cls);
    }

    public static List<FieldInfo> processDefaultValue(Class<?> declareType, List<Field> fields) {
        return processDefaultValue(declareType, fields, true);
    }

    /**
     * to process default value of <code>@Protobuf</code> value on field.
     *
     * @param fields all field to process
     * @return list of fields
     */
    public static List<FieldInfo> processDefaultValue(Class<?> declareType, List<Field> fields, boolean validate) {
        if (fields == null) {
            return null;
        }

        List<FieldInfo> ret = new ArrayList<FieldInfo>(fields.size());

        int maxOrder = 0;
        List<FieldInfo> unorderFields = new ArrayList<FieldInfo>(fields.size());
        for (Field field : fields) {
            ProtoField annotation = field.getAnnotation(ProtoField.class);
            if (annotation == null) {
                throw new UncheckedException("Field '" + field.getName() + "' has no @ProtoField annotation");
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

            FieldInfo fieldInfo = new FieldInfo(declareType, field);
            fieldInfo.setRequired(annotation.required());
            fieldInfo.setDescription(annotation.description());

            // process type
            if (annotation.type() == FieldType.DEFAULT) {
                FieldType fieldType = typeMapping.get(field.getType());
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
                unorderFields.add(fieldInfo);
            }

            if (validate) {
                validateField(fieldInfo);
            }
            ret.add(fieldInfo);
        }

        if (unorderFields.isEmpty()) {
            return ret;
        }

        for (FieldInfo fieldInfo : unorderFields) {
            maxOrder++;
            fieldInfo.setOrder(maxOrder);
        }

        return ret;
    }

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
            case TIME:
                if (type != Date.class) {
                    invalid = true;
                }
                break;
            case LTIME:
                if (type != LocalDateTime.class && type != LocalDate.class) {
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
