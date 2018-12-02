package lee.fund.util.lang;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/11/29 10:56
 * Desc:
 */
public class ClassesUtils {
    private final static Logger logger = LoggerFactory.getLogger(ClassesUtils.class);

    private ClassesUtils() {
    }

    public static List<String> getClassListByPackage(String pkgName) {
        List<String> list = null;
        String regularPkgName = pkgName.replace(".", "/");
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(regularPkgName);
        if (url != null) {
            String type = url.getProtocol();
            if ("file".equals(type)) {
                String path = new File(url.getPath()).getPath();
                String rootPath = path.substring(0, path.length() - regularPkgName.length());
                list = getClassListByFile(rootPath, url.getPath());
            } else if ("jar".equals(type)) {
                list = getClassListByJar(url.getPath());
            }
        } else {
            list = getClassListByJars(((URLClassLoader) loader).getURLs(), regularPkgName);
        }
        return list;
    }

    private static List<String> getClassListByFile(String rootPath, String filePath) {
        File file = new File(filePath);
        List<String> list = Arrays.stream(file.listFiles()).filter(t -> (!t.isDirectory() && t.getPath().endsWith(".class"))).map(t -> {
            String childFilePath = t.getPath().substring(rootPath.length());
            childFilePath = childFilePath.substring(0, childFilePath.length() - 6).replace(File.separator, ".");
            return childFilePath;
        }).collect(Collectors.toList());
        return list;
    }

    private static List<String> getClassListByJars(URL[] urls, String pkgName) {
        List<String> list = new ArrayList<>();
        if (urls != null) {
            for (int i = 0; i < urls.length; i++) {
                URL url = urls[i];
                String urlPath = url.getPath();
                // 不必搜索文件夹
                if (urlPath.endsWith("/")) {
                    continue;
                }
                String jarPath = urlPath + "!/" + pkgName;
                list.addAll(getClassListByJar(jarPath));
            }
        }
        return list;
    }

    private static List<String> getClassListByJar(String jarPath) {
        List<String> list = new ArrayList<>();
        String[] jarInfo = jarPath.split("!");
        String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf("/"));
        String packagePath = jarInfo[1].substring(1);
        try (JarFile jarFile = new JarFile(URLDecoder.decode(jarFilePath, "utf-8"))) {
            Enumeration<JarEntry> entrys = jarFile.entries();
            while (entrys.hasMoreElements()) {
                JarEntry jarEntry = entrys.nextElement();
                String entryName = jarEntry.getName();
                if (entryName.endsWith(".class")) {
                    addClass(list, packagePath, entryName);
                }
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return list;
    }

    private static void addClass(List<String> list, String packagePath, String entryName) {
        int index = entryName.lastIndexOf("/");
        String myPackagePath;
        if (index != -1) {
            myPackagePath = entryName.substring(0, index);
        } else {
            myPackagePath = entryName;
        }
        if (myPackagePath.equals(packagePath)) {
            String name = entryName.replace("/", ".").substring(0, entryName.lastIndexOf("."));
            list.add(name);
        }
    }
}
