package generated;

import com.jscratch.*;
import java.awt.Color;
import java.awt.event.KeyEvent;

/**
* Floating HP bar: heart icon + green fill + black missing segments.
*
* <p>Call graph / interactions:
* <ul>
*   <li>Owned one-per-entity: {@code EnemySprite.hpBar} (updated in
*       {@code think} via {@code hpUpdate}, hidden in {@code die}) and
*       {@code Sprite1.playerHp} (updated in the player loop).</li>
*   <li>Segments are 1px-art {@code offsetSprite}s with size 100, so scale
*       maps 1:1 to world pixels; {@link #update(int, int, int, int)}
*       converts hp/max to fill/empty widths anchored at a shared left
*       edge. No layer calls per frame — order fixed at construction.</li>
*   <li>Visibility propagates: {@code show/hide} fan out to both
*       segments; {@code update} auto-hides at 0 HP.</li>
* </ul>
*/
public class HPBAR extends offsetSprite {
    /* slot start [id:"HPBAR_members" tags:"inClassNotMethod"] */
    public BarFilled filled;
    public BarEmpty empty;
    /** World-unit bar size. 1px art + size 100 means scale == pixels. */
    public static final int BAR_W = 64;
    public static final int BAR_H = 8;
    public HPBAR() {
        /* block comment [block:"HPBAR_block_22" text:"Runs the super action. Values in this block are editable inputs."] */
        /* block start [id:"HPBAR_block_22" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
        super(/* param start [id:"HPBAR_value_85" type:"text"] */"HPBAR"/* param end [id:"HPBAR_value_85"] */);
        /* block end [id:"HPBAR_block_22"] */

        // Example: addCostume(new Costume("costume1", "path/to/img.png"));

        /* slot start [id:"HPBAR_init" tags:"inInstanceMethod"] */


