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

import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.DescriptorProtos.FieldOptions;
import lee.fund.pbf.a3.FieldType;
import lee.fund.pbf.a3.ProtoField;

import java.util.List;

/**
 * JProtobuf POJO class for {@link FieldOptions}
 *
 * @author xiemalin
 * @since 2.0.1
 */
public class FieldOptionsPOJO {

    @ProtoField(order = FieldOptions.CTYPE_FIELD_NUMBER, type = FieldType.ENUM)
    public DescriptorProtos.FieldOptions.CType ctype;

    @ProtoField(order = FieldOptions.PACKED_FIELD_NUMBER)
    public Boolean packed;

    @ProtoField(order = FieldOptions.DEPRECATED_FIELD_NUMBER)
    public Boolean deprecated;

    @ProtoField(order = FieldOptions.LAZY_FIELD_NUMBER)
    public Boolean lazy;

    @ProtoField(order = FieldOptions.WEAK_FIELD_NUMBER)
    public Boolean weak;

    @ProtoField(order = FieldOptions.UNINTERPRETED_OPTION_FIELD_NUMBER, type = FieldType.OBJECT)
    public List<UninterpretedOptionPOJO> uninterpretedOption;

    @Override
    public String toString() {
        return "FieldOptionsPOJO [ctype=" + ctype + ", packed=" + packed + ", deprecated=" + deprecated + ", lazy="
                + lazy + ", weak=" + weak + ", uninterpretedOption=" + uninterpretedOption + "]";
    }
}
