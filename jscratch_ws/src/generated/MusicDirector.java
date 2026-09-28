package generated;

/**
* Decides WHAT plays and WHEN. Thin policy layer over MusicManager:
* title theme, per-floor calm/combat pairs, boss lock-in, victory sting,
* death silence, and a global mute toggle.
*
* <p>Call graph / interactions:
* <ul>
*   <li>Called by: {@code Main} ({@code title}), {@code FloorManager}
*       ({@code onFloor} per floor load), {@code Sprite1} loop
*       ({@code update(aggro)} per frame + {@code toggleMute} on M),
*       {@code FloorManager.descend} path ({@code victory}),
*       player death watch ({@code death}).</li>
*   <li>Calls into {@code MusicManager.playMusic/updateCombat/stopMusic/
*       setEnabled}; per-frame {@code update} is cheap (string compare +
*       throttled 2s retry for pre-gesture audio block).</li>
* </ul>
*
* update() is safe to call every frame: it only touches the stream when
* the desired song differs, and re-tries a blocked start at most once
* every 2 seconds (audio is unavailable until the first user gesture,
* and blind retries would spam threads).
*
* Java 8 compatible.
*/
public final class MusicDirector {
    private static String calmPath = null;
    private static String combatPath = null;
    private static boolean combatMode = false;
    private static boolean muted = false;
    private static long lastRetryMs = 0;
    private static final long RETRY_MS = 2000;

    private MusicDirector() {}

    /** Title / character-creation theme. */
    public static void title() {
        /* block comment [block:"MusicDirector_block_10" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"MusicDirector_block_10" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        calmPath = MusicManager.TITLE;
        /* block end [id:"MusicDirector_block_10"] */
        /* block comment [block:"MusicDirector_block_11" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"MusicDirector_block_11" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        combatPath = MusicManager.TITLE;
        /* block end [id:"MusicDirector_block_11"] */
        /* block comment [block:"MusicDirector_block_12" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"MusicDirector_block_12" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        combatMode = /* param start [id:"MusicDirector_value_44" type:"boolean"] */false/* param end [id:"MusicDirector_value_44"] */;
        /* block end [id:"MusicDirector_block_12"] */
        /* block comment [block:"MusicDirector_block_13" text:"Calls the start method using its declared arguments."] */
        /* block start [id:"MusicDirector_block_13" category:"My Blocks" label:"call start" shape:"command" docs:"Calls the start method using its declared arguments."] */
        start(calmPath);
        /* block end [id:"MusicDirector_block_13"] */
    }

