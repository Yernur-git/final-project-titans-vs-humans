package levels;

import entities.*;
import game.Difficulty;
import game.Game;
import game.GameSettings;
import gameobjects.Projectile;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Level {
    // ... существующие поля ...

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
                Point gridPos = getGridPosition(entity);
                if (gridPos != null) {
                    gameInstance.getGamePanel().clearGridCell(gridPos.x, gridPos.y, entity);
                }
                entitiesToRemove.add(entity);
                continue;
            }
            entity.update();
            if (!entity.isActive()) {
                Point gridPos = getGridPosition(entity);
                if (gridPos != null) {
                    gameInstance.getGamePanel().clearGridCell(gridPos.x, gridPos.y, entity);
                }
                entitiesToRemove.add(entity);
            }
        }

        for (Entity deadEntity : entitiesToRemove) {
            entities.remove(deadEntity);
            if (deadEntity instanceof Titan) {
                int reward = (int) (GameSettings.TITAN_KILL_REWARD_BASE * (1.0 + (levelNumber - 1) * 0.1));
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
                List<Projectile> projectiles = new ArrayList<>(entity.getProjectiles());
                Iterator<Projectile> projIterator = projectiles.iterator();
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
        if (currentWave != null && currentWave.isSpawningComplete() && !levelComplete) {
            boolean titansRemaining = entities.stream().anyMatch(e -> e instanceof Titan && e.isActive());
            if (!titansRemaining) {
                if (currentWaveIndex == waves.indexOf(currentWave)) {
                    System.out.println("Level " + levelNumber + ", Wave " + currentWave.getWaveNumber() + " cleared!");
                    int waveReward = (int)(GameSettings.WAVE_COMPLETE_REWARD_BASE * (1.0 + (levelNumber - 1) * 0.1 + currentWaveIndex * 0.05));
                    gameInstance.addResources(waveReward);
                    gameInstance.notifyObservers("Wave " + currentWave.getWaveNumber() + " cleared! +" + waveReward + " coins");

                    if (currentWaveIndex < waves.size() - 1) {
                        startNextWave();
                    } else {
                        levelComplete = true;
                        int levelReward = (int)(GameSettings.LEVEL_COMPLETE_REWARD_BASE * (1.0 + (levelNumber - 1) * 0.2));
                        gameInstance.addResources(levelReward);
                        gameInstance.notifyObservers("Level " + levelNumber + " complete! +" + levelReward + " coins");

                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(gameInstance.getGamePanel(),
                                    "Congratulations! Level " + levelNumber + " Secured!\n+" + levelReward + " coins!",
                                    "Victory!", JOptionPane.INFORMATION_MESSAGE);
                        });
                        gameInstance.checkLevelCompletion();
                    }
                }
            }
        }
    }

    // ... остальные методы ...
}
