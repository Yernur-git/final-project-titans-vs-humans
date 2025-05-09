package patterns.factory;

import entities.Entity;
import entities.EntityTypeData.TitanType;
import entities.Titan;
import game.Difficulty;

public class TitanFactory implements EntityFactory {


    @Override
    public Entity createEntity(String typeKey_NotUsed, int x, int y, Difficulty difficulty) {
        throw new UnsupportedOperationException("Use createTitan(TitanType, x, y, difficulty) instead for Titans.");
    }


    public Titan createTitan(TitanType type, int x, int y, Difficulty difficulty) {


        Titan titan = new Titan(x, y,
                type.getBaseHealth(),
                type.getBaseDamage(),
                type.getBaseRange(),
                type.getBaseAttackSpeed(),
                type.getBaseMoveSpeed());


        titan.scaleMaxHealth(difficulty.getTitanHealthScale());
        titan.setAttackDamage((int) Math.max(1, Math.round(titan.getAttackDamage() * difficulty.getTitanDamageScale())));
        titan.setMovementSpeed((int) Math.max(1, Math.round(titan.getMovementSpeed() * difficulty.getTitanMoveSpeedScale())));


        titan.setSprite(type.getSpritePath());

        return titan;
    }
}
