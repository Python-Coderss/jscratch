package generated;

/**
* Item definition + static catalog. One entry per gear asset on disk.
* Slots: exactly one equipped item per slot.
* Stat shorthands: atk = melee attack bonus, dmg = bonus damage,
* ac = armour class, spl = spell attack bonus, mana = max mana.
* Icon centers were measured from the actual PNG sizes. Java 8 compatible.
*
* <p>Call graph: pure data — {@code CATALOG/byId/randomItem} consumed by
* {@code Inventory} (starter kits, drops), {@code Chest}, and
* {@code EnemySprite.die} drops; rendered on the ground by
* {@code LootDrop} (pooled costume per item id).
*/
public class Item {
    public final String id;
    public final String name;
    public final String slot;
    public final String file;
    public final int cx, cy;
    public final int atk, dmg, ac, spl, mana;

    public Item(String id, String name, String slot, String file,
    int cx, int cy, int atk, int dmg, int ac, int spl, int mana) {
        /* block comment [block:"Item_block_8" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Item_block_8" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.id = id;
        /* block end [id:"Item_block_8"] */
        /* block comment [block:"Item_block_9" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Item_block_9" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.name = name;
        /* block end [id:"Item_block_9"] */
        /* block comment [block:"Item_block_10" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Item_block_10" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.slot = slot;
        /* block end [id:"Item_block_10"] */
        /* block comment [block:"Item_block_11" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Item_block_11" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.file = file;
        /* block end [id:"Item_block_11"] */
        /* block comment [block:"Item_block_12" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Item_block_12" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.cx = cx;
        /* block end [id:"Item_block_12"] */
        /* block comment [block:"Item_block_13" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Item_block_13" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.cy = cy;
        /* block end [id:"Item_block_13"] */
        /* block comment [block:"Item_block_14" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Item_block_14" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.atk = atk;
        /* block end [id:"Item_block_14"] */
        /* block comment [block:"Item_block_15" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Item_block_15" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.dmg = dmg;
        /* block end [id:"Item_block_15"] */
        /* block comment [block:"Item_block_16" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Item_block_16" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.ac = ac;
        /* block end [id:"Item_block_16"] */
        /* block comment [block:"Item_block_17" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Item_block_17" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.spl = spl;
        /* block end [id:"Item_block_17"] */
        /* block comment [block:"Item_block_18" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Item_block_18" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.mana = mana;
        /* block end [id:"Item_block_18"] */
    }

