package levels;

import entities.Entity;
import game.Difficulty;
import game.Game;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

public class Level {
    private int levelNumber;
    private List<Wave> waves = new ArrayList<>();
    private Wave currentWave;
    private int currentWaveIndex = -1;
    private List<Entity> entities = new ArrayList<>();
    private boolean levelComplete = false;
    private Game gameInstance;
    private Difficulty difficulty;

    public Level(int levelNumber, Game gameInstance, Difficulty difficulty) {
        this.levelNumber = levelNumber;
        this.gameInstance = gameInstance;
        this.difficulty = difficulty;
        System.out.println("Level " + levelNumber + " created with difficulty " + difficulty);
        initializeWaves();
    }

    private void initializeWaves() {
        System.out.println("Level " + levelNumber + ": Initializing waves (Data will come from LevelDataStorage later)");
        System.out.println("Level " + levelNumber + ": (Wave creation logic will be added later)");
    }

    public void update() {
        if (levelComplete) return;
    }

    public void draw(Graphics g) {
    }

    public List<Entity> getEntities() { return entities; }
    public boolean isLevelComplete() { return levelComplete; }
}