        /* block comment [block:"HPBAR_block_23" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"HPBAR_block_23" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(new Costume(

        /* param start [id:"HPBAR_value_86" type:"text"] */"heart"/* param end [id:"HPBAR_value_86"] */,
        /* param start [id:"HPBAR_value_87" type:"text"] */"jscratch_ws/assets/"/* param end [id:"HPBAR_value_87"] */+
        /* param start [id:"HPBAR_value_88" type:"text"] */"heart.png"/* param end [id:"HPBAR_value_88"] */,
        /* param start [id:"HPBAR_value_89" type:"number"] */3/* param end [id:"HPBAR_value_89"] */, //change this to be half your sprite width, this is for rotation
        /* param start [id:"HPBAR_value_90" type:"number"] */3/* param end [id:"HPBAR_value_90"] */ //change this to be half your sprite height, this is for rotation
        ));
        /* block end [id:"HPBAR_block_23"] */



        /* block comment [block:"HPBAR_block_24" text:"Runs the set size action. Values in this block are editable inputs."] */
        /* block start [id:"HPBAR_block_24" category:"Looks" label:"set size" shape:"command" docs:"Runs the set size action. Values in this block are editable inputs."] */
        setSize(/* param start [id:"HPBAR_value_91" type:"number"] */100/* param end [id:"HPBAR_value_91"] */);
        /* block end [id:"HPBAR_block_24"] */



        /* block comment [block:"HPBAR_block_25" text:"Runs the bar empty action. Values in this block are editable inputs."] */
        /* block start [id:"HPBAR_block_25" category:"My Blocks" label:"bar empty" shape:"command" docs:"Runs the bar empty action. Values in this block are editable inputs."] */
        empty = new BarEmpty();
        /* block end [id:"HPBAR_block_25"] */
        /* block comment [block:"HPBAR_block_26" text:"Runs the bar filled action. Values in this block are editable inputs."] */
        /* block start [id:"HPBAR_block_26" category:"My Blocks" label:"bar filled" shape:"command" docs:"Runs the bar filled action. Values in this block are editable inputs."] */
        filled = new BarFilled();
        /* block end [id:"HPBAR_block_26"] */
        /* block comment [block:"HPBAR_block_27" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"HPBAR_block_27" category:"Variables" label:"set stage" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage stage = Stage.getInstance();
        /* block end [id:"HPBAR_block_27"] */
        /* block comment [block:"HPBAR_block_28" text:"Calls the addSprite method using its declared arguments."] */
        /* block start [id:"HPBAR_block_28" category:"My Blocks" label:"call add sprite" shape:"command" docs:"Calls the addSprite method using its declared arguments."] */
        stage.addSprite(empty);
        /* block end [id:"HPBAR_block_28"] */
        /* block comment [block:"HPBAR_block_29" text:"Calls the addSprite method using its declared arguments."] */
        /* block start [id:"HPBAR_block_29" category:"My Blocks" label:"call add sprite" shape:"command" docs:"Calls the addSprite method using its declared arguments."] */
        stage.addSprite(filled);
        /* block end [id:"HPBAR_block_29"] */
        /* block comment [block:"HPBAR_block_30" text:"Calls the addSprite method using its declared arguments."] */
        /* block start [id:"HPBAR_block_30" category:"My Blocks" label:"call add sprite" shape:"command" docs:"Calls the addSprite method using its declared arguments."] */
        stage.addSprite(this);
        /* block end [id:"HPBAR_block_30"] */
        // Bars live in the world layer: keep the minimap border above them
        // once (not every frame).
        /* block comment [block:"HPBAR_block_1" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"HPBAR_block_1" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try {
            /* block comment [block:"HPBAR_block_31" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"HPBAR_block_31" category:"Variables" label:"set sc" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Scroller2d sc = Scroller2d.getInstance();
            /* block end [id:"HPBAR_block_31"] */
            /* block comment [block:"HPBAR_block_32" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"HPBAR_block_32" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"HPBAR_block_2" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"HPBAR_block_2" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (sc != /* param start [id:"HPBAR_value_92" type:"value"] */null/* param end [id:"HPBAR_value_92"] */) sc.bringMinimapFront();
            /* block end [id:"HPBAR_block_32"] */
            /* block end [id:"HPBAR_block_2"] */
        }
        /* block end [id:"HPBAR_block_1"] */ catch (Exception ignored) {}
        /* slot end [id:"HPBAR_init"] */
    }


    /**
    * World-anchored HP bar. Bars sit at (newX, newY); the heart sits just
    * left of the bar. No layer churn here: z-order is fixed at construction
    * so per-frame updates stay O(1).
    */
    public void update(int hp, int maxHP, int newX, int newY) {
        /* block comment [block:"HPBAR_block_3" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"HPBAR_block_3" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (maxHP <= /* param start [id:"HPBAR_value_93" type:"number"] */0/* param end [id:"HPBAR_value_93"] */) {
            /* block comment [block:"HPBAR_block_33" text:"Runs the hide action. Values in this block are editable inputs."] */
            /* block start [id:"HPBAR_block_33" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
            hide();
            /* block end [id:"HPBAR_block_33"] */
            /* block comment [block:"HPBAR_block_34" text:"Returns a value or ends the current method."] */
            /* block start [id:"HPBAR_block_34" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"HPBAR_block_34"] */
        }
        /* block end [id:"HPBAR_block_3"] */
        /* block comment [block:"HPBAR_block_35" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"HPBAR_block_35" category:"Variables" label:"set clamped" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int clamped = hp < /* param start [id:"HPBAR_value_94" type:"number"] */0/* param end [id:"HPBAR_value_94"] */ ? /* param start [id:"HPBAR_value_95" type:"number"] */0/* param end [id:"HPBAR_value_95"] */ : (hp > maxHP ? maxHP : hp);
        /* block end [id:"HPBAR_block_35"] */
        /* block comment [block:"HPBAR_block_36" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"HPBAR_block_36" category:"Variables" label:"set frac" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        float frac = (float) clamped / (float) maxHP;
        /* block end [id:"HPBAR_block_36"] */
        /* block comment [block:"HPBAR_block_37" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"HPBAR_block_37" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"HPBAR_block_4" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"HPBAR_block_4" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (frac < /* param start [id:"HPBAR_value_96" type:"number"] */0f/* param end [id:"HPBAR_value_96"] */) frac = /* param start [id:"HPBAR_value_97" type:"number"] */0f/* param end [id:"HPBAR_value_97"] */;
        /* block end [id:"HPBAR_block_37"] */
        /* block end [id:"HPBAR_block_4"] */
        /* block comment [block:"HPBAR_block_38" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"HPBAR_block_38" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"HPBAR_block_5" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"HPBAR_block_5" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (frac > /* param start [id:"HPBAR_value_98" type:"number"] */1f/* param end [id:"HPBAR_value_98"] */) frac = /* param start [id:"HPBAR_value_99" type:"number"] */1f/* param end [id:"HPBAR_value_99"] */;
        /* block end [id:"HPBAR_block_38"] */
        /* block end [id:"HPBAR_block_5"] */
        /* block comment [block:"HPBAR_block_39" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"HPBAR_block_39" category:"Variables" label:"set left" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int left = newX - BAR_W / /* param start [id:"HPBAR_value_100" type:"number"] */2/* param end [id:"HPBAR_value_100"] */;
        /* block end [id:"HPBAR_block_39"] */
        /* block comment [block:"HPBAR_block_40" text:"Calls the updateFrac method using its declared arguments."] */
        /* block start [id:"HPBAR_block_40" category:"My Blocks" label:"call update frac" shape:"command" docs:"Calls the updateFrac method using its declared arguments."] */
        filled.updateFrac(frac, left, newY);
        /* block end [id:"HPBAR_block_40"] */
        /* block comment [block:"HPBAR_block_41" text:"Calls the updateFrac method using its declared arguments."] */
        /* block start [id:"HPBAR_block_41" category:"My Blocks" label:"call update frac" shape:"command" docs:"Calls the updateFrac method using its declared arguments."] */
        empty.updateFrac(frac, left, newY);
        /* block end [id:"HPBAR_block_41"] */
        /* block comment [block:"HPBAR_block_42" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"HPBAR_block_42" category:"Variables" label:"set ds" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double ds = dispScale();
        /* block end [id:"HPBAR_block_42"] */
        /* block comment [block:"HPBAR_block_43" text:"Runs the set scale x action. Values in this block are editable inputs."] */
        /* block start [id:"HPBAR_block_43" category:"Variables" label:"set scale x" shape:"command" docs:"Runs the set scale x action. Values in this block are editable inputs."] */
        setScaleX(/* param start [id:"HPBAR_value_101" type:"number"] */1.4/* param end [id:"HPBAR_value_101"] */ * ds);
        /* block end [id:"HPBAR_block_43"] */
        /* block comment [block:"HPBAR_block_44" text:"Runs the set scale y action. Values in this block are editable inputs."] */
        /* block start [id:"HPBAR_block_44" category:"Variables" label:"set scale y" shape:"command" docs:"Runs the set scale y action. Values in this block are editable inputs."] */
        setScaleY(/* param start [id:"HPBAR_value_102" type:"number"] */1.4/* param end [id:"HPBAR_value_102"] */ * ds);
        /* block end [id:"HPBAR_block_44"] */
        /* block comment [block:"HPBAR_block_45" text:"Runs the go to action. Values in this block are editable inputs."] */
        /* block start [id:"HPBAR_block_45" category:"Motion" label:"go to" shape:"command" docs:"Runs the go to action. Values in this block are editable inputs."] */
        goTo(left - /* param start [id:"HPBAR_value_103" type:"number"] */12/* param end [id:"HPBAR_value_103"] */, newY);
        /* block end [id:"HPBAR_block_45"] */
        /* block comment [block:"HPBAR_block_6" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"HPBAR_block_6" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (clamped <= /* param start [id:"HPBAR_value_104" type:"number"] */0/* param end [id:"HPBAR_value_104"] */) {
            /* block comment [block:"HPBAR_block_46" text:"Runs the hide action. Values in this block are editable inputs."] */
            /* block start [id:"HPBAR_block_46" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
            hide();
            /* block end [id:"HPBAR_block_46"] */
        } else {
            // Owner is alive: mirror visibility without touching layers.
            /* block comment [block:"HPBAR_block_47" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"HPBAR_block_47" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"HPBAR_block_7" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"HPBAR_block_7" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (!isVisible()) show();
            /* block end [id:"HPBAR_block_47"] */
            /* block end [id:"HPBAR_block_7"] */
            /* block comment [block:"HPBAR_block_48" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"HPBAR_block_48" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"HPBAR_block_8" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"HPBAR_block_8" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (!filled.isVisible()) filled.show();
            /* block end [id:"HPBAR_block_48"] */
            /* block end [id:"HPBAR_block_8"] */
            /* block comment [block:"HPBAR_block_49" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"HPBAR_block_49" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"HPBAR_block_9" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"HPBAR_block_9" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (!empty.isVisible()) empty.show();
            /* block end [id:"HPBAR_block_49"] */
            /* block end [id:"HPBAR_block_9"] */
        }
        /* block end [id:"HPBAR_block_6"] */
    }

    @Override
    public void show() {
        /* block comment [block:"HPBAR_block_50" text:"Runs the show action. Values in this block are editable inputs."] */
        /* block start [id:"HPBAR_block_50" category:"Looks" label:"show" shape:"command" docs:"Runs the show action. Values in this block are editable inputs."] */
        super.show();
        /* block end [id:"HPBAR_block_50"] */
        /* block comment [block:"HPBAR_block_51" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"HPBAR_block_51" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"HPBAR_block_10" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"HPBAR_block_10" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (filled != /* param start [id:"HPBAR_value_105" type:"value"] */null/* param end [id:"HPBAR_value_105"] */) filled.show();
        /* block end [id:"HPBAR_block_51"] */
        /* block end [id:"HPBAR_block_10"] */
        /* block comment [block:"HPBAR_block_52" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"HPBAR_block_52" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"HPBAR_block_11" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"HPBAR_block_11" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (empty != /* param start [id:"HPBAR_value_106" type:"value"] */null/* param end [id:"HPBAR_value_106"] */) empty.show();
        /* block end [id:"HPBAR_block_52"] */
        /* block end [id:"HPBAR_block_11"] */
    }

    @Override
    public void hide() {
        /* block comment [block:"HPBAR_block_53" text:"Runs the hide action. Values in this block are editable inputs."] */
        /* block start [id:"HPBAR_block_53" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
        super.hide();
        /* block end [id:"HPBAR_block_53"] */
        /* block comment [block:"HPBAR_block_54" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"HPBAR_block_54" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"HPBAR_block_12" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"HPBAR_block_12" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (filled != /* param start [id:"HPBAR_value_107" type:"value"] */null/* param end [id:"HPBAR_value_107"] */) filled.hide();
        /* block end [id:"HPBAR_block_54"] */
        /* block end [id:"HPBAR_block_12"] */
        /* block comment [block:"HPBAR_block_55" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"HPBAR_block_55" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"HPBAR_block_13" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"HPBAR_block_13" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (empty != /* param start [id:"HPBAR_value_108" type:"value"] */null/* param end [id:"HPBAR_value_108"] */) empty.hide();
        /* block end [id:"HPBAR_block_55"] */
        /* block end [id:"HPBAR_block_13"] */
    }

    /** Shared bar-segment logic: pixel-exact fill with top-left anchoring. */
    private abstract static class BarSegment extends offsetSprite {
        BarSegment(String name) {
            /* block comment [block:"HPBAR_block_56" text:"Runs the super action. Values in this block are editable inputs."] */
            /* block start [id:"HPBAR_block_56" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
            super(name);
            /* block end [id:"HPBAR_block_56"] */
            /* block comment [block:"HPBAR_block_57" text:"Runs the set size action. Values in this block are editable inputs."] */
            /* block start [id:"HPBAR_block_57" category:"Looks" label:"set size" shape:"command" docs:"Runs the set size action. Values in this block are editable inputs."] */
            setSize(/* param start [id:"HPBAR_value_109" type:"number"] */100/* param end [id:"HPBAR_value_109"] */);
            /* block end [id:"HPBAR_block_57"] */
        }

        void place(int left, int y, int segX, int segW) {
            /* block comment [block:"HPBAR_block_58" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"HPBAR_block_58" category:"Variables" label:"set ds" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double ds = dispScale();
            /* block end [id:"HPBAR_block_58"] */
            // 1px art, size 100 -> scale maps 1:1 to world pixels.
            /* block comment [block:"HPBAR_block_59" text:"Runs the set scale x action. Values in this block are editable inputs."] */
            /* block start [id:"HPBAR_block_59" category:"Variables" label:"set scale x" shape:"command" docs:"Runs the set scale x action. Values in this block are editable inputs."] */
            setScaleX(Math.max(/* param start [id:"HPBAR_value_110" type:"number"] */0/* param end [id:"HPBAR_value_110"] */, segW) * ds);
            /* block end [id:"HPBAR_block_59"] */
            // Hide zero-width segments instead of drawing a 2px min blob.
            /* block comment [block:"HPBAR_block_14" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"HPBAR_block_14" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (segW <= /* param start [id:"HPBAR_value_111" type:"number"] */0/* param end [id:"HPBAR_value_111"] */) {
                /* block comment [block:"HPBAR_block_60" text:"Runs the hide action. Values in this block are editable inputs."] */
                /* block start [id:"HPBAR_block_60" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
                hide();
                /* block end [id:"HPBAR_block_60"] */
                /* block comment [block:"HPBAR_block_61" text:"Returns a value or ends the current method."] */
                /* block start [id:"HPBAR_block_61" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
                return;
                /* block end [id:"HPBAR_block_61"] */
            }
            /* block end [id:"HPBAR_block_14"] */
            /* block comment [block:"HPBAR_block_62" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"HPBAR_block_62" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"HPBAR_block_15" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"HPBAR_block_15" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (!isVisible()) show();
            /* block end [id:"HPBAR_block_62"] */
            /* block end [id:"HPBAR_block_15"] */
            /* block comment [block:"HPBAR_block_63" text:"Runs the set scale y action. Values in this block are editable inputs."] */
            /* block start [id:"HPBAR_block_63" category:"Variables" label:"set scale y" shape:"command" docs:"Runs the set scale y action. Values in this block are editable inputs."] */
            setScaleY(BAR_H * ds);
            /* block end [id:"HPBAR_block_63"] */
            /* block comment [block:"HPBAR_block_64" text:"Runs the go to action. Values in this block are editable inputs."] */
            /* block start [id:"HPBAR_block_64" category:"Motion" label:"go to" shape:"command" docs:"Runs the go to action. Values in this block are editable inputs."] */
            goTo(segX, y);
            /* block end [id:"HPBAR_block_64"] */
        }
    }

    public static class BarFilled extends BarSegment {
        public BarFilled() {
            /* block comment [block:"HPBAR_block_65" text:"Runs the super action. Values in this block are editable inputs."] */
            /* block start [id:"HPBAR_block_65" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
            super(/* param start [id:"HPBAR_value_112" type:"text"] */"BarFilled"/* param end [id:"HPBAR_value_112"] */);
            /* block end [id:"HPBAR_block_65"] */
            /* block comment [block:"HPBAR_block_66" text:"Runs the add costume action. Values in this block are editable inputs."] */
            /* block start [id:"HPBAR_block_66" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
            addCostume(new Costume(

            /* param start [id:"HPBAR_value_113" type:"text"] */"costume"/* param end [id:"HPBAR_value_113"] */,
            /* param start [id:"HPBAR_value_114" type:"text"] */"jscratch_ws/assets/"/* param end [id:"HPBAR_value_114"] */+
            /* param start [id:"HPBAR_value_115" type:"text"] */"hp color.png"/* param end [id:"HPBAR_value_115"] */,
            /* param start [id:"HPBAR_value_116" type:"number"] */0/* param end [id:"HPBAR_value_116"] */, //change this to be half your sprite width, this is for rotation
            /* param start [id:"HPBAR_value_117" type:"number"] */0/* param end [id:"HPBAR_value_117"] */ //change this to be half your sprite height, this is for rotation
            ));
            /* block end [id:"HPBAR_block_66"] */

        }
        public void update(float max, int val, int nx, int ny) {
            /* block comment [block:"HPBAR_block_16" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"HPBAR_block_16" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (max <= /* param start [id:"HPBAR_value_118" type:"number"] */0/* param end [id:"HPBAR_value_118"] */) { /* block comment [block:"HPBAR_block_67" text:"Runs the hide action. Values in this block are editable inputs."] */
                /* block start [id:"HPBAR_block_67" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
                hide();
                /* block end [id:"HPBAR_block_67"] */ /* block comment [block:"HPBAR_block_68" text:"Returns a value or ends the current method."] */
                /* block start [id:"HPBAR_block_68" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
                return;
                /* block end [id:"HPBAR_block_68"] */ }
            /* block end [id:"HPBAR_block_16"] */
            /* block comment [block:"HPBAR_block_69" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"HPBAR_block_69" category:"Variables" label:"set frac" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            float frac = (float) val / max;
            /* block end [id:"HPBAR_block_69"] */
            /* block comment [block:"HPBAR_block_70" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"HPBAR_block_70" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"HPBAR_block_17" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"HPBAR_block_17" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (frac < /* param start [id:"HPBAR_value_119" type:"number"] */0f/* param end [id:"HPBAR_value_119"] */) frac = /* param start [id:"HPBAR_value_120" type:"number"] */0f/* param end [id:"HPBAR_value_120"] */;
            /* block end [id:"HPBAR_block_70"] */
            /* block end [id:"HPBAR_block_17"] */
            /* block comment [block:"HPBAR_block_71" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"HPBAR_block_71" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"HPBAR_block_18" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"HPBAR_block_18" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (frac > /* param start [id:"HPBAR_value_121" type:"number"] */1f/* param end [id:"HPBAR_value_121"] */) frac = /* param start [id:"HPBAR_value_122" type:"number"] */1f/* param end [id:"HPBAR_value_122"] */;
            /* block end [id:"HPBAR_block_71"] */
            /* block end [id:"HPBAR_block_18"] */
            /* block comment [block:"HPBAR_block_72" text:"Calls the updateFrac method using its declared arguments."] */
            /* block start [id:"HPBAR_block_72" category:"My Blocks" label:"call update frac" shape:"command" docs:"Calls the updateFrac method using its declared arguments."] */
            updateFrac(frac, nx - BAR_W / /* param start [id:"HPBAR_value_123" type:"number"] */2/* param end [id:"HPBAR_value_123"] */, ny);
            /* block end [id:"HPBAR_block_72"] */
        }

        void updateFrac(float frac, int left, int y) {
            /* block comment [block:"HPBAR_block_73" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"HPBAR_block_73" category:"Variables" label:"set filledW" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int filledW = Math.round(BAR_W * frac);
            /* block end [id:"HPBAR_block_73"] */
            /* block comment [block:"HPBAR_block_74" text:"Calls the place method using its declared arguments."] */
            /* block start [id:"HPBAR_block_74" category:"My Blocks" label:"call place" shape:"command" docs:"Calls the place method using its declared arguments."] */
            place(left, y, left, filledW);
            /* block end [id:"HPBAR_block_74"] */
        }

    }



    public static class BarEmpty extends BarSegment {
        public BarEmpty() {
            /* block comment [block:"HPBAR_block_75" text:"Runs the super action. Values in this block are editable inputs."] */
            /* block start [id:"HPBAR_block_75" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
            super(/* param start [id:"HPBAR_value_124" type:"text"] */"BarEmpty"/* param end [id:"HPBAR_value_124"] */);
            /* block end [id:"HPBAR_block_75"] */
            /* block comment [block:"HPBAR_block_76" text:"Runs the add costume action. Values in this block are editable inputs."] */
            /* block start [id:"HPBAR_block_76" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
            addCostume(new Costume(

            /* param start [id:"HPBAR_value_125" type:"text"] */"costume"/* param end [id:"HPBAR_value_125"] */,
            /* param start [id:"HPBAR_value_126" type:"text"] */"jscratch_ws/assets/"/* param end [id:"HPBAR_value_126"] */+
            /* param start [id:"HPBAR_value_127" type:"text"] */"hp black.png"/* param end [id:"HPBAR_value_127"] */,
            /* param start [id:"HPBAR_value_128" type:"number"] */0/* param end [id:"HPBAR_value_128"] */, //change this to be half your sprite width, this is for rotation
            /* param start [id:"HPBAR_value_129" type:"number"] */0/* param end [id:"HPBAR_value_129"] */ //change this to be half your sprite height, this is for rotation
            ));
            /* block end [id:"HPBAR_block_76"] */

        }
        public void update(float max, int val, int nx, int ny) {
            /* block comment [block:"HPBAR_block_19" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"HPBAR_block_19" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (max <= /* param start [id:"HPBAR_value_130" type:"number"] */0/* param end [id:"HPBAR_value_130"] */) { /* block comment [block:"HPBAR_block_77" text:"Runs the hide action. Values in this block are editable inputs."] */
                /* block start [id:"HPBAR_block_77" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
                hide();
                /* block end [id:"HPBAR_block_77"] */ /* block comment [block:"HPBAR_block_78" text:"Returns a value or ends the current method."] */
                /* block start [id:"HPBAR_block_78" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
                return;
                /* block end [id:"HPBAR_block_78"] */ }
            /* block end [id:"HPBAR_block_19"] */
            /* block comment [block:"HPBAR_block_79" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"HPBAR_block_79" category:"Variables" label:"set frac" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            float frac = (float) val / max;
            /* block end [id:"HPBAR_block_79"] */
            /* block comment [block:"HPBAR_block_80" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"HPBAR_block_80" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"HPBAR_block_20" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"HPBAR_block_20" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (frac < /* param start [id:"HPBAR_value_131" type:"number"] */0f/* param end [id:"HPBAR_value_131"] */) frac = /* param start [id:"HPBAR_value_132" type:"number"] */0f/* param end [id:"HPBAR_value_132"] */;
            /* block end [id:"HPBAR_block_80"] */
            /* block end [id:"HPBAR_block_20"] */
            /* block comment [block:"HPBAR_block_81" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"HPBAR_block_81" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"HPBAR_block_21" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"HPBAR_block_21" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (frac > /* param start [id:"HPBAR_value_133" type:"number"] */1f/* param end [id:"HPBAR_value_133"] */) frac = /* param start [id:"HPBAR_value_134" type:"number"] */1f/* param end [id:"HPBAR_value_134"] */;
            /* block end [id:"HPBAR_block_81"] */
            /* block end [id:"HPBAR_block_21"] */
            /* block comment [block:"HPBAR_block_82" text:"Calls the updateFrac method using its declared arguments."] */
            /* block start [id:"HPBAR_block_82" category:"My Blocks" label:"call update frac" shape:"command" docs:"Calls the updateFrac method using its declared arguments."] */
            updateFrac(frac, nx - BAR_W / /* param start [id:"HPBAR_value_135" type:"number"] */2/* param end [id:"HPBAR_value_135"] */, ny);
            /* block end [id:"HPBAR_block_82"] */
        }

        void updateFrac(float frac, int left, int y) {
            /* block comment [block:"HPBAR_block_83" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"HPBAR_block_83" category:"Variables" label:"set filledW" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int filledW = Math.round(BAR_W * frac);
            /* block end [id:"HPBAR_block_83"] */
            /* block comment [block:"HPBAR_block_84" text:"Calls the place method using its declared arguments."] */
            /* block start [id:"HPBAR_block_84" category:"My Blocks" label:"call place" shape:"command" docs:"Calls the place method using its declared arguments."] */
            place(left, y, left + filledW, BAR_W - filledW);
            /* block end [id:"HPBAR_block_84"] */
        }

    }



    /* slot end [id:"HPBAR_members"] */
}
