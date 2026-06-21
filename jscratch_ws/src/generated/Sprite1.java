package generated;

import com.jscratch.*;
import java.awt.Color;
import java.awt.event.KeyEvent;

/*
 * 
 * 
 * 
 * THE PLAYER
 * 
 * 
 * 
 */
public class Sprite1 extends BSprite {
	public int mv = 5;
	public double turnSpeed = 0.25; //in seconds, time it takes to turn (smaller = faster)
	/* slot start [id:"2_members" tags:"inClassNotMethod"] */
	public Sprite1() {
		super("Sprite1");
		
		// Example: addCostume(new Costume("costume1", "path/to/img.png"));
		
		/* slot start [id:"2_init" tags:"inInstanceMethod"] */
		/*
		 * 
		 * 
		 * 
		 * Add costumes (not in scratch by the way)
		 * 
		 * 
		 */
		
		
		addCostume(new Costume(
				
					"costume1",
					"jscratch_ws/assets/"+
					"Player_costume1.png",
					28, //change this to be half your sprite width, this is for rotation
					33 //change this to be half your sprite height, this is for rotation
				));
		
		addCostume(new Costume(
				
					"costume2",
					"jscratch_ws/assets/"+
					"Player_costume2.png",
					29, //change this to be half your sprite width, this is for rotation
					33 //change this to be half your sprite height, this is for rotation
				));
		/*
		 * 
		 * Go to center of screen
		 * 
		 */
		
		
		goTo(0,0);
		
		this.startScript(() -> {
            
            /* 
             *  
             *  the game loop
             *  
             */
            int ws = 125; //walk speed (animation not movement speed) smaller = faster
            while (true) {
                if (  
                    Stage.getInstance().isKeyPressed("w")
                    ) {
                    /* slot start [id:"3_key_body" tags:"inInstanceMethod"] */
                    
                    this.startScript(() -> this.animateTurn(0, turnSpeed, "easeIn"));
                
                    movec(mv, 0);
                    nextCostume();
                    this.goToFront();
                    try {
                        Thread.sleep(ws);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    /* slot end [id:"3_key_body"] */
                }
        
                if (  
                    Stage.getInstance().isKeyPressed("a")
                    ) {
                    /* slot start [id:"3_key_body" tags:"inInstanceMethod"] */
                    this.startScript(() -> this.animateTurn(270, turnSpeed, "easeIn"));
                    movec(mv, 3);
                    nextCostume();
                    this.goToFront();
                    try {
                        Thread.sleep(ws);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    /* slot end [id:"3_key_body"] */
                }
                if (  
                    Stage.getInstance().isKeyPressed("s")
                    ) {
                    /* slot start [id:"3_key_body" tags:"inInstanceMethod"] */
                    this.startScript(() -> this.animateTurn(180, turnSpeed, "easeIn"));
                    movec(mv, 2);
                    nextCostume();
                    this.goToFront();
                    try {
                        Thread.sleep(ws);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    /* slot end [id:"3_key_body"] */
                }
                if (  
                    Stage.getInstance().isKeyPressed("d")
                    ) {
                    /* slot start [id:"3_key_body" tags:"inInstanceMethod"] */
                    this.startScript(() -> this.animateTurn(90, turnSpeed, "easeIn"));
                    movec(mv, 1);
                    nextCostume();
                    this.goToFront();
                    try {
                    	Thread.sleep(ws);
                    } catch (InterruptedException e) {
                    	e.printStackTrace();
                    }
    
    
                    
                    /* slot end [id:"3_key_body"] */
                }
            waitTillNextFrame();
            }
        });
		



		
		
		
		
		
		
		
		
		/* slot end [id:"2_init"] */
	}

	public void movec(int amt, int dir) {
		switch (dir) {
			case 0:
				yy += amt;
				break;
			case 1:
				xx += amt;
				break;
			case 2:
				yy -= amt;
				break;
			case 3:
				xx -= amt;
				break;
			default:
		}
		
		Scroller2d s = Scroller2d.getInstance();
		s.x = -xx;
		s.y = -yy;
		s.update();
		super.update();
	}
	/* slot end [id:"2_members"] */
}
