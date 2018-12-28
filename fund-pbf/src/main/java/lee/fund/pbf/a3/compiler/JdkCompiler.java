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

import lee.fund.pbf.a3.utils.ClassHelper;
import lee.fund.util.lang.Exceptions;
import lee.fund.util.lang.UncheckedException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.*;
import javax.tools.JavaFileObject.Kind;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.*;

/**
 * JdkCompiler. (SPI, Singleton, ThreadSafe)
 *
 * @author xiemalin
 * @since 1.0.0
 */
public class JdkCompiler extends AbstractCompiler {

    /**
     * Logger for this class
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(JdkCompiler.class);

    private final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

    private final ClassLoaderImpl classLoader;

    private final JavaFileManagerImpl javaFileManager;

    private volatile List<String> options;

    public JdkCompiler(final ClassLoader loader) {
        options = new ArrayList<String>();
        if (compiler == null) {
            throw new UncheckedException(
                    "compiler is null maybe you are on JRE enviroment please change to JDK enviroment.");
        }
        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<JavaFileObject>();
        StandardJavaFileManager manager = compiler.getStandardFileManager(diagnosticCollector, null, null);
        if (loader instanceof URLClassLoader && (!"sun.misc.Launcher$AppClassLoader".equals(loader.getClass().getName()))) {
            try {
                URLClassLoader urlClassLoader = (URLClassLoader) loader;
                List<File> files = new ArrayList<File>();
                for (URL url : urlClassLoader.getURLs()) {

                    String file = url.getFile();
                    if (StringUtils.endsWith(file, "!/")) {
                        file = StringUtils.removeEnd(file, "!/");
                    }
                    if (file.startsWith("file:")) {
                        file = StringUtils.removeStart(file, "file:");
                    }

                    files.add(new File(file));
                }
                manager.setLocation(StandardLocation.CLASS_PATH, files);
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        }
        classLoader = AccessController.doPrivileged(new PrivilegedAction<ClassLoaderImpl>() {
            public ClassLoaderImpl run() {
                return new ClassLoaderImpl(loader);
            }
        });
        javaFileManager = new JavaFileManagerImpl(manager, classLoader);
    }

    @Override
    public synchronized Class<?> doCompile(String name, String sourceCode, OutputStream os) {
        LOGGER.debug("Begin to compile source code: class is '" + name + "'");

        int i = name.lastIndexOf('.');
        String packageName = i < 0 ? "" : name.substring(0, i);
        String className = i < 0 ? name : name.substring(i + 1);
        JavaFileObjectImpl javaFileObject = new JavaFileObjectImpl(className, sourceCode);
        javaFileManager.putFileForInput(StandardLocation.SOURCE_PATH, packageName, className
                + ClassUtils.JAVA_EXTENSION, javaFileObject);

        DiagnosticCollector<JavaFileObject> diagnosticCollector = new DiagnosticCollector<JavaFileObject>();
        Boolean result =
                compiler.getTask(null, javaFileManager, diagnosticCollector, options, null,
                        Arrays.asList(new JavaFileObject[]{javaFileObject})).call();
        if (result == null || !result) {
            throw new IllegalStateException("Compilation failed. class: " + name + ", diagnostics: "
                    + diagnosticCollector.getDiagnostics());
        }

        LOGGER.debug("compile source code done: class is '" + name + "'");

        LOGGER.debug("loading class '" + name + "'");
        try {
            Class<?> retClass = classLoader.loadClass(name);
            LOGGER.debug("loading class done  '" + name + "'");

            if (os != null) {
                byte[] bytes = classLoader.loadClassBytes(name);
                if (bytes != null) {
                    os.write(bytes);
                    os.flush();
                }
            }
            return retClass;
        } catch (Exception e) {
            throw Exceptions.asUnchecked(e);
        }
    }

    @Override
    public byte[] loadBytes(String className) {
        byte[] bytes = classLoader.loadClassBytes(className);
        return bytes;
    }

    private final class ClassLoaderImpl extends ClassLoader {

        private final Map<String, JavaFileObject> classes = new HashMap<String, JavaFileObject>();

        ClassLoaderImpl(final ClassLoader parentClassLoader) {
            super(parentClassLoader);
        }

        Collection<JavaFileObject> files() {
            return Collections.unmodifiableCollection(classes.values());
        }

        public byte[] loadClassBytes(final String qualifiedClassName) {
            JavaFileObject file = classes.get(qualifiedClassName);
            if (file != null) {
                byte[] bytes = ((JavaFileObjectImpl) file).getByteCode();
                return bytes;
            }
            return null;
        }

        @Override
        protected Class<?> findClass(final String qualifiedClassName) throws ClassNotFoundException {
            JavaFileObject file = classes.get(qualifiedClassName);
            if (file != null) {
                byte[] bytes = ((JavaFileObjectImpl) file).getByteCode();
                return defineClass(qualifiedClassName, bytes, 0, bytes.length);
            }
            try {
                return ClassHelper.forNameWithCallerClassLoader(qualifiedClassName, getClass());
            } catch (ClassNotFoundException nf) {
                return super.findClass(qualifiedClassName);
            }
        }

        void add(final String qualifiedClassName, final JavaFileObject javaFile) {
            classes.put(qualifiedClassName, javaFile);
        }

        @Override
        protected synchronized Class<?> loadClass(final String name, final boolean resolve)
                throws ClassNotFoundException {
            return super.loadClass(name, resolve);
        }

        @Override
        public InputStream getResourceAsStream(final String name) {
            if (name.endsWith(ClassUtils.CLASS_EXTENSION)) {
                String qualifiedClassName =
                        name.substring(0, name.length() - ClassUtils.CLASS_EXTENSION.length()).replace('/', '.');
                JavaFileObjectImpl file = (JavaFileObjectImpl) classes.get(qualifiedClassName);
                if (file != null) {
                    return new ByteArrayInputStream(file.getByteCode());
                }
            }
            return super.getResourceAsStream(name);
        }
    }

    private static final class JavaFileObjectImpl extends SimpleJavaFileObject {

        private ByteArrayOutputStream bytecode;

        private final CharSequence source;

        public JavaFileObjectImpl(final String baseName, final CharSequence source) {
            super(ClassUtils.toURI(baseName + ClassUtils.JAVA_EXTENSION), Kind.SOURCE);
            this.source = source;
        }

        JavaFileObjectImpl(final String name, final Kind kind) {
            super(ClassUtils.toURI(name), kind);
            source = null;
        }

        public JavaFileObjectImpl(URI uri, Kind kind) {
            super(uri, kind);
            source = null;
        }

        @Override
        public CharSequence getCharContent(final boolean ignoreEncodingErrors) throws UnsupportedOperationException {
            if (source == null) {
                throw new UnsupportedOperationException("source == null");
            }
            return source;
        }

        @Override
        public InputStream openInputStream() {
            return new ByteArrayInputStream(getByteCode());
        }

        @Override
        public OutputStream openOutputStream() {
            return bytecode = new ByteArrayOutputStream();
        }

        public byte[] getByteCode() {
            if (bytecode == null) {
                return null;
            }
            return bytecode.toByteArray();
        }
    }

    private static final class JavaFileManagerImpl extends ForwardingJavaFileManager<JavaFileManager> {

        private final ClassLoaderImpl classLoader;

        private final Map<URI, JavaFileObject> fileObjects = new HashMap<URI, JavaFileObject>();

        public JavaFileManagerImpl(JavaFileManager fileManager, ClassLoaderImpl classLoader) {
            super(fileManager);
            this.classLoader = classLoader;
        }

        @Override
        public FileObject getFileForInput(Location location, String packageName, String relativeName)
                throws IOException {
            FileObject o = fileObjects.get(uri(location, packageName, relativeName));
            if (o != null) {
                return o;
            }
            return super.getFileForInput(location, packageName, relativeName);
        }

        public void putFileForInput(StandardLocation location, String packageName, String relativeName,
                                    JavaFileObject file) {
            fileObjects.put(uri(location, packageName, relativeName), file);
        }

        private URI uri(Location location, String packageName, String relativeName) {
            return ClassUtils.toURI(location.getName() + '/' + packageName + '/' + relativeName);
        }

        @Override
        public JavaFileObject getJavaFileForOutput(Location location, String qualifiedName, Kind kind,
                                                   FileObject outputFile) throws IOException {
            JavaFileObject file = new JavaFileObjectImpl(qualifiedName, kind);
            classLoader.add(qualifiedName, file);
            return file;
        }

        @Override
        public ClassLoader getClassLoader(Location location) {
            return classLoader;
        }

        @Override
        public String inferBinaryName(Location loc, JavaFileObject file) {
            if (file instanceof JavaFileObjectImpl) {
                return file.getName();
            }
            return super.inferBinaryName(loc, file);
        }

        @Override
        public Iterable<JavaFileObject> list(Location location, String packageName, Set<Kind> kinds, boolean recurse)
                throws IOException {
            Iterable<JavaFileObject> result = super.list(location, packageName, kinds, recurse);

            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
            List<URL> urlList = new ArrayList<URL>();
            Enumeration<URL> e = contextClassLoader.getResources("com");
            while (e.hasMoreElements()) {
                urlList.add(e.nextElement());
            }

            ArrayList<JavaFileObject> files = new ArrayList<JavaFileObject>();

            if (location == StandardLocation.CLASS_PATH && kinds.contains(Kind.CLASS)) {
                for (JavaFileObject file : fileObjects.values()) {
                    if (file.getKind() == Kind.CLASS && file.getName().startsWith(packageName)) {
                        files.add(file);
                    }
                }

                files.addAll(classLoader.files());
            } else if (location == StandardLocation.SOURCE_PATH && kinds.contains(Kind.SOURCE)) {
                for (JavaFileObject file : fileObjects.values()) {
                    if (file.getKind() == Kind.SOURCE && file.getName().startsWith(packageName)) {
                        files.add(file);
                    }
                }
            }

            for (JavaFileObject file : result) {
                files.add(file);
            }

            return files;
        }
    }


}
