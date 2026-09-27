package com.jscratch.compiler;

import org.eclipse.jdt.internal.compiler.batch.Main;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Compiles workspace Java using the bundled Eclipse JDT batch compiler.
 * Fully-qualified source identities are mapped to matching package directories;
 * package declarations are checked before any source is written.
 */
public class CompilerService {

    public static CompilationResult compile(String[] sourceCode, String[] className, File srcdir, File bindir) {
        StringWriter outWriter = new StringWriter();
        StringWriter errWriter = new StringWriter();
        PrintWriter out = new PrintWriter(outWriter);
        PrintWriter err = new PrintWriter(errWriter);

        CompilationResult res = new CompilationResult();
        try {
            if (sourceCode.length != className.length) {
                throw new RuntimeException("Impossible length mismatch in CompilerService");
            }

            // Validate the whole request before writing any part of it.
            for (int i = 0; i < className.length; i++) {
                validatePackageIdentity(sourceCode[i], className[i]);
            }

            if (!srcdir.exists()) srcdir.mkdirs();
            if (!bindir.exists()) bindir.mkdirs();

            for (int i = 0; i < className.length; i++) {
                File sourceFile = sourcePath(srcdir, className[i]);
                File parent = sourceFile.getParentFile();
                if (parent != null && !parent.exists()) parent.mkdirs();

                byte[] newBytes = sourceCode[i].getBytes(java.nio.charset.StandardCharsets.UTF_8);
                boolean shouldWrite = true;
                if (sourceFile.exists()) {
                    try {
                        shouldWrite = !Arrays.equals(newBytes, Files.readAllBytes(sourceFile.toPath()));
                    } catch (IOException ignored) { }
                }
                if (shouldWrite) Files.write(sourceFile.toPath(), newBytes);
            }

            // The bundled ECJ (3.12.x, pre-modules) can only see platform
            // classes through <java.home>/lib/rt.jar (Java 8).
            String platformCheck = checkPlatformClasses();
            if (platformCheck != null) {
                res.success = false;
                res.output = platformCheck;
                return res;
            }

            List<String> args = new ArrayList<>();
            args.add("-8");
            args.add("-proc:none");
            args.add("-cp");
            args.add(buildClasspath());
            args.add("-d");
            args.add(bindir.getAbsolutePath());
            args.add(srcdir.getAbsolutePath());

            res.success = Main.compile(args.toArray(new String[0]), out, err, null);
            out.flush();
            err.flush();
            res.output = outWriter.toString() + "\n" + errWriter.toString();

            Pattern errorPattern = Pattern.compile("^\\d+\\.\\s+ERROR\\s+in\\s+(.*?)\\s+\\(at\\s+line\\s+(\\d+)\\)");
            BufferedReader reader = new BufferedReader(new StringReader(errWriter.toString()));
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = errorPattern.matcher(line);
                if (!matcher.find()) continue;
                String fileName = new File(matcher.group(1)).getName();
                int lineNumber = Integer.parseInt(matcher.group(2));
                reader.readLine();
                reader.readLine();
                String message = reader.readLine();
                if (message != null) res.errors.add(new CompilationResult.CompilationError(fileName, lineNumber, message.trim()));
            }
            return res;
        } catch (IOException e) {
            res.success = false;
            res.output = e.getMessage();
            return res;
        }
    }

    /** Map a fully qualified class name to its source-tree Java path. */
    private static File sourcePath(File sourceRoot, String qualifiedTypeName) {
        String relative = qualifiedTypeName.replace('.', File.separatorChar) + ".java";
        return new File(sourceRoot, relative);
    }

    /** Refuse to write package declarations to a source path from another package. */
    private static void validatePackageIdentity(String source, String qualifiedTypeName) throws IOException {
        Matcher declared = Pattern.compile("(?m)^\\s*package\\s+([A-Za-z_$][\\w$]*(?:\\.[A-Za-z_$][\\w$]*)*)\\s*;")
                .matcher(source == null ? "" : source);
        String declaredPackage = declared.find() ? declared.group(1) : "";
        int separator = qualifiedTypeName.lastIndexOf('.');
        String requestedPackage = separator < 0 ? "" : qualifiedTypeName.substring(0, separator);
        if (!declaredPackage.equals(requestedPackage)) {
            throw new IOException("Source " + qualifiedTypeName + " declares package '" + declaredPackage
                    + "' but compiler identity uses '" + requestedPackage + "'.");
        }
    }

    /** Verify that this Java 8-targeted bundled ECJ can resolve platform classes. */
    private static String checkPlatformClasses() {
        File rtJar = new File(System.getProperty("java.home"), "lib" + File.separator + "rt.jar");
        if (rtJar.exists()) return null;

        String launchHint = "";
        try {
            File self = new File(CompilerService.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            launchHint = " Relaunch with JDK 8, e.g. \"<jdk8>\\bin\\java.exe\" -jar \"" + self.getAbsolutePath() + "\".";
        } catch (Exception ignored) { }

        return "Cannot compile: this editor is running on Java " + System.getProperty("java.version")
                + " (" + System.getProperty("java.home") + "), which has no lib/rt.jar, so the built-in"
                + " compiler cannot resolve java.lang / java.awt / javax.swing (every file reports"
                + " \"cannot be resolved\"). Eclipse works because it launches the editor on JDK 8."
                + launchHint;
    }

    /** Build the in-process compiler class path, explicitly including Java 8's standard library. */
    private static String buildClasspath() {
        StringBuilder classpath = new StringBuilder(System.getProperty("java.class.path"));
        File rtJar = new File(System.getProperty("java.home"), "lib" + File.separator + "rt.jar");
        if (rtJar.exists()) {
            if (classpath.length() > 0) classpath.append(File.pathSeparatorChar);
            classpath.append(rtJar.getAbsolutePath());
        }
        return classpath.toString();
    }
}
