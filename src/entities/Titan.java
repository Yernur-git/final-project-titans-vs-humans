package entities;

import java.awt.*;

public class Titan extends Entity {
    private static final int DEFAULT_W = 50;
    private static final int DEFAULT_H = 80;
    protected int movementSpeed;

    public Titan(int x, int y, int maxHealth, int damage, int range, int speed, int moveSpeed) {
        super(x, y, DEFAULT_W, DEFAULT_H, maxHealth, damage, range, speed);
        this.movementSpeed = Math.max(1, moveSpeed);
    }

    @Override
    protected void move() {
        if (isActive) {
            x -= movementSpeed;
        }
    }

    @Override
    protected boolean canAttack() {
        return false;
    }

    @Override
    protected void attack() {
    }

    @Override
    protected Color getColor() {
        return Color.RED;
    }
    public int getMovementSpeed() {
        return movementSpeed;
    }
    public void setMovementSpeed(int speed) {
        this.movementSpeed = Math.max(1, speed);
    }
}
