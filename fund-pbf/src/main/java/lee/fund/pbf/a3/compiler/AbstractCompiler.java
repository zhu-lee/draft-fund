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
package lee.fund.pbf.a3.compiler;

import java.io.OutputStream;

/**
 * Abstract compiler. (SPI, Prototype, ThreadSafe)
 * 
 * @author xiemalin
 * @since 1.0.0
 */
public abstract class AbstractCompiler implements Compiler {

    public Class<?> compile(String className, String code, ClassLoader classLoader, OutputStream os, long lastModify) {
        String formattedCode = code.trim();
        try {
            return Class.forName(className, true, classLoader);
        } catch (ClassNotFoundException e) {
            if (!formattedCode.endsWith("}")) {
                throw new IllegalStateException("The java code not endsWith \"}\", code: \n" + formattedCode + "\n");
            }
            try {
                return doCompile(className, formattedCode, os);
            } catch (RuntimeException t) {
                throw t;
            } catch (Exception t) {
                throw new IllegalStateException("Failed to compile class, cause: " + t.getMessage() + ", class: "
                        + className + ", code: \n" + formattedCode + "\n, stack: " + ClassUtils.toString(t));
            }
        }
    }

    protected abstract Class<?> doCompile(String name, String source, OutputStream os);

}
