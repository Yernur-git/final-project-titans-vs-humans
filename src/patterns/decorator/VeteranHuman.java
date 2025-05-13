package patterns.decorator;

import entities.Human;

import java.awt.*;

public class VeteranHuman extends HumanDecorator {
    private static final int VETERAN_COST_INCREASE = 50;
    private static final double ATTACK_DAMAGE_MULTIPLIER = 1.25;
    
    public VeteranHuman(Human decoratedHuman) {
        super(decoratedHuman);

        int boostedDamage = (int) (decoratedHuman.getAttackDamage() * ATTACK_DAMAGE_MULTIPLIER);
        this.setAttackDamage(boostedDamage);
    }

    @Override
    public int getCost() {
        return decoratedHuman.getCost() + VETERAN_COST_INCREASE;
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setColor(Color.ORANGE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(getX() - 1, getY() - 1, getWidth() + 1, getHeight() + 1);
        g2d.dispose();
    }

    @Override
    public Color getColor() {

        return new Color(150, 50, 150);
    }
}
