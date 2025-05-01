package ui;

import game.Game;
import utils.ResourceLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;

public class GamePanel extends JPanel implements ActionListener {

    private Game game;
    private Timer gameTimer; 
    private BufferedImage background; 

    private static final int CELL_WIDTH = 60;
    private static final int CELL_HEIGHT = 60;
    private static final int GRID_ROWS = 5;
    private static final int GRID_COLS = 7;
    private static final int GRID_START_X = 120;
    private static final int[] LANE_Y_CENTERS = {150, 230, 310, 390, 470};


    public GamePanel() {
        this.game = Game.getInstance();
        setPreferredSize(new Dimension(1200, 600));
        loadBackground("default_bg.png"); 

        gameTimer = new Timer(16, this);
        gameTimer.start();

        game.initialize(this); 
        setFocusable(true);
    }

    private void loadBackground(String imageName) {
        background = ResourceLoader.loadImage("backgrounds/" + imageName);
        if (background == null) {
            System.err.println("GamePanel: Failed to load background: " + imageName + ".");
            setBackground(Color.DARK_GRAY); 
        }
        repaint();
    }

    public void levelChanged(int levelNumber) {
        String bgName = levelNumber + "_bg.png";
        BufferedImage newBg = ResourceLoader.loadImage("backgrounds/" + bgName);
        if (newBg != null) {
            this.background = newBg;
        } else {
            System.err.println("GamePanel: Background for level " + levelNumber + " not found (" + bgName + "). Using default.");
            loadBackground("default_bg.png");
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        if (background != null) {
            g2d.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2d.setColor(Color.DARK_GRAY);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        paintGrid(g2d);

    }

     private void paintGrid(Graphics2D g2d) {
         for (int row = 0; row < GRID_ROWS; row++) {
             for (int col = 0; col < GRID_COLS; col++) {
                 int cellX = GRID_START_X + col * CELL_WIDTH;
                 int cellY = LANE_Y_CENTERS[row] - CELL_HEIGHT / 2;
                 g2d.setColor(new Color(255, 255, 255, 20));
                 g2d.fillRect(cellX, cellY, CELL_WIDTH, CELL_HEIGHT);
                 g2d.setColor(new Color(255, 255, 255, 40));
                 g2d.drawRect(cellX, cellY, CELL_WIDTH, CELL_HEIGHT);
             }
         }
     }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        game.update();
    }

}