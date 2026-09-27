package generated;

import com.jscratch.*;

/**
* A floor sprite that stays fixed in world coordinates.
* Useful for adding specific floor types (lava, water, stone) in particular world locations.
*
* <p>Call graph: ctor self-registers into {@code Scroller2d.addFloor};
* repositioned from {@code Scroller2d.update} (world + scroll, scaled) and
* on stage-scale changes. Same pos/scale math as BSprite but without
* collision or a minimap dot.
*/
public class FloorSprite extends Sprite {
    public int xx, yy;
    public double localScale = 1.0;

    public FloorSprite(String name, int x, int y) {
        /* block comment [block:"FloorSprite_block_1" text:"Runs the super action. Values in this block are editable inputs."] */
        /* block start [id:"FloorSprite_block_1" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
        super(name);
        /* block end [id:"FloorSprite_block_1"] */
        /* block comment [block:"FloorSprite_block_2" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"FloorSprite_block_2" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.xx = x;
        /* block end [id:"FloorSprite_block_2"] */
        /* block comment [block:"FloorSprite_block_3" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"FloorSprite_block_3" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        this.yy = y;
        /* block end [id:"FloorSprite_block_3"] */

        // Listen for stage scaling
        this.whenScaleChanged((old, newScale) -> {
            update();
        }/* block comment [block:"FloorSprite_block_4" text:"Evaluates this expression and applies its result."] */
        /* block start [id:"FloorSprite_block_4" category:"Operators" label:"calculate / update value" shape:"command" docs:"Evaluates this expression and applies its result."] */
        );
        /* block end [id:"FloorSprite_block_4"] */

        // Add to scroller's floor list for management
        /* block comment [block:"FloorSprite_block_5" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorSprite_block_5" category:"Variables" label:"set getInstance" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Scroller2d.getInstance().addFloor(this);
        /* block end [id:"FloorSprite_block_5"] */
        /* block comment [block:"FloorSprite_block_6" text:"Calls the update method using its declared arguments."] */
        /* block start [id:"FloorSprite_block_6" category:"My Blocks" label:"call update" shape:"command" docs:"Calls the update method using its declared arguments."] */
        update();
        /* block end [id:"FloorSprite_block_6"] */
    }

    public void update() {
        /* block comment [block:"FloorSprite_block_7" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorSprite_block_7" category:"Variables" label:"set stage" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Stage stage = Stage.getInstance();
        /* block end [id:"FloorSprite_block_7"] */
        /* block comment [block:"FloorSprite_block_8" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorSprite_block_8" category:"Variables" label:"set displayScale" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double displayScale = stage.getScaleFactor();
        /* block end [id:"FloorSprite_block_8"] */
        /* block comment [block:"FloorSprite_block_9" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorSprite_block_9" category:"Variables" label:"set scroller" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        Scroller2d scroller = Scroller2d.getInstance();
        /* block end [id:"FloorSprite_block_9"] */

        // Position = (World position + Scroll offset) * Stage scale.
        // (localScale sizes the sprite only, never the position.)
        /* block comment [block:"FloorSprite_block_10" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorSprite_block_10" category:"Variables" label:"set screenX" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double screenX = (xx + scroller.x) * displayScale;
        /* block end [id:"FloorSprite_block_10"] */
        /* block comment [block:"FloorSprite_block_11" text:"Creates or updates a local value. Use it to keep a value for later operations."] */
        /* block start [id:"FloorSprite_block_11" category:"Variables" label:"set screenY" shape:"command" docs:"Creates or updates a local value. Use it to keep a value for later operations."] */
        double screenY = (yy + scroller.y) * displayScale;
        /* block end [id:"FloorSprite_block_11"] */

        /* block comment [block:"FloorSprite_block_12" text:"Runs the go to action. Values in this block are editable inputs."] */
        /* block start [id:"FloorSprite_block_12" category:"Motion" label:"go to" shape:"command" docs:"Runs the go to action. Values in this block are editable inputs."] */
        super.goTo(screenX, screenY);
        /* block end [id:"FloorSprite_block_12"] */
        /* block comment [block:"FloorSprite_block_13" text:"Runs the set scale x action. Values in this block are editable inputs."] */
        /* block start [id:"FloorSprite_block_13" category:"Variables" label:"set scale x" shape:"command" docs:"Runs the set scale x action. Values in this block are editable inputs."] */
        super.setScaleX(localScale * displayScale);
        /* block end [id:"FloorSprite_block_13"] */
        /* block comment [block:"FloorSprite_block_14" text:"Runs the set scale y action. Values in this block are editable inputs."] */
        /* block start [id:"FloorSprite_block_14" category:"Variables" label:"set scale y" shape:"command" docs:"Runs the set scale y action. Values in this block are editable inputs."] */
        super.setScaleY(localScale * displayScale);
        /* block end [id:"FloorSprite_block_14"] */
    }
}
