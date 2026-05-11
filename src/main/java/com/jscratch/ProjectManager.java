package com.jscratch;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.*;

/**
 * Handles saving and loading of projects in .jscratch format (ZIP containing JSON + assets).
 */
public class ProjectManager {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    
    public static void saveProject(Project project, File targetFile) throws IOException {
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(targetFile))) {
            // Write project.json
            ZipEntry jsonEntry = new ZipEntry("project.json");
            zos.putNextEntry(jsonEntry);
            zos.write(gson.toJson(project).getBytes());
            zos.closeEntry();
            
            // Assets will be handled here (copying files into ZIP)
            // ... logic to include costumes/sounds ...
        }
    }
    
    public static Project loadProject(File sourceFile) throws IOException {
        try (ZipFile zipFile = new ZipFile(sourceFile)) {
            ZipEntry jsonEntry = zipFile.getEntry("project.json");
            if (jsonEntry == null) throw new IOException("Not a valid .jscratch project.");
            
            try (InputStream is = zipFile.getInputStream(jsonEntry);
                 InputStreamReader reader = new InputStreamReader(is)) {
                return gson.fromJson(reader, Project.class);
            }
        }
    }
}
