package patterns.state;

import game.Game;

import javax.swing.*;

public class GameWonState implements GameState {
    private final Game game;
    private boolean messageShown = false;

    public GameWonState(Game game) {
        this.game = game;
        System.out.println("Game Won State Initialized");
        grantWinReward();
        displayWinMessage();
    }

    private void grantWinReward() {
        int finalReward = 500 + game.getCurrentLevelNumber() * 50;
        game.addResources(finalReward);
        game.notifyObservers("Congratulations! You defeated all titans! +" + finalReward + " coins");
    }

    private void displayWinMessage() {
        if (!messageShown) {
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(game.getGamePanel(),
                        "Victory!\nYou have defended humanity and defeated all Titan waves!",
                        "Congratulations!", JOptionPane.INFORMATION_MESSAGE);
            });
            messageShown = true;
        }
    }

    @Override
    public void startGame() {
        System.out.println("Game already won. Reset to play again.");
        game.notifyObservers("Game already won. Reset to start a new game.");
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
        System.out.println("Already in Game Won state.");
    }

    @Override
    public void loseGame(String reason) {
        System.out.println("Cannot lose game from Game Won state.");
    }

    @Override
    public void placeEntity(int x, int y) {
        game.notifyObservers("Cannot place units, game is won.");
    }
}
