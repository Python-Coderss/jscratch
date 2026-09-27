package generated;

import com.jscratch.*;

/**
* One of the 3x3 world background cells. Positioned in absolute screen px
* by Scroller2d (superGoTo bypasses world scrolling); costume switched per
* rebuild to the tile id at that world cell. Shares costumes with the
* minimap via ResourcePool, so FloorManager tints hit both.
*/
public class BackgroundTile extends Sprite {
    public BackgroundTile(String name) {
        /* block comment [block:"BackgroundTile_block_1" text:"Runs the super action. Values in this block are editable inputs."] */
        /* block start [id:"BackgroundTile_block_1" category:"My Blocks" label:"super" shape:"command" docs:"Runs the super action. Values in this block are editable inputs."] */
        super(name);
        /* block end [id:"BackgroundTile_block_1"] */
    }

    // Background tiles are positioned absolutely on screen
    public void superGoTo(int x, int y) {
        /* block comment [block:"BackgroundTile_block_2" text:"Runs the go to action. Values in this block are editable inputs."] */
        /* block start [id:"BackgroundTile_block_2" category:"Motion" label:"go to" shape:"command" docs:"Runs the go to action. Values in this block are editable inputs."] */
        super.goTo(x, y);
        /* block end [id:"BackgroundTile_block_2"] */
    }
}
