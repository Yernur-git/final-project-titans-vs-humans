package entities;

import java.awt.*;

public class BlockerWall extends Entity {
    private static final int WALL_HEALTH = 300;
    private static final int WALL_COST = 50;

    public BlockerWall(int x, int y) {
        super(x, y, EntityTypeData.WALL_DRAW_WIDTH, EntityTypeData.WALL_DRAW_HEIGHT, WALL_HEALTH, 0, 0, 0);
        setSprite("obstacles/wall.png");
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
        return Color.GRAY;
    }

    public int getCost() {
        return WALL_COST;
    }

    @Override
    protected void drawHealthBar(Graphics g) {
        if (currentMaxHealth <= 0) return;

        int barWidth = width;
        int barHeight = 4;
        int barX = x;
        int barY = y - barHeight - 2;

        g.setColor(new Color(150, 75, 0));
        g.fillRect(barX, barY, barWidth, barHeight);

        double healthPercentage = (double) health / currentMaxHealth;
        int fillWidth = (int) (barWidth * healthPercentage);
        if (fillWidth < 0) fillWidth = 0;

        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(barX, barY, fillWidth, barHeight);

        g.setColor(Color.BLACK);
        g.drawRect(barX, barY, barWidth, barHeight);
    }
}
