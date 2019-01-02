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
package lee.fund.pbf.a3.descriptor;

import com.google.protobuf.DescriptorProtos.FieldDescriptorProto;
import lee.fund.pbf.a3.FieldType;
import lee.fund.pbf.a3.ProtoField;

/**
 * JProtobuf supports for {@link FieldDescriptorProto}
 *
 * @author xiemalin
 * @since 2.0.1
 */
public class FieldDescriptorProtoPOJO {

    @Override
    public String toString() {
        return "FieldDescriptorProtoPOJO [name=" + name + ", extendee=" + extendee + ", number=" + number + ", label="
                + label + ", type=" + type + ", typeName=" + typeName + ", defaultValue=" + defaultValue + ", options="
                + options + "]";
    }

    @ProtoField(order = FieldDescriptorProto.NAME_FIELD_NUMBER)
    public String name;

    @ProtoField(order = FieldDescriptorProto.EXTENDEE_FIELD_NUMBER)
    public String extendee;

    @ProtoField(order = FieldDescriptorProto.NUMBER_FIELD_NUMBER)
    public Integer number;

    @ProtoField(order = FieldDescriptorProto.LABEL_FIELD_NUMBER, type = FieldType.ENUM)
    public Label label;

    @ProtoField(order = FieldDescriptorProto.TYPE_FIELD_NUMBER, type = FieldType.ENUM)
    public Type type;

    @ProtoField(order = FieldDescriptorProto.TYPE_NAME_FIELD_NUMBER)
    public String typeName;

    @ProtoField(order = FieldDescriptorProto.DEFAULT_VALUE_FIELD_NUMBER)
    public String defaultValue;

    @ProtoField(order = FieldDescriptorProto.OPTIONS_FIELD_NUMBER, type = FieldType.OBJECT)
    public FieldOptionsPOJO options;
}
