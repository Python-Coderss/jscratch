package generated;

import com.jscratch.Costume;
import com.jscratch.Stage;

import java.awt.Color;
import java.awt.Graphics2D;

/**
* Staircase down, rendered from the stairs art asset.
* Step within ~55 world units to descend. Labels use the BitmapFont glyph art.
*/
public class Stairs extends BSprite {
    private static final Color LABEL = Color.WHITE;

    public Stairs() {
        /* block comment [block:"Stairs_block_5" text:"Runs the super action. Values in this block are editable inputs."] */
        /* block start [id:"Stairs_block_5" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
        super(/* param start [id:"Stairs_value_30" type:"text"] */"Stairs"/* param end [id:"Stairs_value_30"] */);
        /* block end [id:"Stairs_block_5"] */
        /* block comment [block:"Stairs_block_6" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"Stairs_block_6" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(new Costume(/* param start [id:"Stairs_value_31" type:"text"] */"stairs"/* param end [id:"Stairs_value_31"] */, /* param start [id:"Stairs_value_32" type:"text"] */"jscratch_ws/assets/stairs (entrance l walk to r).png"/* param end [id:"Stairs_value_32"] */, /* param start [id:"Stairs_value_33" type:"number"] */20/* param end [id:"Stairs_value_33"] */, /* param start [id:"Stairs_value_34" type:"number"] */15/* param end [id:"Stairs_value_34"] */));
        /* block end [id:"Stairs_block_6"] */
        /* block comment [block:"Stairs_block_7" text:"Runs the set size action. Values in this block are editable inputs."] */
        /* block start [id:"Stairs_block_7" category:"Looks" label:"set size" shape:"command" docs:"Runs the set size action. Values in this block are editable inputs."] */
        setSize(/* param start [id:"Stairs_value_35" type:"number"] */270/* param end [id:"Stairs_value_35"] */);
        /* block end [id:"Stairs_block_7"] */
        /* block comment [block:"Stairs_block_8" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Stairs_block_8" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Stairs_block_1" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Stairs_block_1" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (minimapSprite != /* param start [id:"Stairs_value_36" type:"value"] */null/* param end [id:"Stairs_value_36"] */) minimapSprite.show();
        /* block end [id:"Stairs_block_8"] */
        /* block end [id:"Stairs_block_1"] */
        /* block comment [block:"Stairs_block_9" text:"Calls the update method using its declared arguments."] */
        /* block start [id:"Stairs_block_9" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
        update();
        /* block end [id:"Stairs_block_9"] */
        /* block comment [block:"Stairs_block_10" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Stairs_block_10" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Stairs_block_2" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Stairs_block_2" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (isVisible()) show();
        /* block end [id:"Stairs_block_10"] */
        /* block end [id:"Stairs_block_2"] */
    }

    @Override
    public void draw(Graphics2D g) {
        /* block comment [block:"Stairs_block_11" text:"Runs the draw action. Values in this block are editable inputs."] */
        /* block start [id:"Stairs_block_11" category:"My Blocks" label:"draw" shape:"command" docs:"Runs the draw action. Values in this block are editable inputs."] */
        super.draw(g);
        /* block end [id:"Stairs_block_11"] */
        /* block comment [block:"Stairs_block_12" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Stairs_block_12" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Stairs_block_3" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Stairs_block_3" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!isVisible()) return;
        /* block end [id:"Stairs_block_12"] */
        /* block end [id:"Stairs_block_3"] */
        /* block comment [block:"Stairs_block_13" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Stairs_block_13" category:"Variables" label:"set stage" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage stage = Stage.getInstance();
        /* block end [id:"Stairs_block_13"] */
        /* block comment [block:"Stairs_block_14" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Stairs_block_14" category:"Variables" label:"set w" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int w = stage.getWidth();
        /* block end [id:"Stairs_block_14"] */
        /* block comment [block:"Stairs_block_15" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Stairs_block_15" category:"Variables" label:"set h" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int h = stage.getHeight();
        /* block end [id:"Stairs_block_15"] */
        /* block comment [block:"Stairs_block_16" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Stairs_block_16" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Stairs_block_4" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Stairs_block_4" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (w <= /* param start [id:"Stairs_value_37" type:"number"] */0/* param end [id:"Stairs_value_37"] */ || h <= /* param start [id:"Stairs_value_38" type:"number"] */0/* param end [id:"Stairs_value_38"] */) return;
        /* block end [id:"Stairs_block_16"] */
        /* block end [id:"Stairs_block_4"] */
        /* block comment [block:"Stairs_block_17" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Stairs_block_17" category:"Variables" label:"set fontSize" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int fontSize = Math.max(/* param start [id:"Stairs_value_39" type:"number"] */8/* param end [id:"Stairs_value_39"] */, (int) (/* param start [id:"Stairs_value_40" type:"number"] */11/* param end [id:"Stairs_value_40"] */ * stage.getScaleFactor()));
        /* block end [id:"Stairs_block_17"] */
        /* block comment [block:"Stairs_block_18" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Stairs_block_18" category:"Variables" label:"set f" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        BitmapFont.Style f = BitmapFont.style(fontSize, true);
        /* block end [id:"Stairs_block_18"] */
        /* block comment [block:"Stairs_block_20" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Stairs_block_20" category:"Variables" label:"set ascent" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int ascent = BitmapFont.capHeight(f);
        /* block end [id:"Stairs_block_20"] */
        /* block comment [block:"Stairs_block_21" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Stairs_block_21" category:"Variables" label:"set sx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int sx = (int) (getX() + w / /* param start [id:"Stairs_value_42" type:"number"] */2.0/* param end [id:"Stairs_value_42"] */);
        /* block end [id:"Stairs_block_21"] */
        /* block comment [block:"Stairs_block_22" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Stairs_block_22" category:"Variables" label:"set sy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int sy = (int) (h / /* param start [id:"Stairs_value_43" type:"number"] */2.0/* param end [id:"Stairs_value_43"] */ - getY());
        /* block end [id:"Stairs_block_22"] */
        /* block comment [block:"Stairs_block_23" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Stairs_block_23" category:"Variables" label:"set a" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        String a = /* param start [id:"Stairs_value_44" type:"text"] */"STAIRS"/* param end [id:"Stairs_value_44"] */;
        /* block end [id:"Stairs_block_23"] */
        /* block comment [block:"Stairs_block_24" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Stairs_block_24" category:"Variables" label:"set b" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        String b = /* param start [id:"Stairs_value_45" type:"text"] */"DOWN v"/* param end [id:"Stairs_value_45"] */;
        /* block end [id:"Stairs_block_24"] */
        /* block comment [block:"Stairs_block_25" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Stairs_block_25" category:"Variables" label:"set wa" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int wa = BitmapFont.width(a, f);
        /* block end [id:"Stairs_block_25"] */
        /* block comment [block:"Stairs_block_26" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Stairs_block_26" category:"Variables" label:"set wb" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int wb = BitmapFont.width(b, f);
        /* block end [id:"Stairs_block_26"] */
        /* block comment [block:"Stairs_block_27" text:"Runs the set color action. Values in this block are editable inputs."] */
        /* block start [id:"Stairs_block_27" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
        g.setColor(LABEL);
        /* block end [id:"Stairs_block_27"] */
        /* block comment [block:"Stairs_block_28" text:"Calls the drawString method using its declared arguments."] */
        /* block start [id:"Stairs_block_28" category:"My Blocks" label:"call draw string" shape:"command" docs:"Calls the drawString method using its declared arguments."] */
        BitmapFont.draw(g, a, sx - wa / /* param start [id:"Stairs_value_46" type:"number"] */2/* param end [id:"Stairs_value_46"] */, sy - /* param start [id:"Stairs_value_47" type:"number"] */5/* param end [id:"Stairs_value_47"] */ - ascent, f);
        /* block end [id:"Stairs_block_28"] */
        /* block comment [block:"Stairs_block_29" text:"Calls the drawString method using its declared arguments."] */
        /* block start [id:"Stairs_block_29" category:"My Blocks" label:"call draw string" shape:"command" docs:"Calls the drawString method using its declared arguments."] */
        BitmapFont.draw(g, b, sx - wb / /* param start [id:"Stairs_value_48" type:"number"] */2/* param end [id:"Stairs_value_48"] */, sy + /* param start [id:"Stairs_value_49" type:"number"] */6/* param end [id:"Stairs_value_49"] */ + ascent, f);
        /* block end [id:"Stairs_block_29"] */
    }
}
