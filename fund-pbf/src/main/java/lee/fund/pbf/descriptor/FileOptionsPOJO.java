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

import com.google.protobuf.DescriptorProtos.FileOptions;
import lee.fund.pbf.a3.FieldType;
import lee.fund.pbf.a3.ProtoField;

import java.util.List;

/**
 * JProtobuf supports for {@link FileOptions}
 *
 * @author xiemalin
 * @since 2.0.1
 */
public class FileOptionsPOJO {

    @ProtoField(order = FileOptions.JAVA_PACKAGE_FIELD_NUMBER)
    public String javaPackage;

    @ProtoField(order = FileOptions.JAVA_OUTER_CLASSNAME_FIELD_NUMBER)
    public String javaOuterClassname;

    @ProtoField(order = FileOptions.OPTIMIZE_FOR_FIELD_NUMBER, type = FieldType.ENUM)
    public OptimizeMode optimizeFor;

    @ProtoField(order = FileOptions.JAVA_MULTIPLE_FILES_FIELD_NUMBER)
    public Boolean javaMultipleFiles;

    @ProtoField(order = FileOptions.GO_PACKAGE_FIELD_NUMBER)
    public String goPackage;

    @ProtoField(order = FileOptions.CC_GENERIC_SERVICES_FIELD_NUMBER)
    public Boolean ccGenericServices;

    @ProtoField(order = FileOptions.JAVA_GENERIC_SERVICES_FIELD_NUMBER)
    public Boolean javaGenericServices;

    @ProtoField(order = FileOptions.PY_GENERIC_SERVICES_FIELD_NUMBER)
    public Boolean pyGenericServices;

    @ProtoField(order = FileOptions.JAVA_GENERATE_EQUALS_AND_HASH_FIELD_NUMBER)
    public Boolean javaGenerateEqualsAndHash;

    @ProtoField(order = FileOptions.UNINTERPRETED_OPTION_FIELD_NUMBER, type = FieldType.OBJECT)
    public List<UninterpretedOptionPOJO> uninterpretedOptions;
}

