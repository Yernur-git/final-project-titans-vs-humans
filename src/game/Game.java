import entities.HumanBase;
import levels.Level;
import patterns.factory.HumanFactory;
import patterns.factory.ObstacleFactory;
import patterns.factory.TitanFactory;
import patterns.observer.GameObserver;
import patterns.observer.GameSubject;
import patterns.state.*;
import ui.GamePanel;

import java.util.ArrayList;
import java.util.List;

public class Game implements GameSubject {
    private static Game instance;
    private GamePanel gamePanel;
    private Level currentLevel;
    private HumanBase humanBase;
    private List<GameObserver> observers = new ArrayList<>();

    private HumanFactory humanFactory;
    private TitanFactory titanFactory;
    private ObstacleFactory obstacleFactory;

    private GameState currentState;
    private Difficulty currentDifficulty = Difficulty.MEDIUM;

    private int resources = 200;
    private int currentLevelNumber = 0;

    private Game() {
        currentState = new NotStartedState(this);
        System.out.println("Game Singleton Initialized.");
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
            System.out.println("Game Initialized with GamePanel.");
        }
    }

    public void setState(GameState newState) {
        this.currentState = newState;
        System.out.println("Game State changed to: " + newState.getClass().getSimpleName());
    }

    public void startGame() { currentState.startGame(); }
    public void pauseGame() { currentState.pause(); }
    public void resumeGame() { currentState.resume(); }
    public void update() { currentState.update(); }
    public void gameOver(String reason) { currentState.loseGame(reason); }
    public void gameWin() { currentState.winGame(); }

    public void startNewLevel(int levelNumber) {
        System.out.println("Game: Attempting to start level " + levelNumber + " (Full logic later)");
        this.currentLevelNumber = levelNumber;
        System.out.println("Game: (Level object creation logic will be added later)");
    }

    public GameState getCurrentState() { return currentState; }
    public Difficulty getCurrentDifficulty() { return currentDifficulty; }
    public GamePanel getGamePanel() { return gamePanel; }
    public Level getCurrentLevel() { return currentLevel; }
    public HumanBase getHumanBase() { return humanBase; }
    public HumanFactory getHumanFactory() { return humanFactory; }
    public TitanFactory getTitanFactory() { return titanFactory; }
    public ObstacleFactory getObstacleFactory() { return obstacleFactory; }
    public long getPlacementCooldown() { return GameSettings.PLACEMENT_COOLDOWN_MILLIS; }

    @Override public void registerObserver(GameObserver observer) { observers.add(observer); }
    @Override public void removeObserver(GameObserver observer) { observers.remove(observer); }
    @Override public void notifyObservers(String message) { System.out.println("Game Notification: " + message); }
    @Override public void notifyResourceUpdate() { System.out.println("Game Notification: Resources updated"); }
    @Override public void notifyStateUpdate() { System.out.println("Game Notification: State updated"); }
}
