package com.burrbox.dndiablo;

public class ClassFeature {
    public String name;
    public String description;
    public int levelUnlocked;

    // --- Usage Logic ---
    public boolean isActive;      // True if it's a button you press (Action), False if it's a Passive
    public int maxUses;           // For features like "Rage" or "Second Wind" (0 if infinite)
    public int currentUses;
    
    // Resource Recovery
    // In D&D, some things reset on a "Short Rest" (1 hour) and some on a "Long Rest" (8 hours)
    public boolean resetsOnShortRest; 

    // --- Action Mechanics (Simplified for Dndiablo) ---
    public String actionType;     // e.g., "Buff", "Attack", "Heal", "Utility"
    public int effectValue;       // e.g., A "Second Wind" heal might be 10, a "Rage" bonus might be 2
    public String diceRoll;       // e.g., "1d10 + 5" for dynamic calculation

    public ClassFeature(String name, String description, int levelUnlocked) {
        this.name = name;
        this.description = description;
        this.levelUnlocked = levelUnlocked;
    }
}
