package com.codingapi.springboot.framework.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * ClassLoaderUtils 单元测试
 */
class ClassLoaderUtilsTest {

    @TempDir
    Path tempDir;

    @Test
    void createClassLoaderThrowsWhenJarNotFound() {
        assertThrows(RuntimeException.class, () -> ClassLoaderUtils.createClassLoader("/no/such/file.jar"));
    }

    @Test
    void findAllClassesInDirectory() throws Exception {
        // 构造目录结构: root/com/demo/A.class, root/com/demo/sub/B.class
        File root = tempDir.toFile();
        File pkg = new File(root, "com/demo/sub");
        assertTrue(pkg.mkdirs());
        assertTrue(new File(pkg.getParentFile(), "A.class").createNewFile());
        assertTrue(new File(pkg, "B.class").createNewFile());
        // 非 class 文件应被忽略
        assertTrue(new File(pkg, "readme.txt").createNewFile());

        URLClassLoader classLoader = new URLClassLoader(new java.net.URL[]{root.toURI().toURL()});
        List<String> classes = ClassLoaderUtils.findAllClasses(classLoader);

        assertTrue(classes.contains("com.demo.A"));
        assertTrue(classes.contains("com.demo.sub.B"));
        assertEquals(2, classes.size());
        classLoader.close();
    }

    @Test
    void findAllClassesInJar() throws Exception {
        File jarFile = tempDir.resolve("demo.jar").toFile();
        try (JarOutputStream jar = new JarOutputStream(new FileOutputStream(jarFile))) {
            jar.putNextEntry(new JarEntry("com/demo/Inner.class"));
            jar.write(new byte[]{(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE});
            jar.closeEntry();
            // 非 class 条目应被忽略
            jar.putNextEntry(new JarEntry("META-INF/MANIFEST.MF"));
            jar.write("Manifest-Version: 1.0".getBytes());
            jar.closeEntry();
        }

        URLClassLoader classLoader = ClassLoaderUtils.createClassLoader(jarFile.getAbsolutePath());
        List<String> classes = ClassLoaderUtils.findAllClasses(classLoader);

        assertEquals(1, classes.size());
        assertEquals("com.demo.Inner", classes.get(0));
        classLoader.close();
    }

    @Test
    void findJarClassesLoadsRealClasses() throws Exception {
        // 将当前测试类所在的 classes 目录打包成 jar, 验证类可被真实加载
        String classFilePath = ClassLoaderUtilsTest.class.getName().replace('.', '/') + ".class";
        java.net.URL classUrl = ClassLoaderUtilsTest.class.getClassLoader().getResource(classFilePath);
        assertFalse(classUrl == null);

        File jarFile = tempDir.resolve("real.jar").toFile();
        try (JarOutputStream jar = new JarOutputStream(new FileOutputStream(jarFile))) {
            jar.putNextEntry(new JarEntry(classFilePath));
            try (java.io.InputStream in = classUrl.openStream()) {
                byte[] buffer = new byte[4096];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    jar.write(buffer, 0, len);
                }
            }
            jar.closeEntry();
        }

        List<Class<?>> classes = ClassLoaderUtils.findJarClasses(jarFile.getAbsolutePath());
        assertFalse(classes.isEmpty());

        List<Class<?>> filtered = ClassLoaderUtils.findJarClass(jarFile.getAbsolutePath(), Object.class);
        assertFalse(filtered.isEmpty());

        // 指定不可能匹配的接口类型时返回空
        List<Class<?>> none = ClassLoaderUtils.findJarClass(jarFile.getAbsolutePath(), Runnable.class);
        assertTrue(none.isEmpty() || none.stream().allMatch(Runnable.class::isAssignableFrom));
    }
}
