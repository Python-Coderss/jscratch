package generated;

import com.jscratch.*;

/**
* Basic Zombie enemy using imported assets.
* Role: relentless shambler — never kites, never orbits; walks straight
* through to melee range. Slow speed, high CON save.
*/
public class Zombie extends EnemySprite {
    public Zombie(String name) {
        /* block comment [block:"Zombie_block_3" text:"Runs the super action. Values in this block are editable inputs."] */
        /* block start [id:"Zombie_block_3" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
        super(name);
        /* block end [id:"Zombie_block_3"] */
        /* block comment [block:"Zombie_block_4" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Zombie_block_4" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.localScale = /* param start [id:"Zombie_value_28" type:"number"] */1/* param end [id:"Zombie_value_28"] */;
        /* block end [id:"Zombie_block_4"] */ // Zombies are medium-large
        /* block comment [block:"Zombie_block_5" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Zombie_block_5" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.speed = /* param start [id:"Zombie_value_29" type:"number"] */1/* param end [id:"Zombie_value_29"] */;
        /* block end [id:"Zombie_block_5"] */ // Zombies are slow
        /* block comment [block:"Zombie_block_6" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Zombie_block_6" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.bodyRadius = /* param start [id:"Zombie_value_30" type:"number"] */16/* param end [id:"Zombie_value_30"] */;
        /* block end [id:"Zombie_block_6"] */
        /* block comment [block:"Zombie_block_7" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Zombie_block_7" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.health = /* param start [id:"Zombie_value_31" type:"number"] */80/* param end [id:"Zombie_value_31"] */;
        /* block end [id:"Zombie_block_7"] */
        /* block comment [block:"Zombie_block_8" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Zombie_block_8" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.maxHealth = /* param start [id:"Zombie_value_32" type:"number"] */80/* param end [id:"Zombie_value_32"] */;
        /* block end [id:"Zombie_block_8"] */
        /* block comment [block:"Zombie_block_9" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Zombie_block_9" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.attackRange = /* param start [id:"Zombie_value_33" type:"number"] */30/* param end [id:"Zombie_value_33"] */;
        /* block end [id:"Zombie_block_9"] */
        /* block comment [block:"Zombie_block_10" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Zombie_block_10" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.armourClass = /* param start [id:"Zombie_value_34" type:"number"] */10/* param end [id:"Zombie_value_34"] */;
        /* block end [id:"Zombie_block_10"] */
        /* block comment [block:"Zombie_block_11" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Zombie_block_11" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.attackBonus = /* param start [id:"Zombie_value_35" type:"number"] */1/* param end [id:"Zombie_value_35"] */;
        /* block end [id:"Zombie_block_11"] */
        /* block comment [block:"Zombie_block_12" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Zombie_block_12" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.damageDie = /* param start [id:"Zombie_value_36" type:"number"] */6/* param end [id:"Zombie_value_36"] */;
        /* block end [id:"Zombie_block_12"] */
        /* block comment [block:"Zombie_block_13" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Zombie_block_13" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.damageMod = /* param start [id:"Zombie_value_37" type:"number"] */1/* param end [id:"Zombie_value_37"] */;
        /* block end [id:"Zombie_block_13"] */
        /* block comment [block:"Zombie_block_14" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Zombie_block_14" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.xpValue = /* param start [id:"Zombie_value_38" type:"number"] */30/* param end [id:"Zombie_value_38"] */;
        /* block end [id:"Zombie_block_14"] */
        /* block comment [block:"Zombie_block_15" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Zombie_block_15" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.dexSaveBonus = -/* param start [id:"Zombie_value_39" type:"number"] */1/* param end [id:"Zombie_value_39"] */;
        /* block end [id:"Zombie_block_15"] */
        /* block comment [block:"Zombie_block_16" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Zombie_block_16" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.conSaveBonus = /* param start [id:"Zombie_value_40" type:"number"] */3/* param end [id:"Zombie_value_40"] */;
        /* block end [id:"Zombie_block_16"] */
        /* block comment [block:"Zombie_block_17" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Zombie_block_17" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.visionRange = /* param start [id:"Zombie_value_41" type:"number"] */300/* param end [id:"Zombie_value_41"] */;
        /* block end [id:"Zombie_block_17"] */

        /* block comment [block:"Zombie_block_18" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"Zombie_block_18" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(ResourcePool.getCostume(/* param start [id:"Zombie_value_42" type:"text"] */"Zombie_idle"/* param end [id:"Zombie_value_42"] */, /* param start [id:"Zombie_value_43" type:"text"] */"jscratch_ws/assets/Zombie_costume_idle.png"/* param end [id:"Zombie_value_43"] */, /* param start [id:"Zombie_value_44" type:"number"] */32/* param end [id:"Zombie_value_44"] */, /* param start [id:"Zombie_value_45" type:"number"] */32/* param end [id:"Zombie_value_45"] */));
        /* block end [id:"Zombie_block_18"] */
        /* block comment [block:"Zombie_block_19" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"Zombie_block_19" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(ResourcePool.getCostume(/* param start [id:"Zombie_value_46" type:"text"] */"Zombie_walk1"/* param end [id:"Zombie_value_46"] */, /* param start [id:"Zombie_value_47" type:"text"] */"jscratch_ws/assets/Zombie_costume1.png"/* param end [id:"Zombie_value_47"] */, /* param start [id:"Zombie_value_48" type:"number"] */32/* param end [id:"Zombie_value_48"] */, /* param start [id:"Zombie_value_49" type:"number"] */32/* param end [id:"Zombie_value_49"] */));
        /* block end [id:"Zombie_block_19"] */
        /* block comment [block:"Zombie_block_20" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"Zombie_block_20" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(ResourcePool.getCostume(/* param start [id:"Zombie_value_50" type:"text"] */"Zombie_walk2"/* param end [id:"Zombie_value_50"] */, /* param start [id:"Zombie_value_51" type:"text"] */"jscratch_ws/assets/Zombie_costume2.png"/* param end [id:"Zombie_value_51"] */, /* param start [id:"Zombie_value_52" type:"number"] */32/* param end [id:"Zombie_value_52"] */, /* param start [id:"Zombie_value_53" type:"number"] */32/* param end [id:"Zombie_value_53"] */));
        /* block end [id:"Zombie_block_20"] */

        /* block comment [block:"Zombie_block_21" text:"Runs the switch costume action. Values in this block are editable inputs."] */
        /* block start [id:"Zombie_block_21" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
        this.switchCostume(/* param start [id:"Zombie_value_54" type:"text"] */"Zombie_idle"/* param end [id:"Zombie_value_54"] */);
        /* block end [id:"Zombie_block_21"] */
    }

    // Relentless: never kites, never orbits. It shambles straight at you.
    @Override
    protected boolean canRetreat() { /* block comment [block:"Zombie_block_22" text:"Returns a value or ends the current method."] */
        /* block start [id:"Zombie_block_22" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return /* param start [id:"Zombie_value_55" type:"boolean"] */false/* param end [id:"Zombie_value_55"] */;
        /* block end [id:"Zombie_block_22"] */ }

    @Override
    protected boolean canSwarm() { /* block comment [block:"Zombie_block_23" text:"Returns a value or ends the current method."] */
        /* block start [id:"Zombie_block_23" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return /* param start [id:"Zombie_value_56" type:"boolean"] */false/* param end [id:"Zombie_value_56"] */;
        /* block end [id:"Zombie_block_23"] */ }

    @Override
    public void update() {
        /* block comment [block:"Zombie_block_24" text:"Runs the update action. Values in this block are editable inputs."] */
        /* block start [id:"Zombie_block_24" category:"My Blocks" label:"update" shape:"command" docs:"Runs the update action. Values in this block are editable inputs."] */
        super.update();
        /* block end [id:"Zombie_block_24"] */
        /* block comment [block:"Zombie_block_1" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Zombie_block_1" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (currentState == AIState.PURSUE) {
            /* block comment [block:"Zombie_block_2" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Zombie_block_2" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (System.currentTimeMillis() % /* param start [id:"Zombie_value_57" type:"number"] */600/* param end [id:"Zombie_value_57"] */ < /* param start [id:"Zombie_value_58" type:"number"] */300/* param end [id:"Zombie_value_58"] */) {
                /* block comment [block:"Zombie_block_25" text:"Runs the switch costume action. Values in this block are editable inputs."] */
                /* block start [id:"Zombie_block_25" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
                switchCostume(/* param start [id:"Zombie_value_59" type:"text"] */"Zombie_walk1"/* param end [id:"Zombie_value_59"] */);
                /* block end [id:"Zombie_block_25"] */
            } else {
                /* block comment [block:"Zombie_block_26" text:"Runs the switch costume action. Values in this block are editable inputs."] */
                /* block start [id:"Zombie_block_26" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
                switchCostume(/* param start [id:"Zombie_value_60" type:"text"] */"Zombie_walk2"/* param end [id:"Zombie_value_60"] */);
                /* block end [id:"Zombie_block_26"] */
            }
            /* block end [id:"Zombie_block_2"] */
        } else {
            /* block comment [block:"Zombie_block_27" text:"Runs the switch costume action. Values in this block are editable inputs."] */
            /* block start [id:"Zombie_block_27" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
            switchCostume(/* param start [id:"Zombie_value_61" type:"text"] */"Zombie_idle"/* param end [id:"Zombie_value_61"] */);
            /* block end [id:"Zombie_block_27"] */
        }
        /* block end [id:"Zombie_block_1"] */
    }
}
