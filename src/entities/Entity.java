package entities;

import gameobjects.Projectile;
import patterns.strategy.AttackStrategy;
import utils.ResourceLoader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public abstract class Entity {
    protected int x, y;
    protected int width, height;
    protected int drawWidth, drawHeight;
    protected int maxHealth;
    protected int health;
    protected int currentMaxHealth;
    protected int attackDamage;
    protected int attackRange;
    protected int attackSpeed;
    public long lastAttackTime;
    protected boolean isActive;
    protected AttackStrategy attackStrategy;
    protected List<Projectile> projectiles = new ArrayList<>();
    public BufferedImage sprite;


    public Entity(int x, int y, int width, int height, int maxHealth,
                  int attackDamage, int attackRange, int attackSpeed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.drawWidth = width;
        this.drawHeight = height;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.currentMaxHealth = maxHealth;
        this.attackDamage = attackDamage;
        this.attackRange = attackRange;
        this.attackSpeed = attackSpeed > 0 ? attackSpeed : 1000;
        this.lastAttackTime = 0;
        this.isActive = true;
    }

    protected abstract void move();
    protected abstract boolean canAttack();
    protected abstract void attack();
    protected abstract Color getColor();

    public void update() {
        if (!isActive) return;

        move();
    }

    public void draw(Graphics g) {
        if (!isActive) return;
        g.setColor(getColor());
        g.fillRect(x, y, drawWidth, drawHeight);

    }

    public void die() {
        this.isActive = false;
    }

    public void takeDamage(int damage) {
        if (!isActive) return;
        // Логика получения урона позже
        this.health -= damage;
        if (this.health <= 0) {
            this.health = 0;
            die();
        }
    }

    public boolean isActive() { return isActive; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public void setPosition(int x, int y) { this.x = x; this.y = y; }


    public void setSprite(String imagePath) {
        System.out.println("Entity: Attempting to set sprite (ResourceLoader not available yet): " + imagePath);

    }


    public void setAttackStrategy(AttackStrategy strategy) {
        this.attackStrategy = strategy;
        System.out.println("Entity: Attack strategy set (Strategy pattern not fully implemented yet).");
    }
}
