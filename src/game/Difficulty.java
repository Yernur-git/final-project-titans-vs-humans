package game;

public enum Difficulty {
    EASY("Easy", 0.75, 0.8, 0.9, 1.25, 1.1, 1.2),
    MEDIUM("Medium", 1.0, 1.0, 1.0, 1.0, 1.0, 1.0),
    HARD("Hard", 1.3, 1.25, 1.1, 0.8, 0.9, 0.8);

    private final String displayName;
    private final double titanHealthScale;
    private final double titanDamageScale;
    private final double titanMoveSpeedScale;
    private final double resourceStartMultiplier;
    private final double baseHealthMultiplier;
    private final double spawnIntervalMultiplier;

    Difficulty(String displayName, double ths, double tds, double tms, double rsm, double bhm, double sim) {
        this.displayName = displayName;
        this.titanHealthScale = ths;
        this.titanDamageScale = tds;
        this.titanMoveSpeedScale = tms;
        this.resourceStartMultiplier = rsm;
        this.baseHealthMultiplier = bhm;
        this.spawnIntervalMultiplier = sim;
    }

    public String getDisplayName() { return displayName; }
    public double getTitanHealthScale() { return titanHealthScale; }
    public double getTitanDamageScale() { return titanDamageScale; }
    public double getTitanMoveSpeedScale() { return titanMoveSpeedScale; }
    public double getResourceStartMultiplier() { return resourceStartMultiplier; }
    public double getBaseHealthMultiplier() { return baseHealthMultiplier; }
    public double getSpawnIntervalMultiplier() { return spawnIntervalMultiplier; }

    @Override
    public String toString() {
        return displayName;
    }
}
