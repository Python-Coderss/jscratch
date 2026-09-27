package generated;

import com.jscratch.*;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.*;

/**
* World-space sprite base. Holds world coords (xx, yy), per-sprite scale,
* body radius, and a minimap dot; converts world → screen each update.
*
* <p>Call graph / interactions:
* <ul>
*   <li>Parent of: Sprite1 (player), EnemySprite + subtypes, WallSprite,
*       LootDrop, Spellbook projectiles/effects, Stairs, Chest.</li>
*   <li>Constructor registers with {@code Scroller2d.register} (also
*       re-fronts the minimap border once) and adds a MinimapSprite.</li>
*   <li>{@link #update()} is called by {@code Scroller2d.update} for every
*       registered sprite and directly after every move; it bakes
*       {@code (world + scroll) * displayScale} into screen pos/scale.
*       Early-outs when invisible (keeps the minimap dot hidden).</li>
*   <li>Movement: {@link #tryMove(int, int)} slides along walls via
*       {@code Scroller2d.isColliding}; {@link #destroy()} hides and
*       unregisters so dead/transient sprites stop costing updates.</li>
* </ul>
*/
public class BSprite extends Sprite {
    /* slot start [id:"BSprite_members" tags:"inClassNotMethod"] */
    public int xx = 0;
    public int yy = 0;
    public double localScale = 1.0; // Changed to double for precise sprite-specific scaling
    public MinimapSprite minimapSprite;
    /** Half-width of the collision box. Big sprites need big bodies
    *  or their art hangs over (and reads as phasing through) walls. */
    public int bodyRadius = 10;

    public BSprite(String name) {
        /* block comment [block:"BSprite_block_25" text:"Runs the super action. Values in this block are editable inputs."] */
        /* block start [id:"BSprite_block_25" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
        super(name);
        /* block end [id:"BSprite_block_25"] */

        /* block comment [block:"BSprite_block_26" text:"Runs the minimap sprite action. Values in this block are editable inputs."] */
        /* block start [id:"BSprite_block_26" category:"My Blocks" label:"minimap sprite" shape:"command" docs:"Runs the minimap sprite action. Values in this block are editable inputs."] */
        minimapSprite = new MinimapSprite(name + /* param start [id:"BSprite_value_126" type:"text"] */"_mini"/* param end [id:"BSprite_value_126"] */);
        /* block end [id:"BSprite_block_26"] */
        /* block comment [block:"BSprite_block_27" text:"Runs the set scale x action. Values in this block are editable inputs."] */
        /* block start [id:"BSprite_block_27" category:"Variables" label:"set scale x" shape:"command" docs:"Runs the set scale x action. Values in this block are editable inputs."] */
        minimapSprite.setScaleX(/* param start [id:"BSprite_value_127" type:"number"] */0.2/* param end [id:"BSprite_value_127"] */);
        /* block end [id:"BSprite_block_27"] */
        /* block comment [block:"BSprite_block_28" text:"Runs the set scale y action. Values in this block are editable inputs."] */
        /* block start [id:"BSprite_block_28" category:"Variables" label:"set scale y" shape:"command" docs:"Runs the set scale y action. Values in this block are editable inputs."] */
        minimapSprite.setScaleY(/* param start [id:"BSprite_value_128" type:"number"] */0.2/* param end [id:"BSprite_value_128"] */);
        /* block end [id:"BSprite_block_28"] */
        /* block comment [block:"BSprite_block_29" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_29" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().addSprite(minimapSprite);
        /* block end [id:"BSprite_block_29"] */

        this.whenScaleChanged((old, newscale) -> {
            // This is called when Stage scale changes
            update();
        }/* block comment [block:"BSprite_block_30" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"BSprite_block_30" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        );
        /* block end [id:"BSprite_block_30"] */

        /* block comment [block:"BSprite_block_31" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_31" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Scroller2d.getInstance().register(this, minimapSprite);
        /* block end [id:"BSprite_block_31"] */
    }

    public void update() {
        /* block comment [block:"BSprite_block_1" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BSprite_block_1" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!isVisible()) {
            // Dead/transient sprites stay registered until reaped: keep the
            // minimap dot hidden without paying position math per frame.
            /* block comment [block:"BSprite_block_32" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"BSprite_block_32" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"BSprite_block_2" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"BSprite_block_2" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (minimapSprite != /* param start [id:"BSprite_value_129" type:"value"] */null/* param end [id:"BSprite_value_129"] */ && minimapSprite.isVisible()) minimapSprite.hide();
            /* block end [id:"BSprite_block_32"] */
            /* block end [id:"BSprite_block_2"] */
            /* block comment [block:"BSprite_block_33" text:"Returns a value or ends the current method."] */
            /* block start [id:"BSprite_block_33" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"BSprite_block_33"] */
        }
        /* block end [id:"BSprite_block_1"] */
        /* block comment [block:"BSprite_block_34" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_34" category:"Variables" label:"set stage" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage stage = Stage.getInstance();
        /* block end [id:"BSprite_block_34"] */
        /* block comment [block:"BSprite_block_35" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_35" category:"Variables" label:"set displayScale" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double displayScale = stage.getScaleFactor();
        /* block end [id:"BSprite_block_35"] */
        /* block comment [block:"BSprite_block_36" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_36" category:"Variables" label:"set scroller" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Scroller2d scroller = Scroller2d.getInstance();
        /* block end [id:"BSprite_block_36"] */

