package game;

import entities.HumanBase;
import levels.Level;
import levels.LevelDataStorage;
import patterns.factory.HumanFactory;
import patterns.factory.ObstacleFactory;
import patterns.factory.TitanFactory;
import patterns.observer.GameObserver;
import patterns.observer.GameSubject;
import patterns.state.GameState;
import patterns.state.NotStartedState;
import patterns.state.PausedState;
import patterns.state.RunningState;
import ui.GamePanel;

import java.util.ArrayList;
import java.util.List;

public class Game implements GameSubject {
    private static Game instance;
    private GamePanel gamePanel;
    private Level currentLevel;
    private HumanBase humanBase;
    private final List<GameObserver> observers = new ArrayList<>();

    private final HumanFactory humanFactory;
    private final TitanFactory titanFactory;
    private final ObstacleFactory obstacleFactory;


    private GameState currentState;
    private Difficulty currentDifficulty = Difficulty.MEDIUM;


    private int currentLevelNumber;
    private final int MAX_LEVELS = GameSettings.MAX_LEVELS;
    private final int BASE_HEALTH_START = GameSettings.BASE_HEALTH_START;
    private final int RESOURCE_INTERVAL = GameSettings.RESOURCE_INTERVAL_TICKS;
    private final int RESOURCE_AMOUNT_BASE = GameSettings.RESOURCE_AMOUNT_BASE;
    private final long PLACEMENT_COOLDOWN = GameSettings.PLACEMENT_COOLDOWN_MILLIS;

    private int resources;
    private long gameTick = 0;

    private Game() {
        humanFactory = new HumanFactory();
        titanFactory = new TitanFactory();
        obstacleFactory = new ObstacleFactory();
        currentState = new NotStartedState(this);
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
            notifyStateUpdate();
        }
    }

    public void setState(GameState newState) {
        this.currentState = newState;
        notifyStateUpdate();
    }

    public void startGame() {
        currentState.startGame();
    }

    public void pauseGame() {
        currentState.pause();
    }

    public void resumeGame() {
        currentState.resume();
    }

    public void update() {
        currentState.update();
    }

    public void gameOver(String reason) {
        currentState.loseGame(reason);
    }

    public void gameWin() {
        currentState.winGame();
    }

    public void resetGame() {
        setState(new NotStartedState(this));
        notifyObservers("Game Reset. Select difficulty and start.");
        this.resources = 0;
        notifyResourceUpdate();
        if (gamePanel != null) gamePanel.levelChanged(1);
    }

    public void tick() {
        if (!(currentState instanceof RunningState)) return;

        gameTick++;

        if (gameTick % RESOURCE_INTERVAL == 0) {
            int resourceGain = RESOURCE_AMOUNT_BASE + (currentLevelNumber * 2);
            addResources(resourceGain);
        }

        if (currentLevel != null) {
            currentLevel.update();
        }

        if (humanBase != null && humanBase.isDestroyed()) {
            gameOver("Your base was destroyed!");
            return;
        }

        if (gamePanel != null) {
            gamePanel.repaint();
        }
    }

    public void startNewLevel(int levelNumber) {
        int maxLevelsDefined = LevelDataStorage.getMaxLevelsDefined();
        if (levelNumber > MAX_LEVELS || levelNumber > maxLevelsDefined) {
            gameWin();
            return;
        }
        this.currentLevelNumber = levelNumber;

        double baseHealthMultiplier = currentDifficulty.getBaseHealthMultiplier();

        int scaledBaseHealth = (int) (BASE_HEALTH_START * Math.pow(1.1, levelNumber - 1) * baseHealthMultiplier);
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

    public void checkLevelCompletion() {
        if (currentLevel != null && currentLevel.isLevelComplete() && currentState instanceof RunningState) {
            if (currentLevelNumber < MAX_LEVELS) {
                startNewLevel(currentLevelNumber + 1);
            } else {
                gameWin();
            }
        }
    }

    public void addResources(int amount) {
        resources += amount;
        if (resources < 0) resources = 0;
        notifyResourceUpdate();
    }

    public int getResources() {
        return resources;
    }

    public void setDifficulty(Difficulty difficulty) {
        if (!(currentState instanceof RunningState || currentState instanceof PausedState)) {
            this.currentDifficulty = difficulty;
            notifyObservers("Difficulty set to " + difficulty);
        } else {
            notifyObservers("Cannot change difficulty while game is running or paused.");
        }
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public HumanBase getHumanBase() {
        return humanBase;
    }

    public GameState getCurrentState() {
        return currentState;
    }

    public Difficulty getCurrentDifficulty() {
        return currentDifficulty;
    }

    public int getCurrentLevelNumber() {
        return currentLevelNumber;
    }

    public int getGamePanelWidth() {
        return (gamePanel != null) ? gamePanel.getWidth() : 1200;
    }

    public GamePanel getGamePanel() {
        return gamePanel;
    }

    public HumanFactory getHumanFactory() {
        return humanFactory;
    }

    public TitanFactory getTitanFactory() {
        return titanFactory;
    }

    public ObstacleFactory getObstacleFactory() {
        return obstacleFactory;
    }

    public long getPlacementCooldown() {
        return PLACEMENT_COOLDOWN;
    }

    @Override
    public void registerObserver(GameObserver observer) {
        if (!observers.contains(observer)) observers.add(observer);
    }

    @Override
    public void removeObserver(GameObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String message) {
        for (GameObserver observer : observers) observer.update(message);
    }

    @Override
    public void notifyResourceUpdate() {
        int res = getResources();
        for (GameObserver observer : observers) observer.updateResources(res);
    }

    @Override
    public void notifyStateUpdate() {
        GameState state = getCurrentState();
        for (GameObserver observer : observers) observer.updateState(state);
    }
}
