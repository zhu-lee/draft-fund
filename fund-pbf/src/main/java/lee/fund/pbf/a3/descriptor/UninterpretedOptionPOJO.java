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

import com.google.protobuf.DescriptorProtos.UninterpretedOption;
import lee.fund.pbf.a3.FieldType;
import lee.fund.pbf.a3.ProtoField;

import java.util.List;

/**
 * JProtobuf POJO supports for {@link UninterpretedOption}
 *
 * @author xiemalin
 * @since 2.0.1
 */
public class UninterpretedOptionPOJO {

    @ProtoField(order = UninterpretedOption.NAME_FIELD_NUMBER, type = FieldType.OBJECT)
    public List<NamePartPOJO> names;

    @ProtoField(order = UninterpretedOption.IDENTIFIER_VALUE_FIELD_NUMBER)
    public String identifierValue;

    @ProtoField(order = UninterpretedOption.POSITIVE_INT_VALUE_FIELD_NUMBER)
    public Long positiveIntValue;

    @ProtoField(order = UninterpretedOption.NEGATIVE_INT_VALUE_FIELD_NUMBER)
    public Long negativeIntValue;

    @ProtoField(order = UninterpretedOption.DOUBLE_VALUE_FIELD_NUMBER)
    public Double doubleValue;

    @ProtoField(order = UninterpretedOption.STRING_VALUE_FIELD_NUMBER)
    public String stringValue;

    @ProtoField(order = UninterpretedOption.AGGREGATE_VALUE_FIELD_NUMBER)
    public String aggregateValue;

    @Override
    public String toString() {
        return "UninterpretedOptionPOJO [names=" + names + ", identifierValue=" + identifierValue
                + ", positiveIntValue=" + positiveIntValue + ", negativeIntValue=" + negativeIntValue + ", doubleValue="
                + doubleValue + ", stringValue=" + stringValue + ", aggregateValue=" + aggregateValue + "]";
    }


}
