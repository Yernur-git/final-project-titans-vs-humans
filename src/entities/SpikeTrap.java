package entities;

import java.awt.*;

public class SpikeTrap extends Entity {
    private static final int TRAP_HEALTH = 1;
    private static final int TRAP_DAMAGE = 75;
    private static final int TRAP_COST = 25;
    private boolean triggered = false;

    public SpikeTrap(int x, int y) {
        super(x, y, EntityTypeData.TRAP_DRAW_WIDTH, EntityTypeData.TRAP_DRAW_HEIGHT, TRAP_HEALTH, TRAP_DAMAGE, 0, 0);
        setSprite("obstacles/spiketrap.png");
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
        return Color.DARK_GRAY;
    }

    public int getCost() {
        return TRAP_COST;
    }


    public void trigger(Entity target) {
        if (!triggered && isActive && target instanceof Titan) {
            target.takeDamage(this.attackDamage);
            this.triggered = true;
            this.die();
        }
    }

    public boolean isTriggered() {
        return triggered;
    }

    @Override
    protected void drawHealthBar(Graphics g) {
    }

    @Override
    public void draw(Graphics g) {
        if (!isActive) return;

        if (sprite != null) {
            g.drawImage(sprite, x, y, width, height, null);
        } else {
            g.setColor(getColor());
            g.fillRect(x, y, width, height);
        }
    }
}
