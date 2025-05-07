package entities;

import java.awt.*;

public class HumanBase extends Entity {
    private static final int BASE_WIDTH = 80;
    private static final int BASE_HEIGHT = 120;
    private static final int BASE_X = 50;
    private static final int BASE_Y = 240;

    public HumanBase(int initialMaxHealth) {
        super(BASE_X, BASE_Y, BASE_WIDTH, BASE_HEIGHT, initialMaxHealth, 0, 0, 0);
    }

    @Override protected void move() {}
    @Override protected boolean canAttack() {
        return false;
    }
    @Override protected void attack() {}
    @Override protected Color getColor() {
        return Color.GREEN.darker();
    }
    public boolean isDestroyed() {
        return health <= 0;
    }
}
