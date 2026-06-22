package generated;

import com.jscratch.*;

/**
 * Specialized Orc enemy using imported assets.
 */
public class Orc extends EnemySprite {
    public Orc(String name) {
        super(name);
        this.localScale = 0.7; // Orcs are large
        this.speed = 3;
        this.health = 120;
        this.maxHealth = 120;
        
        // Imported Orc costumes (Pooled)
        addCostume(ResourcePool.getCostume("Orc_idle", "jscratch_ws/assets/Orc_costume_idle.png", 32, 32));
        addCostume(ResourcePool.getCostume("Orc_walk1", "jscratch_ws/assets/Orc_costume1.png", 32, 32));
        addCostume(ResourcePool.getCostume("Orc_walk2", "jscratch_ws/assets/Orc_costume2.png", 32, 32));
        
        this.switchCostume("Orc_idle");
    }

    @Override
    public void performAttack(int playerX, int playerY) {
        // Orc 'Rush' Lunge
        if (System.currentTimeMillis() % 1500 < 200) {
            moveTowards(playerX, playerY, (int)(speed * 4));
        }
    }
    
    @Override
    public void update() {
        super.update();
        // Simple animation logic
        if (currentState == AIState.PURSUE || currentState == AIState.SWARM) {
            if (System.currentTimeMillis() % 400 < 200) {
                switchCostume("Orc_walk1");
            } else {
                switchCostume("Orc_walk2");
            }
        } else {
            switchCostume("Orc_idle");
        }
    }
}
