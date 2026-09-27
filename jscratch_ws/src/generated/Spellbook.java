package generated;

import com.jscratch.Costume;
import com.jscratch.Stage;

import java.util.ArrayList;
import java.util.List;

/**
* Spell kit hub (keys 1-6). All spells are real-time with D&D math underneath:
* d20 + bonus vs AC for attacks, DEX/CON saves vs spell DC for half damage.
*
* <p>Call graph / interactions:
* <ul>
*   <li>Cast entry points ({@code castSlash/Bomb/Bolt/Heal/Poison/Summon})
*       are called edge-triggered from the {@code Sprite1} loop; each gates
*       on cooldown + {@code PlayerStats} mana, then
*       {@code Stage.addSprite}s its effect.</li>
*   <li>Effects: {@code SpellProjectile.fly} (own daemon script thread,
*       wall stop via Scroller2d, hit test vs EnemyManager list) →
*       {@code onHit/onExpire}; {@code Explosion/PoisonCloud} apply AoE via
*       {@code Combat} + saves; {@code SummonedImp} hunts
*       {@code nearestEnemy} on its own thread and is itself targeted back
*       through {@code EnemySprite.acquireTarget}.</li>
*   <li>All one-shot effects end in {@code destroy()} (unregister from
*       Scroller2d) so the sprite registry never grows.</li>
* </ul>
*
*  1 Slash of Nails (maneuver, no mana)  -> SlashOfNails.java
*  2 Bomb Toss -> Boom (AoE, DEX half)   bomb.png 380x116 / boom.png 612x561
*  3 Magic Bolt (single target)          Sprite4 188x176
*  4 Heal (2d6 + spell mod)              heart.png 7x7
*  5 Poison Cloud (DoT zone, CON half)   Sprite7 blobs 153/110/116px
*  6 Summon Imp (45s ally)               imp 136x116, cast flash Sprite2 105x138
*
* Sounds: export (1).wav (boom), Sprite6_pop.wav (bolt/heal), gear pop (summon).
* Java 8 compatible.
*/
public class Spellbook {

    public static final int MANA_BOMB = 4;
    public static final int MANA_BOLT = 2;
    public static final int MANA_HEAL = 6;
    public static final int MANA_POISON = 8;
    public static final int MANA_SUMMON = 10;

    public static final long CD_SLASH = 3000;
    public static final long CD_BOMB = 2500;
    public static final long CD_BOLT = 800;
    public static final long CD_HEAL = 8000;
    public static final long CD_POISON = 12000;
    public static final long CD_SUMMON = 30000;

    private static long cdSlash, cdBomb, cdBolt, cdHeal, cdPoison, cdSummon;

    /** Live summoned imps (enemies may target them). COW: iterated on the
    *  player thread, mutated from imp lifetimes on their own threads. */
    public static final List<SummonedImp> liveImps = new java.util.concurrent.CopyOnWriteArrayList<>();

