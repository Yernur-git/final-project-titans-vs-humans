package ui;

import game.Game;
import patterns.observer.GameObserver;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame implements GameObserver { 

    private MainMenuPanel mainMenuPanel;
    private GamePanel gamePanel;
    private JPanel gameViewPanel;

    private CardLayout cardLayout;
    private JPanel mainContainer;

    private static final String MAIN_MENU_VIEW = "MainMenu";
    private static final String GAME_VIEW = "GameView";

    public GameFrame() {
        super("Attack on Titan: Defense");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        mainMenuPanel = new MainMenuPanel(this);
        gamePanel = new GamePanel();

        gameViewPanel = new JPanel(new BorderLayout());

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
    }
}