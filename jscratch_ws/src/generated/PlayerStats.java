package generated;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
* Static holder for the player's character.
* Generic 2D stats — no D&D engine dependency.
* Only uses com.jscratch (Stage) + java stdlib.
*
* Built by CharacterCreation, read by combat / spells / floors / inventory.
*/
public class PlayerStats {

    /** Minimal stats bundle kept in-process. */
    public static class Stats {
        /* block comment [block:"PlayerStats_block_34" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_34" category:"Variables" label:"set maxHp" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        public int maxHp, hp, tempHp, armourClass, proficiency, level, currentXp;
        /* block end [id:"PlayerStats_block_34"] */
        /* block comment [block:"PlayerStats_block_35" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_35" category:"Variables" label:"set strengthScore" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        public int strengthScore, strengthModifier;
        /* block end [id:"PlayerStats_block_35"] */
        /* block comment [block:"PlayerStats_block_36" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_36" category:"Variables" label:"set dexterityScore" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        public int dexterityScore, dexterityModifier;
        /* block end [id:"PlayerStats_block_36"] */
        /* block comment [block:"PlayerStats_block_37" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_37" category:"Variables" label:"set constitutionScore" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        public int constitutionScore, constitutionModifier;
        /* block end [id:"PlayerStats_block_37"] */
        /* block comment [block:"PlayerStats_block_38" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_38" category:"Variables" label:"set intelligenceScore" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        public int intelligenceScore, intelligenceModifier;
        /* block end [id:"PlayerStats_block_38"] */
        /* block comment [block:"PlayerStats_block_39" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_39" category:"Variables" label:"set wisdomScore" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        public int wisdomScore, wisdomModifier;
        /* block end [id:"PlayerStats_block_39"] */
        /* block comment [block:"PlayerStats_block_40" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_40" category:"Variables" label:"set charismaScore" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        public int charismaScore, charismaModifier;
        /* block end [id:"PlayerStats_block_40"] */
        /* block comment [block:"PlayerStats_block_41" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_41" category:"Variables" label:"set heroClass" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        public String heroClass = /* param start [id:"PlayerStats_value_175" type:"text"] */"fighter"/* param end [id:"PlayerStats_value_175"] */;
        /* block end [id:"PlayerStats_block_41"] */
        /* block comment [block:"PlayerStats_block_42" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_42" category:"Variables" label:"set hitDie" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        public int hitDie = /* param start [id:"PlayerStats_value_176" type:"number"] */10/* param end [id:"PlayerStats_value_176"] */;
        /* block end [id:"PlayerStats_block_42"] */
        /* block comment [block:"PlayerStats_block_43" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_43" category:"Variables" label:"set isSpellcaster" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        public boolean isSpellcaster = /* param start [id:"PlayerStats_value_177" type:"boolean"] */false/* param end [id:"PlayerStats_value_177"] */;
        /* block end [id:"PlayerStats_block_43"] */
        /* block comment [block:"PlayerStats_block_44" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_44" category:"Variables" label:"set spellcastingAbility" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        public String spellcastingAbility = /* param start [id:"PlayerStats_value_178" type:"value"] */null/* param end [id:"PlayerStats_value_178"] */;
        /* block end [id:"PlayerStats_block_44"] */
    }

    public static Stats stats;
    public static String heroName = "Hero";
    public static int maxMana = 10;
    public static int mana = 10;
    public static boolean ready = false;

    public static int bonusMelee = 0;
    public static int bonusDmg = 0;
    public static int bonusAC = 0;
    public static int bonusSpell = 0;

    private static final Random dice = new Random(2026);

    private static class Archetype {
        /* block comment [block:"PlayerStats_block_45" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_45" category:"Variables" label:"set name" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        final String name;
        /* block end [id:"PlayerStats_block_45"] */
        /* block comment [block:"PlayerStats_block_46" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_46" category:"Variables" label:"set hitDie" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        final int hitDie;
        /* block end [id:"PlayerStats_block_46"] */
        /* block comment [block:"PlayerStats_block_47" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_47" category:"Variables" label:"set isCaster" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        final boolean isCaster;
        /* block end [id:"PlayerStats_block_47"] */
        /* block comment [block:"PlayerStats_block_48" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_48" category:"Variables" label:"set ability" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        final String ability;
        /* block end [id:"PlayerStats_block_48"] */
        Archetype(String n, int h, boolean c, String a) { /* block comment [block:"PlayerStats_block_49" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"PlayerStats_block_49" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            name = n;
            /* block end [id:"PlayerStats_block_49"] */ /* block comment [block:"PlayerStats_block_50" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"PlayerStats_block_50" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            hitDie = h;
            /* block end [id:"PlayerStats_block_50"] */ /* block comment [block:"PlayerStats_block_51" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"PlayerStats_block_51" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            isCaster = c;
            /* block end [id:"PlayerStats_block_51"] */ /* block comment [block:"PlayerStats_block_52" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"PlayerStats_block_52" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            ability = a;
            /* block end [id:"PlayerStats_block_52"] */ }
    }

