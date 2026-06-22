package generated;

import com.jscratch.*;

/**
 * Basic Goblin enemy using imported assets.
 */
public class Goblin extends EnemySprite {
    public Goblin(String name) {
        super(name);
        this.localScale = 0.5; // Goblins are small
        this.speed = 4; // Goblins are fast
        this.health = 60;
        this.attackRange = 35;
        
        addCostume(ResourcePool.getCostume("Goblin_idle", "jscratch_ws/assets/Goblin_costume_Idle.png", 32, 32));
        addCostume(ResourcePool.getCostume("Goblin_walk1", "jscratch_ws/assets/Goblin_costume1.png", 32, 32));
        addCostume(ResourcePool.getCostume("Goblin_walk2", "jscratch_ws/assets/Goblin_costume2.png", 32, 32));
        addCostume(ResourcePool.getCostume("Goblin_attack", "jscratch_ws/assets/Goblin_costume_attack.png", 32, 32));
        
        this.switchCostume("Goblin_idle");
    }

    @Override
    public void performAttack(int playerX, int playerY) {
        switchCostume("Goblin_attack");
        super.performAttack(playerX, playerY);
    }

    @Override
    public void update() {
        super.update();
        if (currentState == AIState.PURSUE || currentState == AIState.SWARM) {
            if (System.currentTimeMillis() % 300 < 150) {
                switchCostume("Goblin_walk1");
            } else {
                switchCostume("Goblin_walk2");
            }
        } else if (currentState != AIState.ATTACK) {
            switchCostume("Goblin_idle");
        }
    }
}
