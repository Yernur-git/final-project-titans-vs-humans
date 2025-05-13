package game;

public final class GameSettings {
    private GameSettings() {
    }

    public static final int BASE_HEALTH_START = 1000;
    public static final int MAX_LEVELS = 5;
    public static final int RESOURCE_INTERVAL_TICKS = 150;
    public static final int RESOURCE_AMOUNT_BASE = 10;
    public static final long PLACEMENT_COOLDOWN_MILLIS = 1500;
    public static final int STARTING_RESOURCES_MEDIUM = 200;

    public static final int CELL_WIDTH = 60;
    public static final int CELL_HEIGHT = 60;
    public static final int GRID_ROWS = 5;
    public static final int GRID_COLS = 7;
    public static final int GRID_START_X = 120;

    public static final int[] LANE_Y_CENTERS = {150, 230, 310, 390, 470};

    public static final int LANE_HEIGHT_TOLERANCE = 40;

    public static final int ENTITY_INTERACTION_Y_TOLERANCE = 40;
}