    private static final Map<String, Archetype> ARCH = new HashMap<>();
    static {
        /* block comment [block:"PlayerStats_block_53" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_53" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        ARCH.put(/* param start [id:"PlayerStats_value_179" type:"text"] */"artificer"/* param end [id:"PlayerStats_value_179"] */, new Archetype(/* param start [id:"PlayerStats_value_180" type:"text"] */"artificer"/* param end [id:"PlayerStats_value_180"] */, /* param start [id:"PlayerStats_value_181" type:"number"] */8/* param end [id:"PlayerStats_value_181"] */, /* param start [id:"PlayerStats_value_182" type:"boolean"] */true/* param end [id:"PlayerStats_value_182"] */, /* param start [id:"PlayerStats_value_183" type:"text"] */"Intelligence"/* param end [id:"PlayerStats_value_183"] */));
        /* block end [id:"PlayerStats_block_53"] */
        /* block comment [block:"PlayerStats_block_54" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_54" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        ARCH.put(/* param start [id:"PlayerStats_value_184" type:"text"] */"barbarian"/* param end [id:"PlayerStats_value_184"] */, new Archetype(/* param start [id:"PlayerStats_value_185" type:"text"] */"barbarian"/* param end [id:"PlayerStats_value_185"] */, /* param start [id:"PlayerStats_value_186" type:"number"] */12/* param end [id:"PlayerStats_value_186"] */, /* param start [id:"PlayerStats_value_187" type:"boolean"] */false/* param end [id:"PlayerStats_value_187"] */, /* param start [id:"PlayerStats_value_188" type:"value"] */null/* param end [id:"PlayerStats_value_188"] */));
        /* block end [id:"PlayerStats_block_54"] */
        /* block comment [block:"PlayerStats_block_55" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_55" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        ARCH.put(/* param start [id:"PlayerStats_value_189" type:"text"] */"bard"/* param end [id:"PlayerStats_value_189"] */, new Archetype(/* param start [id:"PlayerStats_value_190" type:"text"] */"bard"/* param end [id:"PlayerStats_value_190"] */, /* param start [id:"PlayerStats_value_191" type:"number"] */8/* param end [id:"PlayerStats_value_191"] */, /* param start [id:"PlayerStats_value_192" type:"boolean"] */true/* param end [id:"PlayerStats_value_192"] */, /* param start [id:"PlayerStats_value_193" type:"text"] */"Charisma"/* param end [id:"PlayerStats_value_193"] */));
        /* block end [id:"PlayerStats_block_55"] */
        /* block comment [block:"PlayerStats_block_56" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_56" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        ARCH.put(/* param start [id:"PlayerStats_value_194" type:"text"] */"cleric"/* param end [id:"PlayerStats_value_194"] */, new Archetype(/* param start [id:"PlayerStats_value_195" type:"text"] */"cleric"/* param end [id:"PlayerStats_value_195"] */, /* param start [id:"PlayerStats_value_196" type:"number"] */8/* param end [id:"PlayerStats_value_196"] */, /* param start [id:"PlayerStats_value_197" type:"boolean"] */true/* param end [id:"PlayerStats_value_197"] */, /* param start [id:"PlayerStats_value_198" type:"text"] */"Wisdom"/* param end [id:"PlayerStats_value_198"] */));
        /* block end [id:"PlayerStats_block_56"] */
        /* block comment [block:"PlayerStats_block_57" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_57" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        ARCH.put(/* param start [id:"PlayerStats_value_199" type:"text"] */"druid"/* param end [id:"PlayerStats_value_199"] */, new Archetype(/* param start [id:"PlayerStats_value_200" type:"text"] */"druid"/* param end [id:"PlayerStats_value_200"] */, /* param start [id:"PlayerStats_value_201" type:"number"] */8/* param end [id:"PlayerStats_value_201"] */, /* param start [id:"PlayerStats_value_202" type:"boolean"] */true/* param end [id:"PlayerStats_value_202"] */, /* param start [id:"PlayerStats_value_203" type:"text"] */"Wisdom"/* param end [id:"PlayerStats_value_203"] */));
        /* block end [id:"PlayerStats_block_57"] */
        /* block comment [block:"PlayerStats_block_58" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_58" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        ARCH.put(/* param start [id:"PlayerStats_value_204" type:"text"] */"fighter"/* param end [id:"PlayerStats_value_204"] */, new Archetype(/* param start [id:"PlayerStats_value_205" type:"text"] */"fighter"/* param end [id:"PlayerStats_value_205"] */, /* param start [id:"PlayerStats_value_206" type:"number"] */10/* param end [id:"PlayerStats_value_206"] */, /* param start [id:"PlayerStats_value_207" type:"boolean"] */false/* param end [id:"PlayerStats_value_207"] */, /* param start [id:"PlayerStats_value_208" type:"value"] */null/* param end [id:"PlayerStats_value_208"] */));
        /* block end [id:"PlayerStats_block_58"] */
        /* block comment [block:"PlayerStats_block_59" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_59" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        ARCH.put(/* param start [id:"PlayerStats_value_209" type:"text"] */"monk"/* param end [id:"PlayerStats_value_209"] */, new Archetype(/* param start [id:"PlayerStats_value_210" type:"text"] */"monk"/* param end [id:"PlayerStats_value_210"] */, /* param start [id:"PlayerStats_value_211" type:"number"] */8/* param end [id:"PlayerStats_value_211"] */, /* param start [id:"PlayerStats_value_212" type:"boolean"] */false/* param end [id:"PlayerStats_value_212"] */, /* param start [id:"PlayerStats_value_213" type:"value"] */null/* param end [id:"PlayerStats_value_213"] */));
        /* block end [id:"PlayerStats_block_59"] */
        /* block comment [block:"PlayerStats_block_60" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_60" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        ARCH.put(/* param start [id:"PlayerStats_value_214" type:"text"] */"paladin"/* param end [id:"PlayerStats_value_214"] */, new Archetype(/* param start [id:"PlayerStats_value_215" type:"text"] */"paladin"/* param end [id:"PlayerStats_value_215"] */, /* param start [id:"PlayerStats_value_216" type:"number"] */10/* param end [id:"PlayerStats_value_216"] */, /* param start [id:"PlayerStats_value_217" type:"boolean"] */true/* param end [id:"PlayerStats_value_217"] */, /* param start [id:"PlayerStats_value_218" type:"text"] */"Charisma"/* param end [id:"PlayerStats_value_218"] */));
        /* block end [id:"PlayerStats_block_60"] */
        /* block comment [block:"PlayerStats_block_61" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_61" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        ARCH.put(/* param start [id:"PlayerStats_value_219" type:"text"] */"ranger"/* param end [id:"PlayerStats_value_219"] */, new Archetype(/* param start [id:"PlayerStats_value_220" type:"text"] */"ranger"/* param end [id:"PlayerStats_value_220"] */, /* param start [id:"PlayerStats_value_221" type:"number"] */10/* param end [id:"PlayerStats_value_221"] */, /* param start [id:"PlayerStats_value_222" type:"boolean"] */true/* param end [id:"PlayerStats_value_222"] */, /* param start [id:"PlayerStats_value_223" type:"text"] */"Wisdom"/* param end [id:"PlayerStats_value_223"] */));
        /* block end [id:"PlayerStats_block_61"] */
        /* block comment [block:"PlayerStats_block_62" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_62" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        ARCH.put(/* param start [id:"PlayerStats_value_224" type:"text"] */"rogue"/* param end [id:"PlayerStats_value_224"] */, new Archetype(/* param start [id:"PlayerStats_value_225" type:"text"] */"rogue"/* param end [id:"PlayerStats_value_225"] */, /* param start [id:"PlayerStats_value_226" type:"number"] */8/* param end [id:"PlayerStats_value_226"] */, /* param start [id:"PlayerStats_value_227" type:"boolean"] */false/* param end [id:"PlayerStats_value_227"] */, /* param start [id:"PlayerStats_value_228" type:"value"] */null/* param end [id:"PlayerStats_value_228"] */));
        /* block end [id:"PlayerStats_block_62"] */
        /* block comment [block:"PlayerStats_block_63" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_63" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        ARCH.put(/* param start [id:"PlayerStats_value_229" type:"text"] */"sorcerer"/* param end [id:"PlayerStats_value_229"] */, new Archetype(/* param start [id:"PlayerStats_value_230" type:"text"] */"sorcerer"/* param end [id:"PlayerStats_value_230"] */, /* param start [id:"PlayerStats_value_231" type:"number"] */6/* param end [id:"PlayerStats_value_231"] */, /* param start [id:"PlayerStats_value_232" type:"boolean"] */true/* param end [id:"PlayerStats_value_232"] */, /* param start [id:"PlayerStats_value_233" type:"text"] */"Charisma"/* param end [id:"PlayerStats_value_233"] */));
        /* block end [id:"PlayerStats_block_63"] */
        /* block comment [block:"PlayerStats_block_64" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_64" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        ARCH.put(/* param start [id:"PlayerStats_value_234" type:"text"] */"warlock"/* param end [id:"PlayerStats_value_234"] */, new Archetype(/* param start [id:"PlayerStats_value_235" type:"text"] */"warlock"/* param end [id:"PlayerStats_value_235"] */, /* param start [id:"PlayerStats_value_236" type:"number"] */8/* param end [id:"PlayerStats_value_236"] */, /* param start [id:"PlayerStats_value_237" type:"boolean"] */true/* param end [id:"PlayerStats_value_237"] */, /* param start [id:"PlayerStats_value_238" type:"text"] */"Charisma"/* param end [id:"PlayerStats_value_238"] */));
        /* block end [id:"PlayerStats_block_64"] */
        /* block comment [block:"PlayerStats_block_65" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_65" category:"Variables" label:"set put" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        ARCH.put(/* param start [id:"PlayerStats_value_239" type:"text"] */"wizard"/* param end [id:"PlayerStats_value_239"] */, new Archetype(/* param start [id:"PlayerStats_value_240" type:"text"] */"wizard"/* param end [id:"PlayerStats_value_240"] */, /* param start [id:"PlayerStats_value_241" type:"number"] */6/* param end [id:"PlayerStats_value_241"] */, /* param start [id:"PlayerStats_value_242" type:"boolean"] */true/* param end [id:"PlayerStats_value_242"] */, /* param start [id:"PlayerStats_value_243" type:"text"] */"Intelligence"/* param end [id:"PlayerStats_value_243"] */));
        /* block end [id:"PlayerStats_block_65"] */
    }

    private static int mod(int score) {
        /* block comment [block:"PlayerStats_block_66" text:"Returns a value or ends the current method."] */
        /* block start [id:"PlayerStats_block_66" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return (int) Math.floor((score - /* param start [id:"PlayerStats_value_244" type:"number"] */10/* param end [id:"PlayerStats_value_244"] */) / /* param start [id:"PlayerStats_value_245" type:"number"] */2.0/* param end [id:"PlayerStats_value_245"] */);
        /* block end [id:"PlayerStats_block_66"] */
    }

    private static void refresh(Stats s) {
        /* block comment [block:"PlayerStats_block_67" text:"Runs the mod action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_67" category:"My Blocks" label:"mod" shape:"command" docs:"Runs the mod action. Values in this block are editable inputs."] */
        s.strengthModifier = mod(s.strengthScore);
        /* block end [id:"PlayerStats_block_67"] */
        /* block comment [block:"PlayerStats_block_68" text:"Runs the mod action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_68" category:"My Blocks" label:"mod" shape:"command" docs:"Runs the mod action. Values in this block are editable inputs."] */
        s.dexterityModifier = mod(s.dexterityScore);
        /* block end [id:"PlayerStats_block_68"] */
        /* block comment [block:"PlayerStats_block_69" text:"Runs the mod action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_69" category:"My Blocks" label:"mod" shape:"command" docs:"Runs the mod action. Values in this block are editable inputs."] */
        s.constitutionModifier = mod(s.constitutionScore);
        /* block end [id:"PlayerStats_block_69"] */
        /* block comment [block:"PlayerStats_block_70" text:"Runs the mod action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_70" category:"My Blocks" label:"mod" shape:"command" docs:"Runs the mod action. Values in this block are editable inputs."] */
        s.intelligenceModifier = mod(s.intelligenceScore);
        /* block end [id:"PlayerStats_block_70"] */
        /* block comment [block:"PlayerStats_block_71" text:"Runs the mod action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_71" category:"My Blocks" label:"mod" shape:"command" docs:"Runs the mod action. Values in this block are editable inputs."] */
        s.wisdomModifier = mod(s.wisdomScore);
        /* block end [id:"PlayerStats_block_71"] */
        /* block comment [block:"PlayerStats_block_72" text:"Runs the mod action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_72" category:"My Blocks" label:"mod" shape:"command" docs:"Runs the mod action. Values in this block are editable inputs."] */
        s.charismaModifier = mod(s.charismaScore);
        /* block end [id:"PlayerStats_block_72"] */
    }

    public static int effectiveAC() {
        /* block comment [block:"PlayerStats_block_73" text:"Returns a value or ends the current method."] */
        /* block start [id:"PlayerStats_block_73" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return (stats == /* param start [id:"PlayerStats_value_246" type:"value"] */null/* param end [id:"PlayerStats_value_246"] */ ? /* param start [id:"PlayerStats_value_247" type:"number"] */10/* param end [id:"PlayerStats_value_247"] */ : stats.armourClass) + bonusAC;
        /* block end [id:"PlayerStats_block_73"] */
    }

    public static int effectiveMelee() {
        /* block comment [block:"PlayerStats_block_74" text:"Returns a value or ends the current method."] */
        /* block start [id:"PlayerStats_block_74" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return (stats == /* param start [id:"PlayerStats_value_248" type:"value"] */null/* param end [id:"PlayerStats_value_248"] */ ? /* param start [id:"PlayerStats_value_249" type:"number"] */0/* param end [id:"PlayerStats_value_249"] */ : getMeleeAttackBonus()) + bonusMelee;
        /* block end [id:"PlayerStats_block_74"] */
    }

    public static void init(String name, String className, int[] scores) {
        /* block comment [block:"PlayerStats_block_75" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_75" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"PlayerStats_block_1" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"PlayerStats_block_1" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (scores == /* param start [id:"PlayerStats_value_250" type:"value"] */null/* param end [id:"PlayerStats_value_250"] */ || scores.length != /* param start [id:"PlayerStats_value_251" type:"number"] */6/* param end [id:"PlayerStats_value_251"] */) scores = getStandardArray();
        /* block end [id:"PlayerStats_block_75"] */
        /* block end [id:"PlayerStats_block_1"] */
        /* block comment [block:"PlayerStats_block_76" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_76" category:"Variables" label:"set key" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        String key = className == /* param start [id:"PlayerStats_value_252" type:"value"] */null/* param end [id:"PlayerStats_value_252"] */ ? /* param start [id:"PlayerStats_value_253" type:"text"] */"fighter"/* param end [id:"PlayerStats_value_253"] */ : className.toLowerCase();
        /* block end [id:"PlayerStats_block_76"] */
        /* block comment [block:"PlayerStats_block_77" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_77" category:"Variables" label:"set a" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Archetype a = ARCH.get(key);
        /* block end [id:"PlayerStats_block_77"] */
        /* block comment [block:"PlayerStats_block_78" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_78" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"PlayerStats_block_2" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"PlayerStats_block_2" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (a == /* param start [id:"PlayerStats_value_254" type:"value"] */null/* param end [id:"PlayerStats_value_254"] */) a = new Archetype(key, /* param start [id:"PlayerStats_value_255" type:"number"] */8/* param end [id:"PlayerStats_value_255"] */, /* param start [id:"PlayerStats_value_256" type:"boolean"] */false/* param end [id:"PlayerStats_value_256"] */, /* param start [id:"PlayerStats_value_257" type:"value"] */null/* param end [id:"PlayerStats_value_257"] */);
        /* block end [id:"PlayerStats_block_78"] */
        /* block end [id:"PlayerStats_block_2"] */
        /* block comment [block:"PlayerStats_block_79" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_79" category:"Variables" label:"set s" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stats s = new Stats();
        /* block end [id:"PlayerStats_block_79"] */
        /* block comment [block:"PlayerStats_block_80" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"PlayerStats_block_80" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        s.heroClass = key;
        /* block end [id:"PlayerStats_block_80"] */
        /* block comment [block:"PlayerStats_block_81" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"PlayerStats_block_81" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        s.hitDie = a.hitDie;
        /* block end [id:"PlayerStats_block_81"] */
        /* block comment [block:"PlayerStats_block_82" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"PlayerStats_block_82" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        s.isSpellcaster = a.isCaster;
        /* block end [id:"PlayerStats_block_82"] */
        /* block comment [block:"PlayerStats_block_83" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"PlayerStats_block_83" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        s.spellcastingAbility = a.ability;
        /* block end [id:"PlayerStats_block_83"] */
        /* block comment [block:"PlayerStats_block_84" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"PlayerStats_block_84" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        s.level = /* param start [id:"PlayerStats_value_258" type:"number"] */1/* param end [id:"PlayerStats_value_258"] */;
        /* block end [id:"PlayerStats_block_84"] */
        /* block comment [block:"PlayerStats_block_85" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"PlayerStats_block_85" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        s.proficiency = /* param start [id:"PlayerStats_value_259" type:"number"] */2/* param end [id:"PlayerStats_value_259"] */;
        /* block end [id:"PlayerStats_block_85"] */
        /* block comment [block:"PlayerStats_block_86" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"PlayerStats_block_86" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        s.strengthScore = scores[/* param start [id:"PlayerStats_value_260" type:"number"] */0/* param end [id:"PlayerStats_value_260"] */];
        /* block end [id:"PlayerStats_block_86"] */
        /* block comment [block:"PlayerStats_block_87" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"PlayerStats_block_87" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        s.dexterityScore = scores[/* param start [id:"PlayerStats_value_261" type:"number"] */1/* param end [id:"PlayerStats_value_261"] */];
        /* block end [id:"PlayerStats_block_87"] */
        /* block comment [block:"PlayerStats_block_88" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"PlayerStats_block_88" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        s.constitutionScore = scores[/* param start [id:"PlayerStats_value_262" type:"number"] */2/* param end [id:"PlayerStats_value_262"] */];
        /* block end [id:"PlayerStats_block_88"] */
        /* block comment [block:"PlayerStats_block_89" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"PlayerStats_block_89" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        s.intelligenceScore = scores[/* param start [id:"PlayerStats_value_263" type:"number"] */3/* param end [id:"PlayerStats_value_263"] */];
        /* block end [id:"PlayerStats_block_89"] */
        /* block comment [block:"PlayerStats_block_90" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"PlayerStats_block_90" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        s.wisdomScore = scores[/* param start [id:"PlayerStats_value_264" type:"number"] */4/* param end [id:"PlayerStats_value_264"] */];
        /* block end [id:"PlayerStats_block_90"] */
        /* block comment [block:"PlayerStats_block_91" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"PlayerStats_block_91" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        s.charismaScore = scores[/* param start [id:"PlayerStats_value_265" type:"number"] */5/* param end [id:"PlayerStats_value_265"] */];
        /* block end [id:"PlayerStats_block_91"] */
        /* block comment [block:"PlayerStats_block_92" text:"Calls the refresh method using its declared arguments."] */
        /* block start [id:"PlayerStats_block_92" category:"My Blocks" label:"call refresh" shape:"command" docs:"Calls the refresh method using its declared arguments."] */
        refresh(s);
        /* block end [id:"PlayerStats_block_92"] */
        /* block comment [block:"PlayerStats_block_93" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"PlayerStats_block_93" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        s.maxHp = a.hitDie + s.constitutionModifier;
        /* block end [id:"PlayerStats_block_93"] */
        /* block comment [block:"PlayerStats_block_94" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_94" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"PlayerStats_block_3" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"PlayerStats_block_3" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (s.maxHp < /* param start [id:"PlayerStats_value_266" type:"number"] */1/* param end [id:"PlayerStats_value_266"] */) s.maxHp = /* param start [id:"PlayerStats_value_267" type:"number"] */1/* param end [id:"PlayerStats_value_267"] */;
        /* block end [id:"PlayerStats_block_94"] */
        /* block end [id:"PlayerStats_block_3"] */
        /* block comment [block:"PlayerStats_block_95" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"PlayerStats_block_95" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        s.hp = s.maxHp;
        /* block end [id:"PlayerStats_block_95"] */
        /* block comment [block:"PlayerStats_block_96" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"PlayerStats_block_96" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        s.armourClass = /* param start [id:"PlayerStats_value_268" type:"number"] */10/* param end [id:"PlayerStats_value_268"] */ + s.dexterityModifier;
        /* block end [id:"PlayerStats_block_96"] */
        /* block comment [block:"PlayerStats_block_97" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"PlayerStats_block_97" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        s.currentXp = /* param start [id:"PlayerStats_value_269" type:"number"] */0/* param end [id:"PlayerStats_value_269"] */;
        /* block end [id:"PlayerStats_block_97"] */
        /* block comment [block:"PlayerStats_block_98" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"PlayerStats_block_98" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        stats = s;
        /* block end [id:"PlayerStats_block_98"] */
        /* block comment [block:"PlayerStats_block_99" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"PlayerStats_block_99" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        heroName = name;
        /* block end [id:"PlayerStats_block_99"] */
        /* block comment [block:"PlayerStats_block_100" text:"Runs the compute max mana action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_100" category:"My Blocks" label:"compute max mana" shape:"command" docs:"Runs the compute max mana action. Values in this block are editable inputs."] */
        maxMana = computeMaxMana();
        /* block end [id:"PlayerStats_block_100"] */
        /* block comment [block:"PlayerStats_block_101" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"PlayerStats_block_101" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        mana = maxMana;
        /* block end [id:"PlayerStats_block_101"] */
        /* block comment [block:"PlayerStats_block_102" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"PlayerStats_block_102" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        ready = /* param start [id:"PlayerStats_value_270" type:"boolean"] */true/* param end [id:"PlayerStats_value_270"] */;
        /* block end [id:"PlayerStats_block_102"] */
    }

    private static int spellMod() {
        /* block comment [block:"PlayerStats_block_103" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_103" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"PlayerStats_block_4" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"PlayerStats_block_4" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (stats == /* param start [id:"PlayerStats_value_271" type:"value"] */null/* param end [id:"PlayerStats_value_271"] */ || !stats.isSpellcaster) return -/* param start [id:"PlayerStats_value_272" type:"number"] */1/* param end [id:"PlayerStats_value_272"] */;
        /* block end [id:"PlayerStats_block_103"] */
        /* block end [id:"PlayerStats_block_4"] */
        /* block comment [block:"PlayerStats_block_104" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_104" category:"Variables" label:"set ab" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        String ab = stats.spellcastingAbility;
        /* block end [id:"PlayerStats_block_104"] */
        /* block comment [block:"PlayerStats_block_105" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_105" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"PlayerStats_block_5" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"PlayerStats_block_5" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (ab == /* param start [id:"PlayerStats_value_273" type:"value"] */null/* param end [id:"PlayerStats_value_273"] */) return -/* param start [id:"PlayerStats_value_274" type:"number"] */1/* param end [id:"PlayerStats_value_274"] */;
        /* block end [id:"PlayerStats_block_105"] */
        /* block end [id:"PlayerStats_block_5"] */
        /* block comment [block:"PlayerStats_block_106" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_106" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"PlayerStats_block_6" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"PlayerStats_block_6" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (ab.equals(/* param start [id:"PlayerStats_value_275" type:"text"] */"Intelligence"/* param end [id:"PlayerStats_value_275"] */)) return stats.intelligenceModifier;
        /* block end [id:"PlayerStats_block_106"] */
        /* block end [id:"PlayerStats_block_6"] */
        /* block comment [block:"PlayerStats_block_107" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_107" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"PlayerStats_block_7" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"PlayerStats_block_7" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (ab.equals(/* param start [id:"PlayerStats_value_276" type:"text"] */"Wisdom"/* param end [id:"PlayerStats_value_276"] */)) return stats.wisdomModifier;
        /* block end [id:"PlayerStats_block_107"] */
        /* block end [id:"PlayerStats_block_7"] */
        /* block comment [block:"PlayerStats_block_108" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_108" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"PlayerStats_block_8" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"PlayerStats_block_8" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (ab.equals(/* param start [id:"PlayerStats_value_277" type:"text"] */"Charisma"/* param end [id:"PlayerStats_value_277"] */)) return stats.charismaModifier;
        /* block end [id:"PlayerStats_block_108"] */
        /* block end [id:"PlayerStats_block_8"] */
        /* block comment [block:"PlayerStats_block_109" text:"Returns a value or ends the current method."] */
        /* block start [id:"PlayerStats_block_109" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return -/* param start [id:"PlayerStats_value_278" type:"number"] */1/* param end [id:"PlayerStats_value_278"] */;
        /* block end [id:"PlayerStats_block_109"] */
    }

    public static int computeMaxMana() {
        /* block comment [block:"PlayerStats_block_110" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_110" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"PlayerStats_block_9" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"PlayerStats_block_9" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (stats == /* param start [id:"PlayerStats_value_279" type:"value"] */null/* param end [id:"PlayerStats_value_279"] */) return /* param start [id:"PlayerStats_value_280" type:"number"] */10/* param end [id:"PlayerStats_value_280"] */;
        /* block end [id:"PlayerStats_block_110"] */
        /* block end [id:"PlayerStats_block_9"] */
        /* block comment [block:"PlayerStats_block_111" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_111" category:"Variables" label:"set sm" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int sm = spellMod();
        /* block end [id:"PlayerStats_block_111"] */
        /* block comment [block:"PlayerStats_block_112" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_112" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"PlayerStats_block_10" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"PlayerStats_block_10" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (sm >= /* param start [id:"PlayerStats_value_281" type:"number"] */0/* param end [id:"PlayerStats_value_281"] */) return /* param start [id:"PlayerStats_value_282" type:"number"] */10/* param end [id:"PlayerStats_value_282"] */ + sm * /* param start [id:"PlayerStats_value_283" type:"number"] */5/* param end [id:"PlayerStats_value_283"] */ + stats.level * /* param start [id:"PlayerStats_value_284" type:"number"] */2/* param end [id:"PlayerStats_value_284"] */;
        /* block end [id:"PlayerStats_block_112"] */
        /* block end [id:"PlayerStats_block_10"] */
        /* block comment [block:"PlayerStats_block_113" text:"Returns a value or ends the current method."] */
        /* block start [id:"PlayerStats_block_113" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return /* param start [id:"PlayerStats_value_285" type:"number"] */4/* param end [id:"PlayerStats_value_285"] */ + stats.level * /* param start [id:"PlayerStats_value_286" type:"number"] */2/* param end [id:"PlayerStats_value_286"] */;
        /* block end [id:"PlayerStats_block_113"] */
    }

    public static int getMeleeAttackBonus() {
        /* block comment [block:"PlayerStats_block_114" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_114" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"PlayerStats_block_11" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"PlayerStats_block_11" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (stats == /* param start [id:"PlayerStats_value_287" type:"value"] */null/* param end [id:"PlayerStats_value_287"] */) return /* param start [id:"PlayerStats_value_288" type:"number"] */0/* param end [id:"PlayerStats_value_288"] */;
        /* block end [id:"PlayerStats_block_114"] */
        /* block end [id:"PlayerStats_block_11"] */
        /* block comment [block:"PlayerStats_block_115" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_115" category:"Variables" label:"set cn" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        String cn = stats.heroClass;
        /* block end [id:"PlayerStats_block_115"] */
        /* block comment [block:"PlayerStats_block_116" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_116" category:"Variables" label:"set finesse" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        boolean finesse = cn.equals(/* param start [id:"PlayerStats_value_289" type:"text"] */"rogue"/* param end [id:"PlayerStats_value_289"] */) || cn.equals(/* param start [id:"PlayerStats_value_290" type:"text"] */"monk"/* param end [id:"PlayerStats_value_290"] */) || cn.equals(/* param start [id:"PlayerStats_value_291" type:"text"] */"ranger"/* param end [id:"PlayerStats_value_291"] */);
        /* block end [id:"PlayerStats_block_116"] */
        /* block comment [block:"PlayerStats_block_117" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_117" category:"Variables" label:"set m" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int m = finesse ? Math.max(stats.strengthModifier, stats.dexterityModifier) : stats.strengthModifier;
        /* block end [id:"PlayerStats_block_117"] */
        /* block comment [block:"PlayerStats_block_118" text:"Returns a value or ends the current method."] */
        /* block start [id:"PlayerStats_block_118" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return m + stats.proficiency;
        /* block end [id:"PlayerStats_block_118"] */
    }

    public static int getSpellAttackBonus() {
        /* block comment [block:"PlayerStats_block_119" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_119" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"PlayerStats_block_12" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"PlayerStats_block_12" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (stats == /* param start [id:"PlayerStats_value_292" type:"value"] */null/* param end [id:"PlayerStats_value_292"] */) return bonusSpell;
        /* block end [id:"PlayerStats_block_119"] */
        /* block end [id:"PlayerStats_block_12"] */
        /* block comment [block:"PlayerStats_block_120" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_120" category:"Variables" label:"set sm" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int sm = spellMod();
        /* block end [id:"PlayerStats_block_120"] */
        /* block comment [block:"PlayerStats_block_121" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_121" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"PlayerStats_block_13" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"PlayerStats_block_13" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (sm < /* param start [id:"PlayerStats_value_293" type:"number"] */0/* param end [id:"PlayerStats_value_293"] */) return stats.proficiency + bonusSpell;
        /* block end [id:"PlayerStats_block_121"] */
        /* block end [id:"PlayerStats_block_13"] */
        /* block comment [block:"PlayerStats_block_122" text:"Returns a value or ends the current method."] */
        /* block start [id:"PlayerStats_block_122" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return sm + stats.proficiency + bonusSpell;
        /* block end [id:"PlayerStats_block_122"] */
    }

    public static int getSpellSaveDC() {
        /* block comment [block:"PlayerStats_block_123" text:"Returns a value or ends the current method."] */
        /* block start [id:"PlayerStats_block_123" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return /* param start [id:"PlayerStats_value_294" type:"number"] */8/* param end [id:"PlayerStats_value_294"] */ + getSpellAttackBonus();
        /* block end [id:"PlayerStats_block_123"] */
    }

    public static boolean takeDamage(int amount) {
        /* block comment [block:"PlayerStats_block_124" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_124" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"PlayerStats_block_14" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"PlayerStats_block_14" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (stats == /* param start [id:"PlayerStats_value_295" type:"value"] */null/* param end [id:"PlayerStats_value_295"] */ || amount <= /* param start [id:"PlayerStats_value_296" type:"number"] */0/* param end [id:"PlayerStats_value_296"] */) return /* param start [id:"PlayerStats_value_297" type:"boolean"] */true/* param end [id:"PlayerStats_value_297"] */;
        /* block end [id:"PlayerStats_block_124"] */
        /* block end [id:"PlayerStats_block_14"] */
        /* block comment [block:"PlayerStats_block_125" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_125" category:"Variables" label:"set left" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int left = amount;
        /* block end [id:"PlayerStats_block_125"] */
        /* block comment [block:"PlayerStats_block_15" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"PlayerStats_block_15" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (stats.tempHp > /* param start [id:"PlayerStats_value_298" type:"number"] */0/* param end [id:"PlayerStats_value_298"] */) {
            /* block comment [block:"PlayerStats_block_126" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"PlayerStats_block_126" category:"Variables" label:"set absorbed" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int absorbed = Math.min(stats.tempHp, left);
            /* block end [id:"PlayerStats_block_126"] */
            /* block comment [block:"PlayerStats_block_127" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"PlayerStats_block_127" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            stats.tempHp -= absorbed;
            /* block end [id:"PlayerStats_block_127"] */
            /* block comment [block:"PlayerStats_block_128" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"PlayerStats_block_128" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            left -= absorbed;
            /* block end [id:"PlayerStats_block_128"] */
        }
        /* block end [id:"PlayerStats_block_15"] */
        /* block comment [block:"PlayerStats_block_129" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"PlayerStats_block_129" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        stats.hp -= left;
        /* block end [id:"PlayerStats_block_129"] */
        /* block comment [block:"PlayerStats_block_130" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_130" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"PlayerStats_block_16" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"PlayerStats_block_16" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (stats.hp < /* param start [id:"PlayerStats_value_299" type:"number"] */0/* param end [id:"PlayerStats_value_299"] */) stats.hp = /* param start [id:"PlayerStats_value_300" type:"number"] */0/* param end [id:"PlayerStats_value_300"] */;
        /* block end [id:"PlayerStats_block_130"] */
        /* block end [id:"PlayerStats_block_16"] */
        /* block comment [block:"PlayerStats_block_131" text:"Returns a value or ends the current method."] */
        /* block start [id:"PlayerStats_block_131" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return stats.hp > /* param start [id:"PlayerStats_value_301" type:"number"] */0/* param end [id:"PlayerStats_value_301"] */;
        /* block end [id:"PlayerStats_block_131"] */
    }

    public static void heal(int amount) {
        /* block comment [block:"PlayerStats_block_132" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_132" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"PlayerStats_block_17" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"PlayerStats_block_17" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (stats == /* param start [id:"PlayerStats_value_302" type:"value"] */null/* param end [id:"PlayerStats_value_302"] */ || amount <= /* param start [id:"PlayerStats_value_303" type:"number"] */0/* param end [id:"PlayerStats_value_303"] */) return;
        /* block end [id:"PlayerStats_block_132"] */
        /* block end [id:"PlayerStats_block_17"] */
        /* block comment [block:"PlayerStats_block_133" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"PlayerStats_block_133" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        stats.hp += amount;
        /* block end [id:"PlayerStats_block_133"] */
        /* block comment [block:"PlayerStats_block_134" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_134" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"PlayerStats_block_18" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"PlayerStats_block_18" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (stats.hp > stats.maxHp) stats.hp = stats.maxHp;
        /* block end [id:"PlayerStats_block_134"] */
        /* block end [id:"PlayerStats_block_18"] */
    }

    public static boolean spendMana(int amount) {
        /* block comment [block:"PlayerStats_block_135" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_135" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"PlayerStats_block_19" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"PlayerStats_block_19" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (amount <= /* param start [id:"PlayerStats_value_304" type:"number"] */0/* param end [id:"PlayerStats_value_304"] */) return /* param start [id:"PlayerStats_value_305" type:"boolean"] */true/* param end [id:"PlayerStats_value_305"] */;
        /* block end [id:"PlayerStats_block_135"] */
        /* block end [id:"PlayerStats_block_19"] */
        /* block comment [block:"PlayerStats_block_136" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_136" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"PlayerStats_block_20" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"PlayerStats_block_20" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (mana < amount) return /* param start [id:"PlayerStats_value_306" type:"boolean"] */false/* param end [id:"PlayerStats_value_306"] */;
        /* block end [id:"PlayerStats_block_136"] */
        /* block end [id:"PlayerStats_block_20"] */
        /* block comment [block:"PlayerStats_block_137" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"PlayerStats_block_137" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        mana -= amount;
        /* block end [id:"PlayerStats_block_137"] */
        /* block comment [block:"PlayerStats_block_138" text:"Returns a value or ends the current method."] */
        /* block start [id:"PlayerStats_block_138" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return /* param start [id:"PlayerStats_value_307" type:"boolean"] */true/* param end [id:"PlayerStats_value_307"] */;
        /* block end [id:"PlayerStats_block_138"] */
    }

    public static void restoreMana(int amount) {
        /* block comment [block:"PlayerStats_block_139" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_139" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"PlayerStats_block_21" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"PlayerStats_block_21" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (amount <= /* param start [id:"PlayerStats_value_308" type:"number"] */0/* param end [id:"PlayerStats_value_308"] */) return;
        /* block end [id:"PlayerStats_block_139"] */
        /* block end [id:"PlayerStats_block_21"] */
        /* block comment [block:"PlayerStats_block_140" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"PlayerStats_block_140" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        mana += amount;
        /* block end [id:"PlayerStats_block_140"] */
        /* block comment [block:"PlayerStats_block_141" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_141" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"PlayerStats_block_22" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"PlayerStats_block_22" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (mana > maxMana) mana = maxMana;
        /* block end [id:"PlayerStats_block_141"] */
        /* block end [id:"PlayerStats_block_22"] */
    }

    public static void addXp(int amount) {
        /* block comment [block:"PlayerStats_block_142" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_142" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"PlayerStats_block_23" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"PlayerStats_block_23" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (stats == /* param start [id:"PlayerStats_value_309" type:"value"] */null/* param end [id:"PlayerStats_value_309"] */ || amount <= /* param start [id:"PlayerStats_value_310" type:"number"] */0/* param end [id:"PlayerStats_value_310"] */) return;
        /* block end [id:"PlayerStats_block_142"] */
        /* block end [id:"PlayerStats_block_23"] */
        /* block comment [block:"PlayerStats_block_143" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"PlayerStats_block_143" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        stats.currentXp += amount;
        /* block end [id:"PlayerStats_block_143"] */
        /* block comment [block:"PlayerStats_block_24" text:"Repeats the body while its Boolean condition is true."] */
        /* block start [id:"PlayerStats_block_24" category:"Control" label:"repeat while" shape:"control" docs:"Repeats the body while its Boolean condition is true."] */
        while (stats.currentXp >= stats.level * /* param start [id:"PlayerStats_value_311" type:"number"] */300/* param end [id:"PlayerStats_value_311"] */) {
            /* block comment [block:"PlayerStats_block_144" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"PlayerStats_block_144" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            stats.currentXp -= stats.level * /* param start [id:"PlayerStats_value_312" type:"number"] */300/* param end [id:"PlayerStats_value_312"] */;
            /* block end [id:"PlayerStats_block_144"] */
            /* block comment [block:"PlayerStats_block_145" text:"Calls the levelUp method using its declared arguments."] */
            /* block start [id:"PlayerStats_block_145" category:"My Blocks" label:"call level up" shape:"command" docs:"Calls the levelUp method using its declared arguments."] */
            levelUp(stats);
            /* block end [id:"PlayerStats_block_145"] */
            /* block comment [block:"PlayerStats_block_146" text:"Runs the compute max mana action. Values in this block are editable inputs."] */
            /* block start [id:"PlayerStats_block_146" category:"My Blocks" label:"compute max mana" shape:"command" docs:"Runs the compute max mana action. Values in this block are editable inputs."] */
            maxMana = computeMaxMana();
            /* block end [id:"PlayerStats_block_146"] */
            /* block comment [block:"PlayerStats_block_147" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"PlayerStats_block_147" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            mana = maxMana;
            /* block end [id:"PlayerStats_block_147"] */
        }
        /* block end [id:"PlayerStats_block_24"] */
    }

    private static void levelUp(Stats s) {
        /* block comment [block:"PlayerStats_block_148" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"PlayerStats_block_148" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        s.level++;
        /* block end [id:"PlayerStats_block_148"] */
        /* block comment [block:"PlayerStats_block_149" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_149" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"PlayerStats_block_25" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"PlayerStats_block_25" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (s.level >= /* param start [id:"PlayerStats_value_313" type:"number"] */17/* param end [id:"PlayerStats_value_313"] */) s.proficiency = /* param start [id:"PlayerStats_value_314" type:"number"] */6/* param end [id:"PlayerStats_value_314"] */;
        /* block end [id:"PlayerStats_block_149"] */
        /* block comment [block:"PlayerStats_block_150" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_150" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        else /* block comment [block:"PlayerStats_block_26" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"PlayerStats_block_26" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (s.level >= /* param start [id:"PlayerStats_value_315" type:"number"] */13/* param end [id:"PlayerStats_value_315"] */) s.proficiency = /* param start [id:"PlayerStats_value_316" type:"number"] */5/* param end [id:"PlayerStats_value_316"] */;
        /* block end [id:"PlayerStats_block_150"] */
        /* block end [id:"PlayerStats_block_25"] */
        /* block comment [block:"PlayerStats_block_151" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_151" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        else /* block comment [block:"PlayerStats_block_27" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"PlayerStats_block_27" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (s.level >= /* param start [id:"PlayerStats_value_317" type:"number"] */9/* param end [id:"PlayerStats_value_317"] */) s.proficiency = /* param start [id:"PlayerStats_value_318" type:"number"] */4/* param end [id:"PlayerStats_value_318"] */;
        /* block end [id:"PlayerStats_block_151"] */
        /* block end [id:"PlayerStats_block_26"] */
        /* block comment [block:"PlayerStats_block_152" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_152" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        else /* block comment [block:"PlayerStats_block_28" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"PlayerStats_block_28" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (s.level >= /* param start [id:"PlayerStats_value_319" type:"number"] */5/* param end [id:"PlayerStats_value_319"] */) s.proficiency = /* param start [id:"PlayerStats_value_320" type:"number"] */3/* param end [id:"PlayerStats_value_320"] */;
        /* block end [id:"PlayerStats_block_152"] */
        /* block end [id:"PlayerStats_block_27"] */
        /* block comment [block:"PlayerStats_block_153" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"PlayerStats_block_153" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        else s.proficiency = /* param start [id:"PlayerStats_value_321" type:"number"] */2/* param end [id:"PlayerStats_value_321"] */;
        /* block end [id:"PlayerStats_block_153"] */
        /* block end [id:"PlayerStats_block_28"] */
        /* block comment [block:"PlayerStats_block_154" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_154" category:"Variables" label:"set gain" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int gain = dice.nextInt(s.hitDie) + /* param start [id:"PlayerStats_value_322" type:"number"] */1/* param end [id:"PlayerStats_value_322"] */ + s.constitutionModifier;
        /* block end [id:"PlayerStats_block_154"] */
        /* block comment [block:"PlayerStats_block_155" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_155" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"PlayerStats_block_29" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"PlayerStats_block_29" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (gain < /* param start [id:"PlayerStats_value_323" type:"number"] */1/* param end [id:"PlayerStats_value_323"] */) gain = /* param start [id:"PlayerStats_value_324" type:"number"] */1/* param end [id:"PlayerStats_value_324"] */;
        /* block end [id:"PlayerStats_block_155"] */
        /* block end [id:"PlayerStats_block_29"] */
        /* block comment [block:"PlayerStats_block_156" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"PlayerStats_block_156" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        s.maxHp += gain;
        /* block end [id:"PlayerStats_block_156"] */
        /* block comment [block:"PlayerStats_block_157" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"PlayerStats_block_157" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        s.hp += gain;
        /* block end [id:"PlayerStats_block_157"] */
        /* block comment [block:"PlayerStats_block_158" text:"Calls the refresh method using its declared arguments."] */
        /* block start [id:"PlayerStats_block_158" category:"My Blocks" label:"call refresh" shape:"command" docs:"Calls the refresh method using its declared arguments."] */
        refresh(s);
        /* block end [id:"PlayerStats_block_158"] */
    }

    // ---- score generation helpers (replaces DndCharacterAPI) ----
    public static int[] getStandardArray() {
        return new int[]{15, 14, 13, 12, 10, 8};
    }

    public static int[] rollAbilityScores() {
        /* block comment [block:"PlayerStats_block_159" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_159" category:"Variables" label:"set scores" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int[] scores = new int[/* param start [id:"PlayerStats_value_325" type:"number"] */6/* param end [id:"PlayerStats_value_325"] */];
        /* block end [id:"PlayerStats_block_159"] */
        /* block comment [block:"PlayerStats_block_30" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"PlayerStats_block_30" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (int i = /* param start [id:"PlayerStats_value_326" type:"number"] */0/* param end [id:"PlayerStats_value_326"] */; i < /* param start [id:"PlayerStats_value_327" type:"number"] */6/* param end [id:"PlayerStats_value_327"] */; i++) scores[i] = rollSingle();
        /* block end [id:"PlayerStats_block_30"] */
        /* block comment [block:"PlayerStats_block_160" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_160" category:"Variables" label:"set sort" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Arrays.sort(scores);
        /* block end [id:"PlayerStats_block_160"] */
        /* block comment [block:"PlayerStats_block_161" text:"Returns a value or ends the current method."] */
        /* block start [id:"PlayerStats_block_161" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return scores;
        /* block end [id:"PlayerStats_block_161"] */
    }

    private static int rollSingle() {
        /* block comment [block:"PlayerStats_block_162" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_162" category:"Variables" label:"set sum" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int sum = /* param start [id:"PlayerStats_value_328" type:"number"] */0/* param end [id:"PlayerStats_value_328"] */, lowest = /* param start [id:"PlayerStats_value_329" type:"number"] */7/* param end [id:"PlayerStats_value_329"] */;
        /* block end [id:"PlayerStats_block_162"] */
        /* block comment [block:"PlayerStats_block_31" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"PlayerStats_block_31" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (int i = /* param start [id:"PlayerStats_value_330" type:"number"] */0/* param end [id:"PlayerStats_value_330"] */; i < /* param start [id:"PlayerStats_value_331" type:"number"] */4/* param end [id:"PlayerStats_value_331"] */; i++) {
            /* block comment [block:"PlayerStats_block_163" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"PlayerStats_block_163" category:"Variables" label:"set r" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int r = dice.nextInt(/* param start [id:"PlayerStats_value_332" type:"number"] */6/* param end [id:"PlayerStats_value_332"] */) + /* param start [id:"PlayerStats_value_333" type:"number"] */1/* param end [id:"PlayerStats_value_333"] */;
            /* block end [id:"PlayerStats_block_163"] */
            /* block comment [block:"PlayerStats_block_164" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"PlayerStats_block_164" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            sum += r;
            /* block end [id:"PlayerStats_block_164"] */
            /* block comment [block:"PlayerStats_block_165" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"PlayerStats_block_165" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"PlayerStats_block_32" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"PlayerStats_block_32" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (r < lowest) lowest = r;
            /* block end [id:"PlayerStats_block_165"] */
            /* block end [id:"PlayerStats_block_32"] */
        }
        /* block end [id:"PlayerStats_block_31"] */
        /* block comment [block:"PlayerStats_block_166" text:"Returns a value or ends the current method."] */
        /* block start [id:"PlayerStats_block_166" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return sum - lowest;
        /* block end [id:"PlayerStats_block_166"] */
    }

    public static int[] getPointBuyBaseline() {
        return new int[]{13, 13, 13, 12, 12, 12};
    }

    // helpers for UI display
    public static Archetype archetypeOf(String className) {
        /* block comment [block:"PlayerStats_block_167" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"PlayerStats_block_167" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"PlayerStats_block_33" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"PlayerStats_block_33" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (className == /* param start [id:"PlayerStats_value_334" type:"value"] */null/* param end [id:"PlayerStats_value_334"] */) return /* param start [id:"PlayerStats_value_335" type:"value"] */null/* param end [id:"PlayerStats_value_335"] */;
        /* block end [id:"PlayerStats_block_167"] */
        /* block end [id:"PlayerStats_block_33"] */
        /* block comment [block:"PlayerStats_block_168" text:"Returns a value or ends the current method."] */
        /* block start [id:"PlayerStats_block_168" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return ARCH.get(className.toLowerCase());
        /* block end [id:"PlayerStats_block_168"] */
    }

    public static int hitDieOf(String className) {
        /* block comment [block:"PlayerStats_block_169" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_169" category:"Variables" label:"set a" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Archetype a = archetypeOf(className);
        /* block end [id:"PlayerStats_block_169"] */
        /* block comment [block:"PlayerStats_block_170" text:"Returns a value or ends the current method."] */
        /* block start [id:"PlayerStats_block_170" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return a == /* param start [id:"PlayerStats_value_336" type:"value"] */null/* param end [id:"PlayerStats_value_336"] */ ? /* param start [id:"PlayerStats_value_337" type:"number"] */8/* param end [id:"PlayerStats_value_337"] */ : a.hitDie;
        /* block end [id:"PlayerStats_block_170"] */
    }

    public static boolean isCasterOf(String className) {
        /* block comment [block:"PlayerStats_block_171" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_171" category:"Variables" label:"set a" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Archetype a = archetypeOf(className);
        /* block end [id:"PlayerStats_block_171"] */
        /* block comment [block:"PlayerStats_block_172" text:"Returns a value or ends the current method."] */
        /* block start [id:"PlayerStats_block_172" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return a != /* param start [id:"PlayerStats_value_338" type:"value"] */null/* param end [id:"PlayerStats_value_338"] */ && a.isCaster;
        /* block end [id:"PlayerStats_block_172"] */
    }

    public static String spellAbilityOf(String className) {
        /* block comment [block:"PlayerStats_block_173" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"PlayerStats_block_173" category:"Variables" label:"set a" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Archetype a = archetypeOf(className);
        /* block end [id:"PlayerStats_block_173"] */
        /* block comment [block:"PlayerStats_block_174" text:"Returns a value or ends the current method."] */
        /* block start [id:"PlayerStats_block_174" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return a == /* param start [id:"PlayerStats_value_339" type:"value"] */null/* param end [id:"PlayerStats_value_339"] */ ? /* param start [id:"PlayerStats_value_340" type:"value"] */null/* param end [id:"PlayerStats_value_340"] */ : a.ability;
        /* block end [id:"PlayerStats_block_174"] */
    }
}
