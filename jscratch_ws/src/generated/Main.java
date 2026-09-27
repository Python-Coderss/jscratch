package generated;

import com.jscratch.*;

/**
* Boot sequence. Called by GameBootstrap/ProjectRunner inside Stage.run.
*
* <p>Game states and who is visible when:
* <ul>
*   <li>{@code CHARACTER_CREATE} — only {@code CharacterCreation}. World
*       tiles/minimap ({@code Scroller2d}), stairs/chest/walls
*       ({@code FloorManager}), enemies/loot and the player are hidden or
*       not yet spawned. See {@code GameState}.</li>
*   <li>{@code GAMEPLAY} — world + dungeon + HUD + player/enemies. Entered
*       via {@code CharacterCreation.finish → GameState.toGameplay}, which
*       broadcasts {@code "enterGameplay"} (world/dungeon reveal) followed
*       by {@code "characterReady"} (player spawn here).</li>
* </ul>
*
* <p>Build order and who owns what:
* <ol>
*   <li>{@code GameState.toCharacterCreate} — menu owns the screen.</li>
*   <li>{@code Scroller2d.getInstance()} — world tiles, minimap layers,
*       collider/floor registries. Starts hidden. Must exist before any BSprite.</li>
*   <li>{@code FloorManager.getInstance()} — tints tiles, builds walls into
*       Scroller2d colliders, configures EnemyManager, places Stairs/Chest.
*       Starts hidden; skips floor music/HUD until gameplay.</li>
*   <li>{@code CharacterCreation} sprite — builds {@code PlayerStats}
*       (sets {@code ready=true}), flips {@code GameState} to gameplay and
*       broadcasts {@code "characterReady"}.</li>
*   <li>{@code MusicDirector.title()} — title theme until the hero exists.</li>
*   <li>On {@code "characterReady"}: spawns {@code Sprite1} (the player
*       loop, which then drives EnemyManager/FloorManager/MusicDirector
*       every frame) and switches music to the current floor.</li>
* </ol>
*/
public class Main {

