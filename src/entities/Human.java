package entities;

import java.awt.*;

public class Human extends Entity {
    private static final int DEFAULT_W = 30;
    private static final int DEFAULT_H = 50;
    private final int cost;

    public Human(int x, int y, int maxHealth, int damage, int range, int speed, int cost) {
        super(x, y, DEFAULT_W, DEFAULT_H, maxHealth, damage, range, speed);
        this.cost = cost;
    }

    @Override
    protected void move() {
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
        return Color.BLUE;
    }

    public int getCost() {
        return cost;
    }
}

