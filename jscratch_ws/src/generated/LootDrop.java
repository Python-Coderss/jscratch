package generated;

import com.jscratch.Stage;

import java.util.ArrayList;
import java.util.List;

/**
* World item pickup. Enemies drop these on death (~30%); walking over one
* picks it up. Drops expire after 90s to avoid sprite clutter.
*
* <p>Call graph / interactions:
* <ul>
*   <li>Spawned by {@code EnemySprite.die} ({@code spawnAt}, 30% roll) and
*       {@code Chest} ({@code spawnForced} on bag-full).</li>
*   <li>Polled per player frame from {@code Sprite1} via
*       {@link #checkAll(int, int)}: pickup → {@code Inventory.add} →
*       HUD msg + SFX; expiry/pickup end in {@code destroy()}.</li>
*   <li>List is copy-on-write (drops spawn on kill threads, polled on the
*       player thread).</li>
* </ul>
*/
public class LootDrop extends BSprite {
    // COW: picked up on the player thread, dropped from kill paths that can
    // run on imp/projectile threads.
    private static final List<LootDrop> live = new java.util.concurrent.CopyOnWriteArrayList<>();

    public final Item item;
    private final long born = System.currentTimeMillis();

    public LootDrop(Item item, int wx, int wy) {
        /* block comment [block:"LootDrop_block_10" text:"Runs the super action. Values in this block are editable inputs."] */
        /* block start [id:"LootDrop_block_10" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
        super(/* param start [id:"LootDrop_value_38" type:"text"] */"Loot_"/* param end [id:"LootDrop_value_38"] */ + item.id);
        /* block end [id:"LootDrop_block_10"] */
        /* block comment [block:"LootDrop_block_11" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"LootDrop_block_11" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.item = item;
        /* block end [id:"LootDrop_block_11"] */
        /* block comment [block:"LootDrop_block_12" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"LootDrop_block_12" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(ResourcePool.getCostume(/* param start [id:"LootDrop_value_39" type:"text"] */"item_"/* param end [id:"LootDrop_value_39"] */ + item.id,
        /* param start [id:"LootDrop_value_40" type:"text"] */"jscratch_ws/assets/"/* param end [id:"LootDrop_value_40"] */ + item.file, item.cx, item.cy));
        /* block end [id:"LootDrop_block_12"] */
        /* block comment [block:"LootDrop_block_13" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"LootDrop_block_13" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"LootDrop_block_1" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"LootDrop_block_1" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (minimapSprite != /* param start [id:"LootDrop_value_41" type:"value"] */null/* param end [id:"LootDrop_value_41"] */) minimapSprite.hide();
        /* block end [id:"LootDrop_block_13"] */
        /* block end [id:"LootDrop_block_1"] */
        /* block comment [block:"LootDrop_block_14" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"LootDrop_block_14" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.localScale = /* param start [id:"LootDrop_value_42" type:"number"] */0.22/* param end [id:"LootDrop_value_42"] */;
        /* block end [id:"LootDrop_block_14"] */
        /* block comment [block:"LootDrop_block_15" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"LootDrop_block_15" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        xx = wx;
        /* block end [id:"LootDrop_block_15"] */
        /* block comment [block:"LootDrop_block_16" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"LootDrop_block_16" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        yy = wy;
        /* block end [id:"LootDrop_block_16"] */
        /* block comment [block:"LootDrop_block_17" text:"Calls the update method using its declared arguments."] */
        /* block start [id:"LootDrop_block_17" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
        update();
        /* block end [id:"LootDrop_block_17"] */
        /* block comment [block:"LootDrop_block_18" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"LootDrop_block_18" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().goToFront(this);
        /* block end [id:"LootDrop_block_18"] */
        /* block comment [block:"LootDrop_block_19" text:"Runs the switch costume action. Values in this block are editable inputs."] */
        /* block start [id:"LootDrop_block_19" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
        switchCostume(/* param start [id:"LootDrop_value_43" type:"text"] */"item_"/* param end [id:"LootDrop_value_43"] */ + item.id);
        /* block end [id:"LootDrop_block_19"] */
    }

    /** Rolled drop (~30% chance of a random catalog item). */
    public static void spawnAt(int wx, int wy) {
        /* block comment [block:"LootDrop_block_20" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"LootDrop_block_20" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"LootDrop_block_2" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"LootDrop_block_2" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (Math.random() < /* param start [id:"LootDrop_value_44" type:"number"] */0.30/* param end [id:"LootDrop_value_44"] */) spawnForced(wx, wy, Item.randomItem());
        /* block end [id:"LootDrop_block_20"] */
        /* block end [id:"LootDrop_block_2"] */
    }

    /** Guaranteed drop (chests, quests). */
    public static void spawnForced(int wx, int wy, Item item) {
        /* block comment [block:"LootDrop_block_21" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"LootDrop_block_21" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"LootDrop_block_3" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"LootDrop_block_3" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (item == /* param start [id:"LootDrop_value_45" type:"value"] */null/* param end [id:"LootDrop_value_45"] */) return;
        /* block end [id:"LootDrop_block_21"] */
        /* block end [id:"LootDrop_block_3"] */
        /* block comment [block:"LootDrop_block_22" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"LootDrop_block_22" category:"Variables" label:"set drop" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        LootDrop drop = new LootDrop(item, wx, wy);
        /* block end [id:"LootDrop_block_22"] */
        /* block comment [block:"LootDrop_block_23" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"LootDrop_block_23" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().addSprite(drop);
        /* block end [id:"LootDrop_block_23"] */
        /* block comment [block:"LootDrop_block_24" text:"Calls the add method using its declared arguments."] */
        /* block start [id:"LootDrop_block_24" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
        live.add(drop);
        /* block end [id:"LootDrop_block_24"] */
    }

    /** Called from the player loop: pickup + expiry. */
    public static void checkAll(int px, int py) {
        /* block comment [block:"LootDrop_block_4" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"LootDrop_block_4" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (int i = live.size() - /* param start [id:"LootDrop_value_46" type:"number"] */1/* param end [id:"LootDrop_value_46"] */; i >= /* param start [id:"LootDrop_value_47" type:"number"] */0/* param end [id:"LootDrop_value_47"] */; i--) {
            /* block comment [block:"LootDrop_block_25" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"LootDrop_block_25" category:"Variables" label:"set d" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            LootDrop d = live.get(i);
            /* block end [id:"LootDrop_block_25"] */
            /* block comment [block:"LootDrop_block_5" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"LootDrop_block_5" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (!d.isVisible()) {
                /* block comment [block:"LootDrop_block_26" text:"Runs the remove action. Values in this block are editable inputs."] */
                /* block start [id:"LootDrop_block_26" category:"Motion" label:"remove" shape:"command" docs:"Runs the remove action. Values in this block are editable inputs."] */
                live.remove(i);
                /* block end [id:"LootDrop_block_26"] */
                /* block comment [block:"LootDrop_block_27" text:"Skips to the next iteration of the nearest loop."] */
                /* block start [id:"LootDrop_block_27" category:"Control" label:"next loop turn" shape:"command" docs:"Skips to the next iteration of the nearest loop."] */
                continue;
                /* block end [id:"LootDrop_block_27"] */
            }
            /* block end [id:"LootDrop_block_5"] */
            /* block comment [block:"LootDrop_block_28" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"LootDrop_block_28" category:"Variables" label:"set dx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double dx = d.xx - px;
            /* block end [id:"LootDrop_block_28"] */
            /* block comment [block:"LootDrop_block_29" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"LootDrop_block_29" category:"Variables" label:"set dy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double dy = d.yy - py;
            /* block end [id:"LootDrop_block_29"] */
            /* block comment [block:"LootDrop_block_6" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"LootDrop_block_6" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (dx * dx + dy * dy < /* param start [id:"LootDrop_value_48" type:"number"] */40/* param end [id:"LootDrop_value_48"] */ * /* param start [id:"LootDrop_value_49" type:"number"] */40/* param end [id:"LootDrop_value_49"] */) {
                /* block comment [block:"LootDrop_block_7" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"LootDrop_block_7" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (Inventory.add(d.item)) {
                    /* block comment [block:"LootDrop_block_30" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    /* block start [id:"LootDrop_block_30" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    Stage.getInstance().setVariable(/* param start [id:"LootDrop_value_50" type:"text"] */"Msg"/* param end [id:"LootDrop_value_50"] */, /* param start [id:"LootDrop_value_51" type:"text"] */"Picked up: "/* param end [id:"LootDrop_value_51"] */ + d.item.name);
                    /* block end [id:"LootDrop_block_30"] */
                    /* block comment [block:"LootDrop_block_31" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    /* block start [id:"LootDrop_block_31" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    Stage.getInstance().playSound(/* param start [id:"LootDrop_value_52" type:"text"] */"jscratch_ws/assets/gear in nven_pop.wav"/* param end [id:"LootDrop_value_52"] */);
                    /* block end [id:"LootDrop_block_31"] */
                    /* block comment [block:"LootDrop_block_32" text:"Calls the destroy method using its declared arguments."] */
                    /* block start [id:"LootDrop_block_32" category:"My Blocks" label:"call destroy" shape:"command" docs:"Calls the destroy method using its declared arguments."] */
                    d.destroy();
                    /* block end [id:"LootDrop_block_32"] */
                    /* block comment [block:"LootDrop_block_33" text:"Runs the remove action. Values in this block are editable inputs."] */
                    /* block start [id:"LootDrop_block_33" category:"Motion" label:"remove" shape:"command" docs:"Runs the remove action. Values in this block are editable inputs."] */
                    live.remove(i);
                    /* block end [id:"LootDrop_block_33"] */
                }
                /* block end [id:"LootDrop_block_7"] */
                // Bag full: leave the drop on the ground (message already set)
            } else /* block comment [block:"LootDrop_block_8" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"LootDrop_block_8" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (System.currentTimeMillis() - d.born > /* param start [id:"LootDrop_value_53" type:"number"] */90000/* param end [id:"LootDrop_value_53"] */) {
                /* block comment [block:"LootDrop_block_34" text:"Calls the destroy method using its declared arguments."] */
                /* block start [id:"LootDrop_block_34" category:"My Blocks" label:"call destroy" shape:"command" docs:"Calls the destroy method using its declared arguments."] */
                d.destroy();
                /* block end [id:"LootDrop_block_34"] */
                /* block comment [block:"LootDrop_block_35" text:"Runs the remove action. Values in this block are editable inputs."] */
                /* block start [id:"LootDrop_block_35" category:"Motion" label:"remove" shape:"command" docs:"Runs the remove action. Values in this block are editable inputs."] */
                live.remove(i);
                /* block end [id:"LootDrop_block_35"] */
            }
            /* block end [id:"LootDrop_block_8"] */
            /* block end [id:"LootDrop_block_6"] */
        }
        /* block end [id:"LootDrop_block_4"] */
    }

    /** Remove all live drops (floor change cleanup lives in EnemyManager.clearAll; drops persist). */
    public static void clearAll() {
        /* block comment [block:"LootDrop_block_36" text:"Runs the for action. Values in this block are editable inputs."] */
        /* block start [id:"LootDrop_block_36" category:"My Blocks" label:"for" shape:"command" docs:"Runs the for action. Values in this block are editable inputs."] */
        /* block comment [block:"LootDrop_block_9" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"LootDrop_block_9" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (LootDrop d : live) d.destroy();
        /* block end [id:"LootDrop_block_36"] */
        /* block end [id:"LootDrop_block_9"] */
        /* block comment [block:"LootDrop_block_37" text:"Calls the clear method using its declared arguments."] */
        /* block start [id:"LootDrop_block_37" category:"My Blocks" label:"call clear" shape:"command" docs:"Calls the clear method using its declared arguments."] */
        live.clear();
        /* block end [id:"LootDrop_block_37"] */
    }
}
