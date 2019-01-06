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

import com.google.protobuf.DescriptorProtos.MessageOptions;
import lee.fund.pbf.a3.FieldType;
import lee.fund.pbf.a3.ProtoField;

import java.util.List;

/**
 * JProtobuf supports for {@link MessageOptions}
 *
 * @author xiemalin
 * @since 2.0.1
 */
public class MessageOptionsPOJO {

    @ProtoField(order = MessageOptions.MESSAGE_SET_WIRE_FORMAT_FIELD_NUMBER)
    public Boolean messageSetWireFormat;

    @ProtoField(order = MessageOptions.NO_STANDARD_DESCRIPTOR_ACCESSOR_FIELD_NUMBER)
    public Boolean noStandardDescriptorAccessor;

    @ProtoField(order = MessageOptions.UNINTERPRETED_OPTION_FIELD_NUMBER, type = FieldType.OBJECT)
    public List<UninterpretedOptionPOJO> uninterpretedOptions;

    @Override
    public String toString() {
        return "MessageOptionsPOJO [messageSetWireFormat=" + messageSetWireFormat + ", noStandardDescriptorAccessor="
                + noStandardDescriptorAccessor + ", uninterpretedOptions=" + uninterpretedOptions + "]";
    }


}