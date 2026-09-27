package generated;

import com.jscratch.*;

/**
* Nail Slash: heavy front-arc melee maneuver (key 1).
* Spawns at the caster, flashes the 3-frame slash VFX, and resolves
* d20 + melee bonus vs AC for 2d6 + STR in a 90-degree arc.
* Dimensions verified: 145x57 / 146x137 / 140x57.
*
* <p>Call graph: spawned by {@code Spellbook.castSlash} from the Sprite1
* loop; damage resolves synchronously against {@code EnemyManager} (arc +
* range check), then a short daemon script thread plays the 3 frames and
* {@code destroy()}s (unregisters from Scroller2d).
*/
public class SlashOfNails extends BSprite {
    public SlashOfNails(int wx, int wy, double dirDeg) {
        /* block comment [block:"SlashOfNails_block_10" text:"Runs the super action. Values in this block are editable inputs."] */
        /* block start [id:"SlashOfNails_block_10" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
        super(/* param start [id:"SlashOfNails_value_36" type:"text"] */"SlashOfNails"/* param end [id:"SlashOfNails_value_36"] */);
        /* block end [id:"SlashOfNails_block_10"] */

        /* block comment [block:"SlashOfNails_block_11" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"SlashOfNails_block_11" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(ResourcePool.getCostume(/* param start [id:"SlashOfNails_value_37" type:"text"] */"slash1"/* param end [id:"SlashOfNails_value_37"] */, /* param start [id:"SlashOfNails_value_38" type:"text"] */"jscratch_ws/assets/Nail slash_slash1.png"/* param end [id:"SlashOfNails_value_38"] */, /* param start [id:"SlashOfNails_value_39" type:"number"] */72/* param end [id:"SlashOfNails_value_39"] */, /* param start [id:"SlashOfNails_value_40" type:"number"] */28/* param end [id:"SlashOfNails_value_40"] */));
        /* block end [id:"SlashOfNails_block_11"] */
        /* block comment [block:"SlashOfNails_block_12" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"SlashOfNails_block_12" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(ResourcePool.getCostume(/* param start [id:"SlashOfNails_value_41" type:"text"] */"slash2"/* param end [id:"SlashOfNails_value_41"] */, /* param start [id:"SlashOfNails_value_42" type:"text"] */"jscratch_ws/assets/Nail slash_slash2.png"/* param end [id:"SlashOfNails_value_42"] */, /* param start [id:"SlashOfNails_value_43" type:"number"] */73/* param end [id:"SlashOfNails_value_43"] */, /* param start [id:"SlashOfNails_value_44" type:"number"] */68/* param end [id:"SlashOfNails_value_44"] */));
        /* block end [id:"SlashOfNails_block_12"] */
        /* block comment [block:"SlashOfNails_block_13" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"SlashOfNails_block_13" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(ResourcePool.getCostume(/* param start [id:"SlashOfNails_value_45" type:"text"] */"slash3"/* param end [id:"SlashOfNails_value_45"] */, /* param start [id:"SlashOfNails_value_46" type:"text"] */"jscratch_ws/assets/Nail slash_slash3.png"/* param end [id:"SlashOfNails_value_46"] */, /* param start [id:"SlashOfNails_value_47" type:"number"] */70/* param end [id:"SlashOfNails_value_47"] */, /* param start [id:"SlashOfNails_value_48" type:"number"] */28/* param end [id:"SlashOfNails_value_48"] */));
        /* block end [id:"SlashOfNails_block_13"] */
        /* block comment [block:"SlashOfNails_block_14" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"SlashOfNails_block_14" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"SlashOfNails_block_1" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"SlashOfNails_block_1" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (minimapSprite != /* param start [id:"SlashOfNails_value_49" type:"value"] */null/* param end [id:"SlashOfNails_value_49"] */) minimapSprite.hide();
        /* block end [id:"SlashOfNails_block_14"] */
        /* block end [id:"SlashOfNails_block_1"] */

        /* block comment [block:"SlashOfNails_block_15" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"SlashOfNails_block_15" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.localScale = /* param start [id:"SlashOfNails_value_50" type:"number"] */0.9/* param end [id:"SlashOfNails_value_50"] */;
        /* block end [id:"SlashOfNails_block_15"] */
        // Offset slightly forward so the arc sits in front of the caster
        /* block comment [block:"SlashOfNails_block_16" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"SlashOfNails_block_16" category:"Variables" label:"set rad" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double rad = Math.toRadians(/* param start [id:"SlashOfNails_value_51" type:"number"] */90/* param end [id:"SlashOfNails_value_51"] */ - dirDeg);
        /* block end [id:"SlashOfNails_block_16"] */
        /* block comment [block:"SlashOfNails_block_17" text:"Runs the cos action. Values in this block are editable inputs."] */
        /* block start [id:"SlashOfNails_block_17" category:"My Blocks" label:"cos" shape:"command" docs:"Runs the cos action. Values in this block are editable inputs."] */
        xx = wx + (int) (Math.cos(rad) * /* param start [id:"SlashOfNails_value_52" type:"number"] */55/* param end [id:"SlashOfNails_value_52"] */);
        /* block end [id:"SlashOfNails_block_17"] */
        /* block comment [block:"SlashOfNails_block_18" text:"Runs the sin action. Values in this block are editable inputs."] */
        /* block start [id:"SlashOfNails_block_18" category:"My Blocks" label:"sin" shape:"command" docs:"Runs the sin action. Values in this block are editable inputs."] */
        yy = wy + (int) (Math.sin(rad) * /* param start [id:"SlashOfNails_value_53" type:"number"] */55/* param end [id:"SlashOfNails_value_53"] */);
        /* block end [id:"SlashOfNails_block_18"] */
        /* block comment [block:"SlashOfNails_block_19" text:"Runs the point in direction action. Values in this block are editable inputs."] */
        /* block start [id:"SlashOfNails_block_19" category:"Motion" label:"point in direction" shape:"command" docs:"Runs the point in direction action. Values in this block are editable inputs."] */
        pointInDirection(dirDeg);
        /* block end [id:"SlashOfNails_block_19"] */
        /* block comment [block:"SlashOfNails_block_20" text:"Calls the update method using its declared arguments."] */
        /* block start [id:"SlashOfNails_block_20" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
        update();
        /* block end [id:"SlashOfNails_block_20"] */
        /* block comment [block:"SlashOfNails_block_21" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"SlashOfNails_block_21" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().goToFront(this);
        /* block end [id:"SlashOfNails_block_21"] */
        /* block comment [block:"SlashOfNails_block_22" text:"Runs the switch costume action. Values in this block are editable inputs."] */
        /* block start [id:"SlashOfNails_block_22" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
        switchCostume(/* param start [id:"SlashOfNails_value_54" type:"text"] */"slash1"/* param end [id:"SlashOfNails_value_54"] */);
        /* block end [id:"SlashOfNails_block_22"] */

        // Resolve damage immediately, then play the 3-frame flash
        /* block comment [block:"SlashOfNails_block_23" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"SlashOfNails_block_23" category:"Variables" label:"set bonus" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int bonus = PlayerStats.ready ? PlayerStats.effectiveMelee() : /* param start [id:"SlashOfNails_value_55" type:"number"] */0/* param end [id:"SlashOfNails_value_55"] */;
        /* block end [id:"SlashOfNails_block_23"] */
        /* block comment [block:"SlashOfNails_block_24" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"SlashOfNails_block_24" category:"Variables" label:"set strMod" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int strMod = (PlayerStats.ready && PlayerStats.stats != /* param start [id:"SlashOfNails_value_56" type:"value"] */null/* param end [id:"SlashOfNails_value_56"] */) ? PlayerStats.stats.strengthModifier : /* param start [id:"SlashOfNails_value_57" type:"number"] */0/* param end [id:"SlashOfNails_value_57"] */;
        /* block end [id:"SlashOfNails_block_24"] */
        /* block comment [block:"SlashOfNails_block_25" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"SlashOfNails_block_25" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        strMod += PlayerStats.bonusDmg;
        /* block end [id:"SlashOfNails_block_25"] */
        /* block comment [block:"SlashOfNails_block_2" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"SlashOfNails_block_2" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (EnemySprite e : EnemyManager.getInstance().getEnemies()) {
            /* block comment [block:"SlashOfNails_block_26" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"SlashOfNails_block_26" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"SlashOfNails_block_3" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"SlashOfNails_block_3" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (!e.isVisible()) continue;
            /* block end [id:"SlashOfNails_block_26"] */
            /* block end [id:"SlashOfNails_block_3"] */
            /* block comment [block:"SlashOfNails_block_27" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"SlashOfNails_block_27" category:"Variables" label:"set dx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double dx = e.xx - wx;
            /* block end [id:"SlashOfNails_block_27"] */
            /* block comment [block:"SlashOfNails_block_28" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"SlashOfNails_block_28" category:"Variables" label:"set dy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double dy = e.yy - wy;
            /* block end [id:"SlashOfNails_block_28"] */
            /* block comment [block:"SlashOfNails_block_29" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"SlashOfNails_block_29" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"SlashOfNails_block_4" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"SlashOfNails_block_4" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (dx * dx + dy * dy > /* param start [id:"SlashOfNails_value_58" type:"number"] */100/* param end [id:"SlashOfNails_value_58"] */ * /* param start [id:"SlashOfNails_value_59" type:"number"] */100/* param end [id:"SlashOfNails_value_59"] */) continue;
            /* block end [id:"SlashOfNails_block_29"] */
            /* block end [id:"SlashOfNails_block_4"] */
            /* block comment [block:"SlashOfNails_block_30" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"SlashOfNails_block_30" category:"Variables" label:"set targetDir" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double targetDir = /* param start [id:"SlashOfNails_value_60" type:"number"] */90/* param end [id:"SlashOfNails_value_60"] */ - Math.toDegrees(Math.atan2(dy, dx));
            /* block end [id:"SlashOfNails_block_30"] */
            /* block comment [block:"SlashOfNails_block_31" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"SlashOfNails_block_31" category:"Variables" label:"set diff" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double diff = Math.abs(((targetDir - dirDeg + /* param start [id:"SlashOfNails_value_61" type:"number"] */540/* param end [id:"SlashOfNails_value_61"] */) % /* param start [id:"SlashOfNails_value_62" type:"number"] */360/* param end [id:"SlashOfNails_value_62"] */) - /* param start [id:"SlashOfNails_value_63" type:"number"] */180/* param end [id:"SlashOfNails_value_63"] */);
            /* block end [id:"SlashOfNails_block_31"] */
            /* block comment [block:"SlashOfNails_block_32" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"SlashOfNails_block_32" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"SlashOfNails_block_5" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"SlashOfNails_block_5" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (diff > /* param start [id:"SlashOfNails_value_64" type:"number"] */50/* param end [id:"SlashOfNails_value_64"] */) continue;
            /* block end [id:"SlashOfNails_block_32"] */
            /* block end [id:"SlashOfNails_block_5"] */ // outside the front arc
            /* block comment [block:"SlashOfNails_block_33" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"SlashOfNails_block_33" category:"Variables" label:"set res" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int res = Combat.meleeStrike(bonus, e.armourClass);
            /* block end [id:"SlashOfNails_block_33"] */
            /* block comment [block:"SlashOfNails_block_34" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"SlashOfNails_block_34" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"SlashOfNails_block_6" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"SlashOfNails_block_6" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (res > /* param start [id:"SlashOfNails_value_65" type:"number"] */0/* param end [id:"SlashOfNails_value_65"] */) e.takeDamage(Combat.rollDamage(/* param start [id:"SlashOfNails_value_66" type:"number"] */6/* param end [id:"SlashOfNails_value_66"] */, strMod, res == /* param start [id:"SlashOfNails_value_67" type:"number"] */2/* param end [id:"SlashOfNails_value_67"] */) + Combat.rollDie(/* param start [id:"SlashOfNails_value_68" type:"number"] */6/* param end [id:"SlashOfNails_value_68"] */));
            /* block end [id:"SlashOfNails_block_34"] */
            /* block end [id:"SlashOfNails_block_6"] */
        }
        /* block end [id:"SlashOfNails_block_2"] */

        this.startScript(() -> {
            /* block comment [block:"SlashOfNails_block_7" text:"Runs work safely and handles matching errors in a catch branch."] */
            /* block start [id:"SlashOfNails_block_7" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
            try { Thread.sleep(/* param start [id:"SlashOfNails_value_69" type:"number"] */70/* param end [id:"SlashOfNails_value_69"] */); }
            /* block end [id:"SlashOfNails_block_7"] */ catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
            switchCostume("slash2");
            update();
            /* block comment [block:"SlashOfNails_block_8" text:"Runs work safely and handles matching errors in a catch branch."] */
            /* block start [id:"SlashOfNails_block_8" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
            try { Thread.sleep(/* param start [id:"SlashOfNails_value_70" type:"number"] */70/* param end [id:"SlashOfNails_value_70"] */); }
            /* block end [id:"SlashOfNails_block_8"] */ catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
            switchCostume("slash3");
            update();
            /* block comment [block:"SlashOfNails_block_9" text:"Runs work safely and handles matching errors in a catch branch."] */
            /* block start [id:"SlashOfNails_block_9" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
            try { Thread.sleep(/* param start [id:"SlashOfNails_value_71" type:"number"] */70/* param end [id:"SlashOfNails_value_71"] */); }
            /* block end [id:"SlashOfNails_block_9"] */ catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
            destroy();
        }/* block comment [block:"SlashOfNails_block_35" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"SlashOfNails_block_35" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        );
        /* block end [id:"SlashOfNails_block_35"] */
    }
}
