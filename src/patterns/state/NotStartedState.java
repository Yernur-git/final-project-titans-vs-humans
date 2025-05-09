package patterns.state;

import game.Game;

public class NotStartedState implements GameState {
    private final Game game;

    public NotStartedState(Game game) {
        this.game = game;
        System.out.println("Game State: Not Started");
        game.notifyObservers("Select difficulty and press Start Game.");
    }

    @Override
    public void startGame() {
        System.out.println("Starting game...");

        game.startNewLevel(1);

        game.setState(new RunningState(game));
    }

    @Override
    public void pause() {
        System.out.println("Game not started, cannot pause.");
        game.notifyObservers("Game must be started before pausing.");
    }

    @Override
    public void resume() {
        System.out.println("Game not started, cannot resume.");
    }

    @Override
    public void update() {


        if (game.getGamePanel() != null) {
            game.getGamePanel().repaint();
        }
    }

    @Override
    public void winGame() {
        System.out.println("Game not started, cannot win.");
    }

    @Override
    public void loseGame(String reason) {
        System.out.println("Game not started, cannot lose.");
    }

    @Override
    public void placeEntity(int x, int y) {
        game.notifyObservers("Cannot place units, game not started.");
    }
}
