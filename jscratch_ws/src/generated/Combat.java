package generated;

import java.util.Random;

/**
* Shared hybrid-combat dice.
* Real-time engine, D&D math underneath: d20 + bonus vs AC,
* nat 20 crits (double dice), nat 1 always misses.
*
* <p>Call graph: pure static helpers called by {@code Sprite1.performAttack},
* {@code EnemySprite.landStrike}, {@code Spellbook} (bolt/slash/bomb/poison),
* {@code EnemySprite.die} (gold) and {@code Chest} (gold). No state, no
* callers beyond the dice methods below. Java 8 compatible.
*/
public class Combat {
    private static final Random dice = new Random();

    public static int d20() {
        /* block comment [block:"Combat_block_5" text:"Returns a value or ends the current method."] */
        /* block start [id:"Combat_block_5" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return dice.nextInt(/* param start [id:"Combat_value_16" type:"number"] */20/* param end [id:"Combat_value_16"] */) + /* param start [id:"Combat_value_17" type:"number"] */1/* param end [id:"Combat_value_17"] */;
        /* block end [id:"Combat_block_5"] */
    }

    public static int rollDie(int sides) {
        /* block comment [block:"Combat_block_6" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Combat_block_6" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Combat_block_1" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Combat_block_1" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (sides <= /* param start [id:"Combat_value_18" type:"number"] */1/* param end [id:"Combat_value_18"] */) return /* param start [id:"Combat_value_19" type:"number"] */1/* param end [id:"Combat_value_19"] */;
        /* block end [id:"Combat_block_6"] */
        /* block end [id:"Combat_block_1"] */
        /* block comment [block:"Combat_block_7" text:"Returns a value or ends the current method."] */
        /* block start [id:"Combat_block_7" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return dice.nextInt(sides) + /* param start [id:"Combat_value_20" type:"number"] */1/* param end [id:"Combat_value_20"] */;
        /* block end [id:"Combat_block_7"] */
    }

    public static int roll(int n, int sides) {
        /* block comment [block:"Combat_block_8" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Combat_block_8" category:"Variables" label:"set total" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int total = /* param start [id:"Combat_value_21" type:"number"] */0/* param end [id:"Combat_value_21"] */;
        /* block end [id:"Combat_block_8"] */
        /* block comment [block:"Combat_block_2" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"Combat_block_2" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (int i = /* param start [id:"Combat_value_22" type:"number"] */0/* param end [id:"Combat_value_22"] */; i < n; i++) total += rollDie(sides);
        /* block end [id:"Combat_block_2"] */
        /* block comment [block:"Combat_block_9" text:"Returns a value or ends the current method."] */
        /* block start [id:"Combat_block_9" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return total;
        /* block end [id:"Combat_block_9"] */
    }

    /**
    * @return 0 = miss, 1 = hit, 2 = crit.
    */
    public static int meleeStrike(int attackBonus, int targetAC) {
        /* block comment [block:"Combat_block_10" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Combat_block_10" category:"Variables" label:"set r" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int r = d20();
        /* block end [id:"Combat_block_10"] */
        /* block comment [block:"Combat_block_11" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Combat_block_11" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Combat_block_3" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Combat_block_3" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (r == /* param start [id:"Combat_value_23" type:"number"] */20/* param end [id:"Combat_value_23"] */) return /* param start [id:"Combat_value_24" type:"number"] */2/* param end [id:"Combat_value_24"] */;
        /* block end [id:"Combat_block_11"] */
        /* block end [id:"Combat_block_3"] */
        /* block comment [block:"Combat_block_12" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Combat_block_12" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Combat_block_4" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Combat_block_4" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (r == /* param start [id:"Combat_value_25" type:"number"] */1/* param end [id:"Combat_value_25"] */) return /* param start [id:"Combat_value_26" type:"number"] */0/* param end [id:"Combat_value_26"] */;
        /* block end [id:"Combat_block_12"] */
        /* block end [id:"Combat_block_4"] */
        /* block comment [block:"Combat_block_13" text:"Returns a value or ends the current method."] */
        /* block start [id:"Combat_block_13" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return (r + attackBonus >= targetAC) ? /* param start [id:"Combat_value_27" type:"number"] */1/* param end [id:"Combat_value_27"] */ : /* param start [id:"Combat_value_28" type:"number"] */0/* param end [id:"Combat_value_28"] */;
        /* block end [id:"Combat_block_13"] */
    }

    /** Damage = dice + mod, minimum 1. Crits roll double dice. */
    public static int rollDamage(int die, int mod, boolean crit) {
        /* block comment [block:"Combat_block_14" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Combat_block_14" category:"Variables" label:"set dmg" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int dmg = crit ? roll(/* param start [id:"Combat_value_29" type:"number"] */2/* param end [id:"Combat_value_29"] */, die) + mod : rollDie(die) + mod;
        /* block end [id:"Combat_block_14"] */
        /* block comment [block:"Combat_block_15" text:"Returns a value or ends the current method."] */
        /* block start [id:"Combat_block_15" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return Math.max(/* param start [id:"Combat_value_30" type:"number"] */1/* param end [id:"Combat_value_30"] */, dmg);
        /* block end [id:"Combat_block_15"] */
    }
}
