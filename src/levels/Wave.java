package levels;

import entities.Entity;
import entities.EntityTypeData.TitanType;
import game.Difficulty;
import game.Game;
import patterns.factory.TitanFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Wave {
    private int waveNumber;
    private long spawnIntervalMillis;
    private Game gameInstance;
    private Difficulty difficulty;
    private TitanFactory titanFactory;
    private List<TitanSpawnInfo> titanSpawnInfos;
    private boolean waveActive;
    private boolean waveSpawningComplete;
    private int titansSpawnedThisWave;
    private int totalTitansInWave;
    private long lastSpawnTime;
    private Random random = new Random();

    public Wave(int waveNumber, long spawnIntervalMillis, Game gameInstance, Difficulty difficulty) {
        this.waveNumber = waveNumber;
        this.spawnIntervalMillis = Math.max(500, spawnIntervalMillis);
        this.gameInstance = gameInstance;
        this.difficulty = difficulty;
        this.titanFactory = gameInstance.getTitanFactory();
        this.titanSpawnInfos = new ArrayList<>();
        this.waveActive = false;
        this.waveSpawningComplete = false;
        this.titansSpawnedThisWave = 0;
        this.totalTitansInWave = 0;
    }

    public void startWave() {
        if (totalTitansInWave > 0) {
            waveActive = true;
            waveSpawningComplete = false;
            titansSpawnedThisWave = 0;
            for (TitanSpawnInfo info : titanSpawnInfos) {
                info.spawnedCount = 0;
            }
            lastSpawnTime = System.currentTimeMillis();
            System.out.println("Wave " + waveNumber + " starting (" + totalTitansInWave + " titans). Interval: " + spawnIntervalMillis + "ms.");
        } else {
            System.out.println("Wave " + waveNumber + " has no titans, marking as complete.");
            waveActive = false;
            waveSpawningComplete = true;
        }
    }

    public void update(List<Entity> activeEntities) {
        if (!waveActive || waveSpawningComplete) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastSpawnTime >= spawnIntervalMillis) {
            boolean spawned = spawnNextTitan(activeEntities);
            if (spawned) {
                lastSpawnTime = currentTime;
                titansSpawnedThisWave++;

                if (titansSpawnedThisWave >= totalTitansInWave) {
                    waveActive = false;
                    waveSpawningComplete = true;
                    System.out.println("Wave " + waveNumber + ": All " + totalTitansInWave + " titans spawned.");
                }
            }
        }
    }

    private boolean spawnNextTitan(List<Entity> activeEntities) {
        List<TitanSpawnInfo> availableGroups = new ArrayList<>();
        for (TitanSpawnInfo info : titanSpawnInfos) {
            if (info.spawnedCount < info.count) {
                availableGroups.add(info);
            }
        }

        if (availableGroups.isEmpty()) {
            System.err.println("Wave " + waveNumber + ": No available groups but not all spawned?! Force complete.");
            waveActive = false;
            waveSpawningComplete = true;
            return false;
        }

        TitanSpawnInfo groupToSpawn = availableGroups.get(random.nextInt(availableGroups.size()));
        int laneIndex = random.nextInt(GameSettings.LANE_Y_CENTERS.length);
        int laneCenterY = GameSettings.LANE_Y_CENTERS[laneIndex];
        int titanHeight = EntityTypeData.TITAN_DRAW_HEIGHT;
        int startY = laneCenterY - titanHeight / 2;
        int startX = gameInstance.getGamePanelWidth() + 40 + random.nextInt(80);

        Entity titan = titanFactory.createTitan(groupToSpawn.type, startX, startY, difficulty);

        if (titan != null) {
            activeEntities.add(titan);
            groupToSpawn.spawnedCount++;
            return true;
        } else {
            System.err.println("Wave " + waveNumber + ": TitanFactory failed to create " + groupToSpawn.type);
            groupToSpawn.spawnedCount = groupToSpawn.count;
            return false;
        }
    }
}
