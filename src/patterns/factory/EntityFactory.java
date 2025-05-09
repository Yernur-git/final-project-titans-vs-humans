package patterns.factory;

import entities.Entity;
import game.Difficulty;

public interface EntityFactory {
    Entity createEntity(String type, int x, int y, Difficulty difficulty);
}
