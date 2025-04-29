package game;

import entities.HumanBase;
import levels.Level;
import ui.GamePanel;
import javax.swing.*;

public class Game {
    private static Game instance;
    private GamePanel gamePanel;
    private Level currentLevel;
    private HumanBase humanBase;

    private int resources = 0;
    private int currentLevelNumber = 1;

    private Game() {

    }

    public static Game getInstance() {
        if (instance == null) {
            instance = new Game();
        }
        return instance;
    }

    public void initialize(GamePanel panel) {
        if (this.gamePanel == null) {
            this.gamePanel = panel;
            System.out.println("Game Initialized");
        }
    }

    public void startGame() {
        System.out.println("Starting Game...");
        this.resources = 100;
        this.humanBase = new HumanBase(1000);
        this.currentLevel = new Level(currentLevelNumber, this, null);
        if (gamePanel != null) gamePanel.repaint();
    }

    public void update() {
        if (currentLevel != null) {
            currentLevel.update();
        }

        if (humanBase != null && humanBase.isDestroyed()) {
            System.out.println("Game Over!");
        }

        if (gamePanel != null) {
            gamePanel.repaint();
        }
    }

    public void addResources(int amount) {
        resources += amount;
        if (resources < 0) resources = 0;
    }

    public int getResources() { return resources; }
    public HumanBase getHumanBase() { return humanBase; }
    public Level getCurrentLevel() { return currentLevel; }
    public GamePanel getGamePanel() { return gamePanel; }
}
