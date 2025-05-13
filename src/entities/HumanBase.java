package entities;

import java.awt.*;

public class HumanBase extends Entity {
    private static final int BASE_WIDTH = 80;
    private static final int BASE_HEIGHT = 120;
    private static final int BASE_X = 50;
    private static final int BASE_Y = 240;

    public HumanBase(int initialMaxHealth) {
        super(BASE_X, BASE_Y, BASE_WIDTH, BASE_HEIGHT, initialMaxHealth, 0, 0, 0);
        setSprite("humans/base.png");
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
        return new Color(0, 100, 0);
    }

    public boolean isDestroyed() {
        return health <= 0;
    }

    @Override
    protected void drawHealthBar(Graphics g) {
        if (currentMaxHealth <= 0) return;

        int barWidth = 150;
        int barHeight = 10;
        int barX = x + (width / 2) - (barWidth / 2);
        int barY = y - barHeight - 10;

        g.setColor(Color.RED);
        g.fillRect(barX, barY, barWidth, barHeight);

        double healthPercentage = (double) health / currentMaxHealth;
        int greenWidth = (int) (barWidth * healthPercentage);
        if (greenWidth < 0) greenWidth = 0;

        g.setColor(Color.GREEN);
        g.fillRect(barX, barY, greenWidth, barHeight);


        g.setColor(Color.BLACK);
        g.drawRect(barX, barY, barWidth, barHeight);
    }

    public void scaleBaseHealth(double factor) {
        this.currentMaxHealth = (int) Math.max(1, Math.round(this.maxHealth * factor));
        this.health = this.currentMaxHealth;
    }
}
