package levels;

import entities.Entity;
import entities.EntityTypeData;
import game.Difficulty;
import game.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Wave {
    private int waveNumber;
    private int totalTitansInWave = 0;
    private int titansSpawnedThisWave = 0;
    private long lastSpawnTime = 0;
    private long spawnIntervalMillis;
    private boolean waveActive = false;
    private boolean waveSpawningComplete = false;
    private Game gameInstance;
    private Difficulty difficulty;

    public Wave(int waveNumber, long spawnIntervalMillis, Game gameInstance, Difficulty difficulty) {
        this.waveNumber = waveNumber;
        this.spawnIntervalMillis = Math.max(500, spawnIntervalMillis);
        this.gameInstance = gameInstance;
        this.difficulty = difficulty;
        System.out.println("Wave " + waveNumber + " created. Interval: " + spawnIntervalMillis + "ms");
    }

    public void addTitanGroup(EntityTypeData.TitanType type, int count) {
        System.out.println("Wave " + waveNumber + ": Adding group of " + count + " " + type + " (Full logic later)");
        this.totalTitansInWave += count;
    }

    public void startWave() {
        System.out.println("Wave " + waveNumber + ": Starting wave (Setting active flag later)");
    }

    public void update(List<Entity> activeEntities) {
        if (!waveActive || waveSpawningComplete) return;
    }

    private boolean spawnNextTitan(List<Entity> activeEntities) {
        System.out.println("Wave " + waveNumber + ": Attempting to spawn next titan (Full logic later)");
        return false;
    }

    public boolean isActive() { return waveActive; }
    public boolean isSpawningComplete() { return waveSpawningComplete; }
    public int getWaveNumber() { return waveNumber; }
}
