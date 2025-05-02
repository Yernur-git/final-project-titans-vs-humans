package ui;

import entities.EntityTypeData;
import entities.HumanBase;
import game.Game;
import patterns.observer.GameObserver;
import patterns.state.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*; 

public class MenuPanel extends JPanel implements GameObserver, ActionListener {

    private Game game;
    private GamePanel gamePanel;
    private GameFrame gameFrame;
    private JLabel statusLabel;
    private JLabel resourceLabel;
    private JLabel waveLabel;
    private JLabel cooldownLabel;
    private JLabel baseHealthLabel;
    private JLabel currentDifficultyLabel;
    private JComboBox<Object> entityTypeCombo;
    private JRadioButton radioBasic, radioArmored, radioVeteran;
    private ButtonGroup decoratorGroup;
    private JPanel decoratorPanel;
    private JButton placeButton, startPauseResumeButton, resetButton;
    private Timer uiUpdateTimer;

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

         currentDifficultyLabel = new JLabel("Difficulty: ..."); 
         add(currentDifficultyLabel); 
         add(Box.createHorizontalStrut(15));

         add(new JLabel("Build:")); 
         entityTypeCombo = new JComboBox<>(); 
         add(entityTypeCombo);
         decoratorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); 
         decoratorPanel.setOpaque(false);
         decoratorPanel.add(new JLabel("Upgrade:"));
         radioBasic = new JRadioButton("None", true);
         radioBasic.setActionCommand("None");
         radioBasic.setOpaque(false);
         radioArmored = new JRadioButton("Armor");
         radioArmored.setActionCommand("Armored");
         radioArmored.setOpaque(false);
         radioVeteran = new JRadioButton("Veteran");
         radioVeteran.setActionCommand("Veteran");
         radioVeteran.setOpaque(false);
         decoratorGroup = new ButtonGroup();
         decoratorGroup.add(radioBasic);
         decoratorGroup.add(radioArmored);
         decoratorGroup.add(radioVeteran);
         decoratorPanel.add(radioBasic);
         decoratorPanel.add(radioArmored);
         decoratorPanel.add(radioVeteran);
         add(decoratorPanel);
         placeButton = new JButton("Place Unit"); 
         placeButton.addActionListener(this); 
         add(placeButton);
         startPauseResumeButton = new JButton("Pause"); 
         startPauseResumeButton.addActionListener(this); 
         add(startPauseResumeButton);
         resetButton = new JButton("Retreat"); 
         resetButton.addActionListener(this); add(resetButton); 
         add(Box.createHorizontalStrut(15));
         resourceLabel = new JLabel("Coins: 0");
         add(resourceLabel); 
         add(Box.createHorizontalStrut(10));
         baseHealthLabel = new JLabel("Wall HP: ---/---"); 
         add(baseHealthLabel); 
         add(Box.createHorizontalStrut(10));
         waveLabel = new JLabel("Wave: -/-");
         add(waveLabel); 
         add(Box.createHorizontalStrut(10));
         cooldownLabel = new JLabel("CD: N/A"); 
         add(cooldownLabel); 
         add(Box.createHorizontalStrut(10));
         statusLabel = new JLabel("Report:");   
         add(statusLabel);

        if (game.getCurrentState() != null) updateStateUI(game.getCurrentState());
        else updateStateUI(new NotStartedState(game)); 
        updateResources(game.getResources()); 
        updateBaseHealthLabel(); 
        updateDecoratorPanelVisibility(); 

        uiUpdateTimer = new Timer(200, e -> {
            updateCooldownDisplay();
            updateBaseHealthLabel();
            updateWaveLabel();
        });
        uiUpdateTimer.start();

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == placeButton) { 
            if (!(game.getCurrentState() instanceof RunningState)) {
                 game.notifyObservers("Can only place units while game is running!");
                 return;
            }
            Object selectedItem = entityTypeCombo.getSelectedItem();
            String decoratorType = "None";
            if (selectedItem instanceof EntityTypeData.HumanType) {
                 decoratorType = decoratorGroup.getSelection().getActionCommand();
                 gamePanel.enterPlacementMode(selectedItem, decoratorType);
            } else if (selectedItem instanceof EntityTypeData.ObstacleType) {
                 gamePanel.enterPlacementMode(selectedItem, "None");
            } else { return; } 
            updateCooldownDisplay();

        } else if (source == startPauseResumeButton) { 
            GameState currentState = game.getCurrentState();
            if (currentState instanceof RunningState) game.pauseGame();
            else if (currentState instanceof PausedState) game.resumeGame();

        } else if (source == resetButton) { 
            int confirm = JOptionPane.showConfirmDialog(this, "Retreat?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                game.resetGame();
                gameFrame.showMainMenu();
            }
        } else if (source == entityTypeCombo) { 
            updateDecoratorPanelVisibility();
        }
    }

    @Override
    public void update(String message) {
        if (message != null && !message.isEmpty()) {
            if (!message.startsWith("Placement mode active")) {
                statusLabel.setText("Report: " + message);
            } else {
                statusLabel.setText(message); 
            }
        }
    }

    @Override
    public void updateResources(int resources) {
        resourceLabel.setText("Coins: " + resources);
    }

    @Override
    public void updateState(GameState currentState) {
        SwingUtilities.invokeLater(() -> updateStateUI(currentState));
    }
}