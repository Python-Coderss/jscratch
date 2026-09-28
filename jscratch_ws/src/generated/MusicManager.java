package generated;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.SourceDataLine;

/**
* Project-level music streamer. Lives in generated/ so the engine
* (com.jscratch.*) is untouched.
*
* <p>Call graph / interactions:
* <ul>
*   <li>Policy caller is {@code MusicDirector} (title/onFloor/update/
*       victory/death/mute); this class only streams one song at a time.</li>
*   <li>{@code playMusic} dedupes (same path + playing = no-op), stops the
*       old stream, and starts a <b>daemon</b> {@code "MusicManager"} thread
*       running {@code streamLoop → streamOnce} (32KB chunks, fade in/out
*       via MASTER_GAIN). {@code crossfadeTo} runs on its own daemon
*       thread; both daemon so music never blocks JVM exit.</li>
*   <li>Stopped by {@code ProjectRunner} (reflective {@code stopMusic})
*       across editor re-runs and by {@code MusicDirector.death/mute}.</li>
* </ul>
*
* Why this exists: engine playSound() loads the whole file into a Clip.
* Fine for short SFX, bad for multi-minute music (100+ MB per song in
* memory). This streams 32 KB at a time, so memory use is constant no
* matter the song length.
*
* Paths: use the same relative style as the rest of the project, e.g.
* "jscratch_ws/assets/music_game/boss_grimm_troupe.wav". Lookup is
* classpath-first (so bundled jars work), then filesystem: project root,
* then the CheerpJ /app/ prefix via resolve().
*
* Browser note: audio is blocked until the first user gesture. Calls made
* before that fail silently (stderr only) by design; later calls work.
* If audio is unavailable at all, everything is a silent no-op so the
* game never crashes because of music.
*
* Java 8 compatible. Single music channel (one song at a time).
*/
public final class MusicManager {

    // ---- Catalog: assets/music_game/ (22050 Hz mono 16-bit WAV) ----
    // Title theme: converted from assets/music_game/title.mp3.
    public static final String TITLE               = "jscratch_ws/assets/music_game/title.wav";
    public static final String BOSS_CHANT_TRIBAL   = "jscratch_ws/assets/music_game/boss_chant_tribal.wav";
    public static final String BOSS_DEATH_ODYSSEY  = "jscratch_ws/assets/music_game/boss_death_odyssey.wav";
    public static final String BOSS_FOURTH_CHORUS  = "jscratch_ws/assets/music_game/boss_fourth_chorus.wav";
    public static final String BOSS_GRIMM_TROUPE   = "jscratch_ws/assets/music_game/boss_grimm_troupe.wav";

    public static final String EXPLORE_PART_FALLING_COMBAT = "jscratch_ws/assets/music_game/explore_part_falling_combat.wav";
    public static final String EXPLORE_PART_FALLING_CALM   = "jscratch_ws/assets/music_game/explore_part_falling_calm.wav";
    public static final String EXPLORE_CASTLE_VEIN_COMBAT  = "jscratch_ws/assets/music_game/explore_castle_vein_combat.wav";
    public static final String EXPLORE_CASTLE_VEIN_CALM    = "jscratch_ws/assets/music_game/explore_castle_vein_calm.wav";
    public static final String EXPLORE_IN_ABSENTIA_COMBAT  = "jscratch_ws/assets/music_game/explore_in_absentia_combat.wav";
    public static final String EXPLORE_IN_ABSENTIA_CALM    = "jscratch_ws/assets/music_game/explore_in_absentia_calm.wav";

    public static final String CALM_THOUSAND_GREETINGS = "jscratch_ws/assets/music_game/calm_thousand_greetings.wav";
    public static final String CALM_DIRTMOUTH          = "jscratch_ws/assets/music_game/calm_dirtmouth.wav";
    public static final String CALM_MOSS_GROTTO        = "jscratch_ws/assets/music_game/calm_moss_grotto.wav";
    public static final String CALM_NEVER_ODD_OR_EVEN  = "jscratch_ws/assets/music_game/calm_never_odd_or_even.wav";

    public static final String EXTRA_GOOFY_TENTH_LEVEL = "jscratch_ws/assets/music_game/extra_goofy_tenth_level_lockdown.wav";
    public static final String EXTRA_FINAL_NO_RETURN   = "jscratch_ws/assets/music_game/extra_final_no_return.wav";
    public static final String EXTRA_TITLE_MEDIEVAL    = "jscratch_ws/assets/music_game/extra_title_medieval.wav";
    public static final String EXTRA_RAIN_FALLS_CALM   = "jscratch_ws/assets/music_game/extra_rain_falls_calm.wav";
    public static final String EXTRA_RAIN_FALLS_COMBAT = "jscratch_ws/assets/music_game/extra_rain_falls_combat.wav";
    public static final String EXTRA_PEST_OF_COSMOS    = "jscratch_ws/assets/music_game/extra_pest_of_cosmos.wav";
    public static final String EXTRA_UNIVERSAL_COLLAPSE = "jscratch_ws/assets/music_game/extra_universal_collapse.wav";
    public static final String EXTRA_CRYSTAL_PEAK      = "jscratch_ws/assets/music_game/extra_crystal_peak.wav";
    public static final String EXTRA_WEREPIG           = "jscratch_ws/assets/music_game/extra_werepig.wav";
    public static final String EXTRA_EFS               = "jscratch_ws/assets/music_game/extra_efs.wav";

    private static final int BUFFER_BYTES = 32 * 1024;
    private static final float FADE_FLOOR_DB = -40.0f;

    private static final Object LOCK = new Object();
    private static volatile Thread playerThread;
    private static volatile String currentPath;
    private static volatile boolean stopRequested;
    private static volatile double volume = 100.0;
    private static volatile boolean enabled = true;

    private MusicManager() {}

