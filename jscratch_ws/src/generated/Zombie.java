package generated;

import com.jscratch.*;

/**
 * Basic Zombie enemy using imported assets.
 */
public class Zombie extends EnemySprite {
    public Zombie(String name) {
        super(name);
        this.localScale = 1; // Zombies are medium-large
        this.speed = 1; // Zombies are slow
        this.health = 80;
        this.attackRange = 30;
        
        addCostume(ResourcePool.getCostume("Zombie_idle", "jscratch_ws/assets/Zombie_costume_idle.png", 32, 32));
        addCostume(ResourcePool.getCostume("Zombie_walk1", "jscratch_ws/assets/Zombie_costume1.png", 32, 32));
        addCostume(ResourcePool.getCostume("Zombie_walk2", "jscratch_ws/assets/Zombie_costume2.png", 32, 32));
        
        this.switchCostume("Zombie_idle");
    }

    @Override
    public void update() {
        super.update();
        if (currentState == AIState.PURSUE) {
            if (System.currentTimeMillis() % 600 < 300) {
                switchCostume("Zombie_walk1");
            } else {
                switchCostume("Zombie_walk2");
            }
        } else {
            switchCostume("Zombie_idle");
        }
    }
}
