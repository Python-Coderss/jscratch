package com.example;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap; // For localVariables and default costume meta
// import java.util.concurrent.ConcurrentHashMap; // Decided on HashMap for now

public class Sprite {
    private String id;
    private String name;
    private double x;
    private double y;
    private String currentCostumeId;
    // For costumes and sounds, we store metadata. The actual data (e.g., image/audio bytes or DataURLs)
    // would be handled differently depending on whether they are server-managed or client-side.
    // For now, these lists will hold maps of metadata, like {"id": "unique_id", "name": "costume_name.png"}
    private List<Map<String, String>> costumes;
    private List<Map<String, String>> sounds;
    private Map<String, Object> localVariables;
    // In a more complete model, 'scripts' would also be here, perhaps as a JSON string or a list of block objects.

    public Sprite(String id, String name, double x, double y, String currentCostumeId,
                  List<Map<String, String>> costumes, List<Map<String, String>> sounds) {
        this.id = id;
        this.name = name;
        this.x = x;
        this.y = y;
        this.currentCostumeId = currentCostumeId;
        this.costumes = costumes != null ? new ArrayList<>(costumes) : new ArrayList<>();
        this.sounds = sounds != null ? new ArrayList<>(sounds) : new ArrayList<>();
        this.localVariables = new HashMap<>(); // Initialize localVariables
    }

    // Default constructor for convenience or if created without all initial data
    public Sprite(String id, String name) {
        this(id, name, 0.0, 0.0, null, new ArrayList<>(), new ArrayList<>());
        // localVariables is initialized by the main constructor call above
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public double getX() { return x; }
    public double getY() { return y; }
    public String getCurrentCostumeId() { return currentCostumeId; }
    public List<Map<String, String>> getCostumes() { return costumes; }
    public List<Map<String, String>> getSounds() { return sounds; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setCurrentCostumeId(String currentCostumeId) { this.currentCostumeId = currentCostumeId; }

    // Methods to manage costumes and sounds metadata
    public void addCostume(Map<String, String> costumeMeta) {
        if (this.costumes == null) {
            this.costumes = new ArrayList<>();
        }
        this.costumes.add(costumeMeta);
        if (this.currentCostumeId == null && !this.costumes.isEmpty()) {
            this.currentCostumeId = this.costumes.get(0).get("id");
        }
    }

    public void addSound(Map<String, String> soundMeta) {
        if (this.sounds == null) {
            this.sounds = new ArrayList<>();
        }
        this.sounds.add(soundMeta);
    }

    // In a server-side model, you might have methods here to load actual costume/sound data
    // from a file path stored in the metadata, but for client-side DataURLs, this is mostly for structure.

    // Local Variable Management
    public Object getLocalVariable(String name) {
        return this.localVariables.get(name);
    }

    public void setLocalVariable(String name, Object value) {
        // Basic type checking or conversion could be added here if needed
        // e.g., ensuring numbers are stored as Double or Long, not just any Object.
        // For now, allowing any Object as per typical dynamic language variable behavior.
        this.localVariables.put(name, value);
    }

    public Map<String, Object> getAllLocalVariables() {
        // Return a copy to prevent external modification of the internal map if desired,
        // though direct modification might be intended in some controlled scenarios.
        return new HashMap<>(this.localVariables);
    }

    public void removeLocalVariable(String name) {
        this.localVariables.remove(name);
    }
}
