package levels;

import entities.Entity;
import entities.EntityTypeData;
import entities.EntityTypeData.TitanType;
import game.Difficulty;
import game.Game;
import game.GameSettings;
import patterns.factory.TitanFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Wave {
    private final int waveNumber;
    private final List<TitanSpawnInfo> titanSpawnInfos;
    private int totalTitansInWave;
    private int titansSpawnedThisWave;
    private long lastSpawnTime;
    private final long spawnIntervalMillis;
    private boolean waveActive;
    private boolean waveSpawningComplete;
    private final Difficulty difficulty;
    private final TitanFactory titanFactory;
    private final Random random = new Random();
    private final Game gameInstance;
    private static final int[] LANE_Y_CENTERS = GameSettings.LANE_Y_CENTERS;

    private static class TitanSpawnInfo {
        TitanType type;
        int count;
        int spawnedCount;

        TitanSpawnInfo(TitanType type, int count) {
            this.type = type;
            this.count = count;
            this.spawnedCount = 0;
        }
    }

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

    public void addTitanGroup(TitanType type, int count) {
        if (count > 0) {
            titanSpawnInfos.add(new TitanSpawnInfo(type, count));
            totalTitansInWave += count;
        }
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
        } else {
            waveActive = false;
            waveSpawningComplete = true;
        }
    }

    public void update(List<Entity> activeEntities) {
        if (!waveActive || waveSpawningComplete) {

            return;
        }

        long currentTime = System.currentTimeMillis();
        long timeElapsed = currentTime - lastSpawnTime;


        if (timeElapsed >= spawnIntervalMillis) {
            boolean spawned = spawnNextTitan(activeEntities);
            if (spawned) {
                lastSpawnTime = currentTime;
                titansSpawnedThisWave++;


                if (titansSpawnedThisWave >= totalTitansInWave) {
                    waveActive = false;
                    waveSpawningComplete = true;
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
            waveActive = false;
            waveSpawningComplete = true;
            return false;
        }


        TitanSpawnInfo groupToSpawn = availableGroups.get(random.nextInt(availableGroups.size()));


        int laneIndex = random.nextInt(LANE_Y_CENTERS.length);

        int laneCenterY = LANE_Y_CENTERS[laneIndex];

        int titanHeight = EntityTypeData.TITAN_DRAW_HEIGHT;

        int startY = laneCenterY - titanHeight / 2;
        int startX = gameInstance.getGamePanelWidth() + 40 + random.nextInt(80);

        Entity titan = titanFactory.createEntity(groupToSpawn.type.name(), startX, startY, difficulty);


        if (titan != null) {
            activeEntities.add(titan);
            groupToSpawn.spawnedCount++;
            return true;
        } else {

            groupToSpawn.spawnedCount = groupToSpawn.count;
            return false;
        }
    }


    public boolean isSpawningComplete() {
        return waveSpawningComplete;
    }

    public boolean isActive() {
        return waveActive;
    }

    public int getWaveNumber() {
        return waveNumber;
    }
}
