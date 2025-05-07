package ui;

import entities.EntityTypeData;
import entities.HumanBase;
import game.Game;
import levels.Level;
import patterns.observer.GameObserver;
import patterns.state.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPanel extends JPanel implements GameObserver, ActionListener {

    private final Game game;
    private final GamePanel gamePanel;
    private final GameFrame gameFrame;


    private final JLabel statusLabel;
    private final JLabel resourceLabel;
    private final JLabel waveLabel;
    private final JLabel cooldownLabel;
    private final JLabel baseHealthLabel;
    private final JLabel currentDifficultyLabel;
    private final JComboBox<Object> entityTypeCombo;
    private final JRadioButton radioBasic;
    private final JRadioButton radioArmored;
    private final JRadioButton radioVeteran;
    private final ButtonGroup decoratorGroup;
    private final JPanel decoratorPanel;
    private final JButton placeButton;
    private final JButton startPauseResumeButton;
    private final JButton resetButton;

    private final Timer uiUpdateTimer;


    public MenuPanel(GamePanel gamePanel, GameFrame gameFrame) {
        this.game = Game.getInstance();
        this.gamePanel = gamePanel;
        this.gameFrame = gameFrame;
        this.game.registerObserver(this);


        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 5));
        setBackground(new Color(210, 180, 140));
        setPreferredSize(new Dimension(1200, 55));


        currentDifficultyLabel = new JLabel("Difficulty: " + game.getCurrentDifficulty().getDisplayName());
        add(currentDifficultyLabel);
        add(Box.createHorizontalStrut(15));


        add(new JLabel("Build:"));
        entityTypeCombo = new JComboBox<>();
        for (EntityTypeData.HumanType type : EntityTypeData.HumanType.values()) {
            entityTypeCombo.addItem(type);
        }
        for (EntityTypeData.ObstacleType type : EntityTypeData.ObstacleType.values()) {
            entityTypeCombo.addItem(type);
        }
        entityTypeCombo.addActionListener(this);
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
        resetButton.addActionListener(this);
        add(resetButton);
        add(Box.createHorizontalStrut(15));


        Font statusFont = new Font("Serif", Font.BOLD, 14);
        resourceLabel = new JLabel("Coins: 0");
        resourceLabel.setFont(statusFont);
        add(resourceLabel);
        add(Box.createHorizontalStrut(10));
        baseHealthLabel = new JLabel("Wall HP: ---/---");
        baseHealthLabel.setFont(statusFont);
        add(baseHealthLabel);
        add(Box.createHorizontalStrut(10));
        waveLabel = new JLabel("Wave: -/-");
        waveLabel.setFont(statusFont);
        add(waveLabel);
        add(Box.createHorizontalStrut(10));
        cooldownLabel = new JLabel("CD: N/A");
        cooldownLabel.setFont(new Font("Serif", Font.PLAIN, 12));
        add(cooldownLabel);
        add(Box.createHorizontalStrut(10));
        statusLabel = new JLabel("Report:");
        statusLabel.setFont(new Font("Serif", Font.PLAIN, 12));
        add(statusLabel);


        if (game.getCurrentState() != null) {
            updateStateUI(game.getCurrentState());
        } else {
            updateStateUI(new NotStartedState(game));
        }
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
            Object selectedItem = entityTypeCombo.getSelectedItem();
            String decoratorType = "None";

            if (selectedItem instanceof EntityTypeData.HumanType) {
                decoratorType = decoratorGroup.getSelection().getActionCommand();
                gamePanel.enterPlacementMode(selectedItem, decoratorType);
            } else if (selectedItem instanceof EntityTypeData.ObstacleType) {
                gamePanel.enterPlacementMode(selectedItem, "None");
            } else {
                System.err.println("Unknown type selected in ComboBox: " + selectedItem);
                return;
            }
            updateCooldownDisplay();

        } else if (source == startPauseResumeButton) {

            GameState currentState = game.getCurrentState();
            if (currentState instanceof RunningState) {
                game.pauseGame();
            } else if (currentState instanceof PausedState) {
                game.resumeGame();
            }
        } else if (source == resetButton) {

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to retreat from this mission?",
                    "Confirm Retreat",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                game.resetGame();
                gameFrame.showMainMenu();
            }
        } else if (source == entityTypeCombo) {
            updateDecoratorPanelVisibility();
        }
    }


    private void updateDecoratorPanelVisibility() {
        Object selected = entityTypeCombo.getSelectedItem();
        boolean isHuman = selected instanceof EntityTypeData.HumanType;
        decoratorPanel.setVisible(isHuman);
        if (!isHuman) {
            radioBasic.setSelected(true);
        }
    }


    private void updateCooldownDisplay() {
        if (!(game.getCurrentState() instanceof RunningState)) {
            cooldownLabel.setText("CD: N/A");
            cooldownLabel.setForeground(Color.GRAY);
            return;
        }
        long remaining = gamePanel.getRemainingCooldown();
        if (remaining <= 0) {
            cooldownLabel.setText("CD: Ready");
            cooldownLabel.setForeground(new Color(0, 100, 0));
        } else {
            cooldownLabel.setText(String.format("CD: %.1fs", remaining / 1000.0));
            cooldownLabel.setForeground(Color.RED);
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


    private void updateStateUI(GameState currentState) {
        boolean isRunning = currentState instanceof RunningState;
        boolean isPaused = currentState instanceof PausedState;
        boolean isNotStarted = currentState instanceof NotStartedState;
        boolean isGameOver = currentState instanceof GameOverState || currentState instanceof GameWonState;

        placeButton.setEnabled(isRunning);
        startPauseResumeButton.setEnabled(isRunning || isPaused);
        resetButton.setEnabled(!isNotStarted);


        if (isRunning) {
            startPauseResumeButton.setText("Pause");
        } else if (isPaused) {
            startPauseResumeButton.setText("Resume");
        } else if (isGameOver) {
            startPauseResumeButton.setText("Mission End");
            startPauseResumeButton.setEnabled(false);
            placeButton.setEnabled(false);
        } else {
            startPauseResumeButton.setText("---");
            startPauseResumeButton.setEnabled(false);
            placeButton.setEnabled(false);
        }


        currentDifficultyLabel.setText("Difficulty: " + game.getCurrentDifficulty().getDisplayName());


        if (statusLabel.getText().equals("Report:") || statusLabel.getText().startsWith("Report: Level")) {
            if (isPaused) statusLabel.setText("Report: Mission Paused.");
            else if (isRunning) statusLabel.setText("Report: Defend the Wall!");
            else if (isGameOver)
                statusLabel.setText(currentState instanceof GameWonState ? "Report: Victory Achieved!" : "Report: Mission Failed...");
            else statusLabel.setText("Report: Awaiting Orders...");
        }

        updateDecoratorPanelVisibility();
        updateCooldownDisplay();
        updateBaseHealthLabel();
        updateWaveLabel();
    }


    private void updateBaseHealthLabel() {
        HumanBase base = game.getHumanBase();
        if (base != null && base.getCurrentMaxHealth() > 0) {
            baseHealthLabel.setText(String.format("Wall HP: %d/%d", base.getHealth(), base.getCurrentMaxHealth()));
            double healthRatio = (double) base.getHealth() / base.getCurrentMaxHealth();
            if (healthRatio <= 0.0) baseHealthLabel.setForeground(Color.BLACK);
            else if (healthRatio < 0.3) baseHealthLabel.setForeground(Color.RED);
            else if (healthRatio < 0.6) baseHealthLabel.setForeground(Color.ORANGE);
            else baseHealthLabel.setForeground(new Color(0, 100, 0));
        } else {
            baseHealthLabel.setText("Wall HP: ---/---");
            baseHealthLabel.setForeground(Color.GRAY);
        }
    }


    private void updateWaveLabel() {
        Level currentLevel = game.getCurrentLevel();
        GameState currentState = game.getCurrentState();
        String waveText = "Wave: -/-";

        if (currentLevel != null && (currentState instanceof RunningState || currentState instanceof PausedState)) {
            String statusText = currentLevel.getCurrentWaveStatus();
            waveText = String.format("Lvl: %d Wave: %d/%d (%s)",
                    currentLevel.getLevelNumber(),
                    currentLevel.getCurrentWaveNumber(),
                    currentLevel.getTotalWaves(),
                    statusText);
        } else if (currentState instanceof GameWonState) {
            waveText = "Status: Victorious!";
        } else if (currentState instanceof GameOverState) {
            waveText = "Status: Defeated";
        } else {
            waveText = "Status: Waiting...";
        }


        if (!waveLabel.getText().equals(waveText)) {
            waveLabel.setText(waveText);
        }
    }
}
