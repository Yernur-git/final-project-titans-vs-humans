package entities;

public final class EntityTypeData {
    private EntityTypeData() {}

    public static final int HUMAN_DRAW_WIDTH = 30;
    public static final int HUMAN_DRAW_HEIGHT = 50;
    public static final int TITAN_DRAW_WIDTH = 50;
    public static final int TITAN_DRAW_HEIGHT = 80;

    public enum HumanType {
        BASIC("Basic Human", 50),
        STRONG("Strong Human", 100),
        FAST("Fast Human", 75);

        private final String displayName;
        private final int cost;

        HumanType(String displayName, int cost) {
            this.displayName = displayName;
            this.cost = cost;
        }

        public String getDisplayName() { return displayName; }
        public int getCost() { return cost; }

        @Override public String toString() { return displayName + " (Cost: " + cost + ")"; }
    }

    public enum TitanType {
        BASIC("Basic Titan"),
        STRONG("Strong Titan"),
        FAST("Fast Titan");

        private final String displayName;

        TitanType(String displayName) {
            this.displayName = displayName;
        }
        public String getDisplayName() { return displayName; }
    }
}

