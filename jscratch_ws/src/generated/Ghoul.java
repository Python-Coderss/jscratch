package generated;

import com.jscratch.*;

/**
* Ghoul: fast, fragile skirmisher (113x146 single-frame asset).
* Role: hit-and-run — kites at 35% HP and (via onLandedStrike) often darts
* away for 1s right after landing a blow.
*/
public class Ghoul extends EnemySprite {
    public Ghoul(String name) {
        /* block comment [block:"Ghoul_block_2" text:"Runs the super action. Values in this block are editable inputs."] */
        /* block start [id:"Ghoul_block_2" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
        super(name);
        /* block end [id:"Ghoul_block_2"] */
        /* block comment [block:"Ghoul_block_3" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Ghoul_block_3" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.localScale = /* param start [id:"Ghoul_value_22" type:"number"] */0.6/* param end [id:"Ghoul_value_22"] */;
        /* block end [id:"Ghoul_block_3"] */
        /* block comment [block:"Ghoul_block_4" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Ghoul_block_4" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.speed = /* param start [id:"Ghoul_value_23" type:"number"] */5/* param end [id:"Ghoul_value_23"] */;
        /* block end [id:"Ghoul_block_4"] */
        /* block comment [block:"Ghoul_block_5" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Ghoul_block_5" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.bodyRadius = /* param start [id:"Ghoul_value_24" type:"number"] */12/* param end [id:"Ghoul_value_24"] */;
        /* block end [id:"Ghoul_block_5"] */
        /* block comment [block:"Ghoul_block_6" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Ghoul_block_6" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.health = /* param start [id:"Ghoul_value_25" type:"number"] */45/* param end [id:"Ghoul_value_25"] */;
        /* block end [id:"Ghoul_block_6"] */
        /* block comment [block:"Ghoul_block_7" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Ghoul_block_7" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.maxHealth = /* param start [id:"Ghoul_value_26" type:"number"] */45/* param end [id:"Ghoul_value_26"] */;
        /* block end [id:"Ghoul_block_7"] */
        /* block comment [block:"Ghoul_block_8" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Ghoul_block_8" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.attackRange = /* param start [id:"Ghoul_value_27" type:"number"] */35/* param end [id:"Ghoul_value_27"] */;
        /* block end [id:"Ghoul_block_8"] */
        /* block comment [block:"Ghoul_block_9" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Ghoul_block_9" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.armourClass = /* param start [id:"Ghoul_value_28" type:"number"] */12/* param end [id:"Ghoul_value_28"] */;
        /* block end [id:"Ghoul_block_9"] */
        /* block comment [block:"Ghoul_block_10" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Ghoul_block_10" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.attackBonus = /* param start [id:"Ghoul_value_29" type:"number"] */3/* param end [id:"Ghoul_value_29"] */;
        /* block end [id:"Ghoul_block_10"] */
        /* block comment [block:"Ghoul_block_11" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Ghoul_block_11" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.damageDie = /* param start [id:"Ghoul_value_30" type:"number"] */6/* param end [id:"Ghoul_value_30"] */;
        /* block end [id:"Ghoul_block_11"] */
        /* block comment [block:"Ghoul_block_12" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Ghoul_block_12" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.damageMod = /* param start [id:"Ghoul_value_31" type:"number"] */0/* param end [id:"Ghoul_value_31"] */;
        /* block end [id:"Ghoul_block_12"] */
        /* block comment [block:"Ghoul_block_13" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Ghoul_block_13" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.dexSaveBonus = /* param start [id:"Ghoul_value_32" type:"number"] */2/* param end [id:"Ghoul_value_32"] */;
        /* block end [id:"Ghoul_block_13"] */
        /* block comment [block:"Ghoul_block_14" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Ghoul_block_14" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.conSaveBonus = /* param start [id:"Ghoul_value_33" type:"number"] */0/* param end [id:"Ghoul_value_33"] */;
        /* block end [id:"Ghoul_block_14"] */
        /* block comment [block:"Ghoul_block_15" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Ghoul_block_15" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.xpValue = /* param start [id:"Ghoul_value_34" type:"number"] */35/* param end [id:"Ghoul_value_34"] */;
        /* block end [id:"Ghoul_block_15"] */
        /* block comment [block:"Ghoul_block_16" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Ghoul_block_16" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.visionRange = /* param start [id:"Ghoul_value_35" type:"number"] */500/* param end [id:"Ghoul_value_35"] */;
        /* block end [id:"Ghoul_block_16"] */

        /* block comment [block:"Ghoul_block_17" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"Ghoul_block_17" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(ResourcePool.getCostume(/* param start [id:"Ghoul_value_36" type:"text"] */"Ghoul"/* param end [id:"Ghoul_value_36"] */, /* param start [id:"Ghoul_value_37" type:"text"] */"jscratch_ws/assets/ghoul_costume1.png"/* param end [id:"Ghoul_value_37"] */, /* param start [id:"Ghoul_value_38" type:"number"] */56/* param end [id:"Ghoul_value_38"] */, /* param start [id:"Ghoul_value_39" type:"number"] */73/* param end [id:"Ghoul_value_39"] */));
        /* block end [id:"Ghoul_block_17"] */
        /* block comment [block:"Ghoul_block_18" text:"Runs the switch costume action. Values in this block are editable inputs."] */
        /* block start [id:"Ghoul_block_18" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
        this.switchCostume(/* param start [id:"Ghoul_value_40" type:"text"] */"Ghoul"/* param end [id:"Ghoul_value_40"] */);
        /* block end [id:"Ghoul_block_18"] */
        // The art is drawn with a 180-degree offset; AI direction stays natural.
        /* block comment [block:"Ghoul_block_19" text:"Runs the set costume rotation offset action. Values in this block are editable inputs."] */
        /* block start [id:"Ghoul_block_19" category:"Looks" label:"set costume rotation offset" shape:"command" docs:"Runs the set costume rotation offset action. Values in this block are editable inputs."] */
        setCostumeRotationOffset(/* param start [id:"Ghoul_value_41" type:"number"] */180/* param end [id:"Ghoul_value_41"] */);
        /* block end [id:"Ghoul_block_19"] */
    }

    // Skirmisher: kites early and often darts away right after landing a hit.
    @Override
    protected double retreatHpFrac() { /* block comment [block:"Ghoul_block_20" text:"Returns a value or ends the current method."] */
        /* block start [id:"Ghoul_block_20" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return /* param start [id:"Ghoul_value_42" type:"number"] */0.35/* param end [id:"Ghoul_value_42"] */;
        /* block end [id:"Ghoul_block_20"] */ }

    @Override
    protected void onLandedStrike(boolean hit) {
        /* block comment [block:"Ghoul_block_1" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Ghoul_block_1" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (hit && Math.random() < /* param start [id:"Ghoul_value_43" type:"number"] */0.35/* param end [id:"Ghoul_value_43"] */) {
            /* block comment [block:"Ghoul_block_21" text:"Calls the startRetreat method using its declared arguments."] */
            /* block start [id:"Ghoul_block_21" category:"My Blocks" label:"call start retreat" shape:"command" docs:"Calls the startRetreat method using its declared arguments."] */
            startRetreat(System.currentTimeMillis(), /* param start [id:"Ghoul_value_44" type:"number"] */1000/* param end [id:"Ghoul_value_44"] */);
            /* block end [id:"Ghoul_block_21"] */
        }
        /* block end [id:"Ghoul_block_1"] */
    }
}
