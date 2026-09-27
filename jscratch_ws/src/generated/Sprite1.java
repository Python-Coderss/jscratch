package generated;

import com.jscratch.*;
import java.awt.Color;
import java.awt.event.KeyEvent;

/*
*
* THE PLAYER — and the per-frame game driver.
*
* <p>Main script loop (one daemon thread, paced by waitTillNextFrame):
* HUD/HP-bar sync + mana regen + death watch → EnemyManager.update →
* aggro scan → MusicDirector.update → mute key → FloorManager.update
* (stairs/chest) → melee/spells → LootDrop.checkAll → inventory keys →
* 8-way stepMove + ONE Scroller2d.update.
*
* <p>Calls out to: PlayerStats (hp/mana/xp), HPBAR (player bar),
* EnemyManager/Combat (melee arc), Spellbook (keys 1-6), FloorManager
* (descend), LootDrop/Inventory/InventoryPanel (pickups/equip),
* MusicDirector (calm/combat + M mute), Scroller2d/BSprite (move/scroll).
* Listens for "floorChanged" to reset to spawn.
*
*/
public class Sprite1 extends BSprite {
    public int mv = 15;
    public int turnSpeed = 5; //in milli seconds, time it takes to turn (smaller = faster)
    public HPBAR playerHp;

    public boolean alive = true;
    private volatile boolean stopRequested = false;
    private long lastAttackMs = 0;
    private long lastManaMs = 0;
    private static final int ATTACK_COOLDOWN_MS = 450;
    private boolean[] prevSpell = new boolean[6];
    private boolean prevInv, prevUp, prevDown, prevEnterInv, prevMute, prevControls;
    // Last published HUD components (publish watcher strings on change only).
    private int lastHp = Integer.MIN_VALUE;
    private int lastMaxHp = Integer.MIN_VALUE;
    private int lastMana = Integer.MIN_VALUE;
    private int lastMaxMana = Integer.MIN_VALUE;
    private int lastLvl = Integer.MIN_VALUE;
    private int lastXp = Integer.MIN_VALUE;
    public static InventoryPanel invPanel;
    public static Sprite1 instance;

