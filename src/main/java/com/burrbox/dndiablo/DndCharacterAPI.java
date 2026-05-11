package com.burrbox.dndiablo;

import java.util.Arrays;
import java.util.Random;

/**
 * API for creating and managing D&D characters.
 * Compatible with Java 8 and older.
 */
public class DndCharacterAPI {

    /**
     * Creates a Level 1 character based on a class and raw ability scores.
     */
    public static DndCharacterStats createLevel1Character(String name, DNDClass dndClass, 
                                                         int str, int dex, int con, 
                                                         int intel, int wis, int cha) {
        DndCharacterStats stats = new DndCharacterStats();
        
        // 1. Assign Basic Info
        stats.dndclass = dndClass;
        stats.level = 1;
        stats.proficiency = 2; // Level 1-4 is always +2

        // 2. Set Ability Scores
        stats.strengthScore = str;
        stats.dexterityScore = dex;
        stats.constitutionScore = con;
        stats.intelligenceScore = intel;
        stats.wisdomScore = wis;
        stats.charismaScore = cha;

        // 3. Calculate Modifiers and Derived Stats
        refreshCalculatedStats(stats);

        // 4. Initial HP (Max Hit Die + Constitution Modifier)
        stats.maxHp = dndClass.hitDie + stats.constitutionModifier;
        stats.hp = stats.maxHp;

        // 5. Default Armor Class (10 + Dex) - Assuming no armor equipped yet
        stats.armourClass = 10 + stats.dexterityModifier;
        
        // 6. Map Saving Throw Proficiencies from Class to Boolean Flags
        applyClassProficiencies(stats, dndClass);

        return stats;
    }

    /**
     * Recalculates all modifiers and passive senses. 
     * Call this whenever an ability score changes (e.g., leveling up or buffs).
     */
    public static void refreshCalculatedStats(DndCharacterStats stats) {
        stats.strengthModifier = calculateModifier(stats.strengthScore);
        stats.dexterityModifier = calculateModifier(stats.dexterityScore);
        stats.constitutionModifier = calculateModifier(stats.constitutionScore);
        stats.intelligenceModifier = calculateModifier(stats.intelligenceScore);
        stats.wisdomModifier = calculateModifier(stats.wisdomScore);
        stats.charismaModifier = calculateModifier(stats.charismaScore);

        // Standard D&D Passive Senses
        stats.passivePerception = 10 + stats.wisdomModifier;
        stats.passiveInsight = 10 + stats.wisdomModifier;
        stats.passiveInvestigation = 10 + stats.intelligenceModifier;
        
        // Initiative is usually just Dex modifier
        stats.initiative = stats.dexterityModifier;
    }

    /**
     * Standard D&D Formula: (Score - 10) / 2, rounded down.
     */
    private static int calculateModifier(int score) {
        return (int) Math.floor((score - 10) / 2.0);
    }

    private static void applyClassProficiencies(DndCharacterStats stats, DNDClass dndClass) {
        // Reset all
        stats.profStrengthSave = false;
        stats.profDexteritySave = false;
        stats.profConstitutionSave = false;
        stats.profIntelligenceSave = false;
        stats.profWisdomSave = false;
        stats.profCharismaSave = false;

        // Apply based on class list
        if (dndClass.savingThrowProficiencies.contains("Strength")) stats.profStrengthSave = true;
        if (dndClass.savingThrowProficiencies.contains("Dexterity")) stats.profDexteritySave = true;
        if (dndClass.savingThrowProficiencies.contains("Constitution")) stats.profConstitutionSave = true;
        if (dndClass.savingThrowProficiencies.contains("Intelligence")) stats.profIntelligenceSave = true;
        if (dndClass.savingThrowProficiencies.contains("Wisdom")) stats.profWisdomSave = true;
        if (dndClass.savingThrowProficiencies.contains("Charisma")) stats.profCharismaSave = true;
    }
    private static final Random dice = new Random(2026); //set seed

    /**
     * Levels up the character. 
     * Increases Proficiency Bonus at levels 5, 9, 13, and 17.
     */
    public static void levelUp(DndCharacterStats stats) {
        stats.level++;

        // 1. Proficiency Bonus Scaling (Official D&D 5e Table)
        if (stats.level >= 17) stats.proficiency = 6;
        else if (stats.level >= 13) stats.proficiency = 5;
        else if (stats.level >= 9) stats.proficiency = 4;
        else if (stats.level >= 5) stats.proficiency = 3;
        else stats.proficiency = 2;

        // 2. HP Increase: Roll Hit Die + Constitution Modifier (Minimum 1)
        int hpGain = dice.nextInt(stats.dndclass.hitDie) + 1 + stats.constitutionModifier;
        if (hpGain < 1) hpGain = 1; // You always gain at least 1 HP
        
        stats.maxHp += hpGain;
        stats.hp += hpGain; // Heal by the amount gained

        refreshCalculatedStats(stats);
    }

    // --- Dynamic Getters for Rolls ---

    /**
     * Gets the total bonus for a Strength Save.
     * Logic: Strength Mod + (Proficiency if proficient)
     */
    public static int getStrengthSaveBonus(DndCharacterStats s) {
        return s.strengthModifier + (s.profStrengthSave ? s.proficiency : 0);
    }

    public static int getDexteritySaveBonus(DndCharacterStats s) {
        return s.dexterityModifier + (s.profDexteritySave ? s.proficiency : 0);
    }

    public static int getConstitutionSaveBonus(DndCharacterStats s) {
        return s.constitutionModifier + (s.profConstitutionSave ? s.proficiency : 0);
    }

    public static int getIntelligenceSaveBonus(DndCharacterStats s) {
        return s.intelligenceModifier + (s.profIntelligenceSave ? s.proficiency : 0);
    }

    public static int getWisdomSaveBonus(DndCharacterStats s) {
        return s.wisdomModifier + (s.profWisdomSave ? s.proficiency : 0);
    }

    public static int getCharismaSaveBonus(DndCharacterStats s) {
        return s.charismaModifier + (s.profCharismaSave ? s.proficiency : 0);
    }
    
    /**
     * Method 1: The Standard Array
     * Returns the fixed set of scores: 15, 14, 13, 12, 10, 8.
     * Best for beginners or balanced gameplay.
     */
    public static int[] getStandardArray() {
        return new int[]{15, 14, 13, 12, 10, 8};
    }

    /**
     * Method 2: 4d6 Drop Lowest (The "Classic" Roll)
     * Rolls four 6-sided dice, discards the lowest, and sums the rest.
     * Repeats this 6 times.
     */
    public static int[] rollAbilityScores() {
        int[] scores = new int[6];
        for (int i = 0; i < 6; i++) {
            scores[i] = rollSingleAbilityScore();
        }
        Arrays.sort(scores); // Optional: sorts them low to high for easier assignment
        return scores;
    }

    private static int rollSingleAbilityScore() {
        int[] rolls = new int[4];
        int sum = 0;
        int lowest = 7; // Higher than any possible d6 roll

        for (int i = 0; i < 4; i++) {
            rolls[i] = dice.nextInt(6) + 1;
            sum += rolls[i];
            if (rolls[i] < lowest) {
                lowest = rolls[i];
            }
        }
        return sum - lowest; // Drop the lowest die
    }

    /**
     * Method 3: Point Buy (The "Fair" Method)
     * In a real UI, you'd let players spend 27 points. 
     * For a quick "Average Hero" generation, this returns a baseline.
     */
    public static int[] getPointBuyBaseline() {
        return new int[]{13, 13, 13, 12, 12, 12};
    }

    }
