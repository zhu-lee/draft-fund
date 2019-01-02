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

import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import com.google.protobuf.DescriptorProtos.UninterpretedOption.NamePart;
import lee.fund.pbf.a3.ProtoField;

import java.util.List;

/**
 * JProtobuf POJO supports for {@link NamePart}
 *
 * @author xiemalin
 * @since 2.0.1
 */
public class NamePartPOJO {

    @ProtoField(order = FileDescriptorSet.FILE_FIELD_NUMBER)
    public List<FileDescriptorProtoPOJO> files;

    @Override
    public String toString() {
        return "NamePartPOJO [files=" + files + "]";
    }


}
