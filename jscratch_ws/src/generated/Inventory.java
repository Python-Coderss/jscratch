package generated;

import com.jscratch.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
* Player inventory: 12-slot bag, gold, one equipped item per slot.
* Equipped bonuses flow into combat via PlayerStats bonus fields.
*
* <p>Call graph / interactions:
* <ul>
*   <li>Filled by: {@code CharacterCreation} starter kits
*       ({@code grantStarter}), {@code LootDrop.checkAll} pickups,
*       {@code Chest} loot, {@code EnemySprite.die} gold.</li>
*   <li>Driven by: {@code Sprite1} loop (I/Up/Down/Enter) →
*       {@code InventoryPanel} → {@link #equip(int)} → {@link #recalc()}
*       writes {@code PlayerStats.bonusMelee/Dmg/AC/Spell/maxMana}.</li>
*   <li>Read by: {@code PlayerStats.effectiveAC/Melee}, spell bonuses,
*       {@code InventoryPanel.draw} (bag + worn view).</li>
* </ul>
* Java 8 compatible.
*/
public class Inventory {
    public static final int MAX_BAG = 12;

    public static final List<Item> bag = new ArrayList<>();
    public static final Map<String, Item> equipped = new HashMap<>();
    public static int gold = 0;
    /**
    * Change counter for the panel: bumped on every mutation so
    * {@code InventoryPanel} can re-sync its glyph text only when content
    * actually changed instead of rebuilding strings every frame.
    */
    public static int version = 0;

    private static void touch() {
        /* block comment [block:"Inventory_block_24" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Inventory_block_24" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        version++;
        /* block end [id:"Inventory_block_24"] */
    }

    public static boolean add(Item item) {
        /* block comment [block:"Inventory_block_25" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Inventory_block_25" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Inventory_block_1" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Inventory_block_1" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (item == /* param start [id:"Inventory_value_63" type:"value"] */null/* param end [id:"Inventory_value_63"] */) return /* param start [id:"Inventory_value_64" type:"boolean"] */false/* param end [id:"Inventory_value_64"] */;
        /* block end [id:"Inventory_block_25"] */
        /* block end [id:"Inventory_block_1"] */
        /* block comment [block:"Inventory_block_2" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Inventory_block_2" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (bag.size() >= MAX_BAG) {
            /* block comment [block:"Inventory_block_26" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Inventory_block_26" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Stage.getInstance().setVariable(/* param start [id:"Inventory_value_65" type:"text"] */"Msg"/* param end [id:"Inventory_value_65"] */, /* param start [id:"Inventory_value_66" type:"text"] */"Inventory full! (12)"/* param end [id:"Inventory_value_66"] */);
            /* block end [id:"Inventory_block_26"] */
            /* block comment [block:"Inventory_block_27" text:"Returns a value or ends the current method."] */
            /* block start [id:"Inventory_block_27" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return /* param start [id:"Inventory_value_67" type:"boolean"] */false/* param end [id:"Inventory_value_67"] */;
            /* block end [id:"Inventory_block_27"] */
        }
        /* block end [id:"Inventory_block_2"] */
        /* block comment [block:"Inventory_block_28" text:"Calls the add method using its declared arguments."] */
        /* block start [id:"Inventory_block_28" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
        bag.add(item);
        /* block end [id:"Inventory_block_28"] */
        /* block comment [block:"Inventory_block_29" text:"Runs the touch action. Values in this block are editable inputs."] */
        /* block start [id:"Inventory_block_29" category:"Sensing" label:"touch" shape:"command" docs:"Runs the touch action. Values in this block are editable inputs."] */
        touch();
        /* block end [id:"Inventory_block_29"] */
        /* block comment [block:"Inventory_block_30" text:"Returns a value or ends the current method."] */
        /* block start [id:"Inventory_block_30" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return /* param start [id:"Inventory_value_68" type:"boolean"] */true/* param end [id:"Inventory_value_68"] */;
        /* block end [id:"Inventory_block_30"] */
    }

    public static void addGold(int amount) {
        /* block comment [block:"Inventory_block_31" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Inventory_block_31" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Inventory_block_3" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Inventory_block_3" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (amount <= /* param start [id:"Inventory_value_69" type:"number"] */0/* param end [id:"Inventory_value_69"] */) return;
        /* block end [id:"Inventory_block_31"] */
        /* block end [id:"Inventory_block_3"] */
        /* block comment [block:"Inventory_block_32" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Inventory_block_32" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        gold += amount;
        /* block end [id:"Inventory_block_32"] */
        /* block comment [block:"Inventory_block_33" text:"Runs the touch action. Values in this block are editable inputs."] */
        /* block start [id:"Inventory_block_33" category:"Sensing" label:"touch" shape:"command" docs:"Runs the touch action. Values in this block are editable inputs."] */
        touch();
        /* block end [id:"Inventory_block_33"] */
        /* block comment [block:"Inventory_block_34" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Inventory_block_34" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().setVariable(/* param start [id:"Inventory_value_70" type:"text"] */"Gold"/* param end [id:"Inventory_value_70"] */, gold);
        /* block end [id:"Inventory_block_34"] */
    }

    /** Equip bag[index]; the previously equipped item goes back to the bag. */
    public static void equip(int index) {
        /* block comment [block:"Inventory_block_35" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Inventory_block_35" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Inventory_block_4" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Inventory_block_4" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (index < /* param start [id:"Inventory_value_71" type:"number"] */0/* param end [id:"Inventory_value_71"] */ || index >= bag.size()) return;
        /* block end [id:"Inventory_block_35"] */
        /* block end [id:"Inventory_block_4"] */
        /* block comment [block:"Inventory_block_36" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Inventory_block_36" category:"Variables" label:"set item" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Item item = bag.get(index);
        /* block end [id:"Inventory_block_36"] */
        /* block comment [block:"Inventory_block_37" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Inventory_block_37" category:"Variables" label:"set old" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Item old = equipped.get(item.slot);
        /* block end [id:"Inventory_block_37"] */
        /* block comment [block:"Inventory_block_5" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Inventory_block_5" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (old != /* param start [id:"Inventory_value_72" type:"value"] */null/* param end [id:"Inventory_value_72"] */ && bag.size() >= MAX_BAG) {
            // No room to unequip into: bag is full and slot occupied
            /* block comment [block:"Inventory_block_38" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Inventory_block_38" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Stage.getInstance().setVariable(/* param start [id:"Inventory_value_73" type:"text"] */"Msg"/* param end [id:"Inventory_value_73"] */, /* param start [id:"Inventory_value_74" type:"text"] */"Bag full, cannot swap"/* param end [id:"Inventory_value_74"] */);
            /* block end [id:"Inventory_block_38"] */
            /* block comment [block:"Inventory_block_39" text:"Returns a value or ends the current method."] */
            /* block start [id:"Inventory_block_39" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"Inventory_block_39"] */
        }
        /* block end [id:"Inventory_block_5"] */
        /* block comment [block:"Inventory_block_40" text:"Runs the remove action. Values in this block are editable inputs."] */
        /* block start [id:"Inventory_block_40" category:"Motion" label:"remove" shape:"command" docs:"Runs the remove action. Values in this block are editable inputs."] */
        bag.remove(index);
        /* block end [id:"Inventory_block_40"] */
        /* block comment [block:"Inventory_block_41" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Inventory_block_41" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Inventory_block_6" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Inventory_block_6" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (old != /* param start [id:"Inventory_value_75" type:"value"] */null/* param end [id:"Inventory_value_75"] */) bag.add(old);
        /* block end [id:"Inventory_block_41"] */
        /* block end [id:"Inventory_block_6"] */
        /* block comment [block:"Inventory_block_42" text:"Calls the put method using its declared arguments."] */
        /* block start [id:"Inventory_block_42" category:"My Blocks" label:"call put" shape:"command" docs:"Calls the put method using its declared arguments."] */
        equipped.put(item.slot, item);
        /* block end [id:"Inventory_block_42"] */
        /* block comment [block:"Inventory_block_43" text:"Calls the recalc method using its declared arguments."] */
        /* block start [id:"Inventory_block_43" category:"My Blocks" label:"call recalc" shape:"command" docs:"Calls the recalc method using its declared arguments."] */
        recalc();
        /* block end [id:"Inventory_block_43"] */
        /* block comment [block:"Inventory_block_44" text:"Runs the touch action. Values in this block are editable inputs."] */
        /* block start [id:"Inventory_block_44" category:"Sensing" label:"touch" shape:"command" docs:"Runs the touch action. Values in this block are editable inputs."] */
        touch();
        /* block end [id:"Inventory_block_44"] */
        /* block comment [block:"Inventory_block_45" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Inventory_block_45" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().setVariable(/* param start [id:"Inventory_value_76" type:"text"] */"Msg"/* param end [id:"Inventory_value_76"] */, /* param start [id:"Inventory_value_77" type:"text"] */"Equipped: "/* param end [id:"Inventory_value_77"] */ + item.name);
        /* block end [id:"Inventory_block_45"] */
        /* block comment [block:"Inventory_block_46" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Inventory_block_46" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().playSound(/* param start [id:"Inventory_value_78" type:"text"] */"jscratch_ws/assets/gear in nven_pop.wav"/* param end [id:"Inventory_value_78"] */);
        /* block end [id:"Inventory_block_46"] */
    }

    /** Recompute equipment bonuses into PlayerStats (keeps mana delta on equip). */
    public static void recalc() {
        /* block comment [block:"Inventory_block_47" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Inventory_block_47" category:"Variables" label:"set atk" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int atk = /* param start [id:"Inventory_value_79" type:"number"] */0/* param end [id:"Inventory_value_79"] */, dmg = /* param start [id:"Inventory_value_80" type:"number"] */0/* param end [id:"Inventory_value_80"] */, ac = /* param start [id:"Inventory_value_81" type:"number"] */0/* param end [id:"Inventory_value_81"] */, spl = /* param start [id:"Inventory_value_82" type:"number"] */0/* param end [id:"Inventory_value_82"] */, mana = /* param start [id:"Inventory_value_83" type:"number"] */0/* param end [id:"Inventory_value_83"] */;
        /* block end [id:"Inventory_block_47"] */
        /* block comment [block:"Inventory_block_7" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"Inventory_block_7" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (Item it : equipped.values()) {
            /* block comment [block:"Inventory_block_48" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Inventory_block_48" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            atk += it.atk;
            /* block end [id:"Inventory_block_48"] */
            /* block comment [block:"Inventory_block_49" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Inventory_block_49" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            dmg += it.dmg;
            /* block end [id:"Inventory_block_49"] */
            /* block comment [block:"Inventory_block_50" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Inventory_block_50" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            ac += it.ac;
            /* block end [id:"Inventory_block_50"] */
            /* block comment [block:"Inventory_block_51" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Inventory_block_51" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            spl += it.spl;
            /* block end [id:"Inventory_block_51"] */
            /* block comment [block:"Inventory_block_52" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Inventory_block_52" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            mana += it.mana;
            /* block end [id:"Inventory_block_52"] */
        }
        /* block end [id:"Inventory_block_7"] */
        /* block comment [block:"Inventory_block_53" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Inventory_block_53" category:"Variables" label:"set bonusMelee" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PlayerStats.bonusMelee = atk;
        /* block end [id:"Inventory_block_53"] */
        /* block comment [block:"Inventory_block_54" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Inventory_block_54" category:"Variables" label:"set bonusDmg" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PlayerStats.bonusDmg = dmg;
        /* block end [id:"Inventory_block_54"] */
        /* block comment [block:"Inventory_block_55" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Inventory_block_55" category:"Variables" label:"set bonusAC" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PlayerStats.bonusAC = ac;
        /* block end [id:"Inventory_block_55"] */
        /* block comment [block:"Inventory_block_56" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Inventory_block_56" category:"Variables" label:"set bonusSpell" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PlayerStats.bonusSpell = spl;
        /* block end [id:"Inventory_block_56"] */

        /* block comment [block:"Inventory_block_57" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Inventory_block_57" category:"Variables" label:"set oldMax" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int oldMax = PlayerStats.maxMana;
        /* block end [id:"Inventory_block_57"] */
        /* block comment [block:"Inventory_block_58" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Inventory_block_58" category:"Variables" label:"set maxMana" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PlayerStats.maxMana = PlayerStats.computeMaxMana() + mana;
        /* block end [id:"Inventory_block_58"] */
        /* block comment [block:"Inventory_block_8" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Inventory_block_8" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (PlayerStats.maxMana > oldMax) {
            /* block comment [block:"Inventory_block_59" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Inventory_block_59" category:"Variables" label:"set mana" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            PlayerStats.mana += PlayerStats.maxMana - oldMax;
            /* block end [id:"Inventory_block_59"] */
        } else /* block comment [block:"Inventory_block_9" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Inventory_block_9" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (PlayerStats.mana > PlayerStats.maxMana) {
            /* block comment [block:"Inventory_block_60" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Inventory_block_60" category:"Variables" label:"set mana" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            PlayerStats.mana = PlayerStats.maxMana;
            /* block end [id:"Inventory_block_60"] */
        }
        /* block end [id:"Inventory_block_9"] */
        /* block end [id:"Inventory_block_8"] */
    }

    /** Class starter kits (shown in CharacterCreation preview). */
    public static void grantStarter(String className) {
        /* block comment [block:"Inventory_block_61" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Inventory_block_61" category:"Variables" label:"set kit" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        String[] kit;
        /* block end [id:"Inventory_block_61"] */
        /* block comment [block:"Inventory_block_10" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Inventory_block_10" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (className.equals(/* param start [id:"Inventory_value_84" type:"text"] */"fighter"/* param end [id:"Inventory_value_84"] */)) kit = new String[]{/* param start [id:"Inventory_value_85" type:"text"] */"fight_sword"/* param end [id:"Inventory_value_85"] */, /* param start [id:"Inventory_value_86" type:"text"] */"breastplate"/* param end [id:"Inventory_value_86"] */}
        /* block end [id:"Inventory_block_10"] */;
        else /* block comment [block:"Inventory_block_11" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Inventory_block_11" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (className.equals(/* param start [id:"Inventory_value_87" type:"text"] */"barbarian"/* param end [id:"Inventory_value_87"] */)) kit = new String[]{/* param start [id:"Inventory_value_88" type:"text"] */"bad_axe"/* param end [id:"Inventory_value_88"] */, /* param start [id:"Inventory_value_89" type:"text"] */"mankey_boots"/* param end [id:"Inventory_value_89"] */}
        /* block end [id:"Inventory_block_11"] */;
        else /* block comment [block:"Inventory_block_12" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Inventory_block_12" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (className.equals(/* param start [id:"Inventory_value_90" type:"text"] */"paladin"/* param end [id:"Inventory_value_90"] */)) kit = new String[]{/* param start [id:"Inventory_value_91" type:"text"] */"fight_sword"/* param end [id:"Inventory_value_91"] */, /* param start [id:"Inventory_value_92" type:"text"] */"good_shield"/* param end [id:"Inventory_value_92"] */}
        /* block end [id:"Inventory_block_12"] */;
        else /* block comment [block:"Inventory_block_13" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Inventory_block_13" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (className.equals(/* param start [id:"Inventory_value_93" type:"text"] */"ranger"/* param end [id:"Inventory_value_93"] */)) kit = new String[]{/* param start [id:"Inventory_value_94" type:"text"] */"h_crosssbow"/* param end [id:"Inventory_value_94"] */, /* param start [id:"Inventory_value_95" type:"text"] */"mankey_boots"/* param end [id:"Inventory_value_95"] */}
        /* block end [id:"Inventory_block_13"] */;
        else /* block comment [block:"Inventory_block_14" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Inventory_block_14" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (className.equals(/* param start [id:"Inventory_value_96" type:"text"] */"rogue"/* param end [id:"Inventory_value_96"] */)) kit = new String[]{/* param start [id:"Inventory_value_97" type:"text"] */"poidagger"/* param end [id:"Inventory_value_97"] */, /* param start [id:"Inventory_value_98" type:"text"] */"rocloak"/* param end [id:"Inventory_value_98"] */}
        /* block end [id:"Inventory_block_14"] */;
        else /* block comment [block:"Inventory_block_15" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Inventory_block_15" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (className.equals(/* param start [id:"Inventory_value_99" type:"text"] */"monk"/* param end [id:"Inventory_value_99"] */)) kit = new String[]{/* param start [id:"Inventory_value_100" type:"text"] */"mankey_glove"/* param end [id:"Inventory_value_100"] */, /* param start [id:"Inventory_value_101" type:"text"] */"mankey_boots"/* param end [id:"Inventory_value_101"] */}
        /* block end [id:"Inventory_block_15"] */;
        else /* block comment [block:"Inventory_block_16" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Inventory_block_16" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (className.equals(/* param start [id:"Inventory_value_102" type:"text"] */"bard"/* param end [id:"Inventory_value_102"] */)) kit = new String[]{/* param start [id:"Inventory_value_103" type:"text"] */"mankey_glove"/* param end [id:"Inventory_value_103"] */, /* param start [id:"Inventory_value_104" type:"text"] */"mankey_shirt"/* param end [id:"Inventory_value_104"] */}
        /* block end [id:"Inventory_block_16"] */;
        else /* block comment [block:"Inventory_block_17" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Inventory_block_17" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (className.equals(/* param start [id:"Inventory_value_105" type:"text"] */"cleric"/* param end [id:"Inventory_value_105"] */)) kit = new String[]{/* param start [id:"Inventory_value_106" type:"text"] */"old_shield"/* param end [id:"Inventory_value_106"] */, /* param start [id:"Inventory_value_107" type:"text"] */"breastplate"/* param end [id:"Inventory_value_107"] */}
        /* block end [id:"Inventory_block_17"] */;
        else /* block comment [block:"Inventory_block_18" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Inventory_block_18" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (className.equals(/* param start [id:"Inventory_value_108" type:"text"] */"druid"/* param end [id:"Inventory_value_108"] */)) kit = new String[]{/* param start [id:"Inventory_value_109" type:"text"] */"scythe"/* param end [id:"Inventory_value_109"] */, /* param start [id:"Inventory_value_110" type:"text"] */"mankey_shirt"/* param end [id:"Inventory_value_110"] */}
        /* block end [id:"Inventory_block_18"] */;
        else /* block comment [block:"Inventory_block_19" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Inventory_block_19" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (className.equals(/* param start [id:"Inventory_value_111" type:"text"] */"artificer"/* param end [id:"Inventory_value_111"] */)) kit = new String[]{/* param start [id:"Inventory_value_112" type:"text"] */"bombdoiler"/* param end [id:"Inventory_value_112"] */, /* param start [id:"Inventory_value_113" type:"text"] */"bomb_gogs"/* param end [id:"Inventory_value_113"] */}
        /* block end [id:"Inventory_block_19"] */;
        else /* block comment [block:"Inventory_block_20" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Inventory_block_20" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (className.equals(/* param start [id:"Inventory_value_114" type:"text"] */"warlock"/* param end [id:"Inventory_value_114"] */)) kit = new String[]{/* param start [id:"Inventory_value_115" type:"text"] */"necro_relic"/* param end [id:"Inventory_value_115"] */, /* param start [id:"Inventory_value_116" type:"text"] */"rocloak"/* param end [id:"Inventory_value_116"] */}
        /* block end [id:"Inventory_block_20"] */;
        else /* block comment [block:"Inventory_block_21" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Inventory_block_21" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (className.equals(/* param start [id:"Inventory_value_117" type:"text"] */"sorcerer"/* param end [id:"Inventory_value_117"] */)) kit = new String[]{/* param start [id:"Inventory_value_118" type:"text"] */"whiz_wand"/* param end [id:"Inventory_value_118"] */, /* param start [id:"Inventory_value_119" type:"text"] */"rocloak"/* param end [id:"Inventory_value_119"] */}
        /* block end [id:"Inventory_block_21"] */;
        else kit = new String[]{"whiz_wand", "wizard_robe"}; // wizard
        /* block comment [block:"Inventory_block_62" text:"Runs the for action. Values in this block are editable inputs."] */
        /* block start [id:"Inventory_block_62" category:"My Blocks" label:"for" shape:"command" docs:"Runs the for action. Values in this block are editable inputs."] */
        /* block comment [block:"Inventory_block_22" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"Inventory_block_22" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (String id : kit) add(Item.byId(id));
        /* block end [id:"Inventory_block_62"] */
        /* block end [id:"Inventory_block_22"] */
        // Start wearing the kit
        /* block comment [block:"Inventory_block_23" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"Inventory_block_23" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (int i = bag.size() - /* param start [id:"Inventory_value_120" type:"number"] */1/* param end [id:"Inventory_value_120"] */; i >= /* param start [id:"Inventory_value_121" type:"number"] */0/* param end [id:"Inventory_value_121"] */; i--) equip(i);
        /* block end [id:"Inventory_block_23"] */
    }
}