        // Screen position = (World position + Scroll offset) * Stage scale.
        // NOTE: localScale sizes the sprite only — it must NOT scale the
        // position, or small enemies drift toward screen center and don't
        // track the world 1:1 when the player moves.
        /* block comment [block:"BSprite_block_37" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_37" category:"Variables" label:"set screenX" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double screenX = (xx + scroller.x) * displayScale;
        /* block end [id:"BSprite_block_37"] */
        /* block comment [block:"BSprite_block_38" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_38" category:"Variables" label:"set screenY" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double screenY = (yy + scroller.y) * displayScale;
        /* block end [id:"BSprite_block_38"] */

        /* block comment [block:"BSprite_block_39" text:"Runs the go to action. Values in this block are editable inputs."] */
        /* block start [id:"BSprite_block_39" category:"Motion" label:"go to" shape:"command" docs:"Runs the go to action. Values in this block are editable inputs."] */
        super.goTo(screenX, screenY);
        /* block end [id:"BSprite_block_39"] */
        /* block comment [block:"BSprite_block_40" text:"Runs the set scale x action. Values in this block are editable inputs."] */
        /* block start [id:"BSprite_block_40" category:"Variables" label:"set scale x" shape:"command" docs:"Runs the set scale x action. Values in this block are editable inputs."] */
        super.setScaleX(localScale * displayScale);
        /* block end [id:"BSprite_block_40"] */
        /* block comment [block:"BSprite_block_41" text:"Runs the set scale y action. Values in this block are editable inputs."] */
        /* block start [id:"BSprite_block_41" category:"Variables" label:"set scale y" shape:"command" docs:"Runs the set scale y action. Values in this block are editable inputs."] */
        super.setScaleY(localScale * displayScale);
        /* block end [id:"BSprite_block_41"] */

