package com.burrbox.dndiablo;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class DNDClass {
	public static final DNDClass ARTIFICER = DNDClassFactory.createClass("artificer");
	public static final DNDClass BARBARIAN = DNDClassFactory.createClass("barbarian");
	public static final DNDClass BARD = DNDClassFactory.createClass("bard");
	public static final DNDClass CLERIC = DNDClassFactory.createClass("cleric");
	public static final DNDClass DRUID = DNDClassFactory.createClass("druid");
	public static final DNDClass FIGHTER = DNDClassFactory.createClass("fighter");
	public static final DNDClass MONK = DNDClassFactory.createClass("monk");
	public static final DNDClass PALADIN = DNDClassFactory.createClass("paladin");
	public static final DNDClass RANGER = DNDClassFactory.createClass("ranger");
	public static final DNDClass ROGUE = DNDClassFactory.createClass("rogue");
	public static final DNDClass SORCERER = DNDClassFactory.createClass("sorcerer");
	public static final DNDClass WARLOCK = DNDClassFactory.createClass("warlock");
	public static final DNDClass WIZARD = DNDClassFactory.createClass("wizard");
	
	public static final List<DNDClass> ALL_CLASSES = Collections.unmodifiableList(
		    Arrays.asList(
		        ARTIFICER, 
		        BARBARIAN, 
		        BARD, 
		        CLERIC, 
		        DRUID, 
		        FIGHTER, 
		        MONK, 
		        PALADIN, 
		        RANGER, 
		        ROGUE, 
		        SORCERER, 
		        WARLOCK, 
		        WIZARD
		    )
		);
	
    public String className;       // e.g., "Fighter", "Wizard"
    public int hitDie;             // e.g., 10 for Fighter, 6 for Wizard
    
    // --- Primary Stats ---
    // Used to recommend where players put their highest scores
    public List<String> primaryAbilities; 

    // --- Proficiencies ---
    // Which saving throws get the proficiency bonus
    public List<String> savingThrowProficiencies; 
    
    // Armor (Light, Medium, Heavy, Shields) and Weapons (Simple, Martial)
    public List<String> armorProficiencies;
    public List<String> weaponProficiencies;
    
    // --- Spellcasting (if any) ---
    public boolean isSpellcaster;  //
    public String spellcastingAbility; // e.g., "Intelligence" for Wizards

    // --- Progression ---
    // A list of features unlocked at specific levels
    public List<ClassFeature> features; 
}
