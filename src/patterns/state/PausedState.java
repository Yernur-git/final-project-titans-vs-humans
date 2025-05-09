package patterns.state;

import game.Game;

public class PausedState implements GameState {
    private final Game game;

    public PausedState(Game game) {
        this.game = game;
        System.out.println("Game State: Paused");
        game.notifyObservers("Game paused.");
    }

    @Override
    public void startGame() {
        System.out.println("Game is already started (paused).");
        game.notifyObservers("Game is paused. Press Resume.");
    }

    @Override
    public void pause() {
        System.out.println("Game is already paused.");
    }

    @Override
    public void resume() {
        System.out.println("Resuming game...");
        game.setState(new RunningState(game));
    }

    @Override
    public void update() {


        if (game.getGamePanel() != null) {
            game.getGamePanel().repaint();
        }
    }

    @Override
    public void winGame() {
        System.out.println("Cannot win game while paused.");
    }

    @Override
    public void loseGame(String reason) {
        System.out.println("Cannot lose game while paused. Resuming may trigger loss.");
    }

    @Override
    public void placeEntity(int x, int y) {
        game.notifyObservers("Cannot place units while paused.");
    }
}
