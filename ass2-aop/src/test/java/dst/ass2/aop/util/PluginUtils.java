package dst.ass2.aop.util;

import dst.ass2.aop.IPluginExecutable;
import dst.ass2.aop.event.EventBusHandler;
import org.apache.commons.io.FileUtils;
import org.aspectj.weaver.internal.tools.PointcutExpressionImpl;
import org.aspectj.weaver.patterns.Pointcut;
import org.springframework.aop.Advisor;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.aspectj.AbstractAspectJAdvice;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.logging.Logger;

import static org.apache.commons.io.filefilter.FileFileFilter.INSTANCE;
import static org.apache.commons.io.filefilter.FileFilterUtils.*;
import static org.springframework.util.ReflectionUtils.*;

/**
 * Contains some utility methods for plugins.
 */
public final class PluginUtils {

    public final static int PLUGIN_TEST_TIMEOUT = 30000;

    public static final File PLUGINS_DIR = new File(
        FileUtils.getTempDirectoryPath(), "plugins_"
        + System.currentTimeMillis());

    public static File ALL_FILE;
    public static File SIMPLE_FILE;

    public static final Method EXECUTE_METHOD = findMethod(
        IPluginExecutable.class, "execute");
    public static final Method INTERRUPTED_METHOD = findMethod(
        IPluginExecutable.class, "interrupted");

    static {
        try {
            ALL_FILE = new ClassPathResource("all.zip").getFile();
            SIMPLE_FILE = new ClassPathResource("simple.zip").getFile();
        } catch (IOException e) {
            throw new RuntimeException("Cannot locate plugin in classpath", e);
        }
    }

    private PluginUtils() {
    }

    /**
     * Creates a new unique {@link File} object within the {@link #PLUGINS_DIR}
     * directory.
     *
     * @return the file
     */
    public static File uniqueFile() {
        return new File(PLUGINS_DIR, "_" + System.nanoTime() + ".jar");
    }

    /**
     * Copies the given file to a file in the plugin directory.<br/>
     *
     * @throws IOException if the destination file already exists or the file was not copied
     * @see #uniqueFile()
     */
    public static void preparePlugin(File file) throws IOException {
        File destFile = uniqueFile();
        if (destFile.exists()) {
            throw new IOException("Destination file must not exist.");
        }

        File tempFile = new File(destFile.getParentFile(), "tmp_"
            + UUID.randomUUID().toString());
        FileUtils.copyFile(file, tempFile);
        if (!tempFile.renameTo(destFile) || !destFile.isFile()) {
            throw new IOException(String.format(
                "File '%s' was not copied to '%s'.", file, destFile));
        }
    }

    /**
     * Deletes all plugin JARs copied to the plugin directory.
     */
    public static void cleanPluginDirectory() {
        FileFilter filter = and(INSTANCE,
            or(prefixFileFilter("_"), prefixFileFilter("tmp_")));
        System.gc();

        for (File file : PLUGINS_DIR.listFiles(filter)) {
            file.delete();
        }
    }

    /**
     * Ads a new {@link EventBusHandler} to the logger declared within the given
     * objects class if necessary.<br/>
     * This method does nothing if the logger already has an
     * {@link EventBusHandler} or there is no logger.
     *
     * @param obj the object
     */
    public static void addBusHandlerIfNecessary(Object obj) {
        Class<?> targetClass = AopUtils.getTargetClass(obj);
        Field field = findField(targetClass, null, Logger.class);
        if (field != null) {
            makeAccessible(field);
            Logger logger = (Logger) getField(field, obj);
            for (Handler handler : logger.getHandlers()) {
                if (handler instanceof EventBusHandler) {
                    return;
                }
            }
            logger.addHandler(new EventBusHandler());
        }
    }

    /**
     * Creates a new instance of the given {@link IPluginExecutable} class and
     * returns a proxy with the AspectJ aspect applied to it.<br/>
     * If {@code aspectClass} is {@code null}, no aspect is applied.
     *
     * @param clazz       the plugin class
     * @param aspectClass the class containing AspectJ definitions
     * @return proxy of the plugin instance
     */
    public static IPluginExecutable getExecutable(
        Class<? extends IPluginExecutable> clazz, Class<?> aspectClass) {
        IPluginExecutable target = BeanUtils.instantiateClass(clazz);
        AspectJProxyFactory factory = new AspectJProxyFactory(target);
        factory.setProxyTargetClass(true);
        if (aspectClass != null) {
            factory.addAspect(BeanUtils.instantiateClass(aspectClass));
        }
        return factory.getProxy();
    }

    /**
     * Returns the pointcut expression of the given advised proxy.
     *
     * @param advised the proxy with the applied aspect
     * @return the pointcut expression or {@code null} if none was found
     */
    public static PointcutExpressionImpl getPointcutExpression(Advised advised) {
        PointcutAdvisor pointcutAdvisor = getPointcutAdvisor(advised);
        if (pointcutAdvisor != null) {
            AspectJExpressionPointcut pointcut = (AspectJExpressionPointcut) pointcutAdvisor
                .getPointcut();
            if (pointcut.getPointcutExpression() instanceof PointcutExpressionImpl) {
                return (PointcutExpressionImpl) pointcut
                    .getPointcutExpression();
            }
        }
        return null;
    }

    /**
     * Returns the pointcut advisor of the given proxy if its advice part is an
     * {@link AbstractAspectJAdvice}.
     *
     * @param advised the proxy with the applied aspect
     * @return the pointcut advisor or {@code null} if there is no AspectJ pointcut advisor applied
     */
    public static PointcutAdvisor getPointcutAdvisor(Advised advised) {
        for (Advisor advisor : advised.getAdvisors()) {
            if (advisor instanceof PointcutAdvisor
                && advisor.getAdvice() instanceof AbstractAspectJAdvice) {
                return (PointcutAdvisor) advisor;
            }
        }
        return null;
    }

    /**
     * Attempts to resolve all parts of the pointcut expression of the aspect
     * applied to the given proxy.
     *
     * @param advised the proxy with the applied aspect
     * @return a string representation of this pointcut expression
     * @see #getPointcutExpression(org.springframework.aop.framework.Advised)
     * @see #getPointcutAdvisor(org.springframework.aop.framework.Advised)
     */
    public static String getBestExpression(Advised advised) {
        PointcutExpressionImpl pointcutExpression = getPointcutExpression(advised);
        if (pointcutExpression != null) {
            Pointcut underlyingPointcut = pointcutExpression
                .getUnderlyingPointcut();
            if (findMethod(underlyingPointcut.getClass(), "toString")
                .getDeclaringClass() != Object.class) {
                return underlyingPointcut.toString();
            }
            return pointcutExpression.getPointcutExpression();
        }
        PointcutAdvisor advisor = getPointcutAdvisor(advised);
        AspectJExpressionPointcut pointcut = (AspectJExpressionPointcut) advisor
            .getPointcut();
        return pointcut.getExpression();
    }

    /**
     * Finds all public methods of the given class annotated with a certain
     * annotation.
     *
     * @param clazz          the class
     * @param annotationType the annotation to search for
     * @return methods annotated with the given annotation
     */
    public static <A extends Annotation> Map<Method, A> findMethodAnnotation(
        Class<?> clazz, Class<A> annotationType) {
        Map<Method, A> map = new HashMap<Method, A>();
        for (Method method : clazz.getDeclaredMethods()) {
            A annotation = AnnotationUtils.findAnnotation(method,
                annotationType);
            if (annotation != null) {
                map.put(method, annotation);
            }
        }
        return map;
    }
}
