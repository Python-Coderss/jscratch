package generated;

import com.jscratch.*;
import java.awt.Color;
import java.awt.event.KeyEvent;

/**
* World-anchored UI sprite (HP bar segments, hearts). Like BSprite but for
* screen-space widgets that track a world point: goTo adds the scroller
* offset and multiplies by the stage display scale, so bars sit over heads
* at any window size. Parent of HPBAR and its BarFilled/BarEmpty segments.
*/
public class offsetSprite extends Sprite {
    Scroller2d instance;
    /* slot start [id:"offsetSprite_members" tags:"inClassNotMethod"] */
    public offsetSprite(String name) {
        /* block comment [block:"offsetSprite_block_7" text:"Runs the super action. Values in this block are editable inputs."] */
        /* block start [id:"offsetSprite_block_7" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
        super(name);
        /* block end [id:"offsetSprite_block_7"] */
        /* block comment [block:"offsetSprite_block_8" text:"Runs the get instance action. Values in this block are editable inputs."] */
        /* block start [id:"offsetSprite_block_8" category:"My Blocks" label:"get instance" shape:"command" docs:"Runs the get instance action. Values in this block are editable inputs."] */
        instance = Scroller2d.getInstance();
        /* block end [id:"offsetSprite_block_8"] */
        // Example: addCostume(new Costume("costume1", "path/to/img.png"));

        /* slot start [id:"offsetSprite_init" tags:"inInstanceMethod"] */
        /* slot end [id:"offsetSprite_init"] */
    }
    public void goTo(int x, int y) {
        /* block comment [block:"offsetSprite_block_9" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"offsetSprite_block_9" category:"Variables" label:"set ds" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double ds = /* param start [id:"offsetSprite_value_20" type:"number"] */1.0/* param end [id:"offsetSprite_value_20"] */;
        /* block end [id:"offsetSprite_block_9"] */
        /* block comment [block:"offsetSprite_block_1" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"offsetSprite_block_1" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try {
            /* block comment [block:"offsetSprite_block_10" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"offsetSprite_block_10" category:"Variables" label:"set st" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Stage st = Stage.getInstance();
            /* block end [id:"offsetSprite_block_10"] */
            /* block comment [block:"offsetSprite_block_11" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"offsetSprite_block_11" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"offsetSprite_block_2" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"offsetSprite_block_2" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (st != /* param start [id:"offsetSprite_value_21" type:"value"] */null/* param end [id:"offsetSprite_value_21"] */) ds = st.getScaleFactor();
            /* block end [id:"offsetSprite_block_11"] */
            /* block end [id:"offsetSprite_block_2"] */
        }
        /* block end [id:"offsetSprite_block_1"] */ catch (Exception ignored) {}
        /* block comment [block:"offsetSprite_block_12" text:"Runs the go to action. Values in this block are editable inputs."] */
        /* block start [id:"offsetSprite_block_12" category:"Motion" label:"go to" shape:"command" docs:"Runs the go to action. Values in this block are editable inputs."] */
        super.goTo((instance.x + x) * ds, (instance.y + y) * ds);
        /* block end [id:"offsetSprite_block_12"] */
    }

    public void goTo(double x, double y) {
        /* block comment [block:"offsetSprite_block_13" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"offsetSprite_block_13" category:"Variables" label:"set ds" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double ds = /* param start [id:"offsetSprite_value_22" type:"number"] */1.0/* param end [id:"offsetSprite_value_22"] */;
        /* block end [id:"offsetSprite_block_13"] */
        /* block comment [block:"offsetSprite_block_3" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"offsetSprite_block_3" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try {
            /* block comment [block:"offsetSprite_block_14" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"offsetSprite_block_14" category:"Variables" label:"set st" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Stage st = Stage.getInstance();
            /* block end [id:"offsetSprite_block_14"] */
            /* block comment [block:"offsetSprite_block_15" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"offsetSprite_block_15" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"offsetSprite_block_4" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"offsetSprite_block_4" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (st != /* param start [id:"offsetSprite_value_23" type:"value"] */null/* param end [id:"offsetSprite_value_23"] */) ds = st.getScaleFactor();
            /* block end [id:"offsetSprite_block_15"] */
            /* block end [id:"offsetSprite_block_4"] */
        }
        /* block end [id:"offsetSprite_block_3"] */ catch (Exception ignored) {}
        /* block comment [block:"offsetSprite_block_16" text:"Runs the go to action. Values in this block are editable inputs."] */
        /* block start [id:"offsetSprite_block_16" category:"Motion" label:"go to" shape:"command" docs:"Runs the go to action. Values in this block are editable inputs."] */
        super.goTo((instance.x + x) * ds, (instance.y + y) * ds);
        /* block end [id:"offsetSprite_block_16"] */
    }

    /** Display scale for sizing world-anchored UI (bars, markers). */
    protected double dispScale() {
        /* block comment [block:"offsetSprite_block_5" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"offsetSprite_block_5" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try {
            /* block comment [block:"offsetSprite_block_17" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"offsetSprite_block_17" category:"Variables" label:"set st" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Stage st = Stage.getInstance();
            /* block end [id:"offsetSprite_block_17"] */
            /* block comment [block:"offsetSprite_block_18" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"offsetSprite_block_18" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"offsetSprite_block_6" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"offsetSprite_block_6" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (st != /* param start [id:"offsetSprite_value_24" type:"value"] */null/* param end [id:"offsetSprite_value_24"] */) return st.getScaleFactor();
            /* block end [id:"offsetSprite_block_18"] */
            /* block end [id:"offsetSprite_block_6"] */
        }
        /* block end [id:"offsetSprite_block_5"] */ catch (Exception ignored) {}
        /* block comment [block:"offsetSprite_block_19" text:"Returns a value or ends the current method."] */
        /* block start [id:"offsetSprite_block_19" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return /* param start [id:"offsetSprite_value_25" type:"number"] */1.0/* param end [id:"offsetSprite_value_25"] */;
        /* block end [id:"offsetSprite_block_19"] */
    }
    /* slot end [id:"offsetSprite_members"] */
}