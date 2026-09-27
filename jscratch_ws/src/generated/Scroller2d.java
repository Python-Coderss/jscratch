package generated;

import com.jscratch.*;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.*;

/**
* World scroller + minimap compositor. Owns scroll offset (x, y = -player),
* the 3x3 world tiles, 11x11 minimap tiles, colliders, floors, and the
* registered-sprite + minimap-border layer order.
*
* <p>Call graph / interactions:
* <ul>
*   <li>Reads: {@code Stage.getScaleFactor} for all positioning/sizing.</li>
*   <li>Written by: {@code Sprite1} (sets x/y = -player each step, then ONE
*       {@link #update()} per frame), {@code FloorManager} (clear/add
*       colliders, add walls/floors, re-tint), {@code BSprite} ctor
*       ({@link #register}) and {@code destroy()} ({@link #unregister}).</li>
*   <li>{@link #update()} repositions tiles + minimap + floors and calls
*       {@code update()} on every registered sprite. It performs NO
*       goToFront calls (layer order is fixed at init/register via
*       {@link #bringMinimapFront}) — per-frame fronting was the top
*       hotspot (~125 list ops per rebuild, ~15 rebuilds/frame).</li>
*   <li>Collision: {@link #isColliding} serves BSprite.tryMove/willCollide,
*       EnemyManager spawn checks, and projectile wall stops.</li>
* </ul>
*/
public class Scroller2d {
    private static Scroller2d instance;

    // World scrolling offset (typically -playerPos)
    public int x, y = 0;

    private List<BSprite> registeredSprites = new java.util.concurrent.CopyOnWriteArrayList<>();
    private List<BackgroundTile> tiles = new ArrayList<>();
    private List<MinimapTile> miniTiles = new ArrayList<>();
    // Rare-write, hot-read: floor rebuilds swap these while enemy/imp threads
    // collide against them, so snapshot semantics beat locking.
    private List<BCollider> colliders = new java.util.concurrent.CopyOnWriteArrayList<>();
    private List<Sprite> floors = new java.util.concurrent.CopyOnWriteArrayList<>();

    private Sprite minimapBg;
    private Sprite minimapBorder;
    private MinimapWalls minimapWalls;

    public void addCollider(int x, int y, int w, int h) {
        /* block comment [block:"Scroller2d_block_40" text:"Calls the add method using its declared arguments."] */
        /* block start [id:"Scroller2d_block_40" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
        colliders.add(new BCollider(x, y, w, h));
        /* block end [id:"Scroller2d_block_40"] */
    }

    public void clearColliders() {
        /* block comment [block:"Scroller2d_block_41" text:"Calls the clear method using its declared arguments."] */
        /* block start [id:"Scroller2d_block_41" category:"My Blocks" label:"call clear" shape:"command" docs:"Calls the clear method using its declared arguments."] */
        colliders.clear();
        /* block end [id:"Scroller2d_block_41"] */
    }

    public boolean isColliding(int wx, int wy, int ww, int wh) {
        /* block comment [block:"Scroller2d_block_1" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"Scroller2d_block_1" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (BCollider c : colliders) {
            /* block comment [block:"Scroller2d_block_42" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"Scroller2d_block_42" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"Scroller2d_block_2" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Scroller2d_block_2" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (c.intersects(wx - ww/ /* param start [id:"Scroller2d_value_137" type:"number"] */2/* param end [id:"Scroller2d_value_137"] */, wy - wh/ /* param start [id:"Scroller2d_value_138" type:"number"] */2/* param end [id:"Scroller2d_value_138"] */, ww, wh)) return /* param start [id:"Scroller2d_value_139" type:"boolean"] */true/* param end [id:"Scroller2d_value_139"] */;
            /* block end [id:"Scroller2d_block_42"] */
            /* block end [id:"Scroller2d_block_2"] */
        }
        /* block end [id:"Scroller2d_block_1"] */
        /* block comment [block:"Scroller2d_block_43" text:"Returns a value or ends the current method."] */
        /* block start [id:"Scroller2d_block_43" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return /* param start [id:"Scroller2d_value_140" type:"boolean"] */false/* param end [id:"Scroller2d_value_140"] */;
        /* block end [id:"Scroller2d_block_43"] */
    }

    public void addFloor(Sprite f) {
        /* block comment [block:"Scroller2d_block_44" text:"Calls the add method using its declared arguments."] */
        /* block start [id:"Scroller2d_block_44" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
        floors.add(f);
        /* block end [id:"Scroller2d_block_44"] */
        /* block comment [block:"Scroller2d_block_45" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Scroller2d_block_45" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().addSprite(f);
        /* block end [id:"Scroller2d_block_45"] */
        /* block comment [block:"Scroller2d_block_3" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Scroller2d_block_3" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!GameState.isGameplay()) {
            /* block comment [block:"Scroller2d_block_46" text:"Runs the hide action. Values in this block are editable inputs."] */
            /* block start [id:"Scroller2d_block_46" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
            f.hide();
            /* block end [id:"Scroller2d_block_46"] */
        }
        /* block end [id:"Scroller2d_block_3"] */
        /* block comment [block:"Scroller2d_block_47" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Scroller2d_block_47" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage.getInstance().goToBack(f);
        /* block end [id:"Scroller2d_block_47"] */
    }

    public List<BSprite> getRegisteredSprites() {
        /* block comment [block:"Scroller2d_block_48" text:"Returns a value or ends the current method."] */
        /* block start [id:"Scroller2d_block_48" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return registeredSprites;
        /* block end [id:"Scroller2d_block_48"] */
    }

    public static Scroller2d getInstance() {
        /* block comment [block:"Scroller2d_block_4" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Scroller2d_block_4" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (instance != /* param start [id:"Scroller2d_value_141" type:"value"] */null/* param end [id:"Scroller2d_value_141"] */) {
            /* block comment [block:"Scroller2d_block_49" text:"Returns a value or ends the current method."] */
            /* block start [id:"Scroller2d_block_49" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return instance;
            /* block end [id:"Scroller2d_block_49"] */
        } else {
            /* block comment [block:"Scroller2d_block_50" text:"Runs the scroller2d action. Values in this block are editable inputs."] */
            /* block start [id:"Scroller2d_block_50" category:"My Blocks" label:"scroller2d" shape:"command" docs:"Runs the scroller2d action. Values in this block are editable inputs."] */
            instance = new Scroller2d();
            /* block end [id:"Scroller2d_block_50"] */
            /* block comment [block:"Scroller2d_block_51" text:"Returns a value or ends the current method."] */
            /* block start [id:"Scroller2d_block_51" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return instance;
            /* block end [id:"Scroller2d_block_51"] */
        }
        /* block end [id:"Scroller2d_block_4"] */
    }

    /**
    * Registers a sprite to be managed by the scroller and displayed on the minimap.
    * New world sprites arrive above the minimap border, so re-front the
    * border once here instead of every frame in update().
    */
    public void register(BSprite s, MinimapSprite m) {
        /* block comment [block:"Scroller2d_block_5" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Scroller2d_block_5" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!registeredSprites.contains(s)) {
            /* block comment [block:"Scroller2d_block_52" text:"Calls the add method using its declared arguments."] */
            /* block start [id:"Scroller2d_block_52" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
            registeredSprites.add(s);
            /* block end [id:"Scroller2d_block_52"] */
        }
        /* block end [id:"Scroller2d_block_5"] */
        /* block comment [block:"Scroller2d_block_6" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Scroller2d_block_6" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (minimapBorder != /* param start [id:"Scroller2d_value_142" type:"value"] */null/* param end [id:"Scroller2d_value_142"] */) {
            /* block comment [block:"Scroller2d_block_53" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Scroller2d_block_53" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            Stage.getInstance().goToFront(minimapBorder);
            /* block end [id:"Scroller2d_block_53"] */
        }
        /* block end [id:"Scroller2d_block_6"] */
    }

    /** One-time layer repair (new HP bars, floor rebuilds). Never per frame. */
    public void bringMinimapFront() {        /* block comment [block:"Scroller2d_block_54" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Scroller2d_block_54" category:"Variables" label:"set stage" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage stage = Stage.getInstance();
        /* block end [id:"Scroller2d_block_54"] */
        /* block comment [block:"Scroller2d_block_55" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Scroller2d_block_55" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Scroller2d_block_7" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Scroller2d_block_7" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (minimapBg != /* param start [id:"Scroller2d_value_143" type:"value"] */null/* param end [id:"Scroller2d_value_143"] */) stage.goToFront(minimapBg);
        /* block end [id:"Scroller2d_block_55"] */
        /* block end [id:"Scroller2d_block_7"] */
        /* block comment [block:"Scroller2d_block_56" text:"Runs the for action. Values in this block are editable inputs."] */
        /* block start [id:"Scroller2d_block_56" category:"My Blocks" label:"for" shape:"command" docs:"Runs the for action. Values in this block are editable inputs."] */
        /* block comment [block:"Scroller2d_block_8" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"Scroller2d_block_8" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (MinimapTile t : miniTiles) stage.goToFront(t);
        /* block end [id:"Scroller2d_block_56"] */
        /* block end [id:"Scroller2d_block_8"] */
        /* block comment [block:"Scroller2d_block_57" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Scroller2d_block_57" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Scroller2d_block_9" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Scroller2d_block_9" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (minimapWalls != /* param start [id:"Scroller2d_value_144" type:"value"] */null/* param end [id:"Scroller2d_value_144"] */) stage.goToFront(minimapWalls);
        /* block end [id:"Scroller2d_block_57"] */
        /* block end [id:"Scroller2d_block_9"] */
        /* block comment [block:"Scroller2d_block_10" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"Scroller2d_block_10" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (BSprite s : registeredSprites) {
            /* block comment [block:"Scroller2d_block_11" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Scroller2d_block_11" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (s.minimapSprite != /* param start [id:"Scroller2d_value_145" type:"value"] */null/* param end [id:"Scroller2d_value_145"] */ && s.minimapSprite.isVisible()) {
                /* block comment [block:"Scroller2d_block_58" text:"Runs the go to front action. Values in this block are editable inputs."] */
                /* block start [id:"Scroller2d_block_58" category:"Motion" label:"go to front" shape:"command" docs:"Runs the go to front action. Values in this block are editable inputs."] */
                stage.goToFront(s.minimapSprite);
                /* block end [id:"Scroller2d_block_58"] */
            }
            /* block end [id:"Scroller2d_block_11"] */
        }
        /* block end [id:"Scroller2d_block_10"] */
        /* block comment [block:"Scroller2d_block_59" text:"Runs the if action. Values in this block are editable inputs."] */
        /* block start [id:"Scroller2d_block_59" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
        /* block comment [block:"Scroller2d_block_12" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Scroller2d_block_12" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (minimapBorder != /* param start [id:"Scroller2d_value_146" type:"value"] */null/* param end [id:"Scroller2d_value_146"] */) stage.goToFront(minimapBorder);
        /* block end [id:"Scroller2d_block_59"] */
        /* block end [id:"Scroller2d_block_12"] */
    }

    public Sprite getMinimapBorder() {
        /* block comment [block:"Scroller2d_block_60" text:"Returns a value or ends the current method."] */
        /* block start [id:"Scroller2d_block_60" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
        return minimapBorder;
        /* block end [id:"Scroller2d_block_60"] */
    }

    /** Remove a dead/transient sprite so update() stops visiting it. */
    public void unregister(BSprite s) {
        /* block comment [block:"Scroller2d_block_61" text:"Runs the remove action. Values in this block are editable inputs."] */
        /* block start [id:"Scroller2d_block_61" category:"Motion" label:"remove" shape:"command" docs:"Runs the remove action. Values in this block are editable inputs."] */
        registeredSprites.remove(s);
        /* block end [id:"Scroller2d_block_61"] */
    }

    public Scroller2d() {
        /* block comment [block:"Scroller2d_block_62" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Scroller2d_block_62" category:"Variables" label:"set stage" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage stage = Stage.getInstance();
        /* block end [id:"Scroller2d_block_62"] */

        // Ensure the UI updates immediately when the stage is resized
        stage.whenScaleChanged((old, newScale) -> {
            update();
        }/* block comment [block:"Scroller2d_block_63" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Scroller2d_block_63" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        );
        /* block end [id:"Scroller2d_block_63"] */

        // 1. Initialize Main World Tiles (3x3 grid)
        /* block comment [block:"Scroller2d_block_13" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"Scroller2d_block_13" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (int i = /* param start [id:"Scroller2d_value_147" type:"number"] */0/* param end [id:"Scroller2d_value_147"] */; i < /* param start [id:"Scroller2d_value_148" type:"number"] */9/* param end [id:"Scroller2d_value_148"] */; i++) {
            /* block comment [block:"Scroller2d_block_64" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Scroller2d_block_64" category:"Variables" label:"set tile" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            BackgroundTile tile = new BackgroundTile(/* param start [id:"Scroller2d_value_149" type:"text"] */"Tile"/* param end [id:"Scroller2d_value_149"] */ + i);
            /* block end [id:"Scroller2d_block_64"] */
            /* block comment [block:"Scroller2d_block_65" text:"Runs the add tile costumes action. Values in this block are editable inputs."] */
            /* block start [id:"Scroller2d_block_65" category:"Looks" label:"add tile costumes" shape:"command" docs:"Runs the add tile costumes action. Values in this block are editable inputs."] */
            addTileCostumes(tile);
            /* block end [id:"Scroller2d_block_65"] */
            /* block comment [block:"Scroller2d_block_66" text:"Calls the add method using its declared arguments."] */
            /* block start [id:"Scroller2d_block_66" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
            tiles.add(tile);
            /* block end [id:"Scroller2d_block_66"] */
            /* block comment [block:"Scroller2d_block_67" text:"Calls the addSprite method using its declared arguments."] */
            /* block start [id:"Scroller2d_block_67" category:"My Blocks" label:"call add sprite" shape:"command" docs:"Calls the addSprite method using its declared arguments."] */
            stage.addSprite(tile);
            /* block end [id:"Scroller2d_block_67"] */
            /* block comment [block:"Scroller2d_block_68" text:"Runs the go to back action. Values in this block are editable inputs."] */
            /* block start [id:"Scroller2d_block_68" category:"Motion" label:"go to back" shape:"command" docs:"Runs the go to back action. Values in this block are editable inputs."] */
            stage.goToBack(tile);
            /* block end [id:"Scroller2d_block_68"] */
        }
        /* block end [id:"Scroller2d_block_13"] */

        // 2. Initialize Minimap Background
        /* block comment [block:"Scroller2d_block_69" text:"Runs the sprite action. Values in this block are editable inputs."] */
        /* block start [id:"Scroller2d_block_69" category:"My Blocks" label:"sprite" shape:"command" docs:"Runs the sprite action. Values in this block are editable inputs."] */
        minimapBg = new Sprite(/* param start [id:"Scroller2d_value_150" type:"text"] */"MinimapBG"/* param end [id:"Scroller2d_value_150"] */);
        /* block end [id:"Scroller2d_block_69"] */
        /* block comment [block:"Scroller2d_block_70" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"Scroller2d_block_70" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        minimapBg.addCostume(new Costume(/* param start [id:"Scroller2d_value_151" type:"text"] */"bg"/* param end [id:"Scroller2d_value_151"] */, /* param start [id:"Scroller2d_value_152" type:"text"] */"jscratch_ws/assets/minimap_bg.png"/* param end [id:"Scroller2d_value_152"] */, MinimapConfig.WIDTH/ /* param start [id:"Scroller2d_value_153" type:"number"] */2/* param end [id:"Scroller2d_value_153"] */, MinimapConfig.HEIGHT/ /* param start [id:"Scroller2d_value_154" type:"number"] */2/* param end [id:"Scroller2d_value_154"] */));
        /* block end [id:"Scroller2d_block_70"] */
        /* block comment [block:"Scroller2d_block_71" text:"Runs the go to action. Values in this block are editable inputs."] */
        /* block start [id:"Scroller2d_block_71" category:"Motion" label:"go to" shape:"command" docs:"Runs the go to action. Values in this block are editable inputs."] */
        minimapBg.goTo(MinimapConfig.CENTER_X, MinimapConfig.CENTER_Y);
        /* block end [id:"Scroller2d_block_71"] */
        /* block comment [block:"Scroller2d_block_72" text:"Calls the addSprite method using its declared arguments."] */
        /* block start [id:"Scroller2d_block_72" category:"My Blocks" label:"call add sprite" shape:"command" docs:"Calls the addSprite method using its declared arguments."] */
        stage.addSprite(minimapBg);
        /* block end [id:"Scroller2d_block_72"] */

        // 3. Initialize Minimap Terrain Tiles
        /* block comment [block:"Scroller2d_block_14" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"Scroller2d_block_14" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (int i = /* param start [id:"Scroller2d_value_155" type:"number"] */0/* param end [id:"Scroller2d_value_155"] */; i < MinimapConfig.TOTAL_MINI_TILES; i++) {
            /* block comment [block:"Scroller2d_block_73" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
            /* block start [id:"Scroller2d_block_73" category:"Variables" label:"set mTile" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
            MinimapTile mTile = new MinimapTile(/* param start [id:"Scroller2d_value_156" type:"text"] */"MTile"/* param end [id:"Scroller2d_value_156"] */ + i);
            /* block end [id:"Scroller2d_block_73"] */
            /* block comment [block:"Scroller2d_block_74" text:"Runs the add tile costumes action. Values in this block are editable inputs."] */
            /* block start [id:"Scroller2d_block_74" category:"Looks" label:"add tile costumes" shape:"command" docs:"Runs the add tile costumes action. Values in this block are editable inputs."] */
            addTileCostumes(mTile);
            /* block end [id:"Scroller2d_block_74"] */
            /* block comment [block:"Scroller2d_block_75" text:"Calls the add method using its declared arguments."] */
            /* block start [id:"Scroller2d_block_75" category:"My Blocks" label:"call add" shape:"command" docs:"Calls the add method using its declared arguments."] */
            miniTiles.add(mTile);
            /* block end [id:"Scroller2d_block_75"] */
            /* block comment [block:"Scroller2d_block_76" text:"Calls the addSprite method using its declared arguments."] */
            /* block start [id:"Scroller2d_block_76" category:"My Blocks" label:"call add sprite" shape:"command" docs:"Calls the addSprite method using its declared arguments."] */
            stage.addSprite(mTile);
            /* block end [id:"Scroller2d_block_76"] */
        }
        /* block end [id:"Scroller2d_block_14"] */

        // 4. Initialize Minimap Border (Sits on top of everything)
        /* block comment [block:"Scroller2d_block_77" text:"Runs the sprite action. Values in this block are editable inputs."] */
        /* block start [id:"Scroller2d_block_77" category:"My Blocks" label:"sprite" shape:"command" docs:"Runs the sprite action. Values in this block are editable inputs."] */
        minimapBorder = new Sprite(/* param start [id:"Scroller2d_value_157" type:"text"] */"MinimapBorder"/* param end [id:"Scroller2d_value_157"] */);
        /* block end [id:"Scroller2d_block_77"] */
        // Center for 168x126 image is 84, 63
        /* block comment [block:"Scroller2d_block_78" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"Scroller2d_block_78" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        minimapBorder.addCostume(new Costume(/* param start [id:"Scroller2d_value_158" type:"text"] */"border"/* param end [id:"Scroller2d_value_158"] */, /* param start [id:"Scroller2d_value_159" type:"text"] */"jscratch_ws/assets/minimap_border.png"/* param end [id:"Scroller2d_value_159"] */, /* param start [id:"Scroller2d_value_160" type:"number"] */84/* param end [id:"Scroller2d_value_160"] */, /* param start [id:"Scroller2d_value_161" type:"number"] */63/* param end [id:"Scroller2d_value_161"] */));
        /* block end [id:"Scroller2d_block_78"] */
        /* block comment [block:"Scroller2d_block_79" text:"Runs the go to action. Values in this block are editable inputs."] */
        /* block start [id:"Scroller2d_block_79" category:"Motion" label:"go to" shape:"command" docs:"Runs the go to action. Values in this block are editable inputs."] */
        minimapBorder.goTo(MinimapConfig.CENTER_X, MinimapConfig.CENTER_Y);
        /* block end [id:"Scroller2d_block_79"] */
        /* block comment [block:"Scroller2d_block_80" text:"Calls the addSprite method using its declared arguments."] */
        /* block start [id:"Scroller2d_block_80" category:"My Blocks" label:"call add sprite" shape:"command" docs:"Calls the addSprite method using its declared arguments."] */
        stage.addSprite(minimapBorder);
        /* block end [id:"Scroller2d_block_80"] */

        // 5. Minimap wall overlay (drawn above terrain, below icons)
        /* block comment [block:"Scroller2d_block_81" text:"Runs the minimap walls action. Values in this block are editable inputs."] */
        /* block start [id:"Scroller2d_block_81" category:"My Blocks" label:"minimap walls" shape:"command" docs:"Runs the minimap walls action. Values in this block are editable inputs."] */
        minimapWalls = new MinimapWalls();
        /* block end [id:"Scroller2d_block_81"] */
        /* block comment [block:"Scroller2d_block_82" text:"Calls the addSprite method using its declared arguments."] */
        /* block start [id:"Scroller2d_block_82" category:"My Blocks" label:"call add sprite" shape:"command" docs:"Calls the addSprite method using its declared arguments."] */
        stage.addSprite(minimapWalls);
        /* block end [id:"Scroller2d_block_82"] */

        // Game-state visibility: world chrome (tiles + minimap) is
        // gameplay-only. CharacterCreation owns the screen until
        // GameState.toGameplay() fires "enterGameplay".
        stage.whenIReceive("enterGameplay", () -> {
            setWorldVisible(true);
            update();
        }/* block comment [block:"Scroller2d_block_83" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"Scroller2d_block_83" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        );
        /* block end [id:"Scroller2d_block_83"] */
        /* block comment [block:"Scroller2d_block_84" text:"Runs the when ireceive action. Values in this block are editable inputs."] */
        /* block start [id:"Scroller2d_block_84" category:"Events" label:"when ireceive" shape:"command" docs:"Runs the when ireceive action. Values in this block are editable inputs."] */
        stage.whenIReceive(/* param start [id:"Scroller2d_value_162" type:"text"] */"enterCharacterCreate"/* param end [id:"Scroller2d_value_162"] */, () -> setWorldVisible(/* param start [id:"Scroller2d_value_163" type:"boolean"] */false/* param end [id:"Scroller2d_value_163"] */));
        /* block end [id:"Scroller2d_block_84"] */
        /* block comment [block:"Scroller2d_block_15" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Scroller2d_block_15" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!GameState.isGameplay()) {
            /* block comment [block:"Scroller2d_block_85" text:"Runs the set world visible action. Values in this block are editable inputs."] */
            /* block start [id:"Scroller2d_block_85" category:"Variables" label:"set world visible" shape:"command" docs:"Runs the set world visible action. Values in this block are editable inputs."] */
            setWorldVisible(/* param start [id:"Scroller2d_value_164" type:"boolean"] */false/* param end [id:"Scroller2d_value_164"] */);
            /* block end [id:"Scroller2d_block_85"] */
        } else {
            /* block comment [block:"Scroller2d_block_86" text:"Calls the update method using its declared arguments."] */
            /* block start [id:"Scroller2d_block_86" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
            update();
            /* block end [id:"Scroller2d_block_86"] */
        }
        /* block end [id:"Scroller2d_block_15"] */
    }

    /**
    * Shows/hides the gameplay-only world chrome: 3x3 world tiles, minimap
    * background + terrain + walls overlay + border, and any floor sprites.
    * Registered world sprites (walls/enemies/...) are owned by their own
    * managers (FloorManager/EnemyManager) so they are left alone here —
    * except their minimap dots, which BSprite.update already hides with
    * the owner.
    */
    public void setWorldVisible(boolean visible) {
        /* block comment [block:"Scroller2d_block_16" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"Scroller2d_block_16" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (BackgroundTile t : tiles) {
            /* block comment [block:"Scroller2d_block_87" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"Scroller2d_block_87" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"Scroller2d_block_17" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Scroller2d_block_17" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (visible) t.show();
            /* block end [id:"Scroller2d_block_87"] */ /* block comment [block:"Scroller2d_block_88" text:"Runs the hide action. Values in this block are editable inputs."] */
            /* block start [id:"Scroller2d_block_88" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
            else t.hide();
            /* block end [id:"Scroller2d_block_88"] */
            /* block end [id:"Scroller2d_block_17"] */
        }
        /* block end [id:"Scroller2d_block_16"] */
        /* block comment [block:"Scroller2d_block_18" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"Scroller2d_block_18" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (MinimapTile t : miniTiles) {
            /* block comment [block:"Scroller2d_block_89" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"Scroller2d_block_89" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"Scroller2d_block_19" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Scroller2d_block_19" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (visible) t.show();
            /* block end [id:"Scroller2d_block_89"] */ /* block comment [block:"Scroller2d_block_90" text:"Runs the hide action. Values in this block are editable inputs."] */
            /* block start [id:"Scroller2d_block_90" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
            else t.hide();
            /* block end [id:"Scroller2d_block_90"] */
            /* block end [id:"Scroller2d_block_19"] */
        }
        /* block end [id:"Scroller2d_block_18"] */
        /* block comment [block:"Scroller2d_block_20" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Scroller2d_block_20" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (minimapBg != /* param start [id:"Scroller2d_value_165" type:"value"] */null/* param end [id:"Scroller2d_value_165"] */) {
            /* block comment [block:"Scroller2d_block_91" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"Scroller2d_block_91" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"Scroller2d_block_21" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Scroller2d_block_21" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (visible) minimapBg.show();
            /* block end [id:"Scroller2d_block_91"] */ /* block comment [block:"Scroller2d_block_92" text:"Runs the hide action. Values in this block are editable inputs."] */
            /* block start [id:"Scroller2d_block_92" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
            else minimapBg.hide();
            /* block end [id:"Scroller2d_block_92"] */
            /* block end [id:"Scroller2d_block_21"] */
        }
        /* block end [id:"Scroller2d_block_20"] */
        /* block comment [block:"Scroller2d_block_22" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Scroller2d_block_22" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (minimapBorder != /* param start [id:"Scroller2d_value_166" type:"value"] */null/* param end [id:"Scroller2d_value_166"] */) {
            /* block comment [block:"Scroller2d_block_93" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"Scroller2d_block_93" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"Scroller2d_block_23" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Scroller2d_block_23" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (visible) minimapBorder.show();
            /* block end [id:"Scroller2d_block_93"] */ /* block comment [block:"Scroller2d_block_94" text:"Runs the hide action. Values in this block are editable inputs."] */
            /* block start [id:"Scroller2d_block_94" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
            else minimapBorder.hide();
            /* block end [id:"Scroller2d_block_94"] */
            /* block end [id:"Scroller2d_block_23"] */
        }
        /* block end [id:"Scroller2d_block_22"] */
        /* block comment [block:"Scroller2d_block_24" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Scroller2d_block_24" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (minimapWalls != /* param start [id:"Scroller2d_value_167" type:"value"] */null/* param end [id:"Scroller2d_value_167"] */) {
            /* block comment [block:"Scroller2d_block_95" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"Scroller2d_block_95" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"Scroller2d_block_25" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Scroller2d_block_25" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (visible) minimapWalls.show();
            /* block end [id:"Scroller2d_block_95"] */ /* block comment [block:"Scroller2d_block_96" text:"Runs the hide action. Values in this block are editable inputs."] */
            /* block start [id:"Scroller2d_block_96" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
            else minimapWalls.hide();
            /* block end [id:"Scroller2d_block_96"] */
            /* block end [id:"Scroller2d_block_25"] */
        }
        /* block end [id:"Scroller2d_block_24"] */
        /* block comment [block:"Scroller2d_block_26" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"Scroller2d_block_26" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (Sprite f : floors) {
            /* block comment [block:"Scroller2d_block_97" text:"Runs the if action. Values in this block are editable inputs."] */
            /* block start [id:"Scroller2d_block_97" category:"My Blocks" label:"if" shape:"command" docs:"Runs the if action. Values in this block are editable inputs."] */
            /* block comment [block:"Scroller2d_block_27" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Scroller2d_block_27" category:"Control" label:"if then else" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (visible) f.show();
            /* block end [id:"Scroller2d_block_97"] */ /* block comment [block:"Scroller2d_block_98" text:"Runs the hide action. Values in this block are editable inputs."] */
            /* block start [id:"Scroller2d_block_98" category:"Looks" label:"hide" shape:"command" docs:"Runs the hide action. Values in this block are editable inputs."] */
            else f.hide();
            /* block end [id:"Scroller2d_block_98"] */
            /* block end [id:"Scroller2d_block_27"] */
        }
        /* block end [id:"Scroller2d_block_26"] */
    }

    /**
    * Loads the set of possible costumes for a tile sprite.
    */
    private void addTileCostumes(Sprite s) {
        /* block comment [block:"Scroller2d_block_28" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"Scroller2d_block_28" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (int tx = -/* param start [id:"Scroller2d_value_168" type:"number"] */1/* param end [id:"Scroller2d_value_168"] */; tx <= /* param start [id:"Scroller2d_value_169" type:"number"] */1/* param end [id:"Scroller2d_value_169"] */; tx++) {
            /* block comment [block:"Scroller2d_block_29" text:"Repeats the nested operations for each loop turn."] */
            /* block start [id:"Scroller2d_block_29" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
            for (int ty = -/* param start [id:"Scroller2d_value_170" type:"number"] */1/* param end [id:"Scroller2d_value_170"] */; ty <= /* param start [id:"Scroller2d_value_171" type:"number"] */1/* param end [id:"Scroller2d_value_171"] */; ty++) {
                /* block comment [block:"Scroller2d_block_99" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"Scroller2d_block_99" category:"Variables" label:"set id" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                String id = tx + /* param start [id:"Scroller2d_value_172" type:"text"] */","/* param end [id:"Scroller2d_value_172"] */ + ty;
                /* block end [id:"Scroller2d_block_99"] */
                /* block comment [block:"Scroller2d_block_100" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"Scroller2d_block_100" category:"Variables" label:"set path" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                String path = /* param start [id:"Scroller2d_value_173" type:"text"] */"jscratch_ws/assets/"/* param end [id:"Scroller2d_value_173"] */ + id + /* param start [id:"Scroller2d_value_174" type:"text"] */".png"/* param end [id:"Scroller2d_value_174"] */;
                /* block end [id:"Scroller2d_block_100"] */
                /* block comment [block:"Scroller2d_block_101" text:"Runs the add costume action. Values in this block are editable inputs."] */
                /* block start [id:"Scroller2d_block_101" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
                s.addCostume(ResourcePool.getCostume(id, path, /* param start [id:"Scroller2d_value_175" type:"number"] */240/* param end [id:"Scroller2d_value_175"] */, /* param start [id:"Scroller2d_value_176" type:"number"] */180/* param end [id:"Scroller2d_value_176"] */));
                /* block end [id:"Scroller2d_block_101"] */
            }
            /* block end [id:"Scroller2d_block_29"] */
        }
        /* block end [id:"Scroller2d_block_28"] */
        /* block comment [block:"Scroller2d_block_102" text:"Runs the add costume action. Values in this block are editable inputs."] */
        /* block start [id:"Scroller2d_block_102" category:"Looks" label:"add costume" shape:"command" docs:"Runs the add costume action. Values in this block are editable inputs."] */
        s.addCostume(ResourcePool.getCostume(/* param start [id:"Scroller2d_value_177" type:"text"] */"err"/* param end [id:"Scroller2d_value_177"] */, /* param start [id:"Scroller2d_value_178" type:"text"] */"jscratch_ws/assets/error.png"/* param end [id:"Scroller2d_value_178"] */, /* param start [id:"Scroller2d_value_179" type:"number"] */240/* param end [id:"Scroller2d_value_179"] */, /* param start [id:"Scroller2d_value_180" type:"number"] */180/* param end [id:"Scroller2d_value_180"] */));
        /* block end [id:"Scroller2d_block_102"] */
    }

    /**
    * Main update loop for positioning all world and UI elements.
    * NOTE: no goToFront calls here. Layer order is fixed at init/register
    * time (see bringMinimapFront); per-frame fronting was ~125 COW array
    * copies per update x N updates per frame.
    */
    public void update() {
        // Gameplay-only scene: during character creation the menu owns the
        // screen. Bail before any MinimapTile/MinimapSprite.update() can
        // re-show() hidden tiles/dots, and keep the world hidden.
        /* block comment [block:"Scroller2d_block_30" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Scroller2d_block_30" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (!GameState.isGameplay()) {
            /* block comment [block:"Scroller2d_block_103" text:"Runs the set world visible action. Values in this block are editable inputs."] */
            /* block start [id:"Scroller2d_block_103" category:"Variables" label:"set world visible" shape:"command" docs:"Runs the set world visible action. Values in this block are editable inputs."] */
            setWorldVisible(/* param start [id:"Scroller2d_value_181" type:"boolean"] */false/* param end [id:"Scroller2d_value_181"] */);
            /* block end [id:"Scroller2d_block_103"] */
            /* block comment [block:"Scroller2d_block_104" text:"Returns a value or ends the current method."] */
            /* block start [id:"Scroller2d_block_104" category:"Control" label:"return" shape:"command" docs:"Returns a value or ends the current method."] */
            return;
            /* block end [id:"Scroller2d_block_104"] */
        }
        /* block end [id:"Scroller2d_block_30"] */
        /* block comment [block:"Scroller2d_block_105" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Scroller2d_block_105" category:"Variables" label:"set stage" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage stage = Stage.getInstance();
        /* block end [id:"Scroller2d_block_105"] */
        /* block comment [block:"Scroller2d_block_106" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Scroller2d_block_106" category:"Variables" label:"set displayScale" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double displayScale = stage.getScaleFactor();
        /* block end [id:"Scroller2d_block_106"] */

        // Calculate which tile the camera (player) is currently in
        /* block comment [block:"Scroller2d_block_107" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Scroller2d_block_107" category:"Variables" label:"set tcx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int tcx = (int) Math.round((double)-x / MinimapConfig.WORLD_TILE_W);
        /* block end [id:"Scroller2d_block_107"] */
        /* block comment [block:"Scroller2d_block_108" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Scroller2d_block_108" category:"Variables" label:"set tcy" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int tcy = (int) Math.round((double)-y / MinimapConfig.WORLD_TILE_H);
        /* block end [id:"Scroller2d_block_108"] */

        // --- Update Main World Tiles ---
        /* block comment [block:"Scroller2d_block_109" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Scroller2d_block_109" category:"Variables" label:"set tileIdx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int tileIdx = /* param start [id:"Scroller2d_value_182" type:"number"] */0/* param end [id:"Scroller2d_value_182"] */;
        /* block end [id:"Scroller2d_block_109"] */
        /* block comment [block:"Scroller2d_block_31" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"Scroller2d_block_31" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (int dy = /* param start [id:"Scroller2d_value_183" type:"number"] */1/* param end [id:"Scroller2d_value_183"] */; dy >= -/* param start [id:"Scroller2d_value_184" type:"number"] */1/* param end [id:"Scroller2d_value_184"] */; dy--) {
            /* block comment [block:"Scroller2d_block_32" text:"Repeats the nested operations for each loop turn."] */
            /* block start [id:"Scroller2d_block_32" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
            for (int dx = -/* param start [id:"Scroller2d_value_185" type:"number"] */1/* param end [id:"Scroller2d_value_185"] */; dx <= /* param start [id:"Scroller2d_value_186" type:"number"] */1/* param end [id:"Scroller2d_value_186"] */; dx++) {
                /* block comment [block:"Scroller2d_block_110" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"Scroller2d_block_110" category:"Variables" label:"set worldX" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int worldX = tcx + dx;
                /* block end [id:"Scroller2d_block_110"] */
                /* block comment [block:"Scroller2d_block_111" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"Scroller2d_block_111" category:"Variables" label:"set worldY" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int worldY = tcy + dy;
                /* block end [id:"Scroller2d_block_111"] */

                /* block comment [block:"Scroller2d_block_112" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"Scroller2d_block_112" category:"Variables" label:"set tile" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                BackgroundTile tile = tiles.get(tileIdx++);
                /* block end [id:"Scroller2d_block_112"] */

                /* block comment [block:"Scroller2d_block_113" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"Scroller2d_block_113" category:"Variables" label:"set screenX" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int screenX = (int)(((worldX * MinimapConfig.WORLD_TILE_W) + x) * displayScale);
                /* block end [id:"Scroller2d_block_113"] */
                /* block comment [block:"Scroller2d_block_114" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"Scroller2d_block_114" category:"Variables" label:"set screenY" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int screenY = (int)(((worldY * MinimapConfig.WORLD_TILE_H) + y) * displayScale);
                /* block end [id:"Scroller2d_block_114"] */

                /* block comment [block:"Scroller2d_block_115" text:"Runs the super go to action. Values in this block are editable inputs."] */
                /* block start [id:"Scroller2d_block_115" category:"Motion" label:"super go to" shape:"command" docs:"Runs the super go to action. Values in this block are editable inputs."] */
                tile.superGoTo(screenX, screenY);
                /* block end [id:"Scroller2d_block_115"] */
                /* block comment [block:"Scroller2d_block_116" text:"Runs the set scale x action. Values in this block are editable inputs."] */
                /* block start [id:"Scroller2d_block_116" category:"Variables" label:"set scale x" shape:"command" docs:"Runs the set scale x action. Values in this block are editable inputs."] */
                tile.setScaleX(displayScale);
                /* block end [id:"Scroller2d_block_116"] */
                /* block comment [block:"Scroller2d_block_117" text:"Runs the set scale y action. Values in this block are editable inputs."] */
                /* block start [id:"Scroller2d_block_117" category:"Variables" label:"set scale y" shape:"command" docs:"Runs the set scale y action. Values in this block are editable inputs."] */
                tile.setScaleY(displayScale);
                /* block end [id:"Scroller2d_block_117"] */
                /* block comment [block:"Scroller2d_block_118" text:"Runs the switch costume action. Values in this block are editable inputs."] */
                /* block start [id:"Scroller2d_block_118" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
                tile.switchCostume(worldX + /* param start [id:"Scroller2d_value_187" type:"text"] */","/* param end [id:"Scroller2d_value_187"] */ + worldY);
                /* block end [id:"Scroller2d_block_118"] */
            }
            /* block end [id:"Scroller2d_block_32"] */
        }
        /* block end [id:"Scroller2d_block_31"] */

        // --- Update Minimap Layers ---

        // 1. Background
        /* block comment [block:"Scroller2d_block_33" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Scroller2d_block_33" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (minimapBg != /* param start [id:"Scroller2d_value_188" type:"value"] */null/* param end [id:"Scroller2d_value_188"] */) {
            /* block comment [block:"Scroller2d_block_119" text:"Runs the go to action. Values in this block are editable inputs."] */
            /* block start [id:"Scroller2d_block_119" category:"Motion" label:"go to" shape:"command" docs:"Runs the go to action. Values in this block are editable inputs."] */
            minimapBg.goTo(MinimapConfig.CENTER_X * displayScale, MinimapConfig.CENTER_Y * displayScale);
            /* block end [id:"Scroller2d_block_119"] */
            /* block comment [block:"Scroller2d_block_120" text:"Runs the set scale x action. Values in this block are editable inputs."] */
            /* block start [id:"Scroller2d_block_120" category:"Variables" label:"set scale x" shape:"command" docs:"Runs the set scale x action. Values in this block are editable inputs."] */
            minimapBg.setScaleX(displayScale);
            /* block end [id:"Scroller2d_block_120"] */
            /* block comment [block:"Scroller2d_block_121" text:"Runs the set scale y action. Values in this block are editable inputs."] */
            /* block start [id:"Scroller2d_block_121" category:"Variables" label:"set scale y" shape:"command" docs:"Runs the set scale y action. Values in this block are editable inputs."] */
            minimapBg.setScaleY(displayScale);
            /* block end [id:"Scroller2d_block_121"] */
        }
        /* block end [id:"Scroller2d_block_33"] */

        // 2. Terrain Tiles
        /* block comment [block:"Scroller2d_block_122" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Scroller2d_block_122" category:"Variables" label:"set miniIdx" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int miniIdx = /* param start [id:"Scroller2d_value_189" type:"number"] */0/* param end [id:"Scroller2d_value_189"] */;
        /* block end [id:"Scroller2d_block_122"] */
        /* block comment [block:"Scroller2d_block_123" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"Scroller2d_block_123" category:"Variables" label:"set r" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        int r = MinimapConfig.RENDER_RADIUS_TILES;
        /* block end [id:"Scroller2d_block_123"] */
        /* block comment [block:"Scroller2d_block_34" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"Scroller2d_block_34" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (int dy = r; dy >= -r; dy--) {
            /* block comment [block:"Scroller2d_block_35" text:"Repeats the nested operations for each loop turn."] */
            /* block start [id:"Scroller2d_block_35" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
            for (int dx = -r; dx <= r; dx++) {
                /* block comment [block:"Scroller2d_block_124" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"Scroller2d_block_124" category:"Variables" label:"set worldX" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int worldX = tcx + dx;
                /* block end [id:"Scroller2d_block_124"] */
                /* block comment [block:"Scroller2d_block_125" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"Scroller2d_block_125" category:"Variables" label:"set worldY" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                int worldY = tcy + dy;
                /* block end [id:"Scroller2d_block_125"] */

                /* block comment [block:"Scroller2d_block_126" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
                /* block start [id:"Scroller2d_block_126" category:"Variables" label:"set mTile" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
                MinimapTile mTile = miniTiles.get(miniIdx++);
                /* block end [id:"Scroller2d_block_126"] */
                /* block comment [block:"Scroller2d_block_127" text:"Calls the update method using its declared arguments."] */
                /* block start [id:"Scroller2d_block_127" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
                mTile.update(worldX * MinimapConfig.WORLD_TILE_W, worldY * MinimapConfig.WORLD_TILE_H, -x, -y);
                /* block end [id:"Scroller2d_block_127"] */

                /* block comment [block:"Scroller2d_block_128" text:"Runs the set scale x action. Values in this block are editable inputs."] */
                /* block start [id:"Scroller2d_block_128" category:"Variables" label:"set scale x" shape:"command" docs:"Runs the set scale x action. Values in this block are editable inputs."] */
                mTile.setScaleX(MinimapConfig.SCALE * displayScale);
                /* block end [id:"Scroller2d_block_128"] */
                /* block comment [block:"Scroller2d_block_129" text:"Runs the set scale y action. Values in this block are editable inputs."] */
                /* block start [id:"Scroller2d_block_129" category:"Variables" label:"set scale y" shape:"command" docs:"Runs the set scale y action. Values in this block are editable inputs."] */
                mTile.setScaleY(MinimapConfig.SCALE * displayScale);
                /* block end [id:"Scroller2d_block_129"] */
                /* block comment [block:"Scroller2d_block_130" text:"Runs the go to action. Values in this block are editable inputs."] */
                /* block start [id:"Scroller2d_block_130" category:"Motion" label:"go to" shape:"command" docs:"Runs the go to action. Values in this block are editable inputs."] */
                mTile.goTo(mTile.getX() * displayScale, mTile.getY() * displayScale);
                /* block end [id:"Scroller2d_block_130"] */

                /* block comment [block:"Scroller2d_block_131" text:"Runs the switch costume action. Values in this block are editable inputs."] */
                /* block start [id:"Scroller2d_block_131" category:"Looks" label:"switch costume" shape:"command" docs:"Runs the switch costume action. Values in this block are editable inputs."] */
                mTile.switchCostume(worldX + /* param start [id:"Scroller2d_value_190" type:"text"] */","/* param end [id:"Scroller2d_value_190"] */ + worldY);
                /* block end [id:"Scroller2d_block_131"] */
            }
            /* block end [id:"Scroller2d_block_35"] */
        }
        /* block end [id:"Scroller2d_block_34"] */

        // 3. Icons (Players, NPCs) - These must go ABOVE the terrain tiles
        /* block comment [block:"Scroller2d_block_36" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"Scroller2d_block_36" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (BSprite s : registeredSprites) {
            /* block comment [block:"Scroller2d_block_132" text:"Calls the update method using its declared arguments."] */
            /* block start [id:"Scroller2d_block_132" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
            s.update();
            /* block end [id:"Scroller2d_block_132"] */
        }
        /* block end [id:"Scroller2d_block_36"] */

        // 5. Floors (World Background objects)
        /* block comment [block:"Scroller2d_block_37" text:"Repeats the nested operations for each loop turn."] */
        /* block start [id:"Scroller2d_block_37" category:"Control" label:"repeat / for" shape:"control" docs:"Repeats the nested operations for each loop turn."] */
        for (Sprite f : floors) {
            /* block comment [block:"Scroller2d_block_38" text:"Checks a Boolean condition and runs the body when it is true."] */
            /* block start [id:"Scroller2d_block_38" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
            if (f instanceof FloorSprite) {
                /* block comment [block:"Scroller2d_block_133" text:"Runs the update action. Values in this block are editable inputs."] */
                /* block start [id:"Scroller2d_block_133" category:"My Blocks" label:"update" shape:"command" docs:"Runs the update action. Values in this block are editable inputs."] */
                ((FloorSprite)f).update();
                /* block end [id:"Scroller2d_block_133"] */
            }
            /* block end [id:"Scroller2d_block_38"] */
        }
        /* block end [id:"Scroller2d_block_37"] */

        // 4. Border (Always on top of icons)
        /* block comment [block:"Scroller2d_block_39" text:"Checks a Boolean condition and runs the body when it is true."] */
        /* block start [id:"Scroller2d_block_39" category:"Control" label:"if then" shape:"control" docs:"Checks a Boolean condition and runs the body when it is true."] */
        if (minimapBorder != /* param start [id:"Scroller2d_value_191" type:"value"] */null/* param end [id:"Scroller2d_value_191"] */) {
            /* block comment [block:"Scroller2d_block_134" text:"Runs the go to action. Values in this block are editable inputs."] */
            /* block start [id:"Scroller2d_block_134" category:"Motion" label:"go to" shape:"command" docs:"Runs the go to action. Values in this block are editable inputs."] */
            minimapBorder.goTo(MinimapConfig.CENTER_X * displayScale, MinimapConfig.CENTER_Y * displayScale);
            /* block end [id:"Scroller2d_block_134"] */
            /* block comment [block:"Scroller2d_block_135" text:"Runs the set scale x action. Values in this block are editable inputs."] */
            /* block start [id:"Scroller2d_block_135" category:"Variables" label:"set scale x" shape:"command" docs:"Runs the set scale x action. Values in this block are editable inputs."] */
            minimapBorder.setScaleX(displayScale);
            /* block end [id:"Scroller2d_block_135"] */
            /* block comment [block:"Scroller2d_block_136" text:"Runs the set scale y action. Values in this block are editable inputs."] */
            /* block start [id:"Scroller2d_block_136" category:"Variables" label:"set scale y" shape:"command" docs:"Runs the set scale y action. Values in this block are editable inputs."] */
            minimapBorder.setScaleY(displayScale);
            /* block end [id:"Scroller2d_block_136"] */
        }
        /* block end [id:"Scroller2d_block_39"] */
    }
}
