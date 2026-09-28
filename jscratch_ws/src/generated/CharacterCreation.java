package generated;

import com.jscratch.Costume;
import com.jscratch.Sprite;
import com.jscratch.Stage;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

/**
* Clickable character-creation GUI, rendered on-stage with standard Graphics2D.
* All text is drawn with the BitmapFont glyph images from jscratch_ws/assets;
* no D&D engine dependency — uses PlayerStats generic helpers.
*
* Pages: TITLE -> CLASS (name + 13 clickable class cards) -> STATS
* (method tabs, reroll, point-buy) -> broadcasts "characterReady".
* Only com.jscratch.* + java.* imports.
*/
public class CharacterCreation extends Sprite {

    private static final String BG_TITLE = "jscratch_ws/assets/cc_title.png";
    private static final String BG_CLASS = "jscratch_ws/assets/cc_class.png";
    private static final String BG_STATS = "jscratch_ws/assets/cc_stats.png";
    private static final String TITLE_LOGO = "jscratch_ws/assets/title.png";
    private static final String CLICK_SFX = "jscratch_ws/assets/Sprite6_pop.wav";

    private static final int NAME_MAX = 12;

    private static final Color GOLD = new Color(226, 200, 120);
    private static final Color INK = Color.WHITE;
    private static final Color SEL_TEXT = Color.YELLOW;
    private static final Color DIM = new Color(140, 140, 155);
    private static final Color FAINT = new Color(170, 170, 185);
    private static final Color BODY = new Color(200, 200, 210);
    private static final Color SUBTLE = new Color(160, 160, 175);
    private static final Color BAD = new Color(255, 120, 120);
    private static final Color FILL = new Color(42, 42, 58);
    private static final Color FILL_HOVER = new Color(62, 62, 82);
    private static final Color PRIMARY = new Color(95, 75, 35);
    private static final Color PRIMARY_HOVER = new Color(120, 95, 45);
    private static final Color DISABLED = new Color(45, 45, 55);
    private static final Color DISABLED_TEXT = new Color(130, 130, 140);
    private static final Color EDGE = new Color(150, 140, 120);
    private static final Color TAB_REST = new Color(35, 35, 48);
    private static final Color TAB_EDGE = new Color(120, 115, 130);
    private static final Color CARD_REST = new Color(30, 30, 42, 230);
    private static final Color CARD_EDGE = new Color(100, 95, 115);
    private static final Color CARD_HOVER_EDGE = new Color(180, 170, 140);
    private static final Color STEP_REST = new Color(70, 60, 35);
    private static final Color STEP_OFF = new Color(40, 40, 50);
    private static final Color STEP_OFF_EDGE = new Color(90, 90, 100);
    private static final Color STEP_OFF_TEXT = new Color(120, 120, 130);
    private static final Color FIELD_REST = new Color(30, 30, 42, 230);
    private static final Color FIELD_FOCUS = new Color(50, 50, 70);
    private static final Color FIELD_EDGE = new Color(100, 95, 115);
    private static final Color FIELD_EMPTY = new Color(130, 130, 145);
    private static final Color PANEL_FILL = new Color(14, 14, 22, 225);
    private static final Color ROW_SEL = new Color(90, 80, 40, 160);
    private static final Color VEIL = new Color(0, 0, 0, 110);
    private static final Color GRAD_TOP = new Color(24, 22, 36);
    private static final Color GRAD_BOT = new Color(8, 8, 14);
    private static final Color GLOW = new Color(60, 50, 90, 60);
    private static final Color LABEL_GREY = new Color(170, 170, 185);
    private static final Color DETAIL_GREY = new Color(190, 190, 200);

    public static final String[] CLASS_ORDER = {
        "artificer", "barbarian", "bard", "cleric", "druid", "fighter",
        "monk", "paladin", "ranger", "rogue", "sorcerer", "warlock", "wizard"
    };

    private int phase = 0;
    /** Optional callback for the title screen's in-game Controls button. */
    private Runnable controlsAction;
    private boolean controlsShown = false;
    private static final int KIND_OVERLAY = 5;
    private int classIndex = 5;
    private int method = 0;
    private int[] scores = PlayerStats.getStandardArray();
    private String heroName = "Hero";
    private boolean nameFocused = false;
    public static final int PB_BUDGET = 27;
    public static final int PB_MIN = 8;
    public static final int PB_MAX = 15;
    private int[] pb = {8, 8, 8, 8, 8, 8};
    private int sel = 0;
    private volatile String hoverId = null;
    private BufferedImage bgTitle, bgClass, bgStats, titleLogo;
    private GradientPaint gradCache;
    private int gradH = -1;
    private boolean prevLeft, prevRight, prevEnter, prevR, prevBack;
    private boolean prev1, prev2, prev3, prevUp, prevDown, prevM, prevH, prevEsc;
    private boolean prevMouse;
    private final boolean[] prevType = new boolean[65536];
    private static final int KIND_BUTTON = 0;
    private static final int KIND_TAB = 1;
    private static final int KIND_CARD = 2;
    private static final int KIND_STEPPER = 3;
    private static final int KIND_FIELD = 4;

    private static final class Btn {
        /* block comment [block:"CharacterCreation_block_173" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_173" category:"Variables" label:"set id" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        final String id;
        /* block end [id:"CharacterCreation_block_173"] */
        /* block comment [block:"CharacterCreation_block_174" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_174" category:"Variables" label:"set kind" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        final int kind;
        /* block end [id:"CharacterCreation_block_174"] */
        /* block comment [block:"CharacterCreation_block_175" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_175" category:"Variables" label:"set idx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        final int idx;
        /* block end [id:"CharacterCreation_block_175"] */
        /* block comment [block:"CharacterCreation_block_176" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_176" category:"Variables" label:"set x" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        final int x, y, w, h;
        /* block end [id:"CharacterCreation_block_176"] */
        /* block comment [block:"CharacterCreation_block_177" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_177" category:"Variables" label:"set enabled" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        final boolean enabled;
        /* block end [id:"CharacterCreation_block_177"] */
        Btn(String id, int kind, int idx, int x, int y, int w, int h, boolean enabled) {
            /* block comment [block:"CharacterCreation_block_178" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_178" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.id = id;
            /* block end [id:"CharacterCreation_block_178"] */ /* block comment [block:"CharacterCreation_block_179" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_179" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.kind = kind;
            /* block end [id:"CharacterCreation_block_179"] */ /* block comment [block:"CharacterCreation_block_180" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_180" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.idx = idx;
            /* block end [id:"CharacterCreation_block_180"] */
            /* block comment [block:"CharacterCreation_block_181" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_181" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.x = x;
            /* block end [id:"CharacterCreation_block_181"] */ /* block comment [block:"CharacterCreation_block_182" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_182" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.y = y;
            /* block end [id:"CharacterCreation_block_182"] */ /* block comment [block:"CharacterCreation_block_183" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_183" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.w = w;
            /* block end [id:"CharacterCreation_block_183"] */ /* block comment [block:"CharacterCreation_block_184" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_184" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.h = h;
            /* block end [id:"CharacterCreation_block_184"] */ /* block comment [block:"CharacterCreation_block_185" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_185" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            this.enabled = enabled;
            /* block end [id:"CharacterCreation_block_185"] */
        }
        boolean hits(int mx, int my) { /* block comment [block:"CharacterCreation_block_186" text:"Returns a value or ends the current method."] */
            /* block start [id:"CharacterCreation_block_186" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return enabled && mx >= x && mx <= x + w && my >= y && my <= y + h;
            /* block end [id:"CharacterCreation_block_186"] */ }
    }

    private volatile List<Btn> buttonsCache;
    private int cacheW = -1, cacheH = -1;
    private volatile boolean dirty = true;
    private int lastW = -1, lastH = -1;
    private int classPanelX, classPanelY, classPanelW, classPanelH;
    private int pbRowY0, pbRowH;

