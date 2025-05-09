package patterns.factory;

import entities.Entity;
import entities.EntityTypeData.HumanType;
import entities.Human;
import game.Difficulty;
import patterns.strategy.AttackStrategy;
import patterns.strategy.BasicAttack;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class HumanFactory implements EntityFactory {

    private final Map<Class<? extends AttackStrategy>, AttackStrategy> strategyInstances = new HashMap<>();

    @Override
    public Entity createEntity(String typeKey_NotUsed, int x, int y, Difficulty difficulty) {
        throw new UnsupportedOperationException("Use createHuman(HumanType, x, y) instead for Humans.");
    }


    public Human createHuman(HumanType type, int x, int y) {

        Human human = new Human(x, y,
                type.getHealth(),
                type.getDamage(),
                type.getRange(),
                type.getAttackSpeed(),
                type.getCost(),
                type.getLifespanMillis());


        human.setAttackStrategy(getStrategyInstance(type.getStrategyClass()));


        human.setSprite(type.getSpritePath());


        return human;
    }


    private AttackStrategy getStrategyInstance(Class<? extends AttackStrategy> strategyClass) {
        return strategyInstances.computeIfAbsent(strategyClass, clazz -> {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                System.err.println("CRITICAL: Failed to instantiate strategy: " + clazz.getName() + ". Using BasicAttack.");
                e.printStackTrace();

                return strategyInstances.computeIfAbsent(BasicAttack.class, basicClazz -> new BasicAttack());
            }
        });
    }
}
