package patterns.decorator;

import entities.Human;

import java.awt.*;

public class ArmoredHuman extends HumanDecorator {
    private static final int ARMOR_COST_INCREASE = 30;
    private static final double DAMAGE_REDUCTION_FACTOR = 0.70;

    public ArmoredHuman(Human decoratedHuman) {
        super(decoratedHuman);

    }

    @Override
    public void takeDamage(int damage) {

        int reducedDamage = (int) (damage * DAMAGE_REDUCTION_FACTOR);
        super.takeDamage(reducedDamage);
    }

    @Override
    public int getCost() {


        return decoratedHuman.getCost() + ARMOR_COST_INCREASE;
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);


        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(new Color(0, 0, 255, 60));
        g2d.fillRect(getX(), getY(), getWidth(), getHeight());
        g2d.dispose();
    }

    @Override
    public Color getColor() {

        return new Color(100, 100, 255);
    }
}
