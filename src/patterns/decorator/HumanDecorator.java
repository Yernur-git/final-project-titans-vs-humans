package patterns.decorator;

import entities.Human; 

public abstract class HumanDecorator extends Human {
    protected Human decoratedHuman;

    public HumanDecorator(Human decoratedHuman) {
        super(decoratedHuman.getX(), decoratedHuman.getY(),
              decoratedHuman.getCurrentMaxHealth(),
              decoratedHuman.getAttackDamage(),
              decoratedHuman.getAttackRange(),
              decoratedHuman.getAttackSpeed(),
              decoratedHuman.getCost(), 
              decoratedHuman.getLifespanMillis()); 

        this.decoratedHuman = decoratedHuman;

        this.health = decoratedHuman.getHealth();
        this.currentMaxHealth = decoratedHuman.getCurrentMaxHealth(); 
        this.lastAttackTime = decoratedHuman.lastAttackTime;
        this.isActive = decoratedHuman.isActive();
        this.sprite = decoratedHuman.sprite; 
        this.attackStrategy = decoratedHuman.getAttackStrategy(); 
        this.spawnTimeMillis = decoratedHuman.getSpawnTimeMillis(); 
    }

    @Override 
    public void update() {
         decoratedHuman.setPosition(this.x, this.y); 
         decoratedHuman.update(); 
         syncStateAfterUpdate(); 
        }
    @Override 
    public void move() { 
        decoratedHuman.move();
         syncPosition(); 
         }
    @Override 
    public boolean canAttack() {
         return decoratedHuman.canAttack(); 
         }
    @Override 
    public void attack() { 
        decoratedHuman.attack(); 
        }
    @Override
     public void takeDamage(int damage) {
         decoratedHuman.takeDamage(damage);
         this.health = decoratedHuman.getHealth(); 
         }
    @Override 
    public void die() {
         decoratedHuman.die();
         this.isActive = false; 
         }
    @Override 
    public void draw(java.awt.Graphics g) {
         decoratedHuman.setPosition(this.x, this.y);
         decoratedHuman.draw(g);
         drawHealthBar(g); 
          }

    @Override
     public int getHealth() {
         return decoratedHuman.getHealth(); 
         }
    @Override
     public int getMaxHealth() { 
        return decoratedHuman.getMaxHealth();
         } 
    @Override
     public int getCurrentMaxHealth() {
         return decoratedHuman.getCurrentMaxHealth(); 
         }
    @Override
     public int getAttackDamage() { 
        return decoratedHuman.getAttackDamage();
         }
     @Override 
     public int getAttackRange() {
         return decoratedHuman.getAttackRange(); 
         }
     @Override
      public int getAttackSpeed() {
         return decoratedHuman.getAttackSpeed(); 
         }
     @Override
      public boolean isActive() {
         return decoratedHuman.isActive(); 
         }
     @Override
      public java.awt.Rectangle getBounds() {
         return new java.awt.Rectangle(this.x, this.y, this.width, this.height);
          } 
     @Override
      public int getX() { 
        return this.x; 
        }
     @Override
      public int getY() { 
        return this.y; 
        }
     @Override
      public int getWidth() {
         return decoratedHuman.getWidth(); 
         } 
     @Override
      public int getHeight() {
         return decoratedHuman.getHeight(); 
         } 
     @Override 
     public patterns.strategy.AttackStrategy getAttackStrategy() { 
        return decoratedHuman.getAttackStrategy(); 
        }
     @Override
      public java.util.List<gameobjects.Projectile> getProjectiles() {
         return this.projectiles; 
         } 
     @Override
      public int getBaseMaxHealth() {
         return decoratedHuman.getBaseMaxHealth();
         }
     @Override
      public int getBaseCost() { 
        return decoratedHuman.getBaseCost(); 
        }
    @Override 
    public void setAttackStrategy(patterns.strategy.AttackStrategy s) {
         this.attackStrategy = s;
        decoratedHuman.setAttackStrategy(s); 
         }
    @Override
     public void setSprite(String path) { 
        super.setSprite(path);
        decoratedHuman.setSprite(path); 
        }
    @Override
    public void setPosition(int x, int y) {
         super.setPosition(x,y);
          decoratedHuman.setPosition(x,y); 
          }
     @Override
      public void setHealth(int h) {
         super.setHealth(h); 
         decoratedHuman.setHealth(h); 
         }
     @Override
      public void setMaxHealth(int mh) {
         super.setMaxHealth(mh);
         decoratedHuman.setMaxHealth(mh); 
         }
     @Override
      public void scaleMaxHealth(double f) {
         super.scaleMaxHealth(f);
         decoratedHuman.scaleMaxHealth(f); 
         }
     @Override
      public void setAttackDamage(int d) {
         super.setAttackDamage(d);
         decoratedHuman.setAttackDamage(d); 
         }
     @Override
      public void setAttackSpeed(int s) {
         super.setAttackSpeed(s);
         decoratedHuman.setAttackSpeed(s); 
        }

    @Override public abstract int getCost();

    private void syncPosition() {
        this.x = decoratedHuman.getX();
        this.y = decoratedHuman.getY(); }

    private void syncStateAfterUpdate() { 
        this.health = decoratedHuman.getHealth();
        this.isActive = decoratedHuman.isActive();
        this.lastAttackTime = decoratedHuman.lastAttackTime;
        this.projectiles = decoratedHuman.getProjectiles(); 
        syncPosition(); 
    }
}
