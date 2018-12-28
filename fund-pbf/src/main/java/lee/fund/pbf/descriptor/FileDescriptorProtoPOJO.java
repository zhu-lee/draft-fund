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
package lee.fund.pbf.descriptor;

import com.google.protobuf.DescriptorProtos.FileDescriptorProto;
import lee.fund.pbf.a3.FieldType;
import lee.fund.pbf.a3.ProtoField;

import java.util.List;

/**
 * JProtobuf POJO supports for {@link FileDescriptorProto}
 *
 * @author xiemalin
 * @since 2.0.1
 */
public class FileDescriptorProtoPOJO {

    /**
     * <pre>
     * file name, relative to root of source tree
     * </pre>
     */
    @ProtoField(order = FileDescriptorProto.NAME_FIELD_NUMBER)
    public String name;

    /**
     * <pre>
     * e.g. "foo", "foo.bar", etc.
     * </pre>
     */
    @ProtoField(order = FileDescriptorProto.PACKAGE_FIELD_NUMBER)
    public String pkg;

    /**
     * <pre>
     * Names of files imported by this file.
     * </pre>
     */
    @ProtoField(order = FileDescriptorProto.DEPENDENCY_FIELD_NUMBER, type = FieldType.STRING)
    public List<String> dependencies;

    /**
     * <pre>
     * All top-level messages types definitions in this file.
     * </pre>
     */
    @ProtoField(order = FileDescriptorProto.MESSAGE_TYPE_FIELD_NUMBER, type = FieldType.OBJECT)
    public List<DescriptorProtoPOJO> messageTypes;

    /**
     * All top-level enumeration types  definitions in this file.
     */
    @ProtoField(order = FileDescriptorProto.ENUM_TYPE_FIELD_NUMBER, type = FieldType.OBJECT)
    public List<EnumDescriptorProtoPOJO> enumTypes;

    /**
     * All service  definitions in this file.
     */
    @ProtoField(order = FileDescriptorProto.SERVICE_FIELD_NUMBER, type = FieldType.OBJECT)
    public List<ServiceDescriptorProtoPOJO> services;

    /**
     * extensions definitions in this file.
     */
    @ProtoField(order = FileDescriptorProto.EXTENSION_FIELD_NUMBER, type = FieldType.OBJECT)
    public List<FieldDescriptorProtoPOJO> extensions;

    /**
     * optionsdefinitions in this file.
     */
    @ProtoField(order = FileDescriptorProto.OPTIONS_FIELD_NUMBER, type = FieldType.OBJECT)
    public FileOptionsPOJO options;

    /**
     * sourceCodeInfo
     */
    @ProtoField(order = FileDescriptorProto.SOURCE_CODE_INFO_FIELD_NUMBER, type = FieldType.OBJECT)
    public SourceCodeInfoPOJO sourceCodeInfo;

    /**
     * publicDependency
     */
    @ProtoField(order = FileDescriptorProto.PUBLIC_DEPENDENCY_FIELD_NUMBER, type = FieldType.INT32)
    public List<Integer> publicDependency;

    /**
     * weakDependency
     */
    @ProtoField(order = FileDescriptorProto.WEAK_DEPENDENCY_FIELD_NUMBER, type = FieldType.INT32)
    public List<Integer> weakDependency;

    @Override
    public String toString() {
        return "FileDescriptorProtoPOJO [name=" + name + ", pkg=" + pkg + ", dependencies=" + dependencies
                + ", messageTypes=" + messageTypes + ", enumTypes=" + enumTypes + ", services=" + services
                + ", extensions=" + extensions + ", options=" + options + ", sourceCodeInfo=" + sourceCodeInfo
                + ", publicDependency=" + publicDependency + ", weakDependency=" + weakDependency + "]";
    }


}
