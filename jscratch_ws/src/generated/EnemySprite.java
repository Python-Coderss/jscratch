package generated;

import com.jscratch.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Advanced Enemy AI with multiple states.
 */
public class EnemySprite extends BSprite {
    public enum AIState { PURSUE, RETREAT, ASSIST, ATTACK, SWARM }
    
    protected AIState currentState = AIState.PURSUE;
    protected int health = 100;
    protected int maxHealth = 100;
    protected int speed = 2;
    protected int attackRange = 40;
    protected int visionRange = 500;
    double distToPlayer = 2000;

    // For Swarm/Positioning
    protected double swarmAngle = 0;

    public EnemySprite(String name) {
        super(name);
        if (minimapSprite != null) {
            minimapSprite.addCostume(new Costume("enemy_dot", "jscratch_ws/assets/npc_dot.png", 5, 5));
            minimapSprite.switchCostume("enemy_dot");
        }
        // Random swarm angle offset so they don't all stack
        this.swarmAngle = Math.random() * Math.PI * 2;
    }
    public void selectState() {

        if (health < 30) {
            currentState = AIState.RETREAT;
        } else if (distToPlayer < attackRange) {
            currentState = AIState.ATTACK;
        } else if (distToPlayer < 150) {
            currentState = AIState.SWARM;
        } else {
            // Check if we should assist others
            EnemySprite allyInNeed = findAllyInNeed();
            if (allyInNeed != null) {
                currentState = AIState.ASSIST;
            } else {
                currentState = AIState.PURSUE;
            }
        }

    }
    public void think(int playerX, int playerY) {
        if (!isVisible()) return;

        distToPlayer = getDistance(xx, yy, playerX, playerY);

        // 1. State Selection Logic
        selectState();
        // 2. State Execution
        switch (currentState) {
            case PURSUE:
                moveTowards(playerX, playerY, speed);
                break;
            case RETREAT:
                moveTowards(playerX, playerY, -speed); // Move away
                break;
            case ATTACK:
                performAttack(playerX, playerY);
                break;
            case SWARM:
                executeSwarm(playerX, playerY);
                break;
            case ASSIST:
                executeAssist();
                break;
        }
        
        update();
    }

    protected void moveTowards(int targetX, int targetY, int moveSpeed) {
        double dx = targetX - xx;
        double dy = targetY - yy;
        double angle = Math.atan2(dy, dx);
        
        pointInDirection(90 - Math.toDegrees(angle));
        
        int stepX = (int)(Math.cos(angle) * moveSpeed);
        int stepY = (int)(Math.sin(angle) * moveSpeed);
        
        if (!willCollide(xx + stepX, yy + stepY)) {
            xx += stepX;
            yy += stepY;
        }
    }

    protected void executeSwarm(int playerX, int playerY) {
        // Circle around the player at a distance
        int desiredDist = 100;
        swarmAngle += 0.02; // Rotate slowly
        
        int targetX = playerX + (int)(Math.cos(swarmAngle) * desiredDist);
        int targetY = playerY + (int)(Math.sin(swarmAngle) * desiredDist);
        
        moveTowards(targetX, targetY, speed);
    }

    protected void executeAssist() {
        EnemySprite ally = findAllyInNeed();
        if (ally != null) {
            moveTowards(ally.xx, ally.yy, speed);
        }
    }

    protected void performAttack(int playerX, int playerY) {
        // Simple attack cooldown/logic
        if (System.currentTimeMillis() % 1000 < 100) {
            // Visualize attack (e.g., small lunge)
            moveTowards(playerX, playerY, speed * 2);
            // In a real game, you'd check for damage here
        }
    }

    protected EnemySprite findAllyInNeed() {
        // Look through registered sprites in Scroller2d
        for (BSprite s : Scroller2d.getInstance().getRegisteredSprites()) {
            if (s instanceof EnemySprite && s != this && s.isVisible()) {
                EnemySprite ally = (EnemySprite) s;
                // Assist if ally is at low health or attacking
                if (ally.health < 50 || ally.currentState == AIState.ATTACK) {
                    return ally;
                }
            }
        }
        return null;
    }

    protected double getDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public void takeDamage(int amount) {
        health -= amount;
        if (health <= 0) die();
    }

    public void die() {
        EnemyManager.enemyCount--;
        hide();
        if (minimapSprite != null) minimapSprite.hide();
    }
}
