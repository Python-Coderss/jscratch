package generated;

import com.jscratch.Costume;
import com.jscratch.Sprite;
import com.jscratch.Stage;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
* Toggleable inventory overlay (I to open/close, Up/Down to select, Enter to equip).
* All labels use the BitmapFont glyph images from jscratch_ws/assets.
* Only com.jscratch.* + java.* imports.
*/
public class InventoryPanel extends Sprite {
    private boolean open = false;
    private int selected = 0;
    private BufferedImage panelCache;
    private long panelCacheKey;
    private boolean panelCacheValid;

    private long panelKey(int w, int h) {
        long k = 0xcbf29ce484222325L;
        k = k * 0x100000001b3L + w;
        k = k * 0x100000001b3L + h;
        k = k * 0x100000001b3L + selected;
        k = k * 0x100000001b3L + Inventory.gold;
        k = k * 0x100000001b3L + Inventory.bag.size();
        for (int i = 0; i < Inventory.bag.size(); i++) {
            Item it = Inventory.bag.get(i);
            k = k * 0x100000001b3L + (it.name == null ? 0 : it.name.hashCode());
            k = k * 0x100000001b3L + it.statLine().hashCode();
        }
        for (int i = 0; i < SLOTS.length; i++) {
            Item it = Inventory.equipped.get(SLOTS[i]);
            k = k * 0x100000001b3L + (it == null || it.name == null ? 0 : it.name.hashCode());
            k = k * 0x100000001b3L + (it == null ? 0 : it.statLine().hashCode());
        }
        return k;
    }

    private static final Color GOLD = new Color(226, 200, 120);
    private static final Color INK = Color.WHITE;
    private static final Color SEL = Color.YELLOW;
    private static final Color SEL_FILL = new Color(90, 80, 40);
    private static final Color PANEL_FILL = new Color(12, 12, 20, 235);
    private static final Color PANEL_EDGE = new Color(150, 140, 120);
    private static final String[] SLOTS = {"WEAPON", "ARMOR", "HELM", "GLOVES", "BOOTS", "CLOAK", "SHIELD", "CHARM"};

