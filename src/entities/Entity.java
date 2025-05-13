package entities;

import gameobjects.Projectile;
import patterns.strategy.AttackStrategy;
import utils.ResourceLoader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class Entity {
    protected int x, y;
    protected int width, height;
    protected int maxHealth;
    protected int health;
    protected int attackDamage;
    protected int attackRange;
    protected int attackSpeed;
    public long lastAttackTime;
    protected boolean isActive;
    protected AttackStrategy attackStrategy;
    protected List<Projectile> projectiles = new ArrayList<>();
    public BufferedImage sprite;
    protected int currentMaxHealth;
    protected long lifespanMillis = -1;
    protected long spawnTimeMillis;

    public Entity(int x, int y, int width, int height, int maxHealth,
                  int attackDamage, int attackRange, int attackSpeed, long lifespanMillis) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.currentMaxHealth = maxHealth;
        this.attackDamage = attackDamage;
        this.attackRange = attackRange;
        this.attackSpeed = attackSpeed;
        this.lastAttackTime = 0;
        this.isActive = true;
        this.lifespanMillis = lifespanMillis;
        this.spawnTimeMillis = System.currentTimeMillis();
    }

    public Entity(int x, int y, int width, int height, int maxHealth,
                  int attackDamage, int attackRange, int attackSpeed) {
        this(x, y, width, height, maxHealth, attackDamage, attackRange, attackSpeed, -1);
    }


    public void update() {
        if (!isActive) return;

        if (lifespanMillis > 0 && System.currentTimeMillis() - spawnTimeMillis > lifespanMillis) {
            die();
            return;
        }

        move();

        long currentTime = System.currentTimeMillis();
        if (canAttack() && (currentTime - lastAttackTime >= attackSpeed)) {
            attack();
            lastAttackTime = currentTime;
        }

        updateProjectiles();

        if (health <= 0) {
            die();
        }
    }

    protected abstract void move();

    protected abstract boolean canAttack();

    protected abstract void attack();

    protected abstract Color getColor();

    protected void updateProjectiles() {
        Iterator<Projectile> iterator = projectiles.iterator();
        while (iterator.hasNext()) {
            Projectile p = iterator.next();
            p.update();
            if (!p.isActive()) {
                iterator.remove();
            }
        }
    }

    public void die() {
        isActive = false;
    }

    public void takeDamage(int damage) {
        if (!isActive) return;
        this.health -= damage;
        if (this.health < 0) {
            this.health = 0;
        }
    }

    public void setAttackStrategy(AttackStrategy attackStrategy) {
        this.attackStrategy = attackStrategy;
    }

    public void draw(Graphics g) {
        if (!isActive) return;

        if (sprite != null) {
            g.drawImage(sprite, x, y, width, height, null);
        } else {
            g.setColor(getColor());
            g.fillRect(x, y, width, height);
        }

        drawHealthBar(g);

        for (Projectile p : projectiles) {
            p.draw(g);
        }
    }

    protected void drawHealthBar(Graphics g) {
        if (currentMaxHealth <= 0) return;

        int barWidth = width;
        int barHeight = 5;
        int barX = x;
        int barY = y - barHeight - 3;

        g.setColor(Color.RED);
        g.fillRect(barX, barY, barWidth, barHeight);

        double healthPercentage = (double) health / currentMaxHealth;
        int greenWidth = (int) (barWidth * healthPercentage);
        if (greenWidth < 0) greenWidth = 0;

        double lifespanPercentage = getRemainingLifespanPercentage();
        Color healthColor = Color.GREEN;

        if (this.lifespanMillis > 0 && lifespanPercentage < 0.25) {
            long time = System.currentTimeMillis();
            if ((time / 300) % 2 == 0) {
                healthColor = Color.YELLOW;
            } else {
                healthColor = Color.ORANGE;
            }
        } else if (this.lifespanMillis > 0 && lifespanPercentage < 0.5) {
            healthColor = new Color(170, 220, 0);
        }

        g.setColor(healthColor);
        g.fillRect(barX, barY, greenWidth, barHeight);

        g.setColor(Color.BLACK);
        g.drawRect(barX, barY, barWidth, barHeight);
    }

    public boolean isActive() {
        return isActive;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getAttackDamage() {
        return attackDamage;
    }

    public int getHealth() {
        return health;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getCurrentMaxHealth() {
        return currentMaxHealth;
    }

    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    public int getAttackRange() {
        return attackRange;
    }

    public int getAttackSpeed() {
        return attackSpeed;
    }

    public AttackStrategy getAttackStrategy() {
        return attackStrategy;
    }

    public int getCenterY() {
        return y + height / 2;
    }

    public long getLifespanMillis() {
        return lifespanMillis;
    }

    public long getSpawnTimeMillis() {
        return spawnTimeMillis;
    }

    public double getRemainingLifespanPercentage() {
        if (lifespanMillis <= 0 || !isActive) {
            return 1.0;
        }
        long timeElapsed = System.currentTimeMillis() - spawnTimeMillis;
        long timeLeft = lifespanMillis - timeElapsed;
        if (timeLeft <= 0) {
            return 0.0;
        }
        return (double) timeLeft / lifespanMillis;
    }

    public void setSprite(String imagePath) {
        this.sprite = ResourceLoader.loadImage(imagePath);
    }

    public void setHealth(int health) {
        this.health = Math.max(0, health);
        if (this.health > this.currentMaxHealth) {
            this.health = this.currentMaxHealth;
        }
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = Math.max(1, maxHealth);
        this.currentMaxHealth = this.maxHealth;
        if (this.health > this.currentMaxHealth) {
            this.health = this.currentMaxHealth;
        }
    }


    public void scaleMaxHealth(double factor) {
        this.currentMaxHealth = (int) Math.max(1, Math.round(this.maxHealth * factor));
        this.health = this.currentMaxHealth;
    }

    public void setAttackDamage(int attackDamage) {
        this.attackDamage = Math.max(0, attackDamage);
    }

    public void setAttackSpeed(int speed) {
        this.attackSpeed = Math.max(100, speed);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
