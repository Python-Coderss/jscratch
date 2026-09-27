package generated;

import com.jscratch.*;

/**
* Specialized Orc enemy using imported assets.
* Role: brave bruiser — kites only below 20% HP, hugs striking range;
* wind-up does a 4x step-in burst.
*/
public class Orc extends EnemySprite {
    public Orc(String name) {
        /* block comment [block:"Orc_block_4" text:"Runs the super action. Values in this block are editable inputs."] */
        /* block start [id:"Orc_block_4" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
        super(name);
        /* block end [id:"Orc_block_4"] */
        /* block comment [block:"Orc_block_5" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Orc_block_5" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.localScale = /* param start [id:"Orc_value_30" type:"number"] */0.7/* param end [id:"Orc_value_30"] */;
        /* block end [id:"Orc_block_5"] */ // Orcs are large
        /* block comment [block:"Orc_block_6" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Orc_block_6" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.speed = /* param start [id:"Orc_value_31" type:"number"] */3/* param end [id:"Orc_value_31"] */;
        /* block end [id:"Orc_block_6"] */
        /* block comment [block:"Orc_block_7" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Orc_block_7" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.bodyRadius = /* param start [id:"Orc_value_32" type:"number"] */16/* param end [id:"Orc_value_32"] */;
        /* block end [id:"Orc_block_7"] */
        /* block comment [block:"Orc_block_8" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Orc_block_8" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.health = /* param start [id:"Orc_value_33" type:"number"] */120/* param end [id:"Orc_value_33"] */;
        /* block end [id:"Orc_block_8"] */
        /* block comment [block:"Orc_block_9" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Orc_block_9" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.maxHealth = /* param start [id:"Orc_value_34" type:"number"] */120/* param end [id:"Orc_value_34"] */;
        /* block end [id:"Orc_block_9"] */
        /* block comment [block:"Orc_block_10" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Orc_block_10" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.armourClass = /* param start [id:"Orc_value_35" type:"number"] */11/* param end [id:"Orc_value_35"] */;
        /* block end [id:"Orc_block_10"] */
        /* block comment [block:"Orc_block_11" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Orc_block_11" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.attackBonus = /* param start [id:"Orc_value_36" type:"number"] */3/* param end [id:"Orc_value_36"] */;
        /* block end [id:"Orc_block_11"] */
        /* block comment [block:"Orc_block_12" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Orc_block_12" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.damageDie = /* param start [id:"Orc_value_37" type:"number"] */8/* param end [id:"Orc_value_37"] */;
        /* block end [id:"Orc_block_12"] */
        /* block comment [block:"Orc_block_13" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Orc_block_13" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.damageMod = /* param start [id:"Orc_value_38" type:"number"] */1/* param end [id:"Orc_value_38"] */;
        /* block end [id:"Orc_block_13"] */
        /* block comment [block:"Orc_block_14" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Orc_block_14" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.xpValue = /* param start [id:"Orc_value_39" type:"number"] */40/* param end [id:"Orc_value_39"] */;
        /* block end [id:"Orc_block_14"] */
        /* block comment [block:"Orc_block_15" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Orc_block_15" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.dexSaveBonus = /* param start [id:"Orc_value_40" type:"number"] */0/* param end [id:"Orc_value_40"] */;
        /* block end [id:"Orc_block_15"] */
        /* block comment [block:"Orc_block_16" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Orc_block_16" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.conSaveBonus = /* param start [id:"Orc_value_41" type:"number"] */2/* param end [id:"Orc_value_41"] */;
        /* block end [id:"Orc_block_16"] */
        /* block comment [block:"Orc_block_17" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Orc_block_17" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.visionRange = /* param start [id:"Orc_value_42" type:"number"] */550/* param end [id:"Orc_value_42"] */;
        /* block end [id:"Orc_block_17"] */

        // Imported Orc costumes (Pooled)
        /* block comment [block:"Orc_block_18" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"Orc_block_18" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(ResourcePool.getCostume(/* param start [id:"Orc_value_43" type:"text"] */"Orc_idle"/* param end [id:"Orc_value_43"] */, /* param start [id:"Orc_value_44" type:"text"] */"jscratch_ws/assets/Orc_costume_idle.png"/* param end [id:"Orc_value_44"] */, /* param start [id:"Orc_value_45" type:"number"] */32/* param end [id:"Orc_value_45"] */, /* param start [id:"Orc_value_46" type:"number"] */32/* param end [id:"Orc_value_46"] */));
        /* block end [id:"Orc_block_18"] */
        /* block comment [block:"Orc_block_19" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"Orc_block_19" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(ResourcePool.getCostume(/* param start [id:"Orc_value_47" type:"text"] */"Orc_walk1"/* param end [id:"Orc_value_47"] */, /* param start [id:"Orc_value_48" type:"text"] */"jscratch_ws/assets/Orc_costume1.png"/* param end [id:"Orc_value_48"] */, /* param start [id:"Orc_value_49" type:"number"] */32/* param end [id:"Orc_value_49"] */, /* param start [id:"Orc_value_50" type:"number"] */32/* param end [id:"Orc_value_50"] */));
        /* block end [id:"Orc_block_19"] */
        /* block comment [block:"Orc_block_20" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"Orc_block_20" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(ResourcePool.getCostume(/* param start [id:"Orc_value_51" type:"text"] */"Orc_walk2"/* param end [id:"Orc_value_51"] */, /* param start [id:"Orc_value_52" type:"text"] */"jscratch_ws/assets/Orc_costume2.png"/* param end [id:"Orc_value_52"] */, /* param start [id:"Orc_value_53" type:"number"] */32/* param end [id:"Orc_value_53"] */, /* param start [id:"Orc_value_54" type:"number"] */32/* param end [id:"Orc_value_54"] */));
        /* block end [id:"Orc_block_20"] */

        /* block comment [block:"Orc_block_21" text:"Runs the switch costume action. Values in this block are editable inputs."] */
        /* block start [id:"Orc_block_21" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
        this.switchCostume(/* param start [id:"Orc_value_55" type:"text"] */"Orc_idle"/* param end [id:"Orc_value_55"] */);
        /* block end [id:"Orc_block_21"] */
    }

    // Bruiser: brave (kites only near death) and hugs striking range.
    @Override
    protected double retreatHpFrac() { /* block comment [block:"Orc_block_22" text:"Returns a value or ends the current method."] */
        /* block start [id:"Orc_block_22" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return /* param start [id:"Orc_value_56" type:"number"] */0.20/* param end [id:"Orc_value_56"] */;
        /* block end [id:"Orc_block_22"] */ }

    @Override
    protected int swarmDist() { /* block comment [block:"Orc_block_23" text:"Returns a value or ends the current method."] */
        /* block start [id:"Orc_block_23" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return attackRange - /* param start [id:"Orc_value_57" type:"number"] */8/* param end [id:"Orc_value_57"] */;
        /* block end [id:"Orc_block_23"] */ }

    @Override
    protected void attackTelegraph(int tx, int ty) {
        // Orc 'Rush' step-in
        /* block comment [block:"Orc_block_1" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Orc_block_1" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (System.currentTimeMillis() % /* param start [id:"Orc_value_58" type:"number"] */1500/* param end [id:"Orc_value_58"] */ < /* param start [id:"Orc_value_59" type:"number"] */200/* param end [id:"Orc_value_59"] */) {
            /* block comment [block:"Orc_block_24" text:"Runs the move towards action. Values in this block are editable inputs."] */
            /* block start [id:"Orc_block_24" category:"Motion" label:"move towards" shape:"command" docs:"Runs the move towards action. Values in this block are editable inputs."] */
            moveTowards(tx, ty, (int)(speed * /* param start [id:"Orc_value_60" type:"number"] */4/* param end [id:"Orc_value_60"] */));
            /* block end [id:"Orc_block_24"] */
        } else {
            /* block comment [block:"Orc_block_25" text:"Runs the move towards action. Values in this block are editable inputs."] */
            /* block start [id:"Orc_block_25" category:"Motion" label:"move towards" shape:"command" docs:"Runs the move towards action. Values in this block are editable inputs."] */
            moveTowards(tx, ty, speed);
            /* block end [id:"Orc_block_25"] */
        }
        /* block end [id:"Orc_block_1"] */
    }

    @Override
    public void update() {
        /* block comment [block:"Orc_block_26" text:"Runs the update action. Values in this block are editable inputs."] */
        /* block start [id:"Orc_block_26" category:"My Blocks" label:"update" shape:"command" docs:"Runs the update action. Values in this block are editable inputs."] */
        super.update();
        /* block end [id:"Orc_block_26"] */
        // Simple animation logic
        /* block comment [block:"Orc_block_2" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Orc_block_2" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (currentState == AIState.PURSUE || currentState == AIState.SWARM) {
            /* block comment [block:"Orc_block_3" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Orc_block_3" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (System.currentTimeMillis() % /* param start [id:"Orc_value_61" type:"number"] */400/* param end [id:"Orc_value_61"] */ < /* param start [id:"Orc_value_62" type:"number"] */200/* param end [id:"Orc_value_62"] */) {
                /* block comment [block:"Orc_block_27" text:"Runs the switch costume action. Values in this block are editable inputs."] */
                /* block start [id:"Orc_block_27" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
                switchCostume(/* param start [id:"Orc_value_63" type:"text"] */"Orc_walk1"/* param end [id:"Orc_value_63"] */);
                /* block end [id:"Orc_block_27"] */
            } else {
                /* block comment [block:"Orc_block_28" text:"Runs the switch costume action. Values in this block are editable inputs."] */
                /* block start [id:"Orc_block_28" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
                switchCostume(/* param start [id:"Orc_value_64" type:"text"] */"Orc_walk2"/* param end [id:"Orc_value_64"] */);
                /* block end [id:"Orc_block_28"] */
            }
            /* block end [id:"Orc_block_3"] */
        } else {
            /* block comment [block:"Orc_block_29" text:"Runs the switch costume action. Values in this block are editable inputs."] */
            /* block start [id:"Orc_block_29" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
            switchCostume(/* param start [id:"Orc_value_65" type:"text"] */"Orc_idle"/* param end [id:"Orc_value_65"] */);
            /* block end [id:"Orc_block_29"] */
        }
        /* block end [id:"Orc_block_2"] */
    }
}
