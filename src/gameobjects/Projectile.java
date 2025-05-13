package gameobjects;

import entities.Entity;

import java.awt.*;

public class Projectile {
    private int x;
    private final int y;
    private final int startX;
    private final int width;
    private final int height;
    private final int speed;
    private final int damage;
    private boolean isActive;
    private final Color color;
    private final int range;
    private final Entity owner;

    public Projectile(Entity owner, int startX, int startY, int width, int height, int speed, int damage, int range, Color color) {
        this.owner = owner;
        this.x = startX;
        this.y = startY;
        this.startX = startX;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.damage = damage;
        this.range = range;
        this.isActive = true;
        this.color = color;
    }

    public void update() {
        if (!isActive) return;

        x += speed;

        if (Math.abs(x - startX) > range) {
            setInactive();
        }
    }

    public void draw(Graphics g) {
        if (!isActive) return;
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }

    public boolean isActive() {
        return isActive;
    }

    public void setInactive() {
        this.isActive = false;
    }

    public int getDamage() {
        return damage;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public Entity getOwner() {
        return owner;
    }
}
