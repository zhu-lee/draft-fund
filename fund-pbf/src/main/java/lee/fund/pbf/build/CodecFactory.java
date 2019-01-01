package lee.fund.pbf.build;

import lee.fund.pbf.a3.Codec;
import lee.fund.pbf.utils.CodecUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Author: zhu.li
 * Since:  jdk 1.8
 * Date:   Created in 2018/12/23 19:26
 * Desc:
 */
public final class CodecFactory {
    private static final Logger logger = LoggerFactory.getLogger(CodecFactory.class);
    private static final ConcurrentMap<String, Codec> CODEC_MAP = new ConcurrentHashMap<>();
    private static final CodecClassLoader LOADER = new CodecClassLoader(Thread.currentThread().getContextClassLoader());

    public static <T> Codec get(Class<T> cls) {
        if (cls.isPrimitive() || cls.isEnum() || cls.isAnnotation() || cls.isInterface()) {
            throw new IllegalArgumentException("can not create codec for class: " + cls.getName());
        }
        return CODEC_MAP.computeIfAbsent(cls.getName(), k -> {
            LOADER.setCls(cls);
            String codecTypeName = CodecUtil.getCodecTypeName(cls);
            try {
                long start = System.currentTimeMillis();
                Class<?> codecClass = LOADER.loadClass(codecTypeName);
                logger.debug("build codec class: {}, time: {} ms", codecTypeName, System.currentTimeMillis() - start);
                Codec codec = (Codec) codecClass.newInstance();
                return codec;
            } catch (Throwable e) {
                logger.error("create codec for class:{},error", codecTypeName);
                throw new RuntimeException(e);
            }
        });
    }
}