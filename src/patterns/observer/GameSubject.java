package patterns.observer;

public interface GameSubject {
    void registerObserver(GameObserver observer);
    void removeObserver(GameObserver observer);
    void notifyObservers(String message);
    void notifyResourceUpdate();
    void notifyStateUpdate();
}
