package ui;

import entities.*;
import game.Game;
import patterns.decorator.ArmoredHuman; 
import patterns.decorator.VeteranHuman;

import java.awt.*;
import java.awt.event.*; 
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import utils.ResourceLoader;


public class GamePanel extends JPanel implements ActionListener {

    private Game game;
    private Timer gameTimer; 
    private BufferedImage background; 
    private boolean placementMode = false;
    private Object selectedEnumType; 
    private String selectedDecoratorType;
    private long lastPlacementTime = 0;
    private final long PLACEMENT_COOLDOWN;
    private Map<Point, Entity> placementGrid = new HashMap<>();
    private Point highlightedCell = null;


    private static final int CELL_WIDTH = 60;
    private static final int CELL_HEIGHT = 60;
    private static final int GRID_ROWS = 5;
    private static final int GRID_COLS = 7;
    private static final int GRID_START_X = 120;
    private static final int[] LANE_Y_CENTERS = {150, 230, 310, 390, 470};


    public GamePanel() {
        this.game = Game.getInstance();
        this.PLACEMENT_COOLDOWN = game.getPlacementCooldown()

        setPreferredSize(new Dimension(1200, 600));
        loadBackground("default_bg.png"); 

        gameTimer = new Timer(16, this);
        gameTimer.start();

        setupMouseListener();
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

    public void enterPlacementMode(Object enumType, String decorator) {
        if (game.getCurrentState() instanceof RunningState) {
            this.selectedEnumType = enumType;
            this.selectedDecoratorType = decorator;
            this.placementMode = true;
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

            String displayName = getDisplayNameForEnumType(enumType); 
            game.notifyObservers("Placement mode active for: " + displayName + ". Click on the grid.");
        } else {
            game.notifyObservers("Cannot enter placement mode when game is not running.");
        }
    }

    private void cancelPlacementMode(String reason) {
        placementMode = false;
        highlightedCell = null;
        setCursor(Cursor.getDefaultCursor());
        if (reason != null && !reason.isEmpty()) {
            game.notifyObservers(reason);
        }
        repaint();
    }

     private void handlePlacementAttempt(int mouseX, int mouseY) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPlacementTime < PLACEMENT_COOLDOWN) {
            cancelPlacementMode(String.format("Cooldown: %.1fs", (PLACEMENT_COOLDOWN - (currentTime - lastPlacementTime)) / 1000.0));
            return;
        }
        Point cell = getGridCellFromMouse(mouseX, mouseY);
        if (cell == null) { cancelPlacementMode("Invalid placement area!"); return; }
        if (placementGrid.containsKey(cell) && placementGrid.get(cell).isActive()) {
            cancelPlacementMode("Cell occupied!"); return;
        }

        Entity entityToPlace = createEntityForPlacement(cell); 
        if (entityToPlace == null) return;

        int totalCost = getTotalCostForSelectedEntity(); 
        if (totalCost < 0) { cancelPlacementMode("Cost error."); return; }

        String displayName = getDisplayNameForEnumType(selectedEnumType);

        if (game.getResources() >= totalCost) {
            game.addResources(-totalCost);
            game.getCurrentLevel().addEntity(entityToPlace);
            placementGrid.put(cell, entityToPlace);
            lastPlacementTime = currentTime;
            game.notifyObservers(displayName + " placed!");
        } else {
            game.notifyObservers("Need " + totalCost + " Coins!");
        }
        placementMode = false;
        highlightedCell = null;
        setCursor(Cursor.getDefaultCursor());
        repaint();
    }

    private Point getGridCellFromMouse(int x, int y) {
        if (x < GRID_START_X || x >= GRID_START_X + GRID_COLS * CELL_WIDTH) {
            return null;
        }
        int col = (x - GRID_START_X) / CELL_WIDTH;

        int closestRow = -1;
        int minDistanceY = Integer.MAX_VALUE;

        for (int row = 0; row < GRID_ROWS; row++) {
            int distanceY = Math.abs(y - LANE_Y_CENTERS[row]);
            if (distanceY < minDistanceY) {
                minDistanceY = distanceY;
                closestRow = row;
            }
        }
        if (closestRow != -1 && minDistanceY <= LANE_HEIGHT_TOLERANCE) {
            if (col >= 0 && col < GRID_COLS) {
                return new Point(closestRow, col);
            }
        }
        return null;
    }

    public Point getGridCellForEntity(Entity entity) {
        for (Map.Entry<Point, Entity> entry : placementGrid.entrySet()) {
            if (entry.getValue() == entity) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void clearGridCell(int row, int col, Entity entity) {
        Point cell = new Point(row, col);
        if (placementGrid.containsKey(cell) && placementGrid.get(cell) == entity) {
            placementGrid.remove(cell);
        }
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

        for (int row = 0; row < GRID_ROWS; row++) {
            for (int col = 0; col < GRID_COLS; col++) {
                int cellX = GRID_START_X + col * CELL_WIDTH;
                int cellY = LANE_Y_CENTERS[row] - CELL_HEIGHT / 2;
                g2d.setColor(new Color(200, 200, 200, 30));
                g2d.fillRect(cellX, cellY, CELL_WIDTH, CELL_HEIGHT);
                g2d.setColor(new Color(255, 255, 255, 50));
                g2d.drawRect(cellX, cellY, CELL_WIDTH, CELL_HEIGHT);
            }
        }

        Level level = game.getCurrentLevel();
        if (level != null) {
            level.draw(g2d);
        } else {
            if (game.getCurrentState() instanceof patterns.state.NotStartedState) {
                 g2d.setColor(Color.WHITE);
                 g2d.setFont(new Font("Arial", Font.BOLD, 24));
                 g2d.drawString("Select difficulty and press Play in Main Menu", 300, 300);
            }
        }
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        game.update();
    }
    
    public long getRemainingCooldown() {
        long elapsed = System.currentTimeMillis() - lastPlacementTime;
        return Math.max(0, PLACEMENT_COOLDOWN - elapsed);
    }
}
