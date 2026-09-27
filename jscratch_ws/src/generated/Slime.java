package generated;

import com.jscratch.*;

/**
* Slime: slow, tanky blob (58x55 single-frame asset).
* Role: mindless tank — never kites; soaks hits (140 HP, high CON) and
* holds melee range.
*/
public class Slime extends EnemySprite {
    public Slime(String name) {
        /* block comment [block:"Slime_block_1" text:"Runs the super action. Values in this block are editable inputs."] */
        /* block start [id:"Slime_block_1" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
        super(name);
        /* block end [id:"Slime_block_1"] */
        /* block comment [block:"Slime_block_2" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Slime_block_2" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.localScale = /* param start [id:"Slime_value_19" type:"number"] */0.9/* param end [id:"Slime_value_19"] */;
        /* block end [id:"Slime_block_2"] */
        /* block comment [block:"Slime_block_3" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Slime_block_3" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.speed = /* param start [id:"Slime_value_20" type:"number"] */1/* param end [id:"Slime_value_20"] */;
        /* block end [id:"Slime_block_3"] */
        /* block comment [block:"Slime_block_4" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Slime_block_4" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.bodyRadius = /* param start [id:"Slime_value_21" type:"number"] */18/* param end [id:"Slime_value_21"] */;
        /* block end [id:"Slime_block_4"] */
        /* block comment [block:"Slime_block_5" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Slime_block_5" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.health = /* param start [id:"Slime_value_22" type:"number"] */140/* param end [id:"Slime_value_22"] */;
        /* block end [id:"Slime_block_5"] */
        /* block comment [block:"Slime_block_6" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Slime_block_6" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.maxHealth = /* param start [id:"Slime_value_23" type:"number"] */140/* param end [id:"Slime_value_23"] */;
        /* block end [id:"Slime_block_6"] */
        /* block comment [block:"Slime_block_7" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Slime_block_7" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.attackRange = /* param start [id:"Slime_value_24" type:"number"] */35/* param end [id:"Slime_value_24"] */;
        /* block end [id:"Slime_block_7"] */
        /* block comment [block:"Slime_block_8" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Slime_block_8" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.armourClass = /* param start [id:"Slime_value_25" type:"number"] */9/* param end [id:"Slime_value_25"] */;
        /* block end [id:"Slime_block_8"] */
        /* block comment [block:"Slime_block_9" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Slime_block_9" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.attackBonus = /* param start [id:"Slime_value_26" type:"number"] */1/* param end [id:"Slime_value_26"] */;
        /* block end [id:"Slime_block_9"] */
        /* block comment [block:"Slime_block_10" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Slime_block_10" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.damageDie = /* param start [id:"Slime_value_27" type:"number"] */8/* param end [id:"Slime_value_27"] */;
        /* block end [id:"Slime_block_10"] */
        /* block comment [block:"Slime_block_11" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Slime_block_11" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.damageMod = /* param start [id:"Slime_value_28" type:"number"] */0/* param end [id:"Slime_value_28"] */;
        /* block end [id:"Slime_block_11"] */
        /* block comment [block:"Slime_block_12" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Slime_block_12" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.dexSaveBonus = -/* param start [id:"Slime_value_29" type:"number"] */1/* param end [id:"Slime_value_29"] */;
        /* block end [id:"Slime_block_12"] */
        /* block comment [block:"Slime_block_13" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Slime_block_13" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.conSaveBonus = /* param start [id:"Slime_value_30" type:"number"] */3/* param end [id:"Slime_value_30"] */;
        /* block end [id:"Slime_block_13"] */
        /* block comment [block:"Slime_block_14" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Slime_block_14" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.xpValue = /* param start [id:"Slime_value_31" type:"number"] */45/* param end [id:"Slime_value_31"] */;
        /* block end [id:"Slime_block_14"] */
        /* block comment [block:"Slime_block_15" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Slime_block_15" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.visionRange = /* param start [id:"Slime_value_32" type:"number"] */250/* param end [id:"Slime_value_32"] */;
        /* block end [id:"Slime_block_15"] */

        /* block comment [block:"Slime_block_16" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"Slime_block_16" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(ResourcePool.getCostume(/* param start [id:"Slime_value_33" type:"text"] */"Slime"/* param end [id:"Slime_value_33"] */, /* param start [id:"Slime_value_34" type:"text"] */"jscratch_ws/assets/slime_costume1.png"/* param end [id:"Slime_value_34"] */, /* param start [id:"Slime_value_35" type:"number"] */29/* param end [id:"Slime_value_35"] */, /* param start [id:"Slime_value_36" type:"number"] */27/* param end [id:"Slime_value_36"] */));
        /* block end [id:"Slime_block_16"] */
        /* block comment [block:"Slime_block_17" text:"Runs the switch costume action. Values in this block are editable inputs."] */
        /* block start [id:"Slime_block_17" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
        this.switchCostume(/* param start [id:"Slime_value_37" type:"text"] */"Slime"/* param end [id:"Slime_value_37"] */);
        /* block end [id:"Slime_block_17"] */
    }

    // Mindless tank: too dumb to kite, holds its ground and soaks hits.
    @Override
    protected boolean canRetreat() { /* block comment [block:"Slime_block_18" text:"Returns a value or ends the current method."] */
        /* block start [id:"Slime_block_18" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return /* param start [id:"Slime_value_38" type:"boolean"] */false/* param end [id:"Slime_value_38"] */;
        /* block end [id:"Slime_block_18"] */ }
}
