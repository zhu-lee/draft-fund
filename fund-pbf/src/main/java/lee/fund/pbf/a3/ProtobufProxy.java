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
package lee.fund.pbf.a3;

import lee.fund.pbf.a3.utils.*;
import lee.fund.util.lang.UncheckedException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Proxy tools for protobuf.
 *
 * @author xiemalin
 * @since 1.0.0
 */
@Deprecated
public final class ProtobufProxy {

    public static final ThreadLocal<Boolean> DEBUG_CONTROLLER = new ThreadLocal<Boolean>();

    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ProtobufProxy.class);

    /**
     * cached {@link Codec} instance by class full name.
     */
    private static final Map<String, Codec> CACHED = new ConcurrentHashMap<String, Codec>();

    /**
     * To generate a protobuf proxy java source code for target class.
     *
     * @param os      to generate java source code
     * @param cls     target class
     * @param charset charset type
     * @throws IOException in case of any io relative exception.
     */
    public static void dynamicCodeGenerate(OutputStream os, Class cls, Charset charset) throws IOException {
        if (cls == null) {
            throw new NullPointerException("Parameter cls is null");
        }
        if (os == null) {
            throw new NullPointerException("Parameter os is null");
        }

        CodeGenerator cg = getCodeGenerator(cls);
        String code = cg.getCode();

        os.write(code.getBytes(charset == null ? Charset.defaultCharset() : charset));
    }

    private static CodeGenerator getCodeGenerator(Class cls) {
        // check if has default constructor

        if (!cls.isMemberClass()) {
            try {
                cls.getConstructor(new Class<?>[0]);
            } catch (NoSuchMethodException e2) {
                throw new IllegalArgumentException(
                        "Class '" + cls.getName() + "' must has default constructor method with no parameters.", e2);
            } catch (SecurityException e2) {
                throw new IllegalArgumentException(e2.getMessage(), e2);
            }
        }

        List<Field> fields = FieldUtils.findMatchedFields(cls, ProtoField.class);
        if (fields.isEmpty()) {
            throw new IllegalArgumentException("Invalid class [" + cls.getName() + "] no field use annotation @"
                    + ProtoField.class.getName() + " at class " + cls.getName());
        }

        List<FieldInfo> fieldInfos = ProtobufProxyUtils.processDefaultValue(cls, fields, true);
        CodeGenerator cg = new CodeGenerator(fieldInfos, cls);

        return cg;
    }

    /**
     * To create a protobuf proxy class for target class.
     *
     * @param <T>
     * @param cls
     * @return
     */
    public static <T> Codec<T> create(Class<T> cls) {
        Boolean debug = DEBUG_CONTROLLER.get();
        if (debug == null) {
            debug = false;
        }

        return create(cls, debug, null);
    }

    /**
     * @param cls        target class to be compiled
     * @param outputPath compile byte files output stream
     */
    public static void compile(Class<?> cls, File outputPath) {
        if (outputPath == null) {
            throw new NullPointerException("Param 'outputPath' is null.");
        }
        if (!outputPath.isDirectory()) {
            throw new UncheckedException("Param 'outputPath' value should be a path directory.");
        }

    }

    public static <T> Codec<T> create(Class<T> cls, boolean debug) {
        return create(cls, debug, null);
    }

    /**
     * To create a protobuf proxy class for target class.
     *
     * @param <T>   target object type to be proxied.
     * @param cls   target object class
     * @param debug true will print generate java source code
     * @return proxy instance object.
     */
    public static <T> Codec<T> create(Class<T> cls, boolean debug, File path) {
        DEBUG_CONTROLLER.set(debug);
        try {
            return doCreate(cls, debug, path);
        } finally {
            DEBUG_CONTROLLER.remove();
        }

    }

    /**
     * To create a protobuf proxy class for target class.
     *
     * @param <T>   target object type to be proxied.
     * @param cls   target object class
     * @param debug true will print generate java source code
     * @return proxy instance object.
     */
    private static <T> Codec<T> doCreate(Class<T> cls, boolean debug, File path) {
        if (cls == null) {
            throw new NullPointerException("Parameter cls is null");
        }
        if (path != null) {
            if (!path.isDirectory()) {
                throw new UncheckedException("Param 'path' value should be a path directory.");
            }
        }

        // get last modify time
        long lastModify = ClassHelper.getLastModifyTime(cls);

        String uniClsName = cls.getName();
        Codec codec = CACHED.get(uniClsName);
        if (codec != null) {
            return codec;
        }

        CodeGenerator cg = getCodeGenerator(cls);
        cg.setDebug(debug);
        cg.setOutputPath(path);

        // try to load first
        String className = cg.getFullClassName();
        Class<?> c = null;
        try {
            c = Class.forName(className);
        } catch (ClassNotFoundException e1) {
            // if class not found so should generate a new java source class.
            c = null;
        }

        if (c != null) {
            try {
                Codec<T> newInstance = (Codec<T>) c.newInstance();
                return newInstance;
            } catch (InstantiationException e) {
                throw new UncheckedException(e);
            } catch (IllegalAccessException e) {
                throw new UncheckedException(e);
            }
        }

        String code = cg.getCode();
        if (debug) {
            CodePrinter.printCode(code, "generate protobuf proxy code");
        }

        FileOutputStream fos = null;
        if (path != null) {
            String pkg = "";
            if (className.indexOf('.') != -1) {
                pkg = StringUtils.substringBeforeLast(className, ".");
            }

            // mkdirs
            String dir = path + File.separator + pkg.replace('.', File.separatorChar);
            File f = new File(dir);
            f.mkdirs();

            try {
                fos = new FileOutputStream(new File(f, cg.getClassName() + ".class"));
            } catch (Exception e) {
                throw new UncheckedException(e);
            }

        }

        Class<?> newClass =
                JDKCompilerHelper.getJdkCompiler().compile(className, code, cls.getClassLoader(), fos, lastModify);

        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {
                throw new UncheckedException(e);
            }
        }

        try {
            Codec<T> newInstance = (Codec<T>) newClass.newInstance();
            if (!CACHED.containsKey(uniClsName)) {
                CACHED.put(uniClsName, newInstance);
            }

            try {
                // try to eagle load
                Set<Class<?>> relativeProxyClasses = cg.getRelativeProxyClasses();
                for (Class<?> relativeClass : relativeProxyClasses) {
                    ProtobufProxy.create(relativeClass, debug, path);
                }
            } catch (Exception e) {
                LOGGER.error("{}", e);
            }

            return newInstance;
        } catch (InstantiationException e) {
            throw new UncheckedException(e);
        } catch (IllegalAccessException e) {
            throw new UncheckedException(e);
        }
    }

    public static void clearCache() {
        CACHED.clear();
    }

}
