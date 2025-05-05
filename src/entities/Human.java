package entities;

import game.Game; 
import game.GameSettings; 
import java.awt.*;
import java.util.List; 

public class Human extends Entity {
    private final int cost;
    private final int baseMaxHealth; 

    public Human(int x, int y, int maxHealth, int damage, int range, int speed, int cost, long lifespanMillis) {
        super(x, y, EntityTypeData.HUMAN_DRAW_WIDTH, EntityTypeData.HUMAN_DRAW_HEIGHT, maxHealth, damage, range, speed, lifespanMillis); 
        this.cost = cost;
        this.baseMaxHealth = maxHealth; 
    }

    @Override
    protected void move() {
    }

    @Override
    protected boolean canAttack(List<Entity> potentialTargets) { 
        if (!isActive || attackStrategy == null || attackSpeed <= 0) return false;
        for (Entity target : potentialTargets) {
            if (target instanceof Titan && target.isActive()) {
                int distanceX = target.getX() - (this.x + this.drawWidth);
                boolean sameLane = Math.abs(this.getCenterY() - target.getCenterY()) <= GameSettings.ENTITY_INTERACTION_Y_TOLERANCE;
                if (distanceX >= 0 && distanceX <= this.attackRange && sameLane) {
                    return true;
                }
            }
        }
        return false; 
    }

 
    @Override
    protected void attack() {
        if (attackStrategy != null) {
            attackStrategy.executeAttack(this);
              System.out.println("Human attacking!"); 
        } else {
            System.err.println("Warning: Human attack called with no strategy!");
        }
    }

    @Override
    protected Color getColor() {
        return new Color(50, 50, 150); 
    }

    public int getCost() { return cost; }
    public int getBaseMaxHealth() { return baseMaxHealth; } 
    public int getBaseCost() { return cost; } 
}
