package patterns.factory;

import entities.Entity;
import entities.EntityTypeData.HumanType;
import entities.Human;
import game.Difficulty;
import patterns.strategy.AttackStrategy;
import patterns.strategy.BasicAttack;

import java.util.HashMap;
import java.util.Map;

public class HumanFactory implements EntityFactory {
    private final Map<Class<? extends AttackStrategy>, AttackStrategy> strategyInstances = new HashMap<>();

    @Override
    public Entity createEntity(String type, int x, int y, Difficulty difficulty) {
        HumanType humanType;
        try {
            humanType = HumanType.valueOf(type.toUpperCase()); // например "BASIC"
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unknown human type: " + type);
        }

        Human human = new Human(x, y,
                humanType.getHealth(),
                humanType.getDamage(),
                humanType.getRange(),
                humanType.getAttackSpeed(),
                humanType.getCost(),
                humanType.getLifespanMillis());

        human.setAttackStrategy(getStrategyInstance(humanType.getStrategyClass()));
        human.setSprite(humanType.getSpritePath());

        return human;
    }

    private AttackStrategy getStrategyInstance(Class<? extends AttackStrategy> strategyClass) {
        return strategyInstances.computeIfAbsent(strategyClass, clazz -> {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                return new BasicAttack();
            }
        });
    }
}

