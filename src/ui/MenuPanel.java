package ui;

import game.Game;
import patterns.observer.GameObserver;

import javax.swing.*;
import java.awt.*;
public class MenuPanel extends JPanel implements GameObserver {

    private Game game;
    private GamePanel gamePanel;
    private GameFrame gameFrame;

    private JLabel statusLabel;

    public MenuPanel(GamePanel gamePanel, GameFrame gameFrame) {
        this.game = Game.getInstance();
        this.gamePanel = gamePanel;
        this.gameFrame = gameFrame;
        this.game.registerObserver(this);

        setLayout(new FlowLayout(FlowLayout.LEFT));
        setBackground(new Color(210, 180, 140, 180)); 
        setPreferredSize(new Dimension(1200, 40));

        statusLabel = new JLabel("Status: Ready for orders...");
        add(statusLabel);

    }
}