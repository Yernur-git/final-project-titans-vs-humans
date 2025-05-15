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
import game.Game.BombEffect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

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

    private boolean isRelocationModeActive = false;
    private Human selectedUnitForRelocation = null;
    private Point highlightedRelocationTargetCell = null;

    private boolean isBombAimingMode = false;
    private Point BombAimLocation = null;

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
        setupKeyBindings();
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
        cancelAllModes(null);
        repaint();
    }

    private void setupKeyBindings() {
        String BombActionKey = "performBomb";
        String cancelModesActionKey = "cancelAllActiveModes";

        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_E, 0), BombActionKey);
        actionMap.put(BombActionKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!(game.getCurrentState() instanceof RunningState)) return;

                if (game.canUseBomb()) {
                    if (!isBombAimingMode) {
                        cancelAllModes(null);
                        isBombAimingMode = true;
                        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                        game.notifyObservers("Bomb aiming: Click to unleash!");
                    } else {
                        cancelAllModes("Bomb aiming cancelled.");
                    }
                }
                repaint();
            }
        });

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), cancelModesActionKey);
        actionMap.put(cancelModesActionKey, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isBombAimingMode || placementMode || isRelocationModeActive) {
                    cancelAllModes("Action cancelled by user.");
                }
            }
        });
    }

    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!(game.getCurrentState() instanceof RunningState)) {
                    cancelAllModes("Game not running.");
                    return;
                }

                if (isBombAimingMode) {
                    game.performBomb(e.getX(), e.getY());
                    isBombAimingMode = false;
                    BombAimLocation = null;
                    setCursor(Cursor.getDefaultCursor());
                } else if (placementMode) {
                    handlePlacementAttempt(e.getX(), e.getY());
                } else if (isRelocationModeActive) {
                    handleRelocationTargetSelection(e.getX(), e.getY());
                } else {
                    handleUnitSelectionForRelocation(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!(game.getCurrentState() instanceof RunningState)) return;

                if (isBombAimingMode || placementMode) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                } else if (isRelocationModeActive) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(Cursor.getDefaultCursor());
                boolean needsRepaint = false;
                if (highlightedCell != null) {
                    highlightedCell = null;
                    needsRepaint = true;
                }
                if (highlightedRelocationTargetCell != null) {
                    highlightedRelocationTargetCell = null;
                    needsRepaint = true;
                }
                if (BombAimLocation != null) {
                    BombAimLocation = null;
                    needsRepaint = true;
                }
                if (needsRepaint) {
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!(game.getCurrentState() instanceof RunningState)) {
                    if (highlightedCell != null || highlightedRelocationTargetCell != null || BombAimLocation != null) {
                        highlightedCell = null;
                        highlightedRelocationTargetCell = null;
                        BombAimLocation = null;
                        repaint();
                    }
                    return;
                }

                Point mouseGridPoint = getGridCellFromMouse(e.getX(), e.getY());

                if (isBombAimingMode) {
                    BombAimLocation = e.getPoint();
                    if (highlightedCell != null || highlightedRelocationTargetCell != null) {
                        highlightedCell = null;
                        highlightedRelocationTargetCell = null;
                    }
                    repaint();
                } else if (placementMode) {
                    if ((mouseGridPoint == null && highlightedCell != null) || (mouseGridPoint != null && !mouseGridPoint.equals(highlightedCell))) {
                        highlightedCell = mouseGridPoint;
                    }
                    if (highlightedRelocationTargetCell != null) highlightedRelocationTargetCell = null;
                    if (BombAimLocation != null) BombAimLocation = null;
                    repaint();
                } else if (isRelocationModeActive && selectedUnitForRelocation != null) {
                    if ((mouseGridPoint == null && highlightedRelocationTargetCell != null) || (mouseGridPoint != null && !mouseGridPoint.equals(highlightedRelocationTargetCell))) {
                        highlightedRelocationTargetCell = mouseGridPoint;
                    }
                    if (highlightedCell != null) highlightedCell = null;
                    if (BombAimLocation != null) BombAimLocation = null;
                    repaint();
                } else {
                    boolean needsRepaint = false;
                    if (highlightedCell != null) {
                        highlightedCell = null;
                        needsRepaint = true;
                    }
                    if (highlightedRelocationTargetCell != null) {
                        highlightedRelocationTargetCell = null;
                        needsRepaint = true;
                    }
                    if (BombAimLocation != null) {
                        BombAimLocation = null;
                        needsRepaint = true;
                    }
                    if (needsRepaint) {
                        repaint();
                    }
                }
            }
        });
    }

    private void handleUnitSelectionForRelocation(int mouseX, int mouseY) {
        if (!game.canRelocateNow()) {
            return;
        }

        Point cell = getGridCellFromMouse(mouseX, mouseY);
        if (cell != null) {
            Entity entity = placementGrid.get(cell);
            if (entity instanceof Human && entity.isActive()) {
                cancelAllModes(null);
                this.isBombAimingMode = false;
                selectedUnitForRelocation = (Human) entity;
                isRelocationModeActive = true;
                placementMode = false;
                setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                game.notifyObservers("Relocation mode: Select new location for " + selectedUnitForRelocation.getClass().getSimpleName());
                repaint();
            } else {
                game.notifyObservers("No movable unit selected.");
            }
        }
    }

    private void handleRelocationTargetSelection(int mouseX, int mouseY) {
        Point targetCell = getGridCellFromMouse(mouseX, mouseY);

        if (targetCell == null) {
            cancelAllModes("Invalid relocation target area!");
            return;
        }

        if (placementGrid.containsKey(targetCell) && placementGrid.get(targetCell) != null && placementGrid.get(targetCell).isActive()) {
            cancelAllModes("Target cell (" + targetCell.x + "," + targetCell.y + ") is occupied!");
            return;
        }

        Point originalCell = getGridCellForEntity(selectedUnitForRelocation);

        if (originalCell == null) {
            cancelAllModes("Error: Could not find original position of the unit.");
            return;
        }

        if (originalCell.equals(targetCell)) {
            cancelAllModes("Unit already at this location. Relocation cancelled.");
            return;
        }

        int targetLaneY = LANE_Y_CENTERS[targetCell.x];
        int cellCenterX = GRID_START_X + targetCell.y * CELL_WIDTH + CELL_WIDTH / 2;
        int newActualX = cellCenterX - selectedUnitForRelocation.getWidth() / 2;
        int newActualY = targetLaneY - selectedUnitForRelocation.getHeight() / 2;

        placementGrid.remove(originalCell);
        selectedUnitForRelocation.setPosition(newActualX, newActualY);
        placementGrid.put(targetCell, selectedUnitForRelocation);

        game.recordRelocation();

        game.notifyObservers(selectedUnitForRelocation.getClass().getSimpleName() + " relocated to (" + targetCell.x + "," + targetCell.y + ").");
        cancelAllModes(null);
    }

    private void cancelAllModes(String reason) {
        placementMode = false;
        isRelocationModeActive = false;
        isBombAimingMode = false;
        selectedUnitForRelocation = null;
        selectedEnumType = null;
        highlightedCell = null;
        highlightedRelocationTargetCell = null;
        BombAimLocation = null;
        setCursor(Cursor.getDefaultCursor());
        if (reason != null && !reason.isEmpty()) {
            game.notifyObservers(reason);
        }
        repaint();
    }

    public void enterPlacementMode(Object enumType, String decorator) {
        if (game.getCurrentState() instanceof RunningState) {
            cancelAllModes(null);
            this.isBombAimingMode = false;
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

    private void handlePlacementAttempt(int mouseX, int mouseY) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastPlacementTime < PLACEMENT_COOLDOWN) {
            long remaining = PLACEMENT_COOLDOWN - (currentTime - lastPlacementTime);
            cancelAllModes(String.format("Placement cooldown: %.1fs left.", remaining / 1000.0));
            return;
        }

        Point cell = getGridCellFromMouse(mouseX, mouseY);
        if (cell == null) {
            cancelAllModes("Invalid placement area!");
            return;
        }

        Entity existingEntity = placementGrid.get(cell);
        if (existingEntity != null && existingEntity.isActive()) {
            cancelAllModes("Cell (" + cell.x + "," + cell.y + ") is already occupied!");
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
            cancelAllModes("Error: No entity type selected.");
            return;
        }

        if (selectedEnumType instanceof EntityTypeData.HumanType humanType) {
            Human baseHuman = game.getHumanFactory().createHuman(humanType, 0, 0);
            if (baseHuman == null) {
                cancelAllModes("Failed to create human.");
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
                cancelAllModes("Failed to create obstacle.");
                return;
            }
            int actualX = cellCenterX - entityToPlace.getWidth() / 2;
            int actualY = targetLaneY - entityToPlace.getHeight() / 2;
            if (entityToPlace instanceof SpikeTrap) actualY += 15;
            entityToPlace.setPosition(actualX, actualY);
            totalCost = obstacleType.getCost();
            displayName = obstacleType.getDisplayName();
        } else {
            cancelAllModes("Invalid entity type selected.");
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
        cancelAllModes(null);
    }

    private Point getGridCellFromMouse(int x, int y) {
        if (x < GRID_START_X || x >= GRID_START_X + GRID_COLS * CELL_WIDTH) {
            return null;
        }
        int col = (x - GRID_START_X) / CELL_WIDTH;
        int closestRow = -1;
        int minDistanceY = Integer.MAX_VALUE;
        for (int r = 0; r < GRID_ROWS; r++) {
            int distanceY = Math.abs(y - LANE_Y_CENTERS[r]);
            if (distanceY < minDistanceY) {
                minDistanceY = distanceY;
                closestRow = r;
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

        for (int r = 0; r < GRID_ROWS; r++) {
            for (int c = 0; c < GRID_COLS; c++) {
                int cellX = GRID_START_X + c * CELL_WIDTH;
                int cellY = LANE_Y_CENTERS[r] - CELL_HEIGHT / 2;
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

        if (isRelocationModeActive) {
            if (selectedUnitForRelocation != null) {
                g2d.setColor(new Color(50, 150, 255, 150));
                g2d.setStroke(new BasicStroke(3));
                g2d.drawRect(selectedUnitForRelocation.getX() -2 , selectedUnitForRelocation.getY() -2,
                        selectedUnitForRelocation.getWidth() + 4, selectedUnitForRelocation.getHeight() + 4);
                g2d.setStroke(new BasicStroke(1));
            }
            if (highlightedRelocationTargetCell != null) {
                int row = highlightedRelocationTargetCell.x;
                int col = highlightedRelocationTargetCell.y;
                int cellX = GRID_START_X + col * CELL_WIDTH;
                int cellY = LANE_Y_CENTERS[row] - CELL_HEIGHT / 2;
                boolean cellOccupied = placementGrid.containsKey(highlightedRelocationTargetCell) &&
                        placementGrid.get(highlightedRelocationTargetCell) != null &&
                        placementGrid.get(highlightedRelocationTargetCell).isActive();

                if (cellOccupied) {
                    g2d.setColor(new Color(255, 100, 100, 100));
                    g2d.fillRect(cellX, cellY, CELL_WIDTH, CELL_HEIGHT);
                    g2d.setColor(Color.RED);
                } else {
                    g2d.setColor(new Color(100, 180, 255, 100));
                    g2d.fillRect(cellX, cellY, CELL_WIDTH, CELL_HEIGHT);
                    g2d.setColor(Color.CYAN);
                }
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRect(cellX, cellY, CELL_WIDTH, CELL_HEIGHT);
                g2d.setStroke(new BasicStroke(1));
            }
        }

        if (isBombAimingMode && BombAimLocation != null) {
            g2d.setColor(new Color(255, 100, 0, 100));
            int r = GameSettings.Bomb_RADIUS;
            g2d.fillOval(BombAimLocation.x - r, BombAimLocation.y - r, 2 * r, 2 * r);
            g2d.setColor(Color.ORANGE);
            g2d.drawOval(BombAimLocation.x - r, BombAimLocation.y - r, 2 * r, 2 * r);
        }

        List<BombEffect> effects = game.getActiveBombEffects();
        if (effects != null) {
            for (BombEffect effect : effects) {
                g2d.setColor(new Color(255, 0, 0, 150));
                int r = effect.getRadius();
                g2d.fillOval(effect.getX() - r, effect.getY() - r, 2 * r, 2 * r);
            }
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