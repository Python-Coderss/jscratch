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
    public double localScale = 1.0; // Changed to double for precise sprite-specific scaling
    public MinimapSprite minimapSprite;

    public BSprite(String name) {
        super(name);
        
        minimapSprite = new MinimapSprite(name + "_mini");
        minimapSprite.setScaleX(0.2);
        minimapSprite.setScaleY(0.2);
        Stage.getInstance().addSprite(minimapSprite);

        this.whenScaleChanged((old, newscale) -> {
            // This is called when Stage scale changes
            update();
        });
        
        Scroller2d.getInstance().register(this, minimapSprite);
    }

    public void update() {
        Stage stage = Stage.getInstance();
        double displayScale = stage.getScaleFactor();
        Scroller2d scroller = Scroller2d.getInstance();

        // Screen position = (World position + Scroll offset) * Sprite scale * Stage scale
        double screenX = (xx + scroller.x) * localScale * displayScale;
        double screenY = (yy + scroller.y) * localScale * displayScale;
        
        super.goTo(screenX, screenY);
        super.setScaleX(localScale * displayScale);
        super.setScaleY(localScale * displayScale);
        
        if (minimapSprite != null) {
            double dir = (Double) getVariable("direction");
            minimapSprite.update(xx, yy, dir, -scroller.x, -scroller.y);
        }
    }

	public void goTo(int x, int y) {
        if (!willCollide(x, y)) {
		    xx = x;
		    yy = y;
        }
		update();
	}

	public void changeY(int y) {
        if (!willCollide(xx, yy + y)) {
		    yy += y;
        }
		update();
	}

	public void changeX(int x) {
        if (!willCollide(xx + x, yy)) {
		    xx += x;
        }
		update();
	}

	public void walk(int steps) {
		// Moving in world units
		double rad = Math.toRadians(90 - (Double)getVariable("direction"));
        int nextX = xx + (int)(steps * Math.cos(rad));
        int nextY = yy + (int)(steps * Math.sin(rad));
        if (!willCollide(nextX, nextY)) {
            xx = nextX;
            yy = nextY;
        }
        update();
	}

    public boolean willCollide(int nextX, int nextY) {
        return Scroller2d.getInstance().isColliding(nextX, nextY, 20, 20);
    }

    public void goToFront() {
        Stage.getInstance().goToFront(this);
        if (minimapSprite != null) {
            Stage.getInstance().goToFront(minimapSprite);
        }
    }

    public void goToBack() {
        Stage.getInstance().goToBack(this);
    }

    public boolean isDirEqualTo(int dir) {
    	double olddir = (Double) getVariable("direction");
    	int dir2 = (int) olddir;
    	return dir2 == dir;
    }

    public void animateTurn(int dir, int time) {
    	double olddir = (Double) getVariable("direction");
    	double change = (dir - olddir + 540) % 360 - 180;
        if (change == 0) return;
        if (time == 0) {
            pointInDirection(dir);
            update();
            return;
        }
        double turnamt = change / time;
        for (int i = 0; i < time; i++) {
            turnRight(turnamt);
            update();
            try { Thread.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
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
   	        if (right) { turnRight(1); } else { turnLeft(1); }
            update();
            try { Thread.sleep((int)(delta * 1000)); } catch (InterruptedException e) { e.printStackTrace(); }
        }
	}

	private double applyCurve(double t, String type) {
  	  switch (type) {
  	      case "easeIn": return t * t;
 	      case "easeOut": return 1 - Math.pow(1 - t, 2);
  	      case "easeInOut":
   	         return t < 0.5 ? 2 * t * t : 1 - Math.pow(-2 * t + 2, 2) / 2;
    	  default: return t; 
  	  }
    }
    
    public boolean isOnStage() {
        Stage stage = Stage.getInstance();
        double displayScale = stage.getScaleFactor();
	    double halfW = (width() / displayScale) / 2.0;
 	    double halfH = (height() / displayScale) / 2.0;
	    double left   = xx - halfW;
	    double right  = xx + halfW;
	    double bottom = yy - halfH;
	    double top    = yy + halfH;
  	    return right >= -240 && left <= 240 && top >= -180 && bottom <= 180;
    }

    @SuppressWarnings("unchecked")
    public void resetCostumes() {
        try {
            Field costumesField = Sprite.class.getDeclaredField("costumes");
            costumesField.setAccessible(true);
            List<?> costumes = (List<?>) costumesField.get(this);
            costumes.clear();
            Field indexField = Sprite.class.getDeclaredField("costumeIndex");
            indexField.setAccessible(true);
            indexField.setInt(this, 0);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to reset costumes via reflection", e);
        }
    }
    /* slot end [id:"BSprite_members"] */
}
