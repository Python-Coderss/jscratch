package generated;

import com.jscratch.Stage;

/**
* Central game-state switch. Only one state is active at a time, and each
* state owns a different visible sprite set:
*
* <ul>
*   <li>{@code CHARACTER_CREATE} — only {@code CharacterCreation} (plus
*       title music). World tiles, minimap, stairs/chest/walls, enemies,
*       loot, player and HUD bars are all hidden.</li>
*   <li>{@code GAMEPLAY} — world + dungeon + HUD + player/enemies. The
*       character menu hides itself.</li>
* </ul>
*
* <p>Call graph:
* <ul>
*   <li>Set once at boot by {@code Main} ({@code toCharacterCreate}).</li>
*   <li>Flipped by {@code CharacterCreation.finish} ({@code toGameplay}),
*       which then also broadcasts {@code "characterReady"} so
*       {@code Main} spawns {@code Sprite1}.</li>
*   <li>World owners listen for {@code "enterGameplay"} /
*       {@code "enterCharacterCreate"}: {@code Scroller2d} (tiles/minimap),
*       {@code FloorManager} (stairs/chest/walls), {@code InventoryPanel}.</li>
*   <li>Tick guards read {@link #isGameplay()} directly: {@code EnemyManager},
*       {@code FloorManager.update}, {@code Sprite1} loop.</li>
* </ul>
*
* Java 8 compatible.
*/
public final class GameState {
    public enum State {
        CHARACTER_CREATE,
        GAMEPLAY
    }

    private static volatile State current = State.CHARACTER_CREATE;
    /** Modal panels pause game updates without changing the underlying screen. */
    private static volatile boolean controlsVisible = false;
    private static volatile boolean gameOver = false;

    private GameState() {}

    public static State get() {
        /* block comment [block:"GameState_block_4" text:"Returns a value or ends the current method."] */
        /* block start [id:"GameState_block_4" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return current;
        /* block end [id:"GameState_block_4"] */
    }

    public static boolean isGameplay() {
        /* block comment [block:"GameState_block_5" text:"Returns a value or ends the current method."] */
        /* block start [id:"GameState_block_5" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return current == State.GAMEPLAY;
        /* block end [id:"GameState_block_5"] */
    }

    public static boolean isCharacterCreate() {
        /* block comment [block:"GameState_block_6" text:"Returns a value or ends the current method."] */
        /* block start [id:"GameState_block_6" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return current == State.CHARACTER_CREATE;
        /* block end [id:"GameState_block_6"] */
    }

    /** True while an informational menu or death panel should freeze combat. */
    public static boolean isPaused() { /* block comment [block:"GameState_block_7" text:"Returns a value or ends the current method."] */
        /* block start [id:"GameState_block_7" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return controlsVisible || gameOver;
        /* block end [id:"GameState_block_7"] */ }
    public static boolean areControlsVisible() { /* block comment [block:"GameState_block_8" text:"Returns a value or ends the current method."] */
        /* block start [id:"GameState_block_8" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return controlsVisible;
        /* block end [id:"GameState_block_8"] */ }
    public static boolean isGameOver() { /* block comment [block:"GameState_block_9" text:"Returns a value or ends the current method."] */
        /* block start [id:"GameState_block_9" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return gameOver;
        /* block end [id:"GameState_block_9"] */ }

    /** Show/hide the controls card, pausing the game loop behind it. */
    public static void setControlsVisible(boolean visible) {
        /* block comment [block:"GameState_block_10" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"GameState_block_10" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        controlsVisible = visible;
        /* block end [id:"GameState_block_10"] */
        /* block comment [block:"GameState_block_11" text:"Calls the publish method using its declared arguments."] */
        /* block start [id:"GameState_block_11" category:"My Blocks" label:"call publish" shape:"command" docs:"Calls the publish method using its declared arguments."] */
        publish();
        /* block end [id:"GameState_block_11"] */
    }

