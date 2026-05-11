package com.jscratch.compiler;

import com.jscratch.Stage;
import java.io.File;
import java.lang.reflect.Method;

public class ProjectRunner {
    private static java.net.URLClassLoader projectClassLoader;

    public static void mount(File buildDir) {
        try {
            projectClassLoader = new java.net.URLClassLoader(
                new java.net.URL[]{buildDir.toURI().toURL()}, 
                ProjectRunner.class.getClassLoader()
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static java.net.URLClassLoader getProjectClassLoader() {
        return projectClassLoader;
    }

    public static void run(File buildDir, String mainClassName) {
        // Kill existing stage if it exists
        if (Stage.instance != null) {
            Stage.instance.disposeStage();
        }
        
        // Create a completely new stage instance
        Stage newStage = new Stage();
        
        Stage.run(newStage, () -> {
            try {
                if (projectClassLoader == null) mount(buildDir);
                
                Class<?> clazz = projectClassLoader.loadClass("generated." + mainClassName);
                Method mainMethod = clazz.getMethod("main", String[].class);
                
                // Execute Main.main(new String[0])
                mainMethod.invoke(null, (Object) new String[0]);
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
