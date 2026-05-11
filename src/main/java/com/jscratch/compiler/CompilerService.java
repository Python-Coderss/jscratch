package com.jscratch.compiler;

import org.eclipse.jdt.internal.compiler.batch.Main;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service to compile generated Java code into bytecode using Eclipse JDT.
 */
public class CompilerService {
    
    public static CompilationResult compile(String[] sourceCode, String className[], File srcdir, File bindir) {
        StringWriter outWriter = new StringWriter();
        StringWriter errWriter = new StringWriter();
        PrintWriter out = new PrintWriter(outWriter);
        PrintWriter err = new PrintWriter(errWriter);
        
        CompilationResult res = new CompilationResult();
        try {
            if (sourceCode.length != className.length) {
                throw new RuntimeException("Impossible length mismatch in CompilerService");
            }
            // Ensure output directory exists
            if (!srcdir.exists()) srcdir.mkdirs();
            if (!bindir.exists()) bindir.mkdirs();
            
            for (int i = 0; i < className.length; i++) {
                File sourceFile;
                if (className[i].contains(".")) {
                    File packagePath = new File(srcdir, className[i].replace('.', '/')).getParentFile();
                    if (!packagePath.exists()) packagePath.mkdirs();
                    sourceFile = new File(packagePath, className[i].substring(className[i].lastIndexOf('.') + 1) + ".java");
                } else {
                    sourceFile = new File(srcdir, className[i] + ".java");
                }

                byte[] newBytes = sourceCode[i].getBytes();
                boolean shouldWrite = true;
                if (sourceFile.exists()) {
                    try {
                        byte[] oldBytes = Files.readAllBytes(sourceFile.toPath());
                        if (java.util.Arrays.equals(newBytes, oldBytes)) {
                            shouldWrite = false;
                        }
                    } catch (IOException e) {}
                }

                if (shouldWrite) {
                    Files.write(sourceFile.toPath(), newBytes);
                }
            }
            
            // Setup JDT arguments
            List<String> args = new ArrayList<>();
            args.add("-8"); // Target Java 8
            args.add("-proc:none"); // Disable annotation processing
            args.add("-cp");
            args.add(System.getProperty("java.class.path"));
            args.add("-d");
            args.add(bindir.getAbsolutePath());
            args.add(srcdir.getAbsolutePath());
            
            // Run JDT compiler
            res.success = Main.compile(args.toArray(new String[0]), out, err, null);
            out.flush();
            err.flush();
            
            res.output = outWriter.toString() + "\n" + errWriter.toString();
            
            // Parse error output for line numbers
            // JDT format: 1. ERROR in C:\path\to\File.java (at line 10)
            Pattern errorPattern = Pattern.compile("^\\d+\\.\\s+ERROR\\s+in\\s+(.*?)\\s+\\(at\\s+line\\s+(\\d+)\\)");
            BufferedReader reader = new BufferedReader(new StringReader(errWriter.toString()));
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher m = errorPattern.matcher(line);
                if (m.find()) {
                    String fullPath = m.group(1);
                    String fileName = new File(fullPath).getName();
                    int lineNum = Integer.parseInt(m.group(2));
                    
                    // The next 3 lines are code, arrow, and then the message
                    reader.readLine(); // skip code
                    reader.readLine(); // skip arrow
                    String messageLine = reader.readLine();
                    if (messageLine != null) {
                        res.errors.add(new CompilationResult.CompilationError(fileName, lineNum, messageLine.trim()));
                    }
                }
            }
            
            return res;
        } catch (IOException e) {
            res.success = false;
            res.output = e.getMessage();
            return res;
        }
    }
}
