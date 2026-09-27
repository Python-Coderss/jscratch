package generated;

import com.jscratch.*;

/**
* Minitaur: floor-7 boss. Telegraphs with a prep pose, then rushes.
* Assets: idle 139x181, prep 147x181, attak1/2 139x181.
* Role: disciplined boss — never kites/orbits; enrages below 50% HP
* (faster, shorter cooldown/swing via think→applyPhase) and charges from
* mid range (150-420px burst on a 5s cooldown via pursue override).
* Spawned scripted by FloorManager; stairs stay sealed while it lives.
*/
public class Minitaur extends EnemySprite {
    public Minitaur(String name) {
        /* block comment [block:"Minitaur_block_8" text:"Runs the super action. Values in this block are editable inputs."] */
        /* block start [id:"Minitaur_block_8" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
        super(name);
        /* block end [id:"Minitaur_block_8"] */
        /* block comment [block:"Minitaur_block_9" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Minitaur_block_9" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.localScale = /* param start [id:"Minitaur_value_59" type:"number"] */1.2/* param end [id:"Minitaur_value_59"] */;
        /* block end [id:"Minitaur_block_9"] */
        /* block comment [block:"Minitaur_block_10" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Minitaur_block_10" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.speed = /* param start [id:"Minitaur_value_60" type:"number"] */2/* param end [id:"Minitaur_value_60"] */;
        /* block end [id:"Minitaur_block_10"] */
        /* block comment [block:"Minitaur_block_11" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Minitaur_block_11" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.bodyRadius = /* param start [id:"Minitaur_value_61" type:"number"] */34/* param end [id:"Minitaur_value_61"] */;
        /* block end [id:"Minitaur_block_11"] */
        /* block comment [block:"Minitaur_block_12" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Minitaur_block_12" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.health = /* param start [id:"Minitaur_value_62" type:"number"] */220/* param end [id:"Minitaur_value_62"] */;
        /* block end [id:"Minitaur_block_12"] */
        /* block comment [block:"Minitaur_block_13" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Minitaur_block_13" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.maxHealth = /* param start [id:"Minitaur_value_63" type:"number"] */220/* param end [id:"Minitaur_value_63"] */;
        /* block end [id:"Minitaur_block_13"] */
        /* block comment [block:"Minitaur_block_14" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Minitaur_block_14" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.attackRange = /* param start [id:"Minitaur_value_64" type:"number"] */50/* param end [id:"Minitaur_value_64"] */;
        /* block end [id:"Minitaur_block_14"] */
        /* block comment [block:"Minitaur_block_15" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Minitaur_block_15" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.armourClass = /* param start [id:"Minitaur_value_65" type:"number"] */14/* param end [id:"Minitaur_value_65"] */;
        /* block end [id:"Minitaur_block_15"] */
        /* block comment [block:"Minitaur_block_16" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Minitaur_block_16" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.attackBonus = /* param start [id:"Minitaur_value_66" type:"number"] */5/* param end [id:"Minitaur_value_66"] */;
        /* block end [id:"Minitaur_block_16"] */
        /* block comment [block:"Minitaur_block_17" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Minitaur_block_17" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.damageDie = /* param start [id:"Minitaur_value_67" type:"number"] */10/* param end [id:"Minitaur_value_67"] */;
        /* block end [id:"Minitaur_block_17"] */
        /* block comment [block:"Minitaur_block_18" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Minitaur_block_18" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.damageMod = /* param start [id:"Minitaur_value_68" type:"number"] */2/* param end [id:"Minitaur_value_68"] */;
        /* block end [id:"Minitaur_block_18"] */
        /* block comment [block:"Minitaur_block_19" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Minitaur_block_19" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.dexSaveBonus = /* param start [id:"Minitaur_value_69" type:"number"] */0/* param end [id:"Minitaur_value_69"] */;
        /* block end [id:"Minitaur_block_19"] */
        /* block comment [block:"Minitaur_block_20" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Minitaur_block_20" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.conSaveBonus = /* param start [id:"Minitaur_value_70" type:"number"] */3/* param end [id:"Minitaur_value_70"] */;
        /* block end [id:"Minitaur_block_20"] */
        /* block comment [block:"Minitaur_block_21" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Minitaur_block_21" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.xpValue = /* param start [id:"Minitaur_value_71" type:"number"] */300/* param end [id:"Minitaur_value_71"] */;
        /* block end [id:"Minitaur_block_21"] */
        /* block comment [block:"Minitaur_block_22" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Minitaur_block_22" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.attackCooldownMs = /* param start [id:"Minitaur_value_72" type:"number"] */1800/* param end [id:"Minitaur_value_72"] */;
        /* block end [id:"Minitaur_block_22"] */
        /* block comment [block:"Minitaur_block_23" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Minitaur_block_23" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.windupMs = /* param start [id:"Minitaur_value_73" type:"number"] */450/* param end [id:"Minitaur_value_73"] */;
        /* block end [id:"Minitaur_block_23"] */ // slow, readable boss swing
        /* block comment [block:"Minitaur_block_24" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Minitaur_block_24" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.visionRange = /* param start [id:"Minitaur_value_74" type:"number"] */650/* param end [id:"Minitaur_value_74"] */;
        /* block end [id:"Minitaur_block_24"] */

        /* block comment [block:"Minitaur_block_25" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"Minitaur_block_25" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(ResourcePool.getCostume(/* param start [id:"Minitaur_value_75" type:"text"] */"Mini_idle"/* param end [id:"Minitaur_value_75"] */, /* param start [id:"Minitaur_value_76" type:"text"] */"jscratch_ws/assets/minitaur_idle.png"/* param end [id:"Minitaur_value_76"] */, /* param start [id:"Minitaur_value_77" type:"number"] */69/* param end [id:"Minitaur_value_77"] */, /* param start [id:"Minitaur_value_78" type:"number"] */90/* param end [id:"Minitaur_value_78"] */));
        /* block end [id:"Minitaur_block_25"] */
        /* block comment [block:"Minitaur_block_26" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"Minitaur_block_26" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(ResourcePool.getCostume(/* param start [id:"Minitaur_value_79" type:"text"] */"Mini_prep"/* param end [id:"Minitaur_value_79"] */, /* param start [id:"Minitaur_value_80" type:"text"] */"jscratch_ws/assets/minitaur_attak_prep.png"/* param end [id:"Minitaur_value_80"] */, /* param start [id:"Minitaur_value_81" type:"number"] */73/* param end [id:"Minitaur_value_81"] */, /* param start [id:"Minitaur_value_82" type:"number"] */90/* param end [id:"Minitaur_value_82"] */));
        /* block end [id:"Minitaur_block_26"] */
        /* block comment [block:"Minitaur_block_27" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"Minitaur_block_27" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(ResourcePool.getCostume(/* param start [id:"Minitaur_value_83" type:"text"] */"Mini_a1"/* param end [id:"Minitaur_value_83"] */, /* param start [id:"Minitaur_value_84" type:"text"] */"jscratch_ws/assets/minitaur_attak1.png"/* param end [id:"Minitaur_value_84"] */, /* param start [id:"Minitaur_value_85" type:"number"] */69/* param end [id:"Minitaur_value_85"] */, /* param start [id:"Minitaur_value_86" type:"number"] */90/* param end [id:"Minitaur_value_86"] */));
        /* block end [id:"Minitaur_block_27"] */
        /* block comment [block:"Minitaur_block_28" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"Minitaur_block_28" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(ResourcePool.getCostume(/* param start [id:"Minitaur_value_87" type:"text"] */"Mini_a2"/* param end [id:"Minitaur_value_87"] */, /* param start [id:"Minitaur_value_88" type:"text"] */"jscratch_ws/assets/minitaur_attak2.png"/* param end [id:"Minitaur_value_88"] */, /* param start [id:"Minitaur_value_89" type:"number"] */69/* param end [id:"Minitaur_value_89"] */, /* param start [id:"Minitaur_value_90" type:"number"] */90/* param end [id:"Minitaur_value_90"] */));
        /* block end [id:"Minitaur_block_28"] */
        /* block comment [block:"Minitaur_block_29" text:"Runs the switch costume action. Values in this block are editable inputs."] */
        /* block start [id:"Minitaur_block_29" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
        this.switchCostume(/* param start [id:"Minitaur_value_91" type:"text"] */"Mini_idle"/* param end [id:"Minitaur_value_91"] */);
        /* block end [id:"Minitaur_block_29"] */
    }

    // Boss discipline: never kites, never orbits. It advances and kills.
    @Override
    protected boolean canRetreat() { /* block comment [block:"Minitaur_block_30" text:"Returns a value or ends the current method."] */
        /* block start [id:"Minitaur_block_30" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return /* param start [id:"Minitaur_value_92" type:"boolean"] */false/* param end [id:"Minitaur_value_92"] */;
        /* block end [id:"Minitaur_block_30"] */ }

    @Override
    protected boolean canSwarm() { /* block comment [block:"Minitaur_block_31" text:"Returns a value or ends the current method."] */
        /* block start [id:"Minitaur_block_31" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return /* param start [id:"Minitaur_value_93" type:"boolean"] */false/* param end [id:"Minitaur_value_93"] */;
        /* block end [id:"Minitaur_block_31"] */ }

    private long chargeUntil = 0;
    private long chargeCdUntil = 0;

    /** Enrage below half HP: faster, shorter cooldown, quicker swing. */
    private void applyPhase(long now) {
        /* block comment [block:"Minitaur_block_32" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Minitaur_block_32" category:"Variables" label:"set enraged" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        boolean enraged = maxHealth > /* param start [id:"Minitaur_value_94" type:"number"] */0/* param end [id:"Minitaur_value_94"] */ && health * /* param start [id:"Minitaur_value_95" type:"number"] */2/* param end [id:"Minitaur_value_95"] */ < maxHealth;
        /* block end [id:"Minitaur_block_32"] */
        /* block comment [block:"Minitaur_block_1" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Minitaur_block_1" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (enraged) {
            /* block comment [block:"Minitaur_block_33" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Minitaur_block_33" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            speed = /* param start [id:"Minitaur_value_96" type:"number"] */4/* param end [id:"Minitaur_value_96"] */;
            /* block end [id:"Minitaur_block_33"] */
            /* block comment [block:"Minitaur_block_34" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Minitaur_block_34" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            attackCooldownMs = /* param start [id:"Minitaur_value_97" type:"number"] */1200/* param end [id:"Minitaur_value_97"] */;
            /* block end [id:"Minitaur_block_34"] */
            /* block comment [block:"Minitaur_block_35" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Minitaur_block_35" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            windupMs = /* param start [id:"Minitaur_value_98" type:"number"] */300/* param end [id:"Minitaur_value_98"] */;
            /* block end [id:"Minitaur_block_35"] */
        } else {
            /* block comment [block:"Minitaur_block_36" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Minitaur_block_36" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            speed = /* param start [id:"Minitaur_value_99" type:"number"] */2/* param end [id:"Minitaur_value_99"] */;
            /* block end [id:"Minitaur_block_36"] */
            /* block comment [block:"Minitaur_block_37" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Minitaur_block_37" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            attackCooldownMs = /* param start [id:"Minitaur_value_100" type:"number"] */1800/* param end [id:"Minitaur_value_100"] */;
            /* block end [id:"Minitaur_block_37"] */
            /* block comment [block:"Minitaur_block_38" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Minitaur_block_38" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            windupMs = /* param start [id:"Minitaur_value_101" type:"number"] */450/* param end [id:"Minitaur_value_101"] */;
            /* block end [id:"Minitaur_block_38"] */
        }
        /* block end [id:"Minitaur_block_1"] */
    }

    @Override
    public void think(int playerX, int playerY) {
        /* block comment [block:"Minitaur_block_39" text:"Calls the applyPhase method using its declared arguments."] */
        /* block start [id:"Minitaur_block_39" category:"My Blocks" label:"call apply phase" shape:"command" docs:"Calls the applyPhase method using its declared arguments."] */
        applyPhase(System.currentTimeMillis());
        /* block end [id:"Minitaur_block_39"] */
        /* block comment [block:"Minitaur_block_40" text:"Runs the think action. Values in this block are editable inputs."] */
        /* block start [id:"Minitaur_block_40" category:"My Blocks" label:"think" shape:"command" docs:"Runs the think action. Values in this block are editable inputs."] */
        super.think(playerX, playerY);
        /* block end [id:"Minitaur_block_40"] */
    }

    /** Mid-range charge: telegraphed burst that closes distance fast. */
    @Override
    protected void pursue(int tx, int ty) {
        /* block comment [block:"Minitaur_block_41" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Minitaur_block_41" category:"Variables" label:"set now" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        long now = System.currentTimeMillis();
        /* block end [id:"Minitaur_block_41"] */
        /* block comment [block:"Minitaur_block_42" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Minitaur_block_42" category:"Variables" label:"set dx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double dx = tx - xx;
        /* block end [id:"Minitaur_block_42"] */
        /* block comment [block:"Minitaur_block_43" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Minitaur_block_43" category:"Variables" label:"set dy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double dy = ty - yy;
        /* block end [id:"Minitaur_block_43"] */
        /* block comment [block:"Minitaur_block_44" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Minitaur_block_44" category:"Variables" label:"set d2" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double d2 = dx * dx + dy * dy;
        /* block end [id:"Minitaur_block_44"] */
        /* block comment [block:"Minitaur_block_2" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Minitaur_block_2" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (now < chargeUntil) {
            /* block comment [block:"Minitaur_block_45" text:"Runs the move towards action. Values in this block are editable inputs."] */
            /* block start [id:"Minitaur_block_45" category:"Motion" label:"move towards" shape:"command" docs:"Runs the move towards action. Values in this block are editable inputs."] */
            moveTowards(tx, ty, speed * /* param start [id:"Minitaur_value_102" type:"number"] */4/* param end [id:"Minitaur_value_102"] */);
            /* block end [id:"Minitaur_block_45"] */
            /* block comment [block:"Minitaur_block_46" text:"Returns a value or ends the current method."] */
            /* block start [id:"Minitaur_block_46" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"Minitaur_block_46"] */
        }
        /* block end [id:"Minitaur_block_2"] */
        /* block comment [block:"Minitaur_block_3" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Minitaur_block_3" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (now >= chargeCdUntil && d2 > /* param start [id:"Minitaur_value_103" type:"number"] */150/* param end [id:"Minitaur_value_103"] */ * /* param start [id:"Minitaur_value_104" type:"number"] */150/* param end [id:"Minitaur_value_104"] */ && d2 < /* param start [id:"Minitaur_value_105" type:"number"] */420/* param end [id:"Minitaur_value_105"] */ * /* param start [id:"Minitaur_value_106" type:"number"] */420/* param end [id:"Minitaur_value_106"] */) {
            /* block comment [block:"Minitaur_block_47" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Minitaur_block_47" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            chargeUntil = now + /* param start [id:"Minitaur_value_107" type:"number"] */600/* param end [id:"Minitaur_value_107"] */;
            /* block end [id:"Minitaur_block_47"] */
            /* block comment [block:"Minitaur_block_48" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Minitaur_block_48" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            chargeCdUntil = now + /* param start [id:"Minitaur_value_108" type:"number"] */5000/* param end [id:"Minitaur_value_108"] */;
            /* block end [id:"Minitaur_block_48"] */
            /* block comment [block:"Minitaur_block_49" text:"Runs the switch costume action. Values in this block are editable inputs."] */
            /* block start [id:"Minitaur_block_49" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
            switchCostume(/* param start [id:"Minitaur_value_109" type:"text"] */"Mini_prep"/* param end [id:"Minitaur_value_109"] */);
            /* block end [id:"Minitaur_block_49"] */
            /* block comment [block:"Minitaur_block_50" text:"Runs the move towards action. Values in this block are editable inputs."] */
            /* block start [id:"Minitaur_block_50" category:"Motion" label:"move towards" shape:"command" docs:"Runs the move towards action. Values in this block are editable inputs."] */
            moveTowards(tx, ty, speed * /* param start [id:"Minitaur_value_110" type:"number"] */4/* param end [id:"Minitaur_value_110"] */);
            /* block end [id:"Minitaur_block_50"] */
            /* block comment [block:"Minitaur_block_51" text:"Returns a value or ends the current method."] */
            /* block start [id:"Minitaur_block_51" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"Minitaur_block_51"] */
        }
        /* block end [id:"Minitaur_block_3"] */
        /* block comment [block:"Minitaur_block_52" text:"Runs the pursue action. Values in this block are editable inputs."] */
        /* block start [id:"Minitaur_block_52" category:"My Blocks" label:"pursue" shape:"command" docs:"Runs the pursue action. Values in this block are editable inputs."] */
        super.pursue(tx, ty);
        /* block end [id:"Minitaur_block_52"] */
    }

    @Override
    protected void attackTelegraph(int tx, int ty) {
        /* block comment [block:"Minitaur_block_53" text:"Runs the switch costume action. Values in this block are editable inputs."] */
        /* block start [id:"Minitaur_block_53" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
        switchCostume(/* param start [id:"Minitaur_value_111" type:"text"] */"Mini_prep"/* param end [id:"Minitaur_value_111"] */);
        /* block end [id:"Minitaur_block_53"] */ // telegraph
        /* block comment [block:"Minitaur_block_54" text:"Runs the move towards action. Values in this block are editable inputs."] */
        /* block start [id:"Minitaur_block_54" category:"Motion" label:"move towards" shape:"command" docs:"Runs the move towards action. Values in this block are editable inputs."] */
        moveTowards(tx, ty, speed * /* param start [id:"Minitaur_value_112" type:"number"] */2/* param end [id:"Minitaur_value_112"] */);
        /* block end [id:"Minitaur_block_54"] */
    }

    @Override
    public void update() {
        /* block comment [block:"Minitaur_block_55" text:"Runs the update action. Values in this block are editable inputs."] */
        /* block start [id:"Minitaur_block_55" category:"My Blocks" label:"update" shape:"command" docs:"Runs the update action. Values in this block are editable inputs."] */
        super.update();
        /* block end [id:"Minitaur_block_55"] */
        /* block comment [block:"Minitaur_block_4" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Minitaur_block_4" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (currentState == AIState.ATTACK) {
            /* block comment [block:"Minitaur_block_56" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"Minitaur_block_56" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"Minitaur_block_5" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Minitaur_block_5" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (System.currentTimeMillis() % /* param start [id:"Minitaur_value_113" type:"number"] */400/* param end [id:"Minitaur_value_113"] */ < /* param start [id:"Minitaur_value_114" type:"number"] */200/* param end [id:"Minitaur_value_114"] */) switchCostume(/* param start [id:"Minitaur_value_115" type:"text"] */"Mini_a1"/* param end [id:"Minitaur_value_115"] */);
            /* block end [id:"Minitaur_block_56"] */
            /* block comment [block:"Minitaur_block_57" text:"Runs the switch costume action. Values in this block are editable inputs."] */
            /* block start [id:"Minitaur_block_57" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
            else switchCostume(/* param start [id:"Minitaur_value_116" type:"text"] */"Mini_a2"/* param end [id:"Minitaur_value_116"] */);
            /* block end [id:"Minitaur_block_57"] */
            /* block end [id:"Minitaur_block_5"] */
        } else /* block comment [block:"Minitaur_block_6" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Minitaur_block_6" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (currentState != AIState.ATTACK) {
            // performAttack sets prep; don't stomp it mid-swing
            /* block comment [block:"Minitaur_block_58" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"Minitaur_block_58" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"Minitaur_block_7" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Minitaur_block_7" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (System.currentTimeMillis() - lastAttackMs > /* param start [id:"Minitaur_value_117" type:"number"] */600/* param end [id:"Minitaur_value_117"] */) switchCostume(/* param start [id:"Minitaur_value_118" type:"text"] */"Mini_idle"/* param end [id:"Minitaur_value_118"] */);
            /* block end [id:"Minitaur_block_58"] */
            /* block end [id:"Minitaur_block_7"] */
        }
        /* block end [id:"Minitaur_block_6"] */
        /* block end [id:"Minitaur_block_4"] */
    }
}
