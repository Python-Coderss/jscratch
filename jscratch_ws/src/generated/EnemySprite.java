package generated;

import com.jscratch.*;
import java.util.List;
import java.util.ArrayList;

/**
* Enemy AI: vision-gated aggro with wander, assist, swarm orbit, kiting
* retreat, wall sliding + unstick detours, separation steering, telegraphed
* melee (wind-up then strike), and target selection between the player and
* summoned imps. Hybrid D&D underneath: d20 + bonus vs AC.
*
* <p>Call graph / interactions:
* <ul>
*   <li>Driven by {@code EnemyManager.update} → {@link #think(int, int)}
*       once per enemy per player frame: hpUpdate → acquireTarget →
*       selectState → state executor → separate → update.</li>
*   <li>Reads: {@code PlayerStats} (AC, hp, takeDamage, death broadcast),
*       {@code Spellbook.nearestImp/liveImps} (target choice),
*       {@code Scroller2d} (collision via tryMove/willCollide),
*       {@code EnemyManager.getEnemies} (assist + separation).</li>
*   <li>Writes: {@code PlayerStats.takeDamage} / imp {@code takeDamage} in
*       {@code landStrike}; {@code PlayerStats.addXp},
*       {@code Inventory.addGold}, {@code LootDrop.spawnAt},
*       {@code EnemyManager.enemyCount--} in {@code die}.</li>
*   <li>Subtypes tune the machine via hooks (canRetreat/retreatHpFrac,
*       canSwarm/swarmDist, onLandedStrike) — see Orc/Ghoul/Minitaur.</li>
* </ul>
*
* State machine notes:
*  - ATTACK entry is immediate (responsive strikes); leaving ATTACK
*    mid-wind-up is forbidden so telegraphs always resolve.
*  - All other transitions respect a 250ms minimum state time plus the
*    swarm hysteresis band, so enemies never buzz between states.
*  - Retreat is percent-based with a 1.5s kite + 7s cooldown: wounded
*    enemies disengage and re-engage instead of cowering forever.
*  - Damage aggroes for 4s even outside vision (they heard the hit).
*  - Subtypes tune behavior via hooks (canRetreat/canSwarm/swarmDist/...).
*/
public class EnemySprite extends BSprite {
    public enum AIState { PURSUE, RETREAT, ASSIST, ATTACK, SWARM, WANDER }

    protected AIState currentState = AIState.PURSUE;
    protected int health = 100;
    protected int maxHealth = 100;
    protected int speed = 2;
    protected int attackRange = 40;
    protected int visionRange = 500;
    // Hybrid D&D combat stats (subtypes override)
    protected int armourClass = 11;
    protected int attackBonus = 2;
    protected int damageDie = 6;
    protected int damageMod = 1;
    protected int xpValue = 25;
    protected int dexSaveBonus = 1;
    protected int conSaveBonus = 1;
    protected long lastAttackMs = 0;
    protected int attackCooldownMs = 1000;
    protected int windupMs = 250;
    protected HPBAR hpBar;

    double distToPlayer = 99999;
    // For Swarm/Positioning
    protected double swarmAngle = 0;

    // Wind-up state
    private boolean windingUp = false;
    private long windupUntil = 0;

    // Target selection (player by default, nearest closer imp otherwise)
    private Spellbook.SummonedImp targetImp = null;
    private int retargetCd = 0;

    // State stability + kiting + aggro memory
    private static final long MIN_STATE_MS = 250;
    private long stateSince = 0;
    private long retreatUntil = 0;
    private long retreatCdUntil = 0;
    private long aggroUntil = 0;

    // Assist cache (ally scans throttled, not per frame)
    private long assistRecheck = 0;
    private EnemySprite cachedAlly = null;

    // Last known target pos (drives assist: go to the ally's fight)
    protected int lastTx = 0;
    protected int lastTy = 0;

    // Wander waypoints
    private int wpx, wpy;
    private boolean hasWp = false;
    private long wanderPauseUntil = 0;

    // Unstick detours
    private int stillFrames = 0;
    private long detourUntil = 0;
    private long lastDetourFlip = 0;
    private int detourDx, detourDy;

