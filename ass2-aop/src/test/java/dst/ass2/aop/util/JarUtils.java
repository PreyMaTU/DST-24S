package dst.ass2.aop.util;

import org.apache.commons.io.input.AutoCloseInputStream;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipOutputStream;

import static org.apache.commons.io.FileUtils.openOutputStream;
import static org.apache.commons.io.IOUtils.copy;
import static org.apache.commons.lang3.StringUtils.defaultIfBlank;
import static org.apache.commons.lang3.StringUtils.join;
import static org.springframework.util.ClassUtils.CLASS_FILE_SUFFIX;
import static org.springframework.util.ClassUtils.convertClassNameToResourcePath;

/**
 * Builds plugin JARs on demand.
 * <p>
 * This class is for internal purposes only.
 * Note that the {@link #main(String...)} method can be adjusted to create other plugins.
 */
public final class JarUtils {
    private JarUtils() {
    }

    public static void main(String... args) throws IOException {
        String path = join(args, " ");
        File dir = new File(defaultIfBlank(path, "ass2-aop/src/test/resources"));

        createJar(new File(dir, "simple.zip"),
            "dst.ass2.aop.sample.SimplePluginExecutable"
        );

        createJar(new File(dir, "all.zip"),
            "dst.ass2.aop.sample.SimplePluginExecutable",
            "dst.ass2.aop.sample.IgnoredPluginExecutable"
        );
    }

    /**
     * Creates a new JAR file containing the given classes.
     *
     * @param jarFile the destination JAR file
     * @param classes the classes to add
     * @throws IOException if an I/O error has occurred
     */
    public static void createJar(File jarFile, String... classes) throws IOException {
        try (JarOutputStream stream = new JarOutputStream(openOutputStream(jarFile))) {
            stream.setLevel(ZipOutputStream.STORED);
            for (String clazz : classes) {
                String path = convertClassNameToResourcePath(clazz) + CLASS_FILE_SUFFIX;
                stream.putNextEntry(new JarEntry(path));
                copy(new AutoCloseInputStream(new ClassPathResource(path).getInputStream()), stream);
            }
        }
    }
}
