package generated;

import com.jscratch.*;

public class BackgroundTile extends Sprite {
    public BackgroundTile(String name) {
        super(name);
    }
    
    // Background tiles are positioned absolutely on screen
    public void superGoTo(int x, int y) {
        super.goTo(x, y);
    }
}
