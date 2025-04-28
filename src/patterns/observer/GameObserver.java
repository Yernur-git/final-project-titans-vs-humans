package patterns.observer;


public interface GameObserver {
    void update(String message);
    void updateResources(int resources);
    void updateState(boolean isRunning, boolean isPaused);
}