package com.burrbox.dndiablo;

import java.util.Arrays;
import java.util.ArrayList;

/**
 * Factory to generate the base templates for all 13 official D&D classes.
 * Data based on the 5th Edition Player's Handbook and Tasha's Cauldron of Everything.
 */
public class DNDClassFactory {
	
    public static DNDClass createClass(String name) {
        DNDClass dndClass = new DNDClass();
        dndClass.className = name;
        dndClass.features = new ArrayList<>(); // Initialize empty list for features

        switch (name.toLowerCase()) {
            case "artificer":
                setStats(dndClass, 8, true, "Intelligence", "Intelligence", "Constitution", "Intelligence");
                break;
            case "barbarian":
                setStats(dndClass, 12, false, null, "Strength", "Strength", "Constitution");
                break;
            case "bard":
                setStats(dndClass, 8, true, "Charisma", "Charisma", "Dexterity", "Charisma");
                break;
            case "cleric":
                setStats(dndClass, 8, true, "Wisdom", "Wisdom", "Wisdom", "Charisma");
                break;
            case "druid":
                setStats(dndClass, 8, true, "Wisdom", "Wisdom", "Intelligence", "Wisdom");
                break;
            case "fighter":
                setStats(dndClass, 10, false, null, "Strength", "Strength", "Constitution"); // Can also be Dex
                break;
            case "monk":
                setStats(dndClass, 8, false, null, "Dexterity", "Strength", "Dexterity");
                break;
            case "paladin":
                setStats(dndClass, 10, true, "Charisma", "Strength", "Wisdom", "Charisma");
                break;
            case "ranger":
                setStats(dndClass, 10, true, "Wisdom", "Dexterity", "Strength", "Dexterity");
                break;
            case "rogue":
                setStats(dndClass, 8, false, null, "Dexterity", "Dexterity", "Intelligence");
                break;
            case "sorcerer":
                setStats(dndClass, 6, true, "Charisma", "Charisma", "Constitution", "Charisma");
                break;
            case "warlock":
                setStats(dndClass, 8, true, "Charisma", "Charisma", "Wisdom", "Charisma");
                break;
            case "wizard":
                setStats(dndClass, 6, true, "Intelligence", "Intelligence", "Intelligence", "Wisdom");
                break;
            default:
                throw new IllegalArgumentException("Unknown D&D Class: " + name);
        }
        return dndClass;
    }

    /**
     * Helper to reduce boilerplate code for class definitions.
     */
    private static void setStats(DNDClass cl, int hitDie, boolean isCaster, String spellAttr, String primary, String save1, String save2) {
        cl.hitDie = hitDie;
        cl.isSpellcaster = isCaster;
        cl.spellcastingAbility = spellAttr;
        cl.primaryAbilities = Arrays.asList(primary);
        cl.savingThrowProficiencies = Arrays.asList(save1, save2);
    }
}
