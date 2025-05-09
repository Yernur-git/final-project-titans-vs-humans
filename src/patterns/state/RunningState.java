package patterns.state;

import game.Game;

public class RunningState implements GameState {
    private final Game game;

    public RunningState(Game game) {
        this.game = game;
        System.out.println("Game State: Running");
        game.notifyObservers("Game running.");
    }

    @Override
    public void startGame() {
        System.out.println("Game is already running.");
        game.notifyObservers("Game is already running.");
    }

    @Override
    public void pause() {
        System.out.println("Pausing game...");
        game.setState(new PausedState(game));
    }

    @Override
    public void resume() {
        System.out.println("Game is already running, cannot resume.");
    }

    @Override
    public void update() {

        game.tick();
    }

    @Override
    public void winGame() {
        System.out.println("Transitioning to Game Won state...");
        game.setState(new GameWonState(game));
    }

    @Override
    public void loseGame(String reason) {
        System.out.println("Transitioning to Game Over state: " + reason);
        game.setState(new GameOverState(game, reason));
    }

    @Override
    public void placeEntity(int x, int y) {


        if (game.getGamePanel() != null) {


        } else {
            System.err.println("Cannot place entity - GamePanel reference missing.");
        }
    }
}
