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
    protected long lifespanMillis = -1; 
    protected long spawnTimeMillis; 

    public Entity(int x, int y, int width, int height, int maxHealth, int attackDamage, int attackRange, int attackSpeed, long lifespanMillis) { 
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
        this.lifespanMillis = lifespanMillis; 
        this.spawnTimeMillis = System.currentTimeMillis();
    }
    public Entity(int x, int y, int width, int height, int maxHealth,
                 int attackDamage, int attackRange, int attackSpeed) {
        this(x, y, width, height, maxHealth, attackDamage, attackRange, attackSpeed, -1); \
    }

    @Override
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
    @Override
    public void die() {
        if (!isActive) return; 
        this.isActive = false;
        System.out.println(this.getClass().getSimpleName() + " died at (" + x + ", " + y + ")");
    
    @Override
    public void takeDamage(int damage) {
        if (!isActive || damage <= 0) return; 
        this.health -= damage;
         System.out.println(this.getClass().getSimpleName() + " took " + damage + " damage. HP left: " + this.health);
        if (this.health < 0) {
            this.health = 0;
        }
    }
   
     public void setAttackStrategy(AttackStrategy strategy) {
         this.attackStrategy = strategy; 
     }
     public AttackStrategy getAttackStrategy() {
         return attackStrategy;
     }
     public List<Projectile> getProjectiles() {
         return projectiles; 
     } 
     public int getHealth() {
         return health; 
     }
     public int getCurrentMaxHealth() { 
         return currentMaxHealth; 
     }
     public int getCenterY() {
         return y + height / 2; 
     } 
     public Rectangle getBounds() {
         return new Rectangle(x, y, width, height); 
     }
     public long getLifespanMillis() {
         return lifespanMillis; 
     } 
     public long getSpawnTimeMillis() {
         return spawnTimeMillis;
     } 
     public double getRemainingLifespanPercentage() {
        if (lifespanMillis <= 0 || !isActive) return 1.0;
        long timeElapsed = System.currentTimeMillis() - spawnTimeMillis;
        long timeLeft = lifespanMillis - timeElapsed;
        if (timeLeft <= 0) return 0.0;
        return (double) timeLeft / lifespanMillis;
    }
     @Override
     public void draw(Graphics g) {
        if (!isActive) return;
        if (sprite != null) {
             g.drawImage(sprite, x, y, drawWidth, drawHeight, null);
        } else {
            g.setColor(getColor());
            g.fillRect(x, y, drawWidth, drawHeight);
        }
         drawHealthBar(g);
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
     public void setSprite(String imagePath) {
         this.sprite = ResourceLoader.loadImage(imagePath);
         if (this.sprite == null) {
             System.err.println("Warning: Failed to load sprite '" + imagePath + "' for " + getClass().getSimpleName());
         }
     }
}