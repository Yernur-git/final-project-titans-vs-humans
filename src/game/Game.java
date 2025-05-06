package game;

import patterns.state.*;
import levels.LevelDataStorage;

public class Game implements GameSubject {
    private HumanBase humanBase;
    private Level currentLevel;
    private int currentLevelNumber;
    private int resources;
    private int gameTick;
    private GamePanel gamePanel;
    private GameState currentState;
    private HumanFactory humanFactory;
    private TitanFactory titanFactory;
    private ObstacleFactory obstacleFactory;
    private Difficulty currentDifficulty;

    public void tick() {
        if (!(currentState instanceof RunningState)) {
            if (gamePanel != null && (currentState instanceof PausedState || currentState instanceof GameOverState || currentState instanceof GameWonState)) {
                gamePanel.repaint();
            }
            return;
        }

        gameTick++;

        if (gameTick % GameSettings.RESOURCE_INTERVAL_TICKS == 0) {
            int resourceGain = GameSettings.RESOURCE_AMOUNT_BASE + (currentLevelNumber * 2);
            addResources(resourceGain);
        }

        if (currentLevel != null) {
            currentLevel.update();
        } else {
            System.err.println("Error: Game running but currentLevel is null!");
        }

        if (humanBase != null && humanBase.isDestroyed()) {
            gameOver("Your base (Wall Maria?) has been destroyed!");
            return;
        }

        if (gamePanel != null) {
            gamePanel.repaint();
        }
    }

    public void addResources(int amount) {
        if (amount == 0) return;
        resources += amount;
        if (resources < 0) resources = 0;
        System.out.println("Resources changed by " + amount + ". Current: " + resources);
        notifyResourceUpdate();
    }

    public void startNewLevel(int levelNumber) {
        int maxLevelsDefined = LevelDataStorage.getMaxLevelsDefined();
        if (levelNumber > GameSettings.MAX_LEVELS || levelNumber > maxLevelsDefined || LevelDataStorage.getLevelInfo(levelNumber) == null) {
            System.out.println("Attempting to start level beyond max defined/available: " + levelNumber);
            gameWin();
            return;
        }
        System.out.println("Starting Level " + levelNumber + " on " + currentDifficulty + " difficulty.");
        this.currentLevelNumber = levelNumber;

        double baseHealthMultiplier = currentDifficulty.getBaseHealthMultiplier();
        int scaledBaseHealth = (int) (GameSettings.BASE_HEALTH_START * Math.pow(1.1, levelNumber - 1) * baseHealthMultiplier);
        this.humanBase = new HumanBase(scaledBaseHealth);

        if (levelNumber == 1) {
            double resourceStartMultiplier = currentDifficulty.getResourceStartMultiplier();
            this.resources = (int) (GameSettings.STARTING_RESOURCES_MEDIUM * resourceStartMultiplier);
        }

        this.currentLevel = new Level(levelNumber, this, currentDifficulty);
        gameTick = 0;

        notifyObservers("Level " + levelNumber + " started (" + currentDifficulty + ")");
        notifyResourceUpdate();
        if (gamePanel != null) gamePanel.levelChanged(levelNumber);
    }

    public Game() {
        humanFactory = new HumanFactory();
        titanFactory = new TitanFactory();
        obstacleFactory = new ObstacleFactory();
        currentState = new NotStartedState(this);
        System.out.println("Game Singleton Initialized with Factories.");
    }

    public HumanFactory getHumanFactory() { return humanFactory; }
    public TitanFactory getTitanFactory() { return titanFactory; }
    public ObstacleFactory getObstacleFactory() { return obstacleFactory; }
}