    public static void main(String[] args) {
        /* block comment [block:"Main_block_9" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Main_block_9" category:"Variables" label:"set stage" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage stage = Stage.getInstance();
        /* block end [id:"Main_block_9"] */
        // Menu owns the screen: world/dungeon boot hidden.
        /* block comment [block:"Main_block_10" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Main_block_10" category:"Variables" label:"set toCharacterCreate" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        GameState.toCharacterCreate();
        /* block end [id:"Main_block_10"] */
        /* block comment [block:"Main_block_11" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Main_block_11" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Scroller2d.getInstance();
        /* block end [id:"Main_block_11"] */
        /* block comment [block:"Main_block_12" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Main_block_12" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        FloorManager.getInstance();
        /* block end [id:"Main_block_12"] */
        /* block comment [block:"Main_block_13" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Main_block_13" category:"Variables" label:"set menu" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        CharacterCreation menu = new CharacterCreation();
        /* block end [id:"Main_block_13"] */
        /* block comment [block:"Main_block_14" text:"Calls the addSprite method using its declared arguments."] */
        /* block start [id:"Main_block_14" category:"My Blocks" label:"call add sprite" shape:"command" docs:"Calls the addSprite method using its declared arguments."] */
        stage.addSprite(menu);
        /* block end [id:"Main_block_14"] */
        /* block comment [block:"Main_block_15" text:"Runs the go to front action. Values in this block are editable inputs."] */
        /* block start [id:"Main_block_15" category:"Motion" label:"go to front" shape:"command" docs:"Runs the go to front action. Values in this block are editable inputs."] */
        stage.goToFront(menu);
        /* block end [id:"Main_block_15"] */
        /* block comment [block:"Main_block_16" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Main_block_16" category:"Variables" label:"set title" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        MusicDirector.title();
        /* block end [id:"Main_block_16"] */
        /* block comment [block:"Main_block_17" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Main_block_17" category:"Variables" label:"set invPanel" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        InventoryPanel invPanel = new InventoryPanel();
        /* block end [id:"Main_block_17"] */
        /* block comment [block:"Main_block_18" text:"Calls the addSprite method using its declared arguments."] */
        /* block start [id:"Main_block_18" category:"My Blocks" label:"call add sprite" shape:"command" docs:"Calls the addSprite method using its declared arguments."] */
        stage.addSprite(invPanel);
        /* block end [id:"Main_block_18"] */
        /* block comment [block:"Main_block_19" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Main_block_19" category:"Variables" label:"set invPanel" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Sprite1.invPanel = invPanel;
        /* block end [id:"Main_block_19"] */
        // Re-assert menu-only visibility: world ctors above already hid
        // themselves, this broadcast covers any stragglers.
        /* block comment [block:"Main_block_20" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Main_block_20" category:"Variables" label:"set toCharacterCreate" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        GameState.toCharacterCreate();
        /* block end [id:"Main_block_20"] */
        /* block comment [block:"Main_block_1" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"Main_block_1" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try { /* block comment [block:"Main_block_21" text:"Runs the go to front action. Values in this block are editable inputs."] */
            /* block start [id:"Main_block_21" category:"Motion" label:"go to front" shape:"command" docs:"Runs the go to front action. Values in this block are editable inputs."] */
            stage.goToFront(menu);
            /* block end [id:"Main_block_21"] */ }
        /* block end [id:"Main_block_1"] */ catch (Exception ignored) {}
        // The music streamer is a daemon thread that outlives the window
        // (the editor JVM stays up), so arm a close hook here in project —
        // the engine is untouched.
        /* block comment [block:"Main_block_22" text:"Runs the arm music stop on close action. Values in this block are editable inputs."] */
        /* block start [id:"Main_block_22" category:"Sound" label:"arm music stop on close" shape:"command" docs:"Runs the arm music stop on close action. Values in this block are editable inputs."] */
        armMusicStopOnClose();
        /* block end [id:"Main_block_22"] */
        CharacterCreation[] menuRef = new CharacterCreation[]{menu};
        stage.whenIReceive("characterReady", () -> {
            // Every run gets a fresh player driver; the character screen is reused.
            stage.addSprite(new Sprite1());
            MusicDirector.onFloor(FloorManager.getInstance().getCurrentDef());
            EnemyManager.getInstance().startSpawnGrace(10000L);
        }/* block comment [block:"Main_block_23" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Main_block_23" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        );
        /* block end [id:"Main_block_23"] */
        stage.whenIReceive("newCharacterRun", () -> {
            // Stop the old player loop before creating the new player instance.
            /* block comment [block:"Main_block_2" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Main_block_2" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (Sprite1.instance != /* param start [id:"Main_value_30" type:"value"] */null/* param end [id:"Main_value_30"] */) Sprite1.instance.stopGameLoop();
            /* block end [id:"Main_block_2"] */
            stage.broadcast("hideControls");
        }/* block comment [block:"Main_block_24" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Main_block_24" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        );
        /* block end [id:"Main_block_24"] */
        stage.whenIReceive("toggleControls", () -> {
            /* block comment [block:"Main_block_3" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Main_block_3" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (menuRef[/* param start [id:"Main_value_31" type:"number"] */0/* param end [id:"Main_value_31"] */] != /* param start [id:"Main_value_32" type:"value"] */null/* param end [id:"Main_value_32"] */) {
                /* block comment [block:"Main_block_4" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"Main_block_4" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (GameState.areControlsVisible()) menuRef[/* param start [id:"Main_value_33" type:"number"] */0/* param end [id:"Main_value_33"] */].closeControls();
                else menuRef[/* param start [id:"Main_value_34" type:"number"] */0/* param end [id:"Main_value_34"] */].openControls();
                /* block end [id:"Main_block_4"] */
            }
            /* block end [id:"Main_block_3"] */
        }/* block comment [block:"Main_block_25" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Main_block_25" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        );
        /* block end [id:"Main_block_25"] */
        /* slot start [id:"1method_body" tags:"inInstanceMethod"] */


        /* slot start [id:"method_body" tags:"stage"] */

        /* slot end [id:"method_body"] */
        /* slot end [id:"1method_body"] */
    }

    /**
    * Stops the music streamer when the stage window closes. The live
    * "JScratch Stage" frame is found by title among displayable frames
    * (disposed rerun frames are skipped, so hooks never double-arm).
    * Fires for both DISPOSE_ON_CLOSE (editor) and EXIT_ON_CLOSE (export).
    */
    private static void armMusicStopOnClose() {
        /* block comment [block:"Main_block_5" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"Main_block_5" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (java.awt.Frame f : java.awt.Frame.getFrames()) {
            /* block comment [block:"Main_block_26" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"Main_block_26" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"Main_block_6" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Main_block_6" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (!(f instanceof javax.swing.JFrame)) continue;
            /* block end [id:"Main_block_26"] */
            /* block end [id:"Main_block_6"] */
            /* block comment [block:"Main_block_27" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"Main_block_27" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"Main_block_7" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Main_block_7" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (!/* param start [id:"Main_value_35" type:"text"] */"JScratch Stage"/* param end [id:"Main_value_35"] */.equals(((javax.swing.JFrame) f).getTitle())) continue;
            /* block end [id:"Main_block_27"] */
            /* block end [id:"Main_block_7"] */
            /* block comment [block:"Main_block_28" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"Main_block_28" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"Main_block_8" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Main_block_8" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (!f.isDisplayable()) continue;
            /* block end [id:"Main_block_28"] */
            /* block end [id:"Main_block_8"] */
            f.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override public void windowClosing(java.awt.event.WindowEvent e) {
                    MusicManager.stopMusic();
                }
            }/* block comment [block:"Main_block_29" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"Main_block_29" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            );
            /* block end [id:"Main_block_29"] */
        }
        /* block end [id:"Main_block_5"] */
    }

}
