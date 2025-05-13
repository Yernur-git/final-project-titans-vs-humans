package entities;

import patterns.strategy.AttackStrategy;
import patterns.strategy.BasicAttack;
import patterns.strategy.FastAttack;
import patterns.strategy.StrongAttack;

public final class EntityTypeData {
    private EntityTypeData() {
    }

    public static final int HUMAN_DRAW_WIDTH = 45;
    public static final int HUMAN_DRAW_HEIGHT = 55;
    public static final int TITAN_DRAW_WIDTH = 55;
    public static final int TITAN_DRAW_HEIGHT = 75;
    public static final int WALL_DRAW_WIDTH = 50;
    public static final int WALL_DRAW_HEIGHT = 55;
    public static final int TRAP_DRAW_WIDTH = 40;
    public static final int TRAP_DRAW_HEIGHT = 15;

    public enum HumanType {
        BASIC("Basic Human", "humans/basic.png", BasicAttack.class, 50, 10, 200, 1000, 50, 45000),
        STRONG("Strong Human", "humans/strong.png", StrongAttack.class, 90, 20, 200, 1500, 100, 60000),
        FAST("Fast Human", "humans/fast.png", FastAttack.class, 40, 8, 220, 600, 75, 30000);

        private final String displayName;
        private final String spritePath;
        private final Class<? extends AttackStrategy> strategyClass;
        private final int health;
        private final int damage;
        private final int range;
        private final int attackSpeed;
        private final int cost;
        private final long lifespanMillis;

        HumanType(String displayName, String sprite, Class<? extends AttackStrategy> strategyClass, int hp, int dmg, int rng, int speed, int cost, long lifespan) {
            this.displayName = displayName;
            this.spritePath = sprite;
            this.strategyClass = strategyClass;
            this.health = hp;
            this.damage = dmg;
            this.range = rng;
            this.attackSpeed = speed;
            this.cost = cost;
            this.lifespanMillis = lifespan;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getSpritePath() {
            return spritePath;
        }

        public Class<? extends AttackStrategy> getStrategyClass() {
            return strategyClass;
        }

        public int getHealth() {
            return health;
        }

        public int getDamage() {
            return damage;
        }

        public int getRange() {
            return range;
        }

        public int getAttackSpeed() {
            return attackSpeed;
        }

        public int getCost() {
            return cost;
        }

        public long getLifespanMillis() {
            return lifespanMillis;
        }

        @Override
        public String toString() {
            return displayName + " (Cost: " + cost + ")";
        }
    }


    public enum TitanType {
        BASIC("Basic Titan", "titans/basic.png", 100, 10, 30, 1200, 1),
        STRONG("Strong Titan", "titans/strong.png", 200, 25, 35, 1800, 1),
        FAST("Fast Titan", "titans/fast.png", 70, 8, 25, 800, 2);

        private final String displayName;
        private final String spritePath;
        private final int baseHealth;
        private final int baseDamage;
        private final int baseRange;
        private final int baseAttackSpeed;
        private final int baseMoveSpeed;

        TitanType(String name, String sprite, int hp, int dmg, int rng, int speed, int moveSpeed) {
            this.displayName = name;
            this.spritePath = sprite;
            this.baseHealth = hp;
            this.baseDamage = dmg;
            this.baseRange = rng;
            this.baseAttackSpeed = speed;
            this.baseMoveSpeed = moveSpeed;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getSpritePath() {
            return spritePath;
        }

        public int getBaseHealth() {
            return baseHealth;
        }

        public int getBaseDamage() {
            return baseDamage;
        }

        public int getBaseRange() {
            return baseRange;
        }

        public int getBaseAttackSpeed() {
            return baseAttackSpeed;
        }

        public int getBaseMoveSpeed() {
            return baseMoveSpeed;
        }
    }


    public enum ObstacleType {
        WALL("Blocker Wall", "obstacles/wall.png", 300, 0, 50, -1, BlockerWall.class),
        TRAP("Spike Trap", "obstacles/spiketrap.png", 1, 75, 25, -1, SpikeTrap.class);

        private final String displayName;
        private final String spritePath;
        private final int health;
        private final int damage;
        private final int cost;
        private final long lifespanMillis;
        private final Class<? extends Entity> entityClass;

        ObstacleType(String name, String sprite, int hp, int dmg, int cost, long lifespan, Class<? extends Entity> clazz) {
            this.displayName = name;
            this.spritePath = sprite;
            this.health = hp;
            this.damage = dmg;
            this.cost = cost;
            this.lifespanMillis = lifespan;
            this.entityClass = clazz;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getSpritePath() {
            return spritePath;
        }

        public int getHealth() {
            return health;
        }

        public int getDamage() {
            return damage;
        }

        public int getCost() {
            return cost;
        }

        public long getLifespanMillis() {
            return lifespanMillis;
        }

        public Class<? extends Entity> getEntityClass() {
            return entityClass;
        }

        @Override
        public String toString() {
            return displayName + " (Cost: " + cost + ")";
        }
    }
}
