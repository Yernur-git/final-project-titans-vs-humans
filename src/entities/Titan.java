package entities;

import game.Game; 
import game.GameSettings; 
import java.awt.*;
import java.util.List; 

public class Titan extends Entity {
    protected int movementSpeed;
    public Titan(int x, int y, int maxHealth, int damage, int range, int speed, int moveSpeed) {
        super(x, y, EntityTypeData.TITAN_DRAW_WIDTH, EntityTypeData.TITAN_DRAW_HEIGHT, maxHealth, damage, range, speed); 
        this.movementSpeed = Math.max(1, moveSpeed);
    }
    @Override
    protected void move() {
        if (!isActive) return;
        if (isBlocked()) {
              System.out.println("Titan blocked, not moving."); 
            return; 
        }
        x -= movementSpeed;
    }

    private boolean isBlocked() {
        HumanBase base = Game.getInstance().getHumanBase();
        if (base != null && base.isActive() && isEntityBlocking(base)) {
            return true;
        }
        List<Entity> entities = Game.getInstance().getCurrentLevel().getEntities();
        for (Entity entity : entities) {
            if (entity.isActive() && (entity instanceof Human || entity instanceof BlockerWall)) {
                if (isEntityBlocking(entity)) {
                    return true;
                }
            }
        }
        return false; 
    }

     private boolean isEntityBlocking(Entity target) {
         if (target == null || !target.isActive()) return false;
         int distanceX = this.x - (target.getX() + target.getWidth());
         boolean inBlockingRangeX = distanceX <= GameSettings.TITAN_BLOCKING_DISTANCE && distanceX > -target.getWidth(); 
         boolean alignedY = Math.abs(this.getCenterY() - target.getCenterY()) <= GameSettings.ENTITY_INTERACTION_Y_TOLERANCE;
         return inBlockingRangeX && alignedY;
     }

        @Override
    protected boolean canAttack(List<Entity> potentialTargets) {
         if (!isActive || attackSpeed <= 0) return false;
         HumanBase base = Game.getInstance().getHumanBase(); 
         if (base != null && base.isActive() && isTargetInAttackZone(base)) {
             return true;
         }
        for (Entity target : potentialTargets) {
             if (target.isActive() && (target instanceof Human || target instanceof BlockerWall)) {
                 if (isTargetInAttackZone(target)) {
                     return true;
                 }
             }
         }
         return false;
    }

        @Override
    protected void attack() {
        if (!isActive) return;
        HumanBase base = Game.getInstance().getHumanBase();
        if (base != null && base.isActive() && isTargetInAttackZone(base)) {
            base.takeDamage(this.attackDamage);
            lastAttackTime = System.currentTimeMillis(); 
            return;
        }
        List<Entity> entities = Game.getInstance().getCurrentLevel().getEntities(); 
        for (Entity target : entities) {
             if (target.isActive() && (target instanceof Human || target instanceof BlockerWall)) {
                if (isTargetInAttackZone(target)) {
                    target.takeDamage(this.attackDamage);
                    lastAttackTime = System.currentTimeMillis(); 
                    return; 
                }
            }
        }
    }

        @Override
    public void update() {
        if (!isActive) return;
        super.update();

        if (isActive && this.x + this.drawWidth < 0) {
            System.out.println("Titan breached! Dealing damage to base.");
            HumanBase base = Game.getInstance().getHumanBase();
            if (base != null && base.isActive()) {
                int breachDamage = this.attackDamage > 0 ? this.attackDamage * 2 : 50; 
                base.takeDamage(breachDamage);
                Game.getInstance().notifyObservers("Titan breached! Wall damaged!");
            }
            this.die();
        }
    }

    private boolean isTargetInAttackZone(Entity target) {
        if (target == null || !target.isActive()) return false;
        int distanceX = this.x - (target.getX() + target.getWidth());
        boolean inRangeX = distanceX >= -5 && distanceX <= this.attackRange;
        boolean alignedY = Math.abs(this.getCenterY() - target.getCenterY()) <= GameSettings.ENTITY_INTERACTION_Y_TOLERANCE;
        return inRangeX && alignedY;
    }
 

    @Override
    protected Color getColor() {
        return new Color(150, 50, 50); 
    }


     public int getMovementSpeed() {
         return movementSpeed; 
     }
     public void setMovementSpeed(int speed) {
         this.movementSpeed = Math.max(1, speed); 
     }
}
