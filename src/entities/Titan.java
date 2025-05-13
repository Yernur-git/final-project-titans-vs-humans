package entities;

import game.Game;
import game.GameSettings;

import java.awt.*;
import java.util.List;

public class Titan extends Entity {
    private int movementSpeed;
    private static final int MAX_Y_DIFFERENCE_FOR_INTERACTION = GameSettings.ENTITY_INTERACTION_Y_TOLERANCE;
    private static final int BLOCKING_DISTANCE = 5;

    public Titan(int x, int y, int maxHealth, int attackDamage, int attackRange, int attackSpeed, int movementSpeed) {
        super(x, y, EntityTypeData.TITAN_DRAW_WIDTH, EntityTypeData.TITAN_DRAW_HEIGHT, maxHealth, attackDamage, attackRange, attackSpeed);
        this.movementSpeed = Math.max(1, movementSpeed);
    }

    @Override
    protected void move() {
        if (isBlocked()) {
            return;
        }
        x -= movementSpeed;
    }

    private boolean isBlocked() {
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
        boolean inBlockingRangeX = distanceX <= BLOCKING_DISTANCE && distanceX > -target.getWidth();
        boolean alignedY = Math.abs(this.getCenterY() - target.getCenterY()) <= MAX_Y_DIFFERENCE_FOR_INTERACTION;

        return inBlockingRangeX && alignedY;
    }

    @Override
    protected boolean canAttack() {
        List<Entity> entities = Game.getInstance().getCurrentLevel().getEntities();
        for (Entity entity : entities) {
            if (entity.isActive() && (entity instanceof Human || entity instanceof BlockerWall)) {
                if (isTargetInAttackZone(entity)) {
                    return true;
                }
            }
        }
        return false;
    }


    private boolean isTargetInAttackZone(Entity target) {
        if (target == null || !target.isActive()) return false;
        int distanceX = this.x - (target.getX() + target.getWidth());
        boolean inRangeX = distanceX >= -5 && distanceX <= this.attackRange;

        boolean alignedY = Math.abs(this.getCenterY() - target.getCenterY()) <= GameSettings.ENTITY_INTERACTION_Y_TOLERANCE;
        return inRangeX && alignedY;
    }

    @Override
    protected void attack() {
        List<Entity> entities = Game.getInstance().getCurrentLevel().getEntities();
        for (Entity entity : entities) {
            if (entity.isActive() && (entity instanceof Human || entity instanceof BlockerWall)) {
                if (isTargetInAttackZone(entity)) {
                    entity.takeDamage(this.attackDamage);
                    return;
                }
            }
        }
    }

    @Override
    protected Color getColor() {
        return new Color(150, 50, 50);
    }

    @Override
    public void update() {
        if (!isActive) return;

        super.update();

        if (this.x + this.width < 0) {
            HumanBase base = Game.getInstance().getHumanBase();
            if (base != null && base.isActive()) {
                base.takeDamage(this.attackDamage > 0 ? this.attackDamage * 2 : 50);
                Game.getInstance().notifyObservers("Titan breached! Base damaged!");
            }
            this.die();
        }
    }

    public int getCenterY() {
        return y + height / 2;
    }

    public void setMovementSpeed(int speed) {
        this.movementSpeed = Math.max(1, speed);
    }

    public int getMovementSpeed() {
        return movementSpeed;
    }
}
