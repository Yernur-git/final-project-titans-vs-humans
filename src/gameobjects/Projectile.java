package gameobjects;

import entities.Entity;
import java.awt.*;

public class Projectile {
    protected int x, y;
    protected int width, height;
    protected int speed;
    protected int damage;
    protected boolean isActive;
    protected Color color;

    public Projectile(int x, int y, int width, int height, int speed, int damage, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
        this.damage = damage;
        this.color = color;
        this.isActive = true;
    }

    public void update() {
        if (!isActive) return;
        x += speed;
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
}
