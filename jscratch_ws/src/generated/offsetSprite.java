package generated;

import com.jscratch.*;
import java.awt.Color;
import java.awt.event.KeyEvent;

public class offsetSprite extends Sprite {
    Scroller2d instance; 
    /* slot start [id:"offsetSprite_members" tags:"inClassNotMethod"] */
    public offsetSprite(String name) {
        super(name);
        instance = Scroller2d.getInstance();
        // Example: addCostume(new Costume("costume1", "path/to/img.png"));

        /* slot start [id:"offsetSprite_init" tags:"inInstanceMethod"] */
        /* slot end [id:"offsetSprite_init"] */
    }
    public void goTo(int x, int y) {
        super.goTo(instance.x + x, instance.y + y); 
    }
    /* slot end [id:"offsetSprite_members"] */
}