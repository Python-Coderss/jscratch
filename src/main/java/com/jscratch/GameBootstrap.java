package com.jscratch;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Entry point for standalone exported JARs.
 *
 * <p>Call chain: {@code java -jar} → {@link #main(String[])} →
 * {@code mountExternalAssets()} → {@code Stage.run(stage, setup,
 * EXIT_ON_CLOSE)} → {@code generated.Main.main()} builds the scene
 * (Scroller2d → CharacterCreation → FloorManager → title music).
 * In the editor the same role is played by {@code ProjectRunner}.
 */
public class GameBootstrap {

    /** Main class of the generated project, relative to the root package. */
    private static final String PROJECT_MAIN_CLASS = "generated.Main";

    public static void main(String[] args) {
        mountExternalAssets();
        String configuredMain = PROJECT_MAIN_CLASS;
        try {
            java.net.URL location = GameBootstrap.class.getProtectionDomain().getCodeSource().getLocation();
            if (location != null && "file".equalsIgnoreCase(location.getProtocol())) {
                File codeSource = new File(location.toURI());
                if (codeSource.isFile()) {
                    try (java.util.jar.JarFile jar = new java.util.jar.JarFile(codeSource)) {
                        String exportedMain = jar.getManifest().getMainAttributes().getValue("JScratch-Project-Main");
                        if (exportedMain != null && !exportedMain.trim().isEmpty()) configuredMain = exportedMain.trim();
                    }
                }
            }
        } catch (Exception ignored) { /* retain legacy generated.Main default */ }
        final String mainClassName = (args.length > 0 && args[0] != null && !args[0].trim().isEmpty())
                ? args[0].trim() : configuredMain;

        Stage.run(new Stage(), () -> {
            try {
                Class<?> clazz = Class.forName(mainClassName);
                Method mainMethod = clazz.getMethod("main", String[].class);
                mainMethod.invoke(null, (Object) new String[0]);
            } catch (Throwable t) {
                t.printStackTrace();
                System.exit(1);
            }
        }, javax.swing.WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * Adds the external assets folder sitting next to the exported jar to the
     * classpath so resource lookups keep working even though assets are no
     * longer bundled inside the jar.
     *
     * <p>Export layout:
     * <pre>
     *   ExportedGame.jar
     *   jscratch_ws/assets/...   (beside the jar)
     * </pre>
     * Adding the jar's directory to the classpath makes
     * {@code getResource("jscratch_ws/assets/...")} resolve to those external
     * files. Also sets {@code jscratch.project.root} to the jar directory so
     * {@code new File("jscratch_ws/assets/...")} lookups work regardless of
     * the working directory the jar was launched from.
     */
    private static void mountExternalAssets() {
        try {
            File jarDir = resolveJarDirectory();
            if (jarDir == null) return;

            // File-based lookups (Costume, generated helpers) resolve against this.
            if (System.getProperty("jscratch.project.root") == null) {
                System.setProperty("jscratch.project.root", jarDir.getAbsolutePath());
            }

            // The jar directory itself goes on the classpath (not the assets
            // dir directly) so the "jscratch_ws/assets/..." prefix keeps
            // resolving via getResource.
            URL jarDirUrl = jarDir.toURI().toURL();
            ClassLoader current = Thread.currentThread().getContextClassLoader();
            if (current == null) current = GameBootstrap.class.getClassLoader();

            // Skip if already on the classpath.
            if (current instanceof URLClassLoader) {
                for (URL u : ((URLClassLoader) current).getURLs()) {
                    if (u.equals(jarDirUrl)) return;
                }
            }

            URLClassLoader cp = new URLClassLoader(new URL[]{jarDirUrl}, current);
            Thread.currentThread().setContextClassLoader(cp);
        } catch (Exception e) {
            System.err.println("GameBootstrap: could not mount external assets: " + e.getMessage());
        }
    }

    /**
     * @return directory containing the running jar, or null if it cannot be
     *         determined (e.g. running from IDE classes dir keeps defaults).
     */
    private static File resolveJarDirectory() {
        try {
            URL location = GameBootstrap.class.getProtectionDomain().getCodeSource().getLocation();
            if (location == null) return null;
            File f = new File(location.toURI());
            if (f.isFile()) {
                File parent = f.getParentFile();
                return parent != null ? parent : new File(System.getProperty("user.dir"));
            }
            // Running from a classes directory (IDE): the working directory
            // already acts as project root, nothing to mount.
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}
