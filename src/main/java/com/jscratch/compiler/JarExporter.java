package com.jscratch.compiler;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
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
        // Entry point that opens the stage window and invokes generated.Main
        CORE_ENGINE_CLASSES.add("com/jscratch/GameBootstrap");
    }

    // Extra engine packages to bundle whole into exported games.
    // Intentionally empty: the core 2D engine exports only com.jscratch.*;
    // game-specific code (e.g. dndiablo) lives in jscratch_ws and is
    // compiled as generated sources, not as engine classes.
    private static final Set<String> ENGINE_PACKAGES = new HashSet<>();

    /**
     * Exports the project to a JAR.
     *
     * @param engineClassesDir either a classes directory (IDE / Maven build
     *        tree) or the running editor jar itself; in the jar case the
     *        engine entries are copied straight out of it so the export always
     *        matches the running editor and never a stale build tree.
     */
    public static void export(File targetJar, File compiledClassesDir, File sourceDir, File assetsDir, File engineClassesDir) throws IOException {
        export(targetJar, compiledClassesDir, sourceDir, assetsDir, engineClassesDir, "generated.Main");
    }

    /** Export a game using its actual fully-qualified Main type. */
    public static void export(File targetJar, File compiledClassesDir, File sourceDir, File assetsDir,
                              File engineClassesDir, String projectMainClass) throws IOException {
        if (targetJar.getParentFile() != null) {
            targetJar.getParentFile().mkdirs();
        }
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        // GameBootstrap opens the stage window, then invokes the configured project entry point.
        manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, "com.jscratch.GameBootstrap");
        manifest.getMainAttributes().putValue("JScratch-Project-Main", projectMainClass == null || projectMainClass.trim().isEmpty()
                ? "generated.Main" : projectMainClass.trim());
        // The jar's own directory is on the classpath so that the external
        // "jscratch_ws/assets/" folder sitting next to the jar resolves via
        // ClassLoader.getResource("jscratch_ws/assets/..."). Entries are
        // resolved relative to the jar location at launch time.
        manifest.getMainAttributes().put(Attributes.Name.CLASS_PATH, ".");
        
        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(targetJar), manifest)) {
            // 1. Add Filtered Engine Classes. The engine source is either a
            // classes directory or the running editor jar itself.
            if (engineClassesDir != null && engineClassesDir.exists()) {
                if (engineClassesDir.isFile()) {
                    addFilteredJarToJar(engineClassesDir, jos, JarExporter::isEngineEntry);
                } else {
                    addFilteredDirectoryToJar(engineClassesDir.toPath(), "", jos, JarExporter::isEngineEntry);
                }
            }
            
            // 2. Add Generated Classes (All of them)
            if (compiledClassesDir != null && compiledClassesDir.exists()) {
                addDirectoryToJar(compiledClassesDir.toPath(), "", jos);
            }
            
            // 3. Add Source Code
            if (sourceDir != null && sourceDir.exists()) {
                addDirectoryToJar(sourceDir.toPath(), "src/", jos);
            }
            
            // 4. Assets are NOT bundled into the jar. They are copied next to
            // the jar instead (see copyDirectory below), keeping the
            // same "jscratch_ws/assets/" layout so relative paths keep working.
        }

        // 5. Copy assets beside the jar: <jarDir>/jscratch_ws/assets/...
        if (assetsDir != null && assetsDir.exists()) {
            File jarDir = targetJar.getAbsoluteFile().getParentFile();
            if (jarDir == null) jarDir = new File(".").getAbsoluteFile();
            File externalAssets = new File(new File(jarDir, "jscratch_ws"), "assets");
            copyDirectory(assetsDir.toPath(), externalAssets.toPath());
        }
    }

    /**
     * Recursively copies a directory to the filesystem, preserving hierarchy.
     */
    private static void copyDirectory(Path source, Path target) throws IOException {
        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Files.createDirectories(target.resolve(source.relativize(dir)));
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path dest = target.resolve(source.relativize(file));
                Files.createDirectories(dest.getParent());
                Files.copy(file, dest, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Engine inclusion rule shared by both engine sources (classes directory
     * and running editor jar): core engine classes (plus inner classes) and
     * whole engine packages such as com.burrbox. Everything else (editor UI,
     * compiler, third-party libs) stays out of the exported game.
     */
    private static boolean isEngineEntry(String relativePath) {
        String name = relativePath.replace(".class", "");
        // Check if it's one of our core classes or an inner class of one
        for (String core : CORE_ENGINE_CLASSES) {
            if (name.equals(core) || name.startsWith(core + "$")) {
                return true;
            }
        }
        // Whole packages that ship with the engine
        for (String pkg : ENGINE_PACKAGES) {
            if (name.startsWith(pkg)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Copies matching entries out of an existing jar (the running editor jar)
     * into the jar being written. The source jar's own manifest and signatures
     * are never copied; the exported game gets a fresh manifest.
     */
    private static void addFilteredJarToJar(File sourceJar, JarOutputStream jos, java.util.function.Predicate<String> filter) throws IOException {
        try (JarFile in = new JarFile(sourceJar)) {
            Enumeration<? extends JarEntry> entries = in.entries();
            byte[] buf = new byte[8192];
            while (entries.hasMoreElements()) {
                JarEntry e = entries.nextElement();
                String name = e.getName();
                if (e.isDirectory() || name.startsWith("META-INF/")) continue;
                if (!name.endsWith(".class")) continue;
                if (!filter.test(name)) continue;
                JarEntry out = new JarEntry(name);
                out.setTime(e.getTime());
                jos.putNextEntry(out);
                try (InputStream is = in.getInputStream(e)) {
                    int n;
                    while ((n = is.read(buf)) != -1) jos.write(buf, 0, n);
                }
                jos.closeEntry();
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
