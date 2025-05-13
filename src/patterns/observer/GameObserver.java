package patterns.observer;

import patterns.state.GameState;

public interface GameObserver {
    void update(String message);
    void updateResources(int currentResources);
    void updateState(GameState currentState);
}
