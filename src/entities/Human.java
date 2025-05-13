package entities;

import game.Game;
import game.GameSettings;

import java.awt.*;
import java.util.List;

public class Human extends Entity {
    private final int cost;
    private final int baseMaxHealth;
    private static final int MAX_Y_DIFFERENCE_FOR_INTERACTION = GameSettings.ENTITY_INTERACTION_Y_TOLERANCE;

    public Human(int x, int y, int maxHealth, int damage, int range, int speed, int cost, long lifespanMillis) {
        super(x, y, EntityTypeData.HUMAN_DRAW_WIDTH, EntityTypeData.HUMAN_DRAW_HEIGHT, maxHealth, damage, range, speed, lifespanMillis);
        this.cost = cost;
        this.baseMaxHealth = maxHealth;
    }

    @Override
    public void update() {
        if (lifespanMillis > 0 && System.currentTimeMillis() - spawnTimeMillis > lifespanMillis) {
            die();
            return;
        }
        super.update();
    }

    @Override
    public void move() {
    }

    @Override
    public boolean canAttack() {
        List<Entity> entities = Game.getInstance().getCurrentLevel().getEntities();
        for (Entity entity : entities) {
            if (entity instanceof Titan && entity.isActive()) {
                int distance = entity.getX() - (this.x + this.width);

                boolean sameLane = Math.abs(this.getCenterY() - entity.getCenterY()) <= MAX_Y_DIFFERENCE_FOR_INTERACTION;
                if (distance >= 0 && distance <= this.attackRange && sameLane) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void attack() {
        if (attackStrategy != null) {
            attackStrategy.executeAttack(this);
        }
    }

    @Override
    public Color getColor() {
        return new Color(50, 50, 150);
    }

    public int getCost() {
        return cost;
    }

    public int getBaseMaxHealth() {
        return baseMaxHealth;
    }

    public int getBaseCost() {
        return cost;
    }
}