    /** Master volume 0-100. Applies to the live song immediately if possible. */
    public static void setVolume(double percent) {
        /* block comment [block:"MusicManager_block_51" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"MusicManager_block_51" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"MusicManager_block_1" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"MusicManager_block_1" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (percent < /* param start [id:"MusicManager_value_157" type:"number"] */0/* param end [id:"MusicManager_value_157"] */) percent = /* param start [id:"MusicManager_value_158" type:"number"] */0/* param end [id:"MusicManager_value_158"] */;
        /* block end [id:"MusicManager_block_51"] */
        /* block end [id:"MusicManager_block_1"] */
        /* block comment [block:"MusicManager_block_52" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"MusicManager_block_52" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"MusicManager_block_2" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"MusicManager_block_2" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (percent > /* param start [id:"MusicManager_value_159" type:"number"] */100/* param end [id:"MusicManager_value_159"] */) percent = /* param start [id:"MusicManager_value_160" type:"number"] */100/* param end [id:"MusicManager_value_160"] */;
        /* block end [id:"MusicManager_block_52"] */
        /* block end [id:"MusicManager_block_2"] */
        /* block comment [block:"MusicManager_block_53" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"MusicManager_block_53" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        volume = percent;
        /* block end [id:"MusicManager_block_53"] */
    }

    /** Mute/unmute everything without losing the current song state. */
    public static void setEnabled(boolean on) {
        /* block comment [block:"MusicManager_block_54" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"MusicManager_block_54" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        enabled = on;
        /* block end [id:"MusicManager_block_54"] */
        /* block comment [block:"MusicManager_block_55" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"MusicManager_block_55" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"MusicManager_block_3" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"MusicManager_block_3" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!on) stopMusic(/* param start [id:"MusicManager_value_161" type:"number"] */0/* param end [id:"MusicManager_value_161"] */);
        /* block end [id:"MusicManager_block_55"] */
        /* block end [id:"MusicManager_block_3"] */
    }

    public static boolean isPlaying() {
        /* block comment [block:"MusicManager_block_56" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MusicManager_block_56" category:"Variables" label:"set t" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Thread t = playerThread;
        /* block end [id:"MusicManager_block_56"] */
        /* block comment [block:"MusicManager_block_57" text:"Returns a value or ends the current method."] */
        /* block start [id:"MusicManager_block_57" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return t != /* param start [id:"MusicManager_value_162" type:"value"] */null/* param end [id:"MusicManager_value_162"] */ && t.isAlive();
        /* block end [id:"MusicManager_block_57"] */
    }

    /** Path of the currently streaming song, or null. */
    public static String current() {
        /* block comment [block:"MusicManager_block_58" text:"Returns a value or ends the current method."] */
        /* block start [id:"MusicManager_block_58" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return currentPath;
        /* block end [id:"MusicManager_block_58"] */
    }

    /** Loop the song indefinitely (default music behavior). */
    public static void playMusic(String path) {
        /* block comment [block:"MusicManager_block_59" text:"Runs the play music action. Values in this block are editable inputs."] */
        /* block start [id:"MusicManager_block_59" category:"Sound" label:"play music" shape:"command" docs:"Runs the play music action. Values in this block are editable inputs."] */
        playMusic(path, /* param start [id:"MusicManager_value_163" type:"boolean"] */true/* param end [id:"MusicManager_value_163"] */, /* param start [id:"MusicManager_value_164" type:"number"] */0/* param end [id:"MusicManager_value_164"] */);
        /* block end [id:"MusicManager_block_59"] */
    }

    public static void playMusic(String path, boolean loop) {
        /* block comment [block:"MusicManager_block_60" text:"Runs the play music action. Values in this block are editable inputs."] */
        /* block start [id:"MusicManager_block_60" category:"Sound" label:"play music" shape:"command" docs:"Runs the play music action. Values in this block are editable inputs."] */
        playMusic(path, loop, /* param start [id:"MusicManager_value_165" type:"number"] */0/* param end [id:"MusicManager_value_165"] */);
        /* block end [id:"MusicManager_block_60"] */
    }

    public static void playMusic(final String path, final boolean loop, final int fadeInMs) {
        synchronized (LOCK) {
            /* block comment [block:"MusicManager_block_61" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"MusicManager_block_61" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"MusicManager_block_4" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"MusicManager_block_4" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (path == /* param start [id:"MusicManager_value_166" type:"value"] */null/* param end [id:"MusicManager_value_166"] */) return;
            /* block end [id:"MusicManager_block_61"] */
            /* block end [id:"MusicManager_block_4"] */
            /* block comment [block:"MusicManager_block_62" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"MusicManager_block_62" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"MusicManager_block_5" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"MusicManager_block_5" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (path.equals(currentPath) && isPlaying()) return;
            /* block end [id:"MusicManager_block_62"] */
            /* block end [id:"MusicManager_block_5"] */
            /* block comment [block:"MusicManager_block_63" text:"Calls the stopLocked method using its declared arguments."] */
            /* block start [id:"MusicManager_block_63" category:"My Blocks" label:"call stop locked" shape:"command" docs:"Calls the stopLocked method using its declared arguments."] */
            stopLocked(/* param start [id:"MusicManager_value_167" type:"number"] */0/* param end [id:"MusicManager_value_167"] */);
            /* block end [id:"MusicManager_block_63"] */
            /* block comment [block:"MusicManager_block_6" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"MusicManager_block_6" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (!enabled) { /* block comment [block:"MusicManager_block_64" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"MusicManager_block_64" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                currentPath = path;
                /* block end [id:"MusicManager_block_64"] */ /* block comment [block:"MusicManager_block_65" text:"Returns a value or ends the current method."] */
                /* block start [id:"MusicManager_block_65" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
                return;
                /* block end [id:"MusicManager_block_65"] */ }
            /* block end [id:"MusicManager_block_6"] */
            /* block comment [block:"MusicManager_block_66" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"MusicManager_block_66" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            stopRequested = /* param start [id:"MusicManager_value_168" type:"boolean"] */false/* param end [id:"MusicManager_value_168"] */;
            /* block end [id:"MusicManager_block_66"] */
            /* block comment [block:"MusicManager_block_67" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"MusicManager_block_67" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            currentPath = path;
            /* block end [id:"MusicManager_block_67"] */
            playerThread = new Thread(new Runnable() {
                public void run() { streamLoop(path, loop, fadeInMs); }
            }/* block comment [block:"MusicManager_block_68" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"MusicManager_block_68" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            , /* param start [id:"MusicManager_value_169" type:"text"] */"MusicManager"/* param end [id:"MusicManager_value_169"] */);
            /* block end [id:"MusicManager_block_68"] */
            /* block comment [block:"MusicManager_block_69" text:"Runs the set daemon action. Values in this block are editable inputs."] */
            /* block start [id:"MusicManager_block_69" category:"Variables" label:"set daemon" shape:"command" docs:"Runs the set daemon action. Values in this block are editable inputs."] */
            playerThread.setDaemon(/* param start [id:"MusicManager_value_170" type:"boolean"] */true/* param end [id:"MusicManager_value_170"] */);
            /* block end [id:"MusicManager_block_69"] */
            /* block comment [block:"MusicManager_block_70" text:"Calls the start method using its declared arguments."] */
            /* block start [id:"MusicManager_block_70" category:"My Blocks" label:"call start" shape:"command" docs:"Calls the start method using its declared arguments."] */
            playerThread.start();
            /* block end [id:"MusicManager_block_70"] */
        }
    }

    /**
    * Calm/combat switch from stuff-to-download.txt: call every frame (or on
    * aggro change) with enemiesNear. Only restarts the stream when the
    * target actually differs, so per-frame calls are cheap.
    */
    public static void updateCombat(boolean enemiesNear, String calmPath, String combatPath) {
        /* block comment [block:"MusicManager_block_71" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MusicManager_block_71" category:"Variables" label:"set want" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        String want = enemiesNear ? combatPath : calmPath;
        /* block end [id:"MusicManager_block_71"] */
        /* block comment [block:"MusicManager_block_72" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"MusicManager_block_72" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"MusicManager_block_7" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"MusicManager_block_7" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (want == /* param start [id:"MusicManager_value_171" type:"value"] */null/* param end [id:"MusicManager_value_171"] */) return;
        /* block end [id:"MusicManager_block_72"] */
        /* block end [id:"MusicManager_block_7"] */
        /* block comment [block:"MusicManager_block_73" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"MusicManager_block_73" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"MusicManager_block_8" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"MusicManager_block_8" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (want.equals(currentPath) && isPlaying()) return;
        /* block end [id:"MusicManager_block_73"] */
        /* block end [id:"MusicManager_block_8"] */
        /* block comment [block:"MusicManager_block_74" text:"Calls the crossfadeTo method using its declared arguments."] */
        /* block start [id:"MusicManager_block_74" category:"My Blocks" label:"call crossfade to" shape:"command" docs:"Calls the crossfadeTo method using its declared arguments."] */
        crossfadeTo(want, /* param start [id:"MusicManager_value_172" type:"number"] */800/* param end [id:"MusicManager_value_172"] */, /* param start [id:"MusicManager_value_173" type:"boolean"] */true/* param end [id:"MusicManager_value_173"] */);
        /* block end [id:"MusicManager_block_74"] */
    }

    /** Fade the current song out and start another. Non-blocking. */
    public static void crossfadeTo(final String path, final int fadeMs, final boolean loop) {
        Thread t = new Thread(new Runnable() {
            public void run() {
                stopMusic(fadeMs);
                long deadline = System.currentTimeMillis() + fadeMs + 500;
                /* block comment [block:"MusicManager_block_9" text:"Repeats the body while its Boolean condition is true."] */
                /* block start [id:"MusicManager_block_9" category:"Control" label:"repeat while" shape:"control" docs:"Repeats the body while its Boolean condition is true."] */
                while (isPlaying() && System.currentTimeMillis() < deadline) {
                    /* block comment [block:"MusicManager_block_10" text:"Runs work safely and handles matching errors in a catch branch."] */
                    /* block start [id:"MusicManager_block_10" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
                    try { Thread.sleep(/* param start [id:"MusicManager_value_174" type:"number"] */50/* param end [id:"MusicManager_value_174"] */); }
                    /* block end [id:"MusicManager_block_10"] */ catch (InterruptedException e) { break; }
                }
                /* block end [id:"MusicManager_block_9"] */
                playMusic(path, loop, fadeMs);
            }
        }/* block comment [block:"MusicManager_block_75" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"MusicManager_block_75" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        , /* param start [id:"MusicManager_value_175" type:"text"] */"MusicManager-xfade"/* param end [id:"MusicManager_value_175"] */);
        /* block end [id:"MusicManager_block_75"] */
        /* block comment [block:"MusicManager_block_76" text:"Runs the set daemon action. Values in this block are editable inputs."] */
        /* block start [id:"MusicManager_block_76" category:"Variables" label:"set daemon" shape:"command" docs:"Runs the set daemon action. Values in this block are editable inputs."] */
        t.setDaemon(/* param start [id:"MusicManager_value_176" type:"boolean"] */true/* param end [id:"MusicManager_value_176"] */);
        /* block end [id:"MusicManager_block_76"] */
        /* block comment [block:"MusicManager_block_77" text:"Calls the start method using its declared arguments."] */
        /* block start [id:"MusicManager_block_77" category:"My Blocks" label:"call start" shape:"command" docs:"Calls the start method using its declared arguments."] */
        t.start();
        /* block end [id:"MusicManager_block_77"] */
    }

    public static void stopMusic() {
        /* block comment [block:"MusicManager_block_78" text:"Runs the stop music action. Values in this block are editable inputs."] */
        /* block start [id:"MusicManager_block_78" category:"Sound" label:"stop music" shape:"command" docs:"Runs the stop music action. Values in this block are editable inputs."] */
        stopMusic(/* param start [id:"MusicManager_value_177" type:"number"] */0/* param end [id:"MusicManager_value_177"] */);
        /* block end [id:"MusicManager_block_78"] */
    }

    public static void stopMusic(int fadeOutMs) {
        synchronized (LOCK) {
            /* block comment [block:"MusicManager_block_79" text:"Calls the stopLocked method using its declared arguments."] */
            /* block start [id:"MusicManager_block_79" category:"My Blocks" label:"call stop locked" shape:"command" docs:"Calls the stopLocked method using its declared arguments."] */
            stopLocked(fadeOutMs);
            /* block end [id:"MusicManager_block_79"] */
        }
    }

    // Must hold LOCK.
    private static void stopLocked(int fadeOutMs) {
        /* block comment [block:"MusicManager_block_80" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MusicManager_block_80" category:"Variables" label:"set t" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Thread t = playerThread;
        /* block end [id:"MusicManager_block_80"] */
        /* block comment [block:"MusicManager_block_81" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"MusicManager_block_81" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        playerThread = /* param start [id:"MusicManager_value_178" type:"value"] */null/* param end [id:"MusicManager_value_178"] */;
        /* block end [id:"MusicManager_block_81"] */
        /* block comment [block:"MusicManager_block_11" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"MusicManager_block_11" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (t != /* param start [id:"MusicManager_value_179" type:"value"] */null/* param end [id:"MusicManager_value_179"] */) {
            /* block comment [block:"MusicManager_block_12" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"MusicManager_block_12" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (fadeOutMs > /* param start [id:"MusicManager_value_180" type:"number"] */0/* param end [id:"MusicManager_value_180"] */) {
                /* block comment [block:"MusicManager_block_82" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"MusicManager_block_82" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                stopRequested = /* param start [id:"MusicManager_value_181" type:"boolean"] */true/* param end [id:"MusicManager_value_181"] */;
                /* block end [id:"MusicManager_block_82"] */
                // Give the player thread a chance to fade; it clears currentPath.
                /* block comment [block:"MusicManager_block_13" text:"Runs work safely and handles matching errors in a catch branch."] */
                /* block start [id:"MusicManager_block_13" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
                try { /* block comment [block:"MusicManager_block_83" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    /* block start [id:"MusicManager_block_83" category:"Variables" label:"set wait" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    LOCK.wait(Math.min(fadeOutMs + /* param start [id:"MusicManager_value_182" type:"number"] */500/* param end [id:"MusicManager_value_182"] */, /* param start [id:"MusicManager_value_183" type:"number"] */3000/* param end [id:"MusicManager_value_183"] */));
                    /* block end [id:"MusicManager_block_83"] */ }
                /* block end [id:"MusicManager_block_13"] */ catch (InterruptedException e) { /* fall through */ }
                /* block comment [block:"MusicManager_block_84" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"MusicManager_block_84" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"MusicManager_block_14" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"MusicManager_block_14" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (t.isAlive()) t.interrupt();
                /* block end [id:"MusicManager_block_84"] */
                /* block end [id:"MusicManager_block_14"] */
            } else {
                /* block comment [block:"MusicManager_block_85" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"MusicManager_block_85" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                stopRequested = /* param start [id:"MusicManager_value_184" type:"boolean"] */true/* param end [id:"MusicManager_value_184"] */;
                /* block end [id:"MusicManager_block_85"] */
                /* block comment [block:"MusicManager_block_86" text:"Calls the interrupt method using its declared arguments."] */
                /* block start [id:"MusicManager_block_86" category:"My Blocks" label:"call interrupt" shape:"command" docs:"Calls the interrupt method using its declared arguments."] */
                t.interrupt();
                /* block end [id:"MusicManager_block_86"] */
            }
            /* block end [id:"MusicManager_block_12"] */
        } else {
            /* block comment [block:"MusicManager_block_87" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"MusicManager_block_87" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            stopRequested = /* param start [id:"MusicManager_value_185" type:"boolean"] */true/* param end [id:"MusicManager_value_185"] */;
            /* block end [id:"MusicManager_block_87"] */
        }
        /* block end [id:"MusicManager_block_11"] */
        /* block comment [block:"MusicManager_block_88" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"MusicManager_block_88" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"MusicManager_block_15" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"MusicManager_block_15" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (playerThread == /* param start [id:"MusicManager_value_186" type:"value"] */null/* param end [id:"MusicManager_value_186"] */) currentPath = /* param start [id:"MusicManager_value_187" type:"value"] */null/* param end [id:"MusicManager_value_187"] */;
        /* block end [id:"MusicManager_block_88"] */
        /* block end [id:"MusicManager_block_15"] */
    }

    /** Classpath first (works inside a jar), then the filesystem. */
    static InputStream openStream(String path) {
        /* block comment [block:"MusicManager_block_89" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MusicManager_block_89" category:"Variables" label:"set cl" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        /* block end [id:"MusicManager_block_89"] */
        /* block comment [block:"MusicManager_block_90" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"MusicManager_block_90" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"MusicManager_block_16" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"MusicManager_block_16" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (cl == /* param start [id:"MusicManager_value_188" type:"value"] */null/* param end [id:"MusicManager_value_188"] */) cl = MusicManager.class.getClassLoader();
        /* block end [id:"MusicManager_block_90"] */
        /* block end [id:"MusicManager_block_16"] */
        /* block comment [block:"MusicManager_block_17" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"MusicManager_block_17" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (cl != /* param start [id:"MusicManager_value_189" type:"value"] */null/* param end [id:"MusicManager_value_189"] */) {
            /* block comment [block:"MusicManager_block_91" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"MusicManager_block_91" category:"Variables" label:"set s" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            InputStream s = cl.getResourceAsStream(path);
            /* block end [id:"MusicManager_block_91"] */
            /* block comment [block:"MusicManager_block_92" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"MusicManager_block_92" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"MusicManager_block_18" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"MusicManager_block_18" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (s == /* param start [id:"MusicManager_value_190" type:"value"] */null/* param end [id:"MusicManager_value_190"] */) s = cl.getResourceAsStream(/* param start [id:"MusicManager_value_191" type:"text"] */"/"/* param end [id:"MusicManager_value_191"] */ + path);
            /* block end [id:"MusicManager_block_92"] */
            /* block end [id:"MusicManager_block_18"] */
            /* block comment [block:"MusicManager_block_93" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"MusicManager_block_93" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"MusicManager_block_19" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"MusicManager_block_19" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (s != /* param start [id:"MusicManager_value_192" type:"value"] */null/* param end [id:"MusicManager_value_192"] */) return s;
            /* block end [id:"MusicManager_block_93"] */
            /* block end [id:"MusicManager_block_19"] */
        }
        /* block end [id:"MusicManager_block_17"] */
        /* block comment [block:"MusicManager_block_20" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"MusicManager_block_20" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try {
            /* block comment [block:"MusicManager_block_94" text:"Returns a value or ends the current method."] */
            /* block start [id:"MusicManager_block_94" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return new FileInputStream(resolve(path));
            /* block end [id:"MusicManager_block_94"] */
        }
        /* block end [id:"MusicManager_block_20"] */ catch (Exception e) {
            /* block comment [block:"MusicManager_block_95" text:"Returns a value or ends the current method."] */
            /* block start [id:"MusicManager_block_95" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return /* param start [id:"MusicManager_value_193" type:"value"] */null/* param end [id:"MusicManager_value_193"] */;
            /* block end [id:"MusicManager_block_95"] */
        }
    }

    /** Resolve against cwd, then the CheerpJ /app/ prefix. */
    static File resolve(String path) {
        /* block comment [block:"MusicManager_block_96" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MusicManager_block_96" category:"Variables" label:"set f" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        File f = new File(path);
        /* block end [id:"MusicManager_block_96"] */
        /* block comment [block:"MusicManager_block_97" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"MusicManager_block_97" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"MusicManager_block_21" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"MusicManager_block_21" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (f.isFile()) return f;
        /* block end [id:"MusicManager_block_97"] */
        /* block end [id:"MusicManager_block_21"] */
        /* block comment [block:"MusicManager_block_98" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MusicManager_block_98" category:"Variables" label:"set app" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        File app = new File(/* param start [id:"MusicManager_value_194" type:"text"] */"/app/"/* param end [id:"MusicManager_value_194"] */ + path);
        /* block end [id:"MusicManager_block_98"] */
        /* block comment [block:"MusicManager_block_99" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"MusicManager_block_99" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"MusicManager_block_22" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"MusicManager_block_22" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (app.isFile()) return app;
        /* block end [id:"MusicManager_block_99"] */
        /* block end [id:"MusicManager_block_22"] */
        // Absolute CheerpJ form of an already-absolute local path is meaningless; just return original.
        /* block comment [block:"MusicManager_block_100" text:"Returns a value or ends the current method."] */
        /* block start [id:"MusicManager_block_100" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return f;
        /* block end [id:"MusicManager_block_100"] */
    }

    private static void streamLoop(String path, boolean loop, int fadeInMs) {
        /* block comment [block:"MusicManager_block_23" text:"Runs the body once, then repeats while the condition is false."] */
        /* block start [id:"MusicManager_block_23" category:"Control" label:"repeat until" shape:"control" docs:"Runs the body once, then repeats while the condition is false."] */
        do {
            /* block comment [block:"MusicManager_block_24" text:"Runs work safely and handles matching errors in a catch branch."] */
            /* block start [id:"MusicManager_block_24" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
            try {
                /* block comment [block:"MusicManager_block_101" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"MusicManager_block_101" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"MusicManager_block_25" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"MusicManager_block_25" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (!streamOnce(path, fadeInMs)) break;
                /* block end [id:"MusicManager_block_101"] */
                /* block end [id:"MusicManager_block_25"] */
            }
            /* block end [id:"MusicManager_block_24"] */ catch (InterruptedException e) {
                /* block comment [block:"MusicManager_block_102" text:"Stops the nearest loop or switch block."] */
                /* block start [id:"MusicManager_block_102" category:"Control" label:"break loop" shape:"command" docs:"Stops the nearest loop or switch block."] */
                break;
                /* block end [id:"MusicManager_block_102"] */
            } catch (Exception e) {
                /* block comment [block:"MusicManager_block_103" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"MusicManager_block_103" category:"Variables" label:"set err" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                System.err.println(/* param start [id:"MusicManager_value_195" type:"text"] */"[MusicManager] cannot play "/* param end [id:"MusicManager_value_195"] */ + path + /* param start [id:"MusicManager_value_196" type:"text"] */": "/* param end [id:"MusicManager_value_196"] */ + e);
                /* block end [id:"MusicManager_block_103"] */
                /* block comment [block:"MusicManager_block_104" text:"Stops the nearest loop or switch block."] */
                /* block start [id:"MusicManager_block_104" category:"Control" label:"break loop" shape:"command" docs:"Stops the nearest loop or switch block."] */
                break;
                /* block end [id:"MusicManager_block_104"] */
            }
            /* block comment [block:"MusicManager_block_105" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"MusicManager_block_105" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            fadeInMs = /* param start [id:"MusicManager_value_197" type:"number"] */0/* param end [id:"MusicManager_value_197"] */;
            /* block end [id:"MusicManager_block_105"] */ // only fade the first pass of a loop
            /* block comment [block:"MusicManager_block_106" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"MusicManager_block_106" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"MusicManager_block_26" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"MusicManager_block_26" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (stopRequested) break;
            /* block end [id:"MusicManager_block_106"] */
            /* block end [id:"MusicManager_block_26"] */
        }
        /* block end [id:"MusicManager_block_23"] */ /* block comment [block:"MusicManager_block_107" text:"Runs the while action. Values in this block are editable inputs."] */
        /* block start [id:"MusicManager_block_107" category:"My Blocks" label:"while" shape:"command" docs:"Runs the while action. Values in this block are editable inputs."] */
        /* block comment [block:"MusicManager_block_27" text:"Repeats the body while its Boolean condition is true."] */
        /* block start [id:"MusicManager_block_27" category:"Control" label:"repeat while" shape:"control" docs:"Repeats the body while its Boolean condition is true."] */
        while (loop && !stopRequested && enabled);
        /* block end [id:"MusicManager_block_107"] */
        /* block end [id:"MusicManager_block_27"] */
        synchronized (LOCK) {
            /* block comment [block:"MusicManager_block_28" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"MusicManager_block_28" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (Thread.currentThread() == playerThread) {
                /* block comment [block:"MusicManager_block_108" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"MusicManager_block_108" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                playerThread = /* param start [id:"MusicManager_value_198" type:"value"] */null/* param end [id:"MusicManager_value_198"] */;
                /* block end [id:"MusicManager_block_108"] */
                /* block comment [block:"MusicManager_block_109" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"MusicManager_block_109" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                currentPath = /* param start [id:"MusicManager_value_199" type:"value"] */null/* param end [id:"MusicManager_value_199"] */;
                /* block end [id:"MusicManager_block_109"] */
            }
            /* block end [id:"MusicManager_block_28"] */
            /* block comment [block:"MusicManager_block_110" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"MusicManager_block_110" category:"Variables" label:"set notifyAll" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            LOCK.notifyAll();
            /* block end [id:"MusicManager_block_110"] */
        }
    }

    /** @return true if the file played to the end (loop may continue). */
    private static boolean streamOnce(String path, int fadeInMs) throws Exception {
        /* block comment [block:"MusicManager_block_111" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MusicManager_block_111" category:"Variables" label:"set src" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        InputStream src = openStream(path);
        /* block end [id:"MusicManager_block_111"] */
        /* block comment [block:"MusicManager_block_29" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"MusicManager_block_29" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (src == /* param start [id:"MusicManager_value_200" type:"value"] */null/* param end [id:"MusicManager_value_200"] */) {
            /* block comment [block:"MusicManager_block_112" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"MusicManager_block_112" category:"Variables" label:"set err" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            System.err.println(/* param start [id:"MusicManager_value_201" type:"text"] */"[MusicManager] file not found: "/* param end [id:"MusicManager_value_201"] */ + path);
            /* block end [id:"MusicManager_block_112"] */
            /* block comment [block:"MusicManager_block_113" text:"Returns a value or ends the current method."] */
            /* block start [id:"MusicManager_block_113" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return /* param start [id:"MusicManager_value_202" type:"boolean"] */false/* param end [id:"MusicManager_value_202"] */;
            /* block end [id:"MusicManager_block_113"] */
        }
        /* block end [id:"MusicManager_block_29"] */
        /* block comment [block:"MusicManager_block_114" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MusicManager_block_114" category:"Variables" label:"set raw" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        AudioInputStream raw;
        /* block end [id:"MusicManager_block_114"] */
        /* block comment [block:"MusicManager_block_30" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"MusicManager_block_30" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try {
            /* block comment [block:"MusicManager_block_115" text:"Runs the get audio input stream action. Values in this block are editable inputs."] */
            /* block start [id:"MusicManager_block_115" category:"My Blocks" label:"get audio input stream" shape:"command" docs:"Runs the get audio input stream action. Values in this block are editable inputs."] */
            raw = AudioSystem.getAudioInputStream(new BufferedInputStream(src, /* param start [id:"MusicManager_value_203" type:"number"] */64/* param end [id:"MusicManager_value_203"] */ * /* param start [id:"MusicManager_value_204" type:"number"] */1024/* param end [id:"MusicManager_value_204"] */));
            /* block end [id:"MusicManager_block_115"] */
        }
        /* block end [id:"MusicManager_block_30"] */ catch (Exception e) {
            /* block comment [block:"MusicManager_block_116" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"MusicManager_block_116" category:"Variables" label:"set err" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            System.err.println(/* param start [id:"MusicManager_value_205" type:"text"] */"[MusicManager] cannot decode "/* param end [id:"MusicManager_value_205"] */ + path + /* param start [id:"MusicManager_value_206" type:"text"] */": "/* param end [id:"MusicManager_value_206"] */ + e);
            /* block end [id:"MusicManager_block_116"] */
            /* block comment [block:"MusicManager_block_31" text:"Runs work safely and handles matching errors in a catch branch."] */
            /* block start [id:"MusicManager_block_31" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
            try { /* block comment [block:"MusicManager_block_117" text:"Calls the close method using its declared arguments."] */
                /* block start [id:"MusicManager_block_117" category:"My Blocks" label:"call close" shape:"command" docs:"Calls the close method using its declared arguments."] */
                src.close();
                /* block end [id:"MusicManager_block_117"] */ }
            /* block end [id:"MusicManager_block_31"] */ catch (Exception ignored) {}
            /* block comment [block:"MusicManager_block_118" text:"Returns a value or ends the current method."] */
            /* block start [id:"MusicManager_block_118" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return /* param start [id:"MusicManager_value_207" type:"boolean"] */false/* param end [id:"MusicManager_value_207"] */;
            /* block end [id:"MusicManager_block_118"] */
        }
        /* block comment [block:"MusicManager_block_119" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MusicManager_block_119" category:"Variables" label:"set base" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        AudioFormat base = raw.getFormat();
        /* block end [id:"MusicManager_block_119"] */
        /* block comment [block:"MusicManager_block_120" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MusicManager_block_120" category:"Variables" label:"set decoded" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        AudioFormat decoded = new AudioFormat(
        AudioFormat.Encoding.PCM_SIGNED,
        base.getSampleRate(), /* param start [id:"MusicManager_value_208" type:"number"] */16/* param end [id:"MusicManager_value_208"] */, base.getChannels(),
        base.getChannels() * /* param start [id:"MusicManager_value_209" type:"number"] */2/* param end [id:"MusicManager_value_209"] */, base.getSampleRate(), /* param start [id:"MusicManager_value_210" type:"boolean"] */false/* param end [id:"MusicManager_value_210"] */);
        /* block end [id:"MusicManager_block_120"] */
        /* block comment [block:"MusicManager_block_121" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MusicManager_block_121" category:"Variables" label:"set in" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        AudioInputStream in;
        /* block end [id:"MusicManager_block_121"] */
        /* block comment [block:"MusicManager_block_32" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"MusicManager_block_32" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try {
            /* block comment [block:"MusicManager_block_122" text:"Runs the is conversion supported action. Values in this block are editable inputs."] */
            /* block start [id:"MusicManager_block_122" category:"My Blocks" label:"is conversion supported" shape:"command" docs:"Runs the is conversion supported action. Values in this block are editable inputs."] */
            in = AudioSystem.isConversionSupported(decoded, base)
            ? AudioSystem.getAudioInputStream(decoded, raw) : raw;
            /* block end [id:"MusicManager_block_122"] */
        }
        /* block end [id:"MusicManager_block_32"] */ catch (Exception e) {
            /* block comment [block:"MusicManager_block_123" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"MusicManager_block_123" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            in = raw;
            /* block end [id:"MusicManager_block_123"] */
        }
        /* block comment [block:"MusicManager_block_124" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MusicManager_block_124" category:"Variables" label:"set Info" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, in.getFormat());
        /* block end [id:"MusicManager_block_124"] */
        /* block comment [block:"MusicManager_block_125" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MusicManager_block_125" category:"Variables" label:"set line" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        SourceDataLine line;
        /* block end [id:"MusicManager_block_125"] */
        /* block comment [block:"MusicManager_block_33" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"MusicManager_block_33" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try {
            /* block comment [block:"MusicManager_block_126" text:"Runs the get line action. Values in this block are editable inputs."] */
            /* block start [id:"MusicManager_block_126" category:"My Blocks" label:"get line" shape:"command" docs:"Runs the get line action. Values in this block are editable inputs."] */
            line = (SourceDataLine) AudioSystem.getLine(info);
            /* block end [id:"MusicManager_block_126"] */
            /* block comment [block:"MusicManager_block_127" text:"Calls the open method using its declared arguments."] */
            /* block start [id:"MusicManager_block_127" category:"My Blocks" label:"call open" shape:"command" docs:"Calls the open method using its declared arguments."] */
            line.open(in.getFormat());
            /* block end [id:"MusicManager_block_127"] */
        }
        /* block end [id:"MusicManager_block_33"] */ catch (Exception e) {
            /* block comment [block:"MusicManager_block_128" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"MusicManager_block_128" category:"Variables" label:"set err" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            System.err.println(/* param start [id:"MusicManager_value_211" type:"text"] */"[MusicManager] no audio line: "/* param end [id:"MusicManager_value_211"] */ + e);
            /* block end [id:"MusicManager_block_128"] */
            /* block comment [block:"MusicManager_block_34" text:"Runs work safely and handles matching errors in a catch branch."] */
            /* block start [id:"MusicManager_block_34" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
            try { /* block comment [block:"MusicManager_block_129" text:"Calls the close method using its declared arguments."] */
                /* block start [id:"MusicManager_block_129" category:"My Blocks" label:"call close" shape:"command" docs:"Calls the close method using its declared arguments."] */
                in.close();
                /* block end [id:"MusicManager_block_129"] */ }
            /* block end [id:"MusicManager_block_34"] */ catch (Exception ignored) {}
            /* block comment [block:"MusicManager_block_130" text:"Returns a value or ends the current method."] */
            /* block start [id:"MusicManager_block_130" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return /* param start [id:"MusicManager_value_212" type:"boolean"] */false/* param end [id:"MusicManager_value_212"] */;
            /* block end [id:"MusicManager_block_130"] */
        }
        /* block comment [block:"MusicManager_block_131" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MusicManager_block_131" category:"Variables" label:"set buf" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        byte[] buf = new byte[BUFFER_BYTES];
        /* block end [id:"MusicManager_block_131"] */
        /* block comment [block:"MusicManager_block_132" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MusicManager_block_132" category:"Variables" label:"set gain" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        FloatControl gain = /* param start [id:"MusicManager_value_213" type:"value"] */null/* param end [id:"MusicManager_value_213"] */;
        /* block end [id:"MusicManager_block_132"] */
        /* block comment [block:"MusicManager_block_35" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"MusicManager_block_35" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try {
            /* block comment [block:"MusicManager_block_36" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"MusicManager_block_36" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                /* block comment [block:"MusicManager_block_133" text:"Runs the get control action. Values in this block are editable inputs."] */
                /* block start [id:"MusicManager_block_133" category:"My Blocks" label:"get control" shape:"command" docs:"Runs the get control action. Values in this block are editable inputs."] */
                gain = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                /* block end [id:"MusicManager_block_133"] */
            }
            /* block end [id:"MusicManager_block_36"] */
        }
        /* block end [id:"MusicManager_block_35"] */ catch (Exception ignored) {}
        /* block comment [block:"MusicManager_block_134" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MusicManager_block_134" category:"Variables" label:"set targetDb" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        float targetDb = toDb(volume);
        /* block end [id:"MusicManager_block_134"] */
        /* block comment [block:"MusicManager_block_135" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MusicManager_block_135" category:"Variables" label:"set fadeStart" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        long fadeStart = System.currentTimeMillis();
        /* block end [id:"MusicManager_block_135"] */
        /* block comment [block:"MusicManager_block_136" text:"Calls the start method using its declared arguments."] */
        /* block start [id:"MusicManager_block_136" category:"My Blocks" label:"call start" shape:"command" docs:"Calls the start method using its declared arguments."] */
        line.start();
        /* block end [id:"MusicManager_block_136"] */
        /* block comment [block:"MusicManager_block_137" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"MusicManager_block_137" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"MusicManager_block_37" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"MusicManager_block_37" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (gain != /* param start [id:"MusicManager_value_214" type:"value"] */null/* param end [id:"MusicManager_value_214"] */) gain.setValue(fadeInMs > /* param start [id:"MusicManager_value_215" type:"number"] */0/* param end [id:"MusicManager_value_215"] */ ? FADE_FLOOR_DB : targetDb);
        /* block end [id:"MusicManager_block_137"] */
        /* block end [id:"MusicManager_block_37"] */
        /* block comment [block:"MusicManager_block_38" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"MusicManager_block_38" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try {
            /* block comment [block:"MusicManager_block_138" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"MusicManager_block_138" category:"Variables" label:"set n" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int n;
            /* block end [id:"MusicManager_block_138"] */
            /* block comment [block:"MusicManager_block_39" text:"Repeats the body while its Boolean condition is true."] */
            /* block start [id:"MusicManager_block_39" category:"Control" label:"repeat while" shape:"control" docs:"Repeats the body while its Boolean condition is true."] */
            while (!stopRequested) {
                /* block comment [block:"MusicManager_block_139" text:"Runs the read action. Values in this block are editable inputs."] */
                /* block start [id:"MusicManager_block_139" category:"My Blocks" label:"read" shape:"command" docs:"Runs the read action. Values in this block are editable inputs."] */
                n = in.read(buf, /* param start [id:"MusicManager_value_216" type:"number"] */0/* param end [id:"MusicManager_value_216"] */, buf.length);
                /* block end [id:"MusicManager_block_139"] */
                /* block comment [block:"MusicManager_block_140" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"MusicManager_block_140" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"MusicManager_block_40" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"MusicManager_block_40" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (n == -/* param start [id:"MusicManager_value_217" type:"number"] */1/* param end [id:"MusicManager_value_217"] */) break;
                /* block end [id:"MusicManager_block_140"] */
                /* block end [id:"MusicManager_block_40"] */ // end of file -> loop or finish
                /* block comment [block:"MusicManager_block_41" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"MusicManager_block_41" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (n > /* param start [id:"MusicManager_value_218" type:"number"] */0/* param end [id:"MusicManager_value_218"] */) {
                    /* block comment [block:"MusicManager_block_42" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"MusicManager_block_42" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (gain != /* param start [id:"MusicManager_value_219" type:"value"] */null/* param end [id:"MusicManager_value_219"] */ && fadeInMs > /* param start [id:"MusicManager_value_220" type:"number"] */0/* param end [id:"MusicManager_value_220"] */) {
                        /* block comment [block:"MusicManager_block_141" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                        /* block start [id:"MusicManager_block_141" category:"Variables" label:"set k" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                        float k = (float) (System.currentTimeMillis() - fadeStart) / fadeInMs;
                        /* block end [id:"MusicManager_block_141"] */
                        /* block comment [block:"MusicManager_block_43" text:"Checks a Boolean condition and runs the body when it is true."] */
                        /* block start [id:"MusicManager_block_43" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                        if (k >= /* param start [id:"MusicManager_value_221" type:"number"] */1/* param end [id:"MusicManager_value_221"] */) {
                            /* block comment [block:"MusicManager_block_142" text:"Runs the set value action. Values in this block are editable inputs."] */
                            /* block start [id:"MusicManager_block_142" category:"Variables" label:"set value" shape:"command" docs:"Runs the set value action. Values in this block are editable inputs."] */
                            gain.setValue(targetDb);
                            /* block end [id:"MusicManager_block_142"] */
                            /* block comment [block:"MusicManager_block_143" text:"Evaluates this expression and applies its result."] */
                            /* block start [id:"MusicManager_block_143" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                            fadeInMs = /* param start [id:"MusicManager_value_222" type:"number"] */0/* param end [id:"MusicManager_value_222"] */;
                            /* block end [id:"MusicManager_block_143"] */
                        } else /* block comment [block:"MusicManager_block_44" text:"Checks a Boolean condition and runs the body when it is true."] */
                        /* block start [id:"MusicManager_block_44" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                        if (k >= /* param start [id:"MusicManager_value_223" type:"number"] */0/* param end [id:"MusicManager_value_223"] */) {
                            /* block comment [block:"MusicManager_block_144" text:"Runs the set value action. Values in this block are editable inputs."] */
                            /* block start [id:"MusicManager_block_144" category:"Variables" label:"set value" shape:"command" docs:"Runs the set value action. Values in this block are editable inputs."] */
                            gain.setValue(FADE_FLOOR_DB + (targetDb - FADE_FLOOR_DB) * k);
                            /* block end [id:"MusicManager_block_144"] */
                        }
                        /* block end [id:"MusicManager_block_44"] */
                        /* block end [id:"MusicManager_block_43"] */
                    } else /* block comment [block:"MusicManager_block_45" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"MusicManager_block_45" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (gain != /* param start [id:"MusicManager_value_224" type:"value"] */null/* param end [id:"MusicManager_value_224"] */) {
                        /* block comment [block:"MusicManager_block_145" text:"Runs the set value action. Values in this block are editable inputs."] */
                        /* block start [id:"MusicManager_block_145" category:"Variables" label:"set value" shape:"command" docs:"Runs the set value action. Values in this block are editable inputs."] */
                        gain.setValue(targetDb);
                        /* block end [id:"MusicManager_block_145"] */
                    }
                    /* block end [id:"MusicManager_block_45"] */
                    /* block end [id:"MusicManager_block_42"] */
                    /* block comment [block:"MusicManager_block_146" text:"Calls the write method using its declared arguments."] */
                    /* block start [id:"MusicManager_block_146" category:"My Blocks" label:"call write" shape:"command" docs:"Calls the write method using its declared arguments."] */
                    line.write(buf, /* param start [id:"MusicManager_value_225" type:"number"] */0/* param end [id:"MusicManager_value_225"] */, n);
                    /* block end [id:"MusicManager_block_146"] */
                }
                /* block end [id:"MusicManager_block_41"] */
                /* block comment [block:"MusicManager_block_147" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"MusicManager_block_147" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"MusicManager_block_46" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"MusicManager_block_46" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (Thread.currentThread().isInterrupted()) break;
                /* block end [id:"MusicManager_block_147"] */
                /* block end [id:"MusicManager_block_46"] */
            }
            /* block end [id:"MusicManager_block_39"] */
            /* block comment [block:"MusicManager_block_148" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"MusicManager_block_148" category:"Variables" label:"set naturalEnd" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            boolean naturalEnd = !stopRequested && !Thread.currentThread().isInterrupted();
            /* block end [id:"MusicManager_block_148"] */
            /* block comment [block:"MusicManager_block_149" text:"Calls the drain method using its declared arguments."] */
            /* block start [id:"MusicManager_block_149" category:"My Blocks" label:"call drain" shape:"command" docs:"Calls the drain method using its declared arguments."] */
            line.drain();
            /* block end [id:"MusicManager_block_149"] */
            /* block comment [block:"MusicManager_block_150" text:"Returns a value or ends the current method."] */
            /* block start [id:"MusicManager_block_150" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return naturalEnd;
            /* block end [id:"MusicManager_block_150"] */
        }
        /* block end [id:"MusicManager_block_38"] */ finally {
            /* block comment [block:"MusicManager_block_47" text:"Runs work safely and handles matching errors in a catch branch."] */
            /* block start [id:"MusicManager_block_47" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
            try { /* block comment [block:"MusicManager_block_151" text:"Calls the stop method using its declared arguments."] */
                /* block start [id:"MusicManager_block_151" category:"My Blocks" label:"call stop" shape:"command" docs:"Calls the stop method using its declared arguments."] */
                line.stop();
                /* block end [id:"MusicManager_block_151"] */ }
            /* block end [id:"MusicManager_block_47"] */ catch (Exception ignored) {}
            /* block comment [block:"MusicManager_block_48" text:"Runs work safely and handles matching errors in a catch branch."] */
            /* block start [id:"MusicManager_block_48" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
            try { /* block comment [block:"MusicManager_block_152" text:"Calls the close method using its declared arguments."] */
                /* block start [id:"MusicManager_block_152" category:"My Blocks" label:"call close" shape:"command" docs:"Calls the close method using its declared arguments."] */
                line.close();
                /* block end [id:"MusicManager_block_152"] */ }
            /* block end [id:"MusicManager_block_48"] */ catch (Exception ignored) {}
            /* block comment [block:"MusicManager_block_49" text:"Runs work safely and handles matching errors in a catch branch."] */
            /* block start [id:"MusicManager_block_49" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
            try { /* block comment [block:"MusicManager_block_153" text:"Calls the close method using its declared arguments."] */
                /* block start [id:"MusicManager_block_153" category:"My Blocks" label:"call close" shape:"command" docs:"Calls the close method using its declared arguments."] */
                in.close();
                /* block end [id:"MusicManager_block_153"] */ }
            /* block end [id:"MusicManager_block_49"] */ catch (Exception ignored) {}
        }
    }

    private static float toDb(double percent) {
        /* block comment [block:"MusicManager_block_154" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"MusicManager_block_154" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"MusicManager_block_50" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"MusicManager_block_50" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (percent <= /* param start [id:"MusicManager_value_226" type:"number"] */0/* param end [id:"MusicManager_value_226"] */) return -/* param start [id:"MusicManager_value_227" type:"number"] */80.0f/* param end [id:"MusicManager_value_227"] */;
        /* block end [id:"MusicManager_block_154"] */
        /* block end [id:"MusicManager_block_50"] */
        /* block comment [block:"MusicManager_block_155" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MusicManager_block_155" category:"Variables" label:"set db" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        float db = (float) (Math.log(percent / /* param start [id:"MusicManager_value_228" type:"number"] */100.0/* param end [id:"MusicManager_value_228"] */) / Math.log(/* param start [id:"MusicManager_value_229" type:"number"] */10.0/* param end [id:"MusicManager_value_229"] */) * /* param start [id:"MusicManager_value_230" type:"number"] */20.0/* param end [id:"MusicManager_value_230"] */);
        /* block end [id:"MusicManager_block_155"] */
        /* block comment [block:"MusicManager_block_156" text:"Returns a value or ends the current method."] */
        /* block start [id:"MusicManager_block_156" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return Math.max(-/* param start [id:"MusicManager_value_231" type:"number"] */80.0f/* param end [id:"MusicManager_value_231"] */, db);
        /* block end [id:"MusicManager_block_156"] */
    }
}
