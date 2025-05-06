package patterns.factory;

import entities.Entity;
import entities.EntityTypeData.ObstacleType;
import java.lang.reflect.InvocationTargetException;

public class ObstacleFactory implements EntityFactory {
    @Override
    public Entity createEntity(String typeKey_NotUsed, int x, int y, double difficultyFactor) {
        throw new UnsupportedOperationException("Use createObstacle(ObstacleType, x, y) instead.");
    }

    public Entity createObstacle(ObstacleType type, int x, int y) {
        Entity obstacle = null;
        try {
            obstacle = type.getEntityClass()
                    .getDeclaredConstructor(int.class, int.class)
                    .newInstance(x, y);
            obstacle.setSprite(type.getSpritePath());
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            System.err.println("CRITICAL: Failed to instantiate obstacle: " + type.getDisplayName());
            e.printStackTrace();
            return null;
        }
        return obstacle;
    }
}