    public EnemySprite(String name) {
        /* block comment [block:"EnemySprite_block_68" text:"Runs the super action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_68" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
        super(name);
        /* block end [id:"EnemySprite_block_68"] */
        /* block comment [block:"EnemySprite_block_1" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_1" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (minimapSprite != /* param start [id:"EnemySprite_value_291" type:"value"] */null/* param end [id:"EnemySprite_value_291"] */) {
            /* block comment [block:"EnemySprite_block_69" text:"Runs the add costume action. Values in this block are editable inputs."] */
            /* block start [id:"EnemySprite_block_69" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
            minimapSprite.addCostume(new Costume(/* param start [id:"EnemySprite_value_292" type:"text"] */"enemy_dot"/* param end [id:"EnemySprite_value_292"] */, /* param start [id:"EnemySprite_value_293" type:"text"] */"jscratch_ws/assets/npc_dot.png"/* param end [id:"EnemySprite_value_293"] */, /* param start [id:"EnemySprite_value_294" type:"number"] */5/* param end [id:"EnemySprite_value_294"] */, /* param start [id:"EnemySprite_value_295" type:"number"] */5/* param end [id:"EnemySprite_value_295"] */));
            /* block end [id:"EnemySprite_block_69"] */
            /* block comment [block:"EnemySprite_block_70" text:"Runs the switch costume action. Values in this block are editable inputs."] */
            /* block start [id:"EnemySprite_block_70" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
            minimapSprite.switchCostume(/* param start [id:"EnemySprite_value_296" type:"text"] */"enemy_dot"/* param end [id:"EnemySprite_value_296"] */);
            /* block end [id:"EnemySprite_block_70"] */
        }
        /* block end [id:"EnemySprite_block_1"] */
        // Random swarm angle offset so they don't all stack
        /* block comment [block:"EnemySprite_block_71" text:"Runs the random action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_71" category:"My Blocks" label:"random" shape:"command" docs:"Runs the random action. Values in this block are editable inputs."] */
        this.swarmAngle = Math.random() * Math.PI * /* param start [id:"EnemySprite_value_297" type:"number"] */2/* param end [id:"EnemySprite_value_297"] */;
        /* block end [id:"EnemySprite_block_71"] */
        /* block comment [block:"EnemySprite_block_72" text:"Runs the hpbar action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_72" category:"My Blocks" label:"hpbar" shape:"command" docs:"Runs the hpbar action. Values in this block are editable inputs."] */
        hpBar = new HPBAR();
        /* block end [id:"EnemySprite_block_72"] */
        /* block comment [block:"EnemySprite_block_73" text:"Runs the current time millis action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_73" category:"My Blocks" label:"current time millis" shape:"command" docs:"Runs the current time millis action. Values in this block are editable inputs."] */
        stateSince = System.currentTimeMillis();
        /* block end [id:"EnemySprite_block_73"] */
    }

    // ---------- role hooks (subtypes override) ----------

    /** Whether this type ever kites at low HP (zombies/slimes/bosses don't). */
    protected boolean canRetreat() { /* block comment [block:"EnemySprite_block_74" text:"Returns a value or ends the current method."] */
        /* block start [id:"EnemySprite_block_74" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return /* param start [id:"EnemySprite_value_298" type:"boolean"] */true/* param end [id:"EnemySprite_value_298"] */;
        /* block end [id:"EnemySprite_block_74"] */ }

    /** HP fraction that triggers a kite (0.30 = below 30%). */
    protected double retreatHpFrac() { /* block comment [block:"EnemySprite_block_75" text:"Returns a value or ends the current method."] */
        /* block start [id:"EnemySprite_block_75" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return /* param start [id:"EnemySprite_value_299" type:"number"] */0.30/* param end [id:"EnemySprite_value_299"] */;
        /* block end [id:"EnemySprite_block_75"] */ }

    /** Whether this type orbits at close range instead of shoving in. */
    protected boolean canSwarm() { /* block comment [block:"EnemySprite_block_76" text:"Returns a value or ends the current method."] */
        /* block start [id:"EnemySprite_block_76" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return /* param start [id:"EnemySprite_value_300" type:"boolean"] */true/* param end [id:"EnemySprite_value_300"] */;
        /* block end [id:"EnemySprite_block_76"] */ }

    /** Orbit distance for SWARM state: inside striking range so the pack
    *  hugs the target and ATTACK engages on arrival instead of circling
    *  far away. */
    protected int swarmDist() { /* block comment [block:"EnemySprite_block_77" text:"Returns a value or ends the current method."] */
        /* block start [id:"EnemySprite_block_77" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return attackRange - /* param start [id:"EnemySprite_value_301" type:"number"] */8/* param end [id:"EnemySprite_value_301"] */;
        /* block end [id:"EnemySprite_block_77"] */ }
    /** Beyond this the enemy gives up and wanders home. */
    protected int leashRange() { /* block comment [block:"EnemySprite_block_78" text:"Returns a value or ends the current method."] */
        /* block start [id:"EnemySprite_block_78" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return visionRange * /* param start [id:"EnemySprite_value_302" type:"number"] */2/* param end [id:"EnemySprite_value_302"] */;
        /* block end [id:"EnemySprite_block_78"] */ }

    /** Post-strike hook (ghouls use it for hit-and-run). */
    protected void onLandedStrike(boolean hit) { }

    /** Start a kiting retreat (also usable by subtype hooks). */
    protected void startRetreat(long now, long ms) {
        /* block comment [block:"EnemySprite_block_79" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"EnemySprite_block_79" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        retreatUntil = now + ms;
        /* block end [id:"EnemySprite_block_79"] */
        /* block comment [block:"EnemySprite_block_80" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"EnemySprite_block_80" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        retreatCdUntil = now + ms + /* param start [id:"EnemySprite_value_303" type:"number"] */5500/* param end [id:"EnemySprite_value_303"] */;
        /* block end [id:"EnemySprite_block_80"] */
        /* block comment [block:"EnemySprite_block_81" text:"Runs the set state action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_81" category:"Variables" label:"set state" shape:"command" docs:"Runs the set state action. Values in this block are editable inputs."] */
        setState(AIState.RETREAT, now);
        /* block end [id:"EnemySprite_block_81"] */
    }

    private void setState(AIState next, long now) {
        /* block comment [block:"EnemySprite_block_2" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_2" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (currentState != next) {
            /* block comment [block:"EnemySprite_block_82" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"EnemySprite_block_82" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            currentState = next;
            /* block end [id:"EnemySprite_block_82"] */
            /* block comment [block:"EnemySprite_block_83" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"EnemySprite_block_83" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            stateSince = now;
            /* block end [id:"EnemySprite_block_83"] */
        }
        /* block end [id:"EnemySprite_block_2"] */
    }

    // ---------- targeting ----------

    protected void acquireTarget(int playerX, int playerY) {
        /* block comment [block:"EnemySprite_block_3" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_3" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (retargetCd-- > /* param start [id:"EnemySprite_value_304" type:"number"] */0/* param end [id:"EnemySprite_value_304"] */) {
            /* block comment [block:"EnemySprite_block_84" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"EnemySprite_block_84" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"EnemySprite_block_4" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"EnemySprite_block_4" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (targetImp == /* param start [id:"EnemySprite_value_305" type:"value"] */null/* param end [id:"EnemySprite_value_305"] */ || !targetImp.isVisible()) targetImp = /* param start [id:"EnemySprite_value_306" type:"value"] */null/* param end [id:"EnemySprite_value_306"] */;
            /* block end [id:"EnemySprite_block_84"] */
            /* block comment [block:"EnemySprite_block_85" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"EnemySprite_block_85" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            else return;
            /* block end [id:"EnemySprite_block_85"] */
            /* block end [id:"EnemySprite_block_4"] */
        }
        /* block end [id:"EnemySprite_block_3"] */
        /* block comment [block:"EnemySprite_block_86" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"EnemySprite_block_86" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        retargetCd = /* param start [id:"EnemySprite_value_307" type:"number"] */30/* param end [id:"EnemySprite_value_307"] */;
        /* block end [id:"EnemySprite_block_86"] */ // re-evaluate ~2x per second
        /* block comment [block:"EnemySprite_block_87" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"EnemySprite_block_87" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        targetImp = /* param start [id:"EnemySprite_value_308" type:"value"] */null/* param end [id:"EnemySprite_value_308"] */;
        /* block end [id:"EnemySprite_block_87"] */
        /* block comment [block:"EnemySprite_block_88" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_88" category:"Variables" label:"set SummonedImp" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Spellbook.SummonedImp imp = Spellbook.nearestImp(xx, yy, visionRange);
        /* block end [id:"EnemySprite_block_88"] */
        /* block comment [block:"EnemySprite_block_5" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_5" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (imp != /* param start [id:"EnemySprite_value_309" type:"value"] */null/* param end [id:"EnemySprite_value_309"] */) {
            /* block comment [block:"EnemySprite_block_89" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"EnemySprite_block_89" category:"Variables" label:"set dix" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double dix = imp.xx - xx;
            /* block end [id:"EnemySprite_block_89"] */
            /* block comment [block:"EnemySprite_block_90" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"EnemySprite_block_90" category:"Variables" label:"set diy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double diy = imp.yy - yy;
            /* block end [id:"EnemySprite_block_90"] */
            /* block comment [block:"EnemySprite_block_91" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"EnemySprite_block_91" category:"Variables" label:"set dpx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double dpx = playerX - xx;
            /* block end [id:"EnemySprite_block_91"] */
            /* block comment [block:"EnemySprite_block_92" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"EnemySprite_block_92" category:"Variables" label:"set dpy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double dpy = playerY - yy;
            /* block end [id:"EnemySprite_block_92"] */
            /* block comment [block:"EnemySprite_block_93" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"EnemySprite_block_93" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"EnemySprite_block_6" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"EnemySprite_block_6" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (dix * dix + diy * diy < dpx * dpx + dpy * dpy) targetImp = imp;
            /* block end [id:"EnemySprite_block_93"] */
            /* block end [id:"EnemySprite_block_6"] */
        }
        /* block end [id:"EnemySprite_block_5"] */
    }

    protected int targetX(int playerX) {
        /* block comment [block:"EnemySprite_block_94" text:"Returns a value or ends the current method."] */
        /* block start [id:"EnemySprite_block_94" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return (targetImp != /* param start [id:"EnemySprite_value_310" type:"value"] */null/* param end [id:"EnemySprite_value_310"] */ && targetImp.isVisible()) ? targetImp.xx : playerX;
        /* block end [id:"EnemySprite_block_94"] */
    }

    protected int targetY(int playerY) {
        /* block comment [block:"EnemySprite_block_95" text:"Returns a value or ends the current method."] */
        /* block start [id:"EnemySprite_block_95" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return (targetImp != /* param start [id:"EnemySprite_value_311" type:"value"] */null/* param end [id:"EnemySprite_value_311"] */ && targetImp.isVisible()) ? targetImp.yy : playerY;
        /* block end [id:"EnemySprite_block_95"] */
    }

    // ---------- state ----------

    public void selectState(double playerDist) {
        /* block comment [block:"EnemySprite_block_96" text:"Calls the selectState method using its declared arguments."] */
        /* block start [id:"EnemySprite_block_96" category:"My Blocks" label:"call select state" shape:"command" docs:"Calls the selectState method using its declared arguments."] */
        selectState(distToPlayer, playerDist, System.currentTimeMillis());
        /* block end [id:"EnemySprite_block_96"] */
    }

    private void selectState(double targetDist, double playerDist, long now) {
        // Mid-swing: never cancel the telegraph (attacks always resolve).
        /* block comment [block:"EnemySprite_block_7" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_7" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (windingUp) {
            /* block comment [block:"EnemySprite_block_97" text:"Runs the set state action. Values in this block are editable inputs."] */
            /* block start [id:"EnemySprite_block_97" category:"Variables" label:"set state" shape:"command" docs:"Runs the set state action. Values in this block are editable inputs."] */
            setState(AIState.ATTACK, stateSince == /* param start [id:"EnemySprite_value_312" type:"number"] */0/* param end [id:"EnemySprite_value_312"] */ ? now : stateSince);
            /* block end [id:"EnemySprite_block_97"] */
            /* block comment [block:"EnemySprite_block_98" text:"Returns a value or ends the current method."] */
            /* block start [id:"EnemySprite_block_98" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"EnemySprite_block_98"] */
        }
        /* block end [id:"EnemySprite_block_7"] */
        /* block comment [block:"EnemySprite_block_99" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_99" category:"Variables" label:"set minTime" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        boolean minTime = now - stateSince >= MIN_STATE_MS;
        /* block end [id:"EnemySprite_block_99"] */

        // 1. Strikes are immediate in both directions.
        /* block comment [block:"EnemySprite_block_8" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_8" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (targetDist <= attackRange) {
            /* block comment [block:"EnemySprite_block_100" text:"Runs the set state action. Values in this block are editable inputs."] */
            /* block start [id:"EnemySprite_block_100" category:"Variables" label:"set state" shape:"command" docs:"Runs the set state action. Values in this block are editable inputs."] */
            setState(AIState.ATTACK, now);
            /* block end [id:"EnemySprite_block_100"] */
            /* block comment [block:"EnemySprite_block_101" text:"Returns a value or ends the current method."] */
            /* block start [id:"EnemySprite_block_101" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"EnemySprite_block_101"] */
        }
        /* block end [id:"EnemySprite_block_8"] */
        // Leaving ATTACK still respects the floor so hit-and-run reads.
        /* block comment [block:"EnemySprite_block_102" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_102" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"EnemySprite_block_9" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_9" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (currentState == AIState.ATTACK && !minTime) return;
        /* block end [id:"EnemySprite_block_102"] */
        /* block end [id:"EnemySprite_block_9"] */

        // 2. Active kite continues until its timer ends.
        /* block comment [block:"EnemySprite_block_10" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_10" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (now < retreatUntil) {
            /* block comment [block:"EnemySprite_block_103" text:"Runs the set state action. Values in this block are editable inputs."] */
            /* block start [id:"EnemySprite_block_103" category:"Variables" label:"set state" shape:"command" docs:"Runs the set state action. Values in this block are editable inputs."] */
            setState(AIState.RETREAT, now);
            /* block end [id:"EnemySprite_block_103"] */
            /* block comment [block:"EnemySprite_block_104" text:"Returns a value or ends the current method."] */
            /* block start [id:"EnemySprite_block_104" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"EnemySprite_block_104"] */
        }
        /* block end [id:"EnemySprite_block_10"] */
        // 3. Fresh kite: wounded, enemy close, cooldown elapsed.
        /* block comment [block:"EnemySprite_block_11" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_11" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (canRetreat() && maxHealth > /* param start [id:"EnemySprite_value_313" type:"number"] */0/* param end [id:"EnemySprite_value_313"] */
        && (double) health / (double) maxHealth < retreatHpFrac()
        && targetDist < /* param start [id:"EnemySprite_value_314" type:"number"] */260/* param end [id:"EnemySprite_value_314"] */ && now >= retreatCdUntil) {
            /* block comment [block:"EnemySprite_block_105" text:"Calls the startRetreat method using its declared arguments."] */
            /* block start [id:"EnemySprite_block_105" category:"My Blocks" label:"call start retreat" shape:"command" docs:"Calls the startRetreat method using its declared arguments."] */
            startRetreat(now, /* param start [id:"EnemySprite_value_315" type:"number"] */1500/* param end [id:"EnemySprite_value_315"] */);
            /* block end [id:"EnemySprite_block_105"] */
            /* block comment [block:"EnemySprite_block_106" text:"Returns a value or ends the current method."] */
            /* block start [id:"EnemySprite_block_106" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"EnemySprite_block_106"] */
        }
        /* block end [id:"EnemySprite_block_11"] */

        /* block comment [block:"EnemySprite_block_107" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_107" category:"Variables" label:"set want" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        AIState want;
        /* block end [id:"EnemySprite_block_107"] */
        // 4. Orbit at knife range (hysteresis band stops boundary buzz).
        /* block comment [block:"EnemySprite_block_12" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_12" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (canSwarm()
        && (targetDist < /* param start [id:"EnemySprite_value_316" type:"number"] */140/* param end [id:"EnemySprite_value_316"] */
        || (currentState == AIState.SWARM && targetDist < /* param start [id:"EnemySprite_value_317" type:"number"] */170/* param end [id:"EnemySprite_value_317"] */))) {
            /* block comment [block:"EnemySprite_block_108" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"EnemySprite_block_108" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            want = AIState.SWARM;
            /* block end [id:"EnemySprite_block_108"] */
        } else /* block comment [block:"EnemySprite_block_13" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_13" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!isAware(playerDist, now)) {
            /* block comment [block:"EnemySprite_block_109" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"EnemySprite_block_109" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            want = AIState.WANDER;
            /* block end [id:"EnemySprite_block_109"] */
        }
        /* block end [id:"EnemySprite_block_12"] */ else {
            /* block comment [block:"EnemySprite_block_110" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"EnemySprite_block_110" category:"Variables" label:"set ally" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            EnemySprite ally = findAllyInNeed(now);
            /* block end [id:"EnemySprite_block_110"] */
            /* block comment [block:"EnemySprite_block_111" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"EnemySprite_block_111" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            want = (ally != /* param start [id:"EnemySprite_value_318" type:"value"] */null/* param end [id:"EnemySprite_value_318"] */) ? AIState.ASSIST : AIState.PURSUE;
            /* block end [id:"EnemySprite_block_111"] */
        }
        /* block end [id:"EnemySprite_block_13"] */
        /* block comment [block:"EnemySprite_block_112" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_112" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"EnemySprite_block_14" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_14" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (want == currentState || minTime) setState(want, now);
        /* block end [id:"EnemySprite_block_112"] */
        /* block end [id:"EnemySprite_block_14"] */
    }

    private boolean isAware(double playerDist, long now) {
        /* block comment [block:"EnemySprite_block_113" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_113" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"EnemySprite_block_15" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_15" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (targetImp != /* param start [id:"EnemySprite_value_319" type:"value"] */null/* param end [id:"EnemySprite_value_319"] */) return /* param start [id:"EnemySprite_value_320" type:"boolean"] */true/* param end [id:"EnemySprite_value_320"] */;
        /* block end [id:"EnemySprite_block_113"] */
        /* block end [id:"EnemySprite_block_15"] */
        /* block comment [block:"EnemySprite_block_114" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_114" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"EnemySprite_block_16" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_16" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (playerDist <= visionRange) return /* param start [id:"EnemySprite_value_321" type:"boolean"] */true/* param end [id:"EnemySprite_value_321"] */;
        /* block end [id:"EnemySprite_block_114"] */
        /* block end [id:"EnemySprite_block_16"] */
        /* block comment [block:"EnemySprite_block_115" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_115" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"EnemySprite_block_17" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_17" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (now < aggroUntil) return /* param start [id:"EnemySprite_value_322" type:"boolean"] */true/* param end [id:"EnemySprite_value_322"] */;
        /* block end [id:"EnemySprite_block_115"] */
        /* block end [id:"EnemySprite_block_17"] */ // recently hurt: knows where
        /* block comment [block:"EnemySprite_block_116" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_116" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"EnemySprite_block_18" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_18" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (playerDist > leashRange()) return /* param start [id:"EnemySprite_value_323" type:"boolean"] */false/* param end [id:"EnemySprite_value_323"] */;
        /* block end [id:"EnemySprite_block_116"] */
        /* block end [id:"EnemySprite_block_18"] */
        /* block comment [block:"EnemySprite_block_117" text:"Returns a value or ends the current method."] */
        /* block start [id:"EnemySprite_block_117" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return playerDist <= visionRange;
        /* block end [id:"EnemySprite_block_117"] */
    }

    public void hpUpdate() {
        /* block comment [block:"EnemySprite_block_118" text:"Calls the update method using its declared arguments."] */
        /* block start [id:"EnemySprite_block_118" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
        hpBar.update(health, maxHealth, xx, yy + /* param start [id:"EnemySprite_value_324" type:"number"] */90/* param end [id:"EnemySprite_value_324"] */);
        /* block end [id:"EnemySprite_block_118"] */
    }

    public void think(int playerX, int playerY) {
        /* block comment [block:"EnemySprite_block_119" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_119" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"EnemySprite_block_19" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_19" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!isVisible() || GameState.isPaused()) return;
        /* block end [id:"EnemySprite_block_119"] */
        /* block end [id:"EnemySprite_block_19"] */

        /* block comment [block:"EnemySprite_block_120" text:"Calls the hpUpdate method using its declared arguments."] */
        /* block start [id:"EnemySprite_block_120" category:"My Blocks" label:"call hp update" shape:"command" docs:"Calls the hpUpdate method using its declared arguments."] */
        hpUpdate();
        /* block end [id:"EnemySprite_block_120"] */
        /* block comment [block:"EnemySprite_block_121" text:"Calls the acquireTarget method using its declared arguments."] */
        /* block start [id:"EnemySprite_block_121" category:"My Blocks" label:"call acquire target" shape:"command" docs:"Calls the acquireTarget method using its declared arguments."] */
        acquireTarget(playerX, playerY);
        /* block end [id:"EnemySprite_block_121"] */
        /* block comment [block:"EnemySprite_block_122" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_122" category:"Variables" label:"set tx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int tx = targetX(playerX);
        /* block end [id:"EnemySprite_block_122"] */
        /* block comment [block:"EnemySprite_block_123" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_123" category:"Variables" label:"set ty" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int ty = targetY(playerY);
        /* block end [id:"EnemySprite_block_123"] */
        /* block comment [block:"EnemySprite_block_124" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"EnemySprite_block_124" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        lastTx = tx;
        /* block end [id:"EnemySprite_block_124"] */
        /* block comment [block:"EnemySprite_block_125" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"EnemySprite_block_125" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        lastTy = ty;
        /* block end [id:"EnemySprite_block_125"] */
        /* block comment [block:"EnemySprite_block_126" text:"Runs the get distance action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_126" category:"Sensing" label:"get distance" shape:"command" docs:"Runs the get distance action. Values in this block are editable inputs."] */
        distToPlayer = getDistance(xx, yy, tx, ty);
        /* block end [id:"EnemySprite_block_126"] */
        /* block comment [block:"EnemySprite_block_127" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_127" category:"Variables" label:"set pdx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double pdx = playerX - xx;
        /* block end [id:"EnemySprite_block_127"] */
        /* block comment [block:"EnemySprite_block_128" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_128" category:"Variables" label:"set pdy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double pdy = playerY - yy;
        /* block end [id:"EnemySprite_block_128"] */
        /* block comment [block:"EnemySprite_block_129" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_129" category:"Variables" label:"set playerDist" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double playerDist = Math.sqrt(pdx * pdx + pdy * pdy);
        /* block end [id:"EnemySprite_block_129"] */

        // 1. State Selection Logic
        /* block comment [block:"EnemySprite_block_130" text:"Calls the selectState method using its declared arguments."] */
        /* block start [id:"EnemySprite_block_130" category:"My Blocks" label:"call select state" shape:"command" docs:"Calls the selectState method using its declared arguments."] */
        selectState(distToPlayer, playerDist, System.currentTimeMillis());
        /* block end [id:"EnemySprite_block_130"] */
        // 2. State Execution
        /* block comment [block:"EnemySprite_block_20" text:"Selects and runs the case that matches the input value."] */
        /* block start [id:"EnemySprite_block_20" category:"Control" label:"choose a case" shape:"control" docs:"Selects and runs the case that matches the input value."] */
        switch (currentState) {
            /* block comment [block:"EnemySprite_block_131" text:"Calls the pursue method using its declared arguments."] */
            /* block start [id:"EnemySprite_block_131" category:"My Blocks" label:"call pursue" shape:"command" docs:"Calls the pursue method using its declared arguments."] */
            case PURSUE:
            pursue(tx, ty);
            /* block end [id:"EnemySprite_block_131"] */
            /* block comment [block:"EnemySprite_block_132" text:"Stops the nearest loop or switch block."] */
            /* block start [id:"EnemySprite_block_132" category:"Control" label:"break loop" shape:"command" docs:"Stops the nearest loop or switch block."] */
            break;
            /* block end [id:"EnemySprite_block_132"] */
            /* block comment [block:"EnemySprite_block_133" text:"Runs the move towards action. Values in this block are editable inputs."] */
            /* block start [id:"EnemySprite_block_133" category:"Motion" label:"move towards" shape:"command" docs:"Runs the move towards action. Values in this block are editable inputs."] */
            case RETREAT:
            moveTowards(tx, ty, -speed);
            /* block end [id:"EnemySprite_block_133"] */ // Move away
            /* block comment [block:"EnemySprite_block_134" text:"Stops the nearest loop or switch block."] */
            /* block start [id:"EnemySprite_block_134" category:"Control" label:"break loop" shape:"command" docs:"Stops the nearest loop or switch block."] */
            break;
            /* block end [id:"EnemySprite_block_134"] */
            /* block comment [block:"EnemySprite_block_135" text:"Calls the performAttack method using its declared arguments."] */
            /* block start [id:"EnemySprite_block_135" category:"My Blocks" label:"call perform attack" shape:"command" docs:"Calls the performAttack method using its declared arguments."] */
            case ATTACK:
            performAttack(tx, ty);
            /* block end [id:"EnemySprite_block_135"] */
            /* block comment [block:"EnemySprite_block_136" text:"Stops the nearest loop or switch block."] */
            /* block start [id:"EnemySprite_block_136" category:"Control" label:"break loop" shape:"command" docs:"Stops the nearest loop or switch block."] */
            break;
            /* block end [id:"EnemySprite_block_136"] */
            /* block comment [block:"EnemySprite_block_137" text:"Calls the executeSwarm method using its declared arguments."] */
            /* block start [id:"EnemySprite_block_137" category:"My Blocks" label:"call execute swarm" shape:"command" docs:"Calls the executeSwarm method using its declared arguments."] */
            case SWARM:
            executeSwarm(tx, ty);
            /* block end [id:"EnemySprite_block_137"] */
            /* block comment [block:"EnemySprite_block_138" text:"Stops the nearest loop or switch block."] */
            /* block start [id:"EnemySprite_block_138" category:"Control" label:"break loop" shape:"command" docs:"Stops the nearest loop or switch block."] */
            break;
            /* block end [id:"EnemySprite_block_138"] */
            /* block comment [block:"EnemySprite_block_139" text:"Calls the executeAssist method using its declared arguments."] */
            /* block start [id:"EnemySprite_block_139" category:"My Blocks" label:"call execute assist" shape:"command" docs:"Calls the executeAssist method using its declared arguments."] */
            case ASSIST:
            executeAssist(tx, ty);
            /* block end [id:"EnemySprite_block_139"] */
            /* block comment [block:"EnemySprite_block_140" text:"Stops the nearest loop or switch block."] */
            /* block start [id:"EnemySprite_block_140" category:"Control" label:"break loop" shape:"command" docs:"Stops the nearest loop or switch block."] */
            break;
            /* block end [id:"EnemySprite_block_140"] */
            /* block comment [block:"EnemySprite_block_141" text:"Calls the executeWander method using its declared arguments."] */
            /* block start [id:"EnemySprite_block_141" category:"My Blocks" label:"call execute wander" shape:"command" docs:"Calls the executeWander method using its declared arguments."] */
            case WANDER:
            executeWander();
            /* block end [id:"EnemySprite_block_141"] */
            /* block comment [block:"EnemySprite_block_142" text:"Stops the nearest loop or switch block."] */
            /* block start [id:"EnemySprite_block_142" category:"Control" label:"break loop" shape:"command" docs:"Stops the nearest loop or switch block."] */
            break;
            /* block end [id:"EnemySprite_block_142"] */
        }
        /* block end [id:"EnemySprite_block_20"] */

        /* block comment [block:"EnemySprite_block_143" text:"Calls the separate method using its declared arguments."] */
        /* block start [id:"EnemySprite_block_143" category:"My Blocks" label:"call separate" shape:"command" docs:"Calls the separate method using its declared arguments."] */
        separate();
        /* block end [id:"EnemySprite_block_143"] */
        /* block comment [block:"EnemySprite_block_144" text:"Calls the update method using its declared arguments."] */
        /* block start [id:"EnemySprite_block_144" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
        update();
        /* block end [id:"EnemySprite_block_144"] */
    }

    // ---------- movement ----------

    protected void faceTowards(int tx, int ty) {
        /* block comment [block:"EnemySprite_block_145" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_145" category:"Variables" label:"set angle" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double angle = Math.atan2(ty - yy, tx - xx);
        /* block end [id:"EnemySprite_block_145"] */
        /* block comment [block:"EnemySprite_block_146" text:"Runs the point in direction action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_146" category:"Motion" label:"point in direction" shape:"command" docs:"Runs the point in direction action. Values in this block are editable inputs."] */
        pointInDirection(/* param start [id:"EnemySprite_value_325" type:"number"] */90/* param end [id:"EnemySprite_value_325"] */ - Math.toDegrees(angle));
        /* block end [id:"EnemySprite_block_146"] */
    }

    /** Slide-aware step toward (or away from, with negative speed) a point.
    *  Holds still inside a 2px dead zone and never overshoots the target,
    *  so arrivals settle instead of vibrating. */
    protected boolean moveTowards(int targetX, int targetY, int moveSpeed) {
        /* block comment [block:"EnemySprite_block_147" text:"Calls the faceTowards method using its declared arguments."] */
        /* block start [id:"EnemySprite_block_147" category:"My Blocks" label:"call face towards" shape:"command" docs:"Calls the faceTowards method using its declared arguments."] */
        faceTowards(targetX, targetY);
        /* block end [id:"EnemySprite_block_147"] */
        /* block comment [block:"EnemySprite_block_148" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_148" category:"Variables" label:"set dx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double dx = targetX - xx;
        /* block end [id:"EnemySprite_block_148"] */
        /* block comment [block:"EnemySprite_block_149" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_149" category:"Variables" label:"set dy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double dy = targetY - yy;
        /* block end [id:"EnemySprite_block_149"] */
        /* block comment [block:"EnemySprite_block_150" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_150" category:"Variables" label:"set dist" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double dist = Math.sqrt(dx * dx + dy * dy);
        /* block end [id:"EnemySprite_block_150"] */
        /* block comment [block:"EnemySprite_block_151" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_151" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"EnemySprite_block_21" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_21" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (dist < /* param start [id:"EnemySprite_value_326" type:"number"] */2/* param end [id:"EnemySprite_value_326"] */ && moveSpeed >= /* param start [id:"EnemySprite_value_327" type:"number"] */0/* param end [id:"EnemySprite_value_327"] */) return /* param start [id:"EnemySprite_value_328" type:"boolean"] */true/* param end [id:"EnemySprite_value_328"] */;
        /* block end [id:"EnemySprite_block_151"] */
        /* block end [id:"EnemySprite_block_21"] */ // arrived: hold (not when fleeing)
        /* block comment [block:"EnemySprite_block_152" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_152" category:"Variables" label:"set stepX" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int stepX, stepY;
        /* block end [id:"EnemySprite_block_152"] */
        /* block comment [block:"EnemySprite_block_22" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_22" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (dist <= Math.abs(moveSpeed)) {
            // Final approach: exact delta, no overshoot
            /* block comment [block:"EnemySprite_block_153" text:"Runs the round action. Values in this block are editable inputs."] */
            /* block start [id:"EnemySprite_block_153" category:"My Blocks" label:"round" shape:"command" docs:"Runs the round action. Values in this block are editable inputs."] */
            stepX = (int) Math.round(dx);
            /* block end [id:"EnemySprite_block_153"] */
            /* block comment [block:"EnemySprite_block_154" text:"Runs the round action. Values in this block are editable inputs."] */
            /* block start [id:"EnemySprite_block_154" category:"My Blocks" label:"round" shape:"command" docs:"Runs the round action. Values in this block are editable inputs."] */
            stepY = (int) Math.round(dy);
            /* block end [id:"EnemySprite_block_154"] */
        } else {
            /* block comment [block:"EnemySprite_block_155" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"EnemySprite_block_155" category:"Variables" label:"set angle" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double angle = Math.atan2(dy, dx);
            /* block end [id:"EnemySprite_block_155"] */
            /* block comment [block:"EnemySprite_block_156" text:"Runs the cos action. Values in this block are editable inputs."] */
            /* block start [id:"EnemySprite_block_156" category:"My Blocks" label:"cos" shape:"command" docs:"Runs the cos action. Values in this block are editable inputs."] */
            stepX = (int) (Math.cos(angle) * moveSpeed);
            /* block end [id:"EnemySprite_block_156"] */
            /* block comment [block:"EnemySprite_block_157" text:"Runs the sin action. Values in this block are editable inputs."] */
            /* block start [id:"EnemySprite_block_157" category:"My Blocks" label:"sin" shape:"command" docs:"Runs the sin action. Values in this block are editable inputs."] */
            stepY = (int) (Math.sin(angle) * moveSpeed);
            /* block end [id:"EnemySprite_block_157"] */
        }
        /* block end [id:"EnemySprite_block_22"] */
        /* block comment [block:"EnemySprite_block_23" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_23" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (stepX == /* param start [id:"EnemySprite_value_329" type:"number"] */0/* param end [id:"EnemySprite_value_329"] */ && stepY == /* param start [id:"EnemySprite_value_330" type:"number"] */0/* param end [id:"EnemySprite_value_330"] */ && moveSpeed != /* param start [id:"EnemySprite_value_331" type:"number"] */0/* param end [id:"EnemySprite_value_331"] */) {
            // Rounding killed the step at close range: nudge one cell
            /* block comment [block:"EnemySprite_block_158" text:"Runs the compare action. Values in this block are editable inputs."] */
            /* block start [id:"EnemySprite_block_158" category:"My Blocks" label:"compare" shape:"command" docs:"Runs the compare action. Values in this block are editable inputs."] */
            stepX = Integer.compare(targetX, xx);
            /* block end [id:"EnemySprite_block_158"] */
            /* block comment [block:"EnemySprite_block_159" text:"Runs the compare action. Values in this block are editable inputs."] */
            /* block start [id:"EnemySprite_block_159" category:"My Blocks" label:"compare" shape:"command" docs:"Runs the compare action. Values in this block are editable inputs."] */
            stepY = Integer.compare(targetY, yy);
            /* block end [id:"EnemySprite_block_159"] */
        }
        /* block end [id:"EnemySprite_block_23"] */
        /* block comment [block:"EnemySprite_block_160" text:"Returns a value or ends the current method."] */
        /* block start [id:"EnemySprite_block_160" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return tryMove(stepX, stepY);
        /* block end [id:"EnemySprite_block_160"] */
    }

    /** Pursuit with unstick detours when walls pin us. */
    protected void pursue(int tx, int ty) {
        /* block comment [block:"EnemySprite_block_161" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_161" category:"Variables" label:"set now" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        long now = System.currentTimeMillis();
        /* block end [id:"EnemySprite_block_161"] */
        /* block comment [block:"EnemySprite_block_24" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_24" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (now < detourUntil) {
            /* block comment [block:"EnemySprite_block_25" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"EnemySprite_block_25" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (!tryMove(detourDx, detourDy) && now - lastDetourFlip > /* param start [id:"EnemySprite_value_332" type:"number"] */300/* param end [id:"EnemySprite_value_332"] */) {
                // Flip direction at most 3x/sec: faster flips just vibrate.
                /* block comment [block:"EnemySprite_block_162" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"EnemySprite_block_162" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                lastDetourFlip = now;
                /* block end [id:"EnemySprite_block_162"] */
                /* block comment [block:"EnemySprite_block_163" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"EnemySprite_block_163" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                detourDx = -detourDx;
                /* block end [id:"EnemySprite_block_163"] */
                /* block comment [block:"EnemySprite_block_164" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"EnemySprite_block_164" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                detourDy = -detourDy;
                /* block end [id:"EnemySprite_block_164"] */
            }
            /* block end [id:"EnemySprite_block_25"] */
            /* block comment [block:"EnemySprite_block_165" text:"Returns a value or ends the current method."] */
            /* block start [id:"EnemySprite_block_165" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"EnemySprite_block_165"] */
        }
        /* block end [id:"EnemySprite_block_24"] */
        /* block comment [block:"EnemySprite_block_166" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_166" category:"Variables" label:"set ox" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int ox = xx;
        /* block end [id:"EnemySprite_block_166"] */
        /* block comment [block:"EnemySprite_block_167" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_167" category:"Variables" label:"set oy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int oy = yy;
        /* block end [id:"EnemySprite_block_167"] */
        /* block comment [block:"EnemySprite_block_168" text:"Runs the move towards action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_168" category:"Motion" label:"move towards" shape:"command" docs:"Runs the move towards action. Values in this block are editable inputs."] */
        moveTowards(tx, ty, speed);
        /* block end [id:"EnemySprite_block_168"] */
        /* block comment [block:"EnemySprite_block_169" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_169" category:"Variables" label:"set pdx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double pdx = tx - xx;
        /* block end [id:"EnemySprite_block_169"] */
        /* block comment [block:"EnemySprite_block_170" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_170" category:"Variables" label:"set pdy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double pdy = ty - yy;
        /* block end [id:"EnemySprite_block_170"] */
        /* block comment [block:"EnemySprite_block_26" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_26" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (xx == ox && oy == yy && pdx * pdx + pdy * pdy > (speed + /* param start [id:"EnemySprite_value_333" type:"number"] */2/* param end [id:"EnemySprite_value_333"] */) * (speed + /* param start [id:"EnemySprite_value_334" type:"number"] */2/* param end [id:"EnemySprite_value_334"] */)) {
            /* block comment [block:"EnemySprite_block_27" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"EnemySprite_block_27" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (++stillFrames > /* param start [id:"EnemySprite_value_335" type:"number"] */40/* param end [id:"EnemySprite_value_335"] */) {
                /* block comment [block:"EnemySprite_block_171" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"EnemySprite_block_171" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                stillFrames = /* param start [id:"EnemySprite_value_336" type:"number"] */0/* param end [id:"EnemySprite_value_336"] */;
                /* block end [id:"EnemySprite_block_171"] */
                /* block comment [block:"EnemySprite_block_172" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"EnemySprite_block_172" category:"Variables" label:"set a" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                double a = Math.atan2(ty - yy, tx - xx)
                + (Math.random() < /* param start [id:"EnemySprite_value_337" type:"number"] */0.5/* param end [id:"EnemySprite_value_337"] */ ? Math.PI / /* param start [id:"EnemySprite_value_338" type:"number"] */2/* param end [id:"EnemySprite_value_338"] */ : -Math.PI / /* param start [id:"EnemySprite_value_339" type:"number"] */2/* param end [id:"EnemySprite_value_339"] */);
                /* block end [id:"EnemySprite_block_172"] */
                /* block comment [block:"EnemySprite_block_173" text:"Runs the cos action. Values in this block are editable inputs."] */
                /* block start [id:"EnemySprite_block_173" category:"My Blocks" label:"cos" shape:"command" docs:"Runs the cos action. Values in this block are editable inputs."] */
                detourDx = (int) (Math.cos(a) * Math.max(/* param start [id:"EnemySprite_value_340" type:"number"] */1/* param end [id:"EnemySprite_value_340"] */, speed));
                /* block end [id:"EnemySprite_block_173"] */
                /* block comment [block:"EnemySprite_block_174" text:"Runs the sin action. Values in this block are editable inputs."] */
                /* block start [id:"EnemySprite_block_174" category:"My Blocks" label:"sin" shape:"command" docs:"Runs the sin action. Values in this block are editable inputs."] */
                detourDy = (int) (Math.sin(a) * Math.max(/* param start [id:"EnemySprite_value_341" type:"number"] */1/* param end [id:"EnemySprite_value_341"] */, speed));
                /* block end [id:"EnemySprite_block_174"] */
                /* block comment [block:"EnemySprite_block_175" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"EnemySprite_block_175" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                detourUntil = now + /* param start [id:"EnemySprite_value_342" type:"number"] */1200/* param end [id:"EnemySprite_value_342"] */;
                /* block end [id:"EnemySprite_block_175"] */
            }
            /* block end [id:"EnemySprite_block_27"] */
        } else {
            /* block comment [block:"EnemySprite_block_176" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"EnemySprite_block_176" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            stillFrames = /* param start [id:"EnemySprite_value_343" type:"number"] */0/* param end [id:"EnemySprite_value_343"] */;
            /* block end [id:"EnemySprite_block_176"] */
        }
        /* block end [id:"EnemySprite_block_26"] */
    }

    protected void executeSwarm(int tx, int ty) {
        // Circle around the target at role orbit distance
        /* block comment [block:"EnemySprite_block_177" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_177" category:"Variables" label:"set desiredDist" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int desiredDist = swarmDist();
        /* block end [id:"EnemySprite_block_177"] */
        /* block comment [block:"EnemySprite_block_178" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"EnemySprite_block_178" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        swarmAngle += /* param start [id:"EnemySprite_value_344" type:"number"] */0.02/* param end [id:"EnemySprite_value_344"] */;
        /* block end [id:"EnemySprite_block_178"] */ // Rotate slowly

        /* block comment [block:"EnemySprite_block_179" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_179" category:"Variables" label:"set orbitX" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int orbitX = tx + (int) (Math.cos(swarmAngle) * desiredDist);
        /* block end [id:"EnemySprite_block_179"] */
        /* block comment [block:"EnemySprite_block_180" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_180" category:"Variables" label:"set orbitY" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int orbitY = ty + (int) (Math.sin(swarmAngle) * desiredDist);
        /* block end [id:"EnemySprite_block_180"] */

        /* block comment [block:"EnemySprite_block_181" text:"Runs the move towards action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_181" category:"Motion" label:"move towards" shape:"command" docs:"Runs the move towards action. Values in this block are editable inputs."] */
        moveTowards(orbitX, orbitY, speed);
        /* block end [id:"EnemySprite_block_181"] */
    }

    protected void executeWander() {
        /* block comment [block:"EnemySprite_block_182" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_182" category:"Variables" label:"set now" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        long now = System.currentTimeMillis();
        /* block end [id:"EnemySprite_block_182"] */
        /* block comment [block:"EnemySprite_block_183" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_183" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"EnemySprite_block_28" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_28" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (now < wanderPauseUntil) return;
        /* block end [id:"EnemySprite_block_183"] */
        /* block end [id:"EnemySprite_block_28"] */
        /* block comment [block:"EnemySprite_block_29" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_29" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!hasWp || getDistance(xx, yy, wpx, wpy) < /* param start [id:"EnemySprite_value_345" type:"number"] */24/* param end [id:"EnemySprite_value_345"] */) {
            // Reached (or lost) the waypoint: pause briefly, then pick anew.
            /* block comment [block:"EnemySprite_block_184" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"EnemySprite_block_184" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"EnemySprite_block_30" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"EnemySprite_block_30" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (hasWp) wanderPauseUntil = now + /* param start [id:"EnemySprite_value_346" type:"number"] */800/* param end [id:"EnemySprite_value_346"] */;
            /* block end [id:"EnemySprite_block_184"] */
            /* block end [id:"EnemySprite_block_30"] */
            /* block comment [block:"EnemySprite_block_185" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"EnemySprite_block_185" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            hasWp = /* param start [id:"EnemySprite_value_347" type:"boolean"] */false/* param end [id:"EnemySprite_value_347"] */;
            /* block end [id:"EnemySprite_block_185"] */
            /* block comment [block:"EnemySprite_block_31" text:"Repeats the nested operations for each loop turn."] */
            /* block start [id:"EnemySprite_block_31" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
            for (int i = /* param start [id:"EnemySprite_value_348" type:"number"] */0/* param end [id:"EnemySprite_value_348"] */; i < /* param start [id:"EnemySprite_value_349" type:"number"] */6/* param end [id:"EnemySprite_value_349"] */ && !hasWp; i++) {
                /* block comment [block:"EnemySprite_block_186" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"EnemySprite_block_186" category:"Variables" label:"set nx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int nx = xx + (int) (Math.random() * /* param start [id:"EnemySprite_value_350" type:"number"] */440/* param end [id:"EnemySprite_value_350"] */ - /* param start [id:"EnemySprite_value_351" type:"number"] */220/* param end [id:"EnemySprite_value_351"] */);
                /* block end [id:"EnemySprite_block_186"] */
                /* block comment [block:"EnemySprite_block_187" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"EnemySprite_block_187" category:"Variables" label:"set ny" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int ny = yy + (int) (Math.random() * /* param start [id:"EnemySprite_value_352" type:"number"] */440/* param end [id:"EnemySprite_value_352"] */ - /* param start [id:"EnemySprite_value_353" type:"number"] */220/* param end [id:"EnemySprite_value_353"] */);
                /* block end [id:"EnemySprite_block_187"] */
                /* block comment [block:"EnemySprite_block_32" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"EnemySprite_block_32" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (!willCollide(nx, ny)) {
                    /* block comment [block:"EnemySprite_block_188" text:"Evaluates this expression and applies its result."] */
                    /* block start [id:"EnemySprite_block_188" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                    wpx = nx;
                    /* block end [id:"EnemySprite_block_188"] */
                    /* block comment [block:"EnemySprite_block_189" text:"Evaluates this expression and applies its result."] */
                    /* block start [id:"EnemySprite_block_189" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                    wpy = ny;
                    /* block end [id:"EnemySprite_block_189"] */
                    /* block comment [block:"EnemySprite_block_190" text:"Evaluates this expression and applies its result."] */
                    /* block start [id:"EnemySprite_block_190" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                    hasWp = /* param start [id:"EnemySprite_value_354" type:"boolean"] */true/* param end [id:"EnemySprite_value_354"] */;
                    /* block end [id:"EnemySprite_block_190"] */
                }
                /* block end [id:"EnemySprite_block_32"] */
            }
            /* block end [id:"EnemySprite_block_31"] */
            /* block comment [block:"EnemySprite_block_33" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"EnemySprite_block_33" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (!hasWp) {
                /* block comment [block:"EnemySprite_block_191" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"EnemySprite_block_191" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                wanderPauseUntil = now + /* param start [id:"EnemySprite_value_355" type:"number"] */800/* param end [id:"EnemySprite_value_355"] */;
                /* block end [id:"EnemySprite_block_191"] */
                /* block comment [block:"EnemySprite_block_192" text:"Returns a value or ends the current method."] */
                /* block start [id:"EnemySprite_block_192" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
                return;
                /* block end [id:"EnemySprite_block_192"] */
            }
            /* block end [id:"EnemySprite_block_33"] */
        }
        /* block end [id:"EnemySprite_block_29"] */
        /* block comment [block:"EnemySprite_block_193" text:"Runs the move towards action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_193" category:"Motion" label:"move towards" shape:"command" docs:"Runs the move towards action. Values in this block are editable inputs."] */
        moveTowards(wpx, wpy, Math.max(/* param start [id:"EnemySprite_value_356" type:"number"] */1/* param end [id:"EnemySprite_value_356"] */, speed / /* param start [id:"EnemySprite_value_357" type:"number"] */2/* param end [id:"EnemySprite_value_357"] */));
        /* block end [id:"EnemySprite_block_193"] */
    }

    protected void executeAssist(int tx, int ty) {
        /* block comment [block:"EnemySprite_block_194" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_194" category:"Variables" label:"set ally" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        EnemySprite ally = cachedAlly;
        /* block end [id:"EnemySprite_block_194"] */
        /* block comment [block:"EnemySprite_block_34" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_34" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (ally == /* param start [id:"EnemySprite_value_358" type:"value"] */null/* param end [id:"EnemySprite_value_358"] */ || !ally.isVisible()) {
            /* block comment [block:"EnemySprite_block_195" text:"Runs the find ally in need action. Values in this block are editable inputs."] */
            /* block start [id:"EnemySprite_block_195" category:"My Blocks" label:"find ally in need" shape:"command" docs:"Runs the find ally in need action. Values in this block are editable inputs."] */
            ally = findAllyInNeed(System.currentTimeMillis());
            /* block end [id:"EnemySprite_block_195"] */
            /* block comment [block:"EnemySprite_block_196" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"EnemySprite_block_196" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            cachedAlly = ally;
            /* block end [id:"EnemySprite_block_196"] */
        }
        /* block end [id:"EnemySprite_block_34"] */
        /* block comment [block:"EnemySprite_block_35" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_35" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (ally == /* param start [id:"EnemySprite_value_359" type:"value"] */null/* param end [id:"EnemySprite_value_359"] */) {
            /* block comment [block:"EnemySprite_block_197" text:"Calls the pursue method using its declared arguments."] */
            /* block start [id:"EnemySprite_block_197" category:"My Blocks" label:"call pursue" shape:"command" docs:"Calls the pursue method using its declared arguments."] */
            pursue(tx, ty);
            /* block end [id:"EnemySprite_block_197"] */
            /* block comment [block:"EnemySprite_block_198" text:"Returns a value or ends the current method."] */
            /* block start [id:"EnemySprite_block_198" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"EnemySprite_block_198"] */
        }
        /* block end [id:"EnemySprite_block_35"] */
        /* block comment [block:"EnemySprite_block_36" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_36" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (ally.currentState == AIState.ATTACK || ally.windingUp) {
            // Join the ally's fight, not the ally's feet.
            /* block comment [block:"EnemySprite_block_199" text:"Calls the pursue method using its declared arguments."] */
            /* block start [id:"EnemySprite_block_199" category:"My Blocks" label:"call pursue" shape:"command" docs:"Calls the pursue method using its declared arguments."] */
            pursue(ally.lastTx, ally.lastTy);
            /* block end [id:"EnemySprite_block_199"] */
        } else {
            /* block comment [block:"EnemySprite_block_200" text:"Calls the pursue method using its declared arguments."] */
            /* block start [id:"EnemySprite_block_200" category:"My Blocks" label:"call pursue" shape:"command" docs:"Calls the pursue method using its declared arguments."] */
            pursue(ally.xx, ally.yy);
            /* block end [id:"EnemySprite_block_200"] */
        }
        /* block end [id:"EnemySprite_block_36"] */
    }

    /** Separation steering so packs don't stack. Gentle by design: small
    *  radius, 1px pushes, and attackers hold still mid-wind-up. */
    protected void separate() {
        /* block comment [block:"EnemySprite_block_201" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_201" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"EnemySprite_block_37" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_37" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (windingUp) return;
        /* block end [id:"EnemySprite_block_201"] */
        /* block end [id:"EnemySprite_block_37"] */
        /* block comment [block:"EnemySprite_block_202" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_202" category:"Variables" label:"set EnemySprite" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        List<EnemySprite> foes = EnemyManager.getInstance().getEnemies();
        /* block end [id:"EnemySprite_block_202"] */
        /* block comment [block:"EnemySprite_block_38" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"EnemySprite_block_38" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (int i = foes.size() - /* param start [id:"EnemySprite_value_360" type:"number"] */1/* param end [id:"EnemySprite_value_360"] */; i >= /* param start [id:"EnemySprite_value_361" type:"number"] */0/* param end [id:"EnemySprite_value_361"] */; i--) {
            /* block comment [block:"EnemySprite_block_203" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"EnemySprite_block_203" category:"Variables" label:"set o" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            EnemySprite o = foes.get(i);
            /* block end [id:"EnemySprite_block_203"] */
            /* block comment [block:"EnemySprite_block_204" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"EnemySprite_block_204" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"EnemySprite_block_39" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"EnemySprite_block_39" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (o == this || !o.isVisible()) continue;
            /* block end [id:"EnemySprite_block_204"] */
            /* block end [id:"EnemySprite_block_39"] */
            /* block comment [block:"EnemySprite_block_205" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"EnemySprite_block_205" category:"Variables" label:"set push" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int push = bodyRadius + o.bodyRadius + /* param start [id:"EnemySprite_value_362" type:"number"] */2/* param end [id:"EnemySprite_value_362"] */;
            /* block end [id:"EnemySprite_block_205"] */
            /* block comment [block:"EnemySprite_block_206" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"EnemySprite_block_206" category:"Variables" label:"set dx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int dx = xx - o.xx;
            /* block end [id:"EnemySprite_block_206"] */
            /* block comment [block:"EnemySprite_block_207" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"EnemySprite_block_207" category:"Variables" label:"set dy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int dy = yy - o.yy;
            /* block end [id:"EnemySprite_block_207"] */
            /* block comment [block:"EnemySprite_block_208" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"EnemySprite_block_208" category:"Variables" label:"set d2" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int d2 = dx * dx + dy * dy;
            /* block end [id:"EnemySprite_block_208"] */
            /* block comment [block:"EnemySprite_block_40" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"EnemySprite_block_40" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (d2 > /* param start [id:"EnemySprite_value_363" type:"number"] */0/* param end [id:"EnemySprite_value_363"] */ && d2 < push * push) {
                /* block comment [block:"EnemySprite_block_209" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"EnemySprite_block_209" category:"Variables" label:"set d" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                double d = Math.sqrt(d2);
                /* block end [id:"EnemySprite_block_209"] */
                /* block comment [block:"EnemySprite_block_210" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"EnemySprite_block_210" category:"Variables" label:"set sx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int sx = (int) Math.round(dx / d);
                /* block end [id:"EnemySprite_block_210"] */
                /* block comment [block:"EnemySprite_block_211" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"EnemySprite_block_211" category:"Variables" label:"set sy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int sy = (int) Math.round(dy / d);
                /* block end [id:"EnemySprite_block_211"] */
                /* block comment [block:"EnemySprite_block_212" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"EnemySprite_block_212" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"EnemySprite_block_41" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"EnemySprite_block_41" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (sx == /* param start [id:"EnemySprite_value_364" type:"number"] */0/* param end [id:"EnemySprite_value_364"] */ && sy == /* param start [id:"EnemySprite_value_365" type:"number"] */0/* param end [id:"EnemySprite_value_365"] */) sx = /* param start [id:"EnemySprite_value_366" type:"number"] */1/* param end [id:"EnemySprite_value_366"] */;
                /* block end [id:"EnemySprite_block_212"] */
                /* block end [id:"EnemySprite_block_41"] */
                /* block comment [block:"EnemySprite_block_213" text:"Runs the try move action. Values in this block are editable inputs."] */
                /* block start [id:"EnemySprite_block_213" category:"Motion" label:"try move" shape:"command" docs:"Runs the try move action. Values in this block are editable inputs."] */
                tryMove(sx, sy);
                /* block end [id:"EnemySprite_block_213"] */
            }
            /* block end [id:"EnemySprite_block_40"] */
        }
        /* block end [id:"EnemySprite_block_38"] */
        // Don't pile onto the target either: hold outside half body radius.
        /* block comment [block:"EnemySprite_block_214" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_214" category:"Variables" label:"set tdx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double tdx = lastTx - xx;
        /* block end [id:"EnemySprite_block_214"] */
        /* block comment [block:"EnemySprite_block_215" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_215" category:"Variables" label:"set tdy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double tdy = lastTy - yy;
        /* block end [id:"EnemySprite_block_215"] */
        /* block comment [block:"EnemySprite_block_216" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_216" category:"Variables" label:"set minD" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double minD = Math.max(/* param start [id:"EnemySprite_value_367" type:"number"] */6/* param end [id:"EnemySprite_value_367"] */, bodyRadius / /* param start [id:"EnemySprite_value_368" type:"number"] */2/* param end [id:"EnemySprite_value_368"] */);
        /* block end [id:"EnemySprite_block_216"] */
        /* block comment [block:"EnemySprite_block_42" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_42" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (tdx * tdx + tdy * tdy < minD * minD && currentState != AIState.ATTACK) {
            /* block comment [block:"EnemySprite_block_217" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"EnemySprite_block_217" category:"Variables" label:"set d" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double d = Math.sqrt(tdx * tdx + tdy * tdy);
            /* block end [id:"EnemySprite_block_217"] */
            /* block comment [block:"EnemySprite_block_43" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"EnemySprite_block_43" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (d > /* param start [id:"EnemySprite_value_369" type:"number"] */0.5/* param end [id:"EnemySprite_value_369"] */) {
                /* block comment [block:"EnemySprite_block_218" text:"Runs the try move action. Values in this block are editable inputs."] */
                /* block start [id:"EnemySprite_block_218" category:"Motion" label:"try move" shape:"command" docs:"Runs the try move action. Values in this block are editable inputs."] */
                tryMove((int) Math.round(-tdx / d), (int) Math.round(-tdy / d));
                /* block end [id:"EnemySprite_block_218"] */
            }
            /* block end [id:"EnemySprite_block_43"] */
        }
        /* block end [id:"EnemySprite_block_42"] */
    }

    // ---------- attacks ----------

    /**
    * Telegraphed melee: first call starts the wind-up (subtype visuals),
    * the strike lands windupMs later if the target is still in reach.
    */
    protected void performAttack(int tx, int ty) {
        /* block comment [block:"EnemySprite_block_219" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_219" category:"Variables" label:"set now" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        long now = System.currentTimeMillis();
        /* block end [id:"EnemySprite_block_219"] */
        /* block comment [block:"EnemySprite_block_220" text:"Calls the faceTowards method using its declared arguments."] */
        /* block start [id:"EnemySprite_block_220" category:"My Blocks" label:"call face towards" shape:"command" docs:"Calls the faceTowards method using its declared arguments."] */
        faceTowards(tx, ty);
        /* block end [id:"EnemySprite_block_220"] */
        /* block comment [block:"EnemySprite_block_44" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_44" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (windingUp) {
            /* block comment [block:"EnemySprite_block_45" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"EnemySprite_block_45" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (now >= windupUntil) {
                /* block comment [block:"EnemySprite_block_221" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"EnemySprite_block_221" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                windingUp = /* param start [id:"EnemySprite_value_370" type:"boolean"] */false/* param end [id:"EnemySprite_value_370"] */;
                /* block end [id:"EnemySprite_block_221"] */
                /* block comment [block:"EnemySprite_block_222" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"EnemySprite_block_222" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                lastAttackMs = now;
                /* block end [id:"EnemySprite_block_222"] */
                /* block comment [block:"EnemySprite_block_223" text:"Calls the landStrike method using its declared arguments."] */
                /* block start [id:"EnemySprite_block_223" category:"My Blocks" label:"call land strike" shape:"command" docs:"Calls the landStrike method using its declared arguments."] */
                landStrike(tx, ty);
                /* block end [id:"EnemySprite_block_223"] */
                /* block comment [block:"EnemySprite_block_224" text:"Returns a value or ends the current method."] */
                /* block start [id:"EnemySprite_block_224" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
                return;
                /* block end [id:"EnemySprite_block_224"] */
            }
            /* block end [id:"EnemySprite_block_45"] */
            // Track through the telegraph: a stationary wind-up always
            // whiffs against a moving target (player outruns the reach).
            /* block comment [block:"EnemySprite_block_225" text:"Runs the move towards action. Values in this block are editable inputs."] */
            /* block start [id:"EnemySprite_block_225" category:"Motion" label:"move towards" shape:"command" docs:"Runs the move towards action. Values in this block are editable inputs."] */
            moveTowards(tx, ty, speed);
            /* block end [id:"EnemySprite_block_225"] */
            /* block comment [block:"EnemySprite_block_226" text:"Returns a value or ends the current method."] */
            /* block start [id:"EnemySprite_block_226" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"EnemySprite_block_226"] */
        }
        /* block end [id:"EnemySprite_block_44"] */
        /* block comment [block:"EnemySprite_block_227" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_227" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"EnemySprite_block_46" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_46" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (now - lastAttackMs < attackCooldownMs) return;
        /* block end [id:"EnemySprite_block_227"] */
        /* block end [id:"EnemySprite_block_46"] */
        /* block comment [block:"EnemySprite_block_228" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_228" category:"Variables" label:"set dx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double dx = tx - xx;
        /* block end [id:"EnemySprite_block_228"] */
        /* block comment [block:"EnemySprite_block_229" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_229" category:"Variables" label:"set dy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double dy = ty - yy;
        /* block end [id:"EnemySprite_block_229"] */
        /* block comment [block:"EnemySprite_block_230" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_230" category:"Variables" label:"set reach" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double reach = attackRange + /* param start [id:"EnemySprite_value_371" type:"number"] */10/* param end [id:"EnemySprite_value_371"] */;
        /* block end [id:"EnemySprite_block_230"] */
        /* block comment [block:"EnemySprite_block_231" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_231" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"EnemySprite_block_47" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_47" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (dx * dx + dy * dy > reach * reach) return;
        /* block end [id:"EnemySprite_block_231"] */
        /* block end [id:"EnemySprite_block_47"] */
        /* block comment [block:"EnemySprite_block_232" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"EnemySprite_block_232" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        windingUp = /* param start [id:"EnemySprite_value_372" type:"boolean"] */true/* param end [id:"EnemySprite_value_372"] */;
        /* block end [id:"EnemySprite_block_232"] */
        /* block comment [block:"EnemySprite_block_233" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"EnemySprite_block_233" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        windupUntil = now + windupMs;
        /* block end [id:"EnemySprite_block_233"] */
        /* block comment [block:"EnemySprite_block_234" text:"Calls the attackTelegraph method using its declared arguments."] */
        /* block start [id:"EnemySprite_block_234" category:"My Blocks" label:"call attack telegraph" shape:"command" docs:"Calls the attackTelegraph method using its declared arguments."] */
        attackTelegraph(tx, ty);
        /* block end [id:"EnemySprite_block_234"] */
    }

    /** Subtype hook: visuals / step-in at wind-up start. */
    protected void attackTelegraph(int tx, int ty) {
        /* block comment [block:"EnemySprite_block_235" text:"Runs the move towards action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_235" category:"Motion" label:"move towards" shape:"command" docs:"Runs the move towards action. Values in this block are editable inputs."] */
        moveTowards(tx, ty, speed);
        /* block end [id:"EnemySprite_block_235"] */
    }

    protected void landStrike(int tx, int ty) {
        /* block comment [block:"EnemySprite_block_48" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_48" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!PlayerStats.ready || GameState.isPaused()) { /* block comment [block:"EnemySprite_block_236" text:"Calls the onLandedStrike method using its declared arguments."] */
            /* block start [id:"EnemySprite_block_236" category:"My Blocks" label:"call on landed strike" shape:"command" docs:"Calls the onLandedStrike method using its declared arguments."] */
            onLandedStrike(/* param start [id:"EnemySprite_value_373" type:"boolean"] */false/* param end [id:"EnemySprite_value_373"] */);
            /* block end [id:"EnemySprite_block_236"] */ /* block comment [block:"EnemySprite_block_237" text:"Returns a value or ends the current method."] */
            /* block start [id:"EnemySprite_block_237" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"EnemySprite_block_237"] */ }
        /* block end [id:"EnemySprite_block_48"] */
        /* block comment [block:"EnemySprite_block_238" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_238" category:"Variables" label:"set dx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double dx = tx - xx;
        /* block end [id:"EnemySprite_block_238"] */
        /* block comment [block:"EnemySprite_block_239" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_239" category:"Variables" label:"set dy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double dy = ty - yy;
        /* block end [id:"EnemySprite_block_239"] */
        /* block comment [block:"EnemySprite_block_240" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_240" category:"Variables" label:"set reach" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double reach = attackRange + /* param start [id:"EnemySprite_value_374" type:"number"] */15/* param end [id:"EnemySprite_value_374"] */;
        /* block end [id:"EnemySprite_block_240"] */
        /* block comment [block:"EnemySprite_block_49" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_49" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (dx * dx + dy * dy > reach * reach) { /* block comment [block:"EnemySprite_block_241" text:"Calls the onLandedStrike method using its declared arguments."] */
            /* block start [id:"EnemySprite_block_241" category:"My Blocks" label:"call on landed strike" shape:"command" docs:"Calls the onLandedStrike method using its declared arguments."] */
            onLandedStrike(/* param start [id:"EnemySprite_value_375" type:"boolean"] */false/* param end [id:"EnemySprite_value_375"] */);
            /* block end [id:"EnemySprite_block_241"] */ /* block comment [block:"EnemySprite_block_242" text:"Returns a value or ends the current method."] */
            /* block start [id:"EnemySprite_block_242" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"EnemySprite_block_242"] */ }
        /* block end [id:"EnemySprite_block_49"] */ // whiffed
        /* block comment [block:"EnemySprite_block_243" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_243" category:"Variables" label:"set impTarget" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        boolean impTarget = targetImp != /* param start [id:"EnemySprite_value_376" type:"value"] */null/* param end [id:"EnemySprite_value_376"] */ && targetImp.isVisible()
        && Math.abs(targetImp.xx - tx) < /* param start [id:"EnemySprite_value_377" type:"number"] */4/* param end [id:"EnemySprite_value_377"] */ && Math.abs(targetImp.yy - ty) < /* param start [id:"EnemySprite_value_378" type:"number"] */4/* param end [id:"EnemySprite_value_378"] */;
        /* block end [id:"EnemySprite_block_243"] */
        /* block comment [block:"EnemySprite_block_244" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_244" category:"Variables" label:"set targetAC" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int targetAC = impTarget ? /* param start [id:"EnemySprite_value_379" type:"number"] */12/* param end [id:"EnemySprite_value_379"] */ : PlayerStats.effectiveAC();
        /* block end [id:"EnemySprite_block_244"] */
        /* block comment [block:"EnemySprite_block_245" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_245" category:"Variables" label:"set res" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int res = Combat.meleeStrike(attackBonus, targetAC);
        /* block end [id:"EnemySprite_block_245"] */
        /* block comment [block:"EnemySprite_block_50" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_50" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (res <= /* param start [id:"EnemySprite_value_380" type:"number"] */0/* param end [id:"EnemySprite_value_380"] */) { /* block comment [block:"EnemySprite_block_246" text:"Calls the onLandedStrike method using its declared arguments."] */
            /* block start [id:"EnemySprite_block_246" category:"My Blocks" label:"call on landed strike" shape:"command" docs:"Calls the onLandedStrike method using its declared arguments."] */
            onLandedStrike(/* param start [id:"EnemySprite_value_381" type:"boolean"] */false/* param end [id:"EnemySprite_value_381"] */);
            /* block end [id:"EnemySprite_block_246"] */ /* block comment [block:"EnemySprite_block_247" text:"Returns a value or ends the current method."] */
            /* block start [id:"EnemySprite_block_247" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"EnemySprite_block_247"] */ }
        /* block end [id:"EnemySprite_block_50"] */
        /* block comment [block:"EnemySprite_block_248" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_248" category:"Variables" label:"set dmg" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int dmg = Combat.rollDamage(damageDie, damageMod, res == /* param start [id:"EnemySprite_value_382" type:"number"] */2/* param end [id:"EnemySprite_value_382"] */);
        /* block end [id:"EnemySprite_block_248"] */
        /* block comment [block:"EnemySprite_block_51" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_51" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (impTarget) {
            /* block comment [block:"EnemySprite_block_249" text:"Calls the takeDamage method using its declared arguments."] */
            /* block start [id:"EnemySprite_block_249" category:"My Blocks" label:"call take damage" shape:"command" docs:"Calls the takeDamage method using its declared arguments."] */
            targetImp.takeDamage(dmg);
            /* block end [id:"EnemySprite_block_249"] */
            /* block comment [block:"EnemySprite_block_250" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"EnemySprite_block_250" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"EnemySprite_block_52" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"EnemySprite_block_52" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (!targetImp.isVisible()) targetImp = /* param start [id:"EnemySprite_value_383" type:"value"] */null/* param end [id:"EnemySprite_value_383"] */;
            /* block end [id:"EnemySprite_block_250"] */
            /* block end [id:"EnemySprite_block_52"] */
        } else {
            /* block comment [block:"EnemySprite_block_53" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"EnemySprite_block_53" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (PlayerStats.stats == /* param start [id:"EnemySprite_value_384" type:"value"] */null/* param end [id:"EnemySprite_value_384"] */ || PlayerStats.stats.hp <= /* param start [id:"EnemySprite_value_385" type:"number"] */0/* param end [id:"EnemySprite_value_385"] */) { /* block comment [block:"EnemySprite_block_251" text:"Calls the onLandedStrike method using its declared arguments."] */
                /* block start [id:"EnemySprite_block_251" category:"My Blocks" label:"call on landed strike" shape:"command" docs:"Calls the onLandedStrike method using its declared arguments."] */
                onLandedStrike(/* param start [id:"EnemySprite_value_386" type:"boolean"] */true/* param end [id:"EnemySprite_value_386"] */);
                /* block end [id:"EnemySprite_block_251"] */ /* block comment [block:"EnemySprite_block_252" text:"Returns a value or ends the current method."] */
                /* block start [id:"EnemySprite_block_252" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
                return;
                /* block end [id:"EnemySprite_block_252"] */ }
            /* block end [id:"EnemySprite_block_53"] */
            /* block comment [block:"EnemySprite_block_253" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"EnemySprite_block_253" category:"Variables" label:"set alive" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            boolean alive = PlayerStats.takeDamage(dmg);
            /* block end [id:"EnemySprite_block_253"] */
            /* block comment [block:"EnemySprite_block_254" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"EnemySprite_block_254" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"EnemySprite_block_54" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"EnemySprite_block_54" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (!alive) Stage.getInstance().broadcast(/* param start [id:"EnemySprite_value_387" type:"text"] */"playerDied"/* param end [id:"EnemySprite_value_387"] */);
            /* block end [id:"EnemySprite_block_254"] */
            /* block end [id:"EnemySprite_block_54"] */
        }
        /* block end [id:"EnemySprite_block_51"] */
        /* block comment [block:"EnemySprite_block_255" text:"Calls the onLandedStrike method using its declared arguments."] */
        /* block start [id:"EnemySprite_block_255" category:"My Blocks" label:"call on landed strike" shape:"command" docs:"Calls the onLandedStrike method using its declared arguments."] */
        onLandedStrike(/* param start [id:"EnemySprite_value_388" type:"boolean"] */true/* param end [id:"EnemySprite_value_388"] */);
        /* block end [id:"EnemySprite_block_255"] */
    }

    /** Throttled ally scan over the enemy list (not all world sprites). */
    protected EnemySprite findAllyInNeed() {
        /* block comment [block:"EnemySprite_block_256" text:"Returns a value or ends the current method."] */
        /* block start [id:"EnemySprite_block_256" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return findAllyInNeed(System.currentTimeMillis());
        /* block end [id:"EnemySprite_block_256"] */
    }

    private EnemySprite findAllyInNeed(long now) {
        /* block comment [block:"EnemySprite_block_55" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_55" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (now < assistRecheck) {
            /* block comment [block:"EnemySprite_block_257" text:"Returns a value or ends the current method."] */
            /* block start [id:"EnemySprite_block_257" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return (cachedAlly != /* param start [id:"EnemySprite_value_389" type:"value"] */null/* param end [id:"EnemySprite_value_389"] */ && cachedAlly.isVisible()) ? cachedAlly : /* param start [id:"EnemySprite_value_390" type:"value"] */null/* param end [id:"EnemySprite_value_390"] */;
            /* block end [id:"EnemySprite_block_257"] */
        }
        /* block end [id:"EnemySprite_block_55"] */
        /* block comment [block:"EnemySprite_block_258" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"EnemySprite_block_258" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        assistRecheck = now + /* param start [id:"EnemySprite_value_391" type:"number"] */500/* param end [id:"EnemySprite_value_391"] */;
        /* block end [id:"EnemySprite_block_258"] */
        /* block comment [block:"EnemySprite_block_259" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"EnemySprite_block_259" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        cachedAlly = /* param start [id:"EnemySprite_value_392" type:"value"] */null/* param end [id:"EnemySprite_value_392"] */;
        /* block end [id:"EnemySprite_block_259"] */
        /* block comment [block:"EnemySprite_block_260" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_260" category:"Variables" label:"set EnemySprite" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        List<EnemySprite> foes = EnemyManager.getInstance().getEnemies();
        /* block end [id:"EnemySprite_block_260"] */
        /* block comment [block:"EnemySprite_block_261" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_261" category:"Variables" label:"set best" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        EnemySprite best = /* param start [id:"EnemySprite_value_393" type:"value"] */null/* param end [id:"EnemySprite_value_393"] */;
        /* block end [id:"EnemySprite_block_261"] */
        /* block comment [block:"EnemySprite_block_262" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_262" category:"Variables" label:"set bestD2" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double bestD2 = Double.MAX_VALUE;
        /* block end [id:"EnemySprite_block_262"] */
        /* block comment [block:"EnemySprite_block_56" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"EnemySprite_block_56" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (int i = foes.size() - /* param start [id:"EnemySprite_value_394" type:"number"] */1/* param end [id:"EnemySprite_value_394"] */; i >= /* param start [id:"EnemySprite_value_395" type:"number"] */0/* param end [id:"EnemySprite_value_395"] */; i--) {
            /* block comment [block:"EnemySprite_block_263" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"EnemySprite_block_263" category:"Variables" label:"set ally" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            EnemySprite ally = foes.get(i);
            /* block end [id:"EnemySprite_block_263"] */
            /* block comment [block:"EnemySprite_block_264" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"EnemySprite_block_264" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"EnemySprite_block_57" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"EnemySprite_block_57" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (ally == this || !ally.isVisible()) continue;
            /* block end [id:"EnemySprite_block_264"] */
            /* block end [id:"EnemySprite_block_57"] */
            // Assist if ally is fighting or badly hurt
            /* block comment [block:"EnemySprite_block_265" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"EnemySprite_block_265" category:"Variables" label:"set needy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            boolean needy = ally.currentState == AIState.ATTACK || ally.windingUp
            || (ally.maxHealth > /* param start [id:"EnemySprite_value_396" type:"number"] */0/* param end [id:"EnemySprite_value_396"] */
            && (double) ally.health / (double) ally.maxHealth < /* param start [id:"EnemySprite_value_397" type:"number"] */0.5/* param end [id:"EnemySprite_value_397"] */);
            /* block end [id:"EnemySprite_block_265"] */
            /* block comment [block:"EnemySprite_block_266" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"EnemySprite_block_266" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"EnemySprite_block_58" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"EnemySprite_block_58" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (!needy) continue;
            /* block end [id:"EnemySprite_block_266"] */
            /* block end [id:"EnemySprite_block_58"] */
            /* block comment [block:"EnemySprite_block_267" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"EnemySprite_block_267" category:"Variables" label:"set dx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double dx = ally.xx - xx;
            /* block end [id:"EnemySprite_block_267"] */
            /* block comment [block:"EnemySprite_block_268" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"EnemySprite_block_268" category:"Variables" label:"set dy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double dy = ally.yy - yy;
            /* block end [id:"EnemySprite_block_268"] */
            /* block comment [block:"EnemySprite_block_269" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"EnemySprite_block_269" category:"Variables" label:"set d2" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double d2 = dx * dx + dy * dy;
            /* block end [id:"EnemySprite_block_269"] */
            /* block comment [block:"EnemySprite_block_59" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"EnemySprite_block_59" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (d2 < visionRange * visionRange && d2 < bestD2) {
                /* block comment [block:"EnemySprite_block_270" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"EnemySprite_block_270" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                bestD2 = d2;
                /* block end [id:"EnemySprite_block_270"] */
                /* block comment [block:"EnemySprite_block_271" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"EnemySprite_block_271" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                best = ally;
                /* block end [id:"EnemySprite_block_271"] */
            }
            /* block end [id:"EnemySprite_block_59"] */
        }
        /* block end [id:"EnemySprite_block_56"] */
        /* block comment [block:"EnemySprite_block_272" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"EnemySprite_block_272" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        cachedAlly = best;
        /* block end [id:"EnemySprite_block_272"] */
        /* block comment [block:"EnemySprite_block_273" text:"Returns a value or ends the current method."] */
        /* block start [id:"EnemySprite_block_273" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return best;
        /* block end [id:"EnemySprite_block_273"] */
    }

    protected double getDistance(int x1, int y1, int x2, int y2) {
        /* block comment [block:"EnemySprite_block_274" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_274" category:"Variables" label:"set dx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double dx = x2 - x1;
        /* block end [id:"EnemySprite_block_274"] */
        /* block comment [block:"EnemySprite_block_275" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_275" category:"Variables" label:"set dy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double dy = y2 - y1;
        /* block end [id:"EnemySprite_block_275"] */
        /* block comment [block:"EnemySprite_block_276" text:"Returns a value or ends the current method."] */
        /* block start [id:"EnemySprite_block_276" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return Math.sqrt(dx * dx + dy * dy);
        /* block end [id:"EnemySprite_block_276"] */
    }

    public void takeDamage(int amount) {
        /* block comment [block:"EnemySprite_block_277" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_277" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"EnemySprite_block_60" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_60" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!isVisible() || GameState.isPaused()) return;
        /* block end [id:"EnemySprite_block_277"] */
        /* block end [id:"EnemySprite_block_60"] */
        /* block comment [block:"EnemySprite_block_278" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"EnemySprite_block_278" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        health -= amount;
        /* block end [id:"EnemySprite_block_278"] */
        // Getting hit focuses the enemy even from outside vision.
        /* block comment [block:"EnemySprite_block_279" text:"Runs the current time millis action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_279" category:"My Blocks" label:"current time millis" shape:"command" docs:"Runs the current time millis action. Values in this block are editable inputs."] */
        aggroUntil = System.currentTimeMillis() + /* param start [id:"EnemySprite_value_398" type:"number"] */4000/* param end [id:"EnemySprite_value_398"] */;
        /* block end [id:"EnemySprite_block_279"] */
        // Interrupt wandering immediately so retaliation isn't vision-gated.
        /* block comment [block:"EnemySprite_block_280" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_280" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"EnemySprite_block_61" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_61" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (currentState == AIState.WANDER) setState(AIState.PURSUE, System.currentTimeMillis());
        /* block end [id:"EnemySprite_block_280"] */
        /* block end [id:"EnemySprite_block_61"] */
        /* block comment [block:"EnemySprite_block_281" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_281" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"EnemySprite_block_62" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_62" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (health <= /* param start [id:"EnemySprite_value_399" type:"number"] */0/* param end [id:"EnemySprite_value_399"] */) die();
        /* block end [id:"EnemySprite_block_281"] */
        /* block end [id:"EnemySprite_block_62"] */
    }

    public void die() {
        /* block comment [block:"EnemySprite_block_282" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_282" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"EnemySprite_block_63" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_63" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!isVisible()) return;
        /* block end [id:"EnemySprite_block_282"] */
        /* block end [id:"EnemySprite_block_63"] */
        /* block comment [block:"EnemySprite_block_64" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_64" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (PlayerStats.ready) {
            /* block comment [block:"EnemySprite_block_283" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"EnemySprite_block_283" category:"Variables" label:"set addXp" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            PlayerStats.addXp(xpValue);
            /* block end [id:"EnemySprite_block_283"] */
            /* block comment [block:"EnemySprite_block_284" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"EnemySprite_block_284" category:"Variables" label:"set addGold" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Inventory.addGold(Combat.rollDie(/* param start [id:"EnemySprite_value_400" type:"number"] */10/* param end [id:"EnemySprite_value_400"] */) + FloorManager.getInstance().getFloor() * /* param start [id:"EnemySprite_value_401" type:"number"] */3/* param end [id:"EnemySprite_value_401"] */);
            /* block end [id:"EnemySprite_block_284"] */
        }
        /* block end [id:"EnemySprite_block_64"] */
        /* block comment [block:"EnemySprite_block_285" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_285" category:"Variables" label:"set spawnAt" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        LootDrop.spawnAt(xx, yy);
        /* block end [id:"EnemySprite_block_285"] */
        /* block comment [block:"EnemySprite_block_286" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemySprite_block_286" category:"Variables" label:"set enemyCount" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        EnemyManager.enemyCount--;
        /* block end [id:"EnemySprite_block_286"] */
        /* block comment [block:"EnemySprite_block_287" text:"Runs the hide action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_287" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
        hide();
        /* block end [id:"EnemySprite_block_287"] */
        /* block comment [block:"EnemySprite_block_288" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_288" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"EnemySprite_block_65" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_65" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (hpBar != /* param start [id:"EnemySprite_value_402" type:"value"] */null/* param end [id:"EnemySprite_value_402"] */) hpBar.hide();
        /* block end [id:"EnemySprite_block_288"] */
        /* block end [id:"EnemySprite_block_65"] */
        /* block comment [block:"EnemySprite_block_289" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"EnemySprite_block_289" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"EnemySprite_block_66" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemySprite_block_66" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (minimapSprite != /* param start [id:"EnemySprite_value_403" type:"value"] */null/* param end [id:"EnemySprite_value_403"] */) minimapSprite.hide();
        /* block end [id:"EnemySprite_block_289"] */
        /* block end [id:"EnemySprite_block_66"] */
        /* block comment [block:"EnemySprite_block_67" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"EnemySprite_block_67" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try { /* block comment [block:"EnemySprite_block_290" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"EnemySprite_block_290" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Scroller2d.getInstance().unregister(this);
            /* block end [id:"EnemySprite_block_290"] */ }
        /* block end [id:"EnemySprite_block_67"] */ catch (Exception ignored) {}
    }
}
