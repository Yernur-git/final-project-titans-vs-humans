package patterns.factory;

import entities.Entity;
import entities.EntityTypeData.ObstacleType;
import game.Difficulty;


public class ObstacleFactory implements EntityFactory {
    @Override
    public Entity createEntity(String type, int x, int y, Difficulty difficulty) {
        ObstacleType obstacleType;
        try {
            obstacleType = ObstacleType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Unknown obstacle type: " + type);
        }

        try {
            Entity obstacle = obstacleType.getEntityClass().getDeclaredConstructor(int.class, int.class).newInstance(x, y);
            obstacle.setSprite(obstacleType.getSpritePath());
            return obstacle;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