    public String statLine() {
        /* block comment [block:"Item_block_19" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Item_block_19" category:"Variables" label:"set sb" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        StringBuilder sb = new StringBuilder();
        /* block end [id:"Item_block_19"] */
        /* block comment [block:"Item_block_20" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Item_block_20" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Item_block_1" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Item_block_1" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (atk != /* param start [id:"Item_value_31" type:"number"] */0/* param end [id:"Item_value_31"] */) sb.append(/* param start [id:"Item_value_32" type:"text"] */"atk"/* param end [id:"Item_value_32"] */).append(signed(atk)).append(/* param start [id:"Item_value_33" type:"character"] */' '/* param end [id:"Item_value_33"] */);
        /* block end [id:"Item_block_20"] */
        /* block end [id:"Item_block_1"] */
        /* block comment [block:"Item_block_21" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Item_block_21" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Item_block_2" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Item_block_2" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (dmg != /* param start [id:"Item_value_34" type:"number"] */0/* param end [id:"Item_value_34"] */) sb.append(/* param start [id:"Item_value_35" type:"text"] */"dmg"/* param end [id:"Item_value_35"] */).append(signed(dmg)).append(/* param start [id:"Item_value_36" type:"character"] */' '/* param end [id:"Item_value_36"] */);
        /* block end [id:"Item_block_21"] */
        /* block end [id:"Item_block_2"] */
        /* block comment [block:"Item_block_22" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Item_block_22" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Item_block_3" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Item_block_3" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (ac != /* param start [id:"Item_value_37" type:"number"] */0/* param end [id:"Item_value_37"] */) sb.append(/* param start [id:"Item_value_38" type:"text"] */"AC"/* param end [id:"Item_value_38"] */).append(signed(ac)).append(/* param start [id:"Item_value_39" type:"character"] */' '/* param end [id:"Item_value_39"] */);
        /* block end [id:"Item_block_22"] */
        /* block end [id:"Item_block_3"] */
        /* block comment [block:"Item_block_23" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Item_block_23" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Item_block_4" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Item_block_4" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (spl != /* param start [id:"Item_value_40" type:"number"] */0/* param end [id:"Item_value_40"] */) sb.append(/* param start [id:"Item_value_41" type:"text"] */"spl"/* param end [id:"Item_value_41"] */).append(signed(spl)).append(/* param start [id:"Item_value_42" type:"character"] */' '/* param end [id:"Item_value_42"] */);
        /* block end [id:"Item_block_23"] */
        /* block end [id:"Item_block_4"] */
        /* block comment [block:"Item_block_24" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Item_block_24" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Item_block_5" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Item_block_5" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (mana != /* param start [id:"Item_value_43" type:"number"] */0/* param end [id:"Item_value_43"] */) sb.append(/* param start [id:"Item_value_44" type:"text"] */"mana"/* param end [id:"Item_value_44"] */).append(signed(mana)).append(/* param start [id:"Item_value_45" type:"character"] */' '/* param end [id:"Item_value_45"] */);
        /* block end [id:"Item_block_24"] */
        /* block end [id:"Item_block_5"] */
        /* block comment [block:"Item_block_25" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Item_block_25" category:"Variables" label:"set s" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        String s = sb.toString().trim();
        /* block end [id:"Item_block_25"] */
        /* block comment [block:"Item_block_26" text:"Returns a value or ends the current method."] */
        /* block start [id:"Item_block_26" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return s.length() == /* param start [id:"Item_value_46" type:"number"] */0/* param end [id:"Item_value_46"] */ ? /* param start [id:"Item_value_47" type:"text"] */"mundane"/* param end [id:"Item_value_47"] */ : s;
        /* block end [id:"Item_block_26"] */
    }

    private static String signed(int v) {
        /* block comment [block:"Item_block_27" text:"Returns a value or ends the current method."] */
        /* block start [id:"Item_block_27" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return v >= /* param start [id:"Item_value_48" type:"number"] */0/* param end [id:"Item_value_48"] */ ? /* param start [id:"Item_value_49" type:"text"] */"+"/* param end [id:"Item_value_49"] */ + v : String.valueOf(v);
        /* block end [id:"Item_block_27"] */
    }

    public static final Item[] CATALOG = {
        new Item("fight_sword", "Fighter Sword", "WEAPON", "gear in nven_fight_sword.png", 86, 278, 2, 2, 0, 0, 0),
        new Item("bad_axe", "Bad Axe", "WEAPON", "gear in nven__bad_axe.png", 180, 197, -1, 3, 0, 0, 0),
        new Item("poidagger", "Poison Dagger", "WEAPON", "gear in nven_poidagger.png", 111, 142, 1, 1, 0, 0, 0),
        new Item("h_crosssbow", "Hunter Crossbow", "WEAPON", "gear in nven_h_crosssbow.png", 138, 189, 2, 0, 0, 0, 0),
        new Item("scythe", "Reaper Scythe", "WEAPON", "gear in nven_scythe.png", 321, 301, 0, 3, 0, 0, 0),
        new Item("whiz_wand", "Whiz Wand", "WEAPON", "gear in nven_whiz_wand.png", 181, 130, 0, 0, 0, 2, 0),
        new Item("breastplate", "Fighter Breastplate", "ARMOR", "gear in nven_breastplate_for_fighter.png", 155, 107, 0, 0, 3, 0, 0),
        new Item("warden_plate", "Warden Plate", "ARMOR", "gear in nven_warden_plate.png", 264, 188, 0, 0, 4, 0, 0),
        new Item("wizard_robe", "Wizard Robe", "ARMOR", "gear in nven_wizard_robe.png", 171, 193, 0, 0, 1, 0, 5),
        new Item("necrobe", "Necrobe", "ARMOR", "gear in nven_necrobe.png", 267, 232, 0, 0, 2, 0, 3),
        new Item("mankey_shirt", "Mankey Shirt", "ARMOR", "gear in nven_mankey_shirt.png", 90, 101, 0, 0, 1, 0, 0),
        new Item("knight_helmet", "Knight Helmet", "HELM", "gear in nven_knight_helmet.png", 84, 129, 0, 0, 1, 0, 0),
        new Item("whiz_hat", "Whiz Hat", "HELM", "gear in nven_whiz_hat.png", 116, 126, 0, 0, 0, 1, 0),
        new Item("bomb_gogs", "Bomb Goggles", "HELM", "gear in nven_bomb_gogs.png", 167, 99, 1, 0, 0, 0, 0),
        new Item("mankey_glove", "Mankey Glove", "GLOVES", "gear in nven_mankey_glove.png", 211, 98, 1, 0, 0, 0, 0),
        new Item("bomb_gloves", "Bomb Gloves", "GLOVES", "gear in nven_bomb_gloves.png", 269, 131, 0, 1, 0, 0, 0),
        new Item("mankey_boots", "Mankey Boots", "BOOTS", "gear in nven_mankey_boots.png", 169, 61, 0, 0, 1, 0, 0),
        new Item("rocloak", "Rogue Cloak", "CLOAK", "gear in nven_rocloak.png", 124, 281, 1, 0, 1, 0, 0),
        new Item("good_shield", "Good Shield", "SHIELD", "gear in nven_good_shield.png", 247, 347, 0, 0, 2, 0, 0),
        new Item("old_shield", "Old Shield", "SHIELD", "gear in nven_shield.png", 149, 226, 0, 0, 1, 0, 0),
        new Item("bombdoiler", "Bombdoiler", "CHARM", "gear in nven_bombdoiler.png", 145, 170, 0, 1, 1, 0, 0),
        new Item("necro_relic", "Necropower Relic", "CHARM", "gear in nven_necropower_thing.png", 158, 189, 0, 0, 0, 1, 5),
    };

    public static Item byId(String id) {
        /* block comment [block:"Item_block_6" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"Item_block_6" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (Item it : CATALOG) {
            /* block comment [block:"Item_block_28" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"Item_block_28" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"Item_block_7" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Item_block_7" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (it.id.equals(id)) return it;
            /* block end [id:"Item_block_28"] */
            /* block end [id:"Item_block_7"] */
        }
        /* block end [id:"Item_block_6"] */
        /* block comment [block:"Item_block_29" text:"Returns a value or ends the current method."] */
        /* block start [id:"Item_block_29" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return /* param start [id:"Item_value_50" type:"value"] */null/* param end [id:"Item_value_50"] */;
        /* block end [id:"Item_block_29"] */
    }

    public static Item randomItem() {
        /* block comment [block:"Item_block_30" text:"Returns a value or ends the current method."] */
        /* block start [id:"Item_block_30" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return CATALOG[(int) (Math.random() * CATALOG.length)];
        /* block end [id:"Item_block_30"] */
    }
}
