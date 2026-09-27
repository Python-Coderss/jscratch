package com.jscratch.compiler;

import com.jscratch.Stage;
import java.io.File;
import java.lang.reflect.Method;

/** Runs compiled workspace code in a separate stage and project class loader. */
public class ProjectRunner {
    /** Latest successful compilation; also used by autocomplete. */
    private static java.net.URLClassLoader projectClassLoader;
    /** Loader and entry identity belonging to the currently running stage. */
    private static java.net.URLClassLoader activeRunClassLoader;
    private static String activeMainClassName = "generated.Main";
    private static String musicClassName = "generated.MusicManager";

    /** Update the class loader to the latest compiled classes. */
    public static void mount(File buildDir) {
        try {
            projectClassLoader = new java.net.URLClassLoader(
                    new java.net.URL[]{buildDir.toURI().toURL()},
                    ProjectRunner.class.getClassLoader());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static java.net.URLClassLoader getProjectClassLoader() {
        return projectClassLoader;
    }

    /** Legacy overload for projects that put their entry point in generated. */
    public static void run(File buildDir, String mainClassName) {
        run(buildDir, "generated", mainClassName);
    }

    /** Run a fully qualified entry type such as {@code game.Main}. */
    public static void runQualified(File buildDir, String fullyQualifiedMainClass) {
        String name = fullyQualifiedMainClass == null ? "generated.Main" : fullyQualifiedMainClass.trim();
        int separator = name.lastIndexOf('.');
        run(buildDir, separator < 0 ? "" : name.substring(0, separator),
                separator < 0 ? name : name.substring(separator + 1));
    }

    /** Run Main using its declared Java package. */
    public static void run(File buildDir, String packageName, String mainTypeName) {
        String normalizedPackage = packageName == null ? "" : packageName.trim();
        String qualifiedMainName = normalizedPackage.isEmpty() ? mainTypeName : normalizedPackage + "." + mainTypeName;
        String nextMusicClassName = normalizedPackage.isEmpty() ? "MusicManager" : normalizedPackage + ".MusicManager";

        // Stop the old runtime before mounting/activating a newer class loader.
        if (Stage.instance != null) {
            stopProjectMusic();
            Stage.instance.disposeStage();
        }
        if (projectClassLoader == null) mount(buildDir);
        activeRunClassLoader = projectClassLoader;
        activeMainClassName = qualifiedMainName;
        musicClassName = nextMusicClassName;
        final java.net.URLClassLoader runLoader = activeRunClassLoader;

        Stage newStage = new Stage();
        Stage.run(newStage, () -> {
            try {
                Class<?> entry = runLoader.loadClass(qualifiedMainName);
                Method mainMethod = entry.getMethod("main", String[].class);
                mainMethod.invoke(null, (Object) new String[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /** Stop the previous run's music; its daemon streamer outlives stage disposal. */
    private static void stopProjectMusic() {
        if (activeRunClassLoader == null) return;
        try {
            // MusicDirector.death() fades out the DnDiablo track, then the
            // manager fallback below stops music for generic projects.
            String mainPackage = packageOf(activeMainClassName);
            if (!mainPackage.isEmpty()) {
                try {
                    Class<?> director = activeRunClassLoader.loadClass(mainPackage + ".MusicDirector");
                    director.getMethod("death").invoke(null);
                } catch (Exception ignored) { }
            }
            Class<?> manager;
            try {
                manager = activeRunClassLoader.loadClass(musicClassName);
            } catch (Exception missingInMainPackage) {
                manager = activeRunClassLoader.loadClass("generated.MusicManager");
            }
            manager.getMethod("stopMusic").invoke(null);
        } catch (Exception ignored) { }
    }

    private static String packageOf(String qualifiedClassName) {
        int separator = qualifiedClassName.lastIndexOf('.');
        return separator < 0 ? "" : qualifiedClassName.substring(0, separator);
    }
}