    public double facing() {
        /* block comment [block:"Sprite1_block_60" text:"Returns a value or ends the current method."] */
        /* block start [id:"Sprite1_block_60" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return (Double) getVariable(/* param start [id:"Sprite1_value_138" type:"text"] */"direction"/* param end [id:"Sprite1_value_138"] */);
        /* block end [id:"Sprite1_block_60"] */
    }

    /* slot start [id:"2_members" tags:"inClassNotMethod"] */
    public Sprite1() {
        /* block comment [block:"Sprite1_block_61" text:"Runs the super action. Values in this block are editable inputs."] */
        /* block start [id:"Sprite1_block_61" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
        super(/* param start [id:"Sprite1_value_139" type:"text"] */"Sprite1"/* param end [id:"Sprite1_value_139"] */);
        /* block end [id:"Sprite1_block_61"] */
        /* block comment [block:"Sprite1_block_62" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Sprite1_block_62" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        instance = this;
        /* block end [id:"Sprite1_block_62"] */
        /* block comment [block:"Sprite1_block_63" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Sprite1_block_63" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.bodyRadius = /* param start [id:"Sprite1_value_140" type:"number"] */14/* param end [id:"Sprite1_value_140"] */;
        /* block end [id:"Sprite1_block_63"] */

        /* slot start [id:"2_init" tags:"inInstanceMethod"] */
        /* block comment [block:"Sprite1_block_64" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"Sprite1_block_64" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(new Costume(
        /* param start [id:"Sprite1_value_141" type:"text"] */"costume1"/* param end [id:"Sprite1_value_141"] */,
        /* param start [id:"Sprite1_value_142" type:"text"] */"jscratch_ws/assets/Player_costume1.png"/* param end [id:"Sprite1_value_142"] */,
        /* param start [id:"Sprite1_value_143" type:"number"] */28/* param end [id:"Sprite1_value_143"] */, /* param start [id:"Sprite1_value_144" type:"number"] */33/* param end [id:"Sprite1_value_144"] */
        ));
        /* block end [id:"Sprite1_block_64"] */

        /* block comment [block:"Sprite1_block_65" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"Sprite1_block_65" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(new Costume(
        /* param start [id:"Sprite1_value_145" type:"text"] */"costume2"/* param end [id:"Sprite1_value_145"] */,
        /* param start [id:"Sprite1_value_146" type:"text"] */"jscratch_ws/assets/Player_costume2.png"/* param end [id:"Sprite1_value_146"] */,
        /* param start [id:"Sprite1_value_147" type:"number"] */29/* param end [id:"Sprite1_value_147"] */, /* param start [id:"Sprite1_value_148" type:"number"] */33/* param end [id:"Sprite1_value_148"] */
        ));
        /* block end [id:"Sprite1_block_65"] */

        // Set minimap costume for player (e.g., a green dot)
        /* block comment [block:"Sprite1_block_66" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"Sprite1_block_66" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        minimapSprite.addCostume(new Costume(/* param start [id:"Sprite1_value_149" type:"text"] */"player_dot"/* param end [id:"Sprite1_value_149"] */, /* param start [id:"Sprite1_value_150" type:"text"] */"jscratch_ws/assets/player_dot.png"/* param end [id:"Sprite1_value_150"] */, /* param start [id:"Sprite1_value_151" type:"number"] */5/* param end [id:"Sprite1_value_151"] */, /* param start [id:"Sprite1_value_152" type:"number"] */5/* param end [id:"Sprite1_value_152"] */));
        /* block end [id:"Sprite1_block_66"] */

        /* block comment [block:"Sprite1_block_67" text:"Runs the go to action. Values in this block are editable inputs."] */
        /* block start [id:"Sprite1_block_67" category:"Motion" label:"go to" shape:"command" docs:"Runs the go to action. Values in this block are editable inputs."] */
        goTo(/* param start [id:"Sprite1_value_153" type:"number"] */0/* param end [id:"Sprite1_value_153"] */,/* param start [id:"Sprite1_value_154" type:"number"] */0/* param end [id:"Sprite1_value_154"] */);
        /* block end [id:"Sprite1_block_67"] */

        /* block comment [block:"Sprite1_block_68" text:"Runs the hpbar action. Values in this block are editable inputs."] */
        /* block start [id:"Sprite1_block_68" category:"My Blocks" label:"hpbar" shape:"command" docs:"Runs the hpbar action. Values in this block are editable inputs."] */
        playerHp = new HPBAR();
        /* block end [id:"Sprite1_block_68"] */

        // Floor changes reset the hero to spawn (FloorManager owns stairs/enemies)
        whenIReceive("floorChanged", () -> {
            xx = 0;
            yy = 0;
            Scroller2d s = Scroller2d.getInstance();
            s.x = 0;
            s.y = 0;
            s.update();
            update();
        }/* block comment [block:"Sprite1_block_69" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Sprite1_block_69" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        );
        /* block end [id:"Sprite1_block_69"] */

        this.startScript(() -> {
            int ws = 100; //walk speed (animation not movement speed) smaller = faster
            /* block comment [block:"Sprite1_block_1" text:"Repeats the body while its Boolean condition is true."] */
            /* block start [id:"Sprite1_block_1" category:"Control" label:"repeat while" shape:"control" docs:"Repeats the body while its Boolean condition is true."] */
            while (!stopRequested) {
                // Gameplay-only driver: pause over menus so enemies, music,
                // floors and HUD never tick during character creation.
                /* block comment [block:"Sprite1_block_2" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"Sprite1_block_2" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (!GameState.isGameplay()) { waitTillNextFrame(); continue; }
                /* block end [id:"Sprite1_block_2"] */
                // H is checked even during a modal pause so it can also close the overlay.
                boolean controls = Stage.getInstance().isKeyPressed(/* param start [id:"Sprite1_value_155" type:"text"] */"h"/* param end [id:"Sprite1_value_155"] */);
                /* block comment [block:"Sprite1_block_3" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"Sprite1_block_3" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (controls && !prevControls) Stage.getInstance().broadcast(/* param start [id:"Sprite1_value_156" type:"text"] */"toggleControls"/* param end [id:"Sprite1_value_156"] */);
                /* block end [id:"Sprite1_block_3"] */
                prevControls = controls;
                /* block comment [block:"Sprite1_block_4" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"Sprite1_block_4" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (GameState.isPaused()) { waitTillNextFrame(); continue; }
                /* block end [id:"Sprite1_block_4"] */
                // HUD + death watch (hybrid stats live in PlayerStats).
                // Watcher strings are published only when a component value
                // actually changed — no per-frame string building.
                /* block comment [block:"Sprite1_block_5" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"Sprite1_block_5" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (PlayerStats.ready && PlayerStats.stats != /* param start [id:"Sprite1_value_157" type:"value"] */null/* param end [id:"Sprite1_value_157"] */) {
                    // Bar 65px above the head: +y renders up, and the camera
                    // keeps the player centered so this is screen-fixed.
                    playerHp.update(PlayerStats.stats.hp, PlayerStats.stats.maxHp, xx, yy + /* param start [id:"Sprite1_value_158" type:"number"] */65/* param end [id:"Sprite1_value_158"] */);
                    long now = System.currentTimeMillis();
                    /* block comment [block:"Sprite1_block_6" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"Sprite1_block_6" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (now - lastManaMs > /* param start [id:"Sprite1_value_159" type:"number"] */1000/* param end [id:"Sprite1_value_159"] */) { PlayerStats.restoreMana(/* param start [id:"Sprite1_value_160" type:"number"] */1/* param end [id:"Sprite1_value_160"] */); lastManaMs = now; }
                    /* block end [id:"Sprite1_block_6"] */
                    int hp = PlayerStats.stats.hp;
                    int maxHp = PlayerStats.stats.maxHp;
                    /* block comment [block:"Sprite1_block_7" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"Sprite1_block_7" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (hp != lastHp || maxHp != lastMaxHp) {
                        lastHp = hp;
                        lastMaxHp = maxHp;
                        Stage.getInstance().setVariable(/* param start [id:"Sprite1_value_161" type:"text"] */"HP"/* param end [id:"Sprite1_value_161"] */, hp + /* param start [id:"Sprite1_value_162" type:"text"] */"/"/* param end [id:"Sprite1_value_162"] */ + maxHp);
                    }
                    /* block end [id:"Sprite1_block_7"] */
                    int mana = PlayerStats.mana;
                    int maxMana = PlayerStats.maxMana;
                    /* block comment [block:"Sprite1_block_8" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"Sprite1_block_8" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (mana != lastMana || maxMana != lastMaxMana) {
                        lastMana = mana;
                        lastMaxMana = maxMana;
                        Stage.getInstance().setVariable(/* param start [id:"Sprite1_value_163" type:"text"] */"Mana"/* param end [id:"Sprite1_value_163"] */, mana + /* param start [id:"Sprite1_value_164" type:"text"] */"/"/* param end [id:"Sprite1_value_164"] */ + maxMana);
                    }
                    /* block end [id:"Sprite1_block_8"] */
                    int lvl = PlayerStats.stats.level;
                    int xp = PlayerStats.stats.currentXp;
                    /* block comment [block:"Sprite1_block_9" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"Sprite1_block_9" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (lvl != lastLvl || xp != lastXp) {
                        lastLvl = lvl;
                        lastXp = xp;
                        Stage.getInstance().setVariable(/* param start [id:"Sprite1_value_165" type:"text"] */"Lvl"/* param end [id:"Sprite1_value_165"] */, lvl + /* param start [id:"Sprite1_value_166" type:"text"] */" XP:"/* param end [id:"Sprite1_value_166"] */ + xp);
                    }
                    /* block end [id:"Sprite1_block_9"] */
                    /* block comment [block:"Sprite1_block_10" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"Sprite1_block_10" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (PlayerStats.stats.hp <= /* param start [id:"Sprite1_value_167" type:"number"] */0/* param end [id:"Sprite1_value_167"] */ && alive) {
                        alive = /* param start [id:"Sprite1_value_168" type:"boolean"] */false/* param end [id:"Sprite1_value_168"] */;
                        Stage.getInstance().setVariable(/* param start [id:"Sprite1_value_169" type:"text"] */"Status"/* param end [id:"Sprite1_value_169"] */, /* param start [id:"Sprite1_value_170" type:"text"] */"YOU DIED"/* param end [id:"Sprite1_value_170"] */);
                        MusicDirector.death();
                    }
                    /* block end [id:"Sprite1_block_10"] */
                }
                /* block end [id:"Sprite1_block_5"] */
                /* block comment [block:"Sprite1_block_11" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"Sprite1_block_11" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (!alive) { waitTillNextFrame(); continue; }
                /* block end [id:"Sprite1_block_11"] */

                // Update Enemies
                EnemyManager.getInstance().update(xx, yy);

                // Music: calm/combat switch with aggro (<=5 enemies, cheap scan)
                boolean enemiesNear = /* param start [id:"Sprite1_value_171" type:"boolean"] */false/* param end [id:"Sprite1_value_171"] */;
                /* block comment [block:"Sprite1_block_12" text:"Repeats the nested operations for each loop turn."] */
                /* block start [id:"Sprite1_block_12" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
                for (EnemySprite e : EnemyManager.getInstance().getEnemies()) {
                    /* block comment [block:"Sprite1_block_13" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"Sprite1_block_13" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (!e.isVisible()) continue;
                    /* block end [id:"Sprite1_block_13"] */
                    double edx = e.xx - xx;
                    double edy = e.yy - yy;
                    /* block comment [block:"Sprite1_block_14" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"Sprite1_block_14" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (edx * edx + edy * edy < /* param start [id:"Sprite1_value_172" type:"number"] */400/* param end [id:"Sprite1_value_172"] */ * /* param start [id:"Sprite1_value_173" type:"number"] */400/* param end [id:"Sprite1_value_173"] */) { enemiesNear = /* param start [id:"Sprite1_value_174" type:"boolean"] */true/* param end [id:"Sprite1_value_174"] */; break; }
                    /* block end [id:"Sprite1_block_14"] */
                }
                /* block end [id:"Sprite1_block_12"] */
                MusicDirector.update(enemiesNear);

                // Mute toggle (M)
                boolean mute = Stage.getInstance().isKeyPressed(/* param start [id:"Sprite1_value_175" type:"text"] */"m"/* param end [id:"Sprite1_value_175"] */);
                /* block comment [block:"Sprite1_block_15" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"Sprite1_block_15" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (mute && !prevMute) {
                    boolean nowMuted = MusicDirector.toggleMute();
                    Stage.getInstance().setVariable(/* param start [id:"Sprite1_value_176" type:"text"] */"Msg"/* param end [id:"Sprite1_value_176"] */, nowMuted ? /* param start [id:"Sprite1_value_177" type:"text"] */"Music off (M)"/* param end [id:"Sprite1_value_177"] */ : /* param start [id:"Sprite1_value_178" type:"text"] */"Music on (M)"/* param end [id:"Sprite1_value_178"] */);
                }
                /* block end [id:"Sprite1_block_15"] */
                prevMute = mute;

                // Stairs / floor progression
                FloorManager.getInstance().update(xx, yy);

                // Attack Check (Space bar)
                /* block comment [block:"Sprite1_block_16" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"Sprite1_block_16" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (Stage.getInstance().isKeyPressed(/* param start [id:"Sprite1_value_179" type:"text"] */"space"/* param end [id:"Sprite1_value_179"] */)) {
                    performAttack();
                }
                /* block end [id:"Sprite1_block_16"] */

                // Spell keys 1-6 (edge-triggered so holding doesn't machine-gun casts)
                boolean[] spellNow = new boolean[/* param start [id:"Sprite1_value_180" type:"number"] */6/* param end [id:"Sprite1_value_180"] */];
                /* block comment [block:"Sprite1_block_17" text:"Repeats the nested operations for each loop turn."] */
                /* block start [id:"Sprite1_block_17" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
                for (int si = /* param start [id:"Sprite1_value_181" type:"number"] */0/* param end [id:"Sprite1_value_181"] */; si < /* param start [id:"Sprite1_value_182" type:"number"] */6/* param end [id:"Sprite1_value_182"] */; si++) {
                    spellNow[si] = Stage.getInstance().isKeyPressed(String.valueOf(si + /* param start [id:"Sprite1_value_183" type:"number"] */1/* param end [id:"Sprite1_value_183"] */));
                }
                /* block end [id:"Sprite1_block_17"] */
                double face = facing();
                /* block comment [block:"Sprite1_block_18" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"Sprite1_block_18" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (spellNow[/* param start [id:"Sprite1_value_184" type:"number"] */0/* param end [id:"Sprite1_value_184"] */] && !prevSpell[/* param start [id:"Sprite1_value_185" type:"number"] */0/* param end [id:"Sprite1_value_185"] */]) Spellbook.castSlash(xx, yy, face);
                /* block end [id:"Sprite1_block_18"] */
                /* block comment [block:"Sprite1_block_19" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"Sprite1_block_19" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (spellNow[/* param start [id:"Sprite1_value_186" type:"number"] */1/* param end [id:"Sprite1_value_186"] */] && !prevSpell[/* param start [id:"Sprite1_value_187" type:"number"] */1/* param end [id:"Sprite1_value_187"] */]) Spellbook.castBomb(xx, yy, face);
                /* block end [id:"Sprite1_block_19"] */
                /* block comment [block:"Sprite1_block_20" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"Sprite1_block_20" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (spellNow[/* param start [id:"Sprite1_value_188" type:"number"] */2/* param end [id:"Sprite1_value_188"] */] && !prevSpell[/* param start [id:"Sprite1_value_189" type:"number"] */2/* param end [id:"Sprite1_value_189"] */]) Spellbook.castBolt(xx, yy, face);
                /* block end [id:"Sprite1_block_20"] */
                /* block comment [block:"Sprite1_block_21" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"Sprite1_block_21" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (spellNow[/* param start [id:"Sprite1_value_190" type:"number"] */3/* param end [id:"Sprite1_value_190"] */] && !prevSpell[/* param start [id:"Sprite1_value_191" type:"number"] */3/* param end [id:"Sprite1_value_191"] */]) Spellbook.castHeal(xx, yy);
                /* block end [id:"Sprite1_block_21"] */
                /* block comment [block:"Sprite1_block_22" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"Sprite1_block_22" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (spellNow[/* param start [id:"Sprite1_value_192" type:"number"] */4/* param end [id:"Sprite1_value_192"] */] && !prevSpell[/* param start [id:"Sprite1_value_193" type:"number"] */4/* param end [id:"Sprite1_value_193"] */]) Spellbook.castPoison(xx, yy, face);
                /* block end [id:"Sprite1_block_22"] */
                /* block comment [block:"Sprite1_block_23" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"Sprite1_block_23" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (spellNow[/* param start [id:"Sprite1_value_194" type:"number"] */5/* param end [id:"Sprite1_value_194"] */] && !prevSpell[/* param start [id:"Sprite1_value_195" type:"number"] */5/* param end [id:"Sprite1_value_195"] */]) Spellbook.castSummon(xx, yy);
                /* block end [id:"Sprite1_block_23"] */
                prevSpell = spellNow;

                // Pickups
                LootDrop.checkAll(xx, yy);

                // Inventory panel (I toggle, Up/Down select, Enter equip)
                boolean inv = Stage.getInstance().isKeyPressed(/* param start [id:"Sprite1_value_196" type:"text"] */"i"/* param end [id:"Sprite1_value_196"] */);
                boolean up = Stage.getInstance().isKeyPressed(/* param start [id:"Sprite1_value_197" type:"text"] */"up arrow"/* param end [id:"Sprite1_value_197"] */);
                boolean down = Stage.getInstance().isKeyPressed(/* param start [id:"Sprite1_value_198" type:"text"] */"down arrow"/* param end [id:"Sprite1_value_198"] */);
                boolean enterInv = Stage.getInstance().isKeyPressed(java.awt.event.KeyEvent.VK_ENTER);
                /* block comment [block:"Sprite1_block_24" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"Sprite1_block_24" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (invPanel != /* param start [id:"Sprite1_value_199" type:"value"] */null/* param end [id:"Sprite1_value_199"] */) {
                    /* block comment [block:"Sprite1_block_25" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"Sprite1_block_25" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (inv && !prevInv) invPanel.toggle();
                    /* block end [id:"Sprite1_block_25"] */
                    /* block comment [block:"Sprite1_block_26" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"Sprite1_block_26" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (invPanel.isOpen()) {
                        /* block comment [block:"Sprite1_block_27" text:"Checks a Boolean condition and runs the body when it is true."] */
                        /* block start [id:"Sprite1_block_27" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                        if (up && !prevUp) invPanel.moveSel(-/* param start [id:"Sprite1_value_200" type:"number"] */1/* param end [id:"Sprite1_value_200"] */);
                        /* block end [id:"Sprite1_block_27"] */
                        /* block comment [block:"Sprite1_block_28" text:"Checks a Boolean condition and runs the body when it is true."] */
                        /* block start [id:"Sprite1_block_28" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                        if (down && !prevDown) invPanel.moveSel(/* param start [id:"Sprite1_value_201" type:"number"] */1/* param end [id:"Sprite1_value_201"] */);
                        /* block end [id:"Sprite1_block_28"] */
                        /* block comment [block:"Sprite1_block_29" text:"Checks a Boolean condition and runs the body when it is true."] */
                        /* block start [id:"Sprite1_block_29" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                        if (enterInv && !prevEnterInv) invPanel.equipSel();
                        /* block end [id:"Sprite1_block_29"] */
                    }
                    /* block end [id:"Sprite1_block_26"] */
                }
                /* block end [id:"Sprite1_block_24"] */
                prevInv = inv;
                prevUp = up;
                prevDown = down;
                prevEnterInv = enterInv;

                // Synchronous 8-way movement: one step per frame, one
                // Scroller.update per frame. The old code spawned 2 threads
                // per direction per frame (cmovec + costume) plus a blocking
                // animateTurn sleep and a 100ms sleep here, which stacked
                // overlapping movers and ran a full 121-tile minimap rebuild
                // per pixel step (~15 rebuilds/frame while moving).
                boolean w = Stage.getInstance().isKeyPressed(/* param start [id:"Sprite1_value_202" type:"text"] */"w"/* param end [id:"Sprite1_value_202"] */);
                boolean a = Stage.getInstance().isKeyPressed(/* param start [id:"Sprite1_value_203" type:"text"] */"a"/* param end [id:"Sprite1_value_203"] */);
                boolean s = Stage.getInstance().isKeyPressed(/* param start [id:"Sprite1_value_204" type:"text"] */"s"/* param end [id:"Sprite1_value_204"] */);
                boolean d = Stage.getInstance().isKeyPressed(/* param start [id:"Sprite1_value_205" type:"text"] */"d"/* param end [id:"Sprite1_value_205"] */);
                int mdx = /* param start [id:"Sprite1_value_206" type:"number"] */0/* param end [id:"Sprite1_value_206"] */, mdy = /* param start [id:"Sprite1_value_207" type:"number"] */0/* param end [id:"Sprite1_value_207"] */;
                int faceDir = -/* param start [id:"Sprite1_value_208" type:"number"] */1/* param end [id:"Sprite1_value_208"] */;
                /* block comment [block:"Sprite1_block_30" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"Sprite1_block_30" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (w && !s) mdy = /* param start [id:"Sprite1_value_209" type:"number"] */1/* param end [id:"Sprite1_value_209"] */;
                else /* block comment [block:"Sprite1_block_31" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"Sprite1_block_31" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (s && !w) mdy = -/* param start [id:"Sprite1_value_210" type:"number"] */1/* param end [id:"Sprite1_value_210"] */;
                /* block end [id:"Sprite1_block_31"] */
                /* block end [id:"Sprite1_block_30"] */
                /* block comment [block:"Sprite1_block_32" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"Sprite1_block_32" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (a && !d) mdx = -/* param start [id:"Sprite1_value_211" type:"number"] */1/* param end [id:"Sprite1_value_211"] */;
                else /* block comment [block:"Sprite1_block_33" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"Sprite1_block_33" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (d && !a) mdx = /* param start [id:"Sprite1_value_212" type:"number"] */1/* param end [id:"Sprite1_value_212"] */;
                /* block end [id:"Sprite1_block_33"] */
                /* block end [id:"Sprite1_block_32"] */
                /* block comment [block:"Sprite1_block_34" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"Sprite1_block_34" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (mdx != /* param start [id:"Sprite1_value_213" type:"number"] */0/* param end [id:"Sprite1_value_213"] */ || mdy != /* param start [id:"Sprite1_value_214" type:"number"] */0/* param end [id:"Sprite1_value_214"] */) {
                    /* block comment [block:"Sprite1_block_35" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"Sprite1_block_35" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (mdx == /* param start [id:"Sprite1_value_215" type:"number"] */0/* param end [id:"Sprite1_value_215"] */ && mdy == /* param start [id:"Sprite1_value_216" type:"number"] */1/* param end [id:"Sprite1_value_216"] */) faceDir = /* param start [id:"Sprite1_value_217" type:"number"] */0/* param end [id:"Sprite1_value_217"] */;
                    else /* block comment [block:"Sprite1_block_36" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"Sprite1_block_36" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (mdx == /* param start [id:"Sprite1_value_218" type:"number"] */1/* param end [id:"Sprite1_value_218"] */ && mdy == /* param start [id:"Sprite1_value_219" type:"number"] */0/* param end [id:"Sprite1_value_219"] */) faceDir = /* param start [id:"Sprite1_value_220" type:"number"] */90/* param end [id:"Sprite1_value_220"] */;
                    /* block end [id:"Sprite1_block_35"] */
                    else /* block comment [block:"Sprite1_block_37" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"Sprite1_block_37" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (mdx == /* param start [id:"Sprite1_value_221" type:"number"] */0/* param end [id:"Sprite1_value_221"] */ && mdy == -/* param start [id:"Sprite1_value_222" type:"number"] */1/* param end [id:"Sprite1_value_222"] */) faceDir = /* param start [id:"Sprite1_value_223" type:"number"] */180/* param end [id:"Sprite1_value_223"] */;
                    /* block end [id:"Sprite1_block_36"] */
                    else /* block comment [block:"Sprite1_block_38" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"Sprite1_block_38" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (mdx == -/* param start [id:"Sprite1_value_224" type:"number"] */1/* param end [id:"Sprite1_value_224"] */ && mdy == /* param start [id:"Sprite1_value_225" type:"number"] */0/* param end [id:"Sprite1_value_225"] */) faceDir = /* param start [id:"Sprite1_value_226" type:"number"] */270/* param end [id:"Sprite1_value_226"] */;
                    /* block end [id:"Sprite1_block_37"] */
                    else /* block comment [block:"Sprite1_block_39" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"Sprite1_block_39" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (mdx == -/* param start [id:"Sprite1_value_227" type:"number"] */1/* param end [id:"Sprite1_value_227"] */ && mdy == /* param start [id:"Sprite1_value_228" type:"number"] */1/* param end [id:"Sprite1_value_228"] */) faceDir = /* param start [id:"Sprite1_value_229" type:"number"] */315/* param end [id:"Sprite1_value_229"] */;
                    /* block end [id:"Sprite1_block_38"] */
                    else /* block comment [block:"Sprite1_block_40" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"Sprite1_block_40" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (mdx == /* param start [id:"Sprite1_value_230" type:"number"] */1/* param end [id:"Sprite1_value_230"] */ && mdy == /* param start [id:"Sprite1_value_231" type:"number"] */1/* param end [id:"Sprite1_value_231"] */) faceDir = /* param start [id:"Sprite1_value_232" type:"number"] */45/* param end [id:"Sprite1_value_232"] */;
                    /* block end [id:"Sprite1_block_39"] */
                    else /* block comment [block:"Sprite1_block_41" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"Sprite1_block_41" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (mdx == -/* param start [id:"Sprite1_value_233" type:"number"] */1/* param end [id:"Sprite1_value_233"] */ && mdy == -/* param start [id:"Sprite1_value_234" type:"number"] */1/* param end [id:"Sprite1_value_234"] */) faceDir = /* param start [id:"Sprite1_value_235" type:"number"] */225/* param end [id:"Sprite1_value_235"] */;
                    /* block end [id:"Sprite1_block_40"] */
                    else /* block comment [block:"Sprite1_block_42" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"Sprite1_block_42" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (mdx == /* param start [id:"Sprite1_value_236" type:"number"] */1/* param end [id:"Sprite1_value_236"] */ && mdy == -/* param start [id:"Sprite1_value_237" type:"number"] */1/* param end [id:"Sprite1_value_237"] */) faceDir = /* param start [id:"Sprite1_value_238" type:"number"] */135/* param end [id:"Sprite1_value_238"] */;
                    /* block end [id:"Sprite1_block_42"] */
                    /* block end [id:"Sprite1_block_41"] */
                    pointInDirection(faceDir);
                    int step = (mdx != /* param start [id:"Sprite1_value_239" type:"number"] */0/* param end [id:"Sprite1_value_239"] */ && mdy != /* param start [id:"Sprite1_value_240" type:"number"] */0/* param end [id:"Sprite1_value_240"] */) ? /* param start [id:"Sprite1_value_241" type:"number"] */4/* param end [id:"Sprite1_value_241"] */ : /* param start [id:"Sprite1_value_242" type:"number"] */5/* param end [id:"Sprite1_value_242"] */;
                    stepMove(mdx * step, mdy * step);
                    nextCostume();
                    Scroller2d sc = Scroller2d.getInstance();
                    sc.x = -xx;
                    sc.y = -yy;
                    sc.update();
                    super.update();
                }
                /* block end [id:"Sprite1_block_34"] */

                waitTillNextFrame();
            }
            /* block end [id:"Sprite1_block_1"] */
        }/* block comment [block:"Sprite1_block_70" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Sprite1_block_70" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        );
        /* block end [id:"Sprite1_block_70"] */
        /* slot end [id:"2_init"] */
    }

    /** Stop this run's driver before constructing a new hero and loop. */
    public void stopGameLoop() {
        /* block comment [block:"Sprite1_block_71" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Sprite1_block_71" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        stopRequested = /* param start [id:"Sprite1_value_243" type:"boolean"] */true/* param end [id:"Sprite1_value_243"] */;
        /* block end [id:"Sprite1_block_71"] */
        /* block comment [block:"Sprite1_block_72" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Sprite1_block_72" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        alive = /* param start [id:"Sprite1_value_244" type:"boolean"] */false/* param end [id:"Sprite1_value_244"] */;
        /* block end [id:"Sprite1_block_72"] */
        /* block comment [block:"Sprite1_block_73" text:"Runs the for action. Values in this block are editable inputs."] */
        /* block start [id:"Sprite1_block_73" category:"My Blocks" label:"for" shape:"command" docs:"Runs the for action. Values in this block are editable inputs."] */
        /* block comment [block:"Sprite1_block_43" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"Sprite1_block_43" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (Thread script : activeScripts) script.interrupt();
        /* block end [id:"Sprite1_block_73"] */
        /* block end [id:"Sprite1_block_43"] */
        /* block comment [block:"Sprite1_block_74" text:"Calls the destroy method using its declared arguments."] */
        /* block start [id:"Sprite1_block_74" category:"My Blocks" label:"call destroy" shape:"command" docs:"Calls the destroy method using its declared arguments."] */
        destroy();
        /* block end [id:"Sprite1_block_74"] */
        /* block comment [block:"Sprite1_block_75" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Sprite1_block_75" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Sprite1_block_44" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Sprite1_block_44" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (instance == this) instance = /* param start [id:"Sprite1_value_245" type:"value"] */null/* param end [id:"Sprite1_value_245"] */;
        /* block end [id:"Sprite1_block_75"] */
        /* block end [id:"Sprite1_block_44"] */
    }

    /** Single collision-aware step with wall sliding; no scene rebuild. */
    private void stepMove(int dx, int dy) {
        /* block comment [block:"Sprite1_block_76" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Sprite1_block_76" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Sprite1_block_45" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Sprite1_block_45" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (dx == /* param start [id:"Sprite1_value_246" type:"number"] */0/* param end [id:"Sprite1_value_246"] */ && dy == /* param start [id:"Sprite1_value_247" type:"number"] */0/* param end [id:"Sprite1_value_247"] */) return;
        /* block end [id:"Sprite1_block_76"] */
        /* block end [id:"Sprite1_block_45"] */
        /* block comment [block:"Sprite1_block_46" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Sprite1_block_46" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!willCollide(xx + dx, yy + dy)) {
            /* block comment [block:"Sprite1_block_77" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Sprite1_block_77" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            xx += dx;
            /* block end [id:"Sprite1_block_77"] */
            /* block comment [block:"Sprite1_block_78" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Sprite1_block_78" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            yy += dy;
            /* block end [id:"Sprite1_block_78"] */
        } else /* block comment [block:"Sprite1_block_47" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Sprite1_block_47" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (dx != /* param start [id:"Sprite1_value_248" type:"number"] */0/* param end [id:"Sprite1_value_248"] */ && !willCollide(xx + dx, yy)) {
            /* block comment [block:"Sprite1_block_79" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Sprite1_block_79" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            xx += dx;
            /* block end [id:"Sprite1_block_79"] */ // slide along X
        }
        /* block end [id:"Sprite1_block_46"] */ else /* block comment [block:"Sprite1_block_48" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Sprite1_block_48" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (dy != /* param start [id:"Sprite1_value_249" type:"number"] */0/* param end [id:"Sprite1_value_249"] */ && !willCollide(xx, yy + dy)) {
            /* block comment [block:"Sprite1_block_80" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Sprite1_block_80" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            yy += dy;
            /* block end [id:"Sprite1_block_80"] */ // slide along Y
        }
        /* block end [id:"Sprite1_block_48"] */
        /* block end [id:"Sprite1_block_47"] */
    }

    public void movec(int amt, int dir) {
        /* block comment [block:"Sprite1_block_81" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Sprite1_block_81" category:"Variables" label:"set nextX" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int nextX = xx;
        /* block end [id:"Sprite1_block_81"] */
        /* block comment [block:"Sprite1_block_82" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Sprite1_block_82" category:"Variables" label:"set nextY" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int nextY = yy;
        /* block end [id:"Sprite1_block_82"] */
        /* block comment [block:"Sprite1_block_49" text:"Selects and runs the case that matches the input value."] */
        /* block start [id:"Sprite1_block_49" category:"Control" label:"choose a case" shape:"control" docs:"Selects and runs the case that matches the input value."] */
        switch (dir) {
            /* block comment [block:"Sprite1_block_83" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Sprite1_block_83" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            case /* param start [id:"Sprite1_value_250" type:"number"] */0/* param end [id:"Sprite1_value_250"] */: nextY += amt;
            /* block end [id:"Sprite1_block_83"] */ /* block comment [block:"Sprite1_block_84" text:"Stops the nearest loop or switch block."] */
            /* block start [id:"Sprite1_block_84" category:"Control" label:"break loop" shape:"command" docs:"Stops the nearest loop or switch block."] */
            break;
            /* block end [id:"Sprite1_block_84"] */
            /* block comment [block:"Sprite1_block_85" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Sprite1_block_85" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            case /* param start [id:"Sprite1_value_251" type:"number"] */1/* param end [id:"Sprite1_value_251"] */: nextX += amt;
            /* block end [id:"Sprite1_block_85"] */ /* block comment [block:"Sprite1_block_86" text:"Stops the nearest loop or switch block."] */
            /* block start [id:"Sprite1_block_86" category:"Control" label:"break loop" shape:"command" docs:"Stops the nearest loop or switch block."] */
            break;
            /* block end [id:"Sprite1_block_86"] */
            /* block comment [block:"Sprite1_block_87" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Sprite1_block_87" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            case /* param start [id:"Sprite1_value_252" type:"number"] */2/* param end [id:"Sprite1_value_252"] */: nextY -= amt;
            /* block end [id:"Sprite1_block_87"] */ /* block comment [block:"Sprite1_block_88" text:"Stops the nearest loop or switch block."] */
            /* block start [id:"Sprite1_block_88" category:"Control" label:"break loop" shape:"command" docs:"Stops the nearest loop or switch block."] */
            break;
            /* block end [id:"Sprite1_block_88"] */
            /* block comment [block:"Sprite1_block_89" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Sprite1_block_89" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            case /* param start [id:"Sprite1_value_253" type:"number"] */3/* param end [id:"Sprite1_value_253"] */: nextX -= amt;
            /* block end [id:"Sprite1_block_89"] */ /* block comment [block:"Sprite1_block_90" text:"Stops the nearest loop or switch block."] */
            /* block start [id:"Sprite1_block_90" category:"Control" label:"break loop" shape:"command" docs:"Stops the nearest loop or switch block."] */
            break;
            /* block end [id:"Sprite1_block_90"] */
        }
        /* block end [id:"Sprite1_block_49"] */

        /* block comment [block:"Sprite1_block_50" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Sprite1_block_50" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!willCollide(nextX, nextY)) {
            /* block comment [block:"Sprite1_block_91" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Sprite1_block_91" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            xx = nextX;
            /* block end [id:"Sprite1_block_91"] */
            /* block comment [block:"Sprite1_block_92" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Sprite1_block_92" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            yy = nextY;
            /* block end [id:"Sprite1_block_92"] */

            /* block comment [block:"Sprite1_block_93" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Sprite1_block_93" category:"Variables" label:"set s" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Scroller2d s = Scroller2d.getInstance();
            /* block end [id:"Sprite1_block_93"] */
            /* block comment [block:"Sprite1_block_94" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Sprite1_block_94" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            s.x = -xx;
            /* block end [id:"Sprite1_block_94"] */
            /* block comment [block:"Sprite1_block_95" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Sprite1_block_95" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            s.y = -yy;
            /* block end [id:"Sprite1_block_95"] */
            /* block comment [block:"Sprite1_block_96" text:"Calls the update method using its declared arguments."] */
            /* block start [id:"Sprite1_block_96" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
            s.update();
            /* block end [id:"Sprite1_block_96"] */
        }
        /* block end [id:"Sprite1_block_50"] */
        /* block comment [block:"Sprite1_block_97" text:"Runs the update action. Values in this block are editable inputs."] */
        /* block start [id:"Sprite1_block_97" category:"My Blocks" label:"update" shape:"command" docs:"Runs the update action. Values in this block are editable inputs."] */
        super.update();
        /* block end [id:"Sprite1_block_97"] */
    }

    public void movecDiagonal(int amt, int xDir, int yDir) {
        /* block comment [block:"Sprite1_block_98" text:"Runs the step move action. Values in this block are editable inputs."] */
        /* block start [id:"Sprite1_block_98" category:"Motion" label:"step move" shape:"command" docs:"Runs the step move action. Values in this block are editable inputs."] */
        stepMove(xDir * amt, yDir * amt);
        /* block end [id:"Sprite1_block_98"] */
        /* block comment [block:"Sprite1_block_99" text:"Calls the syncScroll method using its declared arguments."] */
        /* block start [id:"Sprite1_block_99" category:"My Blocks" label:"call sync scroll" shape:"command" docs:"Calls the syncScroll method using its declared arguments."] */
        syncScroll();
        /* block end [id:"Sprite1_block_99"] */
        /* block comment [block:"Sprite1_block_100" text:"Runs the update action. Values in this block are editable inputs."] */
        /* block start [id:"Sprite1_block_100" category:"My Blocks" label:"update" shape:"command" docs:"Runs the update action. Values in this block are editable inputs."] */
        super.update();
        /* block end [id:"Sprite1_block_100"] */
    }

    private void syncScroll() {
        /* block comment [block:"Sprite1_block_101" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Sprite1_block_101" category:"Variables" label:"set s" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Scroller2d s = Scroller2d.getInstance();
        /* block end [id:"Sprite1_block_101"] */
        /* block comment [block:"Sprite1_block_102" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Sprite1_block_102" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        s.x = -xx;
        /* block end [id:"Sprite1_block_102"] */
        /* block comment [block:"Sprite1_block_103" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Sprite1_block_103" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        s.y = -yy;
        /* block end [id:"Sprite1_block_103"] */
        /* block comment [block:"Sprite1_block_104" text:"Calls the update method using its declared arguments."] */
        /* block start [id:"Sprite1_block_104" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
        s.update();
        /* block end [id:"Sprite1_block_104"] */
    }

    public void cmovec(int amt, int dir, int tim) {
        // Kept for compat; steps without per-step scene rebuilds.
        /* block comment [block:"Sprite1_block_105" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Sprite1_block_105" category:"Variables" label:"set dx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int dx = /* param start [id:"Sprite1_value_254" type:"number"] */0/* param end [id:"Sprite1_value_254"] */, dy = /* param start [id:"Sprite1_value_255" type:"number"] */0/* param end [id:"Sprite1_value_255"] */;
        /* block end [id:"Sprite1_block_105"] */
        /* block comment [block:"Sprite1_block_51" text:"Selects and runs the case that matches the input value."] */
        /* block start [id:"Sprite1_block_51" category:"Control" label:"choose a case" shape:"control" docs:"Selects and runs the case that matches the input value."] */
        switch (dir) {
            /* block comment [block:"Sprite1_block_106" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Sprite1_block_106" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            case /* param start [id:"Sprite1_value_256" type:"number"] */0/* param end [id:"Sprite1_value_256"] */: dy = amt;
            /* block end [id:"Sprite1_block_106"] */ /* block comment [block:"Sprite1_block_107" text:"Stops the nearest loop or switch block."] */
            /* block start [id:"Sprite1_block_107" category:"Control" label:"break loop" shape:"command" docs:"Stops the nearest loop or switch block."] */
            break;
            /* block end [id:"Sprite1_block_107"] */
            /* block comment [block:"Sprite1_block_108" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Sprite1_block_108" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            case /* param start [id:"Sprite1_value_257" type:"number"] */1/* param end [id:"Sprite1_value_257"] */: dx = amt;
            /* block end [id:"Sprite1_block_108"] */ /* block comment [block:"Sprite1_block_109" text:"Stops the nearest loop or switch block."] */
            /* block start [id:"Sprite1_block_109" category:"Control" label:"break loop" shape:"command" docs:"Stops the nearest loop or switch block."] */
            break;
            /* block end [id:"Sprite1_block_109"] */
            /* block comment [block:"Sprite1_block_110" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Sprite1_block_110" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            case /* param start [id:"Sprite1_value_258" type:"number"] */2/* param end [id:"Sprite1_value_258"] */: dy = -amt;
            /* block end [id:"Sprite1_block_110"] */ /* block comment [block:"Sprite1_block_111" text:"Stops the nearest loop or switch block."] */
            /* block start [id:"Sprite1_block_111" category:"Control" label:"break loop" shape:"command" docs:"Stops the nearest loop or switch block."] */
            break;
            /* block end [id:"Sprite1_block_111"] */
            /* block comment [block:"Sprite1_block_112" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Sprite1_block_112" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            case /* param start [id:"Sprite1_value_259" type:"number"] */3/* param end [id:"Sprite1_value_259"] */: dx = -amt;
            /* block end [id:"Sprite1_block_112"] */ /* block comment [block:"Sprite1_block_113" text:"Stops the nearest loop or switch block."] */
            /* block start [id:"Sprite1_block_113" category:"Control" label:"break loop" shape:"command" docs:"Stops the nearest loop or switch block."] */
            break;
            /* block end [id:"Sprite1_block_113"] */
        }
        /* block end [id:"Sprite1_block_51"] */
        /* block comment [block:"Sprite1_block_114" text:"Runs the step move action. Values in this block are editable inputs."] */
        /* block start [id:"Sprite1_block_114" category:"Motion" label:"step move" shape:"command" docs:"Runs the step move action. Values in this block are editable inputs."] */
        stepMove(dx, dy);
        /* block end [id:"Sprite1_block_114"] */
        /* block comment [block:"Sprite1_block_115" text:"Calls the syncScroll method using its declared arguments."] */
        /* block start [id:"Sprite1_block_115" category:"My Blocks" label:"call sync scroll" shape:"command" docs:"Calls the syncScroll method using its declared arguments."] */
        syncScroll();
        /* block end [id:"Sprite1_block_115"] */
        /* block comment [block:"Sprite1_block_116" text:"Runs the update action. Values in this block are editable inputs."] */
        /* block start [id:"Sprite1_block_116" category:"My Blocks" label:"update" shape:"command" docs:"Runs the update action. Values in this block are editable inputs."] */
        super.update();
        /* block end [id:"Sprite1_block_116"] */
    }

    public void cmovecDiagonal(int amt, int xDir, int yDir, int tim) {
        // Kept for compat; single stepped move, one scene rebuild.
        /* block comment [block:"Sprite1_block_117" text:"Runs the step move action. Values in this block are editable inputs."] */
        /* block start [id:"Sprite1_block_117" category:"Motion" label:"step move" shape:"command" docs:"Runs the step move action. Values in this block are editable inputs."] */
        stepMove(xDir * amt, yDir * amt);
        /* block end [id:"Sprite1_block_117"] */
        /* block comment [block:"Sprite1_block_118" text:"Calls the syncScroll method using its declared arguments."] */
        /* block start [id:"Sprite1_block_118" category:"My Blocks" label:"call sync scroll" shape:"command" docs:"Calls the syncScroll method using its declared arguments."] */
        syncScroll();
        /* block end [id:"Sprite1_block_118"] */
        /* block comment [block:"Sprite1_block_119" text:"Runs the update action. Values in this block are editable inputs."] */
        /* block start [id:"Sprite1_block_119" category:"My Blocks" label:"update" shape:"command" docs:"Runs the update action. Values in this block are editable inputs."] */
        super.update();
        /* block end [id:"Sprite1_block_119"] */
    }

    public void performAttack() {
        // Hybrid melee: cooldown-gated d20 + bonus vs enemy AC (Space = basic 1d8 attack)
        /* block comment [block:"Sprite1_block_120" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Sprite1_block_120" category:"Variables" label:"set now" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        long now = System.currentTimeMillis();
        /* block end [id:"Sprite1_block_120"] */
        /* block comment [block:"Sprite1_block_121" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Sprite1_block_121" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Sprite1_block_52" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Sprite1_block_52" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (now - lastAttackMs < ATTACK_COOLDOWN_MS) return;
        /* block end [id:"Sprite1_block_121"] */
        /* block end [id:"Sprite1_block_52"] */
        /* block comment [block:"Sprite1_block_122" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Sprite1_block_122" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        lastAttackMs = now;
        /* block end [id:"Sprite1_block_122"] */

        /* block comment [block:"Sprite1_block_123" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Sprite1_block_123" category:"Variables" label:"set attackDist" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int attackDist = /* param start [id:"Sprite1_value_260" type:"number"] */60/* param end [id:"Sprite1_value_260"] */;
        /* block end [id:"Sprite1_block_123"] */
        /* block comment [block:"Sprite1_block_124" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Sprite1_block_124" category:"Variables" label:"set bonus" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int bonus = PlayerStats.ready ? PlayerStats.effectiveMelee() : /* param start [id:"Sprite1_value_261" type:"number"] */0/* param end [id:"Sprite1_value_261"] */;
        /* block end [id:"Sprite1_block_124"] */
        /* block comment [block:"Sprite1_block_125" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Sprite1_block_125" category:"Variables" label:"set strMod" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int strMod = (PlayerStats.ready && PlayerStats.stats != /* param start [id:"Sprite1_value_262" type:"value"] */null/* param end [id:"Sprite1_value_262"] */) ? PlayerStats.stats.strengthModifier : /* param start [id:"Sprite1_value_263" type:"number"] */0/* param end [id:"Sprite1_value_263"] */;
        /* block end [id:"Sprite1_block_125"] */
        /* block comment [block:"Sprite1_block_126" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Sprite1_block_126" category:"Variables" label:"set dmgMod" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int dmgMod = strMod + PlayerStats.bonusDmg;
        /* block end [id:"Sprite1_block_126"] */

        /* block comment [block:"Sprite1_block_53" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"Sprite1_block_53" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (EnemySprite e : EnemyManager.getInstance().getEnemies()) {
            /* block comment [block:"Sprite1_block_127" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"Sprite1_block_127" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"Sprite1_block_54" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Sprite1_block_54" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (!e.isVisible()) continue;
            /* block end [id:"Sprite1_block_127"] */
            /* block end [id:"Sprite1_block_54"] */
            /* block comment [block:"Sprite1_block_128" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Sprite1_block_128" category:"Variables" label:"set dx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double dx = e.xx - xx;
            /* block end [id:"Sprite1_block_128"] */
            /* block comment [block:"Sprite1_block_129" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Sprite1_block_129" category:"Variables" label:"set dy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double dy = e.yy - yy;
            /* block end [id:"Sprite1_block_129"] */

            /* block comment [block:"Sprite1_block_55" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Sprite1_block_55" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (dx * dx + dy * dy < attackDist * attackDist) {
                /* block comment [block:"Sprite1_block_130" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"Sprite1_block_130" category:"Variables" label:"set res" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int res = Combat.meleeStrike(bonus, e.armourClass);
                /* block end [id:"Sprite1_block_130"] */
                /* block comment [block:"Sprite1_block_56" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"Sprite1_block_56" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (res > /* param start [id:"Sprite1_value_264" type:"number"] */0/* param end [id:"Sprite1_value_264"] */) {
                    /* block comment [block:"Sprite1_block_131" text:"Calls the takeDamage method using its declared arguments."] */
                    /* block start [id:"Sprite1_block_131" category:"My Blocks" label:"call take damage" shape:"command" docs:"Calls the takeDamage method using its declared arguments."] */
                    e.takeDamage(Combat.rollDamage(/* param start [id:"Sprite1_value_265" type:"number"] */8/* param end [id:"Sprite1_value_265"] */, dmgMod, res == /* param start [id:"Sprite1_value_266" type:"number"] */2/* param end [id:"Sprite1_value_266"] */));
                    /* block end [id:"Sprite1_block_131"] */
                    // Knockback in 5px steps so enemies slide along walls
                    // instead of getting embedded inside them.
                    /* block comment [block:"Sprite1_block_132" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    /* block start [id:"Sprite1_block_132" category:"Variables" label:"set dist" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    double dist = Math.sqrt(dx * dx + dy * dy);
                    /* block end [id:"Sprite1_block_132"] */
                    /* block comment [block:"Sprite1_block_57" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"Sprite1_block_57" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (dist > /* param start [id:"Sprite1_value_267" type:"number"] */1/* param end [id:"Sprite1_value_267"] */) {
                        /* block comment [block:"Sprite1_block_133" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                        /* block start [id:"Sprite1_block_133" category:"Variables" label:"set nx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                        double nx = dx / dist;
                        /* block end [id:"Sprite1_block_133"] */
                        /* block comment [block:"Sprite1_block_134" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                        /* block start [id:"Sprite1_block_134" category:"Variables" label:"set ny" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                        double ny = dy / dist;
                        /* block end [id:"Sprite1_block_134"] */
                        /* block comment [block:"Sprite1_block_58" text:"Repeats the nested operations for each loop turn."] */
                        /* block start [id:"Sprite1_block_58" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
                        for (int done = /* param start [id:"Sprite1_value_268" type:"number"] */0/* param end [id:"Sprite1_value_268"] */; done < /* param start [id:"Sprite1_value_269" type:"number"] */20/* param end [id:"Sprite1_value_269"] */; done += /* param start [id:"Sprite1_value_270" type:"number"] */5/* param end [id:"Sprite1_value_270"] */) {
                            /* block comment [block:"Sprite1_block_135" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                            /* block start [id:"Sprite1_block_135" category:"Variables" label:"set sx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                            int sx = (int) Math.round(nx * Math.min(/* param start [id:"Sprite1_value_271" type:"number"] */5/* param end [id:"Sprite1_value_271"] */, /* param start [id:"Sprite1_value_272" type:"number"] */20/* param end [id:"Sprite1_value_272"] */ - done));
                            /* block end [id:"Sprite1_block_135"] */
                            /* block comment [block:"Sprite1_block_136" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                            /* block start [id:"Sprite1_block_136" category:"Variables" label:"set sy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                            int sy = (int) Math.round(ny * Math.min(/* param start [id:"Sprite1_value_273" type:"number"] */5/* param end [id:"Sprite1_value_273"] */, /* param start [id:"Sprite1_value_274" type:"number"] */20/* param end [id:"Sprite1_value_274"] */ - done));
                            /* block end [id:"Sprite1_block_136"] */
                            /* block comment [block:"Sprite1_block_137" text:"Runs the if action. Values in this block are editable inputs."] */
                            /* block start [id:"Sprite1_block_137" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                            /* block comment [block:"Sprite1_block_59" text:"Checks a Boolean condition and runs the body when it is true."] */
                            /* block start [id:"Sprite1_block_59" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                            if ((sx == /* param start [id:"Sprite1_value_275" type:"number"] */0/* param end [id:"Sprite1_value_275"] */ && sy == /* param start [id:"Sprite1_value_276" type:"number"] */0/* param end [id:"Sprite1_value_276"] */) || !e.tryMove(sx, sy)) break;
                            /* block end [id:"Sprite1_block_137"] */
                            /* block end [id:"Sprite1_block_59"] */
                        }
                        /* block end [id:"Sprite1_block_58"] */
                    }
                    /* block end [id:"Sprite1_block_57"] */
                }
                /* block end [id:"Sprite1_block_56"] */
            }
            /* block end [id:"Sprite1_block_55"] */
        }
        /* block end [id:"Sprite1_block_53"] */
    }

    /* slot end [id:"2_members"] */
}