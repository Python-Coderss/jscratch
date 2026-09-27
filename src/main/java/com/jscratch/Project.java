package com.jscratch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Root Project container for JSON serialization.
 */
public class Project {
    public String name = "New Project";
    public int blockIdCounter = 1;
    public StageData stage = new StageData();
    public List<SpriteData> sprites = new ArrayList<>();
    public MapMakerData mapMaker = new MapMakerData();

    public Project() {
        // Main is a special built-in sprite/class that coordinates the project
        sprites.add(new SpriteData("Main"));
        sprites.add(new SpriteData("Sprite1"));
    }

    public static class StageData {
        public int width = 480;
        public int height = 360;
        public List<String> backdrops = new ArrayList<>();
        public int currentBackdropIndex = 0;
    }

    public static class SpriteData {
        /** Simple Java type name, retained separately so UI labels stay readable. */
        public String name;
        /** Declared Java package; missing values in older project JSON remain compatible. */
        public String packageName = "generated";
        /** Source-tree identity; empty means a project-only type not yet written to disk. */
        public String sourcePath = "";
        public List<String> costumes = new ArrayList<>();
        public List<String> sounds = new ArrayList<>();
        public String sourceCode = ""; 

        public SpriteData(String name) {
            this.name = name;
        }

        /** Fully qualified identity used by compiler, runner and package navigator. */
        public String getQualifiedName() {
            return packageName == null || packageName.trim().isEmpty()
                    ? name : packageName.trim() + "." + name;
        }

        /** Relative source path tracked when discovered or written by the editor. */
        public String getSourcePath() {
            if (sourcePath != null && !sourcePath.trim().isEmpty()) return sourcePath.replace('\\', '/');
            return getQualifiedName().replace('.', '/') + ".java";
        }

        /** Keep package-tree file labels concise instead of displaying object ids. */
        @Override public String toString() { return name + ".java"; }
    }

    public static class MapMakerData {
        public int width = 60, height = 45;
        public int tileSize = 8;
        public int displayScale = 4;
        public List<LayerData> layers = new ArrayList<>();
        public Map<Integer, String> tileAssets = new HashMap<>(); // ID -> Costume Name
        
        public MapMakerData() {
            layers.add(new LayerData("Background", width, height));
            layers.add(new LayerData("Main", width, height));
            layers.add(new LayerData("Foreground", width, height));
        }
    }

    public static class LayerData {
        public String name;
        public int[][] tiles;
        
        public LayerData(String name, int w, int h) {
            this.name = name;
            this.tiles = new int[w][h];
        }
    }
}
