package generated;

import com.jscratch.*;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.*;

public class BSprite extends Sprite {
    /* slot start [id:"BSprite_members" tags:"inClassNotMethod"] */


    public int xx = 0;
    public int yy = 0;
    public int scale = 1;

    public BSprite(String name) {
        super(name);
        
        // Example: addCostume(new Costume("costume1", "path/to/img.png"));
        this.whenScaleChanged((old, newscale) -> {
            double scfa = newscale / (old+0.0);
            scale = newscale.intValue();
            this.setScaleX(this.getScaleX() * scfa);
            this.setScaleY(this.getScaleY() * scfa);
            update();
        });
        
        Scroller2d.getInstance().register(this);
        /* slot start [id:"BSprite_init" tags:"inInstanceMethod"] */
        /* slot end [id:"BSprite_init"] */
        
    }

    
    public void update() {
        Scroller2d scroller = Scroller2d.getInstance();
        super.goTo((xx + scroller.x) * scale, (yy + scroller.y) * scale);
        
        // Culling: Hide if too far off stage (assuming 480x360 base coords)
//        if (isOnStage()) {
//            show();
//        } else {
//            hide();
//        }
    }
	public void goTo(int x, int y) {
		xx = x;
		yy = y;
		update();
	}

	public void changeY(int y) {
		yy += y;
		update();
	}

	public void changeX(int x) {
		xx += x;
		update();
	}
	public void walk(int steps) {
		super.move(steps * scale);
	}

    public void goToFront() {
        Stage.getInstance().goToFront(this);
    }

    public void goToBack() {
        Stage.getInstance().goToBack(this);
    }

    public boolean isDirEqualTo(int dir) {
    	double olddir = (Double) getVariable("direction");
    	int dir2 = (int) olddir;
    	
    	return dir2 == dir;
    }
    public void animateTurn(int dir, double time) {
    	
    	double olddir = (Double) getVariable("direction");
    	double change = (dir - olddir + 540) % 360 - 180;
    	
    	if (change > 0) {
    		double timm = time / change;
    		int tim = (int) (timm * 1000);
    		for (int i = 0; i < change; i++) {
    			turnRight(1);
    			try {
    				Thread.sleep(tim);
    			} catch (InterruptedException e) {
    				
    			}
    		}
    	} if (change == 0) {
		return;
    	} else {
    		double p = -change;
    		double timm = time / p;
    		int tim = (int) (timm * 1000);
    		for (int i = 0; i < p; i++) {
    			turnLeft(1);
    			try {
    				Thread.sleep(tim);
    			} catch (InterruptedException e) {
    				
    			}
    		}
    	}
    }

	public void animateTurn(int dir, double time, String curveType) {
 	   double olddir = (Double) getVariable("direction");
 	   double change = (dir - olddir + 540) % 360 - 180;
	
	    if (change == 0) return;
		
	    int steps = (int) Math.abs(change);
	    boolean right = change > 0;

  	  for (int i = 0; i < steps; i++) {
     	   double t1 = (double) i / steps;
     	   double t2 = (double) (i + 1) / steps;

  	        double c1 = applyCurve(t1, curveType);
             double c2 = applyCurve(t2, curveType);
             
             double delta = (c2 - c1) * time;
		   
   	        if (right) {
                 turnRight(1);
             } else {
                 turnLeft(1);
             }
             
             try {
                 Thread.sleep((int)(delta * 1000));
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
         }
	}


	private double applyCurve(double t, String type) {
  	  switch (type) {
  	      case "easeIn": return t * t;
 	       case "easeOut": return 1 - Math.pow(1 - t, 2);
  	      case "easeInOut":
   	         return t < 0.5 
   	             ? 2 * t * t 
    	            : 1 - Math.pow(-2 * t + 2, 2) / 2;
    	    default: return t; // linear
  	  }
}
    
    public boolean isOnStage() {
	    double halfW = width() / 2.0;
 	   halfW /= scale + 0.0;
 	   double halfH = height() / 2.0;
 	   halfH /= scale + 0.0;
	   
	    double left   = xx - halfW;
	    double right  = xx + halfW;
	    double bottom = yy - halfH;
	    double top    = yy + halfH;
	    
  	    return right >= -240 &&
       	      left <= 240 &&
     	      top >= -180 &&
                bottom <= 180;
}

 @SuppressWarnings("unchecked")
    public void resetCostumes() {
        try {
            // Access and clear the costumes list
            Field costumesField = Sprite.class.getDeclaredField("costumes");
            costumesField.setAccessible(true);
            List<?> costumes = (List<?>) costumesField.get(this);
            costumes.clear();

            // Reset costumeIndex to 0
            Field indexField = Sprite.class.getDeclaredField("costumeIndex");
            indexField.setAccessible(true);
            indexField.setInt(this, 0);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to reset costumes via reflection", e);
        }
    }
    /* slot end [id:"BSprite_members"] */
}