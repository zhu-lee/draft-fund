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

import com.google.protobuf.DescriptorProtos.FileOptions;
import lee.fund.util.lang.EnumValueSupport;

/**
 * Enumeration mode for optimize defines at {@link FileOptions}
 *
 * @author xiemalin
 * @since 2.0.1
 */
public enum OptimizeMode implements EnumValueSupport {
    SPEED(1), CODE_SIZE(2), LITE_RUNTIME(3);

    private int value;

    OptimizeMode(int value) {
        this.value = value;
    }

    @Override
    public int value() {
        return value;
    }
}
