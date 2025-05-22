package patterns.factory;

import entities.Entity;
import entities.EntityTypeData.TitanType;
import entities.Titan;
import game.Difficulty;

public class TitanFactory implements EntityFactory {
    @Override
    public Entity createEntity(String type, int x, int y, Difficulty difficulty) {
        TitanType titanType;
        try {
            titanType = TitanType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unknown titan type: " + type);
        }

        Titan titan = new Titan(x, y,
                titanType.getBaseHealth(),
                titanType.getBaseDamage(),
                titanType.getBaseRange(),
                titanType.getBaseAttackSpeed(),
                titanType.getBaseMoveSpeed());

        titan.scaleMaxHealth(difficulty.getTitanHealthScale());
        titan.setAttackDamage((int)(titan.getAttackDamage() * difficulty.getTitanDamageScale()));
        titan.setMovementSpeed((int)(titan.getMovementSpeed() * difficulty.getTitanMoveSpeedScale()));
        titan.setSprite(titanType.getSpritePath());

        return titan;
    }
}

