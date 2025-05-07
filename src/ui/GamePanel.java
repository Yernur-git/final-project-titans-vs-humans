package ui;

import entities.Entity;
import entities.EntityTypeData;
import entities.Human;
import entities.SpikeTrap;
import game.Game;
import game.GameSettings;
import levels.Level;
import patterns.decorator.ArmoredHuman;
import patterns.decorator.VeteranHuman;
import patterns.state.RunningState;
import utils.ResourceLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class GamePanel extends JPanel implements ActionListener {
    private final Game game;
    private final Timer gameTimer;
    private BufferedImage background;

    private boolean placementMode = false;
    private Object selectedEnumType;
    private String selectedDecoratorType;

    private long lastPlacementTime = 0;
    private final long PLACEMENT_COOLDOWN;

    private static final int CELL_WIDTH = GameSettings.CELL_WIDTH;
    private static final int CELL_HEIGHT = GameSettings.CELL_HEIGHT;
    private static final int GRID_ROWS = GameSettings.GRID_ROWS;
    private static final int GRID_COLS = GameSettings.GRID_COLS;
    private static final int GRID_START_X = GameSettings.GRID_START_X;
    private static final int[] LANE_Y_CENTERS = GameSettings.LANE_Y_CENTERS;
    private static final int LANE_HEIGHT_TOLERANCE = GameSettings.LANE_HEIGHT_TOLERANCE;

    private final Map<Point, Entity> placementGrid = new HashMap<>();
    private Point highlightedCell = null;

    public GamePanel() {
        this.game = Game.getInstance();
        this.PLACEMENT_COOLDOWN = game.getPlacementCooldown();

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
            setBackground(Color.DARK_GRAY);
        }
        repaint();
    }

    public void levelChanged(int levelNumber) {
        String bgName = levelNumber + "_bg.png";
        BufferedImage newBg = ResourceLoader.loadImage("backgrounds/" + bgName);
        if (newBg != null) {
            background = newBg;
        } else {
            loadBackground("default_bg.png");
        }
        placementGrid.clear();
        repaint();
    }

    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (placementMode && game.getCurrentState() instanceof RunningState) {
                    handlePlacementAttempt(e.getX(), e.getY());
                } else if (placementMode) {
                    cancelPlacementMode("Cannot place units now (Game not running).");
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (placementMode && game.getCurrentState() instanceof RunningState) {
                    Point currentCell = getGridCellFromMouse(e.getX(), e.getY());
                    if ((currentCell == null && highlightedCell != null) || (currentCell != null && !currentCell.equals(highlightedCell))) {
                        highlightedCell = currentCell;
                        repaint();
                    }
                } else {
                    if (highlightedCell != null) {
                        highlightedCell = null;
                        repaint();
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (placementMode && game.getCurrentState() instanceof RunningState) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(Cursor.getDefaultCursor());
                if (highlightedCell != null) {
                    highlightedCell = null;
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (placementMode && game.getCurrentState() instanceof RunningState) {
                    Point currentCell = getGridCellFromMouse(e.getX(), e.getY());
                    if ((currentCell == null && highlightedCell != null) || (currentCell != null && !currentCell.equals(highlightedCell))) {
                        highlightedCell = currentCell;
                        repaint();
                    }
                } else {
                    if (highlightedCell != null) {
                        highlightedCell = null;
                        repaint();
                    }
                }
            }
        });
    }

    public void enterPlacementMode(Object enumType, String decorator) {
        if (game.getCurrentState() instanceof RunningState) {
            this.selectedEnumType = enumType;
            this.selectedDecoratorType = decorator;
            this.placementMode = true;
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

            String displayName = "";
            if (enumType instanceof EntityTypeData.HumanType)
                displayName = ((EntityTypeData.HumanType) enumType).getDisplayName();
            else if (enumType instanceof EntityTypeData.ObstacleType)
                displayName = ((EntityTypeData.ObstacleType) enumType).getDisplayName();

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
            long remaining = PLACEMENT_COOLDOWN - (currentTime - lastPlacementTime);
            cancelPlacementMode(String.format("Placement cooldown: %.1fs left.", remaining / 1000.0));
            return;
        }

        Point cell = getGridCellFromMouse(mouseX, mouseY);
        if (cell == null) {
            cancelPlacementMode("Invalid placement area!");
            return;
        }

        Entity existingEntity = placementGrid.get(cell);
        if (existingEntity != null && existingEntity.isActive()) {
            cancelPlacementMode("Cell (" + cell.x + "," + cell.y + ") is already occupied!");
            return;
        }

        int row = cell.x;
        int col = cell.y;

        int targetLaneY = LANE_Y_CENTERS[row];

        int cellCenterX = GRID_START_X + col * CELL_WIDTH + CELL_WIDTH / 2;

        Entity entityToPlace = null;
        int totalCost = 0;
        String displayName = "";

        if (selectedEnumType == null) {
            cancelPlacementMode("Error: No entity type selected.");
            return;
        }

        if (selectedEnumType instanceof EntityTypeData.HumanType humanType) {
            Human baseHuman = game.getHumanFactory().createHuman(humanType, 0, 0);
            if (baseHuman == null) {
                cancelPlacementMode("Failed to create human.");
                return;
            }

            int actualX = cellCenterX - baseHuman.getWidth() / 2;
            int actualY = targetLaneY - baseHuman.getHeight() / 2;
            baseHuman.setPosition(actualX, actualY);

            Human finalHuman = baseHuman;
            totalCost = humanType.getCost();
            displayName = humanType.getDisplayName();

            if ("Armored".equals(selectedDecoratorType)) {
                finalHuman = new ArmoredHuman(finalHuman);
                totalCost = finalHuman.getCost();
            } else if ("Veteran".equals(selectedDecoratorType)) {
                finalHuman = new VeteranHuman(finalHuman);
                totalCost = finalHuman.getCost();
            }
            entityToPlace = finalHuman;

        } else if (selectedEnumType instanceof EntityTypeData.ObstacleType obstacleType) {
            entityToPlace = game.getObstacleFactory().createObstacle(obstacleType, 0, 0);
            if (entityToPlace == null) {
                cancelPlacementMode("Failed to create obstacle.");
                return;
            }

            int actualX = cellCenterX - entityToPlace.getWidth() / 2;
            int actualY = targetLaneY - entityToPlace.getHeight() / 2;
            if (entityToPlace instanceof SpikeTrap) actualY += 15;
            entityToPlace.setPosition(actualX, actualY);

            totalCost = obstacleType.getCost();
            displayName = obstacleType.getDisplayName();

        } else {
            cancelPlacementMode("Invalid entity type selected.");
            return;
        }

        if (game.getResources() >= totalCost) {
            game.addResources(-totalCost);
            game.getCurrentLevel().addEntity(entityToPlace);
            placementGrid.put(cell, entityToPlace);
            lastPlacementTime = currentTime;
            game.notifyObservers(displayName + " placed successfully!");
        } else {
            game.notifyObservers("Not enough coins! Need " + totalCost + ", Have " + game.getResources());
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

        if (placementMode && highlightedCell != null) {
            int row = highlightedCell.x;
            int col = highlightedCell.y;
            int cellX = GRID_START_X + col * CELL_WIDTH;
            int cellY = LANE_Y_CENTERS[row] - CELL_HEIGHT / 2;
            g2d.setColor(new Color(100, 255, 100, 100));
            g2d.fillRect(cellX, cellY, CELL_WIDTH, CELL_HEIGHT);
            g2d.setColor(Color.GREEN);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(cellX, cellY, CELL_WIDTH, CELL_HEIGHT);
            g2d.setStroke(new BasicStroke(1));
        }

        Level level = game.getCurrentLevel();
        if (level != null) {
            level.draw(g2d);
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