    /** Called by FloorManager whenever a floor (re)loads. */
    public static void onFloor(FloorManager.FloorDef def) {
        /* block comment [block:"MusicDirector_block_14" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"MusicDirector_block_14" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"MusicDirector_block_1" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"MusicDirector_block_1" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (def == /* param start [id:"MusicDirector_value_45" type:"value"] */null/* param end [id:"MusicDirector_value_45"] */) return;
        /* block end [id:"MusicDirector_block_14"] */
        /* block end [id:"MusicDirector_block_1"] */
        /* block comment [block:"MusicDirector_block_15" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"MusicDirector_block_15" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        calmPath = def.calmTrack;
        /* block end [id:"MusicDirector_block_15"] */
        /* block comment [block:"MusicDirector_block_16" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"MusicDirector_block_16" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        combatPath = def.combatTrack;
        /* block end [id:"MusicDirector_block_16"] */
        /* block comment [block:"MusicDirector_block_17" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"MusicDirector_block_17" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        combatMode = /* param start [id:"MusicDirector_value_46" type:"boolean"] */false/* param end [id:"MusicDirector_value_46"] */;
        /* block end [id:"MusicDirector_block_17"] */
        /* block comment [block:"MusicDirector_block_18" text:"Calls the start method using its declared arguments."] */
        /* block start [id:"MusicDirector_block_18" category:"My Blocks" label:"call start" shape:"command" docs:"Calls the start method using its declared arguments."] */
        start(calmPath);
        /* block end [id:"MusicDirector_block_18"] */
    }

    /**
    * Per-frame hook from the player loop. Switches calm/combat with
    * aggro and self-heals a stream that never started (pre-gesture block).
    */
    public static void update(boolean enemiesNear) {
        /* block comment [block:"MusicDirector_block_19" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"MusicDirector_block_19" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"MusicDirector_block_2" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"MusicDirector_block_2" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (muted) return;
        /* block end [id:"MusicDirector_block_19"] */
        /* block end [id:"MusicDirector_block_2"] */
        /* block comment [block:"MusicDirector_block_20" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MusicDirector_block_20" category:"Variables" label:"set want" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        String want = enemiesNear ? combatPath : calmPath;
        /* block end [id:"MusicDirector_block_20"] */
        /* block comment [block:"MusicDirector_block_21" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"MusicDirector_block_21" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"MusicDirector_block_3" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"MusicDirector_block_3" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (want == /* param start [id:"MusicDirector_value_47" type:"value"] */null/* param end [id:"MusicDirector_value_47"] */) return;
        /* block end [id:"MusicDirector_block_21"] */
        /* block end [id:"MusicDirector_block_3"] */
        /* block comment [block:"MusicDirector_block_4" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"MusicDirector_block_4" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (want.equals(MusicManager.current()) && MusicManager.isPlaying()) {
            /* block comment [block:"MusicDirector_block_22" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"MusicDirector_block_22" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            combatMode = enemiesNear;
            /* block end [id:"MusicDirector_block_22"] */
            /* block comment [block:"MusicDirector_block_23" text:"Returns a value or ends the current method."] */
            /* block start [id:"MusicDirector_block_23" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"MusicDirector_block_23"] */
        }
        /* block end [id:"MusicDirector_block_4"] */
        /* block comment [block:"MusicDirector_block_24" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MusicDirector_block_24" category:"Variables" label:"set now" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        long now = System.currentTimeMillis();
        /* block end [id:"MusicDirector_block_24"] */
        /* block comment [block:"MusicDirector_block_5" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"MusicDirector_block_5" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!MusicManager.isPlaying()) {
            // Blocked or stopped: throttle retries, then (re)start directly.
            /* block comment [block:"MusicDirector_block_25" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"MusicDirector_block_25" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"MusicDirector_block_6" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"MusicDirector_block_6" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (now - lastRetryMs < RETRY_MS) return;
            /* block end [id:"MusicDirector_block_25"] */
            /* block end [id:"MusicDirector_block_6"] */
            /* block comment [block:"MusicDirector_block_26" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"MusicDirector_block_26" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            lastRetryMs = now;
            /* block end [id:"MusicDirector_block_26"] */
            /* block comment [block:"MusicDirector_block_27" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"MusicDirector_block_27" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            combatMode = enemiesNear;
            /* block end [id:"MusicDirector_block_27"] */
            /* block comment [block:"MusicDirector_block_28" text:"Calls the start method using its declared arguments."] */
            /* block start [id:"MusicDirector_block_28" category:"My Blocks" label:"call start" shape:"command" docs:"Calls the start method using its declared arguments."] */
            start(want);
            /* block end [id:"MusicDirector_block_28"] */
            /* block comment [block:"MusicDirector_block_29" text:"Returns a value or ends the current method."] */
            /* block start [id:"MusicDirector_block_29" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"MusicDirector_block_29"] */
        }
        /* block end [id:"MusicDirector_block_5"] */
        /* block comment [block:"MusicDirector_block_30" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"MusicDirector_block_30" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        combatMode = enemiesNear;
        /* block end [id:"MusicDirector_block_30"] */
        /* block comment [block:"MusicDirector_block_31" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MusicDirector_block_31" category:"Variables" label:"set updateCombat" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        MusicManager.updateCombat(enemiesNear, calmPath, combatPath);
        /* block end [id:"MusicDirector_block_31"] */
    }

    /** Final-floor clear: one-shot sting. */
    public static void victory() {
        /* block comment [block:"MusicDirector_block_32" text:"Calls the startOnce method using its declared arguments."] */
        /* block start [id:"MusicDirector_block_32" category:"My Blocks" label:"call start once" shape:"command" docs:"Calls the startOnce method using its declared arguments."] */
        startOnce(MusicManager.EXTRA_FINAL_NO_RETURN);
        /* block end [id:"MusicDirector_block_32"] */
    }

    /** Player death: silence. */
    public static void death() {
        /* block comment [block:"MusicDirector_block_33" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MusicDirector_block_33" category:"Variables" label:"set stopMusic" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        MusicManager.stopMusic(/* param start [id:"MusicDirector_value_48" type:"number"] */500/* param end [id:"MusicDirector_value_48"] */);
        /* block end [id:"MusicDirector_block_33"] */
    }

    /** Global mute toggle (M key). Returns the new muted state. */
    public static boolean toggleMute() {
        /* block comment [block:"MusicDirector_block_34" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"MusicDirector_block_34" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        muted = !muted;
        /* block end [id:"MusicDirector_block_34"] */
        /* block comment [block:"MusicDirector_block_35" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MusicDirector_block_35" category:"Variables" label:"set setEnabled" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        MusicManager.setEnabled(!muted);
        /* block end [id:"MusicDirector_block_35"] */
        /* block comment [block:"MusicDirector_block_7" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"MusicDirector_block_7" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!muted) {
            /* block comment [block:"MusicDirector_block_36" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"MusicDirector_block_36" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            lastRetryMs = /* param start [id:"MusicDirector_value_49" type:"number"] */0/* param end [id:"MusicDirector_value_49"] */;
            /* block end [id:"MusicDirector_block_36"] */ // unmute replays immediately on next update()
        }
        /* block end [id:"MusicDirector_block_7"] */
        /* block comment [block:"MusicDirector_block_37" text:"Returns a value or ends the current method."] */
        /* block start [id:"MusicDirector_block_37" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return muted;
        /* block end [id:"MusicDirector_block_37"] */
    }

    public static boolean isMuted() {
        /* block comment [block:"MusicDirector_block_38" text:"Returns a value or ends the current method."] */
        /* block start [id:"MusicDirector_block_38" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return muted;
        /* block end [id:"MusicDirector_block_38"] */
    }

    private static void start(String path) {
        /* block comment [block:"MusicDirector_block_39" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"MusicDirector_block_39" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"MusicDirector_block_8" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"MusicDirector_block_8" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (muted || path == /* param start [id:"MusicDirector_value_50" type:"value"] */null/* param end [id:"MusicDirector_value_50"] */) return;
        /* block end [id:"MusicDirector_block_39"] */
        /* block end [id:"MusicDirector_block_8"] */
        /* block comment [block:"MusicDirector_block_40" text:"Runs the current time millis action. Values in this block are editable inputs."] */
        /* block start [id:"MusicDirector_block_40" category:"My Blocks" label:"current time millis" shape:"command" docs:"Runs the current time millis action. Values in this block are editable inputs."] */
        lastRetryMs = System.currentTimeMillis();
        /* block end [id:"MusicDirector_block_40"] */
        /* block comment [block:"MusicDirector_block_41" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MusicDirector_block_41" category:"Variables" label:"set playMusic" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        MusicManager.playMusic(path, /* param start [id:"MusicDirector_value_51" type:"boolean"] */true/* param end [id:"MusicDirector_value_51"] */, /* param start [id:"MusicDirector_value_52" type:"number"] */800/* param end [id:"MusicDirector_value_52"] */);
        /* block end [id:"MusicDirector_block_41"] */
    }

    private static void startOnce(String path) {
        /* block comment [block:"MusicDirector_block_42" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"MusicDirector_block_42" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"MusicDirector_block_9" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"MusicDirector_block_9" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (muted || path == /* param start [id:"MusicDirector_value_53" type:"value"] */null/* param end [id:"MusicDirector_value_53"] */) return;
        /* block end [id:"MusicDirector_block_42"] */
        /* block end [id:"MusicDirector_block_9"] */
        /* block comment [block:"MusicDirector_block_43" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MusicDirector_block_43" category:"Variables" label:"set playMusic" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        MusicManager.playMusic(path, /* param start [id:"MusicDirector_value_54" type:"boolean"] */false/* param end [id:"MusicDirector_value_54"] */, /* param start [id:"MusicDirector_value_55" type:"number"] */300/* param end [id:"MusicDirector_value_55"] */);
        /* block end [id:"MusicDirector_block_43"] */
    }
}
