package game;

import entities.Entity;
import entities.HumanBase;
import entities.Titan;
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
import java.util.Iterator;
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
    private long lastRelocationTime = 0;
    private int relocationsUsedThisLevel = 0;

    private int resources;
    private long gameTick = 0;
    private long lastBombTime = 0;
    private List<BombEffect> activeBombEffects = new ArrayList<>();

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

    public boolean canUseBomb() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBombTime < GameSettings.Bomb_COOLDOWN_MILLIS) {
            long remaining = GameSettings.Bomb_COOLDOWN_MILLIS - (currentTime - lastBombTime);
            notifyObservers(String.format("Bomb on cooldown: %.1fs left.", remaining / 1000.0));
            return false;
        }
        return true;
    }

    public void performBomb(int clickX, int clickY) {
        if (!canUseBomb() || !(currentState instanceof RunningState) || currentLevel == null) {
            return;
        }

        lastBombTime = System.currentTimeMillis();
        notifyObservers("Bomb activated!");

        int affectedTitans = 0;
        List<Entity> entities = currentLevel.getEntities();
        List<Entity> targets = new ArrayList<>(entities);

        for (Entity entity : targets) {
            if (entity instanceof Titan && entity.isActive()) {
                Titan titan = (Titan) entity;
                int titanCenterX = titan.getX() + titan.getWidth() / 2;
                int titanCenterY = titan.getY() + titan.getHeight() / 2;
                double distance = Math.sqrt(Math.pow(titanCenterX - clickX, 2) + Math.pow(titanCenterY - clickY, 2));

                if (distance <= GameSettings.Bomb_RADIUS) {
                    titan.takeDamage(GameSettings.Bomb_DAMAGE);
                    affectedTitans++;
                }
            }
        }
        if (affectedTitans > 0) {
            System.out.println("Bomb hit " + affectedTitans + " titans.");
        }
        activeBombEffects.add(new BombEffect(clickX, clickY, GameSettings.Bomb_RADIUS,
                System.currentTimeMillis() + GameSettings.Bomb_EFFECT_DURATION_MILLIS));
        getGamePanel().repaint();
    }

    public void updateBombEffects() {
        Iterator<BombEffect> iterator = activeBombEffects.iterator();
        long currentTime = System.currentTimeMillis();
        while (iterator.hasNext()) {
            BombEffect effect = iterator.next();
            if (currentTime >= effect.getEndTime()) {
                iterator.remove();
            }
        }
    }

    public List<BombEffect> getActiveBombEffects() {
        return activeBombEffects;
    }

    public void tick() {
        if (!(currentState instanceof RunningState)) return;

        gameTick++;
        updateBombEffects();

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

    public long getRemainingBombCooldown() {
        if (lastBombTime == 0 && GameSettings.Bomb_COOLDOWN_MILLIS > 0) return 0;
        if (lastBombTime == 0 && GameSettings.Bomb_COOLDOWN_MILLIS <=0) return Long.MAX_VALUE;

        long elapsed = System.currentTimeMillis() - lastBombTime;
        return Math.max(0, GameSettings.Bomb_COOLDOWN_MILLIS - elapsed);
    }

    public static class BombEffect {
        private int x, y, radius;
        private long endTime;

        public BombEffect(int x, int y, int radius, long endTime) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.endTime = endTime;
        }

        public int getX() { return x; }
        public int getY() { return y; }
        public int getRadius() { return radius; }
        public long getEndTime() { return endTime; }
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

        this.relocationsUsedThisLevel = 0;
        this.lastRelocationTime = 0;

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

    public boolean canRelocateNow() {
        if (relocationsUsedThisLevel >= GameSettings.RELOCATIONS_PER_LEVEL_LIMIT) {
            notifyObservers("Relocation limit reached for this level.");
            return false;
        }
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRelocationTime < getRelocationCooldown()) {
            long remaining = getRelocationCooldown() - (currentTime - lastRelocationTime);
            notifyObservers(String.format("Relocation on cooldown: %.1fs left.", remaining / 1000.0));
            return false;
        }
        return true;
    }

    public void recordRelocation() {
        this.lastRelocationTime = System.currentTimeMillis();
        this.relocationsUsedThisLevel++;
    }


    public long getRemainingRelocationCooldown() {
        if (lastRelocationTime == 0) return 0;
        long elapsed = System.currentTimeMillis() - lastRelocationTime;
        return Math.max(0, getRelocationCooldown() - elapsed);
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

    public long getRelocationCooldown() { return GameSettings.RELOCATION_COOLDOWN_MILLIS; }

    public int getRelocationsAvailableThisLevel() { return GameSettings.RELOCATIONS_PER_LEVEL_LIMIT - relocationsUsedThisLevel; }

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