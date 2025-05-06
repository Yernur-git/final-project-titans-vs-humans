package levels;

import entities.EntityTypeData.TitanType;
import java.util.*;

public final class LevelDataStorage {
    private static final Map<Integer, LevelInfo> levelData = new HashMap<>();

    static {
        levelData.put(1, new LevelInfo(1, List.of(
                new WaveInfo(1, 3000, List.of(new TitanGroupInfo(TitanType.BASIC, 5))),
                new WaveInfo(2, 2500, List.of(new TitanGroupInfo(TitanType.BASIC, 7)))
        ));

        levelData.put(2, new LevelInfo(2, List.of(
                new WaveInfo(1, 3000, List.of(new TitanGroupInfo(TitanType.BASIC, 6))),
                new WaveInfo(2, 2800, List.of(new TitanGroupInfo(TitanType.BASIC, 4), new TitanGroupInfo(TitanType.FAST, 2))),
                new WaveInfo(3, 2500, List.of(new TitanGroupInfo(TitanType.BASIC, 5), new TitanGroupInfo(TitanType.FAST, 3)))
        )));

        // ... уровни 3-5 аналогично ...
    }

    public static LevelInfo getLevelInfo(int levelNumber) {
        return levelData.get(levelNumber);
    }

    public static int getMaxLevelsDefined() {
        return levelData.isEmpty() ? 0 : Collections.max(levelData.keySet());
    }
}
