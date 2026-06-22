package generated;

import com.jscratch.*;
import java.awt.Color;
import java.awt.event.KeyEvent;

/*
 * 
 * THE PLAYER
 * 
 */
public class Sprite1 extends BSprite {
    public int mv = 15;
    public int turnSpeed = 5; //in milli seconds, time it takes to turn (smaller = faster)
    
    /* slot start [id:"2_members" tags:"inClassNotMethod"] */
    public Sprite1() {
        super("Sprite1");
        
        /* slot start [id:"2_init" tags:"inInstanceMethod"] */
        addCostume(new Costume(
                    "costume1",
                    "jscratch_ws/assets/Player_costume1.png",
                    28, 33
                ));
        
        addCostume(new Costume(
                    "costume2",
                    "jscratch_ws/assets/Player_costume2.png",
                    29, 33
                ));

        // Set minimap costume for player (e.g., a green dot)
        minimapSprite.addCostume(new Costume("player_dot", "jscratch_ws/assets/player_dot.png", 5, 5));
        
        goTo(0,0);
        
        this.startScript(() -> {
            int ws = 100; //walk speed (animation not movement speed) smaller = faster
            while (true) {
                // Update Enemies
                EnemyManager.getInstance().update(xx, yy);

                // Attack Check (Space bar)
                if (Stage.getInstance().isKeyPressed("space")) {
                    performAttack();
                }

                // Single direction movement
                if (Stage.getInstance().isKeyPressed("w") && !Stage.getInstance().isKeyPressed("a") && !Stage.getInstance().isKeyPressed("d")) {
                    this.animateTurn(0, turnSpeed);
                    startScript( () -> {cmovec(mv, 0, ws);});
                    startScript( () -> {nextCostume(); this.goToFront();});
                    try { Thread.sleep(ws); } catch (InterruptedException e) { e.printStackTrace(); }
                }
                if (Stage.getInstance().isKeyPressed("a") && !Stage.getInstance().isKeyPressed("w") && !Stage.getInstance().isKeyPressed("s")) {
                    this.animateTurn(270, turnSpeed);
                    startScript( () -> {cmovec(mv, 3, ws);});
                    startScript( () -> {nextCostume(); this.goToFront();});
                    try { Thread.sleep(ws); } catch (InterruptedException e) { e.printStackTrace(); }
                }
                if (Stage.getInstance().isKeyPressed("s") && !Stage.getInstance().isKeyPressed("a") && !Stage.getInstance().isKeyPressed("d")) {
                    this.animateTurn(180, turnSpeed);
                    startScript( () -> {cmovec(mv, 2, ws);});
                    startScript( () -> {nextCostume(); this.goToFront();});
                    try { Thread.sleep(ws); } catch (InterruptedException e) { e.printStackTrace(); }
                }
                if (Stage.getInstance().isKeyPressed("d") && !Stage.getInstance().isKeyPressed("w") && !Stage.getInstance().isKeyPressed("s")) {
                    this.animateTurn(90, turnSpeed);
                    startScript( () -> {cmovec(mv, 1, ws);});
                    startScript( () -> {nextCostume(); this.goToFront();});
                    try { Thread.sleep(ws); } catch (InterruptedException e) { e.printStackTrace(); }
                }
                
                // Diagonal movement
                if (Stage.getInstance().isKeyPressed("w") && Stage.getInstance().isKeyPressed("a")) {
                    this.animateTurn(315, turnSpeed);
                    startScript( () -> {cmovecDiagonal(mv, -1, 1, ws);});
                    startScript( () -> {nextCostume(); this.goToFront();});
                    try { Thread.sleep(ws); } catch (InterruptedException e) { e.printStackTrace(); }
                }
                if (Stage.getInstance().isKeyPressed("w") && Stage.getInstance().isKeyPressed("d")) {
                    this.animateTurn(45, turnSpeed);
                    startScript( () -> {cmovecDiagonal(mv, 1, 1, ws);});
                    startScript( () -> {nextCostume(); this.goToFront();});
                    try { Thread.sleep(ws); } catch (InterruptedException e) { e.printStackTrace(); }
                }
                if (Stage.getInstance().isKeyPressed("s") && Stage.getInstance().isKeyPressed("a")) {
                    this.animateTurn(225, turnSpeed);
                    startScript( () -> {cmovecDiagonal(mv, -1, -1, ws);});
                    startScript( () -> {nextCostume(); this.goToFront();});
                    try { Thread.sleep(ws); } catch (InterruptedException e) { e.printStackTrace(); }
                }
                if (Stage.getInstance().isKeyPressed("s") && Stage.getInstance().isKeyPressed("d")) {
                    this.animateTurn(135, turnSpeed);
                    startScript( () -> {cmovecDiagonal(mv, 1, -1, ws);});
                    startScript( () -> {nextCostume(); this.goToFront();});
                    try { Thread.sleep(ws); } catch (InterruptedException e) { e.printStackTrace(); }
                }
                
                waitTillNextFrame();
            }
        });
        /* slot end [id:"2_init"] */
    }

    public void movec(int amt, int dir) {
        int nextX = xx;
        int nextY = yy;
        switch (dir) {
            case 0: nextY += amt; break;
            case 1: nextX += amt; break;
            case 2: nextY -= amt; break;
            case 3: nextX -= amt; break;
        }
        
        if (!willCollide(nextX, nextY)) {
            xx = nextX;
            yy = nextY;
            
            Scroller2d s = Scroller2d.getInstance();
            s.x = -xx;
            s.y = -yy;
            s.update();
        }
        super.update();
    }
    
    public void movecDiagonal(int amt, int xDir, int yDir) {
        int nextX = xx + (xDir * amt);
        int nextY = yy + (yDir * amt);
        
        if (!willCollide(nextX, nextY)) {
            xx = nextX;
            yy = nextY;
            
            Scroller2d s = Scroller2d.getInstance();
            s.x = -xx;
            s.y = -yy;
            s.update();
        }
        super.update();
    }

    public void cmovec(int amt, int dir, int tim) {
        int t = amt / tim;
        if (t > 0) {
            for (int i = 0; i < tim; i++) {
                movec(t, dir);
                try { Thread.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        } else {
            int b = tim / amt;
            for (int i = 0; i < amt; i++) {
                movec(1, dir);
                try { Thread.sleep(b); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }
    }
    
    public void cmovecDiagonal(int amt, int xDir, int yDir, int tim) {
        // For diagonal movement, we use the standard distance (not sqrt(2) factor to keep consistent speed)
        int t = amt / tim;
        if (t > 0) {
            for (int i = 0; i < tim; i++) {
                movecDiagonal(t, xDir, yDir);
                try { Thread.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        } else {
            int b = tim / amt;
            for (int i = 0; i < amt; i++) {
                movecDiagonal(1, xDir, yDir);
                try { Thread.sleep(b); } catch (InterruptedException e) { e.printStackTrace(); }
            }
        }
    }
    
    public void performAttack() {
        // Simple radial attack check
        int attackDist = 60;
        int damage = 20;

        for (EnemySprite e : EnemyManager.getInstance().getEnemies()) {
            double dx = e.xx - xx;
            double dy = e.yy - yy;
            double dist = Math.sqrt(dx*dx + dy*dy);

            if (dist < attackDist && e.isVisible()) {
                e.takeDamage(damage);
                // Simple knockback
                e.xx += (dx / dist) * 20;
                e.yy += (dy / dist) * 20;
            }
        }
    }

    /* slot end [id:"2_members"] */
}