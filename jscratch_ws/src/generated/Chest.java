package generated;

import com.jscratch.Stage;

/**
* Mimic-style loot chest: one per floor, reset on floor change.
* Closed: mimic_costume1 (123x72). Open: mimic_costume2 (124x104).
* Grants 1 random item + floor-scaled gold on first touch.
*
* <p>Call graph: owned by {@code FloorManager} (placed +
* {@code resetForFloor} per floor); polled per player frame via
* {@code FloorManager.update → check} → pays {@code Inventory.addGold/add}
* (overflow spills via {@code LootDrop.spawnForced}) + SFX + HUD msg.
*/
public class Chest extends BSprite {
    private boolean opened = false;
    private int floor = 1;

    public Chest() {
        /* block comment [block:"Chest_block_5" text:"Runs the super action. Values in this block are editable inputs."] */
        /* block start [id:"Chest_block_5" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
        super(/* param start [id:"Chest_value_31" type:"text"] */"Chest"/* param end [id:"Chest_value_31"] */);
        /* block end [id:"Chest_block_5"] */
        /* block comment [block:"Chest_block_6" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"Chest_block_6" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(ResourcePool.getCostume(/* param start [id:"Chest_value_32" type:"text"] */"chest_closed"/* param end [id:"Chest_value_32"] */, /* param start [id:"Chest_value_33" type:"text"] */"jscratch_ws/assets/mimic_costume1.png"/* param end [id:"Chest_value_33"] */, /* param start [id:"Chest_value_34" type:"number"] */61/* param end [id:"Chest_value_34"] */, /* param start [id:"Chest_value_35" type:"number"] */36/* param end [id:"Chest_value_35"] */));
        /* block end [id:"Chest_block_6"] */
        /* block comment [block:"Chest_block_7" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"Chest_block_7" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(ResourcePool.getCostume(/* param start [id:"Chest_value_36" type:"text"] */"chest_open"/* param end [id:"Chest_value_36"] */, /* param start [id:"Chest_value_37" type:"text"] */"jscratch_ws/assets/mimic_costume2.png"/* param end [id:"Chest_value_37"] */, /* param start [id:"Chest_value_38" type:"number"] */62/* param end [id:"Chest_value_38"] */, /* param start [id:"Chest_value_39" type:"number"] */52/* param end [id:"Chest_value_39"] */));
        /* block end [id:"Chest_block_7"] */
        /* block comment [block:"Chest_block_8" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Chest_block_8" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Chest_block_1" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Chest_block_1" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (minimapSprite != /* param start [id:"Chest_value_40" type:"value"] */null/* param end [id:"Chest_value_40"] */) minimapSprite.hide();
        /* block end [id:"Chest_block_8"] */
        /* block end [id:"Chest_block_1"] */
        /* block comment [block:"Chest_block_9" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Chest_block_9" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.localScale = /* param start [id:"Chest_value_41" type:"number"] */0.8/* param end [id:"Chest_value_41"] */;
        /* block end [id:"Chest_block_9"] */
        /* block comment [block:"Chest_block_10" text:"Runs the switch costume action. Values in this block are editable inputs."] */
        /* block start [id:"Chest_block_10" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
        switchCostume(/* param start [id:"Chest_value_42" type:"text"] */"chest_closed"/* param end [id:"Chest_value_42"] */);
        /* block end [id:"Chest_block_10"] */
        /* block comment [block:"Chest_block_11" text:"Calls the update method using its declared arguments."] */
        /* block start [id:"Chest_block_11" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
        update();
        /* block end [id:"Chest_block_11"] */
    }

    /** Reposition + relock for a new floor. */
    public void resetForFloor(int floor, int x, int y) {
        /* block comment [block:"Chest_block_12" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Chest_block_12" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.floor = floor;
        /* block end [id:"Chest_block_12"] */
        /* block comment [block:"Chest_block_13" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Chest_block_13" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.opened = /* param start [id:"Chest_value_43" type:"boolean"] */false/* param end [id:"Chest_value_43"] */;
        /* block end [id:"Chest_block_13"] */
        /* block comment [block:"Chest_block_14" text:"Runs the go to action. Values in this block are editable inputs."] */
        /* block start [id:"Chest_block_14" category:"Motion" label:"go to" shape:"command" docs:"Runs the go to action. Values in this block are editable inputs."] */
        goTo(x, y);
        /* block end [id:"Chest_block_14"] */
        /* block comment [block:"Chest_block_15" text:"Runs the switch costume action. Values in this block are editable inputs."] */
        /* block start [id:"Chest_block_15" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
        switchCostume(/* param start [id:"Chest_value_44" type:"text"] */"chest_closed"/* param end [id:"Chest_value_44"] */);
        /* block end [id:"Chest_block_15"] */
        /* block comment [block:"Chest_block_16" text:"Runs the show action. Values in this block are editable inputs."] */
        /* block start [id:"Chest_block_16" category:"Looks" label:"show" shape:"command" docs:"Runs the show action. Values in this block are editable inputs."] */
        show();
        /* block end [id:"Chest_block_16"] */
        /* block comment [block:"Chest_block_17" text:"Calls the update method using its declared arguments."] */
        /* block start [id:"Chest_block_17" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
        update();
        /* block end [id:"Chest_block_17"] */
    }

    /** Called from FloorManager.update with the player position. */
    public void check(int px, int py) {
        /* block comment [block:"Chest_block_18" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Chest_block_18" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Chest_block_2" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Chest_block_2" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (opened || !isVisible()) return;
        /* block end [id:"Chest_block_18"] */
        /* block end [id:"Chest_block_2"] */
        /* block comment [block:"Chest_block_19" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Chest_block_19" category:"Variables" label:"set dx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double dx = xx - px;
        /* block end [id:"Chest_block_19"] */
        /* block comment [block:"Chest_block_20" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Chest_block_20" category:"Variables" label:"set dy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double dy = yy - py;
        /* block end [id:"Chest_block_20"] */
        /* block comment [block:"Chest_block_21" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Chest_block_21" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Chest_block_3" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Chest_block_3" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (dx * dx + dy * dy > /* param start [id:"Chest_value_45" type:"number"] */60/* param end [id:"Chest_value_45"] */ * /* param start [id:"Chest_value_46" type:"number"] */60/* param end [id:"Chest_value_46"] */) return;
        /* block end [id:"Chest_block_21"] */
        /* block end [id:"Chest_block_3"] */

        /* block comment [block:"Chest_block_22" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Chest_block_22" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        opened = /* param start [id:"Chest_value_47" type:"boolean"] */true/* param end [id:"Chest_value_47"] */;
        /* block end [id:"Chest_block_22"] */
        /* block comment [block:"Chest_block_23" text:"Runs the switch costume action. Values in this block are editable inputs."] */
        /* block start [id:"Chest_block_23" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
        switchCostume(/* param start [id:"Chest_value_48" type:"text"] */"chest_open"/* param end [id:"Chest_value_48"] */);
        /* block end [id:"Chest_block_23"] */
        /* block comment [block:"Chest_block_24" text:"Calls the update method using its declared arguments."] */
        /* block start [id:"Chest_block_24" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
        update();
        /* block end [id:"Chest_block_24"] */
        /* block comment [block:"Chest_block_25" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Chest_block_25" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().playSound(/* param start [id:"Chest_value_49" type:"text"] */"jscratch_ws/assets/gear in nven_pop.wav"/* param end [id:"Chest_value_49"] */);
        /* block end [id:"Chest_block_25"] */
        /* block comment [block:"Chest_block_26" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Chest_block_26" category:"Variables" label:"set gold" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int gold = /* param start [id:"Chest_value_50" type:"number"] */15/* param end [id:"Chest_value_50"] */ * floor + Combat.rollDie(/* param start [id:"Chest_value_51" type:"number"] */10/* param end [id:"Chest_value_51"] */);
        /* block end [id:"Chest_block_26"] */
        /* block comment [block:"Chest_block_27" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Chest_block_27" category:"Variables" label:"set addGold" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Inventory.addGold(gold);
        /* block end [id:"Chest_block_27"] */
        /* block comment [block:"Chest_block_28" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Chest_block_28" category:"Variables" label:"set loot" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Item loot = Item.randomItem();
        /* block end [id:"Chest_block_28"] */
        /* block comment [block:"Chest_block_4" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Chest_block_4" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!Inventory.add(loot)) {
            /* block comment [block:"Chest_block_29" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Chest_block_29" category:"Variables" label:"set spawnForced" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            LootDrop.spawnForced(xx + /* param start [id:"Chest_value_52" type:"number"] */50/* param end [id:"Chest_value_52"] */, yy, loot);
            /* block end [id:"Chest_block_29"] */ // bag full: drop at our feet
        }
        /* block end [id:"Chest_block_4"] */
        /* block comment [block:"Chest_block_30" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Chest_block_30" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().setVariable(/* param start [id:"Chest_value_53" type:"text"] */"Msg"/* param end [id:"Chest_value_53"] */, /* param start [id:"Chest_value_54" type:"text"] */"Chest: +"/* param end [id:"Chest_value_54"] */ + gold + /* param start [id:"Chest_value_55" type:"text"] */"g, "/* param end [id:"Chest_value_55"] */ + loot.name);
        /* block end [id:"Chest_block_30"] */
    }
}
