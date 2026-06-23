package generated;

import com.jscratch.*;
import java.awt.Color;
import java.awt.event.KeyEvent;

public class HPBAR extends offsetSprite {
    /* slot start [id:"HPBAR_members" tags:"inClassNotMethod"] */
    public BarFilled filled;
    public BarEmpty empty; 
    public HPBAR() {
        super("HPBAR");
        
        // Example: addCostume(new Costume("costume1", "path/to/img.png"));

        /* slot start [id:"HPBAR_init" tags:"inInstanceMethod"] */


addCostume(new Costume(
		
			"heart",
			"jscratch_ws/assets/"+
			"heart.png",
			3, //change this to be half your sprite width, this is for rotation
			3 //change this to be half your sprite height, this is for rotation
		));



    setSize(400);



        empty = new BarEmpty();
        filled = new BarFilled();
        Stage stage = Stage.getInstance(); 
        stage.addSprite(empty);
        stage.addSprite(filled);
        /* slot end [id:"HPBAR_init"] */
    }

    
    public void update(int hp, int maxHP, int newX, int newY) {
        filled.update(maxHP, hp, newX, newY);
        empty.update(maxHP, hp, newX, newY);
        goTo(newX, newY + 50);
        
        Stage.getInstance().goToFront(this);
        Stage.getInstance().goToFront(empty);
        Stage.getInstance().goToFront(filled);
        
    }
    public static class BarFilled extends offsetSprite {
        public BarFilled() {
            super("BarFilled");
            addCostume(new Costume(
        
            "costume",
            "jscratch_ws/assets/"+
            "hp color.png",
            0, //change this to be half your sprite width, this is for rotation
            0 //change this to be half your sprite height, this is for rotation
        ));
            
        }
        public void update(float max, int val, int nx, int ny) {
            setScaleX((val / max) * 100);
            setScaleY(25);
            goTo(nx, ny); 
        }
        
    }


    
    public static class BarEmpty extends offsetSprite { 
        public BarEmpty() {
            super("BarEmpty");
            addCostume(new Costume(
        
            "costume",
            "jscratch_ws/assets/"+
            "hp black.png",
            0, //change this to be half your sprite width, this is for rotation
            0 //change this to be half your sprite height, this is for rotation
        ));
            
        }
        public void update(float max, int val, int nx, int ny) {
            int vari = (int) ((val / max) * 100); 
            int varib = (int) (100 - ((val / max) * 100)); 
            
            setScaleX(varib);
            setScaleY(25);
            goTo(nx + vari, ny); 
        }
        
    }

    

    
    /* slot end [id:"HPBAR_members"] */
}