    /** Enter a paused game-over state while keeping the dungeon visible behind the dialog. */
    public static void setGameOver(boolean over) {
        /* block comment [block:"GameState_block_12" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"GameState_block_12" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        gameOver = over;
        /* block end [id:"GameState_block_12"] */
        /* block comment [block:"GameState_block_13" text:"Calls the publish method using its declared arguments."] */
        /* block start [id:"GameState_block_13" category:"My Blocks" label:"call publish" shape:"command" docs:"Calls the publish method using its declared arguments."] */
        publish();
        /* block end [id:"GameState_block_13"] */
    }

    /** Boot / reset into the character menu. Hides gameplay sprites. */
    public static void toCharacterCreate() {
        /* block comment [block:"GameState_block_14" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"GameState_block_14" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        current = State.CHARACTER_CREATE;
        /* block end [id:"GameState_block_14"] */
        /* block comment [block:"GameState_block_15" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"GameState_block_15" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        controlsVisible = /* param start [id:"GameState_value_25" type:"boolean"] */false/* param end [id:"GameState_value_25"] */;
        /* block end [id:"GameState_block_15"] */
        /* block comment [block:"GameState_block_16" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"GameState_block_16" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        gameOver = /* param start [id:"GameState_value_26" type:"boolean"] */false/* param end [id:"GameState_value_26"] */;
        /* block end [id:"GameState_block_16"] */
        /* block comment [block:"GameState_block_17" text:"Calls the publish method using its declared arguments."] */
        /* block start [id:"GameState_block_17" category:"My Blocks" label:"call publish" shape:"command" docs:"Calls the publish method using its declared arguments."] */
        publish();
        /* block end [id:"GameState_block_17"] */
        /* block comment [block:"GameState_block_1" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"GameState_block_1" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try {
            /* block comment [block:"GameState_block_18" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"GameState_block_18" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Stage.getInstance().broadcast(/* param start [id:"GameState_value_27" type:"text"] */"enterCharacterCreate"/* param end [id:"GameState_value_27"] */);
            /* block end [id:"GameState_block_18"] */
        }
        /* block end [id:"GameState_block_1"] */ catch (Exception ignored) {}
    }

    /** Character finished. Hides the menu, reveals gameplay sprites. */
    public static void toGameplay() {
        /* block comment [block:"GameState_block_19" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"GameState_block_19" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        current = State.GAMEPLAY;
        /* block end [id:"GameState_block_19"] */
        /* block comment [block:"GameState_block_20" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"GameState_block_20" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        controlsVisible = /* param start [id:"GameState_value_28" type:"boolean"] */false/* param end [id:"GameState_value_28"] */;
        /* block end [id:"GameState_block_20"] */
        /* block comment [block:"GameState_block_21" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"GameState_block_21" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        gameOver = /* param start [id:"GameState_value_29" type:"boolean"] */false/* param end [id:"GameState_value_29"] */;
        /* block end [id:"GameState_block_21"] */
        /* block comment [block:"GameState_block_22" text:"Calls the publish method using its declared arguments."] */
        /* block start [id:"GameState_block_22" category:"My Blocks" label:"call publish" shape:"command" docs:"Calls the publish method using its declared arguments."] */
        publish();
        /* block end [id:"GameState_block_22"] */
        /* block comment [block:"GameState_block_2" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"GameState_block_2" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try {
            /* block comment [block:"GameState_block_23" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"GameState_block_23" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Stage.getInstance().broadcast(/* param start [id:"GameState_value_30" type:"text"] */"enterGameplay"/* param end [id:"GameState_value_30"] */);
            /* block end [id:"GameState_block_23"] */
        }
        /* block end [id:"GameState_block_2"] */ catch (Exception ignored) {}
    }

    private static void publish() {
        /* block comment [block:"GameState_block_3" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"GameState_block_3" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try {
            /* block comment [block:"GameState_block_24" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"GameState_block_24" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Stage.getInstance().setVariable(/* param start [id:"GameState_value_31" type:"text"] */"GameState"/* param end [id:"GameState_value_31"] */, current.name());
            /* block end [id:"GameState_block_24"] */
        }
        /* block end [id:"GameState_block_3"] */ catch (Exception ignored) {}
    }
}
