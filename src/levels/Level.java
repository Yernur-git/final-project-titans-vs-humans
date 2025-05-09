package levels;

import entities.*;
import game.Difficulty;
import game.Game;
import gameobjects.Projectile;
import levels.LevelDataStorage.LevelInfo;
import levels.LevelDataStorage.TitanGroupInfo;
import levels.LevelDataStorage.WaveInfo;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Level {
    private final int levelNumber;
    private final List<Wave> waves;
    private Wave currentWave;
    private int currentWaveIndex;
    private final HumanBase humanBase;
    private final List<Entity> entities;
    private boolean levelComplete;
    private final Game gameInstance;
    private final Difficulty difficulty;
    private static final int TITAN_KILL_REWARD_BASE = 15;
    private static final int WAVE_COMPLETE_REWARD_BASE = 75;
    private static final int LEVEL_COMPLETE_REWARD_BASE = 150;

    public Level(int levelNumber, Game gameInstance, Difficulty difficulty) {
        this.levelNumber = levelNumber;
        this.gameInstance = gameInstance;
        this.difficulty = difficulty;
        this.humanBase = gameInstance.getHumanBase();
        this.entities = new ArrayList<>();
        this.waves = new ArrayList<>();
        this.currentWaveIndex = -1;
        this.levelComplete = false;
        initializeWavesFromStorage();
        startNextWave();
    }

    private void initializeWavesFromStorage() {
        LevelInfo levelInfo = LevelDataStorage.getLevelInfo(levelNumber);
        if (levelInfo == null) {
            levelComplete = true;
            return;
        }

        double intervalMultiplier = difficulty.getSpawnIntervalMultiplier();

        for (WaveInfo waveInfo : levelInfo.waves) {
            long adjustedInterval = (long) (waveInfo.baseSpawnIntervalMillis * intervalMultiplier);
            Wave wave = new Wave(waveInfo.waveNumber, adjustedInterval, gameInstance, difficulty);

            for (TitanGroupInfo groupInfo : waveInfo.titanGroups) {
                wave.addTitanGroup(groupInfo.type, groupInfo.count);
            }
            waves.add(wave);
        }
    }

    public void update() {
        if (levelComplete) return;

        if (currentWave != null && currentWave.isActive()) {
            currentWave.update(entities);
        }

        List<Entity> entitiesToRemove = new ArrayList<>();
        Iterator<Entity> entityIterator = entities.iterator();
        while (entityIterator.hasNext()) {
            Entity entity = entityIterator.next();

            if (!entity.isActive()) {
                Point gridPos = gameInstance.getGamePanel().getGridCellForEntity(entity);
                if (gridPos != null) {
                    gameInstance.getGamePanel().clearGridCell(gridPos.x, gridPos.y, entity);
                }
                entitiesToRemove.add(entity);
                continue;
            }

            entity.update();

            if (!entity.isActive()) {
                Point gridPos = gameInstance.getGamePanel().getGridCellForEntity(entity);
                if (gridPos != null) {
                    gameInstance.getGamePanel().clearGridCell(gridPos.x, gridPos.y, entity);
                }
                entitiesToRemove.add(entity);
            }
        }

        for (Entity deadEntity : entitiesToRemove) {
            entities.remove(deadEntity);
            if (deadEntity instanceof Titan) {
                int reward = (int) (TITAN_KILL_REWARD_BASE * (1.0 + (levelNumber - 1) * 0.1));
                gameInstance.addResources(reward);
            }
        }
        handleCollisions();
        checkWaveCompletion();
    }

    private void handleCollisions() {
        List<Entity> currentEntities = new ArrayList<>(entities);

        for (Entity entity : currentEntities) {
            if (entity instanceof Human && entity.isActive()) {
                Iterator<Projectile> projIterator = entity.getProjectiles().iterator();
                while (projIterator.hasNext()) {
                    Projectile p = projIterator.next();
                    if (!p.isActive()) continue;
                    for (Entity target : currentEntities) {
                        if (target.isActive() && (target instanceof Titan || target instanceof BlockerWall)) {
                            if (p.getBounds().intersects(target.getBounds())) {
                                target.takeDamage(p.getDamage());
                                p.setInactive();

                                break;
                            }
                        }
                    }
                }
            }
        }

        for (Entity entity : currentEntities) {
            if (entity instanceof Titan titan && entity.isActive()) {
                for (Entity target : currentEntities) {
                    if (target instanceof SpikeTrap trap && target.isActive()) {
                        if (!trap.isTriggered() && titan.getBounds().intersects(trap.getBounds())) {
                            trap.trigger(titan);
                        }
                    }
                }
            }
        }
    }

    private void checkWaveCompletion() {
        if (currentWave != null && currentWave.isSpawningComplete()) {
            boolean titansRemaining = entities.stream().anyMatch(e -> e instanceof Titan && e.isActive());
            if (!titansRemaining) {
                if (currentWaveIndex == waves.indexOf(currentWave)) {
                    int waveReward = (int) (WAVE_COMPLETE_REWARD_BASE * (1.0 + (levelNumber - 1) * 0.1 + currentWaveIndex * 0.05));
                    gameInstance.addResources(waveReward);
                    gameInstance.notifyObservers("Wave " + currentWave.getWaveNumber() + " cleared! +" + waveReward + " coins");

                    if (currentWaveIndex < waves.size() - 1) {
                        startNextWave();
                    } else {
                        levelComplete = true;
                        int levelReward = (int) (LEVEL_COMPLETE_REWARD_BASE * (1.0 + (levelNumber - 1) * 0.2));
                        gameInstance.addResources(levelReward);
                        gameInstance.notifyObservers("Level " + levelNumber + " complete! +" + levelReward + " coins");
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(gameInstance.getGamePanel(),
                                    "Congratulations! Level " + levelNumber + " passed!\n+" + levelReward + " coins!",
                                    "Level Complete", JOptionPane.INFORMATION_MESSAGE);
                        });
                        gameInstance.checkLevelCompletion();
                    }
                }
            }
        }
    }

    public void startNextWave() {
        currentWaveIndex++;
        if (currentWaveIndex < waves.size()) {
            currentWave = waves.get(currentWaveIndex);
            currentWave.startWave();
            gameInstance.notifyObservers("Wave " + currentWave.getWaveNumber() + " incoming!");
        }
    }

    public void addEntity(Entity entity) {
        if (entity != null) {
            entities.add(entity);
        }
    }

    public void draw(Graphics g) {
        if (humanBase != null) humanBase.draw(g);
        List<Entity> entitiesToDraw = new ArrayList<>(entities);
        for (Entity entity : entitiesToDraw) {
            if (entity instanceof BlockerWall || entity instanceof SpikeTrap) entity.draw(g);
        }
        for (Entity entity : entitiesToDraw) {
            if (entity instanceof Titan) entity.draw(g);
        }
        for (Entity entity : entitiesToDraw) {
            if (entity instanceof Human) entity.draw(g);
        }
    }

    public boolean isLevelComplete() {
        return levelComplete;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public int getLevelNumber() {
        return levelNumber;
    }

    public int getCurrentWaveNumber() {
        return (currentWave != null) ? currentWave.getWaveNumber() : 0;
    }

    public int getTotalWaves() {
        return waves.size();
    }

    public String getCurrentWaveStatus() {
        if (levelComplete) return "Level Complete";
        if (currentWave == null) return "Starting...";
        boolean titansExist = entities.stream().anyMatch(e -> e instanceof Titan && e.isActive());
        if (currentWave.isSpawningComplete()) {
            if (titansExist) return "Clear remaining titans!";
            else {
                if (currentWaveIndex >= waves.size() - 1) return "All waves cleared!";
                else return "Wave Cleared! Next wave soon...";
            }
        } else if (currentWave.isActive()) return "Spawning...";
        else return "Waiting...";
    }
}
