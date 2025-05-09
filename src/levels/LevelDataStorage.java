package levels;

import entities.EntityTypeData.TitanType;

import java.util.*;

public final class LevelDataStorage {
    private LevelDataStorage() {
    }

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

        public WaveInfo(int waveNumber, long interval, List<TitanGroupInfo> groups) {
            this.waveNumber = waveNumber;
            this.baseSpawnIntervalMillis = interval;
            this.titanGroups = Collections.unmodifiableList(new ArrayList<>(groups));
        }
    }

    public static class LevelInfo {
        public final int levelNumber;
        public final List<WaveInfo> waves;

        public LevelInfo(int levelNumber, List<WaveInfo> waves) {
            this.levelNumber = levelNumber;
            this.waves = Collections.unmodifiableList(new ArrayList<>(waves));
        }
    }

    private static final Map<Integer, LevelInfo> levelData = new HashMap<>();

    static {

        levelData.put(1, new LevelInfo(1, List.of(
                new WaveInfo(1, 3000, List.of(new TitanGroupInfo(TitanType.BASIC, 5))),
                new WaveInfo(2, 2500, List.of(new TitanGroupInfo(TitanType.BASIC, 7)))
        )));


        levelData.put(2, new LevelInfo(2, List.of(
                new WaveInfo(1, 3000, List.of(new TitanGroupInfo(TitanType.BASIC, 6))),
                new WaveInfo(2, 2800, List.of(new TitanGroupInfo(TitanType.BASIC, 4), new TitanGroupInfo(TitanType.FAST, 2))),
                new WaveInfo(3, 2500, List.of(new TitanGroupInfo(TitanType.BASIC, 5), new TitanGroupInfo(TitanType.FAST, 3)))
        )));


        levelData.put(3, new LevelInfo(3, List.of(
                new WaveInfo(1, 3500, List.of(new TitanGroupInfo(TitanType.BASIC, 4), new TitanGroupInfo(TitanType.FAST, 2))),
                new WaveInfo(2, 3000, List.of(new TitanGroupInfo(TitanType.BASIC, 3), new TitanGroupInfo(TitanType.STRONG, 1), new TitanGroupInfo(TitanType.FAST, 3))),
                new WaveInfo(3, 2500, List.of(new TitanGroupInfo(TitanType.STRONG, 2), new TitanGroupInfo(TitanType.FAST, 4)))
        )));


        levelData.put(4, new LevelInfo(4, List.of(
                new WaveInfo(1, 3300, List.of(new TitanGroupInfo(TitanType.STRONG, 2), new TitanGroupInfo(TitanType.FAST, 3))),
                new WaveInfo(2, 2800, List.of(new TitanGroupInfo(TitanType.BASIC, 5), new TitanGroupInfo(TitanType.STRONG, 2), new TitanGroupInfo(TitanType.FAST, 3))),
                new WaveInfo(3, 2400, List.of(new TitanGroupInfo(TitanType.STRONG, 3), new TitanGroupInfo(TitanType.FAST, 5)))
        )));


        levelData.put(5, new LevelInfo(5, List.of(
                new WaveInfo(1, 3000, List.of(new TitanGroupInfo(TitanType.BASIC, 6), new TitanGroupInfo(TitanType.FAST, 4), new TitanGroupInfo(TitanType.STRONG, 1))),
                new WaveInfo(2, 2600, List.of(new TitanGroupInfo(TitanType.BASIC, 4), new TitanGroupInfo(TitanType.FAST, 5), new TitanGroupInfo(TitanType.STRONG, 2))),
                new WaveInfo(3, 2200, List.of(new TitanGroupInfo(TitanType.BASIC, 5), new TitanGroupInfo(TitanType.FAST, 6), new TitanGroupInfo(TitanType.STRONG, 3)))
        )));
    }

    public static LevelInfo getLevelInfo(int levelNumber) {
        return levelData.get(levelNumber);
    }

    public static int getMaxLevelsDefined() {
        return levelData.keySet().stream().max(Integer::compareTo).orElse(0);
    }
}
