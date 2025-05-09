package patterns.state;

import game.Game;

import javax.swing.*;

public class GameOverState implements GameState {
    private Game game;
    private String reason;

    public GameOverState(Game game, String reason) {
        this.game = game;
        this.reason = reason;
        System.out.println("Game Over State Initialized: " + reason);
        displayGameOverMessage();
    }

    private void displayGameOverMessage() {

        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(game.getGamePanel(),
                    "Game Over!\n" + reason,
                    "Defeat", JOptionPane.INFORMATION_MESSAGE);

        });
    }

    @Override
    public void startGame() {
        System.out.println("Cannot start game from Game Over state. Reset first.");
        game.notifyObservers("Game is over. Reset to start a new game.");
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void update() {


        if (game.getGamePanel() != null) {
            game.getGamePanel().repaint();
        }
    }

    @Override
    public void winGame() {
        System.out.println("Cannot win game from Game Over state.");
    }

    @Override
    public void loseGame(String reason) {
        System.out.println("Already in Game Over state.");
    }

    @Override
    public void placeEntity(int x, int y) {
        game.notifyObservers("Cannot place units, game is over.");
    }
}
