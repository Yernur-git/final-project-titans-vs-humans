package levels;

import entities.EntityTypeData.TitanType;
import java.util.*;

public final class LevelDataStorage {
    private LevelDataStorage() {}

    public static class TitanGroupInfo {
        public final TitanType type;
        public final int count;
        public TitanGroupInfo(TitanType type, int count) {
            this.type = type;
            this.count = count;
        }
    }

    public static class WaveInfo {
        public final int waveNumber;
        public final long baseSpawnIntervalMillis;
        public final List<TitanGroupInfo> titanGroups;
        public WaveInfo(int wn, long interval, List<TitanGroupInfo> groups) {
            this.waveNumber = wn;
            this.baseSpawnIntervalMillis = interval;
            this.titanGroups = groups;
        }
    }

    public static class LevelInfo {
        public final int levelNumber;
        public final List<WaveInfo> waves;
        public LevelInfo(int ln, List<WaveInfo> waves) {
            this.levelNumber = ln;
            this.waves = waves;
        }
    }

    private static final Map<Integer, LevelInfo> levelData = new HashMap<>();

    static {
        System.out.println("LevelDataStorage: Initializing level data...");
        List<WaveInfo> level1Waves = List.of(
                new WaveInfo(1, 3000, List.of(
                        new TitanGroupInfo(null, 5)
                )),
                new WaveInfo(2, 2500, List.of(
                        new TitanGroupInfo(null, 7)
                ))
        );
        levelData.put(1, new LevelInfo(1, level1Waves));
        System.out.println("LevelDataStorage: Level 1 data added.");
    }

    public static LevelInfo getLevelInfo(int levelNumber) {
        return levelData.get(levelNumber);
    }

    public static int getMaxLevelsDefined() {
        return levelData.isEmpty() ? 0 : Collections.max(levelData.keySet());
    }
}
