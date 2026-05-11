package com.jscratch;

import java.io.File;

public class WorkspaceManager {
    private static final String WORKSPACE_NAME = "jscratch_ws";
    private static File root;
    private static File assets;
    private static File src;
    private static File bin;

    static {
        root = new File(System.getProperty("user.dir"), WORKSPACE_NAME);
        assets = new File(root, "assets");
        src = new File(root, "src");
        bin = new File(root, "bin");
        
        ensureDirs();
    }

    public static void ensureDirs() {
        if (!root.exists()) root.mkdirs();
        if (!assets.exists()) assets.mkdirs();
        if (!src.exists()) src.mkdirs();
        if (!bin.exists()) bin.mkdirs();
    }

    public static File getRoot() { return root; }
    public static File getAssetsDir() { return assets; }
    public static File getSrcDir() { return src; }
    public static File getBinDir() { return bin; }
}