    /** Remove summons from the previous run so their background loops stop. */
    public static void clearSummons() {
        /* block comment [block:"Spellbook_block_1" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"Spellbook_block_1" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (SummonedImp imp : liveImps) {
            /* block comment [block:"Spellbook_block_57" text:"Calls the destroy method using its declared arguments."] */
            /* block start [id:"Spellbook_block_57" category:"My Blocks" label:"call destroy" shape:"command" docs:"Calls the destroy method using its declared arguments."] */
            imp.destroy();
            /* block end [id:"Spellbook_block_57"] */
        }
        /* block end [id:"Spellbook_block_1"] */
        /* block comment [block:"Spellbook_block_58" text:"Calls the clear method using its declared arguments."] */
        /* block start [id:"Spellbook_block_58" category:"My Blocks" label:"call clear" shape:"command" docs:"Calls the clear method using its declared arguments."] */
        liveImps.clear();
        /* block end [id:"Spellbook_block_58"] */
    }

    /** Nearest visible imp within maxDist, or null. */
    public static SummonedImp nearestImp(int x, int y, int maxDist) {
        /* block comment [block:"Spellbook_block_59" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_59" category:"Variables" label:"set best" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        SummonedImp best = /* param start [id:"Spellbook_value_231" type:"value"] */null/* param end [id:"Spellbook_value_231"] */;
        /* block end [id:"Spellbook_block_59"] */
        /* block comment [block:"Spellbook_block_60" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_60" category:"Variables" label:"set bestD2" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double bestD2 = (double) maxDist * maxDist;
        /* block end [id:"Spellbook_block_60"] */
        /* block comment [block:"Spellbook_block_2" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"Spellbook_block_2" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (int i = liveImps.size() - /* param start [id:"Spellbook_value_232" type:"number"] */1/* param end [id:"Spellbook_value_232"] */; i >= /* param start [id:"Spellbook_value_233" type:"number"] */0/* param end [id:"Spellbook_value_233"] */; i--) {
            /* block comment [block:"Spellbook_block_61" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Spellbook_block_61" category:"Variables" label:"set imp" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            SummonedImp imp = liveImps.get(i);
            /* block end [id:"Spellbook_block_61"] */
            /* block comment [block:"Spellbook_block_3" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Spellbook_block_3" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (!imp.isVisible()) {
                /* block comment [block:"Spellbook_block_62" text:"Runs the remove action. Values in this block are editable inputs."] */
                /* block start [id:"Spellbook_block_62" category:"Motion" label:"remove" shape:"command" docs:"Runs the remove action. Values in this block are editable inputs."] */
                liveImps.remove(i);
                /* block end [id:"Spellbook_block_62"] */
                /* block comment [block:"Spellbook_block_63" text:"Skips to the next iteration of the nearest loop."] */
                /* block start [id:"Spellbook_block_63" category:"Control" label:"next loop turn" shape:"command" docs:"Skips to the next iteration of the nearest loop."] */
                continue;
                /* block end [id:"Spellbook_block_63"] */
            }
            /* block end [id:"Spellbook_block_3"] */
            /* block comment [block:"Spellbook_block_64" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Spellbook_block_64" category:"Variables" label:"set dx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double dx = imp.xx - x;
            /* block end [id:"Spellbook_block_64"] */
            /* block comment [block:"Spellbook_block_65" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Spellbook_block_65" category:"Variables" label:"set dy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double dy = imp.yy - y;
            /* block end [id:"Spellbook_block_65"] */
            /* block comment [block:"Spellbook_block_66" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Spellbook_block_66" category:"Variables" label:"set d2" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double d2 = dx * dx + dy * dy;
            /* block end [id:"Spellbook_block_66"] */
            /* block comment [block:"Spellbook_block_4" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Spellbook_block_4" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (d2 < bestD2) {
                /* block comment [block:"Spellbook_block_67" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"Spellbook_block_67" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                bestD2 = d2;
                /* block end [id:"Spellbook_block_67"] */
                /* block comment [block:"Spellbook_block_68" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"Spellbook_block_68" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                best = imp;
                /* block end [id:"Spellbook_block_68"] */
            }
            /* block end [id:"Spellbook_block_4"] */
        }
        /* block end [id:"Spellbook_block_2"] */
        /* block comment [block:"Spellbook_block_69" text:"Returns a value or ends the current method."] */
        /* block start [id:"Spellbook_block_69" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return best;
        /* block end [id:"Spellbook_block_69"] */
    }

    private static void msg(String s) {
        /* block comment [block:"Spellbook_block_70" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_70" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().setVariable(/* param start [id:"Spellbook_value_234" type:"text"] */"Msg"/* param end [id:"Spellbook_value_234"] */, s);
        /* block end [id:"Spellbook_block_70"] */
    }

    private static boolean gate(long last, long cd, int manaCost) {
        /* block comment [block:"Spellbook_block_71" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_71" category:"Variables" label:"set now" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        long now = System.currentTimeMillis();
        /* block end [id:"Spellbook_block_71"] */
        /* block comment [block:"Spellbook_block_72" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Spellbook_block_72" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Spellbook_block_5" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Spellbook_block_5" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (now - last < cd) return /* param start [id:"Spellbook_value_235" type:"boolean"] */false/* param end [id:"Spellbook_value_235"] */;
        /* block end [id:"Spellbook_block_72"] */
        /* block end [id:"Spellbook_block_5"] */
        /* block comment [block:"Spellbook_block_6" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Spellbook_block_6" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (PlayerStats.mana < manaCost) {
            /* block comment [block:"Spellbook_block_73" text:"Calls the msg method using its declared arguments."] */
            /* block start [id:"Spellbook_block_73" category:"My Blocks" label:"call msg" shape:"command" docs:"Calls the msg method using its declared arguments."] */
            msg(/* param start [id:"Spellbook_value_236" type:"text"] */"Not enough mana ("/* param end [id:"Spellbook_value_236"] */ + PlayerStats.mana + /* param start [id:"Spellbook_value_237" type:"text"] */"/"/* param end [id:"Spellbook_value_237"] */ + manaCost + /* param start [id:"Spellbook_value_238" type:"text"] */")"/* param end [id:"Spellbook_value_238"] */);
            /* block end [id:"Spellbook_block_73"] */
            /* block comment [block:"Spellbook_block_74" text:"Returns a value or ends the current method."] */
            /* block start [id:"Spellbook_block_74" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return /* param start [id:"Spellbook_value_239" type:"boolean"] */false/* param end [id:"Spellbook_value_239"] */;
            /* block end [id:"Spellbook_block_74"] */
        }
        /* block end [id:"Spellbook_block_6"] */
        /* block comment [block:"Spellbook_block_75" text:"Returns a value or ends the current method."] */
        /* block start [id:"Spellbook_block_75" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return /* param start [id:"Spellbook_value_240" type:"boolean"] */true/* param end [id:"Spellbook_value_240"] */;
        /* block end [id:"Spellbook_block_75"] */
    }

    // --- 1: Slash (maneuver, cooldown only) ---
    public static void castSlash(int x, int y, double dirDeg) {
        /* block comment [block:"Spellbook_block_76" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_76" category:"Variables" label:"set now" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        long now = System.currentTimeMillis();
        /* block end [id:"Spellbook_block_76"] */
        /* block comment [block:"Spellbook_block_77" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Spellbook_block_77" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Spellbook_block_7" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Spellbook_block_7" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (now - cdSlash < CD_SLASH) return;
        /* block end [id:"Spellbook_block_77"] */
        /* block end [id:"Spellbook_block_7"] */
        /* block comment [block:"Spellbook_block_78" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Spellbook_block_78" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        cdSlash = now;
        /* block end [id:"Spellbook_block_78"] */
        /* block comment [block:"Spellbook_block_79" text:"Calls the msg method using its declared arguments."] */
        /* block start [id:"Spellbook_block_79" category:"My Blocks" label:"call msg" shape:"command" docs:"Calls the msg method using its declared arguments."] */
        msg(/* param start [id:"Spellbook_value_241" type:"text"] */""/* param end [id:"Spellbook_value_241"] */);
        /* block end [id:"Spellbook_block_79"] */
        /* block comment [block:"Spellbook_block_80" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_80" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().addSprite(new SlashOfNails(x, y, dirDeg));
        /* block end [id:"Spellbook_block_80"] */
    }

    // --- 2: Bomb Toss -> Boom ---
    public static void castBomb(int x, int y, double dirDeg) {
        /* block comment [block:"Spellbook_block_81" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Spellbook_block_81" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Spellbook_block_8" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Spellbook_block_8" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!gate(cdBomb, CD_BOMB, MANA_BOMB)) return;
        /* block end [id:"Spellbook_block_81"] */
        /* block end [id:"Spellbook_block_8"] */
        /* block comment [block:"Spellbook_block_82" text:"Runs the current time millis action. Values in this block are editable inputs."] */
        /* block start [id:"Spellbook_block_82" category:"My Blocks" label:"current time millis" shape:"command" docs:"Runs the current time millis action. Values in this block are editable inputs."] */
        cdBomb = System.currentTimeMillis();
        /* block end [id:"Spellbook_block_82"] */
        /* block comment [block:"Spellbook_block_83" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_83" category:"Variables" label:"set spendMana" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PlayerStats.spendMana(MANA_BOMB);
        /* block end [id:"Spellbook_block_83"] */
        /* block comment [block:"Spellbook_block_84" text:"Calls the msg method using its declared arguments."] */
        /* block start [id:"Spellbook_block_84" category:"My Blocks" label:"call msg" shape:"command" docs:"Calls the msg method using its declared arguments."] */
        msg(/* param start [id:"Spellbook_value_242" type:"text"] */""/* param end [id:"Spellbook_value_242"] */);
        /* block end [id:"Spellbook_block_84"] */
        /* block comment [block:"Spellbook_block_85" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_85" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().addSprite(new BombProjectile(x, y, dirDeg));
        /* block end [id:"Spellbook_block_85"] */
    }

    // --- 3: Magic Bolt ---
    public static void castBolt(int x, int y, double dirDeg) {
        /* block comment [block:"Spellbook_block_86" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Spellbook_block_86" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Spellbook_block_9" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Spellbook_block_9" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!gate(cdBolt, CD_BOLT, MANA_BOLT)) return;
        /* block end [id:"Spellbook_block_86"] */
        /* block end [id:"Spellbook_block_9"] */
        /* block comment [block:"Spellbook_block_87" text:"Runs the current time millis action. Values in this block are editable inputs."] */
        /* block start [id:"Spellbook_block_87" category:"My Blocks" label:"current time millis" shape:"command" docs:"Runs the current time millis action. Values in this block are editable inputs."] */
        cdBolt = System.currentTimeMillis();
        /* block end [id:"Spellbook_block_87"] */
        /* block comment [block:"Spellbook_block_88" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_88" category:"Variables" label:"set spendMana" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PlayerStats.spendMana(MANA_BOLT);
        /* block end [id:"Spellbook_block_88"] */
        /* block comment [block:"Spellbook_block_89" text:"Calls the msg method using its declared arguments."] */
        /* block start [id:"Spellbook_block_89" category:"My Blocks" label:"call msg" shape:"command" docs:"Calls the msg method using its declared arguments."] */
        msg(/* param start [id:"Spellbook_value_243" type:"text"] */""/* param end [id:"Spellbook_value_243"] */);
        /* block end [id:"Spellbook_block_89"] */
        /* block comment [block:"Spellbook_block_90" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_90" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().playSound(/* param start [id:"Spellbook_value_244" type:"text"] */"jscratch_ws/assets/Sprite6_pop.wav"/* param end [id:"Spellbook_value_244"] */);
        /* block end [id:"Spellbook_block_90"] */
        /* block comment [block:"Spellbook_block_91" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_91" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().addSprite(new MagicBolt(x, y, dirDeg));
        /* block end [id:"Spellbook_block_91"] */
    }

    // --- 4: Heal ---
    public static void castHeal(int x, int y) {
        /* block comment [block:"Spellbook_block_92" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Spellbook_block_92" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Spellbook_block_10" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Spellbook_block_10" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!gate(cdHeal, CD_HEAL, MANA_HEAL)) return;
        /* block end [id:"Spellbook_block_92"] */
        /* block end [id:"Spellbook_block_10"] */
        /* block comment [block:"Spellbook_block_11" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Spellbook_block_11" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (PlayerStats.stats != /* param start [id:"Spellbook_value_245" type:"value"] */null/* param end [id:"Spellbook_value_245"] */ && PlayerStats.stats.hp >= PlayerStats.stats.maxHp) {
            /* block comment [block:"Spellbook_block_93" text:"Calls the msg method using its declared arguments."] */
            /* block start [id:"Spellbook_block_93" category:"My Blocks" label:"call msg" shape:"command" docs:"Calls the msg method using its declared arguments."] */
            msg(/* param start [id:"Spellbook_value_246" type:"text"] */"Already at full HP"/* param end [id:"Spellbook_value_246"] */);
            /* block end [id:"Spellbook_block_93"] */
            /* block comment [block:"Spellbook_block_94" text:"Returns a value or ends the current method."] */
            /* block start [id:"Spellbook_block_94" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"Spellbook_block_94"] */
        }
        /* block end [id:"Spellbook_block_11"] */
        /* block comment [block:"Spellbook_block_95" text:"Runs the current time millis action. Values in this block are editable inputs."] */
        /* block start [id:"Spellbook_block_95" category:"My Blocks" label:"current time millis" shape:"command" docs:"Runs the current time millis action. Values in this block are editable inputs."] */
        cdHeal = System.currentTimeMillis();
        /* block end [id:"Spellbook_block_95"] */
        /* block comment [block:"Spellbook_block_96" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_96" category:"Variables" label:"set spendMana" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PlayerStats.spendMana(MANA_HEAL);
        /* block end [id:"Spellbook_block_96"] */
        /* block comment [block:"Spellbook_block_97" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_97" category:"Variables" label:"set mod" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int mod = /* param start [id:"Spellbook_value_247" type:"number"] */0/* param end [id:"Spellbook_value_247"] */;
        /* block end [id:"Spellbook_block_97"] */
        /* block comment [block:"Spellbook_block_12" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Spellbook_block_12" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (PlayerStats.ready && PlayerStats.stats != /* param start [id:"Spellbook_value_248" type:"value"] */null/* param end [id:"Spellbook_value_248"] */) {
            /* block comment [block:"Spellbook_block_98" text:"Runs the max action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_98" category:"My Blocks" label:"max" shape:"command" docs:"Runs the max action. Values in this block are editable inputs."] */
            mod = Math.max(PlayerStats.stats.wisdomModifier,
            Math.max(PlayerStats.stats.intelligenceModifier, PlayerStats.stats.charismaModifier));
            /* block end [id:"Spellbook_block_98"] */
        }
        /* block end [id:"Spellbook_block_12"] */
        /* block comment [block:"Spellbook_block_99" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_99" category:"Variables" label:"set heal" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PlayerStats.heal(Combat.roll(/* param start [id:"Spellbook_value_249" type:"number"] */2/* param end [id:"Spellbook_value_249"] */, /* param start [id:"Spellbook_value_250" type:"number"] */6/* param end [id:"Spellbook_value_250"] */) + mod);
        /* block end [id:"Spellbook_block_99"] */
        /* block comment [block:"Spellbook_block_100" text:"Calls the msg method using its declared arguments."] */
        /* block start [id:"Spellbook_block_100" category:"My Blocks" label:"call msg" shape:"command" docs:"Calls the msg method using its declared arguments."] */
        msg(/* param start [id:"Spellbook_value_251" type:"text"] */"Healed!"/* param end [id:"Spellbook_value_251"] */);
        /* block end [id:"Spellbook_block_100"] */
        /* block comment [block:"Spellbook_block_101" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_101" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().playSound(/* param start [id:"Spellbook_value_252" type:"text"] */"jscratch_ws/assets/Sprite6_pop.wav"/* param end [id:"Spellbook_value_252"] */);
        /* block end [id:"Spellbook_block_101"] */
        /* block comment [block:"Spellbook_block_102" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_102" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().addSprite(new HealFlash(x, y));
        /* block end [id:"Spellbook_block_102"] */
    }

    // --- 5: Poison Cloud (lands in front of caster) ---
    public static void castPoison(int x, int y, double dirDeg) {
        /* block comment [block:"Spellbook_block_103" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Spellbook_block_103" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Spellbook_block_13" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Spellbook_block_13" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!gate(cdPoison, CD_POISON, MANA_POISON)) return;
        /* block end [id:"Spellbook_block_103"] */
        /* block end [id:"Spellbook_block_13"] */
        /* block comment [block:"Spellbook_block_104" text:"Runs the current time millis action. Values in this block are editable inputs."] */
        /* block start [id:"Spellbook_block_104" category:"My Blocks" label:"current time millis" shape:"command" docs:"Runs the current time millis action. Values in this block are editable inputs."] */
        cdPoison = System.currentTimeMillis();
        /* block end [id:"Spellbook_block_104"] */
        /* block comment [block:"Spellbook_block_105" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_105" category:"Variables" label:"set spendMana" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PlayerStats.spendMana(MANA_POISON);
        /* block end [id:"Spellbook_block_105"] */
        /* block comment [block:"Spellbook_block_106" text:"Calls the msg method using its declared arguments."] */
        /* block start [id:"Spellbook_block_106" category:"My Blocks" label:"call msg" shape:"command" docs:"Calls the msg method using its declared arguments."] */
        msg(/* param start [id:"Spellbook_value_253" type:"text"] */""/* param end [id:"Spellbook_value_253"] */);
        /* block end [id:"Spellbook_block_106"] */
        /* block comment [block:"Spellbook_block_107" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_107" category:"Variables" label:"set rad" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double rad = Math.toRadians(/* param start [id:"Spellbook_value_254" type:"number"] */90/* param end [id:"Spellbook_value_254"] */ - dirDeg);
        /* block end [id:"Spellbook_block_107"] */
        /* block comment [block:"Spellbook_block_108" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_108" category:"Variables" label:"set tx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int tx = x + (int) (Math.cos(rad) * /* param start [id:"Spellbook_value_255" type:"number"] */130/* param end [id:"Spellbook_value_255"] */);
        /* block end [id:"Spellbook_block_108"] */
        /* block comment [block:"Spellbook_block_109" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_109" category:"Variables" label:"set ty" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int ty = y + (int) (Math.sin(rad) * /* param start [id:"Spellbook_value_256" type:"number"] */130/* param end [id:"Spellbook_value_256"] */);
        /* block end [id:"Spellbook_block_109"] */
        /* block comment [block:"Spellbook_block_110" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_110" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().addSprite(new PoisonCloud(tx, ty));
        /* block end [id:"Spellbook_block_110"] */
    }

    // --- 6: Summon Imp ---
    public static void castSummon(int x, int y) {
        /* block comment [block:"Spellbook_block_111" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Spellbook_block_111" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Spellbook_block_14" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Spellbook_block_14" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!gate(cdSummon, CD_SUMMON, MANA_SUMMON)) return;
        /* block end [id:"Spellbook_block_111"] */
        /* block end [id:"Spellbook_block_14"] */
        /* block comment [block:"Spellbook_block_112" text:"Runs the current time millis action. Values in this block are editable inputs."] */
        /* block start [id:"Spellbook_block_112" category:"My Blocks" label:"current time millis" shape:"command" docs:"Runs the current time millis action. Values in this block are editable inputs."] */
        cdSummon = System.currentTimeMillis();
        /* block end [id:"Spellbook_block_112"] */
        /* block comment [block:"Spellbook_block_113" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_113" category:"Variables" label:"set spendMana" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PlayerStats.spendMana(MANA_SUMMON);
        /* block end [id:"Spellbook_block_113"] */
        /* block comment [block:"Spellbook_block_114" text:"Calls the msg method using its declared arguments."] */
        /* block start [id:"Spellbook_block_114" category:"My Blocks" label:"call msg" shape:"command" docs:"Calls the msg method using its declared arguments."] */
        msg(/* param start [id:"Spellbook_value_257" type:"text"] */"Imp summoned!"/* param end [id:"Spellbook_value_257"] */);
        /* block end [id:"Spellbook_block_114"] */
        /* block comment [block:"Spellbook_block_115" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_115" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().playSound(/* param start [id:"Spellbook_value_258" type:"text"] */"jscratch_ws/assets/gear in nven_pop.wav"/* param end [id:"Spellbook_value_258"] */);
        /* block end [id:"Spellbook_block_115"] */
        /* block comment [block:"Spellbook_block_116" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_116" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().addSprite(new SummonFlash(x, y));
        /* block end [id:"Spellbook_block_116"] */
        /* block comment [block:"Spellbook_block_117" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_117" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().addSprite(new SummonedImp(x + /* param start [id:"Spellbook_value_259" type:"number"] */40/* param end [id:"Spellbook_value_259"] */, y + /* param start [id:"Spellbook_value_260" type:"number"] */40/* param end [id:"Spellbook_value_260"] */));
        /* block end [id:"Spellbook_block_117"] */
    }

    // ================= Projectiles & effects =================

    /** Shared world-space projectile base. Spells fly over terrain (no wall check yet). */
    public abstract static class SpellProjectile extends BSprite {
        /* block comment [block:"Spellbook_block_118" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_118" category:"Variables" label:"set vx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        protected int vx, vy;
        /* block end [id:"Spellbook_block_118"] */
        /* block comment [block:"Spellbook_block_119" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_119" category:"Variables" label:"set life" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        protected int life;
        /* block end [id:"Spellbook_block_119"] */
        /* block comment [block:"Spellbook_block_120" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_120" category:"Variables" label:"set radius" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        protected int radius;
        /* block end [id:"Spellbook_block_120"] */

        public SpellProjectile(String name, int wx, int wy, double dirDeg, int speed, int life, int radius) {
            /* block comment [block:"Spellbook_block_121" text:"Runs the super action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_121" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
            super(name);
            /* block end [id:"Spellbook_block_121"] */
            /* block comment [block:"Spellbook_block_122" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_122" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"Spellbook_block_15" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Spellbook_block_15" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (minimapSprite != /* param start [id:"Spellbook_value_261" type:"value"] */null/* param end [id:"Spellbook_value_261"] */) minimapSprite.hide();
            /* block end [id:"Spellbook_block_122"] */
            /* block end [id:"Spellbook_block_15"] */
            /* block comment [block:"Spellbook_block_123" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Spellbook_block_123" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.life = life;
            /* block end [id:"Spellbook_block_123"] */
            /* block comment [block:"Spellbook_block_124" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Spellbook_block_124" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.radius = radius;
            /* block end [id:"Spellbook_block_124"] */
            /* block comment [block:"Spellbook_block_125" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Spellbook_block_125" category:"Variables" label:"set rad" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double rad = Math.toRadians(/* param start [id:"Spellbook_value_262" type:"number"] */90/* param end [id:"Spellbook_value_262"] */ - dirDeg);
            /* block end [id:"Spellbook_block_125"] */
            /* block comment [block:"Spellbook_block_126" text:"Runs the cos action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_126" category:"My Blocks" label:"cos" shape:"command" docs:"Runs the cos action. Values in this block are editable inputs."] */
            this.vx = (int) (Math.cos(rad) * speed);
            /* block end [id:"Spellbook_block_126"] */
            /* block comment [block:"Spellbook_block_127" text:"Runs the sin action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_127" category:"My Blocks" label:"sin" shape:"command" docs:"Runs the sin action. Values in this block are editable inputs."] */
            this.vy = (int) (Math.sin(rad) * speed);
            /* block end [id:"Spellbook_block_127"] */
            /* block comment [block:"Spellbook_block_128" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Spellbook_block_128" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            xx = wx;
            /* block end [id:"Spellbook_block_128"] */
            /* block comment [block:"Spellbook_block_129" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Spellbook_block_129" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            yy = wy;
            /* block end [id:"Spellbook_block_129"] */
            /* block comment [block:"Spellbook_block_130" text:"Runs the point in direction action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_130" category:"Motion" label:"point in direction" shape:"command" docs:"Runs the point in direction action. Values in this block are editable inputs."] */
            pointInDirection(dirDeg);
            /* block end [id:"Spellbook_block_130"] */
            /* block comment [block:"Spellbook_block_131" text:"Calls the update method using its declared arguments."] */
            /* block start [id:"Spellbook_block_131" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
            update();
            /* block end [id:"Spellbook_block_131"] */
            /* block comment [block:"Spellbook_block_132" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Spellbook_block_132" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Stage.getInstance().goToFront(this);
            /* block end [id:"Spellbook_block_132"] */
        }

        /* block comment [block:"Spellbook_block_133" text:"Calls the onHit method using its declared arguments."] */
        /* block start [id:"Spellbook_block_133" category:"My Blocks" label:"call on hit" shape:"command" docs:"Calls the onHit method using its declared arguments."] */
        protected abstract void onHit(EnemySprite e);
        /* block end [id:"Spellbook_block_133"] */

        /* block comment [block:"Spellbook_block_134" text:"Calls the onExpire method using its declared arguments."] */
        /* block start [id:"Spellbook_block_134" category:"My Blocks" label:"call on expire" shape:"command" docs:"Calls the onExpire method using its declared arguments."] */
        protected abstract void onExpire();
        /* block end [id:"Spellbook_block_134"] */

        public void fly() {
            this.startScript(() -> {
                /* block comment [block:"Spellbook_block_16" text:"Repeats the body while its Boolean condition is true."] */
                /* block start [id:"Spellbook_block_16" category:"Control" label:"repeat while" shape:"control" docs:"Repeats the body while its Boolean condition is true."] */
                while (life-- > /* param start [id:"Spellbook_value_263" type:"number"] */0/* param end [id:"Spellbook_value_263"] */ && isVisible()) {
                    // Stop at walls (checked before stepping, so the blast
                    // visual never embeds; bombs still detonate via onExpire)
                    /* block comment [block:"Spellbook_block_17" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"Spellbook_block_17" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (Scroller2d.getInstance().isColliding(xx + vx, yy + vy, /* param start [id:"Spellbook_value_264" type:"number"] */12/* param end [id:"Spellbook_value_264"] */, /* param start [id:"Spellbook_value_265" type:"number"] */12/* param end [id:"Spellbook_value_265"] */)) break;
                    /* block end [id:"Spellbook_block_17"] */
                    xx += vx;
                    yy += vy;
                    update();
                    boolean hitSomething = /* param start [id:"Spellbook_value_266" type:"boolean"] */false/* param end [id:"Spellbook_value_266"] */;
                    /* block comment [block:"Spellbook_block_18" text:"Repeats the nested operations for each loop turn."] */
                    /* block start [id:"Spellbook_block_18" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
                    for (EnemySprite e : EnemyManager.getInstance().getEnemies()) {
                        /* block comment [block:"Spellbook_block_19" text:"Checks a Boolean condition and runs the body when it is true."] */
                        /* block start [id:"Spellbook_block_19" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                        if (!e.isVisible()) continue;
                        /* block end [id:"Spellbook_block_19"] */
                        double dx = e.xx - xx;
                        double dy = e.yy - yy;
                        double rr = radius + /* param start [id:"Spellbook_value_267" type:"number"] */20/* param end [id:"Spellbook_value_267"] */;
                        /* block comment [block:"Spellbook_block_20" text:"Checks a Boolean condition and runs the body when it is true."] */
                        /* block start [id:"Spellbook_block_20" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                        if (dx * dx + dy * dy < rr * rr) {
                            onHit(e);
                            hitSomething = /* param start [id:"Spellbook_value_268" type:"boolean"] */true/* param end [id:"Spellbook_value_268"] */;
                            break;
                        }
                        /* block end [id:"Spellbook_block_20"] */
                    }
                    /* block end [id:"Spellbook_block_18"] */
                    /* block comment [block:"Spellbook_block_21" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"Spellbook_block_21" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (hitSomething) break;
                    /* block end [id:"Spellbook_block_21"] */
                    /* block comment [block:"Spellbook_block_22" text:"Runs work safely and handles matching errors in a catch branch."] */
                    /* block start [id:"Spellbook_block_22" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
                    try { Thread.sleep(/* param start [id:"Spellbook_value_269" type:"number"] */16/* param end [id:"Spellbook_value_269"] */); }
                    /* block end [id:"Spellbook_block_22"] */ catch (InterruptedException ex) { Thread.currentThread().interrupt(); break; }
                }
                /* block end [id:"Spellbook_block_16"] */
                /* block comment [block:"Spellbook_block_23" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"Spellbook_block_23" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (isVisible()) {
                    destroy();
                    onExpire();
                }
                /* block end [id:"Spellbook_block_23"] */
            }/* block comment [block:"Spellbook_block_135" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Spellbook_block_135" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            );
            /* block end [id:"Spellbook_block_135"] */
        }
    }

    /** Cherry-bomb projectile: explodes on contact or expiry. */
    public static class BombProjectile extends SpellProjectile {
        public BombProjectile(int wx, int wy, double dirDeg) {
            /* block comment [block:"Spellbook_block_136" text:"Runs the super action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_136" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
            super(/* param start [id:"Spellbook_value_270" type:"text"] */"Bomb"/* param end [id:"Spellbook_value_270"] */, wx, wy, dirDeg, /* param start [id:"Spellbook_value_271" type:"number"] */7/* param end [id:"Spellbook_value_271"] */, /* param start [id:"Spellbook_value_272" type:"number"] */70/* param end [id:"Spellbook_value_272"] */, /* param start [id:"Spellbook_value_273" type:"number"] */16/* param end [id:"Spellbook_value_273"] */);
            /* block end [id:"Spellbook_block_136"] */
            /* block comment [block:"Spellbook_block_137" text:"Runs the add costume action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_137" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
            addCostume(ResourcePool.getCostume(/* param start [id:"Spellbook_value_274" type:"text"] */"bomb"/* param end [id:"Spellbook_value_274"] */, /* param start [id:"Spellbook_value_275" type:"text"] */"jscratch_ws/assets/bomb.png"/* param end [id:"Spellbook_value_275"] */, /* param start [id:"Spellbook_value_276" type:"number"] */190/* param end [id:"Spellbook_value_276"] */, /* param start [id:"Spellbook_value_277" type:"number"] */58/* param end [id:"Spellbook_value_277"] */));
            /* block end [id:"Spellbook_block_137"] */
            /* block comment [block:"Spellbook_block_138" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Spellbook_block_138" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.localScale = /* param start [id:"Spellbook_value_278" type:"number"] */0.25/* param end [id:"Spellbook_value_278"] */;
            /* block end [id:"Spellbook_block_138"] */
            /* block comment [block:"Spellbook_block_139" text:"Runs the switch costume action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_139" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
            switchCostume(/* param start [id:"Spellbook_value_279" type:"text"] */"bomb"/* param end [id:"Spellbook_value_279"] */);
            /* block end [id:"Spellbook_block_139"] */
            /* block comment [block:"Spellbook_block_140" text:"Calls the update method using its declared arguments."] */
            /* block start [id:"Spellbook_block_140" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
            update();
            /* block end [id:"Spellbook_block_140"] */
            /* block comment [block:"Spellbook_block_141" text:"Calls the fly method using its declared arguments."] */
            /* block start [id:"Spellbook_block_141" category:"My Blocks" label:"call fly" shape:"command" docs:"Calls the fly method using its declared arguments."] */
            fly();
            /* block end [id:"Spellbook_block_141"] */
        }

        @Override
        protected void onHit(EnemySprite e) {
            // Detonation happens once in onExpire after the flight loop
            // breaks; exploding here too would double-apply the AoE.
        }

        @Override
        protected void onExpire() {
            /* block comment [block:"Spellbook_block_142" text:"Calls the explode method using its declared arguments."] */
            /* block start [id:"Spellbook_block_142" category:"My Blocks" label:"call explode" shape:"command" docs:"Calls the explode method using its declared arguments."] */
            explode();
            /* block end [id:"Spellbook_block_142"] */
        }

        private void explode() {
            /* block comment [block:"Spellbook_block_143" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Spellbook_block_143" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Stage.getInstance().addSprite(new Explosion(xx, yy));
            /* block end [id:"Spellbook_block_143"] */
        }
    }

    /** Starburst AoE: 3d6 fire, DEX save vs spell DC for half. */
    public static class Explosion extends BSprite {
        public Explosion(int wx, int wy) {
            /* block comment [block:"Spellbook_block_144" text:"Runs the super action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_144" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
            super(/* param start [id:"Spellbook_value_280" type:"text"] */"Explosion"/* param end [id:"Spellbook_value_280"] */);
            /* block end [id:"Spellbook_block_144"] */
            /* block comment [block:"Spellbook_block_145" text:"Runs the add costume action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_145" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
            addCostume(ResourcePool.getCostume(/* param start [id:"Spellbook_value_281" type:"text"] */"boom"/* param end [id:"Spellbook_value_281"] */, /* param start [id:"Spellbook_value_282" type:"text"] */"jscratch_ws/assets/boom.png"/* param end [id:"Spellbook_value_282"] */, /* param start [id:"Spellbook_value_283" type:"number"] */306/* param end [id:"Spellbook_value_283"] */, /* param start [id:"Spellbook_value_284" type:"number"] */280/* param end [id:"Spellbook_value_284"] */));
            /* block end [id:"Spellbook_block_145"] */
            /* block comment [block:"Spellbook_block_146" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_146" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"Spellbook_block_24" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Spellbook_block_24" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (minimapSprite != /* param start [id:"Spellbook_value_285" type:"value"] */null/* param end [id:"Spellbook_value_285"] */) minimapSprite.hide();
            /* block end [id:"Spellbook_block_146"] */
            /* block end [id:"Spellbook_block_24"] */
            /* block comment [block:"Spellbook_block_147" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Spellbook_block_147" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.localScale = /* param start [id:"Spellbook_value_286" type:"number"] */0.35/* param end [id:"Spellbook_value_286"] */;
            /* block end [id:"Spellbook_block_147"] */
            /* block comment [block:"Spellbook_block_148" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Spellbook_block_148" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            xx = wx;
            /* block end [id:"Spellbook_block_148"] */
            /* block comment [block:"Spellbook_block_149" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Spellbook_block_149" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            yy = wy;
            /* block end [id:"Spellbook_block_149"] */
            /* block comment [block:"Spellbook_block_150" text:"Calls the update method using its declared arguments."] */
            /* block start [id:"Spellbook_block_150" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
            update();
            /* block end [id:"Spellbook_block_150"] */
            /* block comment [block:"Spellbook_block_151" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Spellbook_block_151" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Stage.getInstance().goToFront(this);
            /* block end [id:"Spellbook_block_151"] */
            /* block comment [block:"Spellbook_block_152" text:"Runs the switch costume action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_152" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
            switchCostume(/* param start [id:"Spellbook_value_287" type:"text"] */"boom"/* param end [id:"Spellbook_value_287"] */);
            /* block end [id:"Spellbook_block_152"] */
            /* block comment [block:"Spellbook_block_153" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Spellbook_block_153" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Stage.getInstance().playSound(/* param start [id:"Spellbook_value_288" type:"text"] */"jscratch_ws/assets/export (1).wav"/* param end [id:"Spellbook_value_288"] */);
            /* block end [id:"Spellbook_block_153"] */

            /* block comment [block:"Spellbook_block_154" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Spellbook_block_154" category:"Variables" label:"set dc" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int dc = PlayerStats.ready ? PlayerStats.getSpellSaveDC() : /* param start [id:"Spellbook_value_289" type:"number"] */10/* param end [id:"Spellbook_value_289"] */;
            /* block end [id:"Spellbook_block_154"] */
            /* block comment [block:"Spellbook_block_25" text:"Repeats the nested operations for each loop turn."] */
            /* block start [id:"Spellbook_block_25" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
            for (EnemySprite e : EnemyManager.getInstance().getEnemies()) {
                /* block comment [block:"Spellbook_block_155" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"Spellbook_block_155" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"Spellbook_block_26" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"Spellbook_block_26" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (!e.isVisible()) continue;
                /* block end [id:"Spellbook_block_155"] */
                /* block end [id:"Spellbook_block_26"] */
                /* block comment [block:"Spellbook_block_156" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"Spellbook_block_156" category:"Variables" label:"set dx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                double dx = e.xx - xx;
                /* block end [id:"Spellbook_block_156"] */
                /* block comment [block:"Spellbook_block_157" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"Spellbook_block_157" category:"Variables" label:"set dy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                double dy = e.yy - yy;
                /* block end [id:"Spellbook_block_157"] */
                /* block comment [block:"Spellbook_block_158" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"Spellbook_block_158" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"Spellbook_block_27" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"Spellbook_block_27" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (dx * dx + dy * dy > /* param start [id:"Spellbook_value_290" type:"number"] */110/* param end [id:"Spellbook_value_290"] */ * /* param start [id:"Spellbook_value_291" type:"number"] */110/* param end [id:"Spellbook_value_291"] */) continue;
                /* block end [id:"Spellbook_block_158"] */
                /* block end [id:"Spellbook_block_27"] */
                /* block comment [block:"Spellbook_block_159" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"Spellbook_block_159" category:"Variables" label:"set dmg" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int dmg = Combat.roll(/* param start [id:"Spellbook_value_292" type:"number"] */3/* param end [id:"Spellbook_value_292"] */, /* param start [id:"Spellbook_value_293" type:"number"] */6/* param end [id:"Spellbook_value_293"] */);
                /* block end [id:"Spellbook_block_159"] */
                /* block comment [block:"Spellbook_block_160" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"Spellbook_block_160" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"Spellbook_block_28" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"Spellbook_block_28" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (Combat.d20() + e.dexSaveBonus >= dc) dmg = Math.max(/* param start [id:"Spellbook_value_294" type:"number"] */1/* param end [id:"Spellbook_value_294"] */, dmg / /* param start [id:"Spellbook_value_295" type:"number"] */2/* param end [id:"Spellbook_value_295"] */);
                /* block end [id:"Spellbook_block_160"] */
                /* block end [id:"Spellbook_block_28"] */
                /* block comment [block:"Spellbook_block_161" text:"Calls the takeDamage method using its declared arguments."] */
                /* block start [id:"Spellbook_block_161" category:"My Blocks" label:"call take damage" shape:"command" docs:"Calls the takeDamage method using its declared arguments."] */
                e.takeDamage(dmg);
                /* block end [id:"Spellbook_block_161"] */
            }
            /* block end [id:"Spellbook_block_25"] */

            this.startScript(() -> {
                /* block comment [block:"Spellbook_block_29" text:"Runs work safely and handles matching errors in a catch branch."] */
                /* block start [id:"Spellbook_block_29" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
                try { Thread.sleep(/* param start [id:"Spellbook_value_296" type:"number"] */450/* param end [id:"Spellbook_value_296"] */); }
                /* block end [id:"Spellbook_block_29"] */ catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
                destroy();
            }/* block comment [block:"Spellbook_block_162" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Spellbook_block_162" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            );
            /* block end [id:"Spellbook_block_162"] */
        }
    }

    /** Fast single-target bolt: d20 + spell attack vs AC, 1d10 + spell mod. */
    public static class MagicBolt extends SpellProjectile {
        public MagicBolt(int wx, int wy, double dirDeg) {
            /* block comment [block:"Spellbook_block_163" text:"Runs the super action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_163" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
            super(/* param start [id:"Spellbook_value_297" type:"text"] */"MagicBolt"/* param end [id:"Spellbook_value_297"] */, wx, wy, dirDeg, /* param start [id:"Spellbook_value_298" type:"number"] */11/* param end [id:"Spellbook_value_298"] */, /* param start [id:"Spellbook_value_299" type:"number"] */50/* param end [id:"Spellbook_value_299"] */, /* param start [id:"Spellbook_value_300" type:"number"] */10/* param end [id:"Spellbook_value_300"] */);
            /* block end [id:"Spellbook_block_163"] */
            /* block comment [block:"Spellbook_block_164" text:"Runs the add costume action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_164" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
            addCostume(ResourcePool.getCostume(/* param start [id:"Spellbook_value_301" type:"text"] */"bolt"/* param end [id:"Spellbook_value_301"] */, /* param start [id:"Spellbook_value_302" type:"text"] */"jscratch_ws/assets/Sprite4_costume1.png"/* param end [id:"Spellbook_value_302"] */, /* param start [id:"Spellbook_value_303" type:"number"] */94/* param end [id:"Spellbook_value_303"] */, /* param start [id:"Spellbook_value_304" type:"number"] */88/* param end [id:"Spellbook_value_304"] */));
            /* block end [id:"Spellbook_block_164"] */
            /* block comment [block:"Spellbook_block_165" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Spellbook_block_165" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.localScale = /* param start [id:"Spellbook_value_305" type:"number"] */0.2/* param end [id:"Spellbook_value_305"] */;
            /* block end [id:"Spellbook_block_165"] */
            /* block comment [block:"Spellbook_block_166" text:"Runs the switch costume action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_166" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
            switchCostume(/* param start [id:"Spellbook_value_306" type:"text"] */"bolt"/* param end [id:"Spellbook_value_306"] */);
            /* block end [id:"Spellbook_block_166"] */
            /* block comment [block:"Spellbook_block_167" text:"Calls the update method using its declared arguments."] */
            /* block start [id:"Spellbook_block_167" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
            update();
            /* block end [id:"Spellbook_block_167"] */
            /* block comment [block:"Spellbook_block_168" text:"Calls the fly method using its declared arguments."] */
            /* block start [id:"Spellbook_block_168" category:"My Blocks" label:"call fly" shape:"command" docs:"Calls the fly method using its declared arguments."] */
            fly();
            /* block end [id:"Spellbook_block_168"] */
        }

        @Override
        protected void onHit(EnemySprite e) {
            /* block comment [block:"Spellbook_block_169" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Spellbook_block_169" category:"Variables" label:"set bonus" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int bonus = PlayerStats.ready ? PlayerStats.getSpellAttackBonus() : /* param start [id:"Spellbook_value_307" type:"number"] */0/* param end [id:"Spellbook_value_307"] */;
            /* block end [id:"Spellbook_block_169"] */
            /* block comment [block:"Spellbook_block_170" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Spellbook_block_170" category:"Variables" label:"set mod" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int mod = /* param start [id:"Spellbook_value_308" type:"number"] */0/* param end [id:"Spellbook_value_308"] */;
            /* block end [id:"Spellbook_block_170"] */
            /* block comment [block:"Spellbook_block_30" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Spellbook_block_30" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (PlayerStats.ready && PlayerStats.stats != /* param start [id:"Spellbook_value_309" type:"value"] */null/* param end [id:"Spellbook_value_309"] */ && PlayerStats.stats.isSpellcaster) {
                /* block comment [block:"Spellbook_block_171" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"Spellbook_block_171" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                mod = bonus - PlayerStats.stats.proficiency;
                /* block end [id:"Spellbook_block_171"] */
            }
            /* block end [id:"Spellbook_block_30"] */
            /* block comment [block:"Spellbook_block_172" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Spellbook_block_172" category:"Variables" label:"set res" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int res = Combat.meleeStrike(bonus, e.armourClass);
            /* block end [id:"Spellbook_block_172"] */
            /* block comment [block:"Spellbook_block_173" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_173" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"Spellbook_block_31" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Spellbook_block_31" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (res > /* param start [id:"Spellbook_value_310" type:"number"] */0/* param end [id:"Spellbook_value_310"] */) e.takeDamage(Combat.rollDamage(/* param start [id:"Spellbook_value_311" type:"number"] */10/* param end [id:"Spellbook_value_311"] */, mod, res == /* param start [id:"Spellbook_value_312" type:"number"] */2/* param end [id:"Spellbook_value_312"] */));
            /* block end [id:"Spellbook_block_173"] */
            /* block end [id:"Spellbook_block_31"] */
        }

        @Override
        protected void onExpire() {
            // fizzles out, nothing more
        }
    }

    /** Lingering poison zone: 1d4 ticks every 500ms for 6s, CON save halves. */
    public static class PoisonCloud extends BSprite {
        public PoisonCloud(int wx, int wy) {
            /* block comment [block:"Spellbook_block_174" text:"Runs the super action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_174" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
            super(/* param start [id:"Spellbook_value_313" type:"text"] */"PoisonCloud"/* param end [id:"Spellbook_value_313"] */);
            /* block end [id:"Spellbook_block_174"] */
            /* block comment [block:"Spellbook_block_175" text:"Runs the add costume action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_175" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
            addCostume(ResourcePool.getCostume(/* param start [id:"Spellbook_value_314" type:"text"] */"smoke1"/* param end [id:"Spellbook_value_314"] */, /* param start [id:"Spellbook_value_315" type:"text"] */"jscratch_ws/assets/Sprite7_pixil-frame-0__3_.png"/* param end [id:"Spellbook_value_315"] */, /* param start [id:"Spellbook_value_316" type:"number"] */76/* param end [id:"Spellbook_value_316"] */, /* param start [id:"Spellbook_value_317" type:"number"] */76/* param end [id:"Spellbook_value_317"] */));
            /* block end [id:"Spellbook_block_175"] */
            /* block comment [block:"Spellbook_block_176" text:"Runs the add costume action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_176" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
            addCostume(ResourcePool.getCostume(/* param start [id:"Spellbook_value_318" type:"text"] */"smoke2"/* param end [id:"Spellbook_value_318"] */, /* param start [id:"Spellbook_value_319" type:"text"] */"jscratch_ws/assets/Sprite7_pixil-frame-0__4_.png"/* param end [id:"Spellbook_value_319"] */, /* param start [id:"Spellbook_value_320" type:"number"] */55/* param end [id:"Spellbook_value_320"] */, /* param start [id:"Spellbook_value_321" type:"number"] */55/* param end [id:"Spellbook_value_321"] */));
            /* block end [id:"Spellbook_block_176"] */
            /* block comment [block:"Spellbook_block_177" text:"Runs the add costume action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_177" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
            addCostume(ResourcePool.getCostume(/* param start [id:"Spellbook_value_322" type:"text"] */"smoke3"/* param end [id:"Spellbook_value_322"] */, /* param start [id:"Spellbook_value_323" type:"text"] */"jscratch_ws/assets/Sprite7_pixil-frame-0__5_.png"/* param end [id:"Spellbook_value_323"] */, /* param start [id:"Spellbook_value_324" type:"number"] */58/* param end [id:"Spellbook_value_324"] */, /* param start [id:"Spellbook_value_325" type:"number"] */58/* param end [id:"Spellbook_value_325"] */));
            /* block end [id:"Spellbook_block_177"] */
            /* block comment [block:"Spellbook_block_178" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_178" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"Spellbook_block_32" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Spellbook_block_32" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (minimapSprite != /* param start [id:"Spellbook_value_326" type:"value"] */null/* param end [id:"Spellbook_value_326"] */) minimapSprite.hide();
            /* block end [id:"Spellbook_block_178"] */
            /* block end [id:"Spellbook_block_32"] */
            /* block comment [block:"Spellbook_block_179" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Spellbook_block_179" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.localScale = /* param start [id:"Spellbook_value_327" type:"number"] */1.4/* param end [id:"Spellbook_value_327"] */;
            /* block end [id:"Spellbook_block_179"] */
            /* block comment [block:"Spellbook_block_180" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Spellbook_block_180" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            xx = wx;
            /* block end [id:"Spellbook_block_180"] */
            /* block comment [block:"Spellbook_block_181" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Spellbook_block_181" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            yy = wy;
            /* block end [id:"Spellbook_block_181"] */
            /* block comment [block:"Spellbook_block_182" text:"Calls the update method using its declared arguments."] */
            /* block start [id:"Spellbook_block_182" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
            update();
            /* block end [id:"Spellbook_block_182"] */
            /* block comment [block:"Spellbook_block_183" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Spellbook_block_183" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Stage.getInstance().goToFront(this);
            /* block end [id:"Spellbook_block_183"] */

            this.startScript(() -> {
                int dc = PlayerStats.ready ? PlayerStats.getSpellSaveDC() : 10;
                int mod = 0;
                /* block comment [block:"Spellbook_block_33" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"Spellbook_block_33" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (PlayerStats.ready && PlayerStats.stats != /* param start [id:"Spellbook_value_328" type:"value"] */null/* param end [id:"Spellbook_value_328"] */) {
                    mod = Math.max(/* param start [id:"Spellbook_value_329" type:"number"] */0/* param end [id:"Spellbook_value_329"] */, PlayerStats.stats.intelligenceModifier);
                }
                /* block end [id:"Spellbook_block_33"] */
                /* block comment [block:"Spellbook_block_34" text:"Repeats the nested operations for each loop turn."] */
                /* block start [id:"Spellbook_block_34" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
                for (int t = /* param start [id:"Spellbook_value_330" type:"number"] */0/* param end [id:"Spellbook_value_330"] */; t < /* param start [id:"Spellbook_value_331" type:"number"] */12/* param end [id:"Spellbook_value_331"] */ && isVisible(); t++) {
                    switchCostume(t % /* param start [id:"Spellbook_value_332" type:"number"] */2/* param end [id:"Spellbook_value_332"] */ == /* param start [id:"Spellbook_value_333" type:"number"] */0/* param end [id:"Spellbook_value_333"] */ ? /* param start [id:"Spellbook_value_334" type:"text"] */"smoke1"/* param end [id:"Spellbook_value_334"] */ : t % /* param start [id:"Spellbook_value_335" type:"number"] */4/* param end [id:"Spellbook_value_335"] */ == /* param start [id:"Spellbook_value_336" type:"number"] */1/* param end [id:"Spellbook_value_336"] */ ? /* param start [id:"Spellbook_value_337" type:"text"] */"smoke2"/* param end [id:"Spellbook_value_337"] */ : /* param start [id:"Spellbook_value_338" type:"text"] */"smoke3"/* param end [id:"Spellbook_value_338"] */);
                    update();
                    /* block comment [block:"Spellbook_block_35" text:"Repeats the nested operations for each loop turn."] */
                    /* block start [id:"Spellbook_block_35" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
                    for (EnemySprite e : EnemyManager.getInstance().getEnemies()) {
                        /* block comment [block:"Spellbook_block_36" text:"Checks a Boolean condition and runs the body when it is true."] */
                        /* block start [id:"Spellbook_block_36" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                        if (!e.isVisible()) continue;
                        /* block end [id:"Spellbook_block_36"] */
                        double dx = e.xx - xx;
                        double dy = e.yy - yy;
                        /* block comment [block:"Spellbook_block_37" text:"Checks a Boolean condition and runs the body when it is true."] */
                        /* block start [id:"Spellbook_block_37" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                        if (dx * dx + dy * dy > /* param start [id:"Spellbook_value_339" type:"number"] */90/* param end [id:"Spellbook_value_339"] */ * /* param start [id:"Spellbook_value_340" type:"number"] */90/* param end [id:"Spellbook_value_340"] */) continue;
                        /* block end [id:"Spellbook_block_37"] */
                        int dmg = Math.max(/* param start [id:"Spellbook_value_341" type:"number"] */1/* param end [id:"Spellbook_value_341"] */, Combat.rollDie(/* param start [id:"Spellbook_value_342" type:"number"] */4/* param end [id:"Spellbook_value_342"] */) + mod / /* param start [id:"Spellbook_value_343" type:"number"] */2/* param end [id:"Spellbook_value_343"] */);
                        /* block comment [block:"Spellbook_block_38" text:"Checks a Boolean condition and runs the body when it is true."] */
                        /* block start [id:"Spellbook_block_38" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                        if (Combat.d20() + e.conSaveBonus >= dc) dmg = Math.max(/* param start [id:"Spellbook_value_344" type:"number"] */1/* param end [id:"Spellbook_value_344"] */, dmg / /* param start [id:"Spellbook_value_345" type:"number"] */2/* param end [id:"Spellbook_value_345"] */);
                        /* block end [id:"Spellbook_block_38"] */
                        e.takeDamage(dmg);
                    }
                    /* block end [id:"Spellbook_block_35"] */
                    /* block comment [block:"Spellbook_block_39" text:"Runs work safely and handles matching errors in a catch branch."] */
                    /* block start [id:"Spellbook_block_39" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
                    try { Thread.sleep(/* param start [id:"Spellbook_value_346" type:"number"] */500/* param end [id:"Spellbook_value_346"] */); }
                    /* block end [id:"Spellbook_block_39"] */ catch (InterruptedException ex) { Thread.currentThread().interrupt(); break; }
                }
                /* block end [id:"Spellbook_block_34"] */
                destroy();
            }/* block comment [block:"Spellbook_block_184" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Spellbook_block_184" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            );
            /* block end [id:"Spellbook_block_184"] */
        }
    }

    /** Heart flash above the caster on heal. */
    public static class HealFlash extends BSprite {
        public HealFlash(int wx, int wy) {
            /* block comment [block:"Spellbook_block_185" text:"Runs the super action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_185" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
            super(/* param start [id:"Spellbook_value_347" type:"text"] */"HealFlash"/* param end [id:"Spellbook_value_347"] */);
            /* block end [id:"Spellbook_block_185"] */
            /* block comment [block:"Spellbook_block_186" text:"Runs the add costume action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_186" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
            addCostume(ResourcePool.getCostume(/* param start [id:"Spellbook_value_348" type:"text"] */"healheart"/* param end [id:"Spellbook_value_348"] */, /* param start [id:"Spellbook_value_349" type:"text"] */"jscratch_ws/assets/heart.png"/* param end [id:"Spellbook_value_349"] */, /* param start [id:"Spellbook_value_350" type:"number"] */3/* param end [id:"Spellbook_value_350"] */, /* param start [id:"Spellbook_value_351" type:"number"] */3/* param end [id:"Spellbook_value_351"] */));
            /* block end [id:"Spellbook_block_186"] */
            /* block comment [block:"Spellbook_block_187" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_187" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"Spellbook_block_40" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Spellbook_block_40" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (minimapSprite != /* param start [id:"Spellbook_value_352" type:"value"] */null/* param end [id:"Spellbook_value_352"] */) minimapSprite.hide();
            /* block end [id:"Spellbook_block_187"] */
            /* block end [id:"Spellbook_block_40"] */
            /* block comment [block:"Spellbook_block_188" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Spellbook_block_188" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.localScale = /* param start [id:"Spellbook_value_353" type:"number"] */3.0/* param end [id:"Spellbook_value_353"] */;
            /* block end [id:"Spellbook_block_188"] */
            /* block comment [block:"Spellbook_block_189" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Spellbook_block_189" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            xx = wx;
            /* block end [id:"Spellbook_block_189"] */
            /* block comment [block:"Spellbook_block_190" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Spellbook_block_190" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            yy = wy + /* param start [id:"Spellbook_value_354" type:"number"] */60/* param end [id:"Spellbook_value_354"] */;
            /* block end [id:"Spellbook_block_190"] */
            /* block comment [block:"Spellbook_block_191" text:"Calls the update method using its declared arguments."] */
            /* block start [id:"Spellbook_block_191" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
            update();
            /* block end [id:"Spellbook_block_191"] */
            /* block comment [block:"Spellbook_block_192" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Spellbook_block_192" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Stage.getInstance().goToFront(this);
            /* block end [id:"Spellbook_block_192"] */
            /* block comment [block:"Spellbook_block_193" text:"Runs the switch costume action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_193" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
            switchCostume(/* param start [id:"Spellbook_value_355" type:"text"] */"healheart"/* param end [id:"Spellbook_value_355"] */);
            /* block end [id:"Spellbook_block_193"] */
            this.startScript(() -> {
                /* block comment [block:"Spellbook_block_41" text:"Runs work safely and handles matching errors in a catch branch."] */
                /* block start [id:"Spellbook_block_41" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
                try { Thread.sleep(/* param start [id:"Spellbook_value_356" type:"number"] */600/* param end [id:"Spellbook_value_356"] */); }
                /* block end [id:"Spellbook_block_41"] */ catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
                destroy();
            }/* block comment [block:"Spellbook_block_194" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Spellbook_block_194" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            );
            /* block end [id:"Spellbook_block_194"] */
        }
    }

    /** Red-maw cast flash at the summon point. */
    public static class SummonFlash extends BSprite {
        public SummonFlash(int wx, int wy) {
            /* block comment [block:"Spellbook_block_195" text:"Runs the super action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_195" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
            super(/* param start [id:"Spellbook_value_357" type:"text"] */"SummonFlash"/* param end [id:"Spellbook_value_357"] */);
            /* block end [id:"Spellbook_block_195"] */
            /* block comment [block:"Spellbook_block_196" text:"Runs the add costume action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_196" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
            addCostume(ResourcePool.getCostume(/* param start [id:"Spellbook_value_358" type:"text"] */"maw"/* param end [id:"Spellbook_value_358"] */, /* param start [id:"Spellbook_value_359" type:"text"] */"jscratch_ws/assets/Sprite2_costume_attack.png"/* param end [id:"Spellbook_value_359"] */, /* param start [id:"Spellbook_value_360" type:"number"] */52/* param end [id:"Spellbook_value_360"] */, /* param start [id:"Spellbook_value_361" type:"number"] */69/* param end [id:"Spellbook_value_361"] */));
            /* block end [id:"Spellbook_block_196"] */
            /* block comment [block:"Spellbook_block_197" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_197" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"Spellbook_block_42" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Spellbook_block_42" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (minimapSprite != /* param start [id:"Spellbook_value_362" type:"value"] */null/* param end [id:"Spellbook_value_362"] */) minimapSprite.hide();
            /* block end [id:"Spellbook_block_197"] */
            /* block end [id:"Spellbook_block_42"] */
            /* block comment [block:"Spellbook_block_198" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Spellbook_block_198" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.localScale = /* param start [id:"Spellbook_value_363" type:"number"] */0.7/* param end [id:"Spellbook_value_363"] */;
            /* block end [id:"Spellbook_block_198"] */
            /* block comment [block:"Spellbook_block_199" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Spellbook_block_199" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            xx = wx;
            /* block end [id:"Spellbook_block_199"] */
            /* block comment [block:"Spellbook_block_200" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Spellbook_block_200" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            yy = wy;
            /* block end [id:"Spellbook_block_200"] */
            /* block comment [block:"Spellbook_block_201" text:"Calls the update method using its declared arguments."] */
            /* block start [id:"Spellbook_block_201" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
            update();
            /* block end [id:"Spellbook_block_201"] */
            /* block comment [block:"Spellbook_block_202" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Spellbook_block_202" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Stage.getInstance().goToFront(this);
            /* block end [id:"Spellbook_block_202"] */
            /* block comment [block:"Spellbook_block_203" text:"Runs the switch costume action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_203" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
            switchCostume(/* param start [id:"Spellbook_value_364" type:"text"] */"maw"/* param end [id:"Spellbook_value_364"] */);
            /* block end [id:"Spellbook_block_203"] */
            this.startScript(() -> {
                /* block comment [block:"Spellbook_block_43" text:"Runs work safely and handles matching errors in a catch branch."] */
                /* block start [id:"Spellbook_block_43" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
                try { Thread.sleep(/* param start [id:"Spellbook_value_365" type:"number"] */500/* param end [id:"Spellbook_value_365"] */); }
                /* block end [id:"Spellbook_block_43"] */ catch (InterruptedException ex) { Thread.currentThread().interrupt(); }
                destroy();
            }/* block comment [block:"Spellbook_block_204" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Spellbook_block_204" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            );
            /* block end [id:"Spellbook_block_204"] */
        }
    }

    /**
    * Imp ally (45s, 30 HP). Hunts the nearest visible enemy (d20+3 vs AC,
    * 1d6+1), otherwise trails the player. Enemies can strike it back
    * (AC 12) via the shared target-selection in EnemySprite.
    */
    public static class SummonedImp extends BSprite {
        /* block comment [block:"Spellbook_block_205" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_205" category:"Variables" label:"set lastStrike" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        private long lastStrike = /* param start [id:"Spellbook_value_366" type:"number"] */0/* param end [id:"Spellbook_value_366"] */;
        /* block end [id:"Spellbook_block_205"] */
        /* block comment [block:"Spellbook_block_206" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Spellbook_block_206" category:"Variables" label:"set hp" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        public int hp = /* param start [id:"Spellbook_value_367" type:"number"] */30/* param end [id:"Spellbook_value_367"] */;
        /* block end [id:"Spellbook_block_206"] */

        public SummonedImp(int wx, int wy) {
            /* block comment [block:"Spellbook_block_207" text:"Runs the super action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_207" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
            super(/* param start [id:"Spellbook_value_368" type:"text"] */"Imp"/* param end [id:"Spellbook_value_368"] */);
            /* block end [id:"Spellbook_block_207"] */
            /* block comment [block:"Spellbook_block_208" text:"Runs the add costume action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_208" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
            addCostume(ResourcePool.getCostume(/* param start [id:"Spellbook_value_369" type:"text"] */"imp"/* param end [id:"Spellbook_value_369"] */, /* param start [id:"Spellbook_value_370" type:"text"] */"jscratch_ws/assets/imp_costume1.png"/* param end [id:"Spellbook_value_370"] */, /* param start [id:"Spellbook_value_371" type:"number"] */68/* param end [id:"Spellbook_value_371"] */, /* param start [id:"Spellbook_value_372" type:"number"] */58/* param end [id:"Spellbook_value_372"] */));
            /* block end [id:"Spellbook_block_208"] */
            /* block comment [block:"Spellbook_block_209" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_209" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"Spellbook_block_44" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Spellbook_block_44" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (minimapSprite != /* param start [id:"Spellbook_value_373" type:"value"] */null/* param end [id:"Spellbook_value_373"] */) minimapSprite.hide();
            /* block end [id:"Spellbook_block_209"] */
            /* block end [id:"Spellbook_block_44"] */
            /* block comment [block:"Spellbook_block_210" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Spellbook_block_210" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.localScale = /* param start [id:"Spellbook_value_374" type:"number"] */0.6/* param end [id:"Spellbook_value_374"] */;
            /* block end [id:"Spellbook_block_210"] */
            /* block comment [block:"Spellbook_block_211" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Spellbook_block_211" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            xx = wx;
            /* block end [id:"Spellbook_block_211"] */
            /* block comment [block:"Spellbook_block_212" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Spellbook_block_212" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            yy = wy;
            /* block end [id:"Spellbook_block_212"] */
            /* block comment [block:"Spellbook_block_213" text:"Calls the update method using its declared arguments."] */
            /* block start [id:"Spellbook_block_213" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
            update();
            /* block end [id:"Spellbook_block_213"] */
            /* block comment [block:"Spellbook_block_214" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Spellbook_block_214" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Stage.getInstance().goToFront(this);
            /* block end [id:"Spellbook_block_214"] */
            /* block comment [block:"Spellbook_block_215" text:"Runs the switch costume action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_215" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
            switchCostume(/* param start [id:"Spellbook_value_375" type:"text"] */"imp"/* param end [id:"Spellbook_value_375"] */);
            /* block end [id:"Spellbook_block_215"] */
            /* block comment [block:"Spellbook_block_216" text:"Calls the add method using its declared arguments."] */
            /* block start [id:"Spellbook_block_216" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
            liveImps.add(this);
            /* block end [id:"Spellbook_block_216"] */

            this.startScript(() -> {
                long born = System.currentTimeMillis();
                /* block comment [block:"Spellbook_block_45" text:"Repeats the body while its Boolean condition is true."] */
                /* block start [id:"Spellbook_block_45" category:"Control" label:"repeat while" shape:"control" docs:"Repeats the body while its Boolean condition is true."] */
                while (isVisible() && System.currentTimeMillis() - born < /* param start [id:"Spellbook_value_376" type:"number"] */45000/* param end [id:"Spellbook_value_376"] */) {
                    EnemySprite target = nearestEnemy(/* param start [id:"Spellbook_value_377" type:"number"] */700/* param end [id:"Spellbook_value_377"] */);
                    /* block comment [block:"Spellbook_block_46" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"Spellbook_block_46" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (target != /* param start [id:"Spellbook_value_378" type:"value"] */null/* param end [id:"Spellbook_value_378"] */) {
                        double dx = target.xx - xx;
                        double dy = target.yy - yy;
                        double dist2 = dx * dx + dy * dy;
                        /* block comment [block:"Spellbook_block_47" text:"Checks a Boolean condition and runs the body when it is true."] */
                        /* block start [id:"Spellbook_block_47" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                        if (dist2 < /* param start [id:"Spellbook_value_379" type:"number"] */45/* param end [id:"Spellbook_value_379"] */ * /* param start [id:"Spellbook_value_380" type:"number"] */45/* param end [id:"Spellbook_value_380"] */) {
                            long now = System.currentTimeMillis();
                            /* block comment [block:"Spellbook_block_48" text:"Checks a Boolean condition and runs the body when it is true."] */
                            /* block start [id:"Spellbook_block_48" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                            if (now - lastStrike > /* param start [id:"Spellbook_value_381" type:"number"] */900/* param end [id:"Spellbook_value_381"] */) {
                                lastStrike = now;
                                /* block comment [block:"Spellbook_block_49" text:"Checks a Boolean condition and runs the body when it is true."] */
                                /* block start [id:"Spellbook_block_49" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                                if (Combat.meleeStrike(/* param start [id:"Spellbook_value_382" type:"number"] */3/* param end [id:"Spellbook_value_382"] */, target.armourClass) > /* param start [id:"Spellbook_value_383" type:"number"] */0/* param end [id:"Spellbook_value_383"] */) {
                                    target.takeDamage(Combat.rollDamage(/* param start [id:"Spellbook_value_384" type:"number"] */6/* param end [id:"Spellbook_value_384"] */, /* param start [id:"Spellbook_value_385" type:"number"] */1/* param end [id:"Spellbook_value_385"] */, /* param start [id:"Spellbook_value_386" type:"boolean"] */false/* param end [id:"Spellbook_value_386"] */));
                                }
                                /* block end [id:"Spellbook_block_49"] */
                            }
                            /* block end [id:"Spellbook_block_48"] */
                        } else {
                            double a = Math.atan2(dy, dx);
                            tryMove((int) (Math.cos(a) * /* param start [id:"Spellbook_value_387" type:"number"] */5/* param end [id:"Spellbook_value_387"] */), (int) (Math.sin(a) * /* param start [id:"Spellbook_value_388" type:"number"] */5/* param end [id:"Spellbook_value_388"] */));
                        }
                        /* block end [id:"Spellbook_block_47"] */
                    } else {
                        // No prey in range: trail the player (scroll offset is -player pos).
                        int px = -Scroller2d.getInstance().x;
                        int py = -Scroller2d.getInstance().y;
                        double dx = px - xx;
                        double dy = py - yy;
                        /* block comment [block:"Spellbook_block_50" text:"Checks a Boolean condition and runs the body when it is true."] */
                        /* block start [id:"Spellbook_block_50" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                        if (dx * dx + dy * dy > /* param start [id:"Spellbook_value_389" type:"number"] */120/* param end [id:"Spellbook_value_389"] */ * /* param start [id:"Spellbook_value_390" type:"number"] */120/* param end [id:"Spellbook_value_390"] */) {
                            double a = Math.atan2(dy, dx);
                            tryMove((int) (Math.cos(a) * /* param start [id:"Spellbook_value_391" type:"number"] */5/* param end [id:"Spellbook_value_391"] */), (int) (Math.sin(a) * /* param start [id:"Spellbook_value_392" type:"number"] */5/* param end [id:"Spellbook_value_392"] */));
                        }
                        /* block end [id:"Spellbook_block_50"] */
                    }
                    /* block end [id:"Spellbook_block_46"] */
                    update();
                    /* block comment [block:"Spellbook_block_51" text:"Runs work safely and handles matching errors in a catch branch."] */
                    /* block start [id:"Spellbook_block_51" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
                    try { Thread.sleep(/* param start [id:"Spellbook_value_393" type:"number"] */33/* param end [id:"Spellbook_value_393"] */); }
                    /* block end [id:"Spellbook_block_51"] */ catch (InterruptedException ex) { Thread.currentThread().interrupt(); break; }
                }
                /* block end [id:"Spellbook_block_45"] */
                liveImps.remove(this);
                destroy();
            }/* block comment [block:"Spellbook_block_217" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Spellbook_block_217" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            );
            /* block end [id:"Spellbook_block_217"] */
        }

        public void takeDamage(int amount) {
            /* block comment [block:"Spellbook_block_218" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"Spellbook_block_218" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"Spellbook_block_52" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Spellbook_block_52" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (!isVisible() || amount <= /* param start [id:"Spellbook_value_394" type:"number"] */0/* param end [id:"Spellbook_value_394"] */) return;
            /* block end [id:"Spellbook_block_218"] */
            /* block end [id:"Spellbook_block_52"] */
            /* block comment [block:"Spellbook_block_219" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Spellbook_block_219" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            hp -= amount;
            /* block end [id:"Spellbook_block_219"] */
            /* block comment [block:"Spellbook_block_53" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Spellbook_block_53" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (hp <= /* param start [id:"Spellbook_value_395" type:"number"] */0/* param end [id:"Spellbook_value_395"] */) {
                /* block comment [block:"Spellbook_block_220" text:"Runs the remove action. Values in this block are editable inputs."] */
                /* block start [id:"Spellbook_block_220" category:"Motion" label:"remove" shape:"command" docs:"Runs the remove action. Values in this block are editable inputs."] */
                liveImps.remove(this);
                /* block end [id:"Spellbook_block_220"] */
                /* block comment [block:"Spellbook_block_221" text:"Calls the destroy method using its declared arguments."] */
                /* block start [id:"Spellbook_block_221" category:"My Blocks" label:"call destroy" shape:"command" docs:"Calls the destroy method using its declared arguments."] */
                destroy();
                /* block end [id:"Spellbook_block_221"] */
            }
            /* block end [id:"Spellbook_block_53"] */
        }

        private EnemySprite nearestEnemy(int maxDist) {
            /* block comment [block:"Spellbook_block_222" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Spellbook_block_222" category:"Variables" label:"set best" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            EnemySprite best = /* param start [id:"Spellbook_value_396" type:"value"] */null/* param end [id:"Spellbook_value_396"] */;
            /* block end [id:"Spellbook_block_222"] */
            /* block comment [block:"Spellbook_block_223" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Spellbook_block_223" category:"Variables" label:"set bestD2" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double bestD2 = (double) maxDist * maxDist;
            /* block end [id:"Spellbook_block_223"] */
            /* block comment [block:"Spellbook_block_54" text:"Repeats the nested operations for each loop turn."] */
            /* block start [id:"Spellbook_block_54" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
            for (EnemySprite e : EnemyManager.getInstance().getEnemies()) {
                /* block comment [block:"Spellbook_block_224" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"Spellbook_block_224" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"Spellbook_block_55" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"Spellbook_block_55" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (!e.isVisible()) continue;
                /* block end [id:"Spellbook_block_224"] */
                /* block end [id:"Spellbook_block_55"] */
                /* block comment [block:"Spellbook_block_225" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"Spellbook_block_225" category:"Variables" label:"set dx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                double dx = e.xx - xx;
                /* block end [id:"Spellbook_block_225"] */
                /* block comment [block:"Spellbook_block_226" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"Spellbook_block_226" category:"Variables" label:"set dy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                double dy = e.yy - yy;
                /* block end [id:"Spellbook_block_226"] */
                /* block comment [block:"Spellbook_block_227" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"Spellbook_block_227" category:"Variables" label:"set d2" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                double d2 = dx * dx + dy * dy;
                /* block end [id:"Spellbook_block_227"] */
                /* block comment [block:"Spellbook_block_56" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"Spellbook_block_56" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (d2 < bestD2) {
                    /* block comment [block:"Spellbook_block_228" text:"Evaluates this expression and applies its result."] */
                    /* block start [id:"Spellbook_block_228" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                    bestD2 = d2;
                    /* block end [id:"Spellbook_block_228"] */
                    /* block comment [block:"Spellbook_block_229" text:"Evaluates this expression and applies its result."] */
                    /* block start [id:"Spellbook_block_229" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                    best = e;
                    /* block end [id:"Spellbook_block_229"] */
                }
                /* block end [id:"Spellbook_block_56"] */
            }
            /* block end [id:"Spellbook_block_54"] */
            /* block comment [block:"Spellbook_block_230" text:"Returns a value or ends the current method."] */
            /* block start [id:"Spellbook_block_230" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return best;
            /* block end [id:"Spellbook_block_230"] */
        }
    }
}