    public CharacterCreation() {
        /* block comment [block:"CharacterCreation_block_187" text:"Runs the super action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_187" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
        super(/* param start [id:"CharacterCreation_value_795" type:"text"] */"CharacterCreation"/* param end [id:"CharacterCreation_value_795"] */);
        /* block end [id:"CharacterCreation_block_187"] */
        /* block comment [block:"CharacterCreation_block_188" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_188" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(new Costume(/* param start [id:"CharacterCreation_value_796" type:"text"] */"blank"/* param end [id:"CharacterCreation_value_796"] */, /* param start [id:"CharacterCreation_value_797" type:"text"] */"jscratch_ws/assets/error.png"/* param end [id:"CharacterCreation_value_797"] */, /* param start [id:"CharacterCreation_value_798" type:"number"] */0/* param end [id:"CharacterCreation_value_798"] */, /* param start [id:"CharacterCreation_value_799" type:"number"] */0/* param end [id:"CharacterCreation_value_799"] */));
        /* block end [id:"CharacterCreation_block_188"] */
        /* block comment [block:"CharacterCreation_block_189" text:"Runs the load bg action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_189" category:"My Blocks" label:"load bg" shape:"command" docs:"Runs the load bg action. Values in this block are editable inputs."] */
        bgTitle = loadBg(BG_TITLE);
        /* block end [id:"CharacterCreation_block_189"] */
        /* block comment [block:"CharacterCreation_block_190" text:"Runs the load bg action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_190" category:"My Blocks" label:"load bg" shape:"command" docs:"Runs the load bg action. Values in this block are editable inputs."] */
        bgClass = loadBg(BG_CLASS);
        /* block end [id:"CharacterCreation_block_190"] */
        /* block comment [block:"CharacterCreation_block_191" text:"Runs the load bg action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_191" category:"My Blocks" label:"load bg" shape:"command" docs:"Runs the load bg action. Values in this block are editable inputs."] */
        bgStats = loadBg(BG_STATS);
        /* block end [id:"CharacterCreation_block_191"] */
        /* block comment [block:"CharacterCreation_block_192" text:"Runs the load bg action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_192" category:"My Blocks" label:"load bg" shape:"command" docs:"Runs the load bg action. Values in this block are editable inputs."] */
        titleLogo = loadBg(TITLE_LOGO);
        /* block end [id:"CharacterCreation_block_192"] */
        /* block comment [block:"CharacterCreation_block_193" text:"Runs the go to action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_193" category:"Motion" label:"go to" shape:"command" docs:"Runs the go to action. Values in this block are editable inputs."] */
        goTo(/* param start [id:"CharacterCreation_value_800" type:"number"] */0/* param end [id:"CharacterCreation_value_800"] */, /* param start [id:"CharacterCreation_value_801" type:"number"] */0/* param end [id:"CharacterCreation_value_801"] */);
        /* block end [id:"CharacterCreation_block_193"] */
        /* block comment [block:"CharacterCreation_block_194" text:"Runs the show action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_194" category:"Looks" label:"show" shape:"command" docs:"Runs the show action. Values in this block are editable inputs."] */
        show();
        /* block end [id:"CharacterCreation_block_194"] */
        /* block comment [block:"CharacterCreation_block_1" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"CharacterCreation_block_1" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try { /* block comment [block:"CharacterCreation_block_195" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_195" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Stage.getInstance().goToFront(this);
            /* block end [id:"CharacterCreation_block_195"] */ }
        /* block end [id:"CharacterCreation_block_1"] */ catch (Exception ignored) {}
        /* block comment [block:"CharacterCreation_block_196" text:"Calls the pushWatchers method using its declared arguments."] */
        /* block start [id:"CharacterCreation_block_196" category:"My Blocks" label:"call push watchers" shape:"command" docs:"Calls the pushWatchers method using its declared arguments."] */
        pushWatchers();
        /* block end [id:"CharacterCreation_block_196"] */
        /* block comment [block:"CharacterCreation_block_2" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"CharacterCreation_block_2" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try {
            Stage.getInstance().whenIReceive(/* param start [id:"CharacterCreation_value_802" type:"text"] */"enterCharacterCreate"/* param end [id:"CharacterCreation_value_802"] */, () -> {
                phase = /* param start [id:"CharacterCreation_value_803" type:"number"] */0/* param end [id:"CharacterCreation_value_803"] */; nameFocused = /* param start [id:"CharacterCreation_value_804" type:"boolean"] */false/* param end [id:"CharacterCreation_value_804"] */; show();
                /* block comment [block:"CharacterCreation_block_3" text:"Runs work safely and handles matching errors in a catch branch."] */
                /* block start [id:"CharacterCreation_block_3" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
                try { Stage.getInstance().goToFront(this); }
                /* block end [id:"CharacterCreation_block_3"] */ catch (Exception ignored) {}
                pushWatchers();
            }/* block comment [block:"CharacterCreation_block_197" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_197" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            );
            /* block end [id:"CharacterCreation_block_197"] */
        }
        /* block end [id:"CharacterCreation_block_2"] */ catch (Exception ignored) {}
        /* block comment [block:"CharacterCreation_block_198" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_198" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().whenIReceive(/* param start [id:"CharacterCreation_value_805" type:"text"] */"showControls"/* param end [id:"CharacterCreation_value_805"] */, () -> openControls());
        /* block end [id:"CharacterCreation_block_198"] */
        /* block comment [block:"CharacterCreation_block_199" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_199" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().whenIReceive(/* param start [id:"CharacterCreation_value_806" type:"text"] */"hideControls"/* param end [id:"CharacterCreation_value_806"] */, () -> closeControls());
        /* block end [id:"CharacterCreation_block_199"] */
        /* block comment [block:"CharacterCreation_block_200" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_200" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().whenIReceive(/* param start [id:"CharacterCreation_value_807" type:"text"] */"playerDied"/* param end [id:"CharacterCreation_value_807"] */, () -> showGameOver());
        /* block end [id:"CharacterCreation_block_200"] */
        this.startScript(() -> {
            /* block comment [block:"CharacterCreation_block_4" text:"Repeats the body while its Boolean condition is true."] */
            /* block start [id:"CharacterCreation_block_4" category:"Control" label:"repeat while" shape:"control" docs:"Repeats the body while its Boolean condition is true."] */
            while (/* param start [id:"CharacterCreation_value_808" type:"boolean"] */true/* param end [id:"CharacterCreation_value_808"] */) {
                /* block comment [block:"CharacterCreation_block_5" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"CharacterCreation_block_5" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if ((phase < /* param start [id:"CharacterCreation_value_809" type:"number"] */3/* param end [id:"CharacterCreation_value_809"] */ && GameState.isCharacterCreate()) || controlsShown || GameState.isGameOver() || GameState.isGameplay()) {
                    pollKeys(); pollMouse(); pollNameTyping();
                }
                /* block end [id:"CharacterCreation_block_5"] */
                waitTillNextFrame();
            }
            /* block end [id:"CharacterCreation_block_4"] */
        }/* block comment [block:"CharacterCreation_block_201" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_201" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        );
        /* block end [id:"CharacterCreation_block_201"] */
    }

    /** Open the controls overlay and pause the gameplay loops. */
    public void openControls() {
        /* block comment [block:"CharacterCreation_block_202" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_202" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_6" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_6" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (GameState.isGameOver()) return;
        /* block end [id:"CharacterCreation_block_202"] */
        /* block end [id:"CharacterCreation_block_6"] */
        /* block comment [block:"CharacterCreation_block_203" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_203" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        controlsShown = /* param start [id:"CharacterCreation_value_810" type:"boolean"] */true/* param end [id:"CharacterCreation_value_810"] */;
        /* block end [id:"CharacterCreation_block_203"] */
        /* block comment [block:"CharacterCreation_block_204" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_204" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        dirty = /* param start [id:"CharacterCreation_value_811" type:"boolean"] */true/* param end [id:"CharacterCreation_value_811"] */;
        /* block end [id:"CharacterCreation_block_204"] */
        /* block comment [block:"CharacterCreation_block_205" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_205" category:"Variables" label:"set setControlsVisible" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        GameState.setControlsVisible(/* param start [id:"CharacterCreation_value_812" type:"boolean"] */true/* param end [id:"CharacterCreation_value_812"] */);
        /* block end [id:"CharacterCreation_block_205"] */
        /* block comment [block:"CharacterCreation_block_206" text:"Runs the show action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_206" category:"Looks" label:"show" shape:"command" docs:"Runs the show action. Values in this block are editable inputs."] */
        show();
        /* block end [id:"CharacterCreation_block_206"] */
        /* block comment [block:"CharacterCreation_block_7" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"CharacterCreation_block_7" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try { /* block comment [block:"CharacterCreation_block_207" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_207" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Stage.getInstance().goToFront(this);
            /* block end [id:"CharacterCreation_block_207"] */ }
        /* block end [id:"CharacterCreation_block_7"] */ catch (Exception ignored) {}
    }

    /** Close the controls overlay and resume the same run. */
    public void closeControls() {
        /* block comment [block:"CharacterCreation_block_208" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_208" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        controlsShown = /* param start [id:"CharacterCreation_value_813" type:"boolean"] */false/* param end [id:"CharacterCreation_value_813"] */;
        /* block end [id:"CharacterCreation_block_208"] */
        /* block comment [block:"CharacterCreation_block_209" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_209" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        dirty = /* param start [id:"CharacterCreation_value_814" type:"boolean"] */true/* param end [id:"CharacterCreation_value_814"] */;
        /* block end [id:"CharacterCreation_block_209"] */
        /* block comment [block:"CharacterCreation_block_210" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_210" category:"Variables" label:"set setControlsVisible" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        GameState.setControlsVisible(/* param start [id:"CharacterCreation_value_815" type:"boolean"] */false/* param end [id:"CharacterCreation_value_815"] */);
        /* block end [id:"CharacterCreation_block_210"] */
        /* block comment [block:"CharacterCreation_block_211" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_211" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_8" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_8" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (GameState.isGameplay()) hide();
        /* block end [id:"CharacterCreation_block_211"] */
        /* block end [id:"CharacterCreation_block_8"] */
    }

    /** Reuse the existing character creator when the hero dies. */
    private void showGameOver() {
        /* block comment [block:"CharacterCreation_block_212" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_212" category:"Variables" label:"set setGameOver" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        GameState.setGameOver(/* param start [id:"CharacterCreation_value_816" type:"boolean"] */true/* param end [id:"CharacterCreation_value_816"] */);
        /* block end [id:"CharacterCreation_block_212"] */
        /* block comment [block:"CharacterCreation_block_213" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_213" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        controlsShown = /* param start [id:"CharacterCreation_value_817" type:"boolean"] */false/* param end [id:"CharacterCreation_value_817"] */;
        /* block end [id:"CharacterCreation_block_213"] */
        /* block comment [block:"CharacterCreation_block_214" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_214" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        phase = /* param start [id:"CharacterCreation_value_818" type:"number"] */4/* param end [id:"CharacterCreation_value_818"] */;
        /* block end [id:"CharacterCreation_block_214"] */
        /* block comment [block:"CharacterCreation_block_215" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_215" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        dirty = /* param start [id:"CharacterCreation_value_819" type:"boolean"] */true/* param end [id:"CharacterCreation_value_819"] */;
        /* block end [id:"CharacterCreation_block_215"] */
        /* block comment [block:"CharacterCreation_block_216" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_216" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        hoverId = /* param start [id:"CharacterCreation_value_820" type:"value"] */null/* param end [id:"CharacterCreation_value_820"] */;
        /* block end [id:"CharacterCreation_block_216"] */
        /* block comment [block:"CharacterCreation_block_217" text:"Runs the show action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_217" category:"Looks" label:"show" shape:"command" docs:"Runs the show action. Values in this block are editable inputs."] */
        show();
        /* block end [id:"CharacterCreation_block_217"] */
        /* block comment [block:"CharacterCreation_block_9" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"CharacterCreation_block_9" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try { /* block comment [block:"CharacterCreation_block_218" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_218" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Stage.getInstance().goToFront(this);
            /* block end [id:"CharacterCreation_block_218"] */ }
        /* block end [id:"CharacterCreation_block_9"] */ catch (Exception ignored) {}
        /* block comment [block:"CharacterCreation_block_219" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_219" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        dirty = /* param start [id:"CharacterCreation_value_821" type:"boolean"] */true/* param end [id:"CharacterCreation_value_821"] */;
        /* block end [id:"CharacterCreation_block_219"] */
    }

    private static BufferedImage loadBg(String path) {
        /* block comment [block:"CharacterCreation_block_10" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"CharacterCreation_block_10" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try {
            /* block comment [block:"CharacterCreation_block_220" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_220" category:"Variables" label:"set f" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            File f = new File(path);
            /* block end [id:"CharacterCreation_block_220"] */
            /* block comment [block:"CharacterCreation_block_11" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"CharacterCreation_block_11" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (!f.exists()) {
                /* block comment [block:"CharacterCreation_block_221" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_221" category:"Variables" label:"set root" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                String root = System.getProperty(/* param start [id:"CharacterCreation_value_822" type:"text"] */"jscratch.project.root"/* param end [id:"CharacterCreation_value_822"] */);
                /* block end [id:"CharacterCreation_block_221"] */
                /* block comment [block:"CharacterCreation_block_12" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"CharacterCreation_block_12" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (root != /* param start [id:"CharacterCreation_value_823" type:"value"] */null/* param end [id:"CharacterCreation_value_823"] */) { /* block comment [block:"CharacterCreation_block_222" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    /* block start [id:"CharacterCreation_block_222" category:"Variables" label:"set r" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    File r = new File(root, path);
                    /* block end [id:"CharacterCreation_block_222"] */ /* block comment [block:"CharacterCreation_block_223" text:"Runs the if action. Values in this block are editable inputs."] */
                    /* block start [id:"CharacterCreation_block_223" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                    /* block comment [block:"CharacterCreation_block_13" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"CharacterCreation_block_13" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (r.exists()) f = r;
                    /* block end [id:"CharacterCreation_block_223"] */
                    /* block end [id:"CharacterCreation_block_13"] */ }
                /* block end [id:"CharacterCreation_block_12"] */
            }
            /* block end [id:"CharacterCreation_block_11"] */
            /* block comment [block:"CharacterCreation_block_224" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"CharacterCreation_block_224" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"CharacterCreation_block_14" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"CharacterCreation_block_14" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (f.exists()) return ImageIO.read(f);
            /* block end [id:"CharacterCreation_block_224"] */
            /* block end [id:"CharacterCreation_block_14"] */
            /* block comment [block:"CharacterCreation_block_225" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_225" category:"Variables" label:"set cl" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            /* block end [id:"CharacterCreation_block_225"] */
            /* block comment [block:"CharacterCreation_block_226" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"CharacterCreation_block_226" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"CharacterCreation_block_15" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"CharacterCreation_block_15" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (cl == /* param start [id:"CharacterCreation_value_824" type:"value"] */null/* param end [id:"CharacterCreation_value_824"] */) cl = CharacterCreation.class.getClassLoader();
            /* block end [id:"CharacterCreation_block_226"] */
            /* block end [id:"CharacterCreation_block_15"] */
            /* block comment [block:"CharacterCreation_block_227" text:"Runs the get resource action. Values in this block are editable inputs."] */
            /* block start [id:"CharacterCreation_block_227" category:"My Blocks" label:"get resource" shape:"command" docs:"Runs the get resource action. Values in this block are editable inputs."] */
            java.net.URL url = cl != /* param start [id:"CharacterCreation_value_825" type:"value"] */null/* param end [id:"CharacterCreation_value_825"] */ ? cl.getResource(path) : /* param start [id:"CharacterCreation_value_826" type:"value"] */null/* param end [id:"CharacterCreation_value_826"] */;
            /* block end [id:"CharacterCreation_block_227"] */
            /* block comment [block:"CharacterCreation_block_228" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"CharacterCreation_block_228" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"CharacterCreation_block_16" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"CharacterCreation_block_16" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (url != /* param start [id:"CharacterCreation_value_827" type:"value"] */null/* param end [id:"CharacterCreation_value_827"] */) return ImageIO.read(url);
            /* block end [id:"CharacterCreation_block_228"] */
            /* block end [id:"CharacterCreation_block_16"] */
        }
        /* block end [id:"CharacterCreation_block_10"] */ catch (Exception ignored) {}
        /* block comment [block:"CharacterCreation_block_229" text:"Returns a value or ends the current method."] */
        /* block start [id:"CharacterCreation_block_229" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return /* param start [id:"CharacterCreation_value_828" type:"value"] */null/* param end [id:"CharacterCreation_value_828"] */;
        /* block end [id:"CharacterCreation_block_229"] */
    }

    private boolean keyLeft() { /* block comment [block:"CharacterCreation_block_230" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_230" category:"Variables" label:"set s" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage s = Stage.getInstance();
        /* block end [id:"CharacterCreation_block_230"] */ /* block comment [block:"CharacterCreation_block_231" text:"Returns a value or ends the current method."] */
        /* block start [id:"CharacterCreation_block_231" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return s.isKeyPressed(KeyEvent.VK_LEFT) || (!nameFocused && s.isKeyPressed(/* param start [id:"CharacterCreation_value_829" type:"text"] */"a"/* param end [id:"CharacterCreation_value_829"] */));
        /* block end [id:"CharacterCreation_block_231"] */ }
    private boolean keyRight() { /* block comment [block:"CharacterCreation_block_232" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_232" category:"Variables" label:"set s" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage s = Stage.getInstance();
        /* block end [id:"CharacterCreation_block_232"] */ /* block comment [block:"CharacterCreation_block_233" text:"Returns a value or ends the current method."] */
        /* block start [id:"CharacterCreation_block_233" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return s.isKeyPressed(KeyEvent.VK_RIGHT) || (!nameFocused && s.isKeyPressed(/* param start [id:"CharacterCreation_value_830" type:"text"] */"d"/* param end [id:"CharacterCreation_value_830"] */));
        /* block end [id:"CharacterCreation_block_233"] */ }
    private boolean keyEnter() { /* block comment [block:"CharacterCreation_block_234" text:"Returns a value or ends the current method."] */
        /* block start [id:"CharacterCreation_block_234" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return Stage.getInstance().isKeyPressed(KeyEvent.VK_ENTER);
        /* block end [id:"CharacterCreation_block_234"] */ }
    private boolean keyBack() { /* block comment [block:"CharacterCreation_block_235" text:"Returns a value or ends the current method."] */
        /* block start [id:"CharacterCreation_block_235" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return Stage.getInstance().isKeyPressed(KeyEvent.VK_BACK_SPACE);
        /* block end [id:"CharacterCreation_block_235"] */ }
    private boolean keyUp() { /* block comment [block:"CharacterCreation_block_236" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_236" category:"Variables" label:"set s" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage s = Stage.getInstance();
        /* block end [id:"CharacterCreation_block_236"] */ /* block comment [block:"CharacterCreation_block_237" text:"Returns a value or ends the current method."] */
        /* block start [id:"CharacterCreation_block_237" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return s.isKeyPressed(KeyEvent.VK_UP) || (!nameFocused && s.isKeyPressed(/* param start [id:"CharacterCreation_value_831" type:"text"] */"w"/* param end [id:"CharacterCreation_value_831"] */));
        /* block end [id:"CharacterCreation_block_237"] */ }
    private boolean keyDown() { /* block comment [block:"CharacterCreation_block_238" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_238" category:"Variables" label:"set s" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage s = Stage.getInstance();
        /* block end [id:"CharacterCreation_block_238"] */ /* block comment [block:"CharacterCreation_block_239" text:"Returns a value or ends the current method."] */
        /* block start [id:"CharacterCreation_block_239" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return s.isKeyPressed(KeyEvent.VK_DOWN) || (!nameFocused && s.isKeyPressed(/* param start [id:"CharacterCreation_value_832" type:"text"] */"s"/* param end [id:"CharacterCreation_value_832"] */));
        /* block end [id:"CharacterCreation_block_239"] */ }
    private static boolean edge(boolean now, boolean prev) { /* block comment [block:"CharacterCreation_block_240" text:"Returns a value or ends the current method."] */
        /* block start [id:"CharacterCreation_block_240" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return now && !prev;
        /* block end [id:"CharacterCreation_block_240"] */ }
    private void click() { /* block comment [block:"CharacterCreation_block_17" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"CharacterCreation_block_17" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try { /* block comment [block:"CharacterCreation_block_241" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_241" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Stage.getInstance().playSound(CLICK_SFX);
            /* block end [id:"CharacterCreation_block_241"] */ }
        /* block end [id:"CharacterCreation_block_17"] */ catch (Exception ignored) {} }

    private void pollKeys() {
        /* block comment [block:"CharacterCreation_block_242" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_242" category:"Variables" label:"set left" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        boolean left = keyLeft();
        /* block end [id:"CharacterCreation_block_242"] */ /* block comment [block:"CharacterCreation_block_243" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_243" category:"Variables" label:"set right" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        boolean right = keyRight();
        /* block end [id:"CharacterCreation_block_243"] */ /* block comment [block:"CharacterCreation_block_244" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_244" category:"Variables" label:"set enter" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        boolean enter = keyEnter();
        /* block end [id:"CharacterCreation_block_244"] */
        /* block comment [block:"CharacterCreation_block_245" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_245" category:"Variables" label:"set r" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        boolean r = !nameFocused && Stage.getInstance().isKeyPressed(/* param start [id:"CharacterCreation_value_833" type:"text"] */"r"/* param end [id:"CharacterCreation_value_833"] */);
        /* block end [id:"CharacterCreation_block_245"] */
        /* block comment [block:"CharacterCreation_block_246" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_246" category:"Variables" label:"set back" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        boolean back = keyBack();
        /* block end [id:"CharacterCreation_block_246"] */
        /* block comment [block:"CharacterCreation_block_247" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_247" category:"Variables" label:"set k1" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        boolean k1 = Stage.getInstance().isKeyPressed(/* param start [id:"CharacterCreation_value_834" type:"text"] */"1"/* param end [id:"CharacterCreation_value_834"] */);
        /* block end [id:"CharacterCreation_block_247"] */ /* block comment [block:"CharacterCreation_block_248" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_248" category:"Variables" label:"set k2" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        boolean k2 = Stage.getInstance().isKeyPressed(/* param start [id:"CharacterCreation_value_835" type:"text"] */"2"/* param end [id:"CharacterCreation_value_835"] */);
        /* block end [id:"CharacterCreation_block_248"] */ /* block comment [block:"CharacterCreation_block_249" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_249" category:"Variables" label:"set k3" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        boolean k3 = Stage.getInstance().isKeyPressed(/* param start [id:"CharacterCreation_value_836" type:"text"] */"3"/* param end [id:"CharacterCreation_value_836"] */);
        /* block end [id:"CharacterCreation_block_249"] */
        /* block comment [block:"CharacterCreation_block_250" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_250" category:"Variables" label:"set up" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        boolean up = keyUp();
        /* block end [id:"CharacterCreation_block_250"] */ /* block comment [block:"CharacterCreation_block_251" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_251" category:"Variables" label:"set down" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        boolean down = keyDown();
        /* block end [id:"CharacterCreation_block_251"] */
        /* block comment [block:"CharacterCreation_block_252" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_252" category:"Variables" label:"set esc" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        boolean esc = Stage.getInstance().isKeyPressed(KeyEvent.VK_ESCAPE);
        /* block end [id:"CharacterCreation_block_252"] */
        /* block comment [block:"CharacterCreation_block_253" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_253" category:"Variables" label:"set muteKey" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        boolean muteKey = !nameFocused && Stage.getInstance().isKeyPressed(/* param start [id:"CharacterCreation_value_837" type:"text"] */"m"/* param end [id:"CharacterCreation_value_837"] */);
        /* block end [id:"CharacterCreation_block_253"] */
        /* block comment [block:"CharacterCreation_block_18" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_18" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!GameState.isGameplay() && edge(muteKey, prevM)) { /* block comment [block:"CharacterCreation_block_254" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_254" category:"Variables" label:"set nowMuted" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            boolean nowMuted = MusicDirector.toggleMute();
            /* block end [id:"CharacterCreation_block_254"] */ /* block comment [block:"CharacterCreation_block_255" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_255" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Stage.getInstance().setVariable(/* param start [id:"CharacterCreation_value_838" type:"text"] */"Msg"/* param end [id:"CharacterCreation_value_838"] */, nowMuted ? /* param start [id:"CharacterCreation_value_839" type:"text"] */"Music off (M)"/* param end [id:"CharacterCreation_value_839"] */ : /* param start [id:"CharacterCreation_value_840" type:"text"] */"Music on (M)"/* param end [id:"CharacterCreation_value_840"] */);
            /* block end [id:"CharacterCreation_block_255"] */ }
        /* block end [id:"CharacterCreation_block_18"] */
        /* block comment [block:"CharacterCreation_block_256" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_256" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        prevM = muteKey;
        /* block end [id:"CharacterCreation_block_256"] */
        /* block comment [block:"CharacterCreation_block_257" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_257" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        prevH = /* param start [id:"CharacterCreation_value_841" type:"boolean"] */false/* param end [id:"CharacterCreation_value_841"] */;
        /* block end [id:"CharacterCreation_block_257"] */
        /* block comment [block:"CharacterCreation_block_19" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_19" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (edge(esc, prevEsc)) {
            /* block comment [block:"CharacterCreation_block_258" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_258" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            nameFocused = /* param start [id:"CharacterCreation_value_842" type:"boolean"] */false/* param end [id:"CharacterCreation_value_842"] */;
            /* block end [id:"CharacterCreation_block_258"] */ /* block comment [block:"CharacterCreation_block_259" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_259" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            dirty = /* param start [id:"CharacterCreation_value_843" type:"boolean"] */true/* param end [id:"CharacterCreation_value_843"] */;
            /* block end [id:"CharacterCreation_block_259"] */
            /* block comment [block:"CharacterCreation_block_260" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"CharacterCreation_block_260" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"CharacterCreation_block_20" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"CharacterCreation_block_20" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (controlsShown) closeControls();
            /* block end [id:"CharacterCreation_block_260"] */
            /* block end [id:"CharacterCreation_block_20"] */
        }
        /* block end [id:"CharacterCreation_block_19"] */
        /* block comment [block:"CharacterCreation_block_261" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_261" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        prevEsc = esc;
        /* block end [id:"CharacterCreation_block_261"] */
        /* block comment [block:"CharacterCreation_block_262" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_262" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_21" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_21" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (controlsShown && edge(enter, prevEnter)) closeControls();
        /* block end [id:"CharacterCreation_block_262"] */
        /* block comment [block:"CharacterCreation_block_263" text:"Calls the startNewCharacter method using its declared arguments."] */
        /* block start [id:"CharacterCreation_block_263" category:"My Blocks" label:"call start new character" shape:"command" docs:"Calls the startNewCharacter method using its declared arguments."] */
        else /* block comment [block:"CharacterCreation_block_22" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_22" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (GameState.isGameOver() && edge(enter, prevEnter)) startNewCharacter();
        /* block end [id:"CharacterCreation_block_263"] */
        /* block end [id:"CharacterCreation_block_21"] */
        else /* block comment [block:"CharacterCreation_block_23" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_23" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (phase == /* param start [id:"CharacterCreation_value_844" type:"number"] */0/* param end [id:"CharacterCreation_value_844"] */) {
            /* block comment [block:"CharacterCreation_block_24" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"CharacterCreation_block_24" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (edge(enter, prevEnter)) { /* block comment [block:"CharacterCreation_block_264" text:"Runs the click action. Values in this block are editable inputs."] */
                /* block start [id:"CharacterCreation_block_264" category:"Events" label:"click" shape:"command" docs:"Runs the click action. Values in this block are editable inputs."] */
                click();
                /* block end [id:"CharacterCreation_block_264"] */ /* block comment [block:"CharacterCreation_block_265" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"CharacterCreation_block_265" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                phase = /* param start [id:"CharacterCreation_value_845" type:"number"] */1/* param end [id:"CharacterCreation_value_845"] */;
                /* block end [id:"CharacterCreation_block_265"] */ /* block comment [block:"CharacterCreation_block_266" text:"Calls the pushWatchers method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_266" category:"My Blocks" label:"call push watchers" shape:"command" docs:"Calls the pushWatchers method using its declared arguments."] */
                pushWatchers();
                /* block end [id:"CharacterCreation_block_266"] */ }
            /* block end [id:"CharacterCreation_block_24"] */
        }
        /* block end [id:"CharacterCreation_block_22"] */ else /* block comment [block:"CharacterCreation_block_25" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_25" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (phase == /* param start [id:"CharacterCreation_value_846" type:"number"] */1/* param end [id:"CharacterCreation_value_846"] */) {
            /* block comment [block:"CharacterCreation_block_26" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"CharacterCreation_block_26" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (edge(left, prevLeft)) { /* block comment [block:"CharacterCreation_block_267" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"CharacterCreation_block_267" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                classIndex = (classIndex + CLASS_ORDER.length - /* param start [id:"CharacterCreation_value_847" type:"number"] */1/* param end [id:"CharacterCreation_value_847"] */) % CLASS_ORDER.length;
                /* block end [id:"CharacterCreation_block_267"] */ /* block comment [block:"CharacterCreation_block_268" text:"Calls the pushWatchers method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_268" category:"My Blocks" label:"call push watchers" shape:"command" docs:"Calls the pushWatchers method using its declared arguments."] */
                pushWatchers();
                /* block end [id:"CharacterCreation_block_268"] */ }
            /* block end [id:"CharacterCreation_block_26"] */
            /* block comment [block:"CharacterCreation_block_27" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"CharacterCreation_block_27" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (edge(right, prevRight)) { /* block comment [block:"CharacterCreation_block_269" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"CharacterCreation_block_269" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                classIndex = (classIndex + /* param start [id:"CharacterCreation_value_848" type:"number"] */1/* param end [id:"CharacterCreation_value_848"] */) % CLASS_ORDER.length;
                /* block end [id:"CharacterCreation_block_269"] */ /* block comment [block:"CharacterCreation_block_270" text:"Calls the pushWatchers method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_270" category:"My Blocks" label:"call push watchers" shape:"command" docs:"Calls the pushWatchers method using its declared arguments."] */
                pushWatchers();
                /* block end [id:"CharacterCreation_block_270"] */ }
            /* block end [id:"CharacterCreation_block_27"] */
            /* block comment [block:"CharacterCreation_block_28" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"CharacterCreation_block_28" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (edge(up, prevUp)) { /* block comment [block:"CharacterCreation_block_271" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"CharacterCreation_block_271" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                classIndex = (classIndex + CLASS_ORDER.length - /* param start [id:"CharacterCreation_value_849" type:"number"] */1/* param end [id:"CharacterCreation_value_849"] */) % CLASS_ORDER.length;
                /* block end [id:"CharacterCreation_block_271"] */ /* block comment [block:"CharacterCreation_block_272" text:"Calls the pushWatchers method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_272" category:"My Blocks" label:"call push watchers" shape:"command" docs:"Calls the pushWatchers method using its declared arguments."] */
                pushWatchers();
                /* block end [id:"CharacterCreation_block_272"] */ }
            /* block end [id:"CharacterCreation_block_28"] */
            /* block comment [block:"CharacterCreation_block_29" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"CharacterCreation_block_29" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (edge(down, prevDown)) { /* block comment [block:"CharacterCreation_block_273" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"CharacterCreation_block_273" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                classIndex = (classIndex + /* param start [id:"CharacterCreation_value_850" type:"number"] */1/* param end [id:"CharacterCreation_value_850"] */) % CLASS_ORDER.length;
                /* block end [id:"CharacterCreation_block_273"] */ /* block comment [block:"CharacterCreation_block_274" text:"Calls the pushWatchers method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_274" category:"My Blocks" label:"call push watchers" shape:"command" docs:"Calls the pushWatchers method using its declared arguments."] */
                pushWatchers();
                /* block end [id:"CharacterCreation_block_274"] */ }
            /* block end [id:"CharacterCreation_block_29"] */
            /* block comment [block:"CharacterCreation_block_30" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"CharacterCreation_block_30" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (edge(enter, prevEnter)) { /* block comment [block:"CharacterCreation_block_275" text:"Runs the click action. Values in this block are editable inputs."] */
                /* block start [id:"CharacterCreation_block_275" category:"Events" label:"click" shape:"command" docs:"Runs the click action. Values in this block are editable inputs."] */
                click();
                /* block end [id:"CharacterCreation_block_275"] */ /* block comment [block:"CharacterCreation_block_276" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"CharacterCreation_block_276" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                nameFocused = /* param start [id:"CharacterCreation_value_851" type:"boolean"] */false/* param end [id:"CharacterCreation_value_851"] */;
                /* block end [id:"CharacterCreation_block_276"] */ /* block comment [block:"CharacterCreation_block_277" text:"Calls the applyMethod method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_277" category:"My Blocks" label:"call apply method" shape:"command" docs:"Calls the applyMethod method using its declared arguments."] */
                applyMethod();
                /* block end [id:"CharacterCreation_block_277"] */ /* block comment [block:"CharacterCreation_block_278" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"CharacterCreation_block_278" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                phase = /* param start [id:"CharacterCreation_value_852" type:"number"] */2/* param end [id:"CharacterCreation_value_852"] */;
                /* block end [id:"CharacterCreation_block_278"] */ /* block comment [block:"CharacterCreation_block_279" text:"Calls the pushWatchers method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_279" category:"My Blocks" label:"call push watchers" shape:"command" docs:"Calls the pushWatchers method using its declared arguments."] */
                pushWatchers();
                /* block end [id:"CharacterCreation_block_279"] */ }
            /* block end [id:"CharacterCreation_block_30"] */
        }
        /* block end [id:"CharacterCreation_block_23"] */ else /* block comment [block:"CharacterCreation_block_31" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_31" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (phase == /* param start [id:"CharacterCreation_value_853" type:"number"] */2/* param end [id:"CharacterCreation_value_853"] */) {
            /* block comment [block:"CharacterCreation_block_32" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"CharacterCreation_block_32" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (edge(k1, prev1)) { /* block comment [block:"CharacterCreation_block_280" text:"Runs the click action. Values in this block are editable inputs."] */
                /* block start [id:"CharacterCreation_block_280" category:"Events" label:"click" shape:"command" docs:"Runs the click action. Values in this block are editable inputs."] */
                click();
                /* block end [id:"CharacterCreation_block_280"] */ /* block comment [block:"CharacterCreation_block_281" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"CharacterCreation_block_281" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                method = /* param start [id:"CharacterCreation_value_854" type:"number"] */0/* param end [id:"CharacterCreation_value_854"] */;
                /* block end [id:"CharacterCreation_block_281"] */ /* block comment [block:"CharacterCreation_block_282" text:"Calls the applyMethod method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_282" category:"My Blocks" label:"call apply method" shape:"command" docs:"Calls the applyMethod method using its declared arguments."] */
                applyMethod();
                /* block end [id:"CharacterCreation_block_282"] */ /* block comment [block:"CharacterCreation_block_283" text:"Calls the pushWatchers method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_283" category:"My Blocks" label:"call push watchers" shape:"command" docs:"Calls the pushWatchers method using its declared arguments."] */
                pushWatchers();
                /* block end [id:"CharacterCreation_block_283"] */ }
            /* block end [id:"CharacterCreation_block_32"] */
            /* block comment [block:"CharacterCreation_block_33" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"CharacterCreation_block_33" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (edge(k2, prev2)) { /* block comment [block:"CharacterCreation_block_284" text:"Runs the click action. Values in this block are editable inputs."] */
                /* block start [id:"CharacterCreation_block_284" category:"Events" label:"click" shape:"command" docs:"Runs the click action. Values in this block are editable inputs."] */
                click();
                /* block end [id:"CharacterCreation_block_284"] */ /* block comment [block:"CharacterCreation_block_285" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"CharacterCreation_block_285" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                method = /* param start [id:"CharacterCreation_value_855" type:"number"] */1/* param end [id:"CharacterCreation_value_855"] */;
                /* block end [id:"CharacterCreation_block_285"] */ /* block comment [block:"CharacterCreation_block_286" text:"Calls the applyMethod method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_286" category:"My Blocks" label:"call apply method" shape:"command" docs:"Calls the applyMethod method using its declared arguments."] */
                applyMethod();
                /* block end [id:"CharacterCreation_block_286"] */ /* block comment [block:"CharacterCreation_block_287" text:"Calls the pushWatchers method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_287" category:"My Blocks" label:"call push watchers" shape:"command" docs:"Calls the pushWatchers method using its declared arguments."] */
                pushWatchers();
                /* block end [id:"CharacterCreation_block_287"] */ }
            /* block end [id:"CharacterCreation_block_33"] */
            /* block comment [block:"CharacterCreation_block_34" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"CharacterCreation_block_34" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (edge(k3, prev3)) { /* block comment [block:"CharacterCreation_block_288" text:"Runs the click action. Values in this block are editable inputs."] */
                /* block start [id:"CharacterCreation_block_288" category:"Events" label:"click" shape:"command" docs:"Runs the click action. Values in this block are editable inputs."] */
                click();
                /* block end [id:"CharacterCreation_block_288"] */ /* block comment [block:"CharacterCreation_block_289" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"CharacterCreation_block_289" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                method = /* param start [id:"CharacterCreation_value_856" type:"number"] */2/* param end [id:"CharacterCreation_value_856"] */;
                /* block end [id:"CharacterCreation_block_289"] */ /* block comment [block:"CharacterCreation_block_290" text:"Calls the applyMethod method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_290" category:"My Blocks" label:"call apply method" shape:"command" docs:"Calls the applyMethod method using its declared arguments."] */
                applyMethod();
                /* block end [id:"CharacterCreation_block_290"] */ /* block comment [block:"CharacterCreation_block_291" text:"Calls the pushWatchers method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_291" category:"My Blocks" label:"call push watchers" shape:"command" docs:"Calls the pushWatchers method using its declared arguments."] */
                pushWatchers();
                /* block end [id:"CharacterCreation_block_291"] */ }
            /* block end [id:"CharacterCreation_block_34"] */
            /* block comment [block:"CharacterCreation_block_35" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"CharacterCreation_block_35" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (edge(r, prevR) && method == /* param start [id:"CharacterCreation_value_857" type:"number"] */1/* param end [id:"CharacterCreation_value_857"] */) { /* block comment [block:"CharacterCreation_block_292" text:"Runs the click action. Values in this block are editable inputs."] */
                /* block start [id:"CharacterCreation_block_292" category:"Events" label:"click" shape:"command" docs:"Runs the click action. Values in this block are editable inputs."] */
                click();
                /* block end [id:"CharacterCreation_block_292"] */ /* block comment [block:"CharacterCreation_block_293" text:"Runs the roll ability scores action. Values in this block are editable inputs."] */
                /* block start [id:"CharacterCreation_block_293" category:"My Blocks" label:"roll ability scores" shape:"command" docs:"Runs the roll ability scores action. Values in this block are editable inputs."] */
                scores = PlayerStats.rollAbilityScores();
                /* block end [id:"CharacterCreation_block_293"] */ /* block comment [block:"CharacterCreation_block_294" text:"Calls the pushWatchers method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_294" category:"My Blocks" label:"call push watchers" shape:"command" docs:"Calls the pushWatchers method using its declared arguments."] */
                pushWatchers();
                /* block end [id:"CharacterCreation_block_294"] */ }
            /* block end [id:"CharacterCreation_block_35"] */
            /* block comment [block:"CharacterCreation_block_36" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"CharacterCreation_block_36" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (method == /* param start [id:"CharacterCreation_value_858" type:"number"] */2/* param end [id:"CharacterCreation_value_858"] */) {
                /* block comment [block:"CharacterCreation_block_37" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"CharacterCreation_block_37" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (edge(up, prevUp)) { /* block comment [block:"CharacterCreation_block_295" text:"Evaluates this expression and applies its result."] */
                    /* block start [id:"CharacterCreation_block_295" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                    sel = (sel + /* param start [id:"CharacterCreation_value_859" type:"number"] */5/* param end [id:"CharacterCreation_value_859"] */) % /* param start [id:"CharacterCreation_value_860" type:"number"] */6/* param end [id:"CharacterCreation_value_860"] */;
                    /* block end [id:"CharacterCreation_block_295"] */ /* block comment [block:"CharacterCreation_block_296" text:"Calls the pushWatchers method using its declared arguments."] */
                    /* block start [id:"CharacterCreation_block_296" category:"My Blocks" label:"call push watchers" shape:"command" docs:"Calls the pushWatchers method using its declared arguments."] */
                    pushWatchers();
                    /* block end [id:"CharacterCreation_block_296"] */ }
                /* block end [id:"CharacterCreation_block_37"] */
                /* block comment [block:"CharacterCreation_block_38" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"CharacterCreation_block_38" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (edge(down, prevDown)) { /* block comment [block:"CharacterCreation_block_297" text:"Evaluates this expression and applies its result."] */
                    /* block start [id:"CharacterCreation_block_297" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                    sel = (sel + /* param start [id:"CharacterCreation_value_861" type:"number"] */1/* param end [id:"CharacterCreation_value_861"] */) % /* param start [id:"CharacterCreation_value_862" type:"number"] */6/* param end [id:"CharacterCreation_value_862"] */;
                    /* block end [id:"CharacterCreation_block_297"] */ /* block comment [block:"CharacterCreation_block_298" text:"Calls the pushWatchers method using its declared arguments."] */
                    /* block start [id:"CharacterCreation_block_298" category:"My Blocks" label:"call push watchers" shape:"command" docs:"Calls the pushWatchers method using its declared arguments."] */
                    pushWatchers();
                    /* block end [id:"CharacterCreation_block_298"] */ }
                /* block end [id:"CharacterCreation_block_38"] */
                /* block comment [block:"CharacterCreation_block_299" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"CharacterCreation_block_299" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"CharacterCreation_block_39" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"CharacterCreation_block_39" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (edge(left, prevLeft)) pbLower(sel);
                /* block end [id:"CharacterCreation_block_299"] */
                /* block end [id:"CharacterCreation_block_39"] */
                /* block comment [block:"CharacterCreation_block_300" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"CharacterCreation_block_300" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"CharacterCreation_block_40" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"CharacterCreation_block_40" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (edge(right, prevRight)) pbRaise(sel);
                /* block end [id:"CharacterCreation_block_300"] */
                /* block end [id:"CharacterCreation_block_40"] */
            }
            /* block end [id:"CharacterCreation_block_36"] */
            /* block comment [block:"CharacterCreation_block_41" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"CharacterCreation_block_41" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (edge(back, prevBack)) { /* block comment [block:"CharacterCreation_block_301" text:"Runs the click action. Values in this block are editable inputs."] */
                /* block start [id:"CharacterCreation_block_301" category:"Events" label:"click" shape:"command" docs:"Runs the click action. Values in this block are editable inputs."] */
                click();
                /* block end [id:"CharacterCreation_block_301"] */ /* block comment [block:"CharacterCreation_block_302" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"CharacterCreation_block_302" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                phase = /* param start [id:"CharacterCreation_value_863" type:"number"] */1/* param end [id:"CharacterCreation_value_863"] */;
                /* block end [id:"CharacterCreation_block_302"] */ /* block comment [block:"CharacterCreation_block_303" text:"Calls the pushWatchers method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_303" category:"My Blocks" label:"call push watchers" shape:"command" docs:"Calls the pushWatchers method using its declared arguments."] */
                pushWatchers();
                /* block end [id:"CharacterCreation_block_303"] */ }
            /* block end [id:"CharacterCreation_block_41"] */
            /* block comment [block:"CharacterCreation_block_42" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"CharacterCreation_block_42" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (edge(enter, prevEnter)) { /* block comment [block:"CharacterCreation_block_304" text:"Calls the finish method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_304" category:"My Blocks" label:"call finish" shape:"command" docs:"Calls the finish method using its declared arguments."] */
                finish();
                /* block end [id:"CharacterCreation_block_304"] */ }
            /* block end [id:"CharacterCreation_block_42"] */
        }
        /* block end [id:"CharacterCreation_block_31"] */
        /* block end [id:"CharacterCreation_block_25"] */
        /* block comment [block:"CharacterCreation_block_305" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_305" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        prevLeft = left;
        /* block end [id:"CharacterCreation_block_305"] */ /* block comment [block:"CharacterCreation_block_306" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_306" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        prevRight = right;
        /* block end [id:"CharacterCreation_block_306"] */ /* block comment [block:"CharacterCreation_block_307" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_307" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        prevEnter = enter;
        /* block end [id:"CharacterCreation_block_307"] */ /* block comment [block:"CharacterCreation_block_308" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_308" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        prevR = r;
        /* block end [id:"CharacterCreation_block_308"] */ /* block comment [block:"CharacterCreation_block_309" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_309" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        prevBack = back;
        /* block end [id:"CharacterCreation_block_309"] */
        /* block comment [block:"CharacterCreation_block_310" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_310" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        prev1 = k1;
        /* block end [id:"CharacterCreation_block_310"] */ /* block comment [block:"CharacterCreation_block_311" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_311" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        prev2 = k2;
        /* block end [id:"CharacterCreation_block_311"] */ /* block comment [block:"CharacterCreation_block_312" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_312" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        prev3 = k3;
        /* block end [id:"CharacterCreation_block_312"] */ /* block comment [block:"CharacterCreation_block_313" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_313" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        prevUp = up;
        /* block end [id:"CharacterCreation_block_313"] */ /* block comment [block:"CharacterCreation_block_314" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_314" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        prevDown = down;
        /* block end [id:"CharacterCreation_block_314"] */
        /* block comment [block:"CharacterCreation_block_315" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_315" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        prevH = /* param start [id:"CharacterCreation_value_864" type:"boolean"] */false/* param end [id:"CharacterCreation_value_864"] */;
        /* block end [id:"CharacterCreation_block_315"] */ // H edge detection is owned by Sprite1 during gameplay.
    }

    private void pollMouse() {
        /* block comment [block:"CharacterCreation_block_316" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_316" category:"Variables" label:"set stage" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage stage = Stage.getInstance();
        /* block end [id:"CharacterCreation_block_316"] */ /* block comment [block:"CharacterCreation_block_317" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_317" category:"Variables" label:"set w" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int w = stage.getWidth();
        /* block end [id:"CharacterCreation_block_317"] */ /* block comment [block:"CharacterCreation_block_318" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_318" category:"Variables" label:"set h" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int h = stage.getHeight();
        /* block end [id:"CharacterCreation_block_318"] */
        /* block comment [block:"CharacterCreation_block_319" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_319" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_43" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_43" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (w <= /* param start [id:"CharacterCreation_value_865" type:"number"] */0/* param end [id:"CharacterCreation_value_865"] */ || h <= /* param start [id:"CharacterCreation_value_866" type:"number"] */0/* param end [id:"CharacterCreation_value_866"] */) return;
        /* block end [id:"CharacterCreation_block_319"] */
        /* block end [id:"CharacterCreation_block_43"] */
        /* block comment [block:"CharacterCreation_block_320" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_320" category:"Variables" label:"set Btn" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        List<Btn> buttons = buttonsCache;
        /* block end [id:"CharacterCreation_block_320"] */ /* block comment [block:"CharacterCreation_block_321" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_321" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_44" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_44" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (buttons == /* param start [id:"CharacterCreation_value_867" type:"value"] */null/* param end [id:"CharacterCreation_value_867"] */) return;
        /* block end [id:"CharacterCreation_block_321"] */
        /* block end [id:"CharacterCreation_block_44"] */
        /* block comment [block:"CharacterCreation_block_322" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_322" category:"Variables" label:"set mx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int mx = stage.getMouseX() + w / /* param start [id:"CharacterCreation_value_868" type:"number"] */2/* param end [id:"CharacterCreation_value_868"] */;
        /* block end [id:"CharacterCreation_block_322"] */ /* block comment [block:"CharacterCreation_block_323" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_323" category:"Variables" label:"set my" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int my = h / /* param start [id:"CharacterCreation_value_869" type:"number"] */2/* param end [id:"CharacterCreation_value_869"] */ - stage.getMouseY();
        /* block end [id:"CharacterCreation_block_323"] */
        /* block comment [block:"CharacterCreation_block_324" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_324" category:"Variables" label:"set hover" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        String hover = /* param start [id:"CharacterCreation_value_870" type:"value"] */null/* param end [id:"CharacterCreation_value_870"] */;
        /* block end [id:"CharacterCreation_block_324"] */ /* block comment [block:"CharacterCreation_block_45" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"CharacterCreation_block_45" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (Btn b : buttons) /* block comment [block:"CharacterCreation_block_46" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_46" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (b.hits(mx, my)) { /* block comment [block:"CharacterCreation_block_325" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_325" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            hover = b.id;
            /* block end [id:"CharacterCreation_block_325"] */ /* block comment [block:"CharacterCreation_block_326" text:"Stops the nearest loop or switch block."] */
            /* block start [id:"CharacterCreation_block_326" category:"Control" label:"break loop" shape:"command" docs:"Stops the nearest loop or switch block."] */
            break;
            /* block end [id:"CharacterCreation_block_326"] */ }
        /* block end [id:"CharacterCreation_block_46"] */
        /* block end [id:"CharacterCreation_block_45"] */
        /* block comment [block:"CharacterCreation_block_327" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_327" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        hoverId = hover;
        /* block end [id:"CharacterCreation_block_327"] */
        /* block comment [block:"CharacterCreation_block_328" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_328" category:"Variables" label:"set md" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        boolean md = stage.isMouseDown();
        /* block end [id:"CharacterCreation_block_328"] */ /* block comment [block:"CharacterCreation_block_329" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_329" category:"Variables" label:"set pressed" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        boolean pressed = md && !prevMouse;
        /* block end [id:"CharacterCreation_block_329"] */ /* block comment [block:"CharacterCreation_block_330" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_330" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        prevMouse = md;
        /* block end [id:"CharacterCreation_block_330"] */
        /* block comment [block:"CharacterCreation_block_331" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_331" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_47" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_47" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!pressed) return;
        /* block end [id:"CharacterCreation_block_331"] */
        /* block end [id:"CharacterCreation_block_47"] */
        /* block comment [block:"CharacterCreation_block_48" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_48" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (hover == /* param start [id:"CharacterCreation_value_871" type:"value"] */null/* param end [id:"CharacterCreation_value_871"] */) { /* block comment [block:"CharacterCreation_block_49" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"CharacterCreation_block_49" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (nameFocused) { /* block comment [block:"CharacterCreation_block_332" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"CharacterCreation_block_332" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                nameFocused = /* param start [id:"CharacterCreation_value_872" type:"boolean"] */false/* param end [id:"CharacterCreation_value_872"] */;
                /* block end [id:"CharacterCreation_block_332"] */ /* block comment [block:"CharacterCreation_block_333" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"CharacterCreation_block_333" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                dirty = /* param start [id:"CharacterCreation_value_873" type:"boolean"] */true/* param end [id:"CharacterCreation_value_873"] */;
                /* block end [id:"CharacterCreation_block_333"] */ }
            /* block end [id:"CharacterCreation_block_49"] */ /* block comment [block:"CharacterCreation_block_334" text:"Returns a value or ends the current method."] */
            /* block start [id:"CharacterCreation_block_334" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"CharacterCreation_block_334"] */ }
        /* block end [id:"CharacterCreation_block_48"] */
        /* block comment [block:"CharacterCreation_block_50" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_50" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (GameState.isGameOver() && hover.equals(/* param start [id:"CharacterCreation_value_874" type:"text"] */"new_character"/* param end [id:"CharacterCreation_value_874"] */)) { /* block comment [block:"CharacterCreation_block_335" text:"Calls the startNewCharacter method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_335" category:"My Blocks" label:"call start new character" shape:"command" docs:"Calls the startNewCharacter method using its declared arguments."] */
            startNewCharacter();
            /* block end [id:"CharacterCreation_block_335"] */ /* block comment [block:"CharacterCreation_block_336" text:"Returns a value or ends the current method."] */
            /* block start [id:"CharacterCreation_block_336" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"CharacterCreation_block_336"] */ }
        /* block end [id:"CharacterCreation_block_50"] */
        /* block comment [block:"CharacterCreation_block_51" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_51" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (controlsShown && hover.equals(/* param start [id:"CharacterCreation_value_875" type:"text"] */"controls_close"/* param end [id:"CharacterCreation_value_875"] */)) { /* block comment [block:"CharacterCreation_block_337" text:"Calls the closeControls method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_337" category:"My Blocks" label:"call close controls" shape:"command" docs:"Calls the closeControls method using its declared arguments."] */
            closeControls();
            /* block end [id:"CharacterCreation_block_337"] */ /* block comment [block:"CharacterCreation_block_338" text:"Returns a value or ends the current method."] */
            /* block start [id:"CharacterCreation_block_338" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"CharacterCreation_block_338"] */ }
        /* block end [id:"CharacterCreation_block_51"] */
        /* block comment [block:"CharacterCreation_block_339" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_339" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_52" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_52" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (controlsShown || GameState.isGameOver()) return;
        /* block end [id:"CharacterCreation_block_339"] */
        /* block end [id:"CharacterCreation_block_52"] */
        /* block comment [block:"CharacterCreation_block_340" text:"Calls the activate method using its declared arguments."] */
        /* block start [id:"CharacterCreation_block_340" category:"My Blocks" label:"call activate" shape:"command" docs:"Calls the activate method using its declared arguments."] */
        activate(hover);
        /* block end [id:"CharacterCreation_block_340"] */
    }

    private void pollNameTyping() {
        /* block comment [block:"CharacterCreation_block_341" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_341" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_53" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_53" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (phase != /* param start [id:"CharacterCreation_value_876" type:"number"] */1/* param end [id:"CharacterCreation_value_876"] */ || !nameFocused) return;
        /* block end [id:"CharacterCreation_block_341"] */
        /* block end [id:"CharacterCreation_block_53"] */
        /* block comment [block:"CharacterCreation_block_342" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_342" category:"Variables" label:"set s" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage s = Stage.getInstance();
        /* block end [id:"CharacterCreation_block_342"] */ /* block comment [block:"CharacterCreation_block_343" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_343" category:"Variables" label:"set shift" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        boolean shift = s.isKeyPressed(KeyEvent.VK_SHIFT);
        /* block end [id:"CharacterCreation_block_343"] */ /* block comment [block:"CharacterCreation_block_344" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_344" category:"Variables" label:"set changed" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        boolean changed = /* param start [id:"CharacterCreation_value_877" type:"boolean"] */false/* param end [id:"CharacterCreation_value_877"] */;
        /* block end [id:"CharacterCreation_block_344"] */
        /* block comment [block:"CharacterCreation_block_54" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"CharacterCreation_block_54" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (int code = KeyEvent.VK_A; code <= KeyEvent.VK_Z; code++) {
            /* block comment [block:"CharacterCreation_block_345" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_345" category:"Variables" label:"set now" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            boolean now = s.isKeyPressed(code);
            /* block end [id:"CharacterCreation_block_345"] */
            /* block comment [block:"CharacterCreation_block_55" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"CharacterCreation_block_55" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (now && !prevType[code] && heroName.length() < NAME_MAX) {
                /* block comment [block:"CharacterCreation_block_346" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_346" category:"Variables" label:"set c" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                char c = (char) code;
                /* block end [id:"CharacterCreation_block_346"] */ /* block comment [block:"CharacterCreation_block_347" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"CharacterCreation_block_347" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"CharacterCreation_block_56" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"CharacterCreation_block_56" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (!shift) c = Character.toLowerCase(c);
                /* block end [id:"CharacterCreation_block_347"] */
                /* block end [id:"CharacterCreation_block_56"] */ /* block comment [block:"CharacterCreation_block_348" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"CharacterCreation_block_348" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"CharacterCreation_block_57" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"CharacterCreation_block_57" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (heroName.length() == /* param start [id:"CharacterCreation_value_878" type:"number"] */0/* param end [id:"CharacterCreation_value_878"] */) c = Character.toUpperCase(c);
                /* block end [id:"CharacterCreation_block_348"] */
                /* block end [id:"CharacterCreation_block_57"] */
                /* block comment [block:"CharacterCreation_block_349" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"CharacterCreation_block_349" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                heroName += c;
                /* block end [id:"CharacterCreation_block_349"] */ /* block comment [block:"CharacterCreation_block_350" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"CharacterCreation_block_350" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                changed = /* param start [id:"CharacterCreation_value_879" type:"boolean"] */true/* param end [id:"CharacterCreation_value_879"] */;
                /* block end [id:"CharacterCreation_block_350"] */
            }
            /* block end [id:"CharacterCreation_block_55"] */ /* block comment [block:"CharacterCreation_block_351" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_351" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            prevType[code] = now;
            /* block end [id:"CharacterCreation_block_351"] */
        }
        /* block end [id:"CharacterCreation_block_54"] */
        /* block comment [block:"CharacterCreation_block_58" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"CharacterCreation_block_58" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (int code = KeyEvent.VK_0; code <= KeyEvent.VK_9; code++) {
            /* block comment [block:"CharacterCreation_block_352" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_352" category:"Variables" label:"set now" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            boolean now = s.isKeyPressed(code);
            /* block end [id:"CharacterCreation_block_352"] */
            /* block comment [block:"CharacterCreation_block_59" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"CharacterCreation_block_59" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (now && !prevType[code] && heroName.length() > /* param start [id:"CharacterCreation_value_880" type:"number"] */0/* param end [id:"CharacterCreation_value_880"] */ && heroName.length() < NAME_MAX) { /* block comment [block:"CharacterCreation_block_353" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"CharacterCreation_block_353" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                heroName += (char) code;
                /* block end [id:"CharacterCreation_block_353"] */ /* block comment [block:"CharacterCreation_block_354" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"CharacterCreation_block_354" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                changed = /* param start [id:"CharacterCreation_value_881" type:"boolean"] */true/* param end [id:"CharacterCreation_value_881"] */;
                /* block end [id:"CharacterCreation_block_354"] */ }
            /* block end [id:"CharacterCreation_block_59"] */
            /* block comment [block:"CharacterCreation_block_355" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_355" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            prevType[code] = now;
            /* block end [id:"CharacterCreation_block_355"] */
        }
        /* block end [id:"CharacterCreation_block_58"] */
        /* block comment [block:"CharacterCreation_block_356" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_356" category:"Variables" label:"set space" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        boolean space = s.isKeyPressed(KeyEvent.VK_SPACE);
        /* block end [id:"CharacterCreation_block_356"] */
        /* block comment [block:"CharacterCreation_block_60" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_60" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (space && !prevType[KeyEvent.VK_SPACE] && heroName.length() > /* param start [id:"CharacterCreation_value_882" type:"number"] */0/* param end [id:"CharacterCreation_value_882"] */ && heroName.length() < NAME_MAX && !heroName.endsWith(/* param start [id:"CharacterCreation_value_883" type:"text"] */" "/* param end [id:"CharacterCreation_value_883"] */)) { /* block comment [block:"CharacterCreation_block_357" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_357" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            heroName += /* param start [id:"CharacterCreation_value_884" type:"text"] */" "/* param end [id:"CharacterCreation_value_884"] */;
            /* block end [id:"CharacterCreation_block_357"] */ /* block comment [block:"CharacterCreation_block_358" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_358" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            changed = /* param start [id:"CharacterCreation_value_885" type:"boolean"] */true/* param end [id:"CharacterCreation_value_885"] */;
            /* block end [id:"CharacterCreation_block_358"] */ }
        /* block end [id:"CharacterCreation_block_60"] */
        /* block comment [block:"CharacterCreation_block_359" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_359" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        prevType[KeyEvent.VK_SPACE] = space;
        /* block end [id:"CharacterCreation_block_359"] */
        /* block comment [block:"CharacterCreation_block_360" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_360" category:"Variables" label:"set minus" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        boolean minus = s.isKeyPressed(KeyEvent.VK_MINUS);
        /* block end [id:"CharacterCreation_block_360"] */
        /* block comment [block:"CharacterCreation_block_61" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_61" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (minus && !prevType[KeyEvent.VK_MINUS] && heroName.length() > /* param start [id:"CharacterCreation_value_886" type:"number"] */0/* param end [id:"CharacterCreation_value_886"] */ && heroName.length() < NAME_MAX) { /* block comment [block:"CharacterCreation_block_361" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_361" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            heroName += /* param start [id:"CharacterCreation_value_887" type:"text"] */"-"/* param end [id:"CharacterCreation_value_887"] */;
            /* block end [id:"CharacterCreation_block_361"] */ /* block comment [block:"CharacterCreation_block_362" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_362" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            changed = /* param start [id:"CharacterCreation_value_888" type:"boolean"] */true/* param end [id:"CharacterCreation_value_888"] */;
            /* block end [id:"CharacterCreation_block_362"] */ }
        /* block end [id:"CharacterCreation_block_61"] */
        /* block comment [block:"CharacterCreation_block_363" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_363" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        prevType[KeyEvent.VK_MINUS] = minus;
        /* block end [id:"CharacterCreation_block_363"] */
        /* block comment [block:"CharacterCreation_block_364" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_364" category:"Variables" label:"set back" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        boolean back = s.isKeyPressed(KeyEvent.VK_BACK_SPACE);
        /* block end [id:"CharacterCreation_block_364"] */
        /* block comment [block:"CharacterCreation_block_62" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_62" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (back && !prevType[KeyEvent.VK_BACK_SPACE] && heroName.length() > /* param start [id:"CharacterCreation_value_889" type:"number"] */0/* param end [id:"CharacterCreation_value_889"] */) { /* block comment [block:"CharacterCreation_block_365" text:"Runs the substring action. Values in this block are editable inputs."] */
            /* block start [id:"CharacterCreation_block_365" category:"My Blocks" label:"substring" shape:"command" docs:"Runs the substring action. Values in this block are editable inputs."] */
            heroName = heroName.substring(/* param start [id:"CharacterCreation_value_890" type:"number"] */0/* param end [id:"CharacterCreation_value_890"] */, heroName.length() - /* param start [id:"CharacterCreation_value_891" type:"number"] */1/* param end [id:"CharacterCreation_value_891"] */);
            /* block end [id:"CharacterCreation_block_365"] */ /* block comment [block:"CharacterCreation_block_366" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_366" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            changed = /* param start [id:"CharacterCreation_value_892" type:"boolean"] */true/* param end [id:"CharacterCreation_value_892"] */;
            /* block end [id:"CharacterCreation_block_366"] */ }
        /* block end [id:"CharacterCreation_block_62"] */
        /* block comment [block:"CharacterCreation_block_367" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_367" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        prevType[KeyEvent.VK_BACK_SPACE] = back;
        /* block end [id:"CharacterCreation_block_367"] */
        /* block comment [block:"CharacterCreation_block_368" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_368" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_63" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_63" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (changed) dirty = /* param start [id:"CharacterCreation_value_893" type:"boolean"] */true/* param end [id:"CharacterCreation_value_893"] */;
        /* block end [id:"CharacterCreation_block_368"] */
        /* block end [id:"CharacterCreation_block_63"] */
    }

    private void activate(String id) {
        /* block comment [block:"CharacterCreation_block_369" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_369" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_64" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_64" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (id == /* param start [id:"CharacterCreation_value_894" type:"value"] */null/* param end [id:"CharacterCreation_value_894"] */) return;
        /* block end [id:"CharacterCreation_block_369"] */
        /* block end [id:"CharacterCreation_block_64"] */
        /* block comment [block:"CharacterCreation_block_65" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_65" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (id.equals(/* param start [id:"CharacterCreation_value_895" type:"text"] */"begin_title"/* param end [id:"CharacterCreation_value_895"] */)) { /* block comment [block:"CharacterCreation_block_370" text:"Runs the click action. Values in this block are editable inputs."] */
            /* block start [id:"CharacterCreation_block_370" category:"Events" label:"click" shape:"command" docs:"Runs the click action. Values in this block are editable inputs."] */
            click();
            /* block end [id:"CharacterCreation_block_370"] */ /* block comment [block:"CharacterCreation_block_371" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_371" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            phase = /* param start [id:"CharacterCreation_value_896" type:"number"] */1/* param end [id:"CharacterCreation_value_896"] */;
            /* block end [id:"CharacterCreation_block_371"] */ /* block comment [block:"CharacterCreation_block_372" text:"Calls the pushWatchers method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_372" category:"My Blocks" label:"call push watchers" shape:"command" docs:"Calls the pushWatchers method using its declared arguments."] */
            pushWatchers();
            /* block end [id:"CharacterCreation_block_372"] */ /* block comment [block:"CharacterCreation_block_373" text:"Returns a value or ends the current method."] */
            /* block start [id:"CharacterCreation_block_373" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"CharacterCreation_block_373"] */ }
        /* block end [id:"CharacterCreation_block_65"] */
        /* block comment [block:"CharacterCreation_block_66" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_66" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (id.equals(/* param start [id:"CharacterCreation_value_897" type:"text"] */"controls_title"/* param end [id:"CharacterCreation_value_897"] */)) { /* block comment [block:"CharacterCreation_block_374" text:"Calls the openControls method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_374" category:"My Blocks" label:"call open controls" shape:"command" docs:"Calls the openControls method using its declared arguments."] */
            openControls();
            /* block end [id:"CharacterCreation_block_374"] */ /* block comment [block:"CharacterCreation_block_375" text:"Returns a value or ends the current method."] */
            /* block start [id:"CharacterCreation_block_375" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"CharacterCreation_block_375"] */ }
        /* block end [id:"CharacterCreation_block_66"] */
        /* block comment [block:"CharacterCreation_block_67" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_67" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (id.equals(/* param start [id:"CharacterCreation_value_898" type:"text"] */"controls_close"/* param end [id:"CharacterCreation_value_898"] */)) { /* block comment [block:"CharacterCreation_block_376" text:"Calls the closeControls method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_376" category:"My Blocks" label:"call close controls" shape:"command" docs:"Calls the closeControls method using its declared arguments."] */
            closeControls();
            /* block end [id:"CharacterCreation_block_376"] */ /* block comment [block:"CharacterCreation_block_377" text:"Returns a value or ends the current method."] */
            /* block start [id:"CharacterCreation_block_377" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"CharacterCreation_block_377"] */ }
        /* block end [id:"CharacterCreation_block_67"] */
        /* block comment [block:"CharacterCreation_block_68" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_68" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (id.equals(/* param start [id:"CharacterCreation_value_899" type:"text"] */"new_character"/* param end [id:"CharacterCreation_value_899"] */)) { /* block comment [block:"CharacterCreation_block_378" text:"Calls the startNewCharacter method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_378" category:"My Blocks" label:"call start new character" shape:"command" docs:"Calls the startNewCharacter method using its declared arguments."] */
            startNewCharacter();
            /* block end [id:"CharacterCreation_block_378"] */ /* block comment [block:"CharacterCreation_block_379" text:"Returns a value or ends the current method."] */
            /* block start [id:"CharacterCreation_block_379" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"CharacterCreation_block_379"] */ }
        /* block end [id:"CharacterCreation_block_68"] */
        /* block comment [block:"CharacterCreation_block_69" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_69" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (id.equals(/* param start [id:"CharacterCreation_value_900" type:"text"] */"prev"/* param end [id:"CharacterCreation_value_900"] */)) { /* block comment [block:"CharacterCreation_block_380" text:"Runs the click action. Values in this block are editable inputs."] */
            /* block start [id:"CharacterCreation_block_380" category:"Events" label:"click" shape:"command" docs:"Runs the click action. Values in this block are editable inputs."] */
            click();
            /* block end [id:"CharacterCreation_block_380"] */ /* block comment [block:"CharacterCreation_block_381" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_381" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            classIndex = (classIndex + CLASS_ORDER.length - /* param start [id:"CharacterCreation_value_901" type:"number"] */1/* param end [id:"CharacterCreation_value_901"] */) % CLASS_ORDER.length;
            /* block end [id:"CharacterCreation_block_381"] */ /* block comment [block:"CharacterCreation_block_382" text:"Calls the pushWatchers method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_382" category:"My Blocks" label:"call push watchers" shape:"command" docs:"Calls the pushWatchers method using its declared arguments."] */
            pushWatchers();
            /* block end [id:"CharacterCreation_block_382"] */ /* block comment [block:"CharacterCreation_block_383" text:"Returns a value or ends the current method."] */
            /* block start [id:"CharacterCreation_block_383" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"CharacterCreation_block_383"] */ }
        /* block end [id:"CharacterCreation_block_69"] */
        /* block comment [block:"CharacterCreation_block_70" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_70" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (id.equals(/* param start [id:"CharacterCreation_value_902" type:"text"] */"next"/* param end [id:"CharacterCreation_value_902"] */)) { /* block comment [block:"CharacterCreation_block_384" text:"Runs the click action. Values in this block are editable inputs."] */
            /* block start [id:"CharacterCreation_block_384" category:"Events" label:"click" shape:"command" docs:"Runs the click action. Values in this block are editable inputs."] */
            click();
            /* block end [id:"CharacterCreation_block_384"] */ /* block comment [block:"CharacterCreation_block_385" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_385" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            classIndex = (classIndex + /* param start [id:"CharacterCreation_value_903" type:"number"] */1/* param end [id:"CharacterCreation_value_903"] */) % CLASS_ORDER.length;
            /* block end [id:"CharacterCreation_block_385"] */ /* block comment [block:"CharacterCreation_block_386" text:"Calls the pushWatchers method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_386" category:"My Blocks" label:"call push watchers" shape:"command" docs:"Calls the pushWatchers method using its declared arguments."] */
            pushWatchers();
            /* block end [id:"CharacterCreation_block_386"] */ /* block comment [block:"CharacterCreation_block_387" text:"Returns a value or ends the current method."] */
            /* block start [id:"CharacterCreation_block_387" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"CharacterCreation_block_387"] */ }
        /* block end [id:"CharacterCreation_block_70"] */
        /* block comment [block:"CharacterCreation_block_71" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_71" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (id.equals(/* param start [id:"CharacterCreation_value_904" type:"text"] */"confirm"/* param end [id:"CharacterCreation_value_904"] */)) { /* block comment [block:"CharacterCreation_block_388" text:"Runs the click action. Values in this block are editable inputs."] */
            /* block start [id:"CharacterCreation_block_388" category:"Events" label:"click" shape:"command" docs:"Runs the click action. Values in this block are editable inputs."] */
            click();
            /* block end [id:"CharacterCreation_block_388"] */ /* block comment [block:"CharacterCreation_block_389" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_389" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            nameFocused = /* param start [id:"CharacterCreation_value_905" type:"boolean"] */false/* param end [id:"CharacterCreation_value_905"] */;
            /* block end [id:"CharacterCreation_block_389"] */ /* block comment [block:"CharacterCreation_block_390" text:"Calls the applyMethod method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_390" category:"My Blocks" label:"call apply method" shape:"command" docs:"Calls the applyMethod method using its declared arguments."] */
            applyMethod();
            /* block end [id:"CharacterCreation_block_390"] */ /* block comment [block:"CharacterCreation_block_391" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_391" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            phase = /* param start [id:"CharacterCreation_value_906" type:"number"] */2/* param end [id:"CharacterCreation_value_906"] */;
            /* block end [id:"CharacterCreation_block_391"] */ /* block comment [block:"CharacterCreation_block_392" text:"Calls the pushWatchers method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_392" category:"My Blocks" label:"call push watchers" shape:"command" docs:"Calls the pushWatchers method using its declared arguments."] */
            pushWatchers();
            /* block end [id:"CharacterCreation_block_392"] */ /* block comment [block:"CharacterCreation_block_393" text:"Returns a value or ends the current method."] */
            /* block start [id:"CharacterCreation_block_393" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"CharacterCreation_block_393"] */ }
        /* block end [id:"CharacterCreation_block_71"] */
        /* block comment [block:"CharacterCreation_block_72" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_72" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (id.equals(/* param start [id:"CharacterCreation_value_907" type:"text"] */"name"/* param end [id:"CharacterCreation_value_907"] */)) { /* block comment [block:"CharacterCreation_block_394" text:"Runs the click action. Values in this block are editable inputs."] */
            /* block start [id:"CharacterCreation_block_394" category:"Events" label:"click" shape:"command" docs:"Runs the click action. Values in this block are editable inputs."] */
            click();
            /* block end [id:"CharacterCreation_block_394"] */ /* block comment [block:"CharacterCreation_block_395" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_395" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            nameFocused = /* param start [id:"CharacterCreation_value_908" type:"boolean"] */true/* param end [id:"CharacterCreation_value_908"] */;
            /* block end [id:"CharacterCreation_block_395"] */ /* block comment [block:"CharacterCreation_block_396" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_396" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            dirty = /* param start [id:"CharacterCreation_value_909" type:"boolean"] */true/* param end [id:"CharacterCreation_value_909"] */;
            /* block end [id:"CharacterCreation_block_396"] */ /* block comment [block:"CharacterCreation_block_397" text:"Returns a value or ends the current method."] */
            /* block start [id:"CharacterCreation_block_397" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"CharacterCreation_block_397"] */ }
        /* block end [id:"CharacterCreation_block_72"] */
        /* block comment [block:"CharacterCreation_block_73" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_73" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (id.startsWith(/* param start [id:"CharacterCreation_value_910" type:"text"] */"class_"/* param end [id:"CharacterCreation_value_910"] */)) { /* block comment [block:"CharacterCreation_block_74" text:"Runs work safely and handles matching errors in a catch branch."] */
            /* block start [id:"CharacterCreation_block_74" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
            try { /* block comment [block:"CharacterCreation_block_398" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_398" category:"Variables" label:"set i" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int i = Integer.parseInt(id.substring(/* param start [id:"CharacterCreation_value_911" type:"number"] */6/* param end [id:"CharacterCreation_value_911"] */));
                /* block end [id:"CharacterCreation_block_398"] */ /* block comment [block:"CharacterCreation_block_75" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"CharacterCreation_block_75" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (i >= /* param start [id:"CharacterCreation_value_912" type:"number"] */0/* param end [id:"CharacterCreation_value_912"] */ && i < CLASS_ORDER.length && i != classIndex) { /* block comment [block:"CharacterCreation_block_399" text:"Runs the click action. Values in this block are editable inputs."] */
                    /* block start [id:"CharacterCreation_block_399" category:"Events" label:"click" shape:"command" docs:"Runs the click action. Values in this block are editable inputs."] */
                    click();
                    /* block end [id:"CharacterCreation_block_399"] */ /* block comment [block:"CharacterCreation_block_400" text:"Evaluates this expression and applies its result."] */
                    /* block start [id:"CharacterCreation_block_400" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                    classIndex = i;
                    /* block end [id:"CharacterCreation_block_400"] */ /* block comment [block:"CharacterCreation_block_401" text:"Calls the pushWatchers method using its declared arguments."] */
                    /* block start [id:"CharacterCreation_block_401" category:"My Blocks" label:"call push watchers" shape:"command" docs:"Calls the pushWatchers method using its declared arguments."] */
                    pushWatchers();
                    /* block end [id:"CharacterCreation_block_401"] */ }
                /* block end [id:"CharacterCreation_block_75"] */ }
            /* block end [id:"CharacterCreation_block_74"] */ catch (NumberFormatException ignored) {} /* block comment [block:"CharacterCreation_block_402" text:"Returns a value or ends the current method."] */
            /* block start [id:"CharacterCreation_block_402" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"CharacterCreation_block_402"] */ }
        /* block end [id:"CharacterCreation_block_73"] */
        /* block comment [block:"CharacterCreation_block_76" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_76" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (id.startsWith(/* param start [id:"CharacterCreation_value_913" type:"text"] */"tab_"/* param end [id:"CharacterCreation_value_913"] */)) { /* block comment [block:"CharacterCreation_block_77" text:"Runs work safely and handles matching errors in a catch branch."] */
            /* block start [id:"CharacterCreation_block_77" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
            try { /* block comment [block:"CharacterCreation_block_403" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_403" category:"Variables" label:"set m" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int m = Integer.parseInt(id.substring(/* param start [id:"CharacterCreation_value_914" type:"number"] */4/* param end [id:"CharacterCreation_value_914"] */));
                /* block end [id:"CharacterCreation_block_403"] */ /* block comment [block:"CharacterCreation_block_78" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"CharacterCreation_block_78" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (m >= /* param start [id:"CharacterCreation_value_915" type:"number"] */0/* param end [id:"CharacterCreation_value_915"] */ && m <= /* param start [id:"CharacterCreation_value_916" type:"number"] */2/* param end [id:"CharacterCreation_value_916"] */ && m != method) { /* block comment [block:"CharacterCreation_block_404" text:"Runs the click action. Values in this block are editable inputs."] */
                    /* block start [id:"CharacterCreation_block_404" category:"Events" label:"click" shape:"command" docs:"Runs the click action. Values in this block are editable inputs."] */
                    click();
                    /* block end [id:"CharacterCreation_block_404"] */ /* block comment [block:"CharacterCreation_block_405" text:"Evaluates this expression and applies its result."] */
                    /* block start [id:"CharacterCreation_block_405" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                    method = m;
                    /* block end [id:"CharacterCreation_block_405"] */ /* block comment [block:"CharacterCreation_block_406" text:"Calls the applyMethod method using its declared arguments."] */
                    /* block start [id:"CharacterCreation_block_406" category:"My Blocks" label:"call apply method" shape:"command" docs:"Calls the applyMethod method using its declared arguments."] */
                    applyMethod();
                    /* block end [id:"CharacterCreation_block_406"] */ /* block comment [block:"CharacterCreation_block_407" text:"Calls the pushWatchers method using its declared arguments."] */
                    /* block start [id:"CharacterCreation_block_407" category:"My Blocks" label:"call push watchers" shape:"command" docs:"Calls the pushWatchers method using its declared arguments."] */
                    pushWatchers();
                    /* block end [id:"CharacterCreation_block_407"] */ }
                /* block end [id:"CharacterCreation_block_78"] */ }
            /* block end [id:"CharacterCreation_block_77"] */ catch (NumberFormatException ignored) {} /* block comment [block:"CharacterCreation_block_408" text:"Returns a value or ends the current method."] */
            /* block start [id:"CharacterCreation_block_408" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"CharacterCreation_block_408"] */ }
        /* block end [id:"CharacterCreation_block_76"] */
        /* block comment [block:"CharacterCreation_block_79" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_79" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (id.equals(/* param start [id:"CharacterCreation_value_917" type:"text"] */"reroll"/* param end [id:"CharacterCreation_value_917"] */)) { /* block comment [block:"CharacterCreation_block_409" text:"Runs the click action. Values in this block are editable inputs."] */
            /* block start [id:"CharacterCreation_block_409" category:"Events" label:"click" shape:"command" docs:"Runs the click action. Values in this block are editable inputs."] */
            click();
            /* block end [id:"CharacterCreation_block_409"] */ /* block comment [block:"CharacterCreation_block_410" text:"Runs the roll ability scores action. Values in this block are editable inputs."] */
            /* block start [id:"CharacterCreation_block_410" category:"My Blocks" label:"roll ability scores" shape:"command" docs:"Runs the roll ability scores action. Values in this block are editable inputs."] */
            scores = PlayerStats.rollAbilityScores();
            /* block end [id:"CharacterCreation_block_410"] */ /* block comment [block:"CharacterCreation_block_411" text:"Calls the pushWatchers method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_411" category:"My Blocks" label:"call push watchers" shape:"command" docs:"Calls the pushWatchers method using its declared arguments."] */
            pushWatchers();
            /* block end [id:"CharacterCreation_block_411"] */ /* block comment [block:"CharacterCreation_block_412" text:"Returns a value or ends the current method."] */
            /* block start [id:"CharacterCreation_block_412" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"CharacterCreation_block_412"] */ }
        /* block end [id:"CharacterCreation_block_79"] */
        /* block comment [block:"CharacterCreation_block_80" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_80" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (id.startsWith(/* param start [id:"CharacterCreation_value_918" type:"text"] */"pb_minus_"/* param end [id:"CharacterCreation_value_918"] */)) { /* block comment [block:"CharacterCreation_block_81" text:"Runs work safely and handles matching errors in a catch branch."] */
            /* block start [id:"CharacterCreation_block_81" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
            try { /* block comment [block:"CharacterCreation_block_413" text:"Runs the click action. Values in this block are editable inputs."] */
                /* block start [id:"CharacterCreation_block_413" category:"Events" label:"click" shape:"command" docs:"Runs the click action. Values in this block are editable inputs."] */
                click();
                /* block end [id:"CharacterCreation_block_413"] */ /* block comment [block:"CharacterCreation_block_414" text:"Calls the pbLower method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_414" category:"My Blocks" label:"call pb lower" shape:"command" docs:"Calls the pbLower method using its declared arguments."] */
                pbLower(Integer.parseInt(id.substring(/* param start [id:"CharacterCreation_value_919" type:"number"] */9/* param end [id:"CharacterCreation_value_919"] */)));
                /* block end [id:"CharacterCreation_block_414"] */ }
            /* block end [id:"CharacterCreation_block_81"] */ catch (NumberFormatException ignored) {} /* block comment [block:"CharacterCreation_block_415" text:"Returns a value or ends the current method."] */
            /* block start [id:"CharacterCreation_block_415" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"CharacterCreation_block_415"] */ }
        /* block end [id:"CharacterCreation_block_80"] */
        /* block comment [block:"CharacterCreation_block_82" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_82" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (id.startsWith(/* param start [id:"CharacterCreation_value_920" type:"text"] */"pb_plus_"/* param end [id:"CharacterCreation_value_920"] */)) { /* block comment [block:"CharacterCreation_block_83" text:"Runs work safely and handles matching errors in a catch branch."] */
            /* block start [id:"CharacterCreation_block_83" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
            try { /* block comment [block:"CharacterCreation_block_416" text:"Runs the click action. Values in this block are editable inputs."] */
                /* block start [id:"CharacterCreation_block_416" category:"Events" label:"click" shape:"command" docs:"Runs the click action. Values in this block are editable inputs."] */
                click();
                /* block end [id:"CharacterCreation_block_416"] */ /* block comment [block:"CharacterCreation_block_417" text:"Calls the pbRaise method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_417" category:"My Blocks" label:"call pb raise" shape:"command" docs:"Calls the pbRaise method using its declared arguments."] */
                pbRaise(Integer.parseInt(id.substring(/* param start [id:"CharacterCreation_value_921" type:"number"] */8/* param end [id:"CharacterCreation_value_921"] */)));
                /* block end [id:"CharacterCreation_block_417"] */ }
            /* block end [id:"CharacterCreation_block_83"] */ catch (NumberFormatException ignored) {} /* block comment [block:"CharacterCreation_block_418" text:"Returns a value or ends the current method."] */
            /* block start [id:"CharacterCreation_block_418" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"CharacterCreation_block_418"] */ }
        /* block end [id:"CharacterCreation_block_82"] */
        /* block comment [block:"CharacterCreation_block_84" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_84" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (id.equals(/* param start [id:"CharacterCreation_value_922" type:"text"] */"back"/* param end [id:"CharacterCreation_value_922"] */)) { /* block comment [block:"CharacterCreation_block_419" text:"Runs the click action. Values in this block are editable inputs."] */
            /* block start [id:"CharacterCreation_block_419" category:"Events" label:"click" shape:"command" docs:"Runs the click action. Values in this block are editable inputs."] */
            click();
            /* block end [id:"CharacterCreation_block_419"] */ /* block comment [block:"CharacterCreation_block_420" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_420" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            phase = /* param start [id:"CharacterCreation_value_923" type:"number"] */1/* param end [id:"CharacterCreation_value_923"] */;
            /* block end [id:"CharacterCreation_block_420"] */ /* block comment [block:"CharacterCreation_block_421" text:"Calls the pushWatchers method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_421" category:"My Blocks" label:"call push watchers" shape:"command" docs:"Calls the pushWatchers method using its declared arguments."] */
            pushWatchers();
            /* block end [id:"CharacterCreation_block_421"] */ /* block comment [block:"CharacterCreation_block_422" text:"Returns a value or ends the current method."] */
            /* block start [id:"CharacterCreation_block_422" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"CharacterCreation_block_422"] */ }
        /* block end [id:"CharacterCreation_block_84"] */
        /* block comment [block:"CharacterCreation_block_85" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_85" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (id.equals(/* param start [id:"CharacterCreation_value_924" type:"text"] */"begin_game"/* param end [id:"CharacterCreation_value_924"] */)) { /* block comment [block:"CharacterCreation_block_423" text:"Calls the finish method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_423" category:"My Blocks" label:"call finish" shape:"command" docs:"Calls the finish method using its declared arguments."] */
            finish();
            /* block end [id:"CharacterCreation_block_423"] */ /* block comment [block:"CharacterCreation_block_424" text:"Returns a value or ends the current method."] */
            /* block start [id:"CharacterCreation_block_424" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"CharacterCreation_block_424"] */ }
        /* block end [id:"CharacterCreation_block_85"] */
    }

    private void applyMethod() {
        /* block comment [block:"CharacterCreation_block_425" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_425" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_86" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_86" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (method == /* param start [id:"CharacterCreation_value_925" type:"number"] */0/* param end [id:"CharacterCreation_value_925"] */) scores = PlayerStats.getStandardArray();
        /* block end [id:"CharacterCreation_block_425"] */
        /* block comment [block:"CharacterCreation_block_426" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_426" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        else /* block comment [block:"CharacterCreation_block_87" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_87" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (method == /* param start [id:"CharacterCreation_value_926" type:"number"] */1/* param end [id:"CharacterCreation_value_926"] */) scores = PlayerStats.rollAbilityScores();
        /* block end [id:"CharacterCreation_block_426"] */
        /* block end [id:"CharacterCreation_block_86"] */
        /* block comment [block:"CharacterCreation_block_427" text:"Runs the clone action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_427" category:"My Blocks" label:"clone" shape:"command" docs:"Runs the clone action. Values in this block are editable inputs."] */
        else scores = pb.clone();
        /* block end [id:"CharacterCreation_block_427"] */
        /* block end [id:"CharacterCreation_block_87"] */
    }

    public static int pbCost(int score) {
        /* block comment [block:"CharacterCreation_block_88" text:"Selects and runs the case that matches the input value."] */
        /* block start [id:"CharacterCreation_block_88" category:"Control" label:"choose a case" shape:"control" docs:"Selects and runs the case that matches the input value."] */
        switch (score) { /* block comment [block:"CharacterCreation_block_428" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_428" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            case /* param start [id:"CharacterCreation_value_927" type:"number"] */9/* param end [id:"CharacterCreation_value_927"] */: return /* param start [id:"CharacterCreation_value_928" type:"number"] */1/* param end [id:"CharacterCreation_value_928"] */;
            /* block end [id:"CharacterCreation_block_428"] */ /* block comment [block:"CharacterCreation_block_429" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_429" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            case /* param start [id:"CharacterCreation_value_929" type:"number"] */10/* param end [id:"CharacterCreation_value_929"] */: return /* param start [id:"CharacterCreation_value_930" type:"number"] */2/* param end [id:"CharacterCreation_value_930"] */;
            /* block end [id:"CharacterCreation_block_429"] */ /* block comment [block:"CharacterCreation_block_430" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_430" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            case /* param start [id:"CharacterCreation_value_931" type:"number"] */11/* param end [id:"CharacterCreation_value_931"] */: return /* param start [id:"CharacterCreation_value_932" type:"number"] */3/* param end [id:"CharacterCreation_value_932"] */;
            /* block end [id:"CharacterCreation_block_430"] */ /* block comment [block:"CharacterCreation_block_431" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_431" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            case /* param start [id:"CharacterCreation_value_933" type:"number"] */12/* param end [id:"CharacterCreation_value_933"] */: return /* param start [id:"CharacterCreation_value_934" type:"number"] */4/* param end [id:"CharacterCreation_value_934"] */;
            /* block end [id:"CharacterCreation_block_431"] */ /* block comment [block:"CharacterCreation_block_432" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_432" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            case /* param start [id:"CharacterCreation_value_935" type:"number"] */13/* param end [id:"CharacterCreation_value_935"] */: return /* param start [id:"CharacterCreation_value_936" type:"number"] */5/* param end [id:"CharacterCreation_value_936"] */;
            /* block end [id:"CharacterCreation_block_432"] */ /* block comment [block:"CharacterCreation_block_433" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_433" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            case /* param start [id:"CharacterCreation_value_937" type:"number"] */14/* param end [id:"CharacterCreation_value_937"] */: return /* param start [id:"CharacterCreation_value_938" type:"number"] */7/* param end [id:"CharacterCreation_value_938"] */;
            /* block end [id:"CharacterCreation_block_433"] */ /* block comment [block:"CharacterCreation_block_434" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_434" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            case /* param start [id:"CharacterCreation_value_939" type:"number"] */15/* param end [id:"CharacterCreation_value_939"] */: return /* param start [id:"CharacterCreation_value_940" type:"number"] */9/* param end [id:"CharacterCreation_value_940"] */;
            /* block end [id:"CharacterCreation_block_434"] */ /* block comment [block:"CharacterCreation_block_435" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_435" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            default: return /* param start [id:"CharacterCreation_value_941" type:"number"] */0/* param end [id:"CharacterCreation_value_941"] */;
            /* block end [id:"CharacterCreation_block_435"] */ }
        /* block end [id:"CharacterCreation_block_88"] */
    }
    public static int pbSpent(int[] a) { /* block comment [block:"CharacterCreation_block_436" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_436" category:"Variables" label:"set t" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int t = /* param start [id:"CharacterCreation_value_942" type:"number"] */0/* param end [id:"CharacterCreation_value_942"] */;
        /* block end [id:"CharacterCreation_block_436"] */ /* block comment [block:"CharacterCreation_block_437" text:"Runs the for action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_437" category:"My Blocks" label:"for" shape:"command" docs:"Runs the for action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_89" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"CharacterCreation_block_89" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (int s : a) t += pbCost(s);
        /* block end [id:"CharacterCreation_block_437"] */
        /* block end [id:"CharacterCreation_block_89"] */ /* block comment [block:"CharacterCreation_block_438" text:"Returns a value or ends the current method."] */
        /* block start [id:"CharacterCreation_block_438" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return t;
        /* block end [id:"CharacterCreation_block_438"] */ }
    public int pbRemaining() { /* block comment [block:"CharacterCreation_block_439" text:"Returns a value or ends the current method."] */
        /* block start [id:"CharacterCreation_block_439" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return PB_BUDGET - pbSpent(pb);
        /* block end [id:"CharacterCreation_block_439"] */ }
    public boolean pbRaise(int i) {
        /* block comment [block:"CharacterCreation_block_440" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_440" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_90" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_90" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (i < /* param start [id:"CharacterCreation_value_943" type:"number"] */0/* param end [id:"CharacterCreation_value_943"] */ || i > /* param start [id:"CharacterCreation_value_944" type:"number"] */5/* param end [id:"CharacterCreation_value_944"] */ || pb[i] >= PB_MAX) return /* param start [id:"CharacterCreation_value_945" type:"boolean"] */false/* param end [id:"CharacterCreation_value_945"] */;
        /* block end [id:"CharacterCreation_block_440"] */
        /* block end [id:"CharacterCreation_block_90"] */
        /* block comment [block:"CharacterCreation_block_441" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_441" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_91" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_91" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (pbSpent(pb) + (pbCost(pb[i] + /* param start [id:"CharacterCreation_value_946" type:"number"] */1/* param end [id:"CharacterCreation_value_946"] */) - pbCost(pb[i])) > PB_BUDGET) return /* param start [id:"CharacterCreation_value_947" type:"boolean"] */false/* param end [id:"CharacterCreation_value_947"] */;
        /* block end [id:"CharacterCreation_block_441"] */
        /* block end [id:"CharacterCreation_block_91"] */
        /* block comment [block:"CharacterCreation_block_442" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_442" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        pb[i]++;
        /* block end [id:"CharacterCreation_block_442"] */ /* block comment [block:"CharacterCreation_block_443" text:"Runs the clone action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_443" category:"My Blocks" label:"clone" shape:"command" docs:"Runs the clone action. Values in this block are editable inputs."] */
        scores = pb.clone();
        /* block end [id:"CharacterCreation_block_443"] */ /* block comment [block:"CharacterCreation_block_444" text:"Calls the pushWatchers method using its declared arguments."] */
        /* block start [id:"CharacterCreation_block_444" category:"My Blocks" label:"call push watchers" shape:"command" docs:"Calls the pushWatchers method using its declared arguments."] */
        pushWatchers();
        /* block end [id:"CharacterCreation_block_444"] */ /* block comment [block:"CharacterCreation_block_445" text:"Returns a value or ends the current method."] */
        /* block start [id:"CharacterCreation_block_445" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return /* param start [id:"CharacterCreation_value_948" type:"boolean"] */true/* param end [id:"CharacterCreation_value_948"] */;
        /* block end [id:"CharacterCreation_block_445"] */
    }
    public boolean pbLower(int i) {
        /* block comment [block:"CharacterCreation_block_446" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_446" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_92" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_92" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (i < /* param start [id:"CharacterCreation_value_949" type:"number"] */0/* param end [id:"CharacterCreation_value_949"] */ || i > /* param start [id:"CharacterCreation_value_950" type:"number"] */5/* param end [id:"CharacterCreation_value_950"] */ || pb[i] <= PB_MIN) return /* param start [id:"CharacterCreation_value_951" type:"boolean"] */false/* param end [id:"CharacterCreation_value_951"] */;
        /* block end [id:"CharacterCreation_block_446"] */
        /* block end [id:"CharacterCreation_block_92"] */
        /* block comment [block:"CharacterCreation_block_447" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_447" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        pb[i]--;
        /* block end [id:"CharacterCreation_block_447"] */ /* block comment [block:"CharacterCreation_block_448" text:"Runs the clone action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_448" category:"My Blocks" label:"clone" shape:"command" docs:"Runs the clone action. Values in this block are editable inputs."] */
        scores = pb.clone();
        /* block end [id:"CharacterCreation_block_448"] */ /* block comment [block:"CharacterCreation_block_449" text:"Calls the pushWatchers method using its declared arguments."] */
        /* block start [id:"CharacterCreation_block_449" category:"My Blocks" label:"call push watchers" shape:"command" docs:"Calls the pushWatchers method using its declared arguments."] */
        pushWatchers();
        /* block end [id:"CharacterCreation_block_449"] */ /* block comment [block:"CharacterCreation_block_450" text:"Returns a value or ends the current method."] */
        /* block start [id:"CharacterCreation_block_450" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return /* param start [id:"CharacterCreation_value_952" type:"boolean"] */true/* param end [id:"CharacterCreation_value_952"] */;
        /* block end [id:"CharacterCreation_block_450"] */
    }

    /** Reset run-owned systems then return to the familiar character flow. */
    private void startNewCharacter() {
        /* block comment [block:"CharacterCreation_block_451" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_451" category:"Variables" label:"set setGameOver" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        GameState.setGameOver(/* param start [id:"CharacterCreation_value_953" type:"boolean"] */false/* param end [id:"CharacterCreation_value_953"] */);
        /* block end [id:"CharacterCreation_block_451"] */
        /* block comment [block:"CharacterCreation_block_452" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_452" category:"Variables" label:"set setControlsVisible" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        GameState.setControlsVisible(/* param start [id:"CharacterCreation_value_954" type:"boolean"] */false/* param end [id:"CharacterCreation_value_954"] */);
        /* block end [id:"CharacterCreation_block_452"] */
        /* block comment [block:"CharacterCreation_block_453" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_453" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        controlsShown = /* param start [id:"CharacterCreation_value_955" type:"boolean"] */false/* param end [id:"CharacterCreation_value_955"] */;
        /* block end [id:"CharacterCreation_block_453"] */
        // Invalidate the previous driver before the spawn callback can add a fresh one.
        /* block comment [block:"CharacterCreation_block_454" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_454" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_93" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_93" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (Sprite1.instance != /* param start [id:"CharacterCreation_value_956" type:"value"] */null/* param end [id:"CharacterCreation_value_956"] */) Sprite1.instance.stopGameLoop();
        /* block end [id:"CharacterCreation_block_454"] */
        /* block end [id:"CharacterCreation_block_93"] */
        /* block comment [block:"CharacterCreation_block_455" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_455" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().broadcast(/* param start [id:"CharacterCreation_value_957" type:"text"] */"newCharacterRun"/* param end [id:"CharacterCreation_value_957"] */);
        /* block end [id:"CharacterCreation_block_455"] */
        /* block comment [block:"CharacterCreation_block_456" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_456" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        phase = /* param start [id:"CharacterCreation_value_958" type:"number"] */0/* param end [id:"CharacterCreation_value_958"] */;
        /* block end [id:"CharacterCreation_block_456"] */
        /* block comment [block:"CharacterCreation_block_457" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_457" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        nameFocused = /* param start [id:"CharacterCreation_value_959" type:"boolean"] */false/* param end [id:"CharacterCreation_value_959"] */;
        /* block end [id:"CharacterCreation_block_457"] */
        /* block comment [block:"CharacterCreation_block_458" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_458" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        heroName = /* param start [id:"CharacterCreation_value_960" type:"text"] */"Hero"/* param end [id:"CharacterCreation_value_960"] */;
        /* block end [id:"CharacterCreation_block_458"] */
        /* block comment [block:"CharacterCreation_block_459" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_459" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        classIndex = /* param start [id:"CharacterCreation_value_961" type:"number"] */5/* param end [id:"CharacterCreation_value_961"] */;
        /* block end [id:"CharacterCreation_block_459"] */
        /* block comment [block:"CharacterCreation_block_460" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_460" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        method = /* param start [id:"CharacterCreation_value_962" type:"number"] */0/* param end [id:"CharacterCreation_value_962"] */;
        /* block end [id:"CharacterCreation_block_460"] */
        /* block comment [block:"CharacterCreation_block_461" text:"Runs the get standard array action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_461" category:"My Blocks" label:"get standard array" shape:"command" docs:"Runs the get standard array action. Values in this block are editable inputs."] */
        scores = PlayerStats.getStandardArray();
        /* block end [id:"CharacterCreation_block_461"] */
        pb = new int[]{8, 8, 8, 8, 8, 8};
        /* block comment [block:"CharacterCreation_block_462" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_462" category:"Variables" label:"set bag" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Inventory.bag.clear();
        /* block end [id:"CharacterCreation_block_462"] */
        /* block comment [block:"CharacterCreation_block_463" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_463" category:"Variables" label:"set equipped" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Inventory.equipped.clear();
        /* block end [id:"CharacterCreation_block_463"] */
        /* block comment [block:"CharacterCreation_block_464" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_464" category:"Variables" label:"set gold" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Inventory.gold = /* param start [id:"CharacterCreation_value_963" type:"number"] */0/* param end [id:"CharacterCreation_value_963"] */;
        /* block end [id:"CharacterCreation_block_464"] */
        /* block comment [block:"CharacterCreation_block_465" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_465" category:"Variables" label:"set recalc" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Inventory.recalc();
        /* block end [id:"CharacterCreation_block_465"] */
        /* block comment [block:"CharacterCreation_block_466" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_466" category:"Variables" label:"set version" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Inventory.version++;
        /* block end [id:"CharacterCreation_block_466"] */
        /* block comment [block:"CharacterCreation_block_467" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_467" category:"Variables" label:"set ready" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PlayerStats.ready = /* param start [id:"CharacterCreation_value_964" type:"boolean"] */false/* param end [id:"CharacterCreation_value_964"] */;
        /* block end [id:"CharacterCreation_block_467"] */
        /* block comment [block:"CharacterCreation_block_468" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_468" category:"Variables" label:"set stats" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PlayerStats.stats = /* param start [id:"CharacterCreation_value_965" type:"value"] */null/* param end [id:"CharacterCreation_value_965"] */;
        /* block end [id:"CharacterCreation_block_468"] */
        /* block comment [block:"CharacterCreation_block_469" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_469" category:"Variables" label:"set heroName" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PlayerStats.heroName = /* param start [id:"CharacterCreation_value_966" type:"text"] */"Hero"/* param end [id:"CharacterCreation_value_966"] */;
        /* block end [id:"CharacterCreation_block_469"] */
        /* block comment [block:"CharacterCreation_block_470" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_470" category:"Variables" label:"set mana" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PlayerStats.mana = PlayerStats.maxMana = /* param start [id:"CharacterCreation_value_967" type:"number"] */10/* param end [id:"CharacterCreation_value_967"] */;
        /* block end [id:"CharacterCreation_block_470"] */
        /* block comment [block:"CharacterCreation_block_471" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_471" category:"Variables" label:"set bonusMelee" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PlayerStats.bonusMelee = PlayerStats.bonusDmg = PlayerStats.bonusAC = PlayerStats.bonusSpell = /* param start [id:"CharacterCreation_value_968" type:"number"] */0/* param end [id:"CharacterCreation_value_968"] */;
        /* block end [id:"CharacterCreation_block_471"] */
        /* block comment [block:"CharacterCreation_block_472" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_472" category:"Variables" label:"set instance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Sprite1.instance = /* param start [id:"CharacterCreation_value_969" type:"value"] */null/* param end [id:"CharacterCreation_value_969"] */;
        /* block end [id:"CharacterCreation_block_472"] */
        /* block comment [block:"CharacterCreation_block_94" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"CharacterCreation_block_94" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try {
            /* block comment [block:"CharacterCreation_block_473" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_473" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            EnemyManager.getInstance().clearAll();
            /* block end [id:"CharacterCreation_block_473"] */
            /* block comment [block:"CharacterCreation_block_474" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_474" category:"Variables" label:"set clearAll" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            LootDrop.clearAll();
            /* block end [id:"CharacterCreation_block_474"] */
            /* block comment [block:"CharacterCreation_block_475" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_475" category:"Variables" label:"set clearSummons" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Spellbook.clearSummons();
            /* block end [id:"CharacterCreation_block_475"] */
            /* block comment [block:"CharacterCreation_block_476" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_476" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            FloorManager.getInstance().resetForNewRun();
            /* block end [id:"CharacterCreation_block_476"] */
        }
        /* block end [id:"CharacterCreation_block_94"] */ catch (Exception ignored) { }
        /* block comment [block:"CharacterCreation_block_477" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_477" category:"Variables" label:"set toCharacterCreate" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        GameState.toCharacterCreate();
        /* block end [id:"CharacterCreation_block_477"] */
        /* block comment [block:"CharacterCreation_block_478" text:"Runs the show action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_478" category:"Looks" label:"show" shape:"command" docs:"Runs the show action. Values in this block are editable inputs."] */
        show();
        /* block end [id:"CharacterCreation_block_478"] */
        /* block comment [block:"CharacterCreation_block_479" text:"Calls the pushWatchers method using its declared arguments."] */
        /* block start [id:"CharacterCreation_block_479" category:"My Blocks" label:"call push watchers" shape:"command" docs:"Calls the pushWatchers method using its declared arguments."] */
        pushWatchers();
        /* block end [id:"CharacterCreation_block_479"] */
        /* block comment [block:"CharacterCreation_block_95" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"CharacterCreation_block_95" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try { /* block comment [block:"CharacterCreation_block_480" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_480" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Stage.getInstance().goToFront(this);
            /* block end [id:"CharacterCreation_block_480"] */ }
        /* block end [id:"CharacterCreation_block_95"] */ catch (Exception ignored) {}
    }

    private void finish() {
        // This menu object survives runs: clear key-edge state so held keys from
        // the death panel/title cannot accidentally fire on the new character.
        /* block comment [block:"CharacterCreation_block_481" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_481" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        prevLeft = prevRight = prevEnter = prevR = prevBack = /* param start [id:"CharacterCreation_value_970" type:"boolean"] */false/* param end [id:"CharacterCreation_value_970"] */;
        /* block end [id:"CharacterCreation_block_481"] */
        /* block comment [block:"CharacterCreation_block_482" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_482" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        prev1 = prev2 = prev3 = prevUp = prevDown = prevM = prevEsc = /* param start [id:"CharacterCreation_value_971" type:"boolean"] */false/* param end [id:"CharacterCreation_value_971"] */;
        /* block end [id:"CharacterCreation_block_482"] */
        /* block comment [block:"CharacterCreation_block_483" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_483" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        prevMouse = /* param start [id:"CharacterCreation_value_972" type:"boolean"] */false/* param end [id:"CharacterCreation_value_972"] */;
        /* block end [id:"CharacterCreation_block_483"] */
        /* block comment [block:"CharacterCreation_block_484" text:"Calls the fill method using its declared arguments."] */
        /* block start [id:"CharacterCreation_block_484" category:"My Blocks" label:"call fill" shape:"command" docs:"Calls the fill method using its declared arguments."] */
        java.util.Arrays.fill(prevType, /* param start [id:"CharacterCreation_value_973" type:"boolean"] */false/* param end [id:"CharacterCreation_value_973"] */);
        /* block end [id:"CharacterCreation_block_484"] */
        /* block comment [block:"CharacterCreation_block_485" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_485" category:"Variables" label:"set copy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int[] copy = new int[/* param start [id:"CharacterCreation_value_974" type:"number"] */6/* param end [id:"CharacterCreation_value_974"] */];
        /* block end [id:"CharacterCreation_block_485"] */ /* block comment [block:"CharacterCreation_block_96" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"CharacterCreation_block_96" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (int i = /* param start [id:"CharacterCreation_value_975" type:"number"] */0/* param end [id:"CharacterCreation_value_975"] */; i < /* param start [id:"CharacterCreation_value_976" type:"number"] */6/* param end [id:"CharacterCreation_value_976"] */; i++) copy[i] = scores[i];
        /* block end [id:"CharacterCreation_block_96"] */
        /* block comment [block:"CharacterCreation_block_486" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_486" category:"Variables" label:"set name" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        String name = heroName.trim().length() == /* param start [id:"CharacterCreation_value_977" type:"number"] */0/* param end [id:"CharacterCreation_value_977"] */ ? /* param start [id:"CharacterCreation_value_978" type:"text"] */"Hero"/* param end [id:"CharacterCreation_value_978"] */ : heroName.trim();
        /* block end [id:"CharacterCreation_block_486"] */
        /* block comment [block:"CharacterCreation_block_487" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_487" category:"Variables" label:"set init" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        PlayerStats.init(name, CLASS_ORDER[classIndex], copy);
        /* block end [id:"CharacterCreation_block_487"] */
        /* block comment [block:"CharacterCreation_block_488" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_488" category:"Variables" label:"set grantStarter" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Inventory.grantStarter(CLASS_ORDER[classIndex]);
        /* block end [id:"CharacterCreation_block_488"] */
        /* block comment [block:"CharacterCreation_block_489" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_489" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        phase = /* param start [id:"CharacterCreation_value_979" type:"number"] */3/* param end [id:"CharacterCreation_value_979"] */;
        /* block end [id:"CharacterCreation_block_489"] */ /* block comment [block:"CharacterCreation_block_490" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_490" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        nameFocused = /* param start [id:"CharacterCreation_value_980" type:"boolean"] */false/* param end [id:"CharacterCreation_value_980"] */;
        /* block end [id:"CharacterCreation_block_490"] */ /* block comment [block:"CharacterCreation_block_491" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_491" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        hoverId = /* param start [id:"CharacterCreation_value_981" type:"value"] */null/* param end [id:"CharacterCreation_value_981"] */;
        /* block end [id:"CharacterCreation_block_491"] */
        /* block comment [block:"CharacterCreation_block_492" text:"Calls the clearMenuWatchers method using its declared arguments."] */
        /* block start [id:"CharacterCreation_block_492" category:"My Blocks" label:"call clear menu watchers" shape:"command" docs:"Calls the clearMenuWatchers method using its declared arguments."] */
        clearMenuWatchers();
        /* block end [id:"CharacterCreation_block_492"] */ /* block comment [block:"CharacterCreation_block_493" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_493" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        dirty = /* param start [id:"CharacterCreation_value_982" type:"boolean"] */true/* param end [id:"CharacterCreation_value_982"] */;
        /* block end [id:"CharacterCreation_block_493"] */ /* block comment [block:"CharacterCreation_block_494" text:"Runs the hide action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_494" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
        hide();
        /* block end [id:"CharacterCreation_block_494"] */ /* block comment [block:"CharacterCreation_block_495" text:"Runs the click action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_495" category:"Events" label:"click" shape:"command" docs:"Runs the click action. Values in this block are editable inputs."] */
        click();
        /* block end [id:"CharacterCreation_block_495"] */ /* block comment [block:"CharacterCreation_block_496" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_496" category:"Variables" label:"set toGameplay" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        GameState.toGameplay();
        /* block end [id:"CharacterCreation_block_496"] */ /* block comment [block:"CharacterCreation_block_497" text:"Runs the broadcast action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_497" category:"Events" label:"broadcast" shape:"command" docs:"Runs the broadcast action. Values in this block are editable inputs."] */
        broadcast(/* param start [id:"CharacterCreation_value_983" type:"text"] */"characterReady"/* param end [id:"CharacterCreation_value_983"] */);
        /* block end [id:"CharacterCreation_block_497"] */
    }

    private void clearMenuWatchers() {
        /* block comment [block:"CharacterCreation_block_97" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"CharacterCreation_block_97" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try { /* block comment [block:"CharacterCreation_block_498" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_498" category:"Variables" label:"set s" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Stage s = Stage.getInstance();
            /* block end [id:"CharacterCreation_block_498"] */ /* block comment [block:"CharacterCreation_block_499" text:"Runs the remove variable action. Values in this block are editable inputs."] */
            /* block start [id:"CharacterCreation_block_499" category:"Motion" label:"remove variable" shape:"command" docs:"Runs the remove variable action. Values in this block are editable inputs."] */
            s.removeVariable(/* param start [id:"CharacterCreation_value_984" type:"text"] */"MenuPhase"/* param end [id:"CharacterCreation_value_984"] */);
            /* block end [id:"CharacterCreation_block_499"] */ /* block comment [block:"CharacterCreation_block_500" text:"Runs the remove variable action. Values in this block are editable inputs."] */
            /* block start [id:"CharacterCreation_block_500" category:"Motion" label:"remove variable" shape:"command" docs:"Runs the remove variable action. Values in this block are editable inputs."] */
            s.removeVariable(/* param start [id:"CharacterCreation_value_985" type:"text"] */"Class"/* param end [id:"CharacterCreation_value_985"] */);
            /* block end [id:"CharacterCreation_block_500"] */ /* block comment [block:"CharacterCreation_block_501" text:"Runs the remove variable action. Values in this block are editable inputs."] */
            /* block start [id:"CharacterCreation_block_501" category:"Motion" label:"remove variable" shape:"command" docs:"Runs the remove variable action. Values in this block are editable inputs."] */
            s.removeVariable(/* param start [id:"CharacterCreation_value_986" type:"text"] */"StatMethod"/* param end [id:"CharacterCreation_value_986"] */);
            /* block end [id:"CharacterCreation_block_501"] */ /* block comment [block:"CharacterCreation_block_502" text:"Runs the remove variable action. Values in this block are editable inputs."] */
            /* block start [id:"CharacterCreation_block_502" category:"Motion" label:"remove variable" shape:"command" docs:"Runs the remove variable action. Values in this block are editable inputs."] */
            s.removeVariable(/* param start [id:"CharacterCreation_value_987" type:"text"] */"PB"/* param end [id:"CharacterCreation_value_987"] */);
            /* block end [id:"CharacterCreation_block_502"] */ }
        /* block end [id:"CharacterCreation_block_97"] */ catch (Exception ignored) {}
    }
    private void pushWatchers() {
        /* block comment [block:"CharacterCreation_block_503" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_503" category:"Variables" label:"set s" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage s = Stage.getInstance();
        /* block end [id:"CharacterCreation_block_503"] */
        /* block comment [block:"CharacterCreation_block_504" text:"Runs the set variable action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_504" category:"Variables" label:"set variable" shape:"command" docs:"Runs the set variable action. Values in this block are editable inputs."] */
        s.setVariable(/* param start [id:"CharacterCreation_value_988" type:"text"] */"MenuPhase"/* param end [id:"CharacterCreation_value_988"] */, phase == /* param start [id:"CharacterCreation_value_989" type:"number"] */0/* param end [id:"CharacterCreation_value_989"] */ ? /* param start [id:"CharacterCreation_value_990" type:"text"] */"Title"/* param end [id:"CharacterCreation_value_990"] */ : phase == /* param start [id:"CharacterCreation_value_991" type:"number"] */1/* param end [id:"CharacterCreation_value_991"] */ ? /* param start [id:"CharacterCreation_value_992" type:"text"] */"Class"/* param end [id:"CharacterCreation_value_992"] */ : /* param start [id:"CharacterCreation_value_993" type:"text"] */"Stats"/* param end [id:"CharacterCreation_value_993"] */);
        /* block end [id:"CharacterCreation_block_504"] */
        /* block comment [block:"CharacterCreation_block_505" text:"Runs the set variable action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_505" category:"Variables" label:"set variable" shape:"command" docs:"Runs the set variable action. Values in this block are editable inputs."] */
        s.setVariable(/* param start [id:"CharacterCreation_value_994" type:"text"] */"Class"/* param end [id:"CharacterCreation_value_994"] */, displayName(CLASS_ORDER[classIndex]));
        /* block end [id:"CharacterCreation_block_505"] */
        /* block comment [block:"CharacterCreation_block_506" text:"Runs the set variable action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_506" category:"Variables" label:"set variable" shape:"command" docs:"Runs the set variable action. Values in this block are editable inputs."] */
        s.setVariable(/* param start [id:"CharacterCreation_value_995" type:"text"] */"StatMethod"/* param end [id:"CharacterCreation_value_995"] */, method == /* param start [id:"CharacterCreation_value_996" type:"number"] */0/* param end [id:"CharacterCreation_value_996"] */ ? /* param start [id:"CharacterCreation_value_997" type:"text"] */"Standard"/* param end [id:"CharacterCreation_value_997"] */ : method == /* param start [id:"CharacterCreation_value_998" type:"number"] */1/* param end [id:"CharacterCreation_value_998"] */ ? /* param start [id:"CharacterCreation_value_999" type:"text"] */"Rolled"/* param end [id:"CharacterCreation_value_999"] */ : /* param start [id:"CharacterCreation_value_1000" type:"text"] */"PointBuy"/* param end [id:"CharacterCreation_value_1000"] */);
        /* block end [id:"CharacterCreation_block_506"] */
        /* block comment [block:"CharacterCreation_block_507" text:"Runs the set variable action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_507" category:"Variables" label:"set variable" shape:"command" docs:"Runs the set variable action. Values in this block are editable inputs."] */
        s.setVariable(/* param start [id:"CharacterCreation_value_1001" type:"text"] */"PB"/* param end [id:"CharacterCreation_value_1001"] */, pbRemaining() + /* param start [id:"CharacterCreation_value_1002" type:"text"] */" pts"/* param end [id:"CharacterCreation_value_1002"] */);
        /* block end [id:"CharacterCreation_block_507"] */ /* block comment [block:"CharacterCreation_block_508" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_508" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        dirty = /* param start [id:"CharacterCreation_value_1003" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1003"] */;
        /* block end [id:"CharacterCreation_block_508"] */
    }
    private static String displayName(String raw) { /* block comment [block:"CharacterCreation_block_509" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_509" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_98" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_98" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (raw == /* param start [id:"CharacterCreation_value_1004" type:"value"] */null/* param end [id:"CharacterCreation_value_1004"] */ || raw.length() == /* param start [id:"CharacterCreation_value_1005" type:"number"] */0/* param end [id:"CharacterCreation_value_1005"] */) return raw;
        /* block end [id:"CharacterCreation_block_509"] */
        /* block end [id:"CharacterCreation_block_98"] */ /* block comment [block:"CharacterCreation_block_510" text:"Returns a value or ends the current method."] */
        /* block start [id:"CharacterCreation_block_510" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return raw.substring(/* param start [id:"CharacterCreation_value_1006" type:"number"] */0/* param end [id:"CharacterCreation_value_1006"] */, /* param start [id:"CharacterCreation_value_1007" type:"number"] */1/* param end [id:"CharacterCreation_value_1007"] */).toUpperCase() + raw.substring(/* param start [id:"CharacterCreation_value_1008" type:"number"] */1/* param end [id:"CharacterCreation_value_1008"] */);
        /* block end [id:"CharacterCreation_block_510"] */ }
    public static String starterKit(String className) {
        /* block comment [block:"CharacterCreation_block_511" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_511" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_99" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_99" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (className.equals(/* param start [id:"CharacterCreation_value_1009" type:"text"] */"fighter"/* param end [id:"CharacterCreation_value_1009"] */)) return /* param start [id:"CharacterCreation_value_1010" type:"text"] */"fight_sword + knight_helmet"/* param end [id:"CharacterCreation_value_1010"] */;
        /* block end [id:"CharacterCreation_block_511"] */
        /* block end [id:"CharacterCreation_block_99"] */
        /* block comment [block:"CharacterCreation_block_512" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_512" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_100" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_100" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (className.equals(/* param start [id:"CharacterCreation_value_1011" type:"text"] */"wizard"/* param end [id:"CharacterCreation_value_1011"] */)) return /* param start [id:"CharacterCreation_value_1012" type:"text"] */"whiz_wand + whiz_hat"/* param end [id:"CharacterCreation_value_1012"] */;
        /* block end [id:"CharacterCreation_block_512"] */
        /* block end [id:"CharacterCreation_block_100"] */
        /* block comment [block:"CharacterCreation_block_513" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_513" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_101" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_101" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (className.equals(/* param start [id:"CharacterCreation_value_1013" type:"text"] */"rogue"/* param end [id:"CharacterCreation_value_1013"] */)) return /* param start [id:"CharacterCreation_value_1014" type:"text"] */"poidagger + h_crosssbow"/* param end [id:"CharacterCreation_value_1014"] */;
        /* block end [id:"CharacterCreation_block_513"] */
        /* block end [id:"CharacterCreation_block_101"] */
        /* block comment [block:"CharacterCreation_block_514" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_514" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_102" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_102" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (className.equals(/* param start [id:"CharacterCreation_value_1015" type:"text"] */"cleric"/* param end [id:"CharacterCreation_value_1015"] */)) return /* param start [id:"CharacterCreation_value_1016" type:"text"] */"good_shield + breastplate"/* param end [id:"CharacterCreation_value_1016"] */;
        /* block end [id:"CharacterCreation_block_514"] */
        /* block end [id:"CharacterCreation_block_102"] */
        /* block comment [block:"CharacterCreation_block_515" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_515" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_103" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_103" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (className.equals(/* param start [id:"CharacterCreation_value_1017" type:"text"] */"paladin"/* param end [id:"CharacterCreation_value_1017"] */)) return /* param start [id:"CharacterCreation_value_1018" type:"text"] */"warden_plate + good_shield"/* param end [id:"CharacterCreation_value_1018"] */;
        /* block end [id:"CharacterCreation_block_515"] */
        /* block end [id:"CharacterCreation_block_103"] */
        /* block comment [block:"CharacterCreation_block_516" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_516" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_104" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_104" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (className.equals(/* param start [id:"CharacterCreation_value_1019" type:"text"] */"ranger"/* param end [id:"CharacterCreation_value_1019"] */)) return /* param start [id:"CharacterCreation_value_1020" type:"text"] */"h_crosssbow + mankey_boots"/* param end [id:"CharacterCreation_value_1020"] */;
        /* block end [id:"CharacterCreation_block_516"] */
        /* block end [id:"CharacterCreation_block_104"] */
        /* block comment [block:"CharacterCreation_block_517" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_517" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_105" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_105" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (className.equals(/* param start [id:"CharacterCreation_value_1021" type:"text"] */"artificer"/* param end [id:"CharacterCreation_value_1021"] */)) return /* param start [id:"CharacterCreation_value_1022" type:"text"] */"bombdoiler + bomb_gogs"/* param end [id:"CharacterCreation_value_1022"] */;
        /* block end [id:"CharacterCreation_block_517"] */
        /* block end [id:"CharacterCreation_block_105"] */
        /* block comment [block:"CharacterCreation_block_518" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_518" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_106" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_106" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (className.equals(/* param start [id:"CharacterCreation_value_1023" type:"text"] */"warlock"/* param end [id:"CharacterCreation_value_1023"] */)) return /* param start [id:"CharacterCreation_value_1024" type:"text"] */"necropower_thing + rocloak"/* param end [id:"CharacterCreation_value_1024"] */;
        /* block end [id:"CharacterCreation_block_518"] */
        /* block end [id:"CharacterCreation_block_106"] */
        /* block comment [block:"CharacterCreation_block_519" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_519" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_107" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_107" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (className.equals(/* param start [id:"CharacterCreation_value_1025" type:"text"] */"bard"/* param end [id:"CharacterCreation_value_1025"] */)) return /* param start [id:"CharacterCreation_value_1026" type:"text"] */"mankey_glove + mankey_shirt"/* param end [id:"CharacterCreation_value_1026"] */;
        /* block end [id:"CharacterCreation_block_519"] */
        /* block end [id:"CharacterCreation_block_107"] */
        /* block comment [block:"CharacterCreation_block_520" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_520" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_108" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_108" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (className.equals(/* param start [id:"CharacterCreation_value_1027" type:"text"] */"druid"/* param end [id:"CharacterCreation_value_1027"] */)) return /* param start [id:"CharacterCreation_value_1028" type:"text"] */"mankey_shirt + scythe"/* param end [id:"CharacterCreation_value_1028"] */;
        /* block end [id:"CharacterCreation_block_520"] */
        /* block end [id:"CharacterCreation_block_108"] */
        /* block comment [block:"CharacterCreation_block_521" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_521" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_109" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_109" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (className.equals(/* param start [id:"CharacterCreation_value_1029" type:"text"] */"monk"/* param end [id:"CharacterCreation_value_1029"] */)) return /* param start [id:"CharacterCreation_value_1030" type:"text"] */"mankey_glove + mankey_boots"/* param end [id:"CharacterCreation_value_1030"] */;
        /* block end [id:"CharacterCreation_block_521"] */
        /* block end [id:"CharacterCreation_block_109"] */
        /* block comment [block:"CharacterCreation_block_522" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_522" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_110" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_110" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (className.equals(/* param start [id:"CharacterCreation_value_1031" type:"text"] */"sorcerer"/* param end [id:"CharacterCreation_value_1031"] */)) return /* param start [id:"CharacterCreation_value_1032" type:"text"] */"whiz_wand + rocloak"/* param end [id:"CharacterCreation_value_1032"] */;
        /* block end [id:"CharacterCreation_block_522"] */
        /* block end [id:"CharacterCreation_block_110"] */
        /* block comment [block:"CharacterCreation_block_523" text:"Returns a value or ends the current method."] */
        /* block start [id:"CharacterCreation_block_523" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return /* param start [id:"CharacterCreation_value_1033" type:"text"] */"whiz_wand + wizard_robe"/* param end [id:"CharacterCreation_value_1033"] */;
        /* block end [id:"CharacterCreation_block_523"] */
    }

    private List<Btn> buttons(int w, int h) {
        /* block comment [block:"CharacterCreation_block_524" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_524" category:"Variables" label:"set Btn" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        List<Btn> b = buttonsCache;
        /* block end [id:"CharacterCreation_block_524"] */
        /* block comment [block:"CharacterCreation_block_111" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_111" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (dirty || b == /* param start [id:"CharacterCreation_value_1034" type:"value"] */null/* param end [id:"CharacterCreation_value_1034"] */ || w != cacheW || h != cacheH) { /* block comment [block:"CharacterCreation_block_525" text:"Runs the build buttons action. Values in this block are editable inputs."] */
            /* block start [id:"CharacterCreation_block_525" category:"My Blocks" label:"build buttons" shape:"command" docs:"Runs the build buttons action. Values in this block are editable inputs."] */
            b = buildButtons(w, h);
            /* block end [id:"CharacterCreation_block_525"] */ /* block comment [block:"CharacterCreation_block_526" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_526" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            buttonsCache = b;
            /* block end [id:"CharacterCreation_block_526"] */ /* block comment [block:"CharacterCreation_block_527" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_527" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            cacheW = w;
            /* block end [id:"CharacterCreation_block_527"] */ /* block comment [block:"CharacterCreation_block_528" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_528" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            cacheH = h;
            /* block end [id:"CharacterCreation_block_528"] */ }
        /* block end [id:"CharacterCreation_block_111"] */
        /* block comment [block:"CharacterCreation_block_529" text:"Returns a value or ends the current method."] */
        /* block start [id:"CharacterCreation_block_529" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return b;
        /* block end [id:"CharacterCreation_block_529"] */
    }

    private List<Btn> buildButtons(int w, int h) {
        /* block comment [block:"CharacterCreation_block_530" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_530" category:"Variables" label:"set Btn" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        List<Btn> out = new ArrayList<>();
        /* block end [id:"CharacterCreation_block_530"] */
        /* block comment [block:"CharacterCreation_block_112" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_112" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (GameState.isGameOver()) {
            /* block comment [block:"CharacterCreation_block_531" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_531" category:"Variables" label:"set nw" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int nw = (int) (w * /* param start [id:"CharacterCreation_value_1035" type:"number"] */0.55/* param end [id:"CharacterCreation_value_1035"] */);
            /* block end [id:"CharacterCreation_block_531"] */ /* block comment [block:"CharacterCreation_block_532" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_532" category:"Variables" label:"set nh" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int nh = (int) (h * /* param start [id:"CharacterCreation_value_1036" type:"number"] */0.12/* param end [id:"CharacterCreation_value_1036"] */);
            /* block end [id:"CharacterCreation_block_532"] */
            /* block comment [block:"CharacterCreation_block_533" text:"Calls the add method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_533" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
            out.add(new Btn(/* param start [id:"CharacterCreation_value_1037" type:"text"] */"new_character"/* param end [id:"CharacterCreation_value_1037"] */, KIND_BUTTON, -/* param start [id:"CharacterCreation_value_1038" type:"number"] */1/* param end [id:"CharacterCreation_value_1038"] */, (w - nw) / /* param start [id:"CharacterCreation_value_1039" type:"number"] */2/* param end [id:"CharacterCreation_value_1039"] */, (int) (h * /* param start [id:"CharacterCreation_value_1040" type:"number"] */0.78/* param end [id:"CharacterCreation_value_1040"] */), nw, nh, /* param start [id:"CharacterCreation_value_1041" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1041"] */));
            /* block end [id:"CharacterCreation_block_533"] */
        } else /* block comment [block:"CharacterCreation_block_113" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_113" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (controlsShown) {
            /* block comment [block:"CharacterCreation_block_534" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_534" category:"Variables" label:"set bw" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int bw = (int) (w * /* param start [id:"CharacterCreation_value_1042" type:"number"] */0.28/* param end [id:"CharacterCreation_value_1042"] */);
            /* block end [id:"CharacterCreation_block_534"] */ /* block comment [block:"CharacterCreation_block_535" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_535" category:"Variables" label:"set bh" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int bh = (int) (h * /* param start [id:"CharacterCreation_value_1043" type:"number"] */0.09/* param end [id:"CharacterCreation_value_1043"] */);
            /* block end [id:"CharacterCreation_block_535"] */
            /* block comment [block:"CharacterCreation_block_536" text:"Calls the add method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_536" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
            out.add(new Btn(/* param start [id:"CharacterCreation_value_1044" type:"text"] */"controls_close"/* param end [id:"CharacterCreation_value_1044"] */, KIND_BUTTON, -/* param start [id:"CharacterCreation_value_1045" type:"number"] */1/* param end [id:"CharacterCreation_value_1045"] */, (w - bw) / /* param start [id:"CharacterCreation_value_1046" type:"number"] */2/* param end [id:"CharacterCreation_value_1046"] */, (int) (h * /* param start [id:"CharacterCreation_value_1047" type:"number"] */0.78/* param end [id:"CharacterCreation_value_1047"] */), bw, bh, /* param start [id:"CharacterCreation_value_1048" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1048"] */));
            /* block end [id:"CharacterCreation_block_536"] */
        }
        /* block end [id:"CharacterCreation_block_112"] */ else /* block comment [block:"CharacterCreation_block_114" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_114" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (phase == /* param start [id:"CharacterCreation_value_1049" type:"number"] */0/* param end [id:"CharacterCreation_value_1049"] */) {
            /* block comment [block:"CharacterCreation_block_537" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_537" category:"Variables" label:"set bw" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int bw = (int) (w * /* param start [id:"CharacterCreation_value_1050" type:"number"] */0.44/* param end [id:"CharacterCreation_value_1050"] */);
            /* block end [id:"CharacterCreation_block_537"] */ /* block comment [block:"CharacterCreation_block_538" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_538" category:"Variables" label:"set bh" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int bh = (int) (h * /* param start [id:"CharacterCreation_value_1051" type:"number"] */0.13/* param end [id:"CharacterCreation_value_1051"] */);
            /* block end [id:"CharacterCreation_block_538"] */
            /* block comment [block:"CharacterCreation_block_539" text:"Calls the add method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_539" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
            out.add(new Btn(/* param start [id:"CharacterCreation_value_1052" type:"text"] */"begin_title"/* param end [id:"CharacterCreation_value_1052"] */, KIND_BUTTON, -/* param start [id:"CharacterCreation_value_1053" type:"number"] */1/* param end [id:"CharacterCreation_value_1053"] */, (w - bw) / /* param start [id:"CharacterCreation_value_1054" type:"number"] */2/* param end [id:"CharacterCreation_value_1054"] */, (int) (h * /* param start [id:"CharacterCreation_value_1055" type:"number"] */0.62/* param end [id:"CharacterCreation_value_1055"] */), bw, bh, /* param start [id:"CharacterCreation_value_1056" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1056"] */));
            /* block end [id:"CharacterCreation_block_539"] */
            /* block comment [block:"CharacterCreation_block_540" text:"Calls the add method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_540" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
            out.add(new Btn(/* param start [id:"CharacterCreation_value_1057" type:"text"] */"controls_title"/* param end [id:"CharacterCreation_value_1057"] */, KIND_BUTTON, -/* param start [id:"CharacterCreation_value_1058" type:"number"] */1/* param end [id:"CharacterCreation_value_1058"] */, (int) (w * /* param start [id:"CharacterCreation_value_1059" type:"number"] */0.72/* param end [id:"CharacterCreation_value_1059"] */), (int) (h * /* param start [id:"CharacterCreation_value_1060" type:"number"] */0.88/* param end [id:"CharacterCreation_value_1060"] */), (int) (w * /* param start [id:"CharacterCreation_value_1061" type:"number"] */0.22/* param end [id:"CharacterCreation_value_1061"] */), (int) (h * /* param start [id:"CharacterCreation_value_1062" type:"number"] */0.08/* param end [id:"CharacterCreation_value_1062"] */), /* param start [id:"CharacterCreation_value_1063" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1063"] */));
            /* block end [id:"CharacterCreation_block_540"] */
        }
        /* block end [id:"CharacterCreation_block_113"] */ else /* block comment [block:"CharacterCreation_block_115" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_115" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (phase == /* param start [id:"CharacterCreation_value_1064" type:"number"] */4/* param end [id:"CharacterCreation_value_1064"] */) {
            /* block comment [block:"CharacterCreation_block_541" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_541" category:"Variables" label:"set bw" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int bw = (int) (w * /* param start [id:"CharacterCreation_value_1065" type:"number"] */0.55/* param end [id:"CharacterCreation_value_1065"] */);
            /* block end [id:"CharacterCreation_block_541"] */ /* block comment [block:"CharacterCreation_block_542" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_542" category:"Variables" label:"set bh" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int bh = (int) (h * /* param start [id:"CharacterCreation_value_1066" type:"number"] */0.13/* param end [id:"CharacterCreation_value_1066"] */);
            /* block end [id:"CharacterCreation_block_542"] */
            /* block comment [block:"CharacterCreation_block_543" text:"Calls the add method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_543" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
            out.add(new Btn(/* param start [id:"CharacterCreation_value_1067" type:"text"] */"new_character"/* param end [id:"CharacterCreation_value_1067"] */, KIND_BUTTON, -/* param start [id:"CharacterCreation_value_1068" type:"number"] */1/* param end [id:"CharacterCreation_value_1068"] */, (w - bw) / /* param start [id:"CharacterCreation_value_1069" type:"number"] */2/* param end [id:"CharacterCreation_value_1069"] */, (int) (h * /* param start [id:"CharacterCreation_value_1070" type:"number"] */0.58/* param end [id:"CharacterCreation_value_1070"] */), bw, bh, /* param start [id:"CharacterCreation_value_1071" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1071"] */));
            /* block end [id:"CharacterCreation_block_543"] */
        }
        /* block end [id:"CharacterCreation_block_114"] */ else /* block comment [block:"CharacterCreation_block_116" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_116" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (phase == /* param start [id:"CharacterCreation_value_1072" type:"number"] */1/* param end [id:"CharacterCreation_value_1072"] */) {
            /* block comment [block:"CharacterCreation_block_544" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_544" category:"Variables" label:"set ds" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double ds = w / /* param start [id:"CharacterCreation_value_1073" type:"number"] */480.0/* param end [id:"CharacterCreation_value_1073"] */;
            /* block end [id:"CharacterCreation_block_544"] */ /* block comment [block:"CharacterCreation_block_545" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_545" category:"Variables" label:"set gap" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int gap = Math.max(/* param start [id:"CharacterCreation_value_1074" type:"number"] */4/* param end [id:"CharacterCreation_value_1074"] */, (int) (/* param start [id:"CharacterCreation_value_1075" type:"number"] */8/* param end [id:"CharacterCreation_value_1075"] */ * ds));
            /* block end [id:"CharacterCreation_block_545"] */ /* block comment [block:"CharacterCreation_block_546" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_546" category:"Variables" label:"set margin" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int margin = (int) (w * /* param start [id:"CharacterCreation_value_1076" type:"number"] */0.06/* param end [id:"CharacterCreation_value_1076"] */);
            /* block end [id:"CharacterCreation_block_546"] */ /* block comment [block:"CharacterCreation_block_547" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_547" category:"Variables" label:"set cols" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int cols = /* param start [id:"CharacterCreation_value_1077" type:"number"] */5/* param end [id:"CharacterCreation_value_1077"] */;
            /* block end [id:"CharacterCreation_block_547"] */
            /* block comment [block:"CharacterCreation_block_548" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_548" category:"Variables" label:"set cardW" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int cardW = (w - /* param start [id:"CharacterCreation_value_1078" type:"number"] */2/* param end [id:"CharacterCreation_value_1078"] */ * margin - (cols - /* param start [id:"CharacterCreation_value_1079" type:"number"] */1/* param end [id:"CharacterCreation_value_1079"] */) * gap) / cols;
            /* block end [id:"CharacterCreation_block_548"] */ /* block comment [block:"CharacterCreation_block_549" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_549" category:"Variables" label:"set gy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int gy = (int) (h * /* param start [id:"CharacterCreation_value_1080" type:"number"] */0.30/* param end [id:"CharacterCreation_value_1080"] */);
            /* block end [id:"CharacterCreation_block_549"] */
            /* block comment [block:"CharacterCreation_block_550" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_550" category:"Variables" label:"set panelTop" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int panelTop = (int) (h * /* param start [id:"CharacterCreation_value_1081" type:"number"] */0.655/* param end [id:"CharacterCreation_value_1081"] */);
            /* block end [id:"CharacterCreation_block_550"] */ /* block comment [block:"CharacterCreation_block_551" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_551" category:"Variables" label:"set cardH" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int cardH = (panelTop - gap - gy - /* param start [id:"CharacterCreation_value_1082" type:"number"] */2/* param end [id:"CharacterCreation_value_1082"] */ * gap) / /* param start [id:"CharacterCreation_value_1083" type:"number"] */3/* param end [id:"CharacterCreation_value_1083"] */;
            /* block end [id:"CharacterCreation_block_551"] */ /* block comment [block:"CharacterCreation_block_552" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"CharacterCreation_block_552" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"CharacterCreation_block_117" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"CharacterCreation_block_117" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (cardH < /* param start [id:"CharacterCreation_value_1084" type:"number"] */1/* param end [id:"CharacterCreation_value_1084"] */) cardH = /* param start [id:"CharacterCreation_value_1085" type:"number"] */1/* param end [id:"CharacterCreation_value_1085"] */;
            /* block end [id:"CharacterCreation_block_552"] */
            /* block end [id:"CharacterCreation_block_117"] */
            /* block comment [block:"CharacterCreation_block_553" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_553" category:"Variables" label:"set idx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int idx = /* param start [id:"CharacterCreation_value_1086" type:"number"] */0/* param end [id:"CharacterCreation_value_1086"] */;
            /* block end [id:"CharacterCreation_block_553"] */ int[] rowCounts = {/* param start [id:"CharacterCreation_value_1087" type:"number"] */5/* param end [id:"CharacterCreation_value_1087"] */, /* param start [id:"CharacterCreation_value_1088" type:"number"] */5/* param end [id:"CharacterCreation_value_1088"] */, /* param start [id:"CharacterCreation_value_1089" type:"number"] */3/* param end [id:"CharacterCreation_value_1089"] */};
            /* block comment [block:"CharacterCreation_block_118" text:"Repeats the nested operations for each loop turn."] */
            /* block start [id:"CharacterCreation_block_118" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
            for (int r = /* param start [id:"CharacterCreation_value_1090" type:"number"] */0/* param end [id:"CharacterCreation_value_1090"] */; r < /* param start [id:"CharacterCreation_value_1091" type:"number"] */3/* param end [id:"CharacterCreation_value_1091"] */; r++) {
                /* block comment [block:"CharacterCreation_block_554" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_554" category:"Variables" label:"set count" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int count = rowCounts[r];
                /* block end [id:"CharacterCreation_block_554"] */ /* block comment [block:"CharacterCreation_block_555" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_555" category:"Variables" label:"set rowW" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int rowW = count * cardW + (count - /* param start [id:"CharacterCreation_value_1092" type:"number"] */1/* param end [id:"CharacterCreation_value_1092"] */) * gap;
                /* block end [id:"CharacterCreation_block_555"] */ /* block comment [block:"CharacterCreation_block_556" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_556" category:"Variables" label:"set x0" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int x0 = (w - rowW) / /* param start [id:"CharacterCreation_value_1093" type:"number"] */2/* param end [id:"CharacterCreation_value_1093"] */;
                /* block end [id:"CharacterCreation_block_556"] */ /* block comment [block:"CharacterCreation_block_557" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_557" category:"Variables" label:"set y" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int y = gy + r * (cardH + gap);
                /* block end [id:"CharacterCreation_block_557"] */
                /* block comment [block:"CharacterCreation_block_119" text:"Repeats the nested operations for each loop turn."] */
                /* block start [id:"CharacterCreation_block_119" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
                for (int c = /* param start [id:"CharacterCreation_value_1094" type:"number"] */0/* param end [id:"CharacterCreation_value_1094"] */; c < count && idx < CLASS_ORDER.length; c++, idx++) out.add(new Btn(/* param start [id:"CharacterCreation_value_1095" type:"text"] */"class_"/* param end [id:"CharacterCreation_value_1095"] */ + idx, KIND_CARD, idx, x0 + c * (cardW + gap), y, cardW, cardH, /* param start [id:"CharacterCreation_value_1096" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1096"] */));
                /* block end [id:"CharacterCreation_block_119"] */
            }
            /* block end [id:"CharacterCreation_block_118"] */
            /* block comment [block:"CharacterCreation_block_558" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_558" category:"Variables" label:"set nh" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int nh = (int) (h * /* param start [id:"CharacterCreation_value_1097" type:"number"] */0.085/* param end [id:"CharacterCreation_value_1097"] */);
            /* block end [id:"CharacterCreation_block_558"] */ /* block comment [block:"CharacterCreation_block_559" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_559" category:"Variables" label:"set ny" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int ny = (int) (h * /* param start [id:"CharacterCreation_value_1098" type:"number"] */0.175/* param end [id:"CharacterCreation_value_1098"] */);
            /* block end [id:"CharacterCreation_block_559"] */
            /* block comment [block:"CharacterCreation_block_560" text:"Calls the add method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_560" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
            out.add(new Btn(/* param start [id:"CharacterCreation_value_1099" type:"text"] */"name"/* param end [id:"CharacterCreation_value_1099"] */, KIND_FIELD, -/* param start [id:"CharacterCreation_value_1100" type:"number"] */1/* param end [id:"CharacterCreation_value_1100"] */, (int) (w * /* param start [id:"CharacterCreation_value_1101" type:"number"] */0.35/* param end [id:"CharacterCreation_value_1101"] */), ny, (int) (w * /* param start [id:"CharacterCreation_value_1102" type:"number"] */0.45/* param end [id:"CharacterCreation_value_1102"] */), nh, /* param start [id:"CharacterCreation_value_1103" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1103"] */));
            /* block end [id:"CharacterCreation_block_560"] */
            /* block comment [block:"CharacterCreation_block_561" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_561" category:"Variables" label:"set fy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int fy = (int) (h * /* param start [id:"CharacterCreation_value_1104" type:"number"] */0.895/* param end [id:"CharacterCreation_value_1104"] */);
            /* block end [id:"CharacterCreation_block_561"] */ /* block comment [block:"CharacterCreation_block_562" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_562" category:"Variables" label:"set fh" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int fh = (int) (h * /* param start [id:"CharacterCreation_value_1105" type:"number"] */0.085/* param end [id:"CharacterCreation_value_1105"] */);
            /* block end [id:"CharacterCreation_block_562"] */
            /* block comment [block:"CharacterCreation_block_563" text:"Calls the add method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_563" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
            out.add(new Btn(/* param start [id:"CharacterCreation_value_1106" type:"text"] */"prev"/* param end [id:"CharacterCreation_value_1106"] */, KIND_BUTTON, -/* param start [id:"CharacterCreation_value_1107" type:"number"] */1/* param end [id:"CharacterCreation_value_1107"] */, (int) (w * /* param start [id:"CharacterCreation_value_1108" type:"number"] */0.06/* param end [id:"CharacterCreation_value_1108"] */), fy, (int) (w * /* param start [id:"CharacterCreation_value_1109" type:"number"] */0.10/* param end [id:"CharacterCreation_value_1109"] */), fh, /* param start [id:"CharacterCreation_value_1110" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1110"] */));
            /* block end [id:"CharacterCreation_block_563"] */
            /* block comment [block:"CharacterCreation_block_564" text:"Calls the add method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_564" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
            out.add(new Btn(/* param start [id:"CharacterCreation_value_1111" type:"text"] */"next"/* param end [id:"CharacterCreation_value_1111"] */, KIND_BUTTON, -/* param start [id:"CharacterCreation_value_1112" type:"number"] */1/* param end [id:"CharacterCreation_value_1112"] */, (int) (w * /* param start [id:"CharacterCreation_value_1113" type:"number"] */0.175/* param end [id:"CharacterCreation_value_1113"] */), fy, (int) (w * /* param start [id:"CharacterCreation_value_1114" type:"number"] */0.10/* param end [id:"CharacterCreation_value_1114"] */), fh, /* param start [id:"CharacterCreation_value_1115" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1115"] */));
            /* block end [id:"CharacterCreation_block_564"] */
            /* block comment [block:"CharacterCreation_block_565" text:"Calls the add method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_565" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
            out.add(new Btn(/* param start [id:"CharacterCreation_value_1116" type:"text"] */"confirm"/* param end [id:"CharacterCreation_value_1116"] */, KIND_BUTTON, -/* param start [id:"CharacterCreation_value_1117" type:"number"] */1/* param end [id:"CharacterCreation_value_1117"] */, (int) (w * /* param start [id:"CharacterCreation_value_1118" type:"number"] */0.66/* param end [id:"CharacterCreation_value_1118"] */), fy, (int) (w * /* param start [id:"CharacterCreation_value_1119" type:"number"] */0.28/* param end [id:"CharacterCreation_value_1119"] */), fh, /* param start [id:"CharacterCreation_value_1120" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1120"] */));
            /* block end [id:"CharacterCreation_block_565"] */
            /* block comment [block:"CharacterCreation_block_566" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_566" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            classPanelX = (int) (w * /* param start [id:"CharacterCreation_value_1121" type:"number"] */0.06/* param end [id:"CharacterCreation_value_1121"] */);
            /* block end [id:"CharacterCreation_block_566"] */ /* block comment [block:"CharacterCreation_block_567" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_567" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            classPanelW = (int) (w * /* param start [id:"CharacterCreation_value_1122" type:"number"] */0.88/* param end [id:"CharacterCreation_value_1122"] */);
            /* block end [id:"CharacterCreation_block_567"] */ /* block comment [block:"CharacterCreation_block_568" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_568" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            classPanelY = (int) (h * /* param start [id:"CharacterCreation_value_1123" type:"number"] */0.655/* param end [id:"CharacterCreation_value_1123"] */);
            /* block end [id:"CharacterCreation_block_568"] */ /* block comment [block:"CharacterCreation_block_569" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_569" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            classPanelH = (int) (h * /* param start [id:"CharacterCreation_value_1124" type:"number"] */0.21/* param end [id:"CharacterCreation_value_1124"] */);
            /* block end [id:"CharacterCreation_block_569"] */
        }
        /* block end [id:"CharacterCreation_block_115"] */ else /* block comment [block:"CharacterCreation_block_120" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_120" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (phase == /* param start [id:"CharacterCreation_value_1125" type:"number"] */2/* param end [id:"CharacterCreation_value_1125"] */) {
            /* block comment [block:"CharacterCreation_block_570" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_570" category:"Variables" label:"set tabY" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int tabY = (int) (h * /* param start [id:"CharacterCreation_value_1126" type:"number"] */0.135/* param end [id:"CharacterCreation_value_1126"] */);
            /* block end [id:"CharacterCreation_block_570"] */ /* block comment [block:"CharacterCreation_block_571" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_571" category:"Variables" label:"set tabH" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int tabH = (int) (h * /* param start [id:"CharacterCreation_value_1127" type:"number"] */0.085/* param end [id:"CharacterCreation_value_1127"] */);
            /* block end [id:"CharacterCreation_block_571"] */ /* block comment [block:"CharacterCreation_block_572" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_572" category:"Variables" label:"set tabW" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int tabW = (int) (w * /* param start [id:"CharacterCreation_value_1128" type:"number"] */0.28/* param end [id:"CharacterCreation_value_1128"] */);
            /* block end [id:"CharacterCreation_block_572"] */ /* block comment [block:"CharacterCreation_block_573" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_573" category:"Variables" label:"set tabX0" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int tabX0 = (int) (w * /* param start [id:"CharacterCreation_value_1129" type:"number"] */0.06/* param end [id:"CharacterCreation_value_1129"] */);
            /* block end [id:"CharacterCreation_block_573"] */ /* block comment [block:"CharacterCreation_block_574" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_574" category:"Variables" label:"set tabGap" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int tabGap = (int) (w * /* param start [id:"CharacterCreation_value_1130" type:"number"] */0.02/* param end [id:"CharacterCreation_value_1130"] */);
            /* block end [id:"CharacterCreation_block_574"] */
            /* block comment [block:"CharacterCreation_block_575" text:"Calls the add method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_575" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
            out.add(new Btn(/* param start [id:"CharacterCreation_value_1131" type:"text"] */"tab_0"/* param end [id:"CharacterCreation_value_1131"] */, KIND_TAB, /* param start [id:"CharacterCreation_value_1132" type:"number"] */0/* param end [id:"CharacterCreation_value_1132"] */, tabX0, tabY, tabW, tabH, /* param start [id:"CharacterCreation_value_1133" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1133"] */));
            /* block end [id:"CharacterCreation_block_575"] */
            /* block comment [block:"CharacterCreation_block_576" text:"Calls the add method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_576" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
            out.add(new Btn(/* param start [id:"CharacterCreation_value_1134" type:"text"] */"tab_1"/* param end [id:"CharacterCreation_value_1134"] */, KIND_TAB, /* param start [id:"CharacterCreation_value_1135" type:"number"] */1/* param end [id:"CharacterCreation_value_1135"] */, tabX0 + (tabW + tabGap), tabY, tabW, tabH, /* param start [id:"CharacterCreation_value_1136" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1136"] */));
            /* block end [id:"CharacterCreation_block_576"] */
            /* block comment [block:"CharacterCreation_block_577" text:"Calls the add method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_577" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
            out.add(new Btn(/* param start [id:"CharacterCreation_value_1137" type:"text"] */"tab_2"/* param end [id:"CharacterCreation_value_1137"] */, KIND_TAB, /* param start [id:"CharacterCreation_value_1138" type:"number"] */2/* param end [id:"CharacterCreation_value_1138"] */, tabX0 + /* param start [id:"CharacterCreation_value_1139" type:"number"] */2/* param end [id:"CharacterCreation_value_1139"] */ * (tabW + tabGap), tabY, tabW, tabH, /* param start [id:"CharacterCreation_value_1140" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1140"] */));
            /* block end [id:"CharacterCreation_block_577"] */
            /* block comment [block:"CharacterCreation_block_121" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"CharacterCreation_block_121" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (method == /* param start [id:"CharacterCreation_value_1141" type:"number"] */1/* param end [id:"CharacterCreation_value_1141"] */) {
                /* block comment [block:"CharacterCreation_block_578" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_578" category:"Variables" label:"set bw" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int bw = (int) (w * /* param start [id:"CharacterCreation_value_1142" type:"number"] */0.30/* param end [id:"CharacterCreation_value_1142"] */);
                /* block end [id:"CharacterCreation_block_578"] */ /* block comment [block:"CharacterCreation_block_579" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_579" category:"Variables" label:"set bh" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int bh = (int) (h * /* param start [id:"CharacterCreation_value_1143" type:"number"] */0.08/* param end [id:"CharacterCreation_value_1143"] */);
                /* block end [id:"CharacterCreation_block_579"] */
                /* block comment [block:"CharacterCreation_block_580" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_580" category:"Variables" label:"set footTop" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int footTop = (int) (h * /* param start [id:"CharacterCreation_value_1144" type:"number"] */0.895/* param end [id:"CharacterCreation_value_1144"] */) - /* param start [id:"CharacterCreation_value_1145" type:"number"] */3/* param end [id:"CharacterCreation_value_1145"] */ - (int) (/* param start [id:"CharacterCreation_value_1146" type:"number"] */9/* param end [id:"CharacterCreation_value_1146"] */ * w / /* param start [id:"CharacterCreation_value_1147" type:"number"] */480.0/* param end [id:"CharacterCreation_value_1147"] */);
                /* block end [id:"CharacterCreation_block_580"] */ /* block comment [block:"CharacterCreation_block_581" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_581" category:"Variables" label:"set rgap" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int rgap = Math.max(/* param start [id:"CharacterCreation_value_1148" type:"number"] */6/* param end [id:"CharacterCreation_value_1148"] */, (int) (/* param start [id:"CharacterCreation_value_1149" type:"number"] */8/* param end [id:"CharacterCreation_value_1149"] */ * w / /* param start [id:"CharacterCreation_value_1150" type:"number"] */480.0/* param end [id:"CharacterCreation_value_1150"] */));
                /* block end [id:"CharacterCreation_block_581"] */
                /* block comment [block:"CharacterCreation_block_582" text:"Calls the add method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_582" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
                out.add(new Btn(/* param start [id:"CharacterCreation_value_1151" type:"text"] */"reroll"/* param end [id:"CharacterCreation_value_1151"] */, KIND_BUTTON, -/* param start [id:"CharacterCreation_value_1152" type:"number"] */1/* param end [id:"CharacterCreation_value_1152"] */, (w - bw) / /* param start [id:"CharacterCreation_value_1153" type:"number"] */2/* param end [id:"CharacterCreation_value_1153"] */, footTop - rgap - bh, bw, bh, /* param start [id:"CharacterCreation_value_1154" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1154"] */));
                /* block end [id:"CharacterCreation_block_582"] */
            }
            /* block end [id:"CharacterCreation_block_121"] */
            /* block comment [block:"CharacterCreation_block_583" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_583" category:"Variables" label:"set ds2" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double ds2 = w / /* param start [id:"CharacterCreation_value_1155" type:"number"] */480.0/* param end [id:"CharacterCreation_value_1155"] */;
            /* block end [id:"CharacterCreation_block_583"] */ /* block comment [block:"CharacterCreation_block_584" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_584" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            pbRowY0 = (int) (h * /* param start [id:"CharacterCreation_value_1156" type:"number"] */0.27/* param end [id:"CharacterCreation_value_1156"] */);
            /* block end [id:"CharacterCreation_block_584"] */ /* block comment [block:"CharacterCreation_block_585" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_585" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            pbRowH = (int) (h * /* param start [id:"CharacterCreation_value_1157" type:"number"] */0.072/* param end [id:"CharacterCreation_value_1157"] */);
            /* block end [id:"CharacterCreation_block_585"] */
            /* block comment [block:"CharacterCreation_block_122" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"CharacterCreation_block_122" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (method == /* param start [id:"CharacterCreation_value_1158" type:"number"] */2/* param end [id:"CharacterCreation_value_1158"] */) {
                /* block comment [block:"CharacterCreation_block_586" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_586" category:"Variables" label:"set stepW" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int stepW = Math.max(/* param start [id:"CharacterCreation_value_1159" type:"number"] */22/* param end [id:"CharacterCreation_value_1159"] */, (int) (/* param start [id:"CharacterCreation_value_1160" type:"number"] */30/* param end [id:"CharacterCreation_value_1160"] */ * ds2));
                /* block end [id:"CharacterCreation_block_586"] */ /* block comment [block:"CharacterCreation_block_587" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_587" category:"Variables" label:"set stepH" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int stepH = Math.max(/* param start [id:"CharacterCreation_value_1161" type:"number"] */18/* param end [id:"CharacterCreation_value_1161"] */, (int) (/* param start [id:"CharacterCreation_value_1162" type:"number"] */22/* param end [id:"CharacterCreation_value_1162"] */ * ds2));
                /* block end [id:"CharacterCreation_block_587"] */
                /* block comment [block:"CharacterCreation_block_588" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_588" category:"Variables" label:"set plusX" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int plusX = (int) (w * /* param start [id:"CharacterCreation_value_1163" type:"number"] */0.76/* param end [id:"CharacterCreation_value_1163"] */);
                /* block end [id:"CharacterCreation_block_588"] */ /* block comment [block:"CharacterCreation_block_589" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_589" category:"Variables" label:"set minusX" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int minusX = (int) (w * /* param start [id:"CharacterCreation_value_1164" type:"number"] */0.52/* param end [id:"CharacterCreation_value_1164"] */);
                /* block end [id:"CharacterCreation_block_589"] */
                /* block comment [block:"CharacterCreation_block_123" text:"Repeats the nested operations for each loop turn."] */
                /* block start [id:"CharacterCreation_block_123" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
                for (int i = /* param start [id:"CharacterCreation_value_1165" type:"number"] */0/* param end [id:"CharacterCreation_value_1165"] */; i < /* param start [id:"CharacterCreation_value_1166" type:"number"] */6/* param end [id:"CharacterCreation_value_1166"] */; i++) {
                    /* block comment [block:"CharacterCreation_block_590" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    /* block start [id:"CharacterCreation_block_590" category:"Variables" label:"set ry" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    int ry = pbRowY0 + i * pbRowH + (pbRowH - stepH) / /* param start [id:"CharacterCreation_value_1167" type:"number"] */2/* param end [id:"CharacterCreation_value_1167"] */;
                    /* block end [id:"CharacterCreation_block_590"] */
                    /* block comment [block:"CharacterCreation_block_591" text:"Calls the add method using its declared arguments."] */
                    /* block start [id:"CharacterCreation_block_591" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
                    out.add(new Btn(/* param start [id:"CharacterCreation_value_1168" type:"text"] */"pb_minus_"/* param end [id:"CharacterCreation_value_1168"] */ + i, KIND_STEPPER, i, minusX, ry, stepW, stepH, pb[i] > PB_MIN));
                    /* block end [id:"CharacterCreation_block_591"] */
                    /* block comment [block:"CharacterCreation_block_592" text:"Calls the add method using its declared arguments."] */
                    /* block start [id:"CharacterCreation_block_592" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
                    out.add(new Btn(/* param start [id:"CharacterCreation_value_1169" type:"text"] */"pb_plus_"/* param end [id:"CharacterCreation_value_1169"] */ + i, KIND_STEPPER, i, plusX, ry, stepW, stepH, pb[i] < PB_MAX && pbRemaining() >= pbCost(pb[i] + /* param start [id:"CharacterCreation_value_1170" type:"number"] */1/* param end [id:"CharacterCreation_value_1170"] */) - pbCost(pb[i])));
                    /* block end [id:"CharacterCreation_block_592"] */
                }
                /* block end [id:"CharacterCreation_block_123"] */
            }
            /* block end [id:"CharacterCreation_block_122"] */
            /* block comment [block:"CharacterCreation_block_593" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_593" category:"Variables" label:"set fy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int fy = (int) (h * /* param start [id:"CharacterCreation_value_1171" type:"number"] */0.895/* param end [id:"CharacterCreation_value_1171"] */);
            /* block end [id:"CharacterCreation_block_593"] */ /* block comment [block:"CharacterCreation_block_594" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_594" category:"Variables" label:"set fh" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int fh = (int) (h * /* param start [id:"CharacterCreation_value_1172" type:"number"] */0.085/* param end [id:"CharacterCreation_value_1172"] */);
            /* block end [id:"CharacterCreation_block_594"] */
            /* block comment [block:"CharacterCreation_block_595" text:"Calls the add method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_595" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
            out.add(new Btn(/* param start [id:"CharacterCreation_value_1173" type:"text"] */"back"/* param end [id:"CharacterCreation_value_1173"] */, KIND_BUTTON, -/* param start [id:"CharacterCreation_value_1174" type:"number"] */1/* param end [id:"CharacterCreation_value_1174"] */, (int) (w * /* param start [id:"CharacterCreation_value_1175" type:"number"] */0.06/* param end [id:"CharacterCreation_value_1175"] */), fy, (int) (w * /* param start [id:"CharacterCreation_value_1176" type:"number"] */0.22/* param end [id:"CharacterCreation_value_1176"] */), fh, /* param start [id:"CharacterCreation_value_1177" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1177"] */));
            /* block end [id:"CharacterCreation_block_595"] */
            /* block comment [block:"CharacterCreation_block_596" text:"Calls the add method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_596" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
            out.add(new Btn(/* param start [id:"CharacterCreation_value_1178" type:"text"] */"begin_game"/* param end [id:"CharacterCreation_value_1178"] */, KIND_BUTTON, -/* param start [id:"CharacterCreation_value_1179" type:"number"] */1/* param end [id:"CharacterCreation_value_1179"] */, (int) (w * /* param start [id:"CharacterCreation_value_1180" type:"number"] */0.66/* param end [id:"CharacterCreation_value_1180"] */), fy, (int) (w * /* param start [id:"CharacterCreation_value_1181" type:"number"] */0.28/* param end [id:"CharacterCreation_value_1181"] */), fh, /* param start [id:"CharacterCreation_value_1182" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1182"] */));
            /* block end [id:"CharacterCreation_block_596"] */
        }
        /* block end [id:"CharacterCreation_block_120"] */
        /* block end [id:"CharacterCreation_block_116"] */
        /* block comment [block:"CharacterCreation_block_597" text:"Returns a value or ends the current method."] */
        /* block start [id:"CharacterCreation_block_597" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return out;
        /* block end [id:"CharacterCreation_block_597"] */
    }

    private Btn findBtn(List<Btn> btns, String id) {
        /* block comment [block:"CharacterCreation_block_598" text:"Runs the for action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_598" category:"My Blocks" label:"for" shape:"command" docs:"Runs the for action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_124" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"CharacterCreation_block_124" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (Btn b : btns) /* block comment [block:"CharacterCreation_block_125" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_125" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (b.id.equals(id)) return b;
        /* block end [id:"CharacterCreation_block_125"] */
        /* block end [id:"CharacterCreation_block_598"] */
        /* block end [id:"CharacterCreation_block_124"] */
        /* block comment [block:"CharacterCreation_block_599" text:"Returns a value or ends the current method."] */
        /* block start [id:"CharacterCreation_block_599" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return /* param start [id:"CharacterCreation_value_1183" type:"value"] */null/* param end [id:"CharacterCreation_value_1183"] */;
        /* block end [id:"CharacterCreation_block_599"] */
    }

    // ---- BitmapFont text helpers (glyph PNGs from jscratch_ws/assets) ----
    private static BitmapFont.Style fontFor(int size, boolean bold) { /* block comment [block:"CharacterCreation_block_600" text:"Returns a value or ends the current method."] */
        /* block start [id:"CharacterCreation_block_600" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return BitmapFont.style(Math.max(/* param start [id:"CharacterCreation_value_1185" type:"number"] */6/* param end [id:"CharacterCreation_value_1185"] */, size), bold);
        /* block end [id:"CharacterCreation_block_600"] */ }
    private static int textWidth(Graphics2D g, String s) { /* block comment [block:"CharacterCreation_block_601" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_601" category:"Variables" label:"set st" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        BitmapFont.Style st = fontFor(12, false);
        /* block end [id:"CharacterCreation_block_601"] */ /* block comment [block:"CharacterCreation_block_602" text:"Returns a value or ends the current method."] */
        /* block start [id:"CharacterCreation_block_602" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return BitmapFont.width(s, st);
        /* block end [id:"CharacterCreation_block_602"] */ }
    private static void drawCentered(Graphics2D g, String s, int cx, int y, Color c, BitmapFont.Style f) {
        /* block comment [block:"CharacterCreation_block_604" text:"Runs the set color action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_604" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
        g.setColor(c);
        /* block end [id:"CharacterCreation_block_604"] */ /* block comment [block:"CharacterCreation_block_605" text:"Calls the draw string method using its declared arguments."] */
        /* block start [id:"CharacterCreation_block_605" category:"My Blocks" label:"call draw string" shape:"command" docs:"Calls the draw string method using its declared arguments."] */
        BitmapFont.drawCentered(g, s, cx, y, f);
        /* block end [id:"CharacterCreation_block_605"] */
    }
    private static void drawText(Graphics2D g, String s, int x, int y, Color c, BitmapFont.Style f) {
        /* block comment [block:"CharacterCreation_block_607" text:"Runs the set color action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_607" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
        g.setColor(c);
        /* block end [id:"CharacterCreation_block_607"] */ /* block comment [block:"CharacterCreation_block_608" text:"Calls the draw string method using its declared arguments."] */
        /* block start [id:"CharacterCreation_block_608" category:"My Blocks" label:"call draw string" shape:"command" docs:"Calls the draw string method using its declared arguments."] */
        BitmapFont.draw(g, s, x, y, f);
        /* block end [id:"CharacterCreation_block_608"] */
    }

    @Override
    public void draw(Graphics2D g) {
        /* block comment [block:"CharacterCreation_block_609" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_609" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_126" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_126" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!isVisible()) return;
        /* block end [id:"CharacterCreation_block_609"] */
        /* block end [id:"CharacterCreation_block_126"] */
        /* block comment [block:"CharacterCreation_block_610" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_610" category:"Variables" label:"set showControlsOverlay" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        boolean showControlsOverlay = controlsShown;
        /* block end [id:"CharacterCreation_block_610"] */
        /* block comment [block:"CharacterCreation_block_611" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_611" category:"Variables" label:"set showDeathOverlay" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        boolean showDeathOverlay = GameState.isGameOver();
        /* block end [id:"CharacterCreation_block_611"] */
        /* block comment [block:"CharacterCreation_block_612" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_612" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_127" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_127" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!showControlsOverlay && !showDeathOverlay
        && (phase >= /* param start [id:"CharacterCreation_value_1187" type:"number"] */3/* param end [id:"CharacterCreation_value_1187"] */ || !GameState.isCharacterCreate())) return;
        /* block end [id:"CharacterCreation_block_612"] */
        /* block end [id:"CharacterCreation_block_127"] */
        /* block comment [block:"CharacterCreation_block_613" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_613" category:"Variables" label:"set stage" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage stage = Stage.getInstance();
        /* block end [id:"CharacterCreation_block_613"] */
        /* block comment [block:"CharacterCreation_block_614" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_614" category:"Variables" label:"set w" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int w = stage.getWidth();
        /* block end [id:"CharacterCreation_block_614"] */ /* block comment [block:"CharacterCreation_block_615" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_615" category:"Variables" label:"set h" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int h = stage.getHeight();
        /* block end [id:"CharacterCreation_block_615"] */
        /* block comment [block:"CharacterCreation_block_616" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_616" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_128" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_128" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (w <= /* param start [id:"CharacterCreation_value_1188" type:"number"] */0/* param end [id:"CharacterCreation_value_1188"] */ || h <= /* param start [id:"CharacterCreation_value_1189" type:"number"] */0/* param end [id:"CharacterCreation_value_1189"] */) return;
        /* block end [id:"CharacterCreation_block_616"] */
        /* block end [id:"CharacterCreation_block_128"] */
        /* block comment [block:"CharacterCreation_block_129" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_129" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (w != lastW || h != lastH) { /* block comment [block:"CharacterCreation_block_617" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_617" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            lastW = w;
            /* block end [id:"CharacterCreation_block_617"] */ /* block comment [block:"CharacterCreation_block_618" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_618" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            lastH = h;
            /* block end [id:"CharacterCreation_block_618"] */ /* block comment [block:"CharacterCreation_block_619" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_619" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            dirty = /* param start [id:"CharacterCreation_value_1190" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1190"] */;
            /* block end [id:"CharacterCreation_block_619"] */ }
        /* block end [id:"CharacterCreation_block_129"] */
        /* block comment [block:"CharacterCreation_block_620" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_620" category:"Variables" label:"set Btn" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        List<Btn> btns = buttons(w, h);
        /* block end [id:"CharacterCreation_block_620"] */
        /* block comment [block:"CharacterCreation_block_621" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_621" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_130" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_130" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (btns == /* param start [id:"CharacterCreation_value_1191" type:"value"] */null/* param end [id:"CharacterCreation_value_1191"] */) return;
        /* block end [id:"CharacterCreation_block_621"] */
        /* block end [id:"CharacterCreation_block_130"] */
        /* block comment [block:"CharacterCreation_block_131" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_131" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (showControlsOverlay || showDeathOverlay) {
            /* block comment [block:"CharacterCreation_block_622" text:"Calls the drawModalOverlay method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_622" category:"My Blocks" label:"call draw modal overlay" shape:"command" docs:"Calls the drawModalOverlay method using its declared arguments."] */
            drawModalOverlay(g, w, h, btns, showDeathOverlay);
            /* block end [id:"CharacterCreation_block_622"] */
            /* block comment [block:"CharacterCreation_block_623" text:"Returns a value or ends the current method."] */
            /* block start [id:"CharacterCreation_block_623" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"CharacterCreation_block_623"] */
        }
        /* block end [id:"CharacterCreation_block_131"] */
        /* block comment [block:"CharacterCreation_block_624" text:"Runs the get transform action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_624" category:"My Blocks" label:"get transform" shape:"command" docs:"Runs the get transform action. Values in this block are editable inputs."] */
        java.awt.geom.AffineTransform old = g.getTransform();
        /* block end [id:"CharacterCreation_block_624"] */
        /* block comment [block:"CharacterCreation_block_132" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"CharacterCreation_block_132" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try {
            /* block comment [block:"CharacterCreation_block_625" text:"Calls the drawBackground method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_625" category:"My Blocks" label:"call draw background" shape:"command" docs:"Calls the drawBackground method using its declared arguments."] */
            drawBackground(g, w, h);
            /* block end [id:"CharacterCreation_block_625"] */
            /* block comment [block:"CharacterCreation_block_626" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_626" category:"Variables" label:"set ds" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double ds = w / /* param start [id:"CharacterCreation_value_1192" type:"number"] */480.0/* param end [id:"CharacterCreation_value_1192"] */;
            /* block end [id:"CharacterCreation_block_626"] */
            // Draw boxes first, then text on top
            /* block comment [block:"CharacterCreation_block_133" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"CharacterCreation_block_133" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (phase == /* param start [id:"CharacterCreation_value_1193" type:"number"] */0/* param end [id:"CharacterCreation_value_1193"] */) {
                /* block comment [block:"CharacterCreation_block_134" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"CharacterCreation_block_134" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (titleLogo != /* param start [id:"CharacterCreation_value_1194" type:"value"] */null/* param end [id:"CharacterCreation_value_1194"] */) {
                    /* block comment [block:"CharacterCreation_block_627" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    /* block start [id:"CharacterCreation_block_627" category:"Variables" label:"set dw" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    int dw = Math.max(/* param start [id:"CharacterCreation_value_1195" type:"number"] */1/* param end [id:"CharacterCreation_value_1195"] */, (int) (titleLogo.getWidth() * ds));
                    /* block end [id:"CharacterCreation_block_627"] */
                    /* block comment [block:"CharacterCreation_block_628" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    /* block start [id:"CharacterCreation_block_628" category:"Variables" label:"set dh" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    int dh = Math.max(/* param start [id:"CharacterCreation_value_1196" type:"number"] */1/* param end [id:"CharacterCreation_value_1196"] */, (int) (titleLogo.getHeight() * ds));
                    /* block end [id:"CharacterCreation_block_628"] */
                    /* block comment [block:"CharacterCreation_block_629" text:"Calls the drawImage method using its declared arguments."] */
                    /* block start [id:"CharacterCreation_block_629" category:"My Blocks" label:"call draw image" shape:"command" docs:"Calls the drawImage method using its declared arguments."] */
                    g.drawImage(titleLogo, (w - dw) / /* param start [id:"CharacterCreation_value_1197" type:"number"] */2/* param end [id:"CharacterCreation_value_1197"] */, (int) (h * /* param start [id:"CharacterCreation_value_1198" type:"number"] */0.085/* param end [id:"CharacterCreation_value_1198"] */), dw, dh, /* param start [id:"CharacterCreation_value_1199" type:"value"] */null/* param end [id:"CharacterCreation_value_1199"] */);
                    /* block end [id:"CharacterCreation_block_629"] */
                } else {
                    /* block comment [block:"CharacterCreation_block_630" text:"Calls the drawCentered method using its declared arguments."] */
                    /* block start [id:"CharacterCreation_block_630" category:"My Blocks" label:"call draw centered" shape:"command" docs:"Calls the drawCentered method using its declared arguments."] */
                    drawCentered(g, /* param start [id:"CharacterCreation_value_1200" type:"text"] */"DNDIABLO"/* param end [id:"CharacterCreation_value_1200"] */, w / /* param start [id:"CharacterCreation_value_1201" type:"number"] */2/* param end [id:"CharacterCreation_value_1201"] */, (int) (h * /* param start [id:"CharacterCreation_value_1202" type:"number"] */0.24/* param end [id:"CharacterCreation_value_1202"] */), GOLD, fontFor((int) (/* param start [id:"CharacterCreation_value_1203" type:"number"] */24/* param end [id:"CharacterCreation_value_1203"] */ * ds), /* param start [id:"CharacterCreation_value_1204" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1204"] */));
                    /* block end [id:"CharacterCreation_block_630"] */
                }
                /* block end [id:"CharacterCreation_block_134"] */
                /* block comment [block:"CharacterCreation_block_631" text:"Calls the drawButtonBox method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_631" category:"My Blocks" label:"call draw button box" shape:"command" docs:"Calls the drawButtonBox method using its declared arguments."] */
                drawButtonBox(g, btns, /* param start [id:"CharacterCreation_value_1205" type:"text"] */"begin_title"/* param end [id:"CharacterCreation_value_1205"] */, /* param start [id:"CharacterCreation_value_1206" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1206"] */);
                /* block end [id:"CharacterCreation_block_631"] */
                /* block comment [block:"CharacterCreation_block_632" text:"Calls the drawButtonBox method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_632" category:"My Blocks" label:"call draw button box" shape:"command" docs:"Calls the drawButtonBox method using its declared arguments."] */
                drawButtonBox(g, btns, /* param start [id:"CharacterCreation_value_1207" type:"text"] */"controls_title"/* param end [id:"CharacterCreation_value_1207"] */, /* param start [id:"CharacterCreation_value_1208" type:"boolean"] */false/* param end [id:"CharacterCreation_value_1208"] */);
                /* block end [id:"CharacterCreation_block_632"] */
                /* block comment [block:"CharacterCreation_block_633" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_633" category:"Variables" label:"set controls" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                Btn controls = findBtn(btns, /* param start [id:"CharacterCreation_value_1209" type:"text"] */"controls_title"/* param end [id:"CharacterCreation_value_1209"] */);
                /* block end [id:"CharacterCreation_block_633"] */
                /* block comment [block:"CharacterCreation_block_634" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"CharacterCreation_block_634" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"CharacterCreation_block_135" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"CharacterCreation_block_135" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (controls != /* param start [id:"CharacterCreation_value_1210" type:"value"] */null/* param end [id:"CharacterCreation_value_1210"] */) drawCentered(g, /* param start [id:"CharacterCreation_value_1211" type:"text"] */"Controls"/* param end [id:"CharacterCreation_value_1211"] */, controls.x + controls.w / /* param start [id:"CharacterCreation_value_1212" type:"number"] */2/* param end [id:"CharacterCreation_value_1212"] */, controls.y + controls.h / /* param start [id:"CharacterCreation_value_1213" type:"number"] */2/* param end [id:"CharacterCreation_value_1213"] */ + /* param start [id:"CharacterCreation_value_1214" type:"number"] */4/* param end [id:"CharacterCreation_value_1214"] */, INK, fontFor((int) (/* param start [id:"CharacterCreation_value_1215" type:"number"] */10/* param end [id:"CharacterCreation_value_1215"] */ * ds), /* param start [id:"CharacterCreation_value_1216" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1216"] */));
                /* block end [id:"CharacterCreation_block_634"] */
                /* block end [id:"CharacterCreation_block_135"] */
                // texts
                /* block comment [block:"CharacterCreation_block_635" text:"Calls the drawCentered method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_635" category:"My Blocks" label:"call draw centered" shape:"command" docs:"Calls the drawCentered method using its declared arguments."] */
                drawCentered(g, /* param start [id:"CharacterCreation_value_1217" type:"text"] */"A 7-floor hybrid dungeon crawl"/* param end [id:"CharacterCreation_value_1217"] */, w / /* param start [id:"CharacterCreation_value_1218" type:"number"] */2/* param end [id:"CharacterCreation_value_1218"] */, (int) (h * /* param start [id:"CharacterCreation_value_1219" type:"number"] */0.24/* param end [id:"CharacterCreation_value_1219"] */ + /* param start [id:"CharacterCreation_value_1220" type:"number"] */30/* param end [id:"CharacterCreation_value_1220"] */ * ds), BODY, fontFor((int) (/* param start [id:"CharacterCreation_value_1221" type:"number"] */11/* param end [id:"CharacterCreation_value_1221"] */ * ds), /* param start [id:"CharacterCreation_value_1222" type:"boolean"] */false/* param end [id:"CharacterCreation_value_1222"] */));
                /* block end [id:"CharacterCreation_block_635"] */
                /* block comment [block:"CharacterCreation_block_636" text:"Calls the drawCentered method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_636" category:"My Blocks" label:"call draw centered" shape:"command" docs:"Calls the drawCentered method using its declared arguments."] */
                drawCentered(g, /* param start [id:"CharacterCreation_value_1223" type:"text"] */"WASD move  |  SPACE melee  |  1-6 spells  |  I inventory"/* param end [id:"CharacterCreation_value_1223"] */, w / /* param start [id:"CharacterCreation_value_1224" type:"number"] */2/* param end [id:"CharacterCreation_value_1224"] */, (int) (h * /* param start [id:"CharacterCreation_value_1225" type:"number"] */0.24/* param end [id:"CharacterCreation_value_1225"] */ + /* param start [id:"CharacterCreation_value_1226" type:"number"] */52/* param end [id:"CharacterCreation_value_1226"] */ * ds), SUBTLE, fontFor((int) (/* param start [id:"CharacterCreation_value_1227" type:"number"] */10/* param end [id:"CharacterCreation_value_1227"] */ * ds), /* param start [id:"CharacterCreation_value_1228" type:"boolean"] */false/* param end [id:"CharacterCreation_value_1228"] */));
                /* block end [id:"CharacterCreation_block_636"] */
                /* block comment [block:"CharacterCreation_block_637" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_637" category:"Variables" label:"set begin" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                Btn begin = findBtn(btns, /* param start [id:"CharacterCreation_value_1229" type:"text"] */"begin_title"/* param end [id:"CharacterCreation_value_1229"] */);
                /* block end [id:"CharacterCreation_block_637"] */
                /* block comment [block:"CharacterCreation_block_638" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"CharacterCreation_block_638" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"CharacterCreation_block_136" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"CharacterCreation_block_136" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (begin != /* param start [id:"CharacterCreation_value_1230" type:"value"] */null/* param end [id:"CharacterCreation_value_1230"] */) drawCentered(g, /* param start [id:"CharacterCreation_value_1231" type:"text"] */"Begin Creation"/* param end [id:"CharacterCreation_value_1231"] */, begin.x + begin.w / /* param start [id:"CharacterCreation_value_1232" type:"number"] */2/* param end [id:"CharacterCreation_value_1232"] */, begin.y + begin.h / /* param start [id:"CharacterCreation_value_1233" type:"number"] */2/* param end [id:"CharacterCreation_value_1233"] */ + /* param start [id:"CharacterCreation_value_1234" type:"number"] */5/* param end [id:"CharacterCreation_value_1234"] */, INK, fontFor((int) (/* param start [id:"CharacterCreation_value_1235" type:"number"] */13/* param end [id:"CharacterCreation_value_1235"] */ * ds), /* param start [id:"CharacterCreation_value_1236" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1236"] */));
                /* block end [id:"CharacterCreation_block_638"] */
                /* block end [id:"CharacterCreation_block_136"] */
                /* block comment [block:"CharacterCreation_block_639" text:"Calls the drawCentered method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_639" category:"My Blocks" label:"call draw centered" shape:"command" docs:"Calls the drawCentered method using its declared arguments."] */
                drawCentered(g, /* param start [id:"CharacterCreation_value_1237" type:"text"] */"Click Begin or press ENTER  |  M mutes music"/* param end [id:"CharacterCreation_value_1237"] */, w / /* param start [id:"CharacterCreation_value_1238" type:"number"] */2/* param end [id:"CharacterCreation_value_1238"] */, (int) (h * /* param start [id:"CharacterCreation_value_1239" type:"number"] */0.62/* param end [id:"CharacterCreation_value_1239"] */ + (h * /* param start [id:"CharacterCreation_value_1240" type:"number"] */0.13/* param end [id:"CharacterCreation_value_1240"] */) + /* param start [id:"CharacterCreation_value_1241" type:"number"] */22/* param end [id:"CharacterCreation_value_1241"] */ * ds), DIM, fontFor((int) (/* param start [id:"CharacterCreation_value_1242" type:"number"] */9/* param end [id:"CharacterCreation_value_1242"] */ * ds), /* param start [id:"CharacterCreation_value_1243" type:"boolean"] */false/* param end [id:"CharacterCreation_value_1243"] */));
                /* block end [id:"CharacterCreation_block_639"] */
                /* block comment [block:"CharacterCreation_block_640" text:"Calls the drawMute method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_640" category:"My Blocks" label:"call draw mute" shape:"command" docs:"Calls the drawMute method using its declared arguments."] */
                drawMute(g, w, h);
                /* block end [id:"CharacterCreation_block_640"] */
            } else /* block comment [block:"CharacterCreation_block_137" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"CharacterCreation_block_137" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (phase == /* param start [id:"CharacterCreation_value_1244" type:"number"] */4/* param end [id:"CharacterCreation_value_1244"] */) {
                /* block comment [block:"CharacterCreation_block_641" text:"Calls the drawCentered method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_641" category:"My Blocks" label:"call draw centered" shape:"command" docs:"Calls the drawCentered method using its declared arguments."] */
                drawCentered(g, /* param start [id:"CharacterCreation_value_1245" type:"text"] */"Your adventure is over"/* param end [id:"CharacterCreation_value_1245"] */, w / /* param start [id:"CharacterCreation_value_1246" type:"number"] */2/* param end [id:"CharacterCreation_value_1246"] */, (int) (h * /* param start [id:"CharacterCreation_value_1247" type:"number"] */0.40/* param end [id:"CharacterCreation_value_1247"] */), BODY, fontFor((int) (/* param start [id:"CharacterCreation_value_1248" type:"number"] */13/* param end [id:"CharacterCreation_value_1248"] */ * ds), /* param start [id:"CharacterCreation_value_1249" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1249"] */));
                /* block end [id:"CharacterCreation_block_641"] */
                /* block comment [block:"CharacterCreation_block_642" text:"Calls the drawButtonBox method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_642" category:"My Blocks" label:"call draw button box" shape:"command" docs:"Calls the drawButtonBox method using its declared arguments."] */
                drawButtonBox(g, btns, /* param start [id:"CharacterCreation_value_1250" type:"text"] */"new_character"/* param end [id:"CharacterCreation_value_1250"] */, /* param start [id:"CharacterCreation_value_1251" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1251"] */);
                /* block end [id:"CharacterCreation_block_642"] */
                /* block comment [block:"CharacterCreation_block_643" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_643" category:"Variables" label:"set restart" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                Btn restart = findBtn(btns, /* param start [id:"CharacterCreation_value_1252" type:"text"] */"new_character"/* param end [id:"CharacterCreation_value_1252"] */);
                /* block end [id:"CharacterCreation_block_643"] */
                /* block comment [block:"CharacterCreation_block_644" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"CharacterCreation_block_644" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"CharacterCreation_block_138" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"CharacterCreation_block_138" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (restart != /* param start [id:"CharacterCreation_value_1253" type:"value"] */null/* param end [id:"CharacterCreation_value_1253"] */) drawCentered(g, /* param start [id:"CharacterCreation_value_1254" type:"text"] */"Make a New Character"/* param end [id:"CharacterCreation_value_1254"] */, restart.x + restart.w / /* param start [id:"CharacterCreation_value_1255" type:"number"] */2/* param end [id:"CharacterCreation_value_1255"] */, restart.y + restart.h / /* param start [id:"CharacterCreation_value_1256" type:"number"] */2/* param end [id:"CharacterCreation_value_1256"] */ + /* param start [id:"CharacterCreation_value_1257" type:"number"] */5/* param end [id:"CharacterCreation_value_1257"] */, INK, fontFor((int) (/* param start [id:"CharacterCreation_value_1258" type:"number"] */12/* param end [id:"CharacterCreation_value_1258"] */ * ds), /* param start [id:"CharacterCreation_value_1259" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1259"] */));
                /* block end [id:"CharacterCreation_block_644"] */
                /* block end [id:"CharacterCreation_block_138"] */
            }
            /* block end [id:"CharacterCreation_block_133"] */ else /* block comment [block:"CharacterCreation_block_139" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"CharacterCreation_block_139" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (phase == /* param start [id:"CharacterCreation_value_1260" type:"number"] */1/* param end [id:"CharacterCreation_value_1260"] */) {
                /* block comment [block:"CharacterCreation_block_645" text:"Runs the for action. Values in this block are editable inputs."] */
                /* block start [id:"CharacterCreation_block_645" category:"My Blocks" label:"for" shape:"command" docs:"Runs the for action. Values in this block are editable inputs."] */
                /* block comment [block:"CharacterCreation_block_140" text:"Repeats the nested operations for each loop turn."] */
                /* block start [id:"CharacterCreation_block_140" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
                for (Btn b : btns) /* block comment [block:"CharacterCreation_block_141" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"CharacterCreation_block_141" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (b.kind == KIND_CARD) drawCardBox(g, b, b.idx == classIndex);
                /* block end [id:"CharacterCreation_block_141"] */
                /* block end [id:"CharacterCreation_block_645"] */
                /* block end [id:"CharacterCreation_block_140"] */
                /* block comment [block:"CharacterCreation_block_646" text:"Calls the panel method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_646" category:"My Blocks" label:"call panel" shape:"command" docs:"Calls the panel method using its declared arguments."] */
                panel(g, classPanelX, classPanelY, classPanelW, classPanelH);
                /* block end [id:"CharacterCreation_block_646"] */
                /* block comment [block:"CharacterCreation_block_647" text:"Calls the drawButtonBox method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_647" category:"My Blocks" label:"call draw button box" shape:"command" docs:"Calls the drawButtonBox method using its declared arguments."] */
                drawButtonBox(g, btns, /* param start [id:"CharacterCreation_value_1261" type:"text"] */"prev"/* param end [id:"CharacterCreation_value_1261"] */, /* param start [id:"CharacterCreation_value_1262" type:"boolean"] */false/* param end [id:"CharacterCreation_value_1262"] */);
                /* block end [id:"CharacterCreation_block_647"] */ /* block comment [block:"CharacterCreation_block_648" text:"Calls the drawButtonBox method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_648" category:"My Blocks" label:"call draw button box" shape:"command" docs:"Calls the drawButtonBox method using its declared arguments."] */
                drawButtonBox(g, btns, /* param start [id:"CharacterCreation_value_1263" type:"text"] */"next"/* param end [id:"CharacterCreation_value_1263"] */, /* param start [id:"CharacterCreation_value_1264" type:"boolean"] */false/* param end [id:"CharacterCreation_value_1264"] */);
                /* block end [id:"CharacterCreation_block_648"] */ /* block comment [block:"CharacterCreation_block_649" text:"Calls the drawButtonBox method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_649" category:"My Blocks" label:"call draw button box" shape:"command" docs:"Calls the drawButtonBox method using its declared arguments."] */
                drawButtonBox(g, btns, /* param start [id:"CharacterCreation_value_1265" type:"text"] */"confirm"/* param end [id:"CharacterCreation_value_1265"] */, /* param start [id:"CharacterCreation_value_1266" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1266"] */);
                /* block end [id:"CharacterCreation_block_649"] */
                /* block comment [block:"CharacterCreation_block_650" text:"Calls the drawFieldBox method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_650" category:"My Blocks" label:"call draw field box" shape:"command" docs:"Calls the drawFieldBox method using its declared arguments."] */
                drawFieldBox(g, btns, w);
                /* block end [id:"CharacterCreation_block_650"] */
                // texts
                /* block comment [block:"CharacterCreation_block_651" text:"Calls the drawCentered method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_651" category:"My Blocks" label:"call draw centered" shape:"command" docs:"Calls the drawCentered method using its declared arguments."] */
                drawCentered(g, /* param start [id:"CharacterCreation_value_1267" type:"text"] */"CHOOSE YOUR CLASS"/* param end [id:"CharacterCreation_value_1267"] */, w / /* param start [id:"CharacterCreation_value_1268" type:"number"] */2/* param end [id:"CharacterCreation_value_1268"] */, (int) (h * /* param start [id:"CharacterCreation_value_1269" type:"number"] */0.09/* param end [id:"CharacterCreation_value_1269"] */ + /* param start [id:"CharacterCreation_value_1270" type:"number"] */5/* param end [id:"CharacterCreation_value_1270"] */), GOLD, fontFor((int) (/* param start [id:"CharacterCreation_value_1271" type:"number"] */15/* param end [id:"CharacterCreation_value_1271"] */ * ds), /* param start [id:"CharacterCreation_value_1272" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1272"] */));
                /* block end [id:"CharacterCreation_block_651"] */
                /* block comment [block:"CharacterCreation_block_652" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_652" category:"Variables" label:"set name" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                Btn name = findBtn(btns, /* param start [id:"CharacterCreation_value_1273" type:"text"] */"name"/* param end [id:"CharacterCreation_value_1273"] */);
                /* block end [id:"CharacterCreation_block_652"] */
                /* block comment [block:"CharacterCreation_block_142" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"CharacterCreation_block_142" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (name != /* param start [id:"CharacterCreation_value_1274" type:"value"] */null/* param end [id:"CharacterCreation_value_1274"] */) {
                    /* block comment [block:"CharacterCreation_block_653" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    /* block start [id:"CharacterCreation_block_653" category:"Variables" label:"set labY" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    int labY = name.y + name.h / /* param start [id:"CharacterCreation_value_1275" type:"number"] */2/* param end [id:"CharacterCreation_value_1275"] */ + /* param start [id:"CharacterCreation_value_1276" type:"number"] */5/* param end [id:"CharacterCreation_value_1276"] */;
                    /* block end [id:"CharacterCreation_block_653"] */
                    /* block comment [block:"CharacterCreation_block_654" text:"Calls the drawText method using its declared arguments."] */
                    /* block start [id:"CharacterCreation_block_654" category:"My Blocks" label:"call draw text" shape:"command" docs:"Calls the drawText method using its declared arguments."] */
                    drawText(g, /* param start [id:"CharacterCreation_value_1277" type:"text"] */"HERO:"/* param end [id:"CharacterCreation_value_1277"] */, name.x - /* param start [id:"CharacterCreation_value_1278" type:"number"] */50/* param end [id:"CharacterCreation_value_1278"] */, labY, LABEL_GREY, fontFor((int) (/* param start [id:"CharacterCreation_value_1279" type:"number"] */10/* param end [id:"CharacterCreation_value_1279"] */ * ds), /* param start [id:"CharacterCreation_value_1280" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1280"] */));
                    /* block end [id:"CharacterCreation_block_654"] */
                    /* block comment [block:"CharacterCreation_block_655" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    /* block start [id:"CharacterCreation_block_655" category:"Variables" label:"set fieldText" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    String fieldText = heroName.length() == /* param start [id:"CharacterCreation_value_1281" type:"number"] */0/* param end [id:"CharacterCreation_value_1281"] */ ? /* param start [id:"CharacterCreation_value_1282" type:"text"] */"Hero"/* param end [id:"CharacterCreation_value_1282"] */ : heroName;
                    /* block end [id:"CharacterCreation_block_655"] */
                    /* block comment [block:"CharacterCreation_block_656" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    /* block start [id:"CharacterCreation_block_656" category:"Variables" label:"set fc" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    Color fc = heroName.length() == /* param start [id:"CharacterCreation_value_1283" type:"number"] */0/* param end [id:"CharacterCreation_value_1283"] */ ? FIELD_EMPTY : INK;
                    /* block end [id:"CharacterCreation_block_656"] */
                    /* block comment [block:"CharacterCreation_block_657" text:"Calls the drawText method using its declared arguments."] */
                    /* block start [id:"CharacterCreation_block_657" category:"My Blocks" label:"call draw text" shape:"command" docs:"Calls the drawText method using its declared arguments."] */
                    drawText(g, fieldText, name.x + /* param start [id:"CharacterCreation_value_1284" type:"number"] */10/* param end [id:"CharacterCreation_value_1284"] */, labY, fc, fontFor((int) (/* param start [id:"CharacterCreation_value_1285" type:"number"] */11/* param end [id:"CharacterCreation_value_1285"] */ * ds), /* param start [id:"CharacterCreation_value_1286" type:"boolean"] */false/* param end [id:"CharacterCreation_value_1286"] */));
                    /* block end [id:"CharacterCreation_block_657"] */
                    /* block comment [block:"CharacterCreation_block_658" text:"Calls the drawCentered method using its declared arguments."] */
                    /* block start [id:"CharacterCreation_block_658" category:"My Blocks" label:"call draw centered" shape:"command" docs:"Calls the drawCentered method using its declared arguments."] */
                    drawCentered(g, nameFocused ? /* param start [id:"CharacterCreation_value_1287" type:"text"] */"Typing name - A/D moves disabled, arrows still work (ESC unfocuses)"/* param end [id:"CharacterCreation_value_1287"] */ : /* param start [id:"CharacterCreation_value_1288" type:"text"] */"Click the name box to rename your hero"/* param end [id:"CharacterCreation_value_1288"] */, w / /* param start [id:"CharacterCreation_value_1289" type:"number"] */2/* param end [id:"CharacterCreation_value_1289"] */, name.y + name.h + (int) (/* param start [id:"CharacterCreation_value_1290" type:"number"] */15/* param end [id:"CharacterCreation_value_1290"] */ * ds), DIM, fontFor((int) (/* param start [id:"CharacterCreation_value_1291" type:"number"] */9/* param end [id:"CharacterCreation_value_1291"] */ * ds), /* param start [id:"CharacterCreation_value_1292" type:"boolean"] */false/* param end [id:"CharacterCreation_value_1292"] */));
                    /* block end [id:"CharacterCreation_block_658"] */
                }
                /* block end [id:"CharacterCreation_block_142"] */
                /* block comment [block:"CharacterCreation_block_143" text:"Repeats the nested operations for each loop turn."] */
                /* block start [id:"CharacterCreation_block_143" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
                for (Btn b : btns) /* block comment [block:"CharacterCreation_block_144" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"CharacterCreation_block_144" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (b.kind == KIND_CARD) {
                    /* block comment [block:"CharacterCreation_block_659" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    /* block start [id:"CharacterCreation_block_659" category:"Variables" label:"set c" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    Color c = b.idx == classIndex ? SEL_TEXT : INK;
                    /* block end [id:"CharacterCreation_block_659"] */
                    /* block comment [block:"CharacterCreation_block_660" text:"Calls the drawCentered method using its declared arguments."] */
                    /* block start [id:"CharacterCreation_block_660" category:"My Blocks" label:"call draw centered" shape:"command" docs:"Calls the drawCentered method using its declared arguments."] */
                    drawCentered(g, displayName(CLASS_ORDER[b.idx]), b.x + b.w / /* param start [id:"CharacterCreation_value_1293" type:"number"] */2/* param end [id:"CharacterCreation_value_1293"] */, b.y + b.h / /* param start [id:"CharacterCreation_value_1294" type:"number"] */2/* param end [id:"CharacterCreation_value_1294"] */ + /* param start [id:"CharacterCreation_value_1295" type:"number"] */4/* param end [id:"CharacterCreation_value_1295"] */, c, fontFor((int) (/* param start [id:"CharacterCreation_value_1296" type:"number"] */10/* param end [id:"CharacterCreation_value_1296"] */ * ds), /* param start [id:"CharacterCreation_value_1297" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1297"] */));
                    /* block end [id:"CharacterCreation_block_660"] */
                }
                /* block end [id:"CharacterCreation_block_144"] */
                /* block end [id:"CharacterCreation_block_143"] */
                /* block comment [block:"CharacterCreation_block_661" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_661" category:"Variables" label:"set cls" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                String cls = CLASS_ORDER[classIndex];
                /* block end [id:"CharacterCreation_block_661"] */
                /* block comment [block:"CharacterCreation_block_662" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_662" category:"Variables" label:"set hit" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int hit = PlayerStats.hitDieOf(cls);
                /* block end [id:"CharacterCreation_block_662"] */
                /* block comment [block:"CharacterCreation_block_663" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_663" category:"Variables" label:"set caster" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                boolean caster = PlayerStats.isCasterOf(cls);
                /* block end [id:"CharacterCreation_block_663"] */
                /* block comment [block:"CharacterCreation_block_664" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_664" category:"Variables" label:"set abil" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                String abil = PlayerStats.spellAbilityOf(cls);
                /* block end [id:"CharacterCreation_block_664"] */
                /* block comment [block:"CharacterCreation_block_665" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_665" category:"Variables" label:"set tx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int tx = classPanelX + (int) (/* param start [id:"CharacterCreation_value_1298" type:"number"] */14/* param end [id:"CharacterCreation_value_1298"] */ * ds);
                /* block end [id:"CharacterCreation_block_665"] */ /* block comment [block:"CharacterCreation_block_666" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_666" category:"Variables" label:"set ty" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int ty = classPanelY + (int) (/* param start [id:"CharacterCreation_value_1299" type:"number"] */18/* param end [id:"CharacterCreation_value_1299"] */ * ds);
                /* block end [id:"CharacterCreation_block_666"] */
                /* block comment [block:"CharacterCreation_block_667" text:"Calls the drawText method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_667" category:"My Blocks" label:"call draw text" shape:"command" docs:"Calls the drawText method using its declared arguments."] */
                drawText(g, displayName(cls) + /* param start [id:"CharacterCreation_value_1300" type:"text"] */"  (d"/* param end [id:"CharacterCreation_value_1300"] */ + hit + /* param start [id:"CharacterCreation_value_1301" type:"text"] */")"/* param end [id:"CharacterCreation_value_1301"] */, tx, ty, GOLD, fontFor((int) (/* param start [id:"CharacterCreation_value_1302" type:"number"] */12/* param end [id:"CharacterCreation_value_1302"] */ * ds), /* param start [id:"CharacterCreation_value_1303" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1303"] */));
                /* block end [id:"CharacterCreation_block_667"] */ /* block comment [block:"CharacterCreation_block_668" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"CharacterCreation_block_668" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                ty += (int) (/* param start [id:"CharacterCreation_value_1304" type:"number"] */18/* param end [id:"CharacterCreation_value_1304"] */ * ds);
                /* block end [id:"CharacterCreation_block_668"] */
                /* block comment [block:"CharacterCreation_block_669" text:"Calls the drawText method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_669" category:"My Blocks" label:"call draw text" shape:"command" docs:"Calls the drawText method using its declared arguments."] */
                drawText(g, caster ? /* param start [id:"CharacterCreation_value_1305" type:"text"] */"Caster ("/* param end [id:"CharacterCreation_value_1305"] */ + abil + /* param start [id:"CharacterCreation_value_1306" type:"text"] */")"/* param end [id:"CharacterCreation_value_1306"] */ : /* param start [id:"CharacterCreation_value_1307" type:"text"] */"Martial"/* param end [id:"CharacterCreation_value_1307"] */, tx, ty, INK, fontFor((int) (/* param start [id:"CharacterCreation_value_1308" type:"number"] */10/* param end [id:"CharacterCreation_value_1308"] */ * ds), /* param start [id:"CharacterCreation_value_1309" type:"boolean"] */false/* param end [id:"CharacterCreation_value_1309"] */));
                /* block end [id:"CharacterCreation_block_669"] */ /* block comment [block:"CharacterCreation_block_670" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"CharacterCreation_block_670" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                ty += (int) (/* param start [id:"CharacterCreation_value_1310" type:"number"] */16/* param end [id:"CharacterCreation_value_1310"] */ * ds);
                /* block end [id:"CharacterCreation_block_670"] */
                /* block comment [block:"CharacterCreation_block_671" text:"Calls the drawText method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_671" category:"My Blocks" label:"call draw text" shape:"command" docs:"Calls the drawText method using its declared arguments."] */
                drawText(g, /* param start [id:"CharacterCreation_value_1311" type:"text"] */"Saves: STR/DEX etc."/* param end [id:"CharacterCreation_value_1311"] */, tx, ty, DETAIL_GREY, fontFor((int) (/* param start [id:"CharacterCreation_value_1312" type:"number"] */10/* param end [id:"CharacterCreation_value_1312"] */ * ds), /* param start [id:"CharacterCreation_value_1313" type:"boolean"] */false/* param end [id:"CharacterCreation_value_1313"] */));
                /* block end [id:"CharacterCreation_block_671"] */ /* block comment [block:"CharacterCreation_block_672" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"CharacterCreation_block_672" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                ty += (int) (/* param start [id:"CharacterCreation_value_1314" type:"number"] */16/* param end [id:"CharacterCreation_value_1314"] */ * ds);
                /* block end [id:"CharacterCreation_block_672"] */
                /* block comment [block:"CharacterCreation_block_673" text:"Calls the drawText method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_673" category:"My Blocks" label:"call draw text" shape:"command" docs:"Calls the drawText method using its declared arguments."] */
                drawText(g, /* param start [id:"CharacterCreation_value_1315" type:"text"] */"Kit: "/* param end [id:"CharacterCreation_value_1315"] */ + starterKit(cls), tx, ty, DETAIL_GREY, fontFor((int) (/* param start [id:"CharacterCreation_value_1316" type:"number"] */10/* param end [id:"CharacterCreation_value_1316"] */ * ds), /* param start [id:"CharacterCreation_value_1317" type:"boolean"] */false/* param end [id:"CharacterCreation_value_1317"] */));
                /* block end [id:"CharacterCreation_block_673"] */
                /* block comment [block:"CharacterCreation_block_674" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_674" category:"Variables" label:"set prev" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                Btn prev = findBtn(btns, /* param start [id:"CharacterCreation_value_1318" type:"text"] */"prev"/* param end [id:"CharacterCreation_value_1318"] */);
                /* block end [id:"CharacterCreation_block_674"] */ /* block comment [block:"CharacterCreation_block_675" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"CharacterCreation_block_675" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"CharacterCreation_block_145" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"CharacterCreation_block_145" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (prev != /* param start [id:"CharacterCreation_value_1319" type:"value"] */null/* param end [id:"CharacterCreation_value_1319"] */) drawCentered(g, /* param start [id:"CharacterCreation_value_1320" type:"text"] */"<"/* param end [id:"CharacterCreation_value_1320"] */, prev.x + prev.w / /* param start [id:"CharacterCreation_value_1321" type:"number"] */2/* param end [id:"CharacterCreation_value_1321"] */, prev.y + prev.h / /* param start [id:"CharacterCreation_value_1322" type:"number"] */2/* param end [id:"CharacterCreation_value_1322"] */ + /* param start [id:"CharacterCreation_value_1323" type:"number"] */4/* param end [id:"CharacterCreation_value_1323"] */, INK, fontFor((int) (/* param start [id:"CharacterCreation_value_1324" type:"number"] */12/* param end [id:"CharacterCreation_value_1324"] */ * ds), /* param start [id:"CharacterCreation_value_1325" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1325"] */));
                /* block end [id:"CharacterCreation_block_675"] */
                /* block end [id:"CharacterCreation_block_145"] */
                /* block comment [block:"CharacterCreation_block_676" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_676" category:"Variables" label:"set next" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                Btn next = findBtn(btns, /* param start [id:"CharacterCreation_value_1326" type:"text"] */"next"/* param end [id:"CharacterCreation_value_1326"] */);
                /* block end [id:"CharacterCreation_block_676"] */ /* block comment [block:"CharacterCreation_block_677" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"CharacterCreation_block_677" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"CharacterCreation_block_146" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"CharacterCreation_block_146" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (next != /* param start [id:"CharacterCreation_value_1327" type:"value"] */null/* param end [id:"CharacterCreation_value_1327"] */) drawCentered(g, /* param start [id:"CharacterCreation_value_1328" type:"text"] */">"/* param end [id:"CharacterCreation_value_1328"] */, next.x + next.w / /* param start [id:"CharacterCreation_value_1329" type:"number"] */2/* param end [id:"CharacterCreation_value_1329"] */, next.y + next.h / /* param start [id:"CharacterCreation_value_1330" type:"number"] */2/* param end [id:"CharacterCreation_value_1330"] */ + /* param start [id:"CharacterCreation_value_1331" type:"number"] */4/* param end [id:"CharacterCreation_value_1331"] */, INK, fontFor((int) (/* param start [id:"CharacterCreation_value_1332" type:"number"] */12/* param end [id:"CharacterCreation_value_1332"] */ * ds), /* param start [id:"CharacterCreation_value_1333" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1333"] */));
                /* block end [id:"CharacterCreation_block_677"] */
                /* block end [id:"CharacterCreation_block_146"] */
                /* block comment [block:"CharacterCreation_block_678" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_678" category:"Variables" label:"set conf" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                Btn conf = findBtn(btns, /* param start [id:"CharacterCreation_value_1334" type:"text"] */"confirm"/* param end [id:"CharacterCreation_value_1334"] */);
                /* block end [id:"CharacterCreation_block_678"] */ /* block comment [block:"CharacterCreation_block_679" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"CharacterCreation_block_679" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"CharacterCreation_block_147" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"CharacterCreation_block_147" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (conf != /* param start [id:"CharacterCreation_value_1335" type:"value"] */null/* param end [id:"CharacterCreation_value_1335"] */) drawCentered(g, /* param start [id:"CharacterCreation_value_1336" type:"text"] */"Confirm >"/* param end [id:"CharacterCreation_value_1336"] */, conf.x + conf.w / /* param start [id:"CharacterCreation_value_1337" type:"number"] */2/* param end [id:"CharacterCreation_value_1337"] */, conf.y + conf.h / /* param start [id:"CharacterCreation_value_1338" type:"number"] */2/* param end [id:"CharacterCreation_value_1338"] */ + /* param start [id:"CharacterCreation_value_1339" type:"number"] */4/* param end [id:"CharacterCreation_value_1339"] */, INK, fontFor((int) (/* param start [id:"CharacterCreation_value_1340" type:"number"] */11/* param end [id:"CharacterCreation_value_1340"] */ * ds), /* param start [id:"CharacterCreation_value_1341" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1341"] */));
                /* block end [id:"CharacterCreation_block_679"] */
                /* block end [id:"CharacterCreation_block_147"] */
                /* block comment [block:"CharacterCreation_block_680" text:"Calls the drawCentered method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_680" category:"My Blocks" label:"call draw centered" shape:"command" docs:"Calls the drawCentered method using its declared arguments."] */
                drawCentered(g, /* param start [id:"CharacterCreation_value_1342" type:"text"] */"Click a card or A/D + ENTER"/* param end [id:"CharacterCreation_value_1342"] */, w / /* param start [id:"CharacterCreation_value_1343" type:"number"] */2/* param end [id:"CharacterCreation_value_1343"] */, (int) (h * /* param start [id:"CharacterCreation_value_1344" type:"number"] */0.895/* param end [id:"CharacterCreation_value_1344"] */ - /* param start [id:"CharacterCreation_value_1345" type:"number"] */8/* param end [id:"CharacterCreation_value_1345"] */), DIM, fontFor((int) (/* param start [id:"CharacterCreation_value_1346" type:"number"] */9/* param end [id:"CharacterCreation_value_1346"] */ * ds), /* param start [id:"CharacterCreation_value_1347" type:"boolean"] */false/* param end [id:"CharacterCreation_value_1347"] */));
                /* block end [id:"CharacterCreation_block_680"] */
                /* block comment [block:"CharacterCreation_block_681" text:"Calls the drawMute method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_681" category:"My Blocks" label:"call draw mute" shape:"command" docs:"Calls the drawMute method using its declared arguments."] */
                drawMute(g, w, h);
                /* block end [id:"CharacterCreation_block_681"] */
            }
            /* block end [id:"CharacterCreation_block_137"] */ else {
                /* block comment [block:"CharacterCreation_block_148" text:"Repeats the nested operations for each loop turn."] */
                /* block start [id:"CharacterCreation_block_148" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
                for (Btn b : btns) {
                    /* block comment [block:"CharacterCreation_block_682" text:"Runs the if action. Values in this block are editable inputs."] */
                    /* block start [id:"CharacterCreation_block_682" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                    /* block comment [block:"CharacterCreation_block_149" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"CharacterCreation_block_149" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (b.kind == KIND_TAB) drawTabBox(g, b, b.idx == method);
                    /* block end [id:"CharacterCreation_block_682"] */
                    /* block comment [block:"CharacterCreation_block_683" text:"Calls the drawStepperBox method using its declared arguments."] */
                    /* block start [id:"CharacterCreation_block_683" category:"My Blocks" label:"call draw stepper box" shape:"command" docs:"Calls the drawStepperBox method using its declared arguments."] */
                    else /* block comment [block:"CharacterCreation_block_150" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"CharacterCreation_block_150" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (b.kind == KIND_STEPPER) drawStepperBox(g, b);
                    /* block end [id:"CharacterCreation_block_150"] */
                    /* block end [id:"CharacterCreation_block_683"] */
                    /* block end [id:"CharacterCreation_block_149"] */
                }
                /* block end [id:"CharacterCreation_block_148"] */
                /* block comment [block:"CharacterCreation_block_151" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"CharacterCreation_block_151" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (method == /* param start [id:"CharacterCreation_value_1348" type:"number"] */2/* param end [id:"CharacterCreation_value_1348"] */ && sel >= /* param start [id:"CharacterCreation_value_1349" type:"number"] */0/* param end [id:"CharacterCreation_value_1349"] */ && sel < /* param start [id:"CharacterCreation_value_1350" type:"number"] */6/* param end [id:"CharacterCreation_value_1350"] */ && pbRowH != /* param start [id:"CharacterCreation_value_1351" type:"number"] */0/* param end [id:"CharacterCreation_value_1351"] */) {
                    /* block comment [block:"CharacterCreation_block_684" text:"Runs the set color action. Values in this block are editable inputs."] */
                    /* block start [id:"CharacterCreation_block_684" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
                    g.setColor(ROW_SEL);
                    /* block end [id:"CharacterCreation_block_684"] */ /* block comment [block:"CharacterCreation_block_685" text:"Calls the fillRoundRect method using its declared arguments."] */
                    /* block start [id:"CharacterCreation_block_685" category:"My Blocks" label:"call fill round rect" shape:"command" docs:"Calls the fillRoundRect method using its declared arguments."] */
                    g.fillRoundRect((int) (w * /* param start [id:"CharacterCreation_value_1352" type:"number"] */0.06/* param end [id:"CharacterCreation_value_1352"] */), pbRowY0 + sel * pbRowH, (int) (w * /* param start [id:"CharacterCreation_value_1353" type:"number"] */0.88/* param end [id:"CharacterCreation_value_1353"] */), pbRowH, /* param start [id:"CharacterCreation_value_1354" type:"number"] */8/* param end [id:"CharacterCreation_value_1354"] */, /* param start [id:"CharacterCreation_value_1355" type:"number"] */8/* param end [id:"CharacterCreation_value_1355"] */);
                    /* block end [id:"CharacterCreation_block_685"] */
                }
                /* block end [id:"CharacterCreation_block_151"] */
                /* block comment [block:"CharacterCreation_block_686" text:"Calls the drawButtonBox method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_686" category:"My Blocks" label:"call draw button box" shape:"command" docs:"Calls the drawButtonBox method using its declared arguments."] */
                drawButtonBox(g, btns, /* param start [id:"CharacterCreation_value_1356" type:"text"] */"reroll"/* param end [id:"CharacterCreation_value_1356"] */, /* param start [id:"CharacterCreation_value_1357" type:"boolean"] */false/* param end [id:"CharacterCreation_value_1357"] */);
                /* block end [id:"CharacterCreation_block_686"] */ /* block comment [block:"CharacterCreation_block_687" text:"Calls the drawButtonBox method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_687" category:"My Blocks" label:"call draw button box" shape:"command" docs:"Calls the drawButtonBox method using its declared arguments."] */
                drawButtonBox(g, btns, /* param start [id:"CharacterCreation_value_1358" type:"text"] */"back"/* param end [id:"CharacterCreation_value_1358"] */, /* param start [id:"CharacterCreation_value_1359" type:"boolean"] */false/* param end [id:"CharacterCreation_value_1359"] */);
                /* block end [id:"CharacterCreation_block_687"] */ /* block comment [block:"CharacterCreation_block_688" text:"Calls the drawButtonBox method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_688" category:"My Blocks" label:"call draw button box" shape:"command" docs:"Calls the drawButtonBox method using its declared arguments."] */
                drawButtonBox(g, btns, /* param start [id:"CharacterCreation_value_1360" type:"text"] */"begin_game"/* param end [id:"CharacterCreation_value_1360"] */, /* param start [id:"CharacterCreation_value_1361" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1361"] */);
                /* block end [id:"CharacterCreation_block_688"] */
                // texts
                /* block comment [block:"CharacterCreation_block_689" text:"Calls the drawCentered method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_689" category:"My Blocks" label:"call draw centered" shape:"command" docs:"Calls the drawCentered method using its declared arguments."] */
                drawCentered(g, /* param start [id:"CharacterCreation_value_1362" type:"text"] */"ABILITY SCORES - "/* param end [id:"CharacterCreation_value_1362"] */ + displayName(CLASS_ORDER[classIndex]), w / /* param start [id:"CharacterCreation_value_1363" type:"number"] */2/* param end [id:"CharacterCreation_value_1363"] */, (int) (h * /* param start [id:"CharacterCreation_value_1364" type:"number"] */0.075/* param end [id:"CharacterCreation_value_1364"] */ + /* param start [id:"CharacterCreation_value_1365" type:"number"] */5/* param end [id:"CharacterCreation_value_1365"] */), GOLD, fontFor((int) (/* param start [id:"CharacterCreation_value_1366" type:"number"] */13/* param end [id:"CharacterCreation_value_1366"] */ * ds), /* param start [id:"CharacterCreation_value_1367" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1367"] */));
                /* block end [id:"CharacterCreation_block_689"] */
                String[] tabNames = {/* param start [id:"CharacterCreation_value_1368" type:"text"] */"Standard"/* param end [id:"CharacterCreation_value_1368"] */, /* param start [id:"CharacterCreation_value_1369" type:"text"] */"Rolled 4d6"/* param end [id:"CharacterCreation_value_1369"] */, /* param start [id:"CharacterCreation_value_1370" type:"text"] */"Point Buy"/* param end [id:"CharacterCreation_value_1370"] */};
                /* block comment [block:"CharacterCreation_block_152" text:"Repeats the nested operations for each loop turn."] */
                /* block start [id:"CharacterCreation_block_152" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
                for (int i = /* param start [id:"CharacterCreation_value_1371" type:"number"] */0/* param end [id:"CharacterCreation_value_1371"] */; i < /* param start [id:"CharacterCreation_value_1372" type:"number"] */3/* param end [id:"CharacterCreation_value_1372"] */; i++) {
                    /* block comment [block:"CharacterCreation_block_690" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    /* block start [id:"CharacterCreation_block_690" category:"Variables" label:"set t" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    Btn t = findBtn(btns, /* param start [id:"CharacterCreation_value_1373" type:"text"] */"tab_"/* param end [id:"CharacterCreation_value_1373"] */ + i);
                    /* block end [id:"CharacterCreation_block_690"] */ /* block comment [block:"CharacterCreation_block_691" text:"Runs the if action. Values in this block are editable inputs."] */
                    /* block start [id:"CharacterCreation_block_691" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                    /* block comment [block:"CharacterCreation_block_153" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"CharacterCreation_block_153" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (t == /* param start [id:"CharacterCreation_value_1374" type:"value"] */null/* param end [id:"CharacterCreation_value_1374"] */) continue;
                    /* block end [id:"CharacterCreation_block_691"] */
                    /* block end [id:"CharacterCreation_block_153"] */
                    /* block comment [block:"CharacterCreation_block_692" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    /* block start [id:"CharacterCreation_block_692" category:"Variables" label:"set c" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    Color c = method == i ? SEL_TEXT : INK;
                    /* block end [id:"CharacterCreation_block_692"] */
                    /* block comment [block:"CharacterCreation_block_693" text:"Calls the drawCentered method using its declared arguments."] */
                    /* block start [id:"CharacterCreation_block_693" category:"My Blocks" label:"call draw centered" shape:"command" docs:"Calls the drawCentered method using its declared arguments."] */
                    drawCentered(g, tabNames[i], t.x + t.w / /* param start [id:"CharacterCreation_value_1375" type:"number"] */2/* param end [id:"CharacterCreation_value_1375"] */, t.y + t.h / /* param start [id:"CharacterCreation_value_1376" type:"number"] */2/* param end [id:"CharacterCreation_value_1376"] */ + /* param start [id:"CharacterCreation_value_1377" type:"number"] */4/* param end [id:"CharacterCreation_value_1377"] */, c, fontFor((int) (/* param start [id:"CharacterCreation_value_1378" type:"number"] */10/* param end [id:"CharacterCreation_value_1378"] */ * ds), /* param start [id:"CharacterCreation_value_1379" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1379"] */));
                    /* block end [id:"CharacterCreation_block_693"] */
                }
                /* block end [id:"CharacterCreation_block_152"] */
                /* block comment [block:"CharacterCreation_block_694" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"CharacterCreation_block_694" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"CharacterCreation_block_154" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"CharacterCreation_block_154" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (method == /* param start [id:"CharacterCreation_value_1380" type:"number"] */2/* param end [id:"CharacterCreation_value_1380"] */) drawCentered(g, /* param start [id:"CharacterCreation_value_1381" type:"text"] */"Points left: "/* param end [id:"CharacterCreation_value_1381"] */ + pbRemaining() + /* param start [id:"CharacterCreation_value_1382" type:"text"] */"/"/* param end [id:"CharacterCreation_value_1382"] */ + PB_BUDGET + /* param start [id:"CharacterCreation_value_1383" type:"text"] */"   (click +/- or arrows)"/* param end [id:"CharacterCreation_value_1383"] */, (int) (w * /* param start [id:"CharacterCreation_value_1384" type:"number"] */0.36/* param end [id:"CharacterCreation_value_1384"] */), pbRowY0 - (int) (/* param start [id:"CharacterCreation_value_1385" type:"number"] */12/* param end [id:"CharacterCreation_value_1385"] */ * ds), pbRemaining() < /* param start [id:"CharacterCreation_value_1386" type:"number"] */0/* param end [id:"CharacterCreation_value_1386"] */ ? BAD : GOLD, fontFor((int) (/* param start [id:"CharacterCreation_value_1387" type:"number"] */10/* param end [id:"CharacterCreation_value_1387"] */ * ds), /* param start [id:"CharacterCreation_value_1388" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1388"] */));
                /* block end [id:"CharacterCreation_block_694"] */
                /* block end [id:"CharacterCreation_block_154"] */
                String[] names = {/* param start [id:"CharacterCreation_value_1389" type:"text"] */"STR"/* param end [id:"CharacterCreation_value_1389"] */, /* param start [id:"CharacterCreation_value_1390" type:"text"] */"DEX"/* param end [id:"CharacterCreation_value_1390"] */, /* param start [id:"CharacterCreation_value_1391" type:"text"] */"CON"/* param end [id:"CharacterCreation_value_1391"] */, /* param start [id:"CharacterCreation_value_1392" type:"text"] */"INT"/* param end [id:"CharacterCreation_value_1392"] */, /* param start [id:"CharacterCreation_value_1393" type:"text"] */"WIS"/* param end [id:"CharacterCreation_value_1393"] */, /* param start [id:"CharacterCreation_value_1394" type:"text"] */"CHA"/* param end [id:"CharacterCreation_value_1394"] */};
                /* block comment [block:"CharacterCreation_block_155" text:"Repeats the nested operations for each loop turn."] */
                /* block start [id:"CharacterCreation_block_155" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
                for (int i = /* param start [id:"CharacterCreation_value_1395" type:"number"] */0/* param end [id:"CharacterCreation_value_1395"] */; i < /* param start [id:"CharacterCreation_value_1396" type:"number"] */6/* param end [id:"CharacterCreation_value_1396"] */; i++) {
                    /* block comment [block:"CharacterCreation_block_695" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    /* block start [id:"CharacterCreation_block_695" category:"Variables" label:"set ry" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    int ry = pbRowY0 + i * pbRowH;
                    /* block end [id:"CharacterCreation_block_695"] */ /* block comment [block:"CharacterCreation_block_696" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    /* block start [id:"CharacterCreation_block_696" category:"Variables" label:"set mod" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    int mod = (int) Math.floor((scores[i] - /* param start [id:"CharacterCreation_value_1397" type:"number"] */10/* param end [id:"CharacterCreation_value_1397"] */) / /* param start [id:"CharacterCreation_value_1398" type:"number"] */2.0/* param end [id:"CharacterCreation_value_1398"] */);
                    /* block end [id:"CharacterCreation_block_696"] */
                    /* block comment [block:"CharacterCreation_block_697" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    /* block start [id:"CharacterCreation_block_697" category:"Variables" label:"set rowSel" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    boolean rowSel = method == /* param start [id:"CharacterCreation_value_1399" type:"number"] */2/* param end [id:"CharacterCreation_value_1399"] */ && i == sel;
                    /* block end [id:"CharacterCreation_block_697"] */
                    /* block comment [block:"CharacterCreation_block_698" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    /* block start [id:"CharacterCreation_block_698" category:"Variables" label:"set row" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    String row = names[i] + /* param start [id:"CharacterCreation_value_1400" type:"text"] */": "/* param end [id:"CharacterCreation_value_1400"] */ + scores[i] + /* param start [id:"CharacterCreation_value_1401" type:"text"] */"  (mod "/* param end [id:"CharacterCreation_value_1401"] */ + (mod >= /* param start [id:"CharacterCreation_value_1402" type:"number"] */0/* param end [id:"CharacterCreation_value_1402"] */ ? /* param start [id:"CharacterCreation_value_1403" type:"text"] */"+"/* param end [id:"CharacterCreation_value_1403"] */ : /* param start [id:"CharacterCreation_value_1404" type:"text"] */""/* param end [id:"CharacterCreation_value_1404"] */) + mod + /* param start [id:"CharacterCreation_value_1405" type:"text"] */")"/* param end [id:"CharacterCreation_value_1405"] */;
                    /* block end [id:"CharacterCreation_block_698"] */
                    /* block comment [block:"CharacterCreation_block_699" text:"Runs the if action. Values in this block are editable inputs."] */
                    /* block start [id:"CharacterCreation_block_699" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                    /* block comment [block:"CharacterCreation_block_156" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"CharacterCreation_block_156" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (rowSel) row = /* param start [id:"CharacterCreation_value_1406" type:"text"] */"> "/* param end [id:"CharacterCreation_value_1406"] */ + row + /* param start [id:"CharacterCreation_value_1407" type:"text"] */" <"/* param end [id:"CharacterCreation_value_1407"] */;
                    /* block end [id:"CharacterCreation_block_699"] */
                    /* block end [id:"CharacterCreation_block_156"] */
                    /* block comment [block:"CharacterCreation_block_700" text:"Calls the drawText method using its declared arguments."] */
                    /* block start [id:"CharacterCreation_block_700" category:"My Blocks" label:"call draw text" shape:"command" docs:"Calls the drawText method using its declared arguments."] */
                    drawText(g, row, (int) (w * /* param start [id:"CharacterCreation_value_1408" type:"number"] */0.10/* param end [id:"CharacterCreation_value_1408"] */), ry + pbRowH / /* param start [id:"CharacterCreation_value_1409" type:"number"] */2/* param end [id:"CharacterCreation_value_1409"] */ + /* param start [id:"CharacterCreation_value_1410" type:"number"] */5/* param end [id:"CharacterCreation_value_1410"] */, rowSel ? SEL_TEXT : INK, fontFor((int) (/* param start [id:"CharacterCreation_value_1411" type:"number"] */11/* param end [id:"CharacterCreation_value_1411"] */ * ds), /* param start [id:"CharacterCreation_value_1412" type:"boolean"] */false/* param end [id:"CharacterCreation_value_1412"] */));
                    /* block end [id:"CharacterCreation_block_700"] */
                    /* block comment [block:"CharacterCreation_block_157" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"CharacterCreation_block_157" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (method == /* param start [id:"CharacterCreation_value_1413" type:"number"] */2/* param end [id:"CharacterCreation_value_1413"] */) {
                        /* block comment [block:"CharacterCreation_block_701" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                        /* block start [id:"CharacterCreation_block_701" category:"Variables" label:"set minus" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                        Btn minus = findBtn(btns, /* param start [id:"CharacterCreation_value_1414" type:"text"] */"pb_minus_"/* param end [id:"CharacterCreation_value_1414"] */ + i);
                        /* block end [id:"CharacterCreation_block_701"] */ /* block comment [block:"CharacterCreation_block_702" text:"Runs the if action. Values in this block are editable inputs."] */
                        /* block start [id:"CharacterCreation_block_702" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                        /* block comment [block:"CharacterCreation_block_158" text:"Checks a Boolean condition and runs the body when it is true."] */
                        /* block start [id:"CharacterCreation_block_158" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                        if (minus != /* param start [id:"CharacterCreation_value_1415" type:"value"] */null/* param end [id:"CharacterCreation_value_1415"] */) drawCentered(g, /* param start [id:"CharacterCreation_value_1416" type:"text"] */"-"/* param end [id:"CharacterCreation_value_1416"] */, minus.x + minus.w / /* param start [id:"CharacterCreation_value_1417" type:"number"] */2/* param end [id:"CharacterCreation_value_1417"] */, minus.y + minus.h / /* param start [id:"CharacterCreation_value_1418" type:"number"] */2/* param end [id:"CharacterCreation_value_1418"] */ + /* param start [id:"CharacterCreation_value_1419" type:"number"] */4/* param end [id:"CharacterCreation_value_1419"] */, minus.enabled ? INK : STEP_OFF_TEXT, fontFor((int) (/* param start [id:"CharacterCreation_value_1420" type:"number"] */11/* param end [id:"CharacterCreation_value_1420"] */ * ds), /* param start [id:"CharacterCreation_value_1421" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1421"] */));
                        /* block end [id:"CharacterCreation_block_702"] */
                        /* block end [id:"CharacterCreation_block_158"] */
                        /* block comment [block:"CharacterCreation_block_703" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                        /* block start [id:"CharacterCreation_block_703" category:"Variables" label:"set plus" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                        Btn plus = findBtn(btns, /* param start [id:"CharacterCreation_value_1422" type:"text"] */"pb_plus_"/* param end [id:"CharacterCreation_value_1422"] */ + i);
                        /* block end [id:"CharacterCreation_block_703"] */ /* block comment [block:"CharacterCreation_block_704" text:"Runs the if action. Values in this block are editable inputs."] */
                        /* block start [id:"CharacterCreation_block_704" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                        /* block comment [block:"CharacterCreation_block_159" text:"Checks a Boolean condition and runs the body when it is true."] */
                        /* block start [id:"CharacterCreation_block_159" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                        if (plus != /* param start [id:"CharacterCreation_value_1423" type:"value"] */null/* param end [id:"CharacterCreation_value_1423"] */) drawCentered(g, /* param start [id:"CharacterCreation_value_1424" type:"text"] */"+"/* param end [id:"CharacterCreation_value_1424"] */, plus.x + plus.w / /* param start [id:"CharacterCreation_value_1425" type:"number"] */2/* param end [id:"CharacterCreation_value_1425"] */, plus.y + plus.h / /* param start [id:"CharacterCreation_value_1426" type:"number"] */2/* param end [id:"CharacterCreation_value_1426"] */ + /* param start [id:"CharacterCreation_value_1427" type:"number"] */4/* param end [id:"CharacterCreation_value_1427"] */, plus.enabled ? INK : STEP_OFF_TEXT, fontFor((int) (/* param start [id:"CharacterCreation_value_1428" type:"number"] */11/* param end [id:"CharacterCreation_value_1428"] */ * ds), /* param start [id:"CharacterCreation_value_1429" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1429"] */));
                        /* block end [id:"CharacterCreation_block_704"] */
                        /* block end [id:"CharacterCreation_block_159"] */
                    }
                    /* block end [id:"CharacterCreation_block_157"] */
                }
                /* block end [id:"CharacterCreation_block_155"] */
                /* block comment [block:"CharacterCreation_block_705" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_705" category:"Variables" label:"set methodLine" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                String methodLine = method == /* param start [id:"CharacterCreation_value_1430" type:"number"] */0/* param end [id:"CharacterCreation_value_1430"] */ ? /* param start [id:"CharacterCreation_value_1431" type:"text"] */"Standard array [15, 14, 13, 12, 10, 8]"/* param end [id:"CharacterCreation_value_1431"] */ : method == /* param start [id:"CharacterCreation_value_1432" type:"number"] */1/* param end [id:"CharacterCreation_value_1432"] */ ? /* param start [id:"CharacterCreation_value_1433" type:"text"] */"Rolled 4d6, drop lowest  |  R or Reroll for new spread"/* param end [id:"CharacterCreation_value_1433"] */ : /* param start [id:"CharacterCreation_value_1434" type:"text"] */"Point buy: 27 pts, all start at 8, max 15"/* param end [id:"CharacterCreation_value_1434"] */;
                /* block end [id:"CharacterCreation_block_705"] */
                /* block comment [block:"CharacterCreation_block_706" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_706" category:"Variables" label:"set reroll" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                Btn reroll = findBtn(btns, /* param start [id:"CharacterCreation_value_1435" type:"text"] */"reroll"/* param end [id:"CharacterCreation_value_1435"] */);
                /* block end [id:"CharacterCreation_block_706"] */
                /* block comment [block:"CharacterCreation_block_707" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_707" category:"Variables" label:"set hintBottom" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int hintBottom = reroll != /* param start [id:"CharacterCreation_value_1436" type:"value"] */null/* param end [id:"CharacterCreation_value_1436"] */ ? reroll.y - /* param start [id:"CharacterCreation_value_1437" type:"number"] */6/* param end [id:"CharacterCreation_value_1437"] */ : (int) (h * /* param start [id:"CharacterCreation_value_1438" type:"number"] */0.815/* param end [id:"CharacterCreation_value_1438"] */);
                /* block end [id:"CharacterCreation_block_707"] */
                /* block comment [block:"CharacterCreation_block_708" text:"Calls the drawCentered method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_708" category:"My Blocks" label:"call draw centered" shape:"command" docs:"Calls the drawCentered method using its declared arguments."] */
                drawCentered(g, methodLine, w / /* param start [id:"CharacterCreation_value_1439" type:"number"] */2/* param end [id:"CharacterCreation_value_1439"] */, hintBottom, FAINT, fontFor((int) (/* param start [id:"CharacterCreation_value_1440" type:"number"] */9/* param end [id:"CharacterCreation_value_1440"] */ * ds), /* param start [id:"CharacterCreation_value_1441" type:"boolean"] */false/* param end [id:"CharacterCreation_value_1441"] */));
                /* block end [id:"CharacterCreation_block_708"] */
                /* block comment [block:"CharacterCreation_block_709" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"CharacterCreation_block_709" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"CharacterCreation_block_160" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"CharacterCreation_block_160" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (reroll != /* param start [id:"CharacterCreation_value_1442" type:"value"] */null/* param end [id:"CharacterCreation_value_1442"] */) drawCentered(g, /* param start [id:"CharacterCreation_value_1443" type:"text"] */"Reroll (R)"/* param end [id:"CharacterCreation_value_1443"] */, reroll.x + reroll.w / /* param start [id:"CharacterCreation_value_1444" type:"number"] */2/* param end [id:"CharacterCreation_value_1444"] */, reroll.y + reroll.h / /* param start [id:"CharacterCreation_value_1445" type:"number"] */2/* param end [id:"CharacterCreation_value_1445"] */ + /* param start [id:"CharacterCreation_value_1446" type:"number"] */4/* param end [id:"CharacterCreation_value_1446"] */, INK, fontFor((int) (/* param start [id:"CharacterCreation_value_1447" type:"number"] */11/* param end [id:"CharacterCreation_value_1447"] */ * ds), /* param start [id:"CharacterCreation_value_1448" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1448"] */));
                /* block end [id:"CharacterCreation_block_709"] */
                /* block end [id:"CharacterCreation_block_160"] */
                /* block comment [block:"CharacterCreation_block_710" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_710" category:"Variables" label:"set back" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                Btn back = findBtn(btns, /* param start [id:"CharacterCreation_value_1449" type:"text"] */"back"/* param end [id:"CharacterCreation_value_1449"] */);
                /* block end [id:"CharacterCreation_block_710"] */ /* block comment [block:"CharacterCreation_block_711" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"CharacterCreation_block_711" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"CharacterCreation_block_161" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"CharacterCreation_block_161" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (back != /* param start [id:"CharacterCreation_value_1450" type:"value"] */null/* param end [id:"CharacterCreation_value_1450"] */) drawCentered(g, /* param start [id:"CharacterCreation_value_1451" type:"text"] */"< Back"/* param end [id:"CharacterCreation_value_1451"] */, back.x + back.w / /* param start [id:"CharacterCreation_value_1452" type:"number"] */2/* param end [id:"CharacterCreation_value_1452"] */, back.y + back.h / /* param start [id:"CharacterCreation_value_1453" type:"number"] */2/* param end [id:"CharacterCreation_value_1453"] */ + /* param start [id:"CharacterCreation_value_1454" type:"number"] */4/* param end [id:"CharacterCreation_value_1454"] */, INK, fontFor((int) (/* param start [id:"CharacterCreation_value_1455" type:"number"] */11/* param end [id:"CharacterCreation_value_1455"] */ * ds), /* param start [id:"CharacterCreation_value_1456" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1456"] */));
                /* block end [id:"CharacterCreation_block_711"] */
                /* block end [id:"CharacterCreation_block_161"] */
                /* block comment [block:"CharacterCreation_block_712" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_712" category:"Variables" label:"set begin" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                Btn begin = findBtn(btns, /* param start [id:"CharacterCreation_value_1457" type:"text"] */"begin_game"/* param end [id:"CharacterCreation_value_1457"] */);
                /* block end [id:"CharacterCreation_block_712"] */ /* block comment [block:"CharacterCreation_block_713" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"CharacterCreation_block_713" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"CharacterCreation_block_162" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"CharacterCreation_block_162" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (begin != /* param start [id:"CharacterCreation_value_1458" type:"value"] */null/* param end [id:"CharacterCreation_value_1458"] */) drawCentered(g, /* param start [id:"CharacterCreation_value_1459" type:"text"] */"Descend v"/* param end [id:"CharacterCreation_value_1459"] */, begin.x + begin.w / /* param start [id:"CharacterCreation_value_1460" type:"number"] */2/* param end [id:"CharacterCreation_value_1460"] */, begin.y + begin.h / /* param start [id:"CharacterCreation_value_1461" type:"number"] */2/* param end [id:"CharacterCreation_value_1461"] */ + /* param start [id:"CharacterCreation_value_1462" type:"number"] */4/* param end [id:"CharacterCreation_value_1462"] */, INK, fontFor((int) (/* param start [id:"CharacterCreation_value_1463" type:"number"] */11/* param end [id:"CharacterCreation_value_1463"] */ * ds), /* param start [id:"CharacterCreation_value_1464" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1464"] */));
                /* block end [id:"CharacterCreation_block_713"] */
                /* block end [id:"CharacterCreation_block_162"] */
                /* block comment [block:"CharacterCreation_block_714" text:"Calls the drawCentered method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_714" category:"My Blocks" label:"call draw centered" shape:"command" docs:"Calls the drawCentered method using its declared arguments."] */
                drawCentered(g, /* param start [id:"CharacterCreation_value_1465" type:"text"] */"1/2/3 method  |  BKSP back  |  ENTER descend"/* param end [id:"CharacterCreation_value_1465"] */, w / /* param start [id:"CharacterCreation_value_1466" type:"number"] */2/* param end [id:"CharacterCreation_value_1466"] */, (int) (h * /* param start [id:"CharacterCreation_value_1467" type:"number"] */0.895/* param end [id:"CharacterCreation_value_1467"] */ - /* param start [id:"CharacterCreation_value_1468" type:"number"] */8/* param end [id:"CharacterCreation_value_1468"] */), DIM, fontFor((int) (/* param start [id:"CharacterCreation_value_1469" type:"number"] */9/* param end [id:"CharacterCreation_value_1469"] */ * ds), /* param start [id:"CharacterCreation_value_1470" type:"boolean"] */false/* param end [id:"CharacterCreation_value_1470"] */));
                /* block end [id:"CharacterCreation_block_714"] */
                /* block comment [block:"CharacterCreation_block_715" text:"Calls the drawMute method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_715" category:"My Blocks" label:"call draw mute" shape:"command" docs:"Calls the drawMute method using its declared arguments."] */
                drawMute(g, w, h);
                /* block end [id:"CharacterCreation_block_715"] */
            }
            /* block end [id:"CharacterCreation_block_139"] */
        }
        /* block end [id:"CharacterCreation_block_132"] */ finally { /* block comment [block:"CharacterCreation_block_716" text:"Runs the set transform action. Values in this block are editable inputs."] */
            /* block start [id:"CharacterCreation_block_716" category:"Variables" label:"set transform" shape:"command" docs:"Runs the set transform action. Values in this block are editable inputs."] */
            g.setTransform(old);
            /* block end [id:"CharacterCreation_block_716"] */ }
        /* block comment [block:"CharacterCreation_block_717" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_717" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        dirty = /* param start [id:"CharacterCreation_value_1471" type:"boolean"] */false/* param end [id:"CharacterCreation_value_1471"] */;
        /* block end [id:"CharacterCreation_block_717"] */
    }

    /** Controls/death modal drawn over the live stage while world updates are paused. */
    private void drawModalOverlay(Graphics2D g, int w, int h, List<Btn> btns, boolean death) {
        /* block comment [block:"CharacterCreation_block_718" text:"Runs the get transform action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_718" category:"My Blocks" label:"get transform" shape:"command" docs:"Runs the get transform action. Values in this block are editable inputs."] */
        java.awt.geom.AffineTransform old = g.getTransform();
        /* block end [id:"CharacterCreation_block_718"] */
        /* block comment [block:"CharacterCreation_block_163" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"CharacterCreation_block_163" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try {
            /* block comment [block:"CharacterCreation_block_719" text:"Runs the set color action. Values in this block are editable inputs."] */
            /* block start [id:"CharacterCreation_block_719" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
            g.setColor(new Color(/* param start [id:"CharacterCreation_value_1472" type:"number"] */0/* param end [id:"CharacterCreation_value_1472"] */, /* param start [id:"CharacterCreation_value_1473" type:"number"] */0/* param end [id:"CharacterCreation_value_1473"] */, /* param start [id:"CharacterCreation_value_1474" type:"number"] */0/* param end [id:"CharacterCreation_value_1474"] */, /* param start [id:"CharacterCreation_value_1475" type:"number"] */185/* param end [id:"CharacterCreation_value_1475"] */));
            /* block end [id:"CharacterCreation_block_719"] */ /* block comment [block:"CharacterCreation_block_720" text:"Calls the fillRect method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_720" category:"My Blocks" label:"call fill rect" shape:"command" docs:"Calls the fillRect method using its declared arguments."] */
            g.fillRect(/* param start [id:"CharacterCreation_value_1476" type:"number"] */0/* param end [id:"CharacterCreation_value_1476"] */, /* param start [id:"CharacterCreation_value_1477" type:"number"] */0/* param end [id:"CharacterCreation_value_1477"] */, w, h);
            /* block end [id:"CharacterCreation_block_720"] */
            /* block comment [block:"CharacterCreation_block_721" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_721" category:"Variables" label:"set px" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int px = (int) (w * /* param start [id:"CharacterCreation_value_1478" type:"number"] */0.12/* param end [id:"CharacterCreation_value_1478"] */), py = (int) (h * /* param start [id:"CharacterCreation_value_1479" type:"number"] */0.10/* param end [id:"CharacterCreation_value_1479"] */);
            /* block end [id:"CharacterCreation_block_721"] */
            /* block comment [block:"CharacterCreation_block_722" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_722" category:"Variables" label:"set pw" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int pw = (int) (w * /* param start [id:"CharacterCreation_value_1480" type:"number"] */0.76/* param end [id:"CharacterCreation_value_1480"] */), ph = (int) (h * /* param start [id:"CharacterCreation_value_1481" type:"number"] */0.78/* param end [id:"CharacterCreation_value_1481"] */);
            /* block end [id:"CharacterCreation_block_722"] */
            /* block comment [block:"CharacterCreation_block_723" text:"Calls the panel method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_723" category:"My Blocks" label:"call panel" shape:"command" docs:"Calls the panel method using its declared arguments."] */
            panel(g, px, py, pw, ph);
            /* block end [id:"CharacterCreation_block_723"] */
            /* block comment [block:"CharacterCreation_block_724" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_724" category:"Variables" label:"set ds" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double ds = w / /* param start [id:"CharacterCreation_value_1482" type:"number"] */480.0/* param end [id:"CharacterCreation_value_1482"] */;
            /* block end [id:"CharacterCreation_block_724"] */
            /* block comment [block:"CharacterCreation_block_725" text:"Calls the drawCentered method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_725" category:"My Blocks" label:"call draw centered" shape:"command" docs:"Calls the drawCentered method using its declared arguments."] */
            drawCentered(g, death ? /* param start [id:"CharacterCreation_value_1483" type:"text"] */"YOU DIED"/* param end [id:"CharacterCreation_value_1483"] */ : /* param start [id:"CharacterCreation_value_1484" type:"text"] */"CONTROLS"/* param end [id:"CharacterCreation_value_1484"] */, w / /* param start [id:"CharacterCreation_value_1485" type:"number"] */2/* param end [id:"CharacterCreation_value_1485"] */, py + (int) (/* param start [id:"CharacterCreation_value_1486" type:"number"] */30/* param end [id:"CharacterCreation_value_1486"] */ * ds), GOLD, fontFor((int) (/* param start [id:"CharacterCreation_value_1487" type:"number"] */18/* param end [id:"CharacterCreation_value_1487"] */ * ds), /* param start [id:"CharacterCreation_value_1488" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1488"] */));
            /* block end [id:"CharacterCreation_block_725"] */
            String[] lines = death
            ? new String[]{/* param start [id:"CharacterCreation_value_1489" type:"text"] */"Your run has ended."/* param end [id:"CharacterCreation_value_1489"] */, /* param start [id:"CharacterCreation_value_1490" type:"text"] */"Choose Make a New Character to restart on Floor 1."/* param end [id:"CharacterCreation_value_1490"] */, /* param start [id:"CharacterCreation_value_1491" type:"text"] */"Press ENTER to return to character creation."/* param end [id:"CharacterCreation_value_1491"] */}
            : new String[]{/* param start [id:"CharacterCreation_value_1492" type:"text"] */"W A S D   Move"/* param end [id:"CharacterCreation_value_1492"] */, /* param start [id:"CharacterCreation_value_1493" type:"text"] */"SPACE       Melee attack"/* param end [id:"CharacterCreation_value_1493"] */, /* param start [id:"CharacterCreation_value_1494" type:"text"] */"1 - 6           Cast the six equipped spells"/* param end [id:"CharacterCreation_value_1494"] */, /* param start [id:"CharacterCreation_value_1495" type:"text"] */"I                    Open or close inventory"/* param end [id:"CharacterCreation_value_1495"] */, /* param start [id:"CharacterCreation_value_1496" type:"text"] */"UP / DOWN  Select a bag item"/* param end [id:"CharacterCreation_value_1496"] */, /* param start [id:"CharacterCreation_value_1497" type:"text"] */"ENTER        Equip the selected item"/* param end [id:"CharacterCreation_value_1497"] */, /* param start [id:"CharacterCreation_value_1498" type:"text"] */"H                    Show or hide this controls card"/* param end [id:"CharacterCreation_value_1498"] */, /* param start [id:"CharacterCreation_value_1499" type:"text"] */"M                   Toggle music"/* param end [id:"CharacterCreation_value_1499"] */};
            /* block comment [block:"CharacterCreation_block_726" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_726" category:"Variables" label:"set y" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int y = py + (int) (/* param start [id:"CharacterCreation_value_1500" type:"number"] */68/* param end [id:"CharacterCreation_value_1500"] */ * ds);
            /* block end [id:"CharacterCreation_block_726"] */
            /* block comment [block:"CharacterCreation_block_164" text:"Repeats the nested operations for each loop turn."] */
            /* block start [id:"CharacterCreation_block_164" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
            for (String line : lines) {
                /* block comment [block:"CharacterCreation_block_727" text:"Calls the drawText method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_727" category:"My Blocks" label:"call draw text" shape:"command" docs:"Calls the drawText method using its declared arguments."] */
                drawText(g, line, px + (int) (/* param start [id:"CharacterCreation_value_1501" type:"number"] */24/* param end [id:"CharacterCreation_value_1501"] */ * ds), y, INK, fontFor((int) (/* param start [id:"CharacterCreation_value_1502" type:"number"] */11/* param end [id:"CharacterCreation_value_1502"] */ * ds), /* param start [id:"CharacterCreation_value_1503" type:"boolean"] */false/* param end [id:"CharacterCreation_value_1503"] */));
                /* block end [id:"CharacterCreation_block_727"] */
                /* block comment [block:"CharacterCreation_block_728" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"CharacterCreation_block_728" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                y += (int) (death ? /* param start [id:"CharacterCreation_value_1504" type:"number"] */25/* param end [id:"CharacterCreation_value_1504"] */ * ds : /* param start [id:"CharacterCreation_value_1505" type:"number"] */31/* param end [id:"CharacterCreation_value_1505"] */ * ds);
                /* block end [id:"CharacterCreation_block_728"] */
            }
            /* block end [id:"CharacterCreation_block_164"] */
            /* block comment [block:"CharacterCreation_block_165" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"CharacterCreation_block_165" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (death) {
                /* block comment [block:"CharacterCreation_block_729" text:"Calls the drawButtonBox method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_729" category:"My Blocks" label:"call draw button box" shape:"command" docs:"Calls the drawButtonBox method using its declared arguments."] */
                drawButtonBox(g, btns, /* param start [id:"CharacterCreation_value_1506" type:"text"] */"new_character"/* param end [id:"CharacterCreation_value_1506"] */, /* param start [id:"CharacterCreation_value_1507" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1507"] */);
                /* block end [id:"CharacterCreation_block_729"] */
                /* block comment [block:"CharacterCreation_block_730" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_730" category:"Variables" label:"set restart" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                Btn restart = findBtn(btns, /* param start [id:"CharacterCreation_value_1508" type:"text"] */"new_character"/* param end [id:"CharacterCreation_value_1508"] */);
                /* block end [id:"CharacterCreation_block_730"] */
                /* block comment [block:"CharacterCreation_block_731" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"CharacterCreation_block_731" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"CharacterCreation_block_166" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"CharacterCreation_block_166" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (restart != /* param start [id:"CharacterCreation_value_1509" type:"value"] */null/* param end [id:"CharacterCreation_value_1509"] */) drawCentered(g, /* param start [id:"CharacterCreation_value_1510" type:"text"] */"Make a New Character"/* param end [id:"CharacterCreation_value_1510"] */, restart.x + restart.w / /* param start [id:"CharacterCreation_value_1511" type:"number"] */2/* param end [id:"CharacterCreation_value_1511"] */, restart.y + restart.h / /* param start [id:"CharacterCreation_value_1512" type:"number"] */2/* param end [id:"CharacterCreation_value_1512"] */ + /* param start [id:"CharacterCreation_value_1513" type:"number"] */5/* param end [id:"CharacterCreation_value_1513"] */, INK, fontFor((int) (/* param start [id:"CharacterCreation_value_1514" type:"number"] */12/* param end [id:"CharacterCreation_value_1514"] */ * ds), /* param start [id:"CharacterCreation_value_1515" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1515"] */));
                /* block end [id:"CharacterCreation_block_731"] */
                /* block end [id:"CharacterCreation_block_166"] */
            } else {
                /* block comment [block:"CharacterCreation_block_732" text:"Calls the drawButtonBox method using its declared arguments."] */
                /* block start [id:"CharacterCreation_block_732" category:"My Blocks" label:"call draw button box" shape:"command" docs:"Calls the drawButtonBox method using its declared arguments."] */
                drawButtonBox(g, btns, /* param start [id:"CharacterCreation_value_1516" type:"text"] */"controls_close"/* param end [id:"CharacterCreation_value_1516"] */, /* param start [id:"CharacterCreation_value_1517" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1517"] */);
                /* block end [id:"CharacterCreation_block_732"] */
                /* block comment [block:"CharacterCreation_block_733" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"CharacterCreation_block_733" category:"Variables" label:"set close" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                Btn close = findBtn(btns, /* param start [id:"CharacterCreation_value_1518" type:"text"] */"controls_close"/* param end [id:"CharacterCreation_value_1518"] */);
                /* block end [id:"CharacterCreation_block_733"] */
                /* block comment [block:"CharacterCreation_block_734" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"CharacterCreation_block_734" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"CharacterCreation_block_167" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"CharacterCreation_block_167" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (close != /* param start [id:"CharacterCreation_value_1519" type:"value"] */null/* param end [id:"CharacterCreation_value_1519"] */) drawCentered(g, /* param start [id:"CharacterCreation_value_1520" type:"text"] */"Close (H)"/* param end [id:"CharacterCreation_value_1520"] */, close.x + close.w / /* param start [id:"CharacterCreation_value_1521" type:"number"] */2/* param end [id:"CharacterCreation_value_1521"] */, close.y + close.h / /* param start [id:"CharacterCreation_value_1522" type:"number"] */2/* param end [id:"CharacterCreation_value_1522"] */ + /* param start [id:"CharacterCreation_value_1523" type:"number"] */4/* param end [id:"CharacterCreation_value_1523"] */, INK, fontFor((int) (/* param start [id:"CharacterCreation_value_1524" type:"number"] */10/* param end [id:"CharacterCreation_value_1524"] */ * ds), /* param start [id:"CharacterCreation_value_1525" type:"boolean"] */true/* param end [id:"CharacterCreation_value_1525"] */));
                /* block end [id:"CharacterCreation_block_734"] */
                /* block end [id:"CharacterCreation_block_167"] */
            }
            /* block end [id:"CharacterCreation_block_165"] */
        }
        /* block end [id:"CharacterCreation_block_163"] */ finally { /* block comment [block:"CharacterCreation_block_735" text:"Runs the set transform action. Values in this block are editable inputs."] */
            /* block start [id:"CharacterCreation_block_735" category:"Variables" label:"set transform" shape:"command" docs:"Runs the set transform action. Values in this block are editable inputs."] */
            g.setTransform(old);
            /* block end [id:"CharacterCreation_block_735"] */ }
        /* block comment [block:"CharacterCreation_block_736" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"CharacterCreation_block_736" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        dirty = /* param start [id:"CharacterCreation_value_1526" type:"boolean"] */false/* param end [id:"CharacterCreation_value_1526"] */;
        /* block end [id:"CharacterCreation_block_736"] */
    }

    private void drawMute(Graphics2D g, int w, int h) {
        /* block comment [block:"CharacterCreation_block_737" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_737" category:"Variables" label:"set s" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        String s = /* param start [id:"CharacterCreation_value_1527" type:"text"] */"M: mute ("/* param end [id:"CharacterCreation_value_1527"] */ + (MusicDirector.isMuted() ? /* param start [id:"CharacterCreation_value_1528" type:"text"] */"off"/* param end [id:"CharacterCreation_value_1528"] */ : /* param start [id:"CharacterCreation_value_1529" type:"text"] */"on"/* param end [id:"CharacterCreation_value_1529"] */) + /* param start [id:"CharacterCreation_value_1530" type:"text"] */")"/* param end [id:"CharacterCreation_value_1530"] */;
        /* block end [id:"CharacterCreation_block_737"] */
        /* block comment [block:"CharacterCreation_block_738" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_738" category:"Variables" label:"set f" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        BitmapFont.Style f = fontFor(Math.max(/* param start [id:"CharacterCreation_value_1531" type:"number"] */7/* param end [id:"CharacterCreation_value_1531"] */, (int) (/* param start [id:"CharacterCreation_value_1532" type:"number"] */9/* param end [id:"CharacterCreation_value_1532"] */ * (w / /* param start [id:"CharacterCreation_value_1533" type:"number"] */480.0/* param end [id:"CharacterCreation_value_1533"] */))), /* param start [id:"CharacterCreation_value_1534" type:"boolean"] */false/* param end [id:"CharacterCreation_value_1534"] */);
        /* block end [id:"CharacterCreation_block_738"] */
        /* block comment [block:"CharacterCreation_block_740" text:"Runs the set color action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_740" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
        g.setColor(DIM);
        /* block end [id:"CharacterCreation_block_740"] */
        /* block comment [block:"CharacterCreation_block_741" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_741" category:"Variables" label:"set tw" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int tw = BitmapFont.width(s, f);
        /* block end [id:"CharacterCreation_block_741"] */
        /* block comment [block:"CharacterCreation_block_742" text:"Calls the draw string method using its declared arguments."] */
        /* block start [id:"CharacterCreation_block_742" category:"My Blocks" label:"call draw string" shape:"command" docs:"Calls the draw string method using its declared arguments."] */
        BitmapFont.draw(g, s, w - /* param start [id:"CharacterCreation_value_1535" type:"number"] */3/* param end [id:"CharacterCreation_value_1535"] */ - tw, /* param start [id:"CharacterCreation_value_1536" type:"number"] */12/* param end [id:"CharacterCreation_value_1536"] */, f);
        /* block end [id:"CharacterCreation_block_742"] */
    }

    private void drawBackground(Graphics2D g, int w, int h) {
        /* block comment [block:"CharacterCreation_block_743" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_743" category:"Variables" label:"set bg" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        BufferedImage bg = phase == /* param start [id:"CharacterCreation_value_1537" type:"number"] */0/* param end [id:"CharacterCreation_value_1537"] */ ? bgTitle : phase == /* param start [id:"CharacterCreation_value_1538" type:"number"] */1/* param end [id:"CharacterCreation_value_1538"] */ ? bgClass : bgStats;
        /* block end [id:"CharacterCreation_block_743"] */
        /* block comment [block:"CharacterCreation_block_168" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_168" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (bg != /* param start [id:"CharacterCreation_value_1539" type:"value"] */null/* param end [id:"CharacterCreation_value_1539"] */) { /* block comment [block:"CharacterCreation_block_744" text:"Calls the drawImage method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_744" category:"My Blocks" label:"call draw image" shape:"command" docs:"Calls the drawImage method using its declared arguments."] */
            g.drawImage(bg, /* param start [id:"CharacterCreation_value_1540" type:"number"] */0/* param end [id:"CharacterCreation_value_1540"] */, /* param start [id:"CharacterCreation_value_1541" type:"number"] */0/* param end [id:"CharacterCreation_value_1541"] */, w, h, /* param start [id:"CharacterCreation_value_1542" type:"value"] */null/* param end [id:"CharacterCreation_value_1542"] */);
            /* block end [id:"CharacterCreation_block_744"] */ /* block comment [block:"CharacterCreation_block_745" text:"Runs the set color action. Values in this block are editable inputs."] */
            /* block start [id:"CharacterCreation_block_745" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
            g.setColor(VEIL);
            /* block end [id:"CharacterCreation_block_745"] */ /* block comment [block:"CharacterCreation_block_746" text:"Calls the fillRect method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_746" category:"My Blocks" label:"call fill rect" shape:"command" docs:"Calls the fillRect method using its declared arguments."] */
            g.fillRect(/* param start [id:"CharacterCreation_value_1543" type:"number"] */0/* param end [id:"CharacterCreation_value_1543"] */, /* param start [id:"CharacterCreation_value_1544" type:"number"] */0/* param end [id:"CharacterCreation_value_1544"] */, w, h);
            /* block end [id:"CharacterCreation_block_746"] */ /* block comment [block:"CharacterCreation_block_747" text:"Returns a value or ends the current method."] */
            /* block start [id:"CharacterCreation_block_747" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"CharacterCreation_block_747"] */ }
        /* block end [id:"CharacterCreation_block_168"] */
        /* block comment [block:"CharacterCreation_block_169" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_169" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (gradCache == /* param start [id:"CharacterCreation_value_1545" type:"value"] */null/* param end [id:"CharacterCreation_value_1545"] */ || gradH != h) { /* block comment [block:"CharacterCreation_block_748" text:"Runs the gradient paint action. Values in this block are editable inputs."] */
            /* block start [id:"CharacterCreation_block_748" category:"My Blocks" label:"gradient paint" shape:"command" docs:"Runs the gradient paint action. Values in this block are editable inputs."] */
            gradCache = new GradientPaint(/* param start [id:"CharacterCreation_value_1546" type:"number"] */0/* param end [id:"CharacterCreation_value_1546"] */, /* param start [id:"CharacterCreation_value_1547" type:"number"] */0/* param end [id:"CharacterCreation_value_1547"] */, GRAD_TOP, /* param start [id:"CharacterCreation_value_1548" type:"number"] */0/* param end [id:"CharacterCreation_value_1548"] */, h, GRAD_BOT);
            /* block end [id:"CharacterCreation_block_748"] */ /* block comment [block:"CharacterCreation_block_749" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"CharacterCreation_block_749" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            gradH = h;
            /* block end [id:"CharacterCreation_block_749"] */ }
        /* block end [id:"CharacterCreation_block_169"] */
        /* block comment [block:"CharacterCreation_block_750" text:"Runs the set paint action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_750" category:"Variables" label:"set paint" shape:"command" docs:"Runs the set paint action. Values in this block are editable inputs."] */
        g.setPaint(gradCache);
        /* block end [id:"CharacterCreation_block_750"] */ /* block comment [block:"CharacterCreation_block_751" text:"Calls the fillRect method using its declared arguments."] */
        /* block start [id:"CharacterCreation_block_751" category:"My Blocks" label:"call fill rect" shape:"command" docs:"Calls the fillRect method using its declared arguments."] */
        g.fillRect(/* param start [id:"CharacterCreation_value_1549" type:"number"] */0/* param end [id:"CharacterCreation_value_1549"] */, /* param start [id:"CharacterCreation_value_1550" type:"number"] */0/* param end [id:"CharacterCreation_value_1550"] */, w, h);
        /* block end [id:"CharacterCreation_block_751"] */ /* block comment [block:"CharacterCreation_block_752" text:"Runs the set paint action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_752" category:"Variables" label:"set paint" shape:"command" docs:"Runs the set paint action. Values in this block are editable inputs."] */
        g.setPaint(/* param start [id:"CharacterCreation_value_1551" type:"value"] */null/* param end [id:"CharacterCreation_value_1551"] */);
        /* block end [id:"CharacterCreation_block_752"] */ /* block comment [block:"CharacterCreation_block_753" text:"Runs the set color action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_753" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
        g.setColor(GLOW);
        /* block end [id:"CharacterCreation_block_753"] */ /* block comment [block:"CharacterCreation_block_754" text:"Calls the fillOval method using its declared arguments."] */
        /* block start [id:"CharacterCreation_block_754" category:"My Blocks" label:"call fill oval" shape:"command" docs:"Calls the fillOval method using its declared arguments."] */
        g.fillOval(w / /* param start [id:"CharacterCreation_value_1552" type:"number"] */4/* param end [id:"CharacterCreation_value_1552"] */, -h / /* param start [id:"CharacterCreation_value_1553" type:"number"] */4/* param end [id:"CharacterCreation_value_1553"] */, w / /* param start [id:"CharacterCreation_value_1554" type:"number"] */2/* param end [id:"CharacterCreation_value_1554"] */, h / /* param start [id:"CharacterCreation_value_1555" type:"number"] */2/* param end [id:"CharacterCreation_value_1555"] */);
        /* block end [id:"CharacterCreation_block_754"] */
    }
    private static void panel(Graphics2D g, int x, int y, int w, int h) { /* block comment [block:"CharacterCreation_block_755" text:"Runs the set color action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_755" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
        g.setColor(PANEL_FILL);
        /* block end [id:"CharacterCreation_block_755"] */ /* block comment [block:"CharacterCreation_block_756" text:"Calls the fillRoundRect method using its declared arguments."] */
        /* block start [id:"CharacterCreation_block_756" category:"My Blocks" label:"call fill round rect" shape:"command" docs:"Calls the fillRoundRect method using its declared arguments."] */
        g.fillRoundRect(x, y, w, h, /* param start [id:"CharacterCreation_value_1556" type:"number"] */12/* param end [id:"CharacterCreation_value_1556"] */, /* param start [id:"CharacterCreation_value_1557" type:"number"] */12/* param end [id:"CharacterCreation_value_1557"] */);
        /* block end [id:"CharacterCreation_block_756"] */ /* block comment [block:"CharacterCreation_block_757" text:"Runs the set color action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_757" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
        g.setColor(EDGE);
        /* block end [id:"CharacterCreation_block_757"] */ /* block comment [block:"CharacterCreation_block_758" text:"Calls the drawRoundRect method using its declared arguments."] */
        /* block start [id:"CharacterCreation_block_758" category:"My Blocks" label:"call draw round rect" shape:"command" docs:"Calls the drawRoundRect method using its declared arguments."] */
        g.drawRoundRect(x, y, w, h, /* param start [id:"CharacterCreation_value_1558" type:"number"] */12/* param end [id:"CharacterCreation_value_1558"] */, /* param start [id:"CharacterCreation_value_1559" type:"number"] */12/* param end [id:"CharacterCreation_value_1559"] */);
        /* block end [id:"CharacterCreation_block_758"] */ }
    private void drawButtonBox(Graphics2D g, List<Btn> btns, String id, boolean primary) {
        /* block comment [block:"CharacterCreation_block_759" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_759" category:"Variables" label:"set b" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Btn b = findBtn(btns, id);
        /* block end [id:"CharacterCreation_block_759"] */ /* block comment [block:"CharacterCreation_block_760" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_760" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_170" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_170" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (b == /* param start [id:"CharacterCreation_value_1560" type:"value"] */null/* param end [id:"CharacterCreation_value_1560"] */) return;
        /* block end [id:"CharacterCreation_block_760"] */
        /* block end [id:"CharacterCreation_block_170"] */
        /* block comment [block:"CharacterCreation_block_761" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_761" category:"Variables" label:"set fill" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Color fill = !b.enabled ? DISABLED : primary ? (isHover(id) ? PRIMARY_HOVER : PRIMARY) : isHover(id) ? FILL_HOVER : FILL;
        /* block end [id:"CharacterCreation_block_761"] */
        /* block comment [block:"CharacterCreation_block_762" text:"Runs the set color action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_762" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
        g.setColor(fill);
        /* block end [id:"CharacterCreation_block_762"] */ /* block comment [block:"CharacterCreation_block_763" text:"Calls the fillRoundRect method using its declared arguments."] */
        /* block start [id:"CharacterCreation_block_763" category:"My Blocks" label:"call fill round rect" shape:"command" docs:"Calls the fillRoundRect method using its declared arguments."] */
        g.fillRoundRect(b.x, b.y, b.w, b.h, /* param start [id:"CharacterCreation_value_1561" type:"number"] */10/* param end [id:"CharacterCreation_value_1561"] */, /* param start [id:"CharacterCreation_value_1562" type:"number"] */10/* param end [id:"CharacterCreation_value_1562"] */);
        /* block end [id:"CharacterCreation_block_763"] */ /* block comment [block:"CharacterCreation_block_764" text:"Runs the set color action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_764" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
        g.setColor(primary ? GOLD : EDGE);
        /* block end [id:"CharacterCreation_block_764"] */ /* block comment [block:"CharacterCreation_block_765" text:"Calls the drawRoundRect method using its declared arguments."] */
        /* block start [id:"CharacterCreation_block_765" category:"My Blocks" label:"call draw round rect" shape:"command" docs:"Calls the drawRoundRect method using its declared arguments."] */
        g.drawRoundRect(b.x, b.y, b.w, b.h, /* param start [id:"CharacterCreation_value_1563" type:"number"] */10/* param end [id:"CharacterCreation_value_1563"] */, /* param start [id:"CharacterCreation_value_1564" type:"number"] */10/* param end [id:"CharacterCreation_value_1564"] */);
        /* block end [id:"CharacterCreation_block_765"] */
    }
    private void drawTabBox(Graphics2D g, Btn b, boolean selected) {
        /* block comment [block:"CharacterCreation_block_766" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_766" category:"Variables" label:"set fill" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Color fill = selected ? PRIMARY : isHover(b.id) ? FILL_HOVER : TAB_REST;
        /* block end [id:"CharacterCreation_block_766"] */
        /* block comment [block:"CharacterCreation_block_767" text:"Runs the set color action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_767" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
        g.setColor(fill);
        /* block end [id:"CharacterCreation_block_767"] */ /* block comment [block:"CharacterCreation_block_768" text:"Calls the fillRoundRect method using its declared arguments."] */
        /* block start [id:"CharacterCreation_block_768" category:"My Blocks" label:"call fill round rect" shape:"command" docs:"Calls the fillRoundRect method using its declared arguments."] */
        g.fillRoundRect(b.x, b.y, b.w, b.h, /* param start [id:"CharacterCreation_value_1565" type:"number"] */10/* param end [id:"CharacterCreation_value_1565"] */, /* param start [id:"CharacterCreation_value_1566" type:"number"] */10/* param end [id:"CharacterCreation_value_1566"] */);
        /* block end [id:"CharacterCreation_block_768"] */ /* block comment [block:"CharacterCreation_block_769" text:"Runs the set color action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_769" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
        g.setColor(selected ? GOLD : TAB_EDGE);
        /* block end [id:"CharacterCreation_block_769"] */ /* block comment [block:"CharacterCreation_block_770" text:"Calls the drawRoundRect method using its declared arguments."] */
        /* block start [id:"CharacterCreation_block_770" category:"My Blocks" label:"call draw round rect" shape:"command" docs:"Calls the drawRoundRect method using its declared arguments."] */
        g.drawRoundRect(b.x, b.y, b.w, b.h, /* param start [id:"CharacterCreation_value_1567" type:"number"] */10/* param end [id:"CharacterCreation_value_1567"] */, /* param start [id:"CharacterCreation_value_1568" type:"number"] */10/* param end [id:"CharacterCreation_value_1568"] */);
        /* block end [id:"CharacterCreation_block_770"] */
    }
    private void drawCardBox(Graphics2D g, Btn b, boolean selected) {
        /* block comment [block:"CharacterCreation_block_771" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_771" category:"Variables" label:"set fill" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Color fill = selected ? PRIMARY : isHover(b.id) ? FILL_HOVER : CARD_REST;
        /* block end [id:"CharacterCreation_block_771"] */
        /* block comment [block:"CharacterCreation_block_772" text:"Runs the set color action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_772" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
        g.setColor(fill);
        /* block end [id:"CharacterCreation_block_772"] */ /* block comment [block:"CharacterCreation_block_773" text:"Calls the fillRoundRect method using its declared arguments."] */
        /* block start [id:"CharacterCreation_block_773" category:"My Blocks" label:"call fill round rect" shape:"command" docs:"Calls the fillRoundRect method using its declared arguments."] */
        g.fillRoundRect(b.x, b.y, b.w, b.h, /* param start [id:"CharacterCreation_value_1569" type:"number"] */8/* param end [id:"CharacterCreation_value_1569"] */, /* param start [id:"CharacterCreation_value_1570" type:"number"] */8/* param end [id:"CharacterCreation_value_1570"] */);
        /* block end [id:"CharacterCreation_block_773"] */ /* block comment [block:"CharacterCreation_block_774" text:"Runs the set color action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_774" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
        g.setColor(selected ? GOLD : isHover(b.id) ? CARD_HOVER_EDGE : CARD_EDGE);
        /* block end [id:"CharacterCreation_block_774"] */ /* block comment [block:"CharacterCreation_block_775" text:"Calls the drawRoundRect method using its declared arguments."] */
        /* block start [id:"CharacterCreation_block_775" category:"My Blocks" label:"call draw round rect" shape:"command" docs:"Calls the drawRoundRect method using its declared arguments."] */
        g.drawRoundRect(b.x, b.y, b.w, b.h, /* param start [id:"CharacterCreation_value_1571" type:"number"] */8/* param end [id:"CharacterCreation_value_1571"] */, /* param start [id:"CharacterCreation_value_1572" type:"number"] */8/* param end [id:"CharacterCreation_value_1572"] */);
        /* block end [id:"CharacterCreation_block_775"] */
    }
    private void drawStepperBox(Graphics2D g, Btn b) {
        /* block comment [block:"CharacterCreation_block_776" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_776" category:"Variables" label:"set fill" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Color fill = !b.enabled ? STEP_OFF : isHover(b.id) ? PRIMARY_HOVER : STEP_REST;
        /* block end [id:"CharacterCreation_block_776"] */
        /* block comment [block:"CharacterCreation_block_777" text:"Runs the set color action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_777" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
        g.setColor(fill);
        /* block end [id:"CharacterCreation_block_777"] */ /* block comment [block:"CharacterCreation_block_778" text:"Calls the fillRoundRect method using its declared arguments."] */
        /* block start [id:"CharacterCreation_block_778" category:"My Blocks" label:"call fill round rect" shape:"command" docs:"Calls the fillRoundRect method using its declared arguments."] */
        g.fillRoundRect(b.x, b.y, b.w, b.h, /* param start [id:"CharacterCreation_value_1573" type:"number"] */6/* param end [id:"CharacterCreation_value_1573"] */, /* param start [id:"CharacterCreation_value_1574" type:"number"] */6/* param end [id:"CharacterCreation_value_1574"] */);
        /* block end [id:"CharacterCreation_block_778"] */ /* block comment [block:"CharacterCreation_block_779" text:"Runs the set color action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_779" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
        g.setColor(b.enabled ? GOLD : STEP_OFF_EDGE);
        /* block end [id:"CharacterCreation_block_779"] */ /* block comment [block:"CharacterCreation_block_780" text:"Calls the drawRoundRect method using its declared arguments."] */
        /* block start [id:"CharacterCreation_block_780" category:"My Blocks" label:"call draw round rect" shape:"command" docs:"Calls the drawRoundRect method using its declared arguments."] */
        g.drawRoundRect(b.x, b.y, b.w, b.h, /* param start [id:"CharacterCreation_value_1575" type:"number"] */6/* param end [id:"CharacterCreation_value_1575"] */, /* param start [id:"CharacterCreation_value_1576" type:"number"] */6/* param end [id:"CharacterCreation_value_1576"] */);
        /* block end [id:"CharacterCreation_block_780"] */
    }
    private void drawFieldBox(Graphics2D g, List<Btn> btns, int w) {
        /* block comment [block:"CharacterCreation_block_781" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"CharacterCreation_block_781" category:"Variables" label:"set name" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Btn name = findBtn(btns, /* param start [id:"CharacterCreation_value_1577" type:"text"] */"name"/* param end [id:"CharacterCreation_value_1577"] */);
        /* block end [id:"CharacterCreation_block_781"] */ /* block comment [block:"CharacterCreation_block_782" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_782" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"CharacterCreation_block_171" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_171" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (name == /* param start [id:"CharacterCreation_value_1578" type:"value"] */null/* param end [id:"CharacterCreation_value_1578"] */) return;
        /* block end [id:"CharacterCreation_block_782"] */
        /* block end [id:"CharacterCreation_block_171"] */
        /* block comment [block:"CharacterCreation_block_783" text:"Runs the set color action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_783" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
        g.setColor(nameFocused ? FIELD_FOCUS : FIELD_REST);
        /* block end [id:"CharacterCreation_block_783"] */ /* block comment [block:"CharacterCreation_block_784" text:"Calls the fillRoundRect method using its declared arguments."] */
        /* block start [id:"CharacterCreation_block_784" category:"My Blocks" label:"call fill round rect" shape:"command" docs:"Calls the fillRoundRect method using its declared arguments."] */
        g.fillRoundRect(name.x, name.y, name.w, name.h, /* param start [id:"CharacterCreation_value_1579" type:"number"] */8/* param end [id:"CharacterCreation_value_1579"] */, /* param start [id:"CharacterCreation_value_1580" type:"number"] */8/* param end [id:"CharacterCreation_value_1580"] */);
        /* block end [id:"CharacterCreation_block_784"] */
        /* block comment [block:"CharacterCreation_block_785" text:"Runs the set color action. Values in this block are editable inputs."] */
        /* block start [id:"CharacterCreation_block_785" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
        g.setColor(nameFocused ? GOLD : FIELD_EDGE);
        /* block end [id:"CharacterCreation_block_785"] */ /* block comment [block:"CharacterCreation_block_786" text:"Calls the drawRoundRect method using its declared arguments."] */
        /* block start [id:"CharacterCreation_block_786" category:"My Blocks" label:"call draw round rect" shape:"command" docs:"Calls the drawRoundRect method using its declared arguments."] */
        g.drawRoundRect(name.x, name.y, name.w, name.h, /* param start [id:"CharacterCreation_value_1581" type:"number"] */8/* param end [id:"CharacterCreation_value_1581"] */, /* param start [id:"CharacterCreation_value_1582" type:"number"] */8/* param end [id:"CharacterCreation_value_1582"] */);
        /* block end [id:"CharacterCreation_block_786"] */
        /* block comment [block:"CharacterCreation_block_172" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"CharacterCreation_block_172" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (nameFocused && (System.currentTimeMillis() / /* param start [id:"CharacterCreation_value_1583" type:"number"] */500/* param end [id:"CharacterCreation_value_1583"] */) % /* param start [id:"CharacterCreation_value_1584" type:"number"] */2/* param end [id:"CharacterCreation_value_1584"] */ == /* param start [id:"CharacterCreation_value_1585" type:"number"] */0/* param end [id:"CharacterCreation_value_1585"] */) {
            // caret after text
            /* block comment [block:"CharacterCreation_block_787" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_787" category:"Variables" label:"set txt" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            String txt = heroName.length() == /* param start [id:"CharacterCreation_value_1586" type:"number"] */0/* param end [id:"CharacterCreation_value_1586"] */ ? /* param start [id:"CharacterCreation_value_1587" type:"text"] */""/* param end [id:"CharacterCreation_value_1587"] */ : heroName;
            /* block end [id:"CharacterCreation_block_787"] */
            /* block comment [block:"CharacterCreation_block_788" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_788" category:"Variables" label:"set f" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            BitmapFont.Style f = fontFor((int) (/* param start [id:"CharacterCreation_value_1588" type:"number"] */11/* param end [id:"CharacterCreation_value_1588"] */ * (w / /* param start [id:"CharacterCreation_value_1589" type:"number"] */480.0/* param end [id:"CharacterCreation_value_1589"] */)), /* param start [id:"CharacterCreation_value_1590" type:"boolean"] */false/* param end [id:"CharacterCreation_value_1590"] */);
            /* block end [id:"CharacterCreation_block_788"] */
            /* block comment [block:"CharacterCreation_block_790" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_790" category:"Variables" label:"set tw" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int tw = BitmapFont.width(txt, f);
            /* block end [id:"CharacterCreation_block_790"] */
            /* block comment [block:"CharacterCreation_block_791" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"CharacterCreation_block_791" category:"Variables" label:"set cx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int cx = name.x + /* param start [id:"CharacterCreation_value_1591" type:"number"] */10/* param end [id:"CharacterCreation_value_1591"] */ + tw + /* param start [id:"CharacterCreation_value_1592" type:"number"] */2/* param end [id:"CharacterCreation_value_1592"] */;
            /* block end [id:"CharacterCreation_block_791"] */ /* block comment [block:"CharacterCreation_block_792" text:"Runs the set color action. Values in this block are editable inputs."] */
            /* block start [id:"CharacterCreation_block_792" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
            g.setColor(SEL_TEXT);
            /* block end [id:"CharacterCreation_block_792"] */ /* block comment [block:"CharacterCreation_block_793" text:"Calls the drawLine method using its declared arguments."] */
            /* block start [id:"CharacterCreation_block_793" category:"My Blocks" label:"call draw line" shape:"command" docs:"Calls the drawLine method using its declared arguments."] */
            g.drawLine(cx, name.y + /* param start [id:"CharacterCreation_value_1593" type:"number"] */6/* param end [id:"CharacterCreation_value_1593"] */, cx, name.y + name.h - /* param start [id:"CharacterCreation_value_1594" type:"number"] */6/* param end [id:"CharacterCreation_value_1594"] */);
            /* block end [id:"CharacterCreation_block_793"] */
        }
        /* block end [id:"CharacterCreation_block_172"] */
    }
    private boolean isHover(String id) { /* block comment [block:"CharacterCreation_block_794" text:"Returns a value or ends the current method."] */
        /* block start [id:"CharacterCreation_block_794" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return id != /* param start [id:"CharacterCreation_value_1595" type:"value"] */null/* param end [id:"CharacterCreation_value_1595"] */ && id.equals(hoverId);
        /* block end [id:"CharacterCreation_block_794"] */ }
}
