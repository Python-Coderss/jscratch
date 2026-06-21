package com.jscratch.compiler;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.*;

/**
 * Exports the project as a standalone executable JAR file.
 * Only includes the core engine classes required for runtime.
 */
public class JarExporter {
    
    // Core engine classes that don't depend on external libraries (GSON, JDT, etc.)
    private static final Set<String> CORE_ENGINE_CLASSES = new HashSet<>();
    static {
        CORE_ENGINE_CLASSES.add("com/jscratch/Sprite");
        CORE_ENGINE_CLASSES.add("com/jscratch/Stage");
        CORE_ENGINE_CLASSES.add("com/jscratch/Costume");
        CORE_ENGINE_CLASSES.add("com/jscratch/ScratchObject");
        CORE_ENGINE_CLASSES.add("com/jscratch/utils/ReflectionUtils");
    }

    /**
     * Exports the project to a JAR.
     */
    public static void export(File targetJar, File compiledClassesDir, File sourceDir, File assetsDir, File engineClassesDir) throws IOException {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, "generated.Main");
        
        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(targetJar), manifest)) {
            // 1. Add Filtered Engine Classes
            if (engineClassesDir != null && engineClassesDir.exists()) {
                addFilteredDirectoryToJar(engineClassesDir.toPath(), "", jos, path -> {
                    String name = path.replace(".class", "");
                    // Check if it's one of our core classes or an inner class of one
                    for (String core : CORE_ENGINE_CLASSES) {
                        if (name.equals(core) || name.startsWith(core + "$")) {
                            return true;
                        }
                    }
                    return false;
                });
            }
            
            // 2. Add Generated Classes (All of them)
            if (compiledClassesDir != null && compiledClassesDir.exists()) {
                addDirectoryToJar(compiledClassesDir.toPath(), "", jos);
            }
            
            // 3. Add Source Code
            if (sourceDir != null && sourceDir.exists()) {
                addDirectoryToJar(sourceDir.toPath(), "src/", jos);
            }
            
            // 4. Add Assets
            if (assetsDir != null && assetsDir.exists()) {
                addDirectoryToJar(assetsDir.toPath(), "jscratch_ws/assets/", jos);
            }
        }
    }

    private static void addDirectoryToJar(Path rootSource, String jarPrefix, JarOutputStream jos) throws IOException {
        addFilteredDirectoryToJar(rootSource, jarPrefix, jos, p -> true);
    }

    private static void addFilteredDirectoryToJar(Path rootSource, String jarPrefix, JarOutputStream jos, java.util.function.Predicate<String> filter) throws IOException {
        Files.walkFileTree(rootSource, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String relativePath = rootSource.relativize(file).toString().replace("\\", "/");
                
                if (!filter.test(relativePath)) {
                    return FileVisitResult.CONTINUE;
                }

                String entryName = jarPrefix + relativePath;
                if (entryName.equalsIgnoreCase("META-INF/MANIFEST.MF")) {
                    return FileVisitResult.CONTINUE;
                }
                
                JarEntry entry = new JarEntry(entryName);
                entry.setTime(attrs.lastModifiedTime().toMillis());
                jos.putNextEntry(entry);
                Files.copy(file, jos);
                jos.closeEntry();
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                // Directories don't strictly need entries but it's cleaner
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
