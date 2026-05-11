package com.burrbox.dndiablo;

import java.util.List;

public class DndCharacterStats {
    public static final int TINY = 0;
    public static final int SMALL = 1;
    public static final int MEDIUM = 2;
    public static final int LARGE = 3;

    // --- Vitality & Combat ---
    public int maxHp;
    public int hp;
    public int tempHp; // Temporary hit points (don't stack, lost first)
    public int armourClass;
    public int initiative;
    public int proficiency;
    public int speed; 
    public int size;
    
    // Death Saves (Track 0-3 for each)
    public int deathSaveSuccesses;
    public int deathSaveFailures;

    // --- Ability Scores & Modifiers ---
    public int strengthScore, strengthModifier;
    public int dexterityScore, dexterityModifier;
    public int constitutionScore, constitutionModifier;
    public int intelligenceScore, intelligenceModifier;
    public int wisdomScore, wisdomModifier;
    public int charismaScore, charismaModifier;

    // --- Saving Throw Proficiencies ---
    // Boolean flags to know when to add the proficiency bonus to a save
    public boolean profStrengthSave;
    public boolean profDexteritySave;
    public boolean profConstitutionSave;
    public boolean profIntelligenceSave;
    public boolean profWisdomSave;
    public boolean profCharismaSave;

    // --- Senses & Skills ---
    public int passivePerception; // 10 + Wisdom Mod (+ Proficiency if applicable)
    public int passiveInvestigation;
    public int passiveInsight;

    // --- Class & Resource Tracking ---
    public DNDClass dndclass;
    public int level;
    public int currentXp;
    
    // Hit Dice (e.g., current: 3, total: 5)
    public int hitDiceCount; 
    public String hitDiceType; // e.g., "d10" or "d8"

    // --- Magic / Spellcasting (If applicable) ---
    public int spellSaveDC;
    public int spellAttackBonus;
    public int[] spellSlotsMax = new int[9]; // Index 0 = Level 1 slots
    public int[] spellSlotsCurrent = new int[9];

    // --- Equipment & Traits ---
    // In a game like "Dndiablo," you likely need these for logic:
    public List<String> damageResistances;
    public List<String> damageImmunities;
    public List<String> conditionImmunities;
}