        /* block comment [block:"BSprite_block_3" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BSprite_block_3" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (minimapSprite != /* param start [id:"BSprite_value_130" type:"value"] */null/* param end [id:"BSprite_value_130"] */) {
            /* block comment [block:"BSprite_block_42" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"BSprite_block_42" category:"Variables" label:"set dir" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double dir = (Double) getVariable(/* param start [id:"BSprite_value_131" type:"text"] */"direction"/* param end [id:"BSprite_value_131"] */);
            /* block end [id:"BSprite_block_42"] */
            /* block comment [block:"BSprite_block_43" text:"Calls the update method using its declared arguments."] */
            /* block start [id:"BSprite_block_43" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
            minimapSprite.update(xx, yy, dir, -scroller.x, -scroller.y);
            /* block end [id:"BSprite_block_43"] */
        }
        /* block end [id:"BSprite_block_3"] */
    }

    public void goTo(int x, int y) {
        /* block comment [block:"BSprite_block_4" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BSprite_block_4" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!willCollide(x, y)) {
            /* block comment [block:"BSprite_block_44" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"BSprite_block_44" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            xx = x;
            /* block end [id:"BSprite_block_44"] */
            /* block comment [block:"BSprite_block_45" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"BSprite_block_45" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            yy = y;
            /* block end [id:"BSprite_block_45"] */
        }
        /* block end [id:"BSprite_block_4"] */
        /* block comment [block:"BSprite_block_46" text:"Calls the update method using its declared arguments."] */
        /* block start [id:"BSprite_block_46" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
        update();
        /* block end [id:"BSprite_block_46"] */
    }

    public void changeY(int y) {
        /* block comment [block:"BSprite_block_5" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BSprite_block_5" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!willCollide(xx, yy + y)) {
            /* block comment [block:"BSprite_block_47" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"BSprite_block_47" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            yy += y;
            /* block end [id:"BSprite_block_47"] */
        }
        /* block end [id:"BSprite_block_5"] */
        /* block comment [block:"BSprite_block_48" text:"Calls the update method using its declared arguments."] */
        /* block start [id:"BSprite_block_48" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
        update();
        /* block end [id:"BSprite_block_48"] */
    }

    public void changeX(int x) {
        /* block comment [block:"BSprite_block_6" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BSprite_block_6" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!willCollide(xx + x, yy)) {
            /* block comment [block:"BSprite_block_49" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"BSprite_block_49" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            xx += x;
            /* block end [id:"BSprite_block_49"] */
        }
        /* block end [id:"BSprite_block_6"] */
        /* block comment [block:"BSprite_block_50" text:"Calls the update method using its declared arguments."] */
        /* block start [id:"BSprite_block_50" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
        update();
        /* block end [id:"BSprite_block_50"] */
    }

    public void walk(int steps) {
        // Moving in world units
        /* block comment [block:"BSprite_block_51" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_51" category:"Variables" label:"set rad" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double rad = Math.toRadians(/* param start [id:"BSprite_value_132" type:"number"] */90/* param end [id:"BSprite_value_132"] */ - (Double)getVariable(/* param start [id:"BSprite_value_133" type:"text"] */"direction"/* param end [id:"BSprite_value_133"] */));
        /* block end [id:"BSprite_block_51"] */
        /* block comment [block:"BSprite_block_52" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_52" category:"Variables" label:"set nextX" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int nextX = xx + (int)(steps * Math.cos(rad));
        /* block end [id:"BSprite_block_52"] */
        /* block comment [block:"BSprite_block_53" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_53" category:"Variables" label:"set nextY" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int nextY = yy + (int)(steps * Math.sin(rad));
        /* block end [id:"BSprite_block_53"] */
        /* block comment [block:"BSprite_block_7" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BSprite_block_7" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!willCollide(nextX, nextY)) {
            /* block comment [block:"BSprite_block_54" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"BSprite_block_54" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            xx = nextX;
            /* block end [id:"BSprite_block_54"] */
            /* block comment [block:"BSprite_block_55" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"BSprite_block_55" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            yy = nextY;
            /* block end [id:"BSprite_block_55"] */
        }
        /* block end [id:"BSprite_block_7"] */
        /* block comment [block:"BSprite_block_56" text:"Calls the update method using its declared arguments."] */
        /* block start [id:"BSprite_block_56" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
        update();
        /* block end [id:"BSprite_block_56"] */
    }

    public boolean willCollide(int nextX, int nextY) {
        /* block comment [block:"BSprite_block_57" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_57" category:"Variables" label:"set d" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int d = Math.max(/* param start [id:"BSprite_value_134" type:"number"] */4/* param end [id:"BSprite_value_134"] */, bodyRadius * /* param start [id:"BSprite_value_135" type:"number"] */2/* param end [id:"BSprite_value_135"] */);
        /* block end [id:"BSprite_block_57"] */
        /* block comment [block:"BSprite_block_58" text:"Returns a value or ends the current method."] */
        /* block start [id:"BSprite_block_58" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return Scroller2d.getInstance().isColliding(nextX, nextY, d, d);
        /* block end [id:"BSprite_block_58"] */
    }

    /**
    * Collision-aware step with wall sliding: tries the full move, then
    * X-only, then Y-only. Returns true if any movement happened.
    */
    public boolean tryMove(int dx, int dy) {
        /* block comment [block:"BSprite_block_59" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"BSprite_block_59" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"BSprite_block_8" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BSprite_block_8" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (dx == /* param start [id:"BSprite_value_136" type:"number"] */0/* param end [id:"BSprite_value_136"] */ && dy == /* param start [id:"BSprite_value_137" type:"number"] */0/* param end [id:"BSprite_value_137"] */) return /* param start [id:"BSprite_value_138" type:"boolean"] */true/* param end [id:"BSprite_value_138"] */;
        /* block end [id:"BSprite_block_59"] */
        /* block end [id:"BSprite_block_8"] */
        /* block comment [block:"BSprite_block_9" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BSprite_block_9" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!willCollide(xx + dx, yy + dy)) {
            /* block comment [block:"BSprite_block_60" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"BSprite_block_60" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            xx += dx;
            /* block end [id:"BSprite_block_60"] */
            /* block comment [block:"BSprite_block_61" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"BSprite_block_61" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            yy += dy;
            /* block end [id:"BSprite_block_61"] */
            /* block comment [block:"BSprite_block_62" text:"Returns a value or ends the current method."] */
            /* block start [id:"BSprite_block_62" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return /* param start [id:"BSprite_value_139" type:"boolean"] */true/* param end [id:"BSprite_value_139"] */;
            /* block end [id:"BSprite_block_62"] */
        }
        /* block end [id:"BSprite_block_9"] */
        /* block comment [block:"BSprite_block_10" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BSprite_block_10" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (dx != /* param start [id:"BSprite_value_140" type:"number"] */0/* param end [id:"BSprite_value_140"] */ && !willCollide(xx + dx, yy)) {
            /* block comment [block:"BSprite_block_63" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"BSprite_block_63" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            xx += dx;
            /* block end [id:"BSprite_block_63"] */
            /* block comment [block:"BSprite_block_64" text:"Returns a value or ends the current method."] */
            /* block start [id:"BSprite_block_64" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return /* param start [id:"BSprite_value_141" type:"boolean"] */true/* param end [id:"BSprite_value_141"] */;
            /* block end [id:"BSprite_block_64"] */
        }
        /* block end [id:"BSprite_block_10"] */
        /* block comment [block:"BSprite_block_11" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BSprite_block_11" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (dy != /* param start [id:"BSprite_value_142" type:"number"] */0/* param end [id:"BSprite_value_142"] */ && !willCollide(xx, yy + dy)) {
            /* block comment [block:"BSprite_block_65" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"BSprite_block_65" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            yy += dy;
            /* block end [id:"BSprite_block_65"] */
            /* block comment [block:"BSprite_block_66" text:"Returns a value or ends the current method."] */
            /* block start [id:"BSprite_block_66" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return /* param start [id:"BSprite_value_143" type:"boolean"] */true/* param end [id:"BSprite_value_143"] */;
            /* block end [id:"BSprite_block_66"] */
        }
        /* block end [id:"BSprite_block_11"] */
        /* block comment [block:"BSprite_block_67" text:"Returns a value or ends the current method."] */
        /* block start [id:"BSprite_block_67" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return /* param start [id:"BSprite_value_144" type:"boolean"] */false/* param end [id:"BSprite_value_144"] */;
        /* block end [id:"BSprite_block_67"] */
    }

    public void goToFront() {
        /* block comment [block:"BSprite_block_68" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_68" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().goToFront(this);
        /* block end [id:"BSprite_block_68"] */
        /* block comment [block:"BSprite_block_12" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BSprite_block_12" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (minimapSprite != /* param start [id:"BSprite_value_145" type:"value"] */null/* param end [id:"BSprite_value_145"] */) {
            /* block comment [block:"BSprite_block_69" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"BSprite_block_69" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Stage.getInstance().goToFront(minimapSprite);
            /* block end [id:"BSprite_block_69"] */
        }
        /* block end [id:"BSprite_block_12"] */
    }

    /** Hide + detach from the scroller so dead/transient sprites stop costing updates. */
    public void destroy() {
        /* block comment [block:"BSprite_block_13" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"BSprite_block_13" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try {
            /* block comment [block:"BSprite_block_70" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"BSprite_block_70" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Scroller2d.getInstance().unregister(this);
            /* block end [id:"BSprite_block_70"] */
        }
        /* block end [id:"BSprite_block_13"] */ catch (Exception ignored) {}
        /* block comment [block:"BSprite_block_71" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"BSprite_block_71" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"BSprite_block_14" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BSprite_block_14" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (minimapSprite != /* param start [id:"BSprite_value_146" type:"value"] */null/* param end [id:"BSprite_value_146"] */) minimapSprite.hide();
        /* block end [id:"BSprite_block_71"] */
        /* block end [id:"BSprite_block_14"] */
        /* block comment [block:"BSprite_block_72" text:"Runs the hide action. Values in this block are editable inputs."] */
        /* block start [id:"BSprite_block_72" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
        hide();
        /* block end [id:"BSprite_block_72"] */
    }

    public void goToBack() {
        /* block comment [block:"BSprite_block_73" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_73" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().goToBack(this);
        /* block end [id:"BSprite_block_73"] */
    }

    public boolean isDirEqualTo(int dir) {
        /* block comment [block:"BSprite_block_74" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_74" category:"Variables" label:"set olddir" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double olddir = (Double) getVariable(/* param start [id:"BSprite_value_147" type:"text"] */"direction"/* param end [id:"BSprite_value_147"] */);
        /* block end [id:"BSprite_block_74"] */
        /* block comment [block:"BSprite_block_75" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_75" category:"Variables" label:"set dir2" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int dir2 = (int) olddir;
        /* block end [id:"BSprite_block_75"] */
        /* block comment [block:"BSprite_block_76" text:"Returns a value or ends the current method."] */
        /* block start [id:"BSprite_block_76" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return dir2 == dir;
        /* block end [id:"BSprite_block_76"] */
    }

    public void animateTurn(int dir, int time) {
        /* block comment [block:"BSprite_block_77" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_77" category:"Variables" label:"set olddir" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double olddir = (Double) getVariable(/* param start [id:"BSprite_value_148" type:"text"] */"direction"/* param end [id:"BSprite_value_148"] */);
        /* block end [id:"BSprite_block_77"] */
        /* block comment [block:"BSprite_block_78" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_78" category:"Variables" label:"set change" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double change = (dir - olddir + /* param start [id:"BSprite_value_149" type:"number"] */540/* param end [id:"BSprite_value_149"] */) % /* param start [id:"BSprite_value_150" type:"number"] */360/* param end [id:"BSprite_value_150"] */ - /* param start [id:"BSprite_value_151" type:"number"] */180/* param end [id:"BSprite_value_151"] */;
        /* block end [id:"BSprite_block_78"] */
        /* block comment [block:"BSprite_block_79" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"BSprite_block_79" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"BSprite_block_15" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BSprite_block_15" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (change == /* param start [id:"BSprite_value_152" type:"number"] */0/* param end [id:"BSprite_value_152"] */) return;
        /* block end [id:"BSprite_block_79"] */
        /* block end [id:"BSprite_block_15"] */
        /* block comment [block:"BSprite_block_16" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BSprite_block_16" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (time == /* param start [id:"BSprite_value_153" type:"number"] */0/* param end [id:"BSprite_value_153"] */) {
            /* block comment [block:"BSprite_block_80" text:"Runs the point in direction action. Values in this block are editable inputs."] */
            /* block start [id:"BSprite_block_80" category:"Motion" label:"point in direction" shape:"command" docs:"Runs the point in direction action. Values in this block are editable inputs."] */
            pointInDirection(dir);
            /* block end [id:"BSprite_block_80"] */
            /* block comment [block:"BSprite_block_81" text:"Calls the update method using its declared arguments."] */
            /* block start [id:"BSprite_block_81" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
            update();
            /* block end [id:"BSprite_block_81"] */
            /* block comment [block:"BSprite_block_82" text:"Returns a value or ends the current method."] */
            /* block start [id:"BSprite_block_82" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"BSprite_block_82"] */
        }
        /* block end [id:"BSprite_block_16"] */
        /* block comment [block:"BSprite_block_83" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_83" category:"Variables" label:"set turnamt" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double turnamt = change / time;
        /* block end [id:"BSprite_block_83"] */
        /* block comment [block:"BSprite_block_17" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"BSprite_block_17" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (int i = /* param start [id:"BSprite_value_154" type:"number"] */0/* param end [id:"BSprite_value_154"] */; i < time; i++) {
            /* block comment [block:"BSprite_block_84" text:"Runs the turn right action. Values in this block are editable inputs."] */
            /* block start [id:"BSprite_block_84" category:"Motion" label:"turn right" shape:"command" docs:"Runs the turn right action. Values in this block are editable inputs."] */
            turnRight(turnamt);
            /* block end [id:"BSprite_block_84"] */
            /* block comment [block:"BSprite_block_85" text:"Calls the update method using its declared arguments."] */
            /* block start [id:"BSprite_block_85" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
            update();
            /* block end [id:"BSprite_block_85"] */
            /* block comment [block:"BSprite_block_18" text:"Runs work safely and handles matching errors in a catch branch."] */
            /* block start [id:"BSprite_block_18" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
            try { /* block comment [block:"BSprite_block_86" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"BSprite_block_86" category:"Variables" label:"set sleep" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                Thread.sleep(/* param start [id:"BSprite_value_155" type:"number"] */1/* param end [id:"BSprite_value_155"] */);
                /* block end [id:"BSprite_block_86"] */ }
            /* block end [id:"BSprite_block_18"] */ catch (InterruptedException e) { /* block comment [block:"BSprite_block_87" text:"Calls the printStackTrace method using its declared arguments."] */
                /* block start [id:"BSprite_block_87" category:"My Blocks" label:"call print stack trace" shape:"command" docs:"Calls the printStackTrace method using its declared arguments."] */
                e.printStackTrace();
                /* block end [id:"BSprite_block_87"] */ }
        }
        /* block end [id:"BSprite_block_17"] */
    }

    public void animateTurn(int dir, double time, String curveType) {
        /* block comment [block:"BSprite_block_88" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_88" category:"Variables" label:"set olddir" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double olddir = (Double) getVariable(/* param start [id:"BSprite_value_156" type:"text"] */"direction"/* param end [id:"BSprite_value_156"] */);
        /* block end [id:"BSprite_block_88"] */
        /* block comment [block:"BSprite_block_89" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_89" category:"Variables" label:"set change" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double change = (dir - olddir + /* param start [id:"BSprite_value_157" type:"number"] */540/* param end [id:"BSprite_value_157"] */) % /* param start [id:"BSprite_value_158" type:"number"] */360/* param end [id:"BSprite_value_158"] */ - /* param start [id:"BSprite_value_159" type:"number"] */180/* param end [id:"BSprite_value_159"] */;
        /* block end [id:"BSprite_block_89"] */
        /* block comment [block:"BSprite_block_90" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"BSprite_block_90" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"BSprite_block_19" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"BSprite_block_19" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (change == /* param start [id:"BSprite_value_160" type:"number"] */0/* param end [id:"BSprite_value_160"] */) return;
        /* block end [id:"BSprite_block_90"] */
        /* block end [id:"BSprite_block_19"] */
        /* block comment [block:"BSprite_block_91" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_91" category:"Variables" label:"set steps" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int steps = (int) Math.abs(change);
        /* block end [id:"BSprite_block_91"] */
        /* block comment [block:"BSprite_block_92" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_92" category:"Variables" label:"set right" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        boolean right = change > /* param start [id:"BSprite_value_161" type:"number"] */0/* param end [id:"BSprite_value_161"] */;
        /* block end [id:"BSprite_block_92"] */
        /* block comment [block:"BSprite_block_20" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"BSprite_block_20" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (int i = /* param start [id:"BSprite_value_162" type:"number"] */0/* param end [id:"BSprite_value_162"] */; i < steps; i++) {
            /* block comment [block:"BSprite_block_93" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"BSprite_block_93" category:"Variables" label:"set t1" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double t1 = (double) i / steps;
            /* block end [id:"BSprite_block_93"] */
            /* block comment [block:"BSprite_block_94" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"BSprite_block_94" category:"Variables" label:"set t2" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double t2 = (double) (i + /* param start [id:"BSprite_value_163" type:"number"] */1/* param end [id:"BSprite_value_163"] */) / steps;
            /* block end [id:"BSprite_block_94"] */
            /* block comment [block:"BSprite_block_95" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"BSprite_block_95" category:"Variables" label:"set c1" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double c1 = applyCurve(t1, curveType);
            /* block end [id:"BSprite_block_95"] */
            /* block comment [block:"BSprite_block_96" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"BSprite_block_96" category:"Variables" label:"set c2" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double c2 = applyCurve(t2, curveType);
            /* block end [id:"BSprite_block_96"] */
            /* block comment [block:"BSprite_block_97" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"BSprite_block_97" category:"Variables" label:"set delta" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            double delta = (c2 - c1) * time;
            /* block end [id:"BSprite_block_97"] */
            /* block comment [block:"BSprite_block_21" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"BSprite_block_21" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (right) { /* block comment [block:"BSprite_block_98" text:"Runs the turn right action. Values in this block are editable inputs."] */
                /* block start [id:"BSprite_block_98" category:"Motion" label:"turn right" shape:"command" docs:"Runs the turn right action. Values in this block are editable inputs."] */
                turnRight(/* param start [id:"BSprite_value_164" type:"number"] */1/* param end [id:"BSprite_value_164"] */);
                /* block end [id:"BSprite_block_98"] */ } else { /* block comment [block:"BSprite_block_99" text:"Runs the turn left action. Values in this block are editable inputs."] */
                /* block start [id:"BSprite_block_99" category:"Motion" label:"turn left" shape:"command" docs:"Runs the turn left action. Values in this block are editable inputs."] */
                turnLeft(/* param start [id:"BSprite_value_165" type:"number"] */1/* param end [id:"BSprite_value_165"] */);
                /* block end [id:"BSprite_block_99"] */ }
            /* block end [id:"BSprite_block_21"] */
            /* block comment [block:"BSprite_block_100" text:"Calls the update method using its declared arguments."] */
            /* block start [id:"BSprite_block_100" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
            update();
            /* block end [id:"BSprite_block_100"] */
            /* block comment [block:"BSprite_block_22" text:"Runs work safely and handles matching errors in a catch branch."] */
            /* block start [id:"BSprite_block_22" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
            try { /* block comment [block:"BSprite_block_101" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"BSprite_block_101" category:"Variables" label:"set sleep" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                Thread.sleep((int)(delta * /* param start [id:"BSprite_value_166" type:"number"] */1000/* param end [id:"BSprite_value_166"] */));
                /* block end [id:"BSprite_block_101"] */ }
            /* block end [id:"BSprite_block_22"] */ catch (InterruptedException e) { /* block comment [block:"BSprite_block_102" text:"Calls the printStackTrace method using its declared arguments."] */
                /* block start [id:"BSprite_block_102" category:"My Blocks" label:"call print stack trace" shape:"command" docs:"Calls the printStackTrace method using its declared arguments."] */
                e.printStackTrace();
                /* block end [id:"BSprite_block_102"] */ }
        }
        /* block end [id:"BSprite_block_20"] */
    }

    private double applyCurve(double t, String type) {
        /* block comment [block:"BSprite_block_23" text:"Selects and runs the case that matches the input value."] */
        /* block start [id:"BSprite_block_23" category:"Control" label:"choose a case" shape:"control" docs:"Selects and runs the case that matches the input value."] */
        switch (type) {
            /* block comment [block:"BSprite_block_103" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"BSprite_block_103" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            case /* param start [id:"BSprite_value_167" type:"text"] */"easeIn"/* param end [id:"BSprite_value_167"] */: return t * t;
            /* block end [id:"BSprite_block_103"] */
            /* block comment [block:"BSprite_block_104" text:"Calls the pow method using its declared arguments."] */
            /* block start [id:"BSprite_block_104" category:"My Blocks" label:"call pow" shape:"command" docs:"Calls the pow method using its declared arguments."] */
            case /* param start [id:"BSprite_value_168" type:"text"] */"easeOut"/* param end [id:"BSprite_value_168"] */: return /* param start [id:"BSprite_value_169" type:"number"] */1/* param end [id:"BSprite_value_169"] */ - Math.pow(/* param start [id:"BSprite_value_170" type:"number"] */1/* param end [id:"BSprite_value_170"] */ - t, /* param start [id:"BSprite_value_171" type:"number"] */2/* param end [id:"BSprite_value_171"] */);
            /* block end [id:"BSprite_block_104"] */
            /* block comment [block:"BSprite_block_105" text:"Calls the pow method using its declared arguments."] */
            /* block start [id:"BSprite_block_105" category:"My Blocks" label:"call pow" shape:"command" docs:"Calls the pow method using its declared arguments."] */
            case /* param start [id:"BSprite_value_172" type:"text"] */"easeInOut"/* param end [id:"BSprite_value_172"] */:
            return t < /* param start [id:"BSprite_value_173" type:"number"] */0.5/* param end [id:"BSprite_value_173"] */ ? /* param start [id:"BSprite_value_174" type:"number"] */2/* param end [id:"BSprite_value_174"] */ * t * t : /* param start [id:"BSprite_value_175" type:"number"] */1/* param end [id:"BSprite_value_175"] */ - Math.pow(-/* param start [id:"BSprite_value_176" type:"number"] */2/* param end [id:"BSprite_value_176"] */ * t + /* param start [id:"BSprite_value_177" type:"number"] */2/* param end [id:"BSprite_value_177"] */, /* param start [id:"BSprite_value_178" type:"number"] */2/* param end [id:"BSprite_value_178"] */) / /* param start [id:"BSprite_value_179" type:"number"] */2/* param end [id:"BSprite_value_179"] */;
            /* block end [id:"BSprite_block_105"] */
            /* block comment [block:"BSprite_block_106" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"BSprite_block_106" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            default: return t;
            /* block end [id:"BSprite_block_106"] */
        }
        /* block end [id:"BSprite_block_23"] */
    }

    public boolean isOnStage() {
        /* block comment [block:"BSprite_block_107" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_107" category:"Variables" label:"set stage" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage stage = Stage.getInstance();
        /* block end [id:"BSprite_block_107"] */
        /* block comment [block:"BSprite_block_108" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_108" category:"Variables" label:"set displayScale" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double displayScale = stage.getScaleFactor();
        /* block end [id:"BSprite_block_108"] */
        /* block comment [block:"BSprite_block_109" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_109" category:"Variables" label:"set halfW" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double halfW = (width() / displayScale) / /* param start [id:"BSprite_value_180" type:"number"] */2.0/* param end [id:"BSprite_value_180"] */;
        /* block end [id:"BSprite_block_109"] */
        /* block comment [block:"BSprite_block_110" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_110" category:"Variables" label:"set halfH" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double halfH = (height() / displayScale) / /* param start [id:"BSprite_value_181" type:"number"] */2.0/* param end [id:"BSprite_value_181"] */;
        /* block end [id:"BSprite_block_110"] */
        /* block comment [block:"BSprite_block_111" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_111" category:"Variables" label:"set left" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double left   = xx - halfW;
        /* block end [id:"BSprite_block_111"] */
        /* block comment [block:"BSprite_block_112" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_112" category:"Variables" label:"set right" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double right  = xx + halfW;
        /* block end [id:"BSprite_block_112"] */
        /* block comment [block:"BSprite_block_113" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_113" category:"Variables" label:"set bottom" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double bottom = yy - halfH;
        /* block end [id:"BSprite_block_113"] */
        /* block comment [block:"BSprite_block_114" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"BSprite_block_114" category:"Variables" label:"set top" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double top    = yy + halfH;
        /* block end [id:"BSprite_block_114"] */
        /* block comment [block:"BSprite_block_115" text:"Returns a value or ends the current method."] */
        /* block start [id:"BSprite_block_115" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return right >= -/* param start [id:"BSprite_value_182" type:"number"] */240/* param end [id:"BSprite_value_182"] */ && left <= /* param start [id:"BSprite_value_183" type:"number"] */240/* param end [id:"BSprite_value_183"] */ && top >= -/* param start [id:"BSprite_value_184" type:"number"] */180/* param end [id:"BSprite_value_184"] */ && bottom <= /* param start [id:"BSprite_value_185" type:"number"] */180/* param end [id:"BSprite_value_185"] */;
        /* block end [id:"BSprite_block_115"] */
    }

    @SuppressWarnings("unchecked")
    public void resetCostumes() {
        /* block comment [block:"BSprite_block_24" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"BSprite_block_24" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try {

            // Reflection only — engine classes stay untouched.
            /* block comment [block:"BSprite_block_116" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"BSprite_block_116" category:"Variables" label:"set costumesField" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Field costumesField = Sprite.class.getDeclaredField(/* param start [id:"BSprite_value_186" type:"text"] */"costumes"/* param end [id:"BSprite_value_186"] */);
            /* block end [id:"BSprite_block_116"] */
            /* block comment [block:"BSprite_block_117" text:"Runs the set accessible action. Values in this block are editable inputs."] */
            /* block start [id:"BSprite_block_117" category:"Variables" label:"set accessible" shape:"command" docs:"Runs the set accessible action. Values in this block are editable inputs."] */
            costumesField.setAccessible(/* param start [id:"BSprite_value_187" type:"boolean"] */true/* param end [id:"BSprite_value_187"] */);
            /* block end [id:"BSprite_block_117"] */
            /* block comment [block:"BSprite_block_118" text:"Runs the get action. Values in this block are editable inputs."] */
            /* block start [id:"BSprite_block_118" category:"My Blocks" label:"get" shape:"command" docs:"Runs the get action. Values in this block are editable inputs."] */
            ((java.util.List<?>) costumesField.get(this)).clear();
            /* block end [id:"BSprite_block_118"] */

            // Reset the costume index back to the first costume.
            /* block comment [block:"BSprite_block_119" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"BSprite_block_119" category:"Variables" label:"set indexField" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Field indexField = Sprite.class.getDeclaredField(/* param start [id:"BSprite_value_188" type:"text"] */"costumeIndex"/* param end [id:"BSprite_value_188"] */);
            /* block end [id:"BSprite_block_119"] */
            /* block comment [block:"BSprite_block_120" text:"Runs the set accessible action. Values in this block are editable inputs."] */
            /* block start [id:"BSprite_block_120" category:"Variables" label:"set accessible" shape:"command" docs:"Runs the set accessible action. Values in this block are editable inputs."] */
            indexField.setAccessible(/* param start [id:"BSprite_value_189" type:"boolean"] */true/* param end [id:"BSprite_value_189"] */);
            /* block end [id:"BSprite_block_120"] */
            /* block comment [block:"BSprite_block_121" text:"Runs the set int action. Values in this block are editable inputs."] */
            /* block start [id:"BSprite_block_121" category:"Variables" label:"set int" shape:"command" docs:"Runs the set int action. Values in this block are editable inputs."] */
            indexField.setInt(this, /* param start [id:"BSprite_value_190" type:"number"] */0/* param end [id:"BSprite_value_190"] */);
            /* block end [id:"BSprite_block_121"] */

            // Keep the name→index cache consistent with the cleared list.
            /* block comment [block:"BSprite_block_122" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"BSprite_block_122" category:"Variables" label:"set cacheField" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Field cacheField = Sprite.class.getDeclaredField(/* param start [id:"BSprite_value_191" type:"text"] */"costumeIndexByName"/* param end [id:"BSprite_value_191"] */);
            /* block end [id:"BSprite_block_122"] */
            /* block comment [block:"BSprite_block_123" text:"Runs the set accessible action. Values in this block are editable inputs."] */
            /* block start [id:"BSprite_block_123" category:"Variables" label:"set accessible" shape:"command" docs:"Runs the set accessible action. Values in this block are editable inputs."] */
            cacheField.setAccessible(/* param start [id:"BSprite_value_192" type:"boolean"] */true/* param end [id:"BSprite_value_192"] */);
            /* block end [id:"BSprite_block_123"] */
            /* block comment [block:"BSprite_block_124" text:"Runs the get action. Values in this block are editable inputs."] */
            /* block start [id:"BSprite_block_124" category:"My Blocks" label:"get" shape:"command" docs:"Runs the get action. Values in this block are editable inputs."] */
            ((java.util.Map<?, ?>) cacheField.get(this)).clear();
            /* block end [id:"BSprite_block_124"] */
        }
        /* block end [id:"BSprite_block_24"] */ catch (Exception e) {
            /* block comment [block:"BSprite_block_125" text:"Raises an error for a matching catch block."] */
            /* block start [id:"BSprite_block_125" category:"Control" label:"throw error" shape:"command" docs:"Raises an error for a matching catch block."] */
            throw new RuntimeException(/* param start [id:"BSprite_value_193" type:"text"] */"Failed to reset costumes"/* param end [id:"BSprite_value_193"] */, e);
            /* block end [id:"BSprite_block_125"] */
        }
    }
    /* slot end [id:"BSprite_members"] */
}
