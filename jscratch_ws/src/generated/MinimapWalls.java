package generated;

import com.jscratch.Costume;
import com.jscratch.Sprite;
import com.jscratch.Stage;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;

/**
* Single overlay sprite that draws every active wall rect on the minimap.
* Same convention as Sprite.draw: screen px = scratch * displayScale + stage/2
* with Y flipped, where scratch = CENTER + rel*SCALE. Keeps walls in the
* top-right minimap window with bg/tiles/icons.
* Reads FloorManager.peekWalls() so it never forces early init.
*
* <p>Call graph: owned once by {@code Scroller2d}; drawn per frame by
* {@code Stage}; reads {@code FloorManager.peekWalls()} (border + interior
* rects) and the scroller offset for the player anchor. Culled per rect.
*/
public class MinimapWalls extends Sprite {
    private static final Color MINI_WALL = new Color(28, 26, 34);

    public MinimapWalls() {
        /* block comment [block:"MinimapWalls_block_7" text:"Runs the super action. Values in this block are editable inputs."] */
        /* block start [id:"MinimapWalls_block_7" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
        super(/* param start [id:"MinimapWalls_value_31" type:"text"] */"MinimapWalls"/* param end [id:"MinimapWalls_value_31"] */);
        /* block end [id:"MinimapWalls_block_7"] */
        /* block comment [block:"MinimapWalls_block_8" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"MinimapWalls_block_8" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(new Costume(/* param start [id:"MinimapWalls_value_32" type:"text"] */"blank"/* param end [id:"MinimapWalls_value_32"] */, /* param start [id:"MinimapWalls_value_33" type:"text"] */"jscratch_ws/assets/error.png"/* param end [id:"MinimapWalls_value_33"] */, /* param start [id:"MinimapWalls_value_34" type:"number"] */0/* param end [id:"MinimapWalls_value_34"] */, /* param start [id:"MinimapWalls_value_35" type:"number"] */0/* param end [id:"MinimapWalls_value_35"] */));
        /* block end [id:"MinimapWalls_block_8"] */
    }

    @Override
    public void draw(Graphics2D g) {
        /* block comment [block:"MinimapWalls_block_9" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"MinimapWalls_block_9" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"MinimapWalls_block_1" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"MinimapWalls_block_1" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!isVisible()) return;
        /* block end [id:"MinimapWalls_block_9"] */
        /* block end [id:"MinimapWalls_block_1"] */
        /* block comment [block:"MinimapWalls_block_10" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MinimapWalls_block_10" category:"Variables" label:"set int" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        List<int[]> walls = FloorManager.peekWalls();
        /* block end [id:"MinimapWalls_block_10"] */
        /* block comment [block:"MinimapWalls_block_11" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"MinimapWalls_block_11" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"MinimapWalls_block_2" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"MinimapWalls_block_2" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (walls == /* param start [id:"MinimapWalls_value_36" type:"value"] */null/* param end [id:"MinimapWalls_value_36"] */ || walls.isEmpty()) return;
        /* block end [id:"MinimapWalls_block_11"] */
        /* block end [id:"MinimapWalls_block_2"] */
        /* block comment [block:"MinimapWalls_block_12" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MinimapWalls_block_12" category:"Variables" label:"set stage" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage stage = Stage.getInstance();
        /* block end [id:"MinimapWalls_block_12"] */
        /* block comment [block:"MinimapWalls_block_13" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MinimapWalls_block_13" category:"Variables" label:"set displayScale" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double displayScale = stage.getScaleFactor();
        /* block end [id:"MinimapWalls_block_13"] */
        /* block comment [block:"MinimapWalls_block_14" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MinimapWalls_block_14" category:"Variables" label:"set sc" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Scroller2d sc = Scroller2d.getInstance();
        /* block end [id:"MinimapWalls_block_14"] */
        /* block comment [block:"MinimapWalls_block_15" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MinimapWalls_block_15" category:"Variables" label:"set playerX" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int playerX = -sc.x;
        /* block end [id:"MinimapWalls_block_15"] */
        /* block comment [block:"MinimapWalls_block_16" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"MinimapWalls_block_16" category:"Variables" label:"set playerY" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int playerY = -sc.y;
        /* block end [id:"MinimapWalls_block_16"] */

        /* block comment [block:"MinimapWalls_block_17" text:"Runs the get transform action. Values in this block are editable inputs."] */
        /* block start [id:"MinimapWalls_block_17" category:"My Blocks" label:"get transform" shape:"command" docs:"Runs the get transform action. Values in this block are editable inputs."] */
        java.awt.geom.AffineTransform old = g.getTransform();
        /* block end [id:"MinimapWalls_block_17"] */
        /* block comment [block:"MinimapWalls_block_3" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"MinimapWalls_block_3" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try {
            /* block comment [block:"MinimapWalls_block_18" text:"Runs the set color action. Values in this block are editable inputs."] */
            /* block start [id:"MinimapWalls_block_18" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
            g.setColor(MINI_WALL);
            /* block end [id:"MinimapWalls_block_18"] */
            /* block comment [block:"MinimapWalls_block_4" text:"Repeats the nested operations for each loop turn."] */
            /* block start [id:"MinimapWalls_block_4" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
            for (int[] r : walls) {
                /* block comment [block:"MinimapWalls_block_19" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"MinimapWalls_block_19" category:"Variables" label:"set relX" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int relX = (int) ((r[/* param start [id:"MinimapWalls_value_40" type:"number"] */0/* param end [id:"MinimapWalls_value_40"] */] - playerX) * MinimapConfig.SCALE);
                /* block end [id:"MinimapWalls_block_19"] */
                /* block comment [block:"MinimapWalls_block_20" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"MinimapWalls_block_20" category:"Variables" label:"set relY" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int relY = (int) ((r[/* param start [id:"MinimapWalls_value_41" type:"number"] */1/* param end [id:"MinimapWalls_value_41"] */] - playerY) * MinimapConfig.SCALE);
                /* block end [id:"MinimapWalls_block_20"] */
                // Cull rects fully outside the minimap window
                /* block comment [block:"MinimapWalls_block_21" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"MinimapWalls_block_21" category:"Variables" label:"set hw" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int hw = (int) (r[/* param start [id:"MinimapWalls_value_42" type:"number"] */2/* param end [id:"MinimapWalls_value_42"] */] * MinimapConfig.SCALE / /* param start [id:"MinimapWalls_value_43" type:"number"] */2/* param end [id:"MinimapWalls_value_43"] */) + /* param start [id:"MinimapWalls_value_44" type:"number"] */2/* param end [id:"MinimapWalls_value_44"] */;
                /* block end [id:"MinimapWalls_block_21"] */
                /* block comment [block:"MinimapWalls_block_22" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"MinimapWalls_block_22" category:"Variables" label:"set hh" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int hh = (int) (r[/* param start [id:"MinimapWalls_value_45" type:"number"] */3/* param end [id:"MinimapWalls_value_45"] */] * MinimapConfig.SCALE / /* param start [id:"MinimapWalls_value_46" type:"number"] */2/* param end [id:"MinimapWalls_value_46"] */) + /* param start [id:"MinimapWalls_value_47" type:"number"] */2/* param end [id:"MinimapWalls_value_47"] */;
                /* block end [id:"MinimapWalls_block_22"] */
                /* block comment [block:"MinimapWalls_block_23" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"MinimapWalls_block_23" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"MinimapWalls_block_5" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"MinimapWalls_block_5" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (Math.abs(relX) - hw > MinimapConfig.WIDTH / /* param start [id:"MinimapWalls_value_48" type:"number"] */2/* param end [id:"MinimapWalls_value_48"] */) continue;
                /* block end [id:"MinimapWalls_block_23"] */
                /* block end [id:"MinimapWalls_block_5"] */
                /* block comment [block:"MinimapWalls_block_24" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"MinimapWalls_block_24" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"MinimapWalls_block_6" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"MinimapWalls_block_6" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (Math.abs(relY) - hh > MinimapConfig.HEIGHT / /* param start [id:"MinimapWalls_value_49" type:"number"] */2/* param end [id:"MinimapWalls_value_49"] */) continue;
                /* block end [id:"MinimapWalls_block_24"] */
                /* block end [id:"MinimapWalls_block_6"] */
                // Match Sprite.draw convention: screen = scratch + stage/2,
                // and Y is flipped (scratch +Y = up). Keeps walls under the
                // top-right bg/border/tiles/icons.
                /* block comment [block:"MinimapWalls_block_25" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"MinimapWalls_block_25" category:"Variables" label:"set sx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int sx = (int) ((MinimapConfig.CENTER_X + relX) * displayScale + stage.getWidth() / /* param start [id:"MinimapWalls_value_50" type:"number"] */2.0/* param end [id:"MinimapWalls_value_50"] */);
                /* block end [id:"MinimapWalls_block_25"] */
                /* block comment [block:"MinimapWalls_block_26" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"MinimapWalls_block_26" category:"Variables" label:"set sy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int sy = (int) (stage.getHeight() / /* param start [id:"MinimapWalls_value_51" type:"number"] */2.0/* param end [id:"MinimapWalls_value_51"] */ - (MinimapConfig.CENTER_Y + relY) * displayScale);
                /* block end [id:"MinimapWalls_block_26"] */
                /* block comment [block:"MinimapWalls_block_27" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"MinimapWalls_block_27" category:"Variables" label:"set dw" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int dw = Math.max(/* param start [id:"MinimapWalls_value_52" type:"number"] */1/* param end [id:"MinimapWalls_value_52"] */, (int) (r[/* param start [id:"MinimapWalls_value_53" type:"number"] */2/* param end [id:"MinimapWalls_value_53"] */] * MinimapConfig.SCALE * displayScale));
                /* block end [id:"MinimapWalls_block_27"] */
                /* block comment [block:"MinimapWalls_block_28" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"MinimapWalls_block_28" category:"Variables" label:"set dh" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int dh = Math.max(/* param start [id:"MinimapWalls_value_54" type:"number"] */1/* param end [id:"MinimapWalls_value_54"] */, (int) (r[/* param start [id:"MinimapWalls_value_55" type:"number"] */3/* param end [id:"MinimapWalls_value_55"] */] * MinimapConfig.SCALE * displayScale));
                /* block end [id:"MinimapWalls_block_28"] */
                /* block comment [block:"MinimapWalls_block_29" text:"Calls the fillRect method using its declared arguments."] */
                /* block start [id:"MinimapWalls_block_29" category:"My Blocks" label:"call fill rect" shape:"command" docs:"Calls the fillRect method using its declared arguments."] */
                g.fillRect(sx - dw / /* param start [id:"MinimapWalls_value_56" type:"number"] */2/* param end [id:"MinimapWalls_value_56"] */, sy - dh / /* param start [id:"MinimapWalls_value_57" type:"number"] */2/* param end [id:"MinimapWalls_value_57"] */, dw, dh);
                /* block end [id:"MinimapWalls_block_29"] */
            }
            /* block end [id:"MinimapWalls_block_4"] */
        }
        /* block end [id:"MinimapWalls_block_3"] */ finally {
            /* block comment [block:"MinimapWalls_block_30" text:"Runs the set transform action. Values in this block are editable inputs."] */
            /* block start [id:"MinimapWalls_block_30" category:"Variables" label:"set transform" shape:"command" docs:"Runs the set transform action. Values in this block are editable inputs."] */
            g.setTransform(old);
            /* block end [id:"MinimapWalls_block_30"] */
        }
    }
}
