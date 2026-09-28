package generated;

import com.jscratch.Costume;
import com.jscratch.Stage;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
* Dungeon wall visual: the floor's 8x8 stone texture tiled across the
* rect (pre-rendered once, blitted per frame), so walls read as masonry
* instead of flat grey boxes. Center-positioned (cx, cy, w, h); the
* matching BCollider is top-left based.
* Minimap dot hidden: walls render on the minimap via MinimapWalls.
*
* <p>Call graph: built per rect by {@code FloorManager.buildWalls} (old
* walls hidden + unregistered); {@code draw} is custom (face blit +
* borders) using Stage size/scale + localScale. Collider twin lives in
* {@code Scroller2d} colliders.
*/
public class WallSprite extends BSprite {
    private static final int TEXEL = 3; // stone texture scale (8px -> 24px cells)
    private static final Color EDGE_COLOR = new Color(40, 38, 46);
    private static final Color TOP_COLOR = new Color(140, 136, 150);
    private final int w, h;
    private BufferedImage face;

    public WallSprite(int cx, int cy, int w, int h, String texPath) {
        /* block comment [block:"WallSprite_block_13" text:"Runs the super action. Values in this block are editable inputs."] */
        /* block start [id:"WallSprite_block_13" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
        super(/* param start [id:"WallSprite_value_53" type:"text"] */"Wall"/* param end [id:"WallSprite_value_53"] */);
        /* block end [id:"WallSprite_block_13"] */
        /* block comment [block:"WallSprite_block_14" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"WallSprite_block_14" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(new Costume(/* param start [id:"WallSprite_value_54" type:"text"] */"blank"/* param end [id:"WallSprite_value_54"] */, /* param start [id:"WallSprite_value_55" type:"text"] */"jscratch_ws/assets/error.png"/* param end [id:"WallSprite_value_55"] */, /* param start [id:"WallSprite_value_56" type:"number"] */0/* param end [id:"WallSprite_value_56"] */, /* param start [id:"WallSprite_value_57" type:"number"] */0/* param end [id:"WallSprite_value_57"] */));
        /* block end [id:"WallSprite_block_14"] */
        /* block comment [block:"WallSprite_block_15" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"WallSprite_block_15" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"WallSprite_block_1" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"WallSprite_block_1" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (minimapSprite != /* param start [id:"WallSprite_value_58" type:"value"] */null/* param end [id:"WallSprite_value_58"] */) minimapSprite.hide();
        /* block end [id:"WallSprite_block_15"] */
        /* block end [id:"WallSprite_block_1"] */
        /* block comment [block:"WallSprite_block_16" text:"Runs the max action. Values in this block are editable inputs."] */
        /* block start [id:"WallSprite_block_16" category:"My Blocks" label:"max" shape:"command" docs:"Runs the max action. Values in this block are editable inputs."] */
        this.w = Math.max(/* param start [id:"WallSprite_value_59" type:"number"] */8/* param end [id:"WallSprite_value_59"] */, w);
        /* block end [id:"WallSprite_block_16"] */
        /* block comment [block:"WallSprite_block_17" text:"Runs the max action. Values in this block are editable inputs."] */
        /* block start [id:"WallSprite_block_17" category:"My Blocks" label:"max" shape:"command" docs:"Runs the max action. Values in this block are editable inputs."] */
        this.h = Math.max(/* param start [id:"WallSprite_value_60" type:"number"] */8/* param end [id:"WallSprite_value_60"] */, h);
        /* block end [id:"WallSprite_block_17"] */
        /* block comment [block:"WallSprite_block_18" text:"Calls the renderFace method using its declared arguments."] */
        /* block start [id:"WallSprite_block_18" category:"My Blocks" label:"call render face" shape:"command" docs:"Calls the renderFace method using its declared arguments."] */
        renderFace(texPath);
        /* block end [id:"WallSprite_block_18"] */
        /* block comment [block:"WallSprite_block_19" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"WallSprite_block_19" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        xx = cx;
        /* block end [id:"WallSprite_block_19"] */
        /* block comment [block:"WallSprite_block_20" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"WallSprite_block_20" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        yy = cy;
        /* block end [id:"WallSprite_block_20"] */
        /* block comment [block:"WallSprite_block_21" text:"Calls the update method using its declared arguments."] */
        /* block start [id:"WallSprite_block_21" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
        update();
        /* block end [id:"WallSprite_block_21"] */
    }

    /** Back-compat: plain grey-stone walls. */
    public WallSprite(int cx, int cy, int w, int h) {
        /* block comment [block:"WallSprite_block_22" text:"Runs the this action. Values in this block are editable inputs."] */
        /* block start [id:"WallSprite_block_22" category:"My Blocks" label:"this" shape:"command" docs:"Runs the this action. Values in this block are editable inputs."] */
        this(cx, cy, w, h, /* param start [id:"WallSprite_value_61" type:"text"] */"jscratch_ws/assets/map tile grey stone.png"/* param end [id:"WallSprite_value_61"] */);
        /* block end [id:"WallSprite_block_22"] */
    }

    private void renderFace(String texPath) {
        /* block comment [block:"WallSprite_block_23" text:"Runs the buffered image action. Values in this block are editable inputs."] */
        /* block start [id:"WallSprite_block_23" category:"My Blocks" label:"buffered image" shape:"command" docs:"Runs the buffered image action. Values in this block are editable inputs."] */
        face = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        /* block end [id:"WallSprite_block_23"] */
        /* block comment [block:"WallSprite_block_24" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"WallSprite_block_24" category:"Variables" label:"set tex" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        BufferedImage tex = /* param start [id:"WallSprite_value_62" type:"value"] */null/* param end [id:"WallSprite_value_62"] */;
        /* block end [id:"WallSprite_block_24"] */
        /* block comment [block:"WallSprite_block_2" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"WallSprite_block_2" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try {
            /* block comment [block:"WallSprite_block_25" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"WallSprite_block_25" category:"Variables" label:"set c" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Costume c = ResourcePool.getCostume(/* param start [id:"WallSprite_value_63" type:"text"] */"walltex_"/* param end [id:"WallSprite_value_63"] */ + texPath, texPath, /* param start [id:"WallSprite_value_64" type:"number"] */4/* param end [id:"WallSprite_value_64"] */, /* param start [id:"WallSprite_value_65" type:"number"] */4/* param end [id:"WallSprite_value_65"] */);
            /* block end [id:"WallSprite_block_25"] */
            /* block comment [block:"WallSprite_block_26" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"WallSprite_block_26" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"WallSprite_block_3" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"WallSprite_block_3" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (c != /* param start [id:"WallSprite_value_66" type:"value"] */null/* param end [id:"WallSprite_value_66"] */) tex = c.getImage();
            /* block end [id:"WallSprite_block_26"] */
            /* block end [id:"WallSprite_block_3"] */
        }
        /* block end [id:"WallSprite_block_2"] */ catch (Exception e) {
            /* block comment [block:"WallSprite_block_27" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"WallSprite_block_27" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            tex = /* param start [id:"WallSprite_value_67" type:"value"] */null/* param end [id:"WallSprite_value_67"] */;
            /* block end [id:"WallSprite_block_27"] */
        }
        /* block comment [block:"WallSprite_block_28" text:"Runs the create graphics action. Values in this block are editable inputs."] */
        /* block start [id:"WallSprite_block_28" category:"My Blocks" label:"create graphics" shape:"command" docs:"Runs the create graphics action. Values in this block are editable inputs."] */
        java.awt.Graphics2D g = face.createGraphics();
        /* block end [id:"WallSprite_block_28"] */
        /* block comment [block:"WallSprite_block_4" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"WallSprite_block_4" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try {
            /* block comment [block:"WallSprite_block_5" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"WallSprite_block_5" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (tex != /* param start [id:"WallSprite_value_68" type:"value"] */null/* param end [id:"WallSprite_value_68"] */) {
                /* block comment [block:"WallSprite_block_29" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"WallSprite_block_29" category:"Variables" label:"set tw" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int tw = tex.getWidth() * TEXEL;
                /* block end [id:"WallSprite_block_29"] */
                /* block comment [block:"WallSprite_block_30" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"WallSprite_block_30" category:"Variables" label:"set th" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int th = tex.getHeight() * TEXEL;
                /* block end [id:"WallSprite_block_30"] */
                /* block comment [block:"WallSprite_block_31" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"WallSprite_block_31" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"WallSprite_block_6" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"WallSprite_block_6" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (tw < /* param start [id:"WallSprite_value_69" type:"number"] */1/* param end [id:"WallSprite_value_69"] */) tw = /* param start [id:"WallSprite_value_70" type:"number"] */1/* param end [id:"WallSprite_value_70"] */;
                /* block end [id:"WallSprite_block_31"] */
                /* block end [id:"WallSprite_block_6"] */
                /* block comment [block:"WallSprite_block_32" text:"Runs the if action. Values in this block are editable inputs."] */
                /* block start [id:"WallSprite_block_32" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                /* block comment [block:"WallSprite_block_7" text:"Checks a Boolean condition and runs the body when it is true."] */
                /* block start [id:"WallSprite_block_7" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                if (th < /* param start [id:"WallSprite_value_71" type:"number"] */1/* param end [id:"WallSprite_value_71"] */) th = /* param start [id:"WallSprite_value_72" type:"number"] */1/* param end [id:"WallSprite_value_72"] */;
                /* block end [id:"WallSprite_block_32"] */
                /* block end [id:"WallSprite_block_7"] */
                /* block comment [block:"WallSprite_block_8" text:"Repeats the nested operations for each loop turn."] */
                /* block start [id:"WallSprite_block_8" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
                for (int y = /* param start [id:"WallSprite_value_73" type:"number"] */0/* param end [id:"WallSprite_value_73"] */; y < h; y += th) {
                    /* block comment [block:"WallSprite_block_9" text:"Repeats the nested operations for each loop turn."] */
                    /* block start [id:"WallSprite_block_9" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
                    for (int x = /* param start [id:"WallSprite_value_74" type:"number"] */0/* param end [id:"WallSprite_value_74"] */; x < w; x += tw) {
                        /* block comment [block:"WallSprite_block_33" text:"Calls the drawImage method using its declared arguments."] */
                        /* block start [id:"WallSprite_block_33" category:"My Blocks" label:"call draw image" shape:"command" docs:"Calls the drawImage method using its declared arguments."] */
                        g.drawImage(tex, x, y, tw, th, /* param start [id:"WallSprite_value_75" type:"value"] */null/* param end [id:"WallSprite_value_75"] */);
                        /* block end [id:"WallSprite_block_33"] */
                    }
                    /* block end [id:"WallSprite_block_9"] */
                }
                /* block end [id:"WallSprite_block_8"] */
            } else {
                /* block comment [block:"WallSprite_block_34" text:"Runs the set color action. Values in this block are editable inputs."] */
                /* block start [id:"WallSprite_block_34" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
                g.setColor(new Color(/* param start [id:"WallSprite_value_76" type:"number"] */74/* param end [id:"WallSprite_value_76"] */, /* param start [id:"WallSprite_value_77" type:"number"] */70/* param end [id:"WallSprite_value_77"] */, /* param start [id:"WallSprite_value_78" type:"number"] */82/* param end [id:"WallSprite_value_78"] */));
                /* block end [id:"WallSprite_block_34"] */
                /* block comment [block:"WallSprite_block_35" text:"Calls the fillRect method using its declared arguments."] */
                /* block start [id:"WallSprite_block_35" category:"My Blocks" label:"call fill rect" shape:"command" docs:"Calls the fillRect method using its declared arguments."] */
                g.fillRect(/* param start [id:"WallSprite_value_79" type:"number"] */0/* param end [id:"WallSprite_value_79"] */, /* param start [id:"WallSprite_value_80" type:"number"] */0/* param end [id:"WallSprite_value_80"] */, w, h);
                /* block end [id:"WallSprite_block_35"] */
            }
            /* block end [id:"WallSprite_block_5"] */
            // Darken the base so stacked walls read with depth
            /* block comment [block:"WallSprite_block_36" text:"Runs the set color action. Values in this block are editable inputs."] */
            /* block start [id:"WallSprite_block_36" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
            g.setColor(new Color(/* param start [id:"WallSprite_value_81" type:"number"] */0/* param end [id:"WallSprite_value_81"] */, /* param start [id:"WallSprite_value_82" type:"number"] */0/* param end [id:"WallSprite_value_82"] */, /* param start [id:"WallSprite_value_83" type:"number"] */0/* param end [id:"WallSprite_value_83"] */, /* param start [id:"WallSprite_value_84" type:"number"] */60/* param end [id:"WallSprite_value_84"] */));
            /* block end [id:"WallSprite_block_36"] */
            /* block comment [block:"WallSprite_block_37" text:"Calls the fillRect method using its declared arguments."] */
            /* block start [id:"WallSprite_block_37" category:"My Blocks" label:"call fill rect" shape:"command" docs:"Calls the fillRect method using its declared arguments."] */
            g.fillRect(/* param start [id:"WallSprite_value_85" type:"number"] */0/* param end [id:"WallSprite_value_85"] */, h - /* param start [id:"WallSprite_value_86" type:"number"] */6/* param end [id:"WallSprite_value_86"] */, w, /* param start [id:"WallSprite_value_87" type:"number"] */6/* param end [id:"WallSprite_value_87"] */);
            /* block end [id:"WallSprite_block_37"] */
        }
        /* block end [id:"WallSprite_block_4"] */ finally {
            /* block comment [block:"WallSprite_block_38" text:"Calls the dispose method using its declared arguments."] */
            /* block start [id:"WallSprite_block_38" category:"My Blocks" label:"call dispose" shape:"command" docs:"Calls the dispose method using its declared arguments."] */
            g.dispose();
            /* block end [id:"WallSprite_block_38"] */
        }
    }

    @Override
    public void draw(Graphics2D g) {
        /* block comment [block:"WallSprite_block_39" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"WallSprite_block_39" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"WallSprite_block_10" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"WallSprite_block_10" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!isVisible()) return;
        /* block end [id:"WallSprite_block_39"] */
        /* block end [id:"WallSprite_block_10"] */
        /* block comment [block:"WallSprite_block_40" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"WallSprite_block_40" category:"Variables" label:"set stage" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage stage = Stage.getInstance();
        /* block end [id:"WallSprite_block_40"] */
        /* block comment [block:"WallSprite_block_41" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"WallSprite_block_41" category:"Variables" label:"set sx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int sx = (int) (getX() + stage.getWidth() / /* param start [id:"WallSprite_value_88" type:"number"] */2.0/* param end [id:"WallSprite_value_88"] */);
        /* block end [id:"WallSprite_block_41"] */
        /* block comment [block:"WallSprite_block_42" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"WallSprite_block_42" category:"Variables" label:"set sy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int sy = (int) (stage.getHeight() / /* param start [id:"WallSprite_value_89" type:"number"] */2.0/* param end [id:"WallSprite_value_89"] */ - getY());
        /* block end [id:"WallSprite_block_42"] */

        double s = stage.getScaleFactor() * localScale;
        int dw = Math.max(2, (int) (w * s));
        int dh = Math.max(2, (int) (h * s));
        if (sx - dw / 2 > stage.getWidth() || sy - dh / 2 > stage.getHeight()
                || sx + dw / 2 < 0 || sy + dh / 2 < 0) return;

        /* block comment [block:"WallSprite_block_43" text:"Runs the get transform action. Values in this block are editable inputs."] */
        /* block start [id:"WallSprite_block_43" category:"My Blocks" label:"get transform" shape:"command" docs:"Runs the get transform action. Values in this block are editable inputs."] */
        java.awt.geom.AffineTransform old = g.getTransform();
        /* block end [id:"WallSprite_block_43"] */
        /* block comment [block:"WallSprite_block_11" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"WallSprite_block_11" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try {
            /* block comment [block:"WallSprite_block_12" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"WallSprite_block_12" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (face != /* param start [id:"WallSprite_value_92" type:"value"] */null/* param end [id:"WallSprite_value_92"] */) {
                /* block comment [block:"WallSprite_block_47" text:"Calls the drawImage method using its declared arguments."] */
                /* block start [id:"WallSprite_block_47" category:"My Blocks" label:"call draw image" shape:"command" docs:"Calls the drawImage method using its declared arguments."] */
                g.drawImage(face, sx - dw / /* param start [id:"WallSprite_value_93" type:"number"] */2/* param end [id:"WallSprite_value_93"] */, sy - dh / /* param start [id:"WallSprite_value_94" type:"number"] */2/* param end [id:"WallSprite_value_94"] */, dw, dh, /* param start [id:"WallSprite_value_95" type:"value"] */null/* param end [id:"WallSprite_value_95"] */);
                /* block end [id:"WallSprite_block_47"] */
            }
            /* block end [id:"WallSprite_block_12"] */
            /* block comment [block:"WallSprite_block_48" text:"Runs the set color action. Values in this block are editable inputs."] */
            /* block start [id:"WallSprite_block_48" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
            g.setColor(EDGE_COLOR);
            /* block end [id:"WallSprite_block_48"] */
            /* block comment [block:"WallSprite_block_49" text:"Calls the drawRect method using its declared arguments."] */
            /* block start [id:"WallSprite_block_49" category:"My Blocks" label:"call draw rect" shape:"command" docs:"Calls the drawRect method using its declared arguments."] */
            g.drawRect(sx - dw / /* param start [id:"WallSprite_value_99" type:"number"] */2/* param end [id:"WallSprite_value_99"] */, sy - dh / /* param start [id:"WallSprite_value_100" type:"number"] */2/* param end [id:"WallSprite_value_100"] */, dw, dh);
            /* block end [id:"WallSprite_block_49"] */
            /* block comment [block:"WallSprite_block_50" text:"Runs the set color action. Values in this block are editable inputs."] */
            /* block start [id:"WallSprite_block_50" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
            g.setColor(TOP_COLOR);
            /* block end [id:"WallSprite_block_50"] */
            /* block comment [block:"WallSprite_block_51" text:"Calls the drawLine method using its declared arguments."] */
            /* block start [id:"WallSprite_block_51" category:"My Blocks" label:"call draw line" shape:"command" docs:"Calls the drawLine method using its declared arguments."] */
            g.drawLine(sx - dw / /* param start [id:"WallSprite_value_104" type:"number"] */2/* param end [id:"WallSprite_value_104"] */, sy - dh / /* param start [id:"WallSprite_value_105" type:"number"] */2/* param end [id:"WallSprite_value_105"] */, sx + dw / /* param start [id:"WallSprite_value_106" type:"number"] */2/* param end [id:"WallSprite_value_106"] */, sy - dh / /* param start [id:"WallSprite_value_107" type:"number"] */2/* param end [id:"WallSprite_value_107"] */);
            /* block end [id:"WallSprite_block_51"] */
        }
        /* block end [id:"WallSprite_block_11"] */ finally {
            /* block comment [block:"WallSprite_block_52" text:"Runs the set transform action. Values in this block are editable inputs."] */
            /* block start [id:"WallSprite_block_52" category:"Variables" label:"set transform" shape:"command" docs:"Runs the set transform action. Values in this block are editable inputs."] */
            g.setTransform(old);
            /* block end [id:"WallSprite_block_52"] */
        }
    }
}
