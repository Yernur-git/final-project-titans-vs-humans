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
    private final int startX; 
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
        this.color = color;
        this.isActive = true;
    }


    @Override
    public void update() {
        if (!isActive) return;
        x += speed;

        if (Math.abs(x - startX) > range) {
            setInactive(); 
            return; 
        }
        if (x < -width || x > Game.getInstance().getGamePanelWidth()) {
            setInactive();
        }
    }

    public Entity getOwner() {
        return owner;
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
