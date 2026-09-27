package generated;

import com.jscratch.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
* Spawns and ticks all enemies. Owns the enemy list, spawn timer/positions,
* per-floor mix + stat scaling, and dead-entry reaping.
*
* <p>Call graph / interactions:
* <ul>
*   <li>Configured per floor by {@code FloorManager.applyFloor} via
*       {@link #configure(int, int[], double, int)}; scripted adds (boss)
*       via {@link #addEnemy}.</li>
*   <li>Ticked once per player frame from {@code Sprite1}:
*       {@link #update(int, int)} spawns (off-view, collision-checked) then
*       calls {@code EnemySprite.think} per live enemy.</li>
*   <li>Read by: {@code Sprite1.performAttack/melee}, {@code Spellbook}
*       projectiles/AoE/imp targeting, {@code SlashOfNails} arc,
*       {@code MusicDirector} aggro scan (via Sprite1).</li>
*   <li>List is copy-on-write: iterated on player, imp, and effect threads.</li>
* </ul>
*/
public class EnemyManager {
    private static EnemyManager instance;
    // Iterated every frame on the player thread and from imp/projectile
    // threads; mutated rarely (spawn/death). Snapshot semantics avoid CMEs.
    private List<EnemySprite> enemies = new java.util.concurrent.CopyOnWriteArrayList<>();
    private Random random = new Random();
    private long lastSpawnTime = 0;
    /** After a floor/run transition, let the player orient before new mobs appear. */
    private long spawnGraceUntil = 0;
    private int spawnInterval = 3000; // 3 seconds

    public static int enemyLimit = 1;
    public static int enemyCount = 0;

    // Per-floor configuration (type ids: 0 orc, 1 goblin, 2 zombie, 3 ghoul, 4 slime)
    private int[] mix = {1, 2};
    private double hpMultiplier = 1.0;
    private int damageBonus = 0;

    /** Reconfigure spawning for the current floor. */
    public void configure(int limit, int[] mix, double hpMultiplier, int damageBonus) {
        /* block comment [block:"EnemyManager_block_21" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"EnemyManager_block_21" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        enemyLimit = limit;
        /* block end [id:"EnemyManager_block_21"] */
        /* block comment [block:"EnemyManager_block_22" text:"Runs the clone action. Values in this block are editable inputs."] */
        /* block start [id:"EnemyManager_block_22" category:"My Blocks" label:"clone" shape:"command" docs:"Runs the clone action. Values in this block are editable inputs."] */
        this.mix = mix.clone();
        /* block end [id:"EnemyManager_block_22"] */
        /* block comment [block:"EnemyManager_block_23" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"EnemyManager_block_23" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.hpMultiplier = hpMultiplier;
        /* block end [id:"EnemyManager_block_23"] */
        /* block comment [block:"EnemyManager_block_24" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"EnemyManager_block_24" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.damageBonus = damageBonus;
        /* block end [id:"EnemyManager_block_24"] */
    }

    /** Prevent regular enemy spawns for the requested time after entering a floor/run. */
    public void startSpawnGrace(long durationMs) {
        /* block comment [block:"EnemyManager_block_25" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemyManager_block_25" category:"Variables" label:"set now" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        long now = System.currentTimeMillis();
        /* block end [id:"EnemyManager_block_25"] */
        /* block comment [block:"EnemyManager_block_26" text:"Runs the max action. Values in this block are editable inputs."] */
        /* block start [id:"EnemyManager_block_26" category:"My Blocks" label:"max" shape:"command" docs:"Runs the max action. Values in this block are editable inputs."] */
        spawnGraceUntil = now + Math.max(/* param start [id:"EnemyManager_value_87" type:"number"] */0/* param end [id:"EnemyManager_value_87"] */, durationMs);
        /* block end [id:"EnemyManager_block_26"] */
        /* block comment [block:"EnemyManager_block_27" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"EnemyManager_block_27" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        lastSpawnTime = now;
        /* block end [id:"EnemyManager_block_27"] */
    }

    /** Remove every active enemy (used on floor change and new-run reset). */
    public void clearAll() {
        /* block comment [block:"EnemyManager_block_1" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"EnemyManager_block_1" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (EnemySprite e : enemies) {
            /* block comment [block:"EnemyManager_block_28" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"EnemyManager_block_28" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"EnemyManager_block_2" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"EnemyManager_block_2" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (e.isVisible()) e.hide();
            /* block end [id:"EnemyManager_block_28"] */
            /* block end [id:"EnemyManager_block_2"] */
            /* block comment [block:"EnemyManager_block_29" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"EnemyManager_block_29" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"EnemyManager_block_3" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"EnemyManager_block_3" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (e.hpBar != /* param start [id:"EnemyManager_value_88" type:"value"] */null/* param end [id:"EnemyManager_value_88"] */) e.hpBar.hide();
            /* block end [id:"EnemyManager_block_29"] */
            /* block end [id:"EnemyManager_block_3"] */
            /* block comment [block:"EnemyManager_block_30" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"EnemyManager_block_30" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"EnemyManager_block_4" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"EnemyManager_block_4" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (e.minimapSprite != /* param start [id:"EnemyManager_value_89" type:"value"] */null/* param end [id:"EnemyManager_value_89"] */) e.minimapSprite.hide();
            /* block end [id:"EnemyManager_block_30"] */
            /* block end [id:"EnemyManager_block_4"] */
            /* block comment [block:"EnemyManager_block_5" text:"Runs work safely and handles matching errors in a catch branch."] */
            /* block start [id:"EnemyManager_block_5" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
            try { /* block comment [block:"EnemyManager_block_31" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"EnemyManager_block_31" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                Scroller2d.getInstance().unregister(e);
                /* block end [id:"EnemyManager_block_31"] */ }
            /* block end [id:"EnemyManager_block_5"] */ catch (Exception ignored) {}
        }
        /* block end [id:"EnemyManager_block_1"] */
        /* block comment [block:"EnemyManager_block_32" text:"Calls the clear method using its declared arguments."] */
        /* block start [id:"EnemyManager_block_32" category:"My Blocks" label:"call clear" shape:"command" docs:"Calls the clear method using its declared arguments."] */
        enemies.clear();
        /* block end [id:"EnemyManager_block_32"] */
        /* block comment [block:"EnemyManager_block_33" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"EnemyManager_block_33" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        enemyCount = /* param start [id:"EnemyManager_value_90" type:"number"] */0/* param end [id:"EnemyManager_value_90"] */;
        /* block end [id:"EnemyManager_block_33"] */
    }

    /** Spawn a specific enemy at a world position (bosses, scripted encounters). */
    public void addEnemy(EnemySprite e, int x, int y) {
        /* block comment [block:"EnemyManager_block_34" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"EnemyManager_block_34" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        e.health = (int) (e.maxHealth * hpMultiplier);
        /* block end [id:"EnemyManager_block_34"] */
        /* block comment [block:"EnemyManager_block_35" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"EnemyManager_block_35" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        e.maxHealth = e.health;
        /* block end [id:"EnemyManager_block_35"] */
        /* block comment [block:"EnemyManager_block_36" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"EnemyManager_block_36" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        e.damageMod += damageBonus;
        /* block end [id:"EnemyManager_block_36"] */
        /* block comment [block:"EnemyManager_block_37" text:"Runs the go to action. Values in this block are editable inputs."] */
        /* block start [id:"EnemyManager_block_37" category:"Motion" label:"go to" shape:"command" docs:"Runs the go to action. Values in this block are editable inputs."] */
        e.goTo(x, y);
        /* block end [id:"EnemyManager_block_37"] */
        /* block comment [block:"EnemyManager_block_38" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemyManager_block_38" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().addSprite(e);
        /* block end [id:"EnemyManager_block_38"] */
        /* block comment [block:"EnemyManager_block_6" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemyManager_block_6" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!GameState.isGameplay()) {
            /* block comment [block:"EnemyManager_block_39" text:"Runs the hide action. Values in this block are editable inputs."] */
            /* block start [id:"EnemyManager_block_39" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
            e.hide();
            /* block end [id:"EnemyManager_block_39"] */
            /* block comment [block:"EnemyManager_block_40" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"EnemyManager_block_40" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"EnemyManager_block_7" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"EnemyManager_block_7" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (e.hpBar != /* param start [id:"EnemyManager_value_91" type:"value"] */null/* param end [id:"EnemyManager_value_91"] */) e.hpBar.hide();
            /* block end [id:"EnemyManager_block_40"] */
            /* block end [id:"EnemyManager_block_7"] */
            /* block comment [block:"EnemyManager_block_41" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"EnemyManager_block_41" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"EnemyManager_block_8" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"EnemyManager_block_8" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (e.minimapSprite != /* param start [id:"EnemyManager_value_92" type:"value"] */null/* param end [id:"EnemyManager_value_92"] */) e.minimapSprite.hide();
            /* block end [id:"EnemyManager_block_41"] */
            /* block end [id:"EnemyManager_block_8"] */
        }
        /* block end [id:"EnemyManager_block_6"] */
        /* block comment [block:"EnemyManager_block_42" text:"Calls the add method using its declared arguments."] */
        /* block start [id:"EnemyManager_block_42" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
        enemies.add(e);
        /* block end [id:"EnemyManager_block_42"] */
        /* block comment [block:"EnemyManager_block_43" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"EnemyManager_block_43" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        enemyCount++;
        /* block end [id:"EnemyManager_block_43"] */
    }


    public static EnemyManager getInstance() {
        /* block comment [block:"EnemyManager_block_44" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"EnemyManager_block_44" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"EnemyManager_block_9" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemyManager_block_9" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (instance == /* param start [id:"EnemyManager_value_93" type:"value"] */null/* param end [id:"EnemyManager_value_93"] */) instance = new EnemyManager();
        /* block end [id:"EnemyManager_block_44"] */
        /* block end [id:"EnemyManager_block_9"] */
        /* block comment [block:"EnemyManager_block_45" text:"Returns a value or ends the current method."] */
        /* block start [id:"EnemyManager_block_45" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return instance;
        /* block end [id:"EnemyManager_block_45"] */
    }

    public void update(int playerX, int playerY) {
        // Gameplay-only: no spawning or AI ticks over the character menu.
        /* block comment [block:"EnemyManager_block_46" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"EnemyManager_block_46" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"EnemyManager_block_10" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemyManager_block_10" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!GameState.isGameplay()) return;
        /* block end [id:"EnemyManager_block_46"] */
        /* block end [id:"EnemyManager_block_10"] */
        // 1. Spawning Logic
        /* block comment [block:"EnemyManager_block_47" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemyManager_block_47" category:"Variables" label:"set now" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        long now = System.currentTimeMillis();
        /* block end [id:"EnemyManager_block_47"] */
        /* block comment [block:"EnemyManager_block_11" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemyManager_block_11" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (now >= spawnGraceUntil && now - lastSpawnTime > spawnInterval) {
            /* block comment [block:"EnemyManager_block_48" text:"Calls the spawnEnemy method using its declared arguments."] */
            /* block start [id:"EnemyManager_block_48" category:"My Blocks" label:"call spawn enemy" shape:"command" docs:"Calls the spawnEnemy method using its declared arguments."] */
            spawnEnemy(playerX, playerY);
            /* block end [id:"EnemyManager_block_48"] */
            /* block comment [block:"EnemyManager_block_49" text:"Runs the current time millis action. Values in this block are editable inputs."] */
            /* block start [id:"EnemyManager_block_49" category:"My Blocks" label:"current time millis" shape:"command" docs:"Runs the current time millis action. Values in this block are editable inputs."] */
            lastSpawnTime = System.currentTimeMillis();
            /* block end [id:"EnemyManager_block_49"] */
        }
        /* block end [id:"EnemyManager_block_11"] */

        // 2. Update all active enemies
        /* block comment [block:"EnemyManager_block_12" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"EnemyManager_block_12" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (int i = enemies.size() - /* param start [id:"EnemyManager_value_94" type:"number"] */1/* param end [id:"EnemyManager_value_94"] */; i >= /* param start [id:"EnemyManager_value_95" type:"number"] */0/* param end [id:"EnemyManager_value_95"] */; i--) {
            /* block comment [block:"EnemyManager_block_50" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"EnemyManager_block_50" category:"Variables" label:"set e" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            EnemySprite e = enemies.get(i);
            /* block end [id:"EnemyManager_block_50"] */
            /* block comment [block:"EnemyManager_block_13" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"EnemyManager_block_13" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (e.isVisible()) {
                /* block comment [block:"EnemyManager_block_51" text:"Calls the think method using its declared arguments."] */
                /* block start [id:"EnemyManager_block_51" category:"My Blocks" label:"call think" shape:"command" docs:"Calls the think method using its declared arguments."] */
                e.think(playerX, playerY);
                /* block end [id:"EnemyManager_block_51"] */
            } else {
                /* block comment [block:"EnemyManager_block_14" text:"Runs work safely and handles matching errors in a catch branch."] */
                /* block start [id:"EnemyManager_block_14" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
                try { /* block comment [block:"EnemyManager_block_52" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    /* block start [id:"EnemyManager_block_52" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    Scroller2d.getInstance().unregister(e);
                    /* block end [id:"EnemyManager_block_52"] */ }
                /* block end [id:"EnemyManager_block_14"] */ catch (Exception ignored) {}
                /* block comment [block:"EnemyManager_block_53" text:"Runs the remove action. Values in this block are editable inputs."] */
                /* block start [id:"EnemyManager_block_53" category:"Motion" label:"remove" shape:"command" docs:"Runs the remove action. Values in this block are editable inputs."] */
                enemies.remove(i);
                /* block end [id:"EnemyManager_block_53"] */
            }
            /* block end [id:"EnemyManager_block_13"] */
        }
        /* block end [id:"EnemyManager_block_12"] */
    }

    private void spawnEnemy(int playerX, int playerY) {
        // Spawn randomly around the player just outside of view, on free ground
        /* block comment [block:"EnemyManager_block_54" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"EnemyManager_block_54" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"EnemyManager_block_15" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemyManager_block_15" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (enemyCount >= enemyLimit) return;
        /* block end [id:"EnemyManager_block_54"] */
        /* block end [id:"EnemyManager_block_15"] */ //ensure limit
        /* block comment [block:"EnemyManager_block_55" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemyManager_block_55" category:"Variables" label:"set radius" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int radius = /* param start [id:"EnemyManager_value_96" type:"number"] */400/* param end [id:"EnemyManager_value_96"] */;
        /* block end [id:"EnemyManager_block_55"] */
        /* block comment [block:"EnemyManager_block_56" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemyManager_block_56" category:"Variables" label:"set spawnX" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int spawnX = playerX;
        /* block end [id:"EnemyManager_block_56"] */
        /* block comment [block:"EnemyManager_block_57" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemyManager_block_57" category:"Variables" label:"set spawnY" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int spawnY = playerY;
        /* block end [id:"EnemyManager_block_57"] */
        // 34px box fits every regular enemy body (boss spawns are hand-placed).
        /* block comment [block:"EnemyManager_block_58" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemyManager_block_58" category:"Variables" label:"set free" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        boolean free = !Scroller2d.getInstance().isColliding(spawnX, spawnY, /* param start [id:"EnemyManager_value_97" type:"number"] */34/* param end [id:"EnemyManager_value_97"] */, /* param start [id:"EnemyManager_value_98" type:"number"] */34/* param end [id:"EnemyManager_value_98"] */);
        /* block end [id:"EnemyManager_block_58"] */
        /* block comment [block:"EnemyManager_block_16" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"EnemyManager_block_16" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (int tries = /* param start [id:"EnemyManager_value_99" type:"number"] */0/* param end [id:"EnemyManager_value_99"] */; tries < /* param start [id:"EnemyManager_value_100" type:"number"] */10/* param end [id:"EnemyManager_value_100"] */ && !free; tries++) {
            /* block comment [block:"EnemyManager_block_59" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"EnemyManager_block_59" category:"Variables" label:"set angle" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double angle = random.nextDouble() * Math.PI * /* param start [id:"EnemyManager_value_101" type:"number"] */2/* param end [id:"EnemyManager_value_101"] */;
            /* block end [id:"EnemyManager_block_59"] */
            /* block comment [block:"EnemyManager_block_60" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"EnemyManager_block_60" category:"Variables" label:"set sx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int sx = playerX + (int)(Math.cos(angle) * radius);
            /* block end [id:"EnemyManager_block_60"] */
            /* block comment [block:"EnemyManager_block_61" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"EnemyManager_block_61" category:"Variables" label:"set sy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int sy = playerY + (int)(Math.sin(angle) * radius);
            /* block end [id:"EnemyManager_block_61"] */
            /* block comment [block:"EnemyManager_block_17" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"EnemyManager_block_17" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (!Scroller2d.getInstance().isColliding(sx, sy, /* param start [id:"EnemyManager_value_102" type:"number"] */34/* param end [id:"EnemyManager_value_102"] */, /* param start [id:"EnemyManager_value_103" type:"number"] */34/* param end [id:"EnemyManager_value_103"] */)) {
                /* block comment [block:"EnemyManager_block_62" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"EnemyManager_block_62" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                spawnX = sx;
                /* block end [id:"EnemyManager_block_62"] */
                /* block comment [block:"EnemyManager_block_63" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"EnemyManager_block_63" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                spawnY = sy;
                /* block end [id:"EnemyManager_block_63"] */
                /* block comment [block:"EnemyManager_block_64" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"EnemyManager_block_64" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                free = /* param start [id:"EnemyManager_value_104" type:"boolean"] */true/* param end [id:"EnemyManager_value_104"] */;
                /* block end [id:"EnemyManager_block_64"] */
            }
            /* block end [id:"EnemyManager_block_17"] */
        }
        /* block end [id:"EnemyManager_block_16"] */
        /* block comment [block:"EnemyManager_block_65" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"EnemyManager_block_65" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"EnemyManager_block_18" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemyManager_block_18" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!free) return;
        /* block end [id:"EnemyManager_block_65"] */
        /* block end [id:"EnemyManager_block_18"] */ // no room this tick, try again next interval

        /* block comment [block:"EnemyManager_block_66" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"EnemyManager_block_66" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"EnemyManager_block_19" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"EnemyManager_block_19" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (mix == /* param start [id:"EnemyManager_value_105" type:"value"] */null/* param end [id:"EnemyManager_value_105"] */ || mix.length == /* param start [id:"EnemyManager_value_106" type:"number"] */0/* param end [id:"EnemyManager_value_106"] */) return;
        /* block end [id:"EnemyManager_block_66"] */
        /* block end [id:"EnemyManager_block_19"] */
        /* block comment [block:"EnemyManager_block_67" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemyManager_block_67" category:"Variables" label:"set type" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int type = mix[random.nextInt(mix.length)];
        /* block end [id:"EnemyManager_block_67"] */
        /* block comment [block:"EnemyManager_block_68" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemyManager_block_68" category:"Variables" label:"set e" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        EnemySprite e;
        /* block end [id:"EnemyManager_block_68"] */

        /* block comment [block:"EnemyManager_block_20" text:"Selects and runs the case that matches the input value."] */
        /* block start [id:"EnemyManager_block_20" category:"Control" label:"choose a case" shape:"control" docs:"Selects and runs the case that matches the input value."] */
        switch (type) {
            /* block comment [block:"EnemyManager_block_69" text:"Runs the orc action. Values in this block are editable inputs."] */
            /* block start [id:"EnemyManager_block_69" category:"My Blocks" label:"orc" shape:"command" docs:"Runs the orc action. Values in this block are editable inputs."] */
            case /* param start [id:"EnemyManager_value_107" type:"number"] */0/* param end [id:"EnemyManager_value_107"] */: e = new Orc(/* param start [id:"EnemyManager_value_108" type:"text"] */"Orc_"/* param end [id:"EnemyManager_value_108"] */ + enemies.size());
            /* block end [id:"EnemyManager_block_69"] */ /* block comment [block:"EnemyManager_block_70" text:"Stops the nearest loop or switch block."] */
            /* block start [id:"EnemyManager_block_70" category:"Control" label:"break loop" shape:"command" docs:"Stops the nearest loop or switch block."] */
            break;
            /* block end [id:"EnemyManager_block_70"] */
            /* block comment [block:"EnemyManager_block_71" text:"Runs the goblin action. Values in this block are editable inputs."] */
            /* block start [id:"EnemyManager_block_71" category:"My Blocks" label:"goblin" shape:"command" docs:"Runs the goblin action. Values in this block are editable inputs."] */
            case /* param start [id:"EnemyManager_value_109" type:"number"] */1/* param end [id:"EnemyManager_value_109"] */: e = new Goblin(/* param start [id:"EnemyManager_value_110" type:"text"] */"Goblin_"/* param end [id:"EnemyManager_value_110"] */ + enemies.size());
            /* block end [id:"EnemyManager_block_71"] */ /* block comment [block:"EnemyManager_block_72" text:"Stops the nearest loop or switch block."] */
            /* block start [id:"EnemyManager_block_72" category:"Control" label:"break loop" shape:"command" docs:"Stops the nearest loop or switch block."] */
            break;
            /* block end [id:"EnemyManager_block_72"] */
            /* block comment [block:"EnemyManager_block_73" text:"Runs the ghoul action. Values in this block are editable inputs."] */
            /* block start [id:"EnemyManager_block_73" category:"My Blocks" label:"ghoul" shape:"command" docs:"Runs the ghoul action. Values in this block are editable inputs."] */
            case /* param start [id:"EnemyManager_value_111" type:"number"] */3/* param end [id:"EnemyManager_value_111"] */: e = new Ghoul(/* param start [id:"EnemyManager_value_112" type:"text"] */"Ghoul_"/* param end [id:"EnemyManager_value_112"] */ + enemies.size());
            /* block end [id:"EnemyManager_block_73"] */ /* block comment [block:"EnemyManager_block_74" text:"Stops the nearest loop or switch block."] */
            /* block start [id:"EnemyManager_block_74" category:"Control" label:"break loop" shape:"command" docs:"Stops the nearest loop or switch block."] */
            break;
            /* block end [id:"EnemyManager_block_74"] */
            /* block comment [block:"EnemyManager_block_75" text:"Runs the slime action. Values in this block are editable inputs."] */
            /* block start [id:"EnemyManager_block_75" category:"My Blocks" label:"slime" shape:"command" docs:"Runs the slime action. Values in this block are editable inputs."] */
            case /* param start [id:"EnemyManager_value_113" type:"number"] */4/* param end [id:"EnemyManager_value_113"] */: e = new Slime(/* param start [id:"EnemyManager_value_114" type:"text"] */"Slime_"/* param end [id:"EnemyManager_value_114"] */ + enemies.size());
            /* block end [id:"EnemyManager_block_75"] */ /* block comment [block:"EnemyManager_block_76" text:"Stops the nearest loop or switch block."] */
            /* block start [id:"EnemyManager_block_76" category:"Control" label:"break loop" shape:"command" docs:"Stops the nearest loop or switch block."] */
            break;
            /* block end [id:"EnemyManager_block_76"] */
            /* block comment [block:"EnemyManager_block_77" text:"Runs the zombie action. Values in this block are editable inputs."] */
            /* block start [id:"EnemyManager_block_77" category:"My Blocks" label:"zombie" shape:"command" docs:"Runs the zombie action. Values in this block are editable inputs."] */
            default: e = new Zombie(/* param start [id:"EnemyManager_value_115" type:"text"] */"Zombie_"/* param end [id:"EnemyManager_value_115"] */ + enemies.size());
            /* block end [id:"EnemyManager_block_77"] */ /* block comment [block:"EnemyManager_block_78" text:"Stops the nearest loop or switch block."] */
            /* block start [id:"EnemyManager_block_78" category:"Control" label:"break loop" shape:"command" docs:"Stops the nearest loop or switch block."] */
            break;
            /* block end [id:"EnemyManager_block_78"] */
        }
        /* block end [id:"EnemyManager_block_20"] */

        /* block comment [block:"EnemyManager_block_79" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"EnemyManager_block_79" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        e.health = (int) (e.maxHealth * hpMultiplier);
        /* block end [id:"EnemyManager_block_79"] */
        /* block comment [block:"EnemyManager_block_80" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"EnemyManager_block_80" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        e.maxHealth = e.health;
        /* block end [id:"EnemyManager_block_80"] */
        /* block comment [block:"EnemyManager_block_81" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"EnemyManager_block_81" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        e.damageMod += damageBonus;
        /* block end [id:"EnemyManager_block_81"] */

        /* block comment [block:"EnemyManager_block_82" text:"Runs the go to action. Values in this block are editable inputs."] */
        /* block start [id:"EnemyManager_block_82" category:"Motion" label:"go to" shape:"command" docs:"Runs the go to action. Values in this block are editable inputs."] */
        e.goTo(spawnX, spawnY);
        /* block end [id:"EnemyManager_block_82"] */
        /* block comment [block:"EnemyManager_block_83" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"EnemyManager_block_83" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().addSprite(e);
        /* block end [id:"EnemyManager_block_83"] */
        /* block comment [block:"EnemyManager_block_84" text:"Calls the add method using its declared arguments."] */
        /* block start [id:"EnemyManager_block_84" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
        enemies.add(e);
        /* block end [id:"EnemyManager_block_84"] */
        /* block comment [block:"EnemyManager_block_85" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"EnemyManager_block_85" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        enemyCount++;
        /* block end [id:"EnemyManager_block_85"] */
    }

    public List<EnemySprite> getEnemies() {
        /* block comment [block:"EnemyManager_block_86" text:"Returns a value or ends the current method."] */
        /* block start [id:"EnemyManager_block_86" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return enemies;
        /* block end [id:"EnemyManager_block_86"] */
    }
}
