package generated;

import com.jscratch.*;

/**
* Basic Goblin enemy using imported assets.
* Role: fast skirmisher — tight 90px swarm orbit, kites at 35% HP.
* Wound it and it disengages, then re-engages (see EnemySprite kiting).
*/
public class Goblin extends EnemySprite {
    public Goblin(String name) {
        /* block comment [block:"Goblin_block_4" text:"Runs the super action. Values in this block are editable inputs."] */
        /* block start [id:"Goblin_block_4" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
        super(name);
        /* block end [id:"Goblin_block_4"] */
        /* block comment [block:"Goblin_block_5" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Goblin_block_5" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.localScale = /* param start [id:"Goblin_value_32" type:"number"] */0.5/* param end [id:"Goblin_value_32"] */;
        /* block end [id:"Goblin_block_5"] */ // Goblins are small
        /* block comment [block:"Goblin_block_6" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Goblin_block_6" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.speed = /* param start [id:"Goblin_value_33" type:"number"] */4/* param end [id:"Goblin_value_33"] */;
        /* block end [id:"Goblin_block_6"] */ // Goblins are fast
        /* block comment [block:"Goblin_block_7" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Goblin_block_7" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.bodyRadius = /* param start [id:"Goblin_value_34" type:"number"] */12/* param end [id:"Goblin_value_34"] */;
        /* block end [id:"Goblin_block_7"] */
        /* block comment [block:"Goblin_block_8" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Goblin_block_8" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.health = /* param start [id:"Goblin_value_35" type:"number"] */60/* param end [id:"Goblin_value_35"] */;
        /* block end [id:"Goblin_block_8"] */
        /* block comment [block:"Goblin_block_9" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Goblin_block_9" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.maxHealth = /* param start [id:"Goblin_value_36" type:"number"] */60/* param end [id:"Goblin_value_36"] */;
        /* block end [id:"Goblin_block_9"] */
        /* block comment [block:"Goblin_block_10" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Goblin_block_10" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.attackRange = /* param start [id:"Goblin_value_37" type:"number"] */35/* param end [id:"Goblin_value_37"] */;
        /* block end [id:"Goblin_block_10"] */
        /* block comment [block:"Goblin_block_11" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Goblin_block_11" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.armourClass = /* param start [id:"Goblin_value_38" type:"number"] */12/* param end [id:"Goblin_value_38"] */;
        /* block end [id:"Goblin_block_11"] */
        /* block comment [block:"Goblin_block_12" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Goblin_block_12" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.attackBonus = /* param start [id:"Goblin_value_39" type:"number"] */2/* param end [id:"Goblin_value_39"] */;
        /* block end [id:"Goblin_block_12"] */
        /* block comment [block:"Goblin_block_13" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Goblin_block_13" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.damageDie = /* param start [id:"Goblin_value_40" type:"number"] */6/* param end [id:"Goblin_value_40"] */;
        /* block end [id:"Goblin_block_13"] */
        /* block comment [block:"Goblin_block_14" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Goblin_block_14" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.damageMod = /* param start [id:"Goblin_value_41" type:"number"] */0/* param end [id:"Goblin_value_41"] */;
        /* block end [id:"Goblin_block_14"] */
        /* block comment [block:"Goblin_block_15" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Goblin_block_15" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.xpValue = /* param start [id:"Goblin_value_42" type:"number"] */25/* param end [id:"Goblin_value_42"] */;
        /* block end [id:"Goblin_block_15"] */
        /* block comment [block:"Goblin_block_16" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Goblin_block_16" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.dexSaveBonus = /* param start [id:"Goblin_value_43" type:"number"] */2/* param end [id:"Goblin_value_43"] */;
        /* block end [id:"Goblin_block_16"] */
        /* block comment [block:"Goblin_block_17" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Goblin_block_17" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.conSaveBonus = /* param start [id:"Goblin_value_44" type:"number"] */0/* param end [id:"Goblin_value_44"] */;
        /* block end [id:"Goblin_block_17"] */
        /* block comment [block:"Goblin_block_18" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Goblin_block_18" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.visionRange = /* param start [id:"Goblin_value_45" type:"number"] */450/* param end [id:"Goblin_value_45"] */;
        /* block end [id:"Goblin_block_18"] */

        /* block comment [block:"Goblin_block_19" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"Goblin_block_19" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(ResourcePool.getCostume(/* param start [id:"Goblin_value_46" type:"text"] */"Goblin_idle"/* param end [id:"Goblin_value_46"] */, /* param start [id:"Goblin_value_47" type:"text"] */"jscratch_ws/assets/Goblin_costume_Idle.png"/* param end [id:"Goblin_value_47"] */, /* param start [id:"Goblin_value_48" type:"number"] */32/* param end [id:"Goblin_value_48"] */, /* param start [id:"Goblin_value_49" type:"number"] */32/* param end [id:"Goblin_value_49"] */));
        /* block end [id:"Goblin_block_19"] */
        /* block comment [block:"Goblin_block_20" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"Goblin_block_20" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(ResourcePool.getCostume(/* param start [id:"Goblin_value_50" type:"text"] */"Goblin_walk1"/* param end [id:"Goblin_value_50"] */, /* param start [id:"Goblin_value_51" type:"text"] */"jscratch_ws/assets/Goblin_costume1.png"/* param end [id:"Goblin_value_51"] */, /* param start [id:"Goblin_value_52" type:"number"] */32/* param end [id:"Goblin_value_52"] */, /* param start [id:"Goblin_value_53" type:"number"] */32/* param end [id:"Goblin_value_53"] */));
        /* block end [id:"Goblin_block_20"] */
        /* block comment [block:"Goblin_block_21" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"Goblin_block_21" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(ResourcePool.getCostume(/* param start [id:"Goblin_value_54" type:"text"] */"Goblin_walk2"/* param end [id:"Goblin_value_54"] */, /* param start [id:"Goblin_value_55" type:"text"] */"jscratch_ws/assets/Goblin_costume2.png"/* param end [id:"Goblin_value_55"] */, /* param start [id:"Goblin_value_56" type:"number"] */32/* param end [id:"Goblin_value_56"] */, /* param start [id:"Goblin_value_57" type:"number"] */32/* param end [id:"Goblin_value_57"] */));
        /* block end [id:"Goblin_block_21"] */
        /* block comment [block:"Goblin_block_22" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"Goblin_block_22" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(ResourcePool.getCostume(/* param start [id:"Goblin_value_58" type:"text"] */"Goblin_attack"/* param end [id:"Goblin_value_58"] */, /* param start [id:"Goblin_value_59" type:"text"] */"jscratch_ws/assets/Goblin_costume_attack.png"/* param end [id:"Goblin_value_59"] */, /* param start [id:"Goblin_value_60" type:"number"] */32/* param end [id:"Goblin_value_60"] */, /* param start [id:"Goblin_value_61" type:"number"] */32/* param end [id:"Goblin_value_61"] */));
        /* block end [id:"Goblin_block_22"] */

        /* block comment [block:"Goblin_block_23" text:"Runs the switch costume action. Values in this block are editable inputs."] */
        /* block start [id:"Goblin_block_23" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
        this.switchCostume(/* param start [id:"Goblin_value_62" type:"text"] */"Goblin_idle"/* param end [id:"Goblin_value_62"] */);
        /* block end [id:"Goblin_block_23"] */
    }

    // Skirmisher: hugs striking range, kites when bloodied.
    @Override
    protected int swarmDist() { /* block comment [block:"Goblin_block_24" text:"Returns a value or ends the current method."] */
        /* block start [id:"Goblin_block_24" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return attackRange - /* param start [id:"Goblin_value_63" type:"number"] */8/* param end [id:"Goblin_value_63"] */;
        /* block end [id:"Goblin_block_24"] */ }

    @Override
    protected double retreatHpFrac() { /* block comment [block:"Goblin_block_25" text:"Returns a value or ends the current method."] */
        /* block start [id:"Goblin_block_25" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return /* param start [id:"Goblin_value_64" type:"number"] */0.35/* param end [id:"Goblin_value_64"] */;
        /* block end [id:"Goblin_block_25"] */ }

    @Override
    protected void attackTelegraph(int tx, int ty) {
        /* block comment [block:"Goblin_block_26" text:"Runs the switch costume action. Values in this block are editable inputs."] */
        /* block start [id:"Goblin_block_26" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
        switchCostume(/* param start [id:"Goblin_value_65" type:"text"] */"Goblin_attack"/* param end [id:"Goblin_value_65"] */);
        /* block end [id:"Goblin_block_26"] */
        /* block comment [block:"Goblin_block_27" text:"Runs the move towards action. Values in this block are editable inputs."] */
        /* block start [id:"Goblin_block_27" category:"Motion" label:"move towards" shape:"command" docs:"Runs the move towards action. Values in this block are editable inputs."] */
        moveTowards(tx, ty, speed);
        /* block end [id:"Goblin_block_27"] */
    }

    @Override
    public void update() {
        /* block comment [block:"Goblin_block_28" text:"Runs the update action. Values in this block are editable inputs."] */
        /* block start [id:"Goblin_block_28" category:"My Blocks" label:"update" shape:"command" docs:"Runs the update action. Values in this block are editable inputs."] */
        super.update();
        /* block end [id:"Goblin_block_28"] */
        /* block comment [block:"Goblin_block_1" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Goblin_block_1" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (currentState == AIState.PURSUE || currentState == AIState.SWARM) {
            /* block comment [block:"Goblin_block_2" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Goblin_block_2" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (System.currentTimeMillis() % /* param start [id:"Goblin_value_66" type:"number"] */300/* param end [id:"Goblin_value_66"] */ < /* param start [id:"Goblin_value_67" type:"number"] */150/* param end [id:"Goblin_value_67"] */) {
                /* block comment [block:"Goblin_block_29" text:"Runs the switch costume action. Values in this block are editable inputs."] */
                /* block start [id:"Goblin_block_29" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
                switchCostume(/* param start [id:"Goblin_value_68" type:"text"] */"Goblin_walk1"/* param end [id:"Goblin_value_68"] */);
                /* block end [id:"Goblin_block_29"] */
            } else {
                /* block comment [block:"Goblin_block_30" text:"Runs the switch costume action. Values in this block are editable inputs."] */
                /* block start [id:"Goblin_block_30" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
                switchCostume(/* param start [id:"Goblin_value_69" type:"text"] */"Goblin_walk2"/* param end [id:"Goblin_value_69"] */);
                /* block end [id:"Goblin_block_30"] */
            }
            /* block end [id:"Goblin_block_2"] */
        } else /* block comment [block:"Goblin_block_3" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Goblin_block_3" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (currentState != AIState.ATTACK) {
            /* block comment [block:"Goblin_block_31" text:"Runs the switch costume action. Values in this block are editable inputs."] */
            /* block start [id:"Goblin_block_31" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
            switchCostume(/* param start [id:"Goblin_value_70" type:"text"] */"Goblin_idle"/* param end [id:"Goblin_value_70"] */);
            /* block end [id:"Goblin_block_31"] */
        }
        /* block end [id:"Goblin_block_3"] */
        /* block end [id:"Goblin_block_1"] */
    }
}
