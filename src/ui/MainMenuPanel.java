package ui;

import game.Difficulty;
import game.Game;
import utils.ResourceLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class MainMenuPanel extends JPanel implements ActionListener {

    private GameFrame gameFrame;
    private JButton playButton;
    private JButton difficultyButton;
    private JButton quitButton;
    private JLabel titleLabel;
    private Font titleFont = new Font("Impact", Font.BOLD, 60); 
    private Font menuFont = new Font("Segoe UI", Font.BOLD, 36); 
    private Color menuColor = new Color(220, 220, 200);
    private Color selectedColor = Color.WHITE;
    private Color titleColor = new Color(180, 50, 50);
    private BufferedImage backgroundImage;

    public MainMenuPanel(GameFrame frame) {
        this.gameFrame = frame;
        setLayout(new GridBagLayout());
        setBackground(Color.BLACK);

        loadBackgroundImage();
        loadFonts(); 

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 10, 0);

        titleLabel = new JLabel("ATTACK ON TITAN: DEFENSE");
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(titleColor);
        gbc.insets = new Insets(40, 0, 60, 0);
        add(titleLabel, gbc);

        gbc.insets = new Insets(15, 0, 15, 0);
        playButton = createMenuButton("New Garrison");
        difficultyButton = createMenuButton("Select Difficulty (Not Implemented)"); 
        quitButton = createMenuButton("Quit Garrison");

        add(playButton, gbc);
        add(difficultyButton, gbc);
        add(quitButton, gbc);
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(menuFont);
        button.setForeground(menuColor);
        button.setOpaque(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.addActionListener(this);

        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { button.setForeground(selectedColor); }
            @Override public void mouseExited(MouseEvent e) { button.setForeground(menuColor); }
        });
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == playButton) {
            Game.getInstance().startGame();
            gameFrame.showGameView();
        } else if (source == difficultyButton) {
            Difficulty current = Game.getInstance().getCurrentDifficulty();
            Difficulty[] options = Difficulty.values();
            Difficulty choice = (Difficulty) JOptionPane.showInputDialog(
                    this,
                    "Select Garrison Difficulty:",
                    "Difficulty",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    current);

            if (choice != null) {
                Game.getInstance().setDifficulty(choice);
                difficultyButton.setText("Select Difficulty: " + choice.getDisplayName());
            }
        } else if (source == quitButton) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Abandon the Walls?",
                    "Quit Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }

    private void loadBackgroundImage(String imageName) {
        backgroundImage = ResourceLoader.loadImage("backgrounds/" + imageName);
        if (backgroundImage == null) {
            setBackground(Color.DARK_GRAY);
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (this.backgroundImage != null) {
            g.drawImage(this.backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private void loadFonts() {
        try {
            String fontPath = "/resources/fonts/AOT.ttf";
            InputStream is = getClass().getResourceAsStream(fontPath);
            if (is != null) {
                Font baseFont = Font.createFont(Font.TRUETYPE_FONT, is);
                titleFont = baseFont.deriveFont(Font.BOLD, 60f);
                menuFont = baseFont.deriveFont(Font.PLAIN, 36f);
                is.close();
            } else {
                loadFallbackFonts();
            }
        } catch (Exception e) {
            e.printStackTrace();
            loadFallbackFonts();
        }
    }
     private void loadFallbackFonts() {
        titleFont = new Font("Impact", Font.BOLD, 60);
        menuFont = new Font("Segoe UI", Font.BOLD, 36);
    }
}