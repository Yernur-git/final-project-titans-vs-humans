package patterns.factory;

import entities.Entity;
import entities.EntityTypeData.ObstacleType;
import game.Difficulty;

import java.lang.reflect.InvocationTargetException;

public class ObstacleFactory implements EntityFactory {


    @Override
    public Entity createEntity(String typeKey_NotUsed, int x, int y, Difficulty difficulty) {
        throw new UnsupportedOperationException("Use createObstacle(ObstacleType, x, y) instead for Obstacles.");
    }


    public Entity createObstacle(ObstacleType type, int x, int y) {
        Entity obstacle = null;
        try {

            obstacle = type.getEntityClass().getDeclaredConstructor(int.class, int.class).newInstance(x, y);


            obstacle.setSprite(type.getSpritePath());


            if (type == ObstacleType.TRAP && obstacle instanceof entities.SpikeTrap) {


            }


        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            System.err.println("CRITICAL: Failed to instantiate obstacle: " + type.getEntityClass().getName());
            e.printStackTrace();
            return null;
        }

        return obstacle;
    }
}
