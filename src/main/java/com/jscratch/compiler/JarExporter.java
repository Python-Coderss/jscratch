package com.jscratch.compiler;

import java.io.*;
import java.nio.file.*;
import java.util.jar.*;
import java.util.zip.*;

/**
 * Exports the project as a JAR file.
 */
public class JarExporter {
    
    public static void export(File targetJar, File compiledClassesDir, File sourceDir) throws IOException {
        try (JarOutputStream jos = new JarOutputStream(new FileOutputStream(targetJar))) {
            // Add manifest
            Manifest manifest = new Manifest();
            manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
            manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, "com.jscratch.MainRunner");
            
            // ... logic to walk and add all files from compiledClassesDir and sourceDir ...
        }
    }
}