    public InventoryPanel() {
        /* block comment [block:"InventoryPanel_block_15" text:"Runs the super action. Values in this block are editable inputs."] */
        /* block start [id:"InventoryPanel_block_15" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
        super(/* param start [id:"InventoryPanel_value_78" type:"text"] */"InventoryPanel"/* param end [id:"InventoryPanel_value_78"] */);
        /* block end [id:"InventoryPanel_block_15"] */
        /* block comment [block:"InventoryPanel_block_16" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"InventoryPanel_block_16" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        addCostume(new Costume(/* param start [id:"InventoryPanel_value_79" type:"text"] */"blank"/* param end [id:"InventoryPanel_value_79"] */, /* param start [id:"InventoryPanel_value_80" type:"text"] */"jscratch_ws/assets/error.png"/* param end [id:"InventoryPanel_value_80"] */, /* param start [id:"InventoryPanel_value_81" type:"number"] */0/* param end [id:"InventoryPanel_value_81"] */, /* param start [id:"InventoryPanel_value_82" type:"number"] */0/* param end [id:"InventoryPanel_value_82"] */));
        /* block end [id:"InventoryPanel_block_16"] */
        /* block comment [block:"InventoryPanel_block_17" text:"Runs the hide action. Values in this block are editable inputs."] */
        /* block start [id:"InventoryPanel_block_17" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
        hide();
        /* block end [id:"InventoryPanel_block_17"] */
        /* block comment [block:"InventoryPanel_block_1" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"InventoryPanel_block_1" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try {
            Stage.getInstance().whenIReceive(/* param start [id:"InventoryPanel_value_83" type:"text"] */"enterCharacterCreate"/* param end [id:"InventoryPanel_value_83"] */, () -> { open = /* param start [id:"InventoryPanel_value_84" type:"boolean"] */false/* param end [id:"InventoryPanel_value_84"] */; hide(); }/* block comment [block:"InventoryPanel_block_18" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"InventoryPanel_block_18" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            );
            /* block end [id:"InventoryPanel_block_18"] */
        }
        /* block end [id:"InventoryPanel_block_1"] */ catch (Exception ignored) {}
    }

    public boolean isOpen() { /* block comment [block:"InventoryPanel_block_19" text:"Returns a value or ends the current method."] */
        /* block start [id:"InventoryPanel_block_19" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return open && GameState.isGameplay();
        /* block end [id:"InventoryPanel_block_19"] */ }

    public void toggle() {
        /* block comment [block:"InventoryPanel_block_2" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"InventoryPanel_block_2" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!GameState.isGameplay()) { /* block comment [block:"InventoryPanel_block_20" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"InventoryPanel_block_20" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            open = /* param start [id:"InventoryPanel_value_85" type:"boolean"] */false/* param end [id:"InventoryPanel_value_85"] */;
            /* block end [id:"InventoryPanel_block_20"] */ /* block comment [block:"InventoryPanel_block_21" text:"Runs the hide action. Values in this block are editable inputs."] */
            /* block start [id:"InventoryPanel_block_21" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
            hide();
            /* block end [id:"InventoryPanel_block_21"] */ /* block comment [block:"InventoryPanel_block_22" text:"Returns a value or ends the current method."] */
            /* block start [id:"InventoryPanel_block_22" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"InventoryPanel_block_22"] */ }
        /* block end [id:"InventoryPanel_block_2"] */
        /* block comment [block:"InventoryPanel_block_23" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"InventoryPanel_block_23" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        open = !open;
        /* block end [id:"InventoryPanel_block_23"] */
        /* block comment [block:"InventoryPanel_block_3" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"InventoryPanel_block_3" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (open) { /* block comment [block:"InventoryPanel_block_24" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"InventoryPanel_block_24" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            selected = /* param start [id:"InventoryPanel_value_86" type:"number"] */0/* param end [id:"InventoryPanel_value_86"] */;
            /* block end [id:"InventoryPanel_block_24"] */ /* block comment [block:"InventoryPanel_block_25" text:"Runs the show action. Values in this block are editable inputs."] */
            /* block start [id:"InventoryPanel_block_25" category:"Looks" label:"show" shape:"command" docs:"Runs the show action. Values in this block are editable inputs."] */
            show();
            /* block end [id:"InventoryPanel_block_25"] */ } /* block comment [block:"InventoryPanel_block_26" text:"Runs the hide action. Values in this block are editable inputs."] */
        /* block start [id:"InventoryPanel_block_26" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
        else hide();
        /* block end [id:"InventoryPanel_block_26"] */
        /* block end [id:"InventoryPanel_block_3"] */
        /* block comment [block:"InventoryPanel_block_4" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"InventoryPanel_block_4" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try { /* block comment [block:"InventoryPanel_block_27" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"InventoryPanel_block_27" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Stage.getInstance().goToFront(this);
            /* block end [id:"InventoryPanel_block_27"] */ }
        /* block end [id:"InventoryPanel_block_4"] */ catch (Exception ignored) {}
    }

    public void moveSel(int d) {
        /* block comment [block:"InventoryPanel_block_28" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"InventoryPanel_block_28" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"InventoryPanel_block_5" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"InventoryPanel_block_5" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (Inventory.bag.isEmpty()) return;
        /* block end [id:"InventoryPanel_block_28"] */
        /* block end [id:"InventoryPanel_block_5"] */
        /* block comment [block:"InventoryPanel_block_29" text:"Runs the size action. Values in this block are editable inputs."] */
        /* block start [id:"InventoryPanel_block_29" category:"Looks" label:"size" shape:"command" docs:"Runs the size action. Values in this block are editable inputs."] */
        selected = (selected + d + Inventory.bag.size()) % Inventory.bag.size();
        /* block end [id:"InventoryPanel_block_29"] */
    }

    public void equipSel() {
        /* block comment [block:"InventoryPanel_block_30" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"InventoryPanel_block_30" category:"Variables" label:"set equip" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Inventory.equip(selected);
        /* block end [id:"InventoryPanel_block_30"] */
        /* block comment [block:"InventoryPanel_block_31" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"InventoryPanel_block_31" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"InventoryPanel_block_6" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"InventoryPanel_block_6" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (selected >= Inventory.bag.size()) selected = Math.max(/* param start [id:"InventoryPanel_value_87" type:"number"] */0/* param end [id:"InventoryPanel_value_87"] */, Inventory.bag.size() - /* param start [id:"InventoryPanel_value_88" type:"number"] */1/* param end [id:"InventoryPanel_value_88"] */);
        /* block end [id:"InventoryPanel_block_31"] */
        /* block end [id:"InventoryPanel_block_6"] */
    }

    private static BitmapFont.Style font(int size, boolean bold) { /* block comment [block:"InventoryPanel_block_32" text:"Returns a value or ends the current method."] */
        /* block start [id:"InventoryPanel_block_32" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return BitmapFont.style(Math.max(/* param start [id:"InventoryPanel_value_90" type:"number"] */7/* param end [id:"InventoryPanel_value_90"] */, size), bold);
        /* block end [id:"InventoryPanel_block_32"] */ }

    @Override
    public void draw(Graphics2D g) {
        /* block comment [block:"InventoryPanel_block_33" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"InventoryPanel_block_33" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"InventoryPanel_block_7" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"InventoryPanel_block_7" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!open || !isVisible() || !GameState.isGameplay()) return;
        /* block end [id:"InventoryPanel_block_33"] */
        /* block end [id:"InventoryPanel_block_7"] */
        /* block comment [block:"InventoryPanel_block_34" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"InventoryPanel_block_34" category:"Variables" label:"set stage" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage stage = Stage.getInstance();
        /* block end [id:"InventoryPanel_block_34"] */
        /* block comment [block:"InventoryPanel_block_35" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"InventoryPanel_block_35" category:"Variables" label:"set w" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int w = stage.getWidth(), h = stage.getHeight();
        /* block end [id:"InventoryPanel_block_35"] */
        /* block comment [block:"InventoryPanel_block_36" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"InventoryPanel_block_36" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"InventoryPanel_block_8" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"InventoryPanel_block_8" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (w <= /* param start [id:"InventoryPanel_value_91" type:"number"] */0/* param end [id:"InventoryPanel_value_91"] */ || h <= /* param start [id:"InventoryPanel_value_92" type:"number"] */0/* param end [id:"InventoryPanel_value_92"] */) return;
        /* block end [id:"InventoryPanel_block_36"] */
        /* block end [id:"InventoryPanel_block_8"] */
        /* block comment [block:"InventoryPanel_block_37" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"InventoryPanel_block_37" category:"Variables" label:"set ds" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double ds = w / /* param start [id:"InventoryPanel_value_93" type:"number"] */480.0/* param end [id:"InventoryPanel_value_93"] */;
        /* block end [id:"InventoryPanel_block_37"] */
        /* block comment [block:"InventoryPanel_block_38" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"InventoryPanel_block_38" category:"Variables" label:"set pw" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int pw = /* param start [id:"InventoryPanel_value_94" type:"number"] */300/* param end [id:"InventoryPanel_value_94"] */, px = w - pw - /* param start [id:"InventoryPanel_value_95" type:"number"] */10/* param end [id:"InventoryPanel_value_95"] */, py = /* param start [id:"InventoryPanel_value_96" type:"number"] */10/* param end [id:"InventoryPanel_value_96"] */, ph = h - /* param start [id:"InventoryPanel_value_97" type:"number"] */20/* param end [id:"InventoryPanel_value_97"] */;
        /* block end [id:"InventoryPanel_block_38"] */
        /* block comment [block:"InventoryPanel_block_39" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"InventoryPanel_block_39" category:"Variables" label:"set tx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int tx = px + /* param start [id:"InventoryPanel_value_98" type:"number"] */12/* param end [id:"InventoryPanel_value_98"] */;
        /* block end [id:"InventoryPanel_block_39"] */
        long pkey = panelKey(w, h);
        if (panelCacheValid && pkey == panelCacheKey && panelCache != null) {
            g.drawImage(panelCache, px, py, null);
            return;
        }
        if (panelCache == null || panelCache.getWidth() != pw || panelCache.getHeight() != ph) {
            panelCache = new BufferedImage(pw, ph, BufferedImage.TYPE_INT_ARGB);
        }
        Graphics2D live = g;
        Graphics2D cg = panelCache.createGraphics();
        cg.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
        cg.setComposite(java.awt.AlphaComposite.Clear);
        cg.fillRect(0, 0, pw, ph);
        cg.setComposite(java.awt.AlphaComposite.SrcOver);
        cg.translate(-px, -py);
        g = cg;
        /* block comment [block:"InventoryPanel_block_40" text:"Runs the get transform action. Values in this block are editable inputs."] */
        /* block start [id:"InventoryPanel_block_40" category:"My Blocks" label:"get transform" shape:"command" docs:"Runs the get transform action. Values in this block are editable inputs."] */
        java.awt.geom.AffineTransform old = g.getTransform();
        /* block end [id:"InventoryPanel_block_40"] */
        /* block comment [block:"InventoryPanel_block_9" text:"Runs work safely and handles matching errors in a catch branch."] */
        /* block start [id:"InventoryPanel_block_9" category:"Control" label:"try / catch" shape:"control" docs:"Runs work safely and handles matching errors in a catch branch."] */
        try {
            /* block comment [block:"InventoryPanel_block_41" text:"Runs the set color action. Values in this block are editable inputs."] */
            /* block start [id:"InventoryPanel_block_41" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
            g.setColor(PANEL_FILL);
            /* block end [id:"InventoryPanel_block_41"] */ /* block comment [block:"InventoryPanel_block_42" text:"Calls the fillRoundRect method using its declared arguments."] */
            /* block start [id:"InventoryPanel_block_42" category:"My Blocks" label:"call fill round rect" shape:"command" docs:"Calls the fillRoundRect method using its declared arguments."] */
            g.fillRoundRect(px, py, pw, ph, /* param start [id:"InventoryPanel_value_99" type:"number"] */12/* param end [id:"InventoryPanel_value_99"] */, /* param start [id:"InventoryPanel_value_100" type:"number"] */12/* param end [id:"InventoryPanel_value_100"] */);
            /* block end [id:"InventoryPanel_block_42"] */
            /* block comment [block:"InventoryPanel_block_43" text:"Runs the set color action. Values in this block are editable inputs."] */
            /* block start [id:"InventoryPanel_block_43" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
            g.setColor(PANEL_EDGE);
            /* block end [id:"InventoryPanel_block_43"] */ /* block comment [block:"InventoryPanel_block_44" text:"Calls the drawRoundRect method using its declared arguments."] */
            /* block start [id:"InventoryPanel_block_44" category:"My Blocks" label:"call draw round rect" shape:"command" docs:"Calls the drawRoundRect method using its declared arguments."] */
            g.drawRoundRect(px, py, pw, ph, /* param start [id:"InventoryPanel_value_101" type:"number"] */12/* param end [id:"InventoryPanel_value_101"] */, /* param start [id:"InventoryPanel_value_102" type:"number"] */12/* param end [id:"InventoryPanel_value_102"] */);
            /* block end [id:"InventoryPanel_block_44"] */
            /* block comment [block:"InventoryPanel_block_45" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"InventoryPanel_block_45" category:"Variables" label:"set y" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            int y = py + /* param start [id:"InventoryPanel_value_103" type:"number"] */18/* param end [id:"InventoryPanel_value_103"] */;
            /* block end [id:"InventoryPanel_block_45"] */
            /* block comment [block:"InventoryPanel_block_46" text:"Runs the set font action. Values in this block are editable inputs."] */
            /* block start [id:"InventoryPanel_block_46" category:"Variables" label:"set font" shape:"command" docs:"Runs the set font action. Values in this block are editable inputs."] */
            BitmapFont.Style fs = font((int)(/* param start [id:"InventoryPanel_value_104" type:"number"] */12/* param end [id:"InventoryPanel_value_104"] */*ds), /* param start [id:"InventoryPanel_value_105" type:"boolean"] */true/* param end [id:"InventoryPanel_value_105"] */);
            /* block end [id:"InventoryPanel_block_46"] */ /* block comment [block:"InventoryPanel_block_47" text:"Runs the set color action. Values in this block are editable inputs."] */
            /* block start [id:"InventoryPanel_block_47" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
            g.setColor(GOLD);
            /* block end [id:"InventoryPanel_block_47"] */ /* block comment [block:"InventoryPanel_block_48" text:"Calls the draw string method using its declared arguments."] */
            /* block start [id:"InventoryPanel_block_48" category:"My Blocks" label:"call draw string" shape:"command" docs:"Calls the draw string method using its declared arguments."] */
            BitmapFont.draw(g, /* param start [id:"InventoryPanel_value_106" type:"text"] */"INVENTORY (I)"/* param end [id:"InventoryPanel_value_106"] */, tx, y, fs);
            /* block end [id:"InventoryPanel_block_48"] */ /* block comment [block:"InventoryPanel_block_49" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"InventoryPanel_block_49" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            y += /* param start [id:"InventoryPanel_value_107" type:"number"] */18/* param end [id:"InventoryPanel_value_107"] */;
            /* block end [id:"InventoryPanel_block_49"] */
            /* block comment [block:"InventoryPanel_block_50" text:"Runs the set font action. Values in this block are editable inputs."] */
            /* block start [id:"InventoryPanel_block_50" category:"Variables" label:"set font" shape:"command" docs:"Runs the set font action. Values in this block are editable inputs."] */
            fs = font((int)(/* param start [id:"InventoryPanel_value_108" type:"number"] */10/* param end [id:"InventoryPanel_value_108"] */*ds), /* param start [id:"InventoryPanel_value_109" type:"boolean"] */false/* param end [id:"InventoryPanel_value_109"] */);
            /* block end [id:"InventoryPanel_block_50"] */ /* block comment [block:"InventoryPanel_block_51" text:"Runs the set color action. Values in this block are editable inputs."] */
            /* block start [id:"InventoryPanel_block_51" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
            g.setColor(INK);
            /* block end [id:"InventoryPanel_block_51"] */ /* block comment [block:"InventoryPanel_block_52" text:"Calls the drawString method using its declared arguments."] */
            /* block start [id:"InventoryPanel_block_52" category:"My Blocks" label:"call draw string" shape:"command" docs:"Calls the drawString method using its declared arguments."] */
            BitmapFont.draw(g, /* param start [id:"InventoryPanel_value_110" type:"text"] */"Gold: "/* param end [id:"InventoryPanel_value_110"] */ + Inventory.gold, tx, y, fs);
            /* block end [id:"InventoryPanel_block_52"] */ /* block comment [block:"InventoryPanel_block_53" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"InventoryPanel_block_53" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            y += /* param start [id:"InventoryPanel_value_111" type:"number"] */18/* param end [id:"InventoryPanel_value_111"] */;
            /* block end [id:"InventoryPanel_block_53"] */
            /* block comment [block:"InventoryPanel_block_54" text:"Runs the set font action. Values in this block are editable inputs."] */
            /* block start [id:"InventoryPanel_block_54" category:"Variables" label:"set font" shape:"command" docs:"Runs the set font action. Values in this block are editable inputs."] */
            fs = font((int)(/* param start [id:"InventoryPanel_value_112" type:"number"] */11/* param end [id:"InventoryPanel_value_112"] */*ds), /* param start [id:"InventoryPanel_value_113" type:"boolean"] */true/* param end [id:"InventoryPanel_value_113"] */);
            /* block end [id:"InventoryPanel_block_54"] */ /* block comment [block:"InventoryPanel_block_55" text:"Calls the drawString method using its declared arguments."] */
            /* block start [id:"InventoryPanel_block_55" category:"My Blocks" label:"call draw string" shape:"command" docs:"Calls the drawString method using its declared arguments."] */
            BitmapFont.draw(g, /* param start [id:"InventoryPanel_value_114" type:"text"] */"-- Worn --"/* param end [id:"InventoryPanel_value_114"] */, tx, y, fs);
            /* block end [id:"InventoryPanel_block_55"] */ /* block comment [block:"InventoryPanel_block_56" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"InventoryPanel_block_56" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            y += /* param start [id:"InventoryPanel_value_115" type:"number"] */18/* param end [id:"InventoryPanel_value_115"] */;
            /* block end [id:"InventoryPanel_block_56"] */
            /* block comment [block:"InventoryPanel_block_57" text:"Runs the set font action. Values in this block are editable inputs."] */
            /* block start [id:"InventoryPanel_block_57" category:"Variables" label:"set font" shape:"command" docs:"Runs the set font action. Values in this block are editable inputs."] */
            fs = font((int)(/* param start [id:"InventoryPanel_value_116" type:"number"] */10/* param end [id:"InventoryPanel_value_116"] */*ds), /* param start [id:"InventoryPanel_value_117" type:"boolean"] */false/* param end [id:"InventoryPanel_value_117"] */);
            /* block end [id:"InventoryPanel_block_57"] */
            /* block comment [block:"InventoryPanel_block_10" text:"Repeats the nested operations for each loop turn."] */
            /* block start [id:"InventoryPanel_block_10" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
            for (int i = /* param start [id:"InventoryPanel_value_118" type:"number"] */0/* param end [id:"InventoryPanel_value_118"] */; i < SLOTS.length; i++) {
                /* block comment [block:"InventoryPanel_block_58" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"InventoryPanel_block_58" category:"Variables" label:"set it" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                Item it = Inventory.equipped.get(SLOTS[i]);
                /* block end [id:"InventoryPanel_block_58"] */
                /* block comment [block:"InventoryPanel_block_59" text:"Calls the drawString method using its declared arguments."] */
                /* block start [id:"InventoryPanel_block_59" category:"My Blocks" label:"call draw string" shape:"command" docs:"Calls the drawString method using its declared arguments."] */
                BitmapFont.draw(g, SLOTS[i] + /* param start [id:"InventoryPanel_value_119" type:"text"] */": "/* param end [id:"InventoryPanel_value_119"] */ + (it == /* param start [id:"InventoryPanel_value_120" type:"value"] */null/* param end [id:"InventoryPanel_value_120"] */ ? /* param start [id:"InventoryPanel_value_121" type:"text"] */"-"/* param end [id:"InventoryPanel_value_121"] */ : it.name), tx, y, fs);
                /* block end [id:"InventoryPanel_block_59"] */ /* block comment [block:"InventoryPanel_block_60" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"InventoryPanel_block_60" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                y += /* param start [id:"InventoryPanel_value_122" type:"number"] */16/* param end [id:"InventoryPanel_value_122"] */;
                /* block end [id:"InventoryPanel_block_60"] */
            }
            /* block end [id:"InventoryPanel_block_10"] */
            /* block comment [block:"InventoryPanel_block_61" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"InventoryPanel_block_61" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            y += /* param start [id:"InventoryPanel_value_123" type:"number"] */4/* param end [id:"InventoryPanel_value_123"] */;
            /* block end [id:"InventoryPanel_block_61"] */ /* block comment [block:"InventoryPanel_block_62" text:"Runs the set font action. Values in this block are editable inputs."] */
            /* block start [id:"InventoryPanel_block_62" category:"Variables" label:"set font" shape:"command" docs:"Runs the set font action. Values in this block are editable inputs."] */
            fs = font((int)(/* param start [id:"InventoryPanel_value_124" type:"number"] */11/* param end [id:"InventoryPanel_value_124"] */*ds), /* param start [id:"InventoryPanel_value_125" type:"boolean"] */true/* param end [id:"InventoryPanel_value_125"] */);
            /* block end [id:"InventoryPanel_block_62"] */ /* block comment [block:"InventoryPanel_block_63" text:"Calls the drawString method using its declared arguments."] */
            /* block start [id:"InventoryPanel_block_63" category:"My Blocks" label:"call draw string" shape:"command" docs:"Calls the drawString method using its declared arguments."] */
            BitmapFont.draw(g, /* param start [id:"InventoryPanel_value_126" type:"text"] */"-- Bag ("/* param end [id:"InventoryPanel_value_126"] */ + Inventory.bag.size() + /* param start [id:"InventoryPanel_value_127" type:"text"] */"/12) --"/* param end [id:"InventoryPanel_value_127"] */, tx, y, fs);
            /* block end [id:"InventoryPanel_block_63"] */ /* block comment [block:"InventoryPanel_block_64" text:"Evaluates this expression and applies its result."] */
            /* block start [id:"InventoryPanel_block_64" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
            y += /* param start [id:"InventoryPanel_value_128" type:"number"] */16/* param end [id:"InventoryPanel_value_128"] */;
            /* block end [id:"InventoryPanel_block_64"] */
            /* block comment [block:"InventoryPanel_block_65" text:"Runs the set font action. Values in this block are editable inputs."] */
            /* block start [id:"InventoryPanel_block_65" category:"Variables" label:"set font" shape:"command" docs:"Runs the set font action. Values in this block are editable inputs."] */
            fs = font((int)(/* param start [id:"InventoryPanel_value_129" type:"number"] */10/* param end [id:"InventoryPanel_value_129"] */*ds), /* param start [id:"InventoryPanel_value_130" type:"boolean"] */false/* param end [id:"InventoryPanel_value_130"] */);
            /* block end [id:"InventoryPanel_block_65"] */
            /* block comment [block:"InventoryPanel_block_11" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"InventoryPanel_block_11" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (Inventory.bag.isEmpty()) { /* block comment [block:"InventoryPanel_block_66" text:"Calls the drawString method using its declared arguments."] */
                /* block start [id:"InventoryPanel_block_66" category:"My Blocks" label:"call draw string" shape:"command" docs:"Calls the drawString method using its declared arguments."] */
                BitmapFont.draw(g, /* param start [id:"InventoryPanel_value_131" type:"text"] */"(empty)"/* param end [id:"InventoryPanel_value_131"] */, tx, y, fs);
                /* block end [id:"InventoryPanel_block_66"] */ /* block comment [block:"InventoryPanel_block_67" text:"Evaluates this expression and applies its result."] */
                /* block start [id:"InventoryPanel_block_67" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                y += /* param start [id:"InventoryPanel_value_132" type:"number"] */16/* param end [id:"InventoryPanel_value_132"] */;
                /* block end [id:"InventoryPanel_block_67"] */ }
            else {
                /* block comment [block:"InventoryPanel_block_12" text:"Repeats the nested operations for each loop turn."] */
                /* block start [id:"InventoryPanel_block_12" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
                for (int i = /* param start [id:"InventoryPanel_value_133" type:"number"] */0/* param end [id:"InventoryPanel_value_133"] */; i < Inventory.bag.size(); i++) {
                    /* block comment [block:"InventoryPanel_block_68" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    /* block start [id:"InventoryPanel_block_68" category:"Variables" label:"set it" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    Item it = Inventory.bag.get(i);
                    /* block end [id:"InventoryPanel_block_68"] */
                    /* block comment [block:"InventoryPanel_block_69" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    /* block start [id:"InventoryPanel_block_69" category:"Variables" label:"set t" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                    String t = it.name + /* param start [id:"InventoryPanel_value_134" type:"text"] */" ["/* param end [id:"InventoryPanel_value_134"] */ + it.statLine() + /* param start [id:"InventoryPanel_value_135" type:"text"] */"]"/* param end [id:"InventoryPanel_value_135"] */;
                    /* block end [id:"InventoryPanel_block_69"] */
                    /* block comment [block:"InventoryPanel_block_70" text:"Runs the if action. Values in this block are editable inputs."] */
                    /* block start [id:"InventoryPanel_block_70" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
                    /* block comment [block:"InventoryPanel_block_13" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"InventoryPanel_block_13" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (t.length() > /* param start [id:"InventoryPanel_value_136" type:"number"] */34/* param end [id:"InventoryPanel_value_136"] */) t = t.substring(/* param start [id:"InventoryPanel_value_137" type:"number"] */0/* param end [id:"InventoryPanel_value_137"] */, /* param start [id:"InventoryPanel_value_138" type:"number"] */34/* param end [id:"InventoryPanel_value_138"] */);
                    /* block end [id:"InventoryPanel_block_70"] */
                    /* block end [id:"InventoryPanel_block_13"] */
                    /* block comment [block:"InventoryPanel_block_14" text:"Checks a Boolean condition and runs the body when it is true."] */
                    /* block start [id:"InventoryPanel_block_14" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
                    if (i == selected) {
                        /* block comment [block:"InventoryPanel_block_71" text:"Runs the set color action. Values in this block are editable inputs."] */
                        /* block start [id:"InventoryPanel_block_71" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
                        g.setColor(SEL_FILL);
                        /* block end [id:"InventoryPanel_block_71"] */ /* block comment [block:"InventoryPanel_block_72" text:"Calls the fillRect method using its declared arguments."] */
                        /* block start [id:"InventoryPanel_block_72" category:"My Blocks" label:"call fill rect" shape:"command" docs:"Calls the fillRect method using its declared arguments."] */
                        g.fillRect(px + /* param start [id:"InventoryPanel_value_139" type:"number"] */8/* param end [id:"InventoryPanel_value_139"] */, y - /* param start [id:"InventoryPanel_value_140" type:"number"] */11/* param end [id:"InventoryPanel_value_140"] */, pw - /* param start [id:"InventoryPanel_value_141" type:"number"] */16/* param end [id:"InventoryPanel_value_141"] */, /* param start [id:"InventoryPanel_value_142" type:"number"] */14/* param end [id:"InventoryPanel_value_142"] */);
                        /* block end [id:"InventoryPanel_block_72"] */
                        /* block comment [block:"InventoryPanel_block_73" text:"Runs the set color action. Values in this block are editable inputs."] */
                        /* block start [id:"InventoryPanel_block_73" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
                        g.setColor(SEL);
                        /* block end [id:"InventoryPanel_block_73"] */
                    } /* block comment [block:"InventoryPanel_block_74" text:"Runs the set color action. Values in this block are editable inputs."] */
                    /* block start [id:"InventoryPanel_block_74" category:"Variables" label:"set color" shape:"command" docs:"Runs the set color action. Values in this block are editable inputs."] */
                    else g.setColor(INK);
                    /* block end [id:"InventoryPanel_block_74"] */
                    /* block end [id:"InventoryPanel_block_14"] */
                    /* block comment [block:"InventoryPanel_block_75" text:"Calls the drawString method using its declared arguments."] */
                    /* block start [id:"InventoryPanel_block_75" category:"My Blocks" label:"call draw string" shape:"command" docs:"Calls the drawString method using its declared arguments."] */
                    BitmapFont.draw(g, t, tx, y, fs);
                    /* block end [id:"InventoryPanel_block_75"] */ /* block comment [block:"InventoryPanel_block_76" text:"Evaluates this expression and applies its result."] */
                    /* block start [id:"InventoryPanel_block_76" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
                    y += /* param start [id:"InventoryPanel_value_143" type:"number"] */15/* param end [id:"InventoryPanel_value_143"] */;
                    /* block end [id:"InventoryPanel_block_76"] */
                }
                /* block end [id:"InventoryPanel_block_12"] */
            }
            /* block end [id:"InventoryPanel_block_11"] */
        }
        /* block end [id:"InventoryPanel_block_9"] */ finally { /* block comment [block:"InventoryPanel_block_77" text:"Runs the set transform action. Values in this block are editable inputs."] */
            /* block start [id:"InventoryPanel_block_77" category:"Variables" label:"set transform" shape:"command" docs:"Runs the set transform action. Values in this block are editable inputs."] */
            g.setTransform(old);
            /* block end [id:"InventoryPanel_block_77"] */ }
        cg.dispose();
        g = live;
        g.drawImage(panelCache, px, py, null);
        panelCacheKey = pkey;
        panelCacheValid = true;
    }
}
