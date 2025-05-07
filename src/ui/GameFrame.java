package ui;

import game.Game;
import patterns.observer.GameObserver;
import patterns.state.GameState;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame implements GameObserver {
    private final MainMenuPanel mainMenuPanel;
    private final GamePanel gamePanel;
    private final MenuPanel menuPanel;
    private final JPanel gameViewPanel;
    private final CardLayout cardLayout;
    private final JPanel mainContainer;

    private static final String MAIN_MENU_VIEW = "MainMenu";
    private static final String GAME_VIEW = "GameView";

    public GameFrame() {
        super("Attack on Titan: Defense");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        mainMenuPanel = new MainMenuPanel(this);
        gamePanel = new GamePanel();
        menuPanel = new MenuPanel(gamePanel, this);

        gameViewPanel = new JPanel(new BorderLayout());
        gameViewPanel.add(gamePanel, BorderLayout.CENTER);
        gameViewPanel.add(menuPanel, BorderLayout.SOUTH);

        cardLayout = new CardLayout();
        mainContainer = new JPanel(cardLayout);

        mainContainer.add(mainMenuPanel, MAIN_MENU_VIEW);
        mainContainer.add(gameViewPanel, GAME_VIEW);

        add(mainContainer);

        Game.getInstance().registerObserver(this);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        showMainMenu();
    }

    public void showMainMenu() {
        cardLayout.show(mainContainer, MAIN_MENU_VIEW);
    }

    public void showGameView() {
        cardLayout.show(mainContainer, GAME_VIEW);
        gamePanel.requestFocusInWindow();
    }

    @Override
    public void update(String message) {
    }

    @Override
    public void updateResources(int resources) {
    }

    @Override
    public void updateState(GameState currentState) {
        String title = "Titans vs Humans Defense";
        String stateName = currentState.getClass().getSimpleName().replace("State", "");
        if (!stateName.equals("Running") && !stateName.equals("NotStarted")) {
            title += " - " + stateName;
        }
        setTitle(title);
    }
}
