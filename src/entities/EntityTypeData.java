package entities;

import patterns.strategy.*; 

public final class EntityTypeData {
    public static final int HUMAN_DRAW_WIDTH = 30; 
    public static final int HUMAN_DRAW_HEIGHT = 50;
    public static final int TITAN_DRAW_WIDTH = 50;
    public static final int TITAN_DRAW_HEIGHT = 80;
    public static final int WALL_DRAW_WIDTH = 40;
    public static final int WALL_DRAW_HEIGHT = 55;
    public static final int TRAP_DRAW_WIDTH = 35;
    public static final int TRAP_DRAW_HEIGHT = 15;

    public enum HumanType {
        BASIC("Basic Human", "humans/basic.png", BasicAttack.class, 50, 10, 200, 1000, 50, 45000),
        STRONG("Strong Human", "humans/strong.png", StrongAttack.class, 90, 20, 200, 1500, 100, 60000),
        FAST("Fast Human", "humans/fast.png", FastAttack.class, 40, 8, 220, 600, 75, 30000);
        private final String displayName, spritePath;
        private final Class<? extends AttackStrategy> strategyClass;
        private final int health, damage, range, attackSpeed, cost;
        private final long lifespanMillis;
        HumanType(String dn, String sp, Class<? extends AttackStrategy> sc, int hp, int dmg, int rng, int speed, int c, long life) {
            displayName=dn; spritePath=sp; strategyClass=sc; health=hp; damage=dmg; range=rng; attackSpeed=speed; cost=c; lifespanMillis=life;
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
        @Override public String toString() {
            return displayName + " (Cost: " + cost + ")"; 
        }
    }

    public enum TitanType {
        BASIC("Basic Titan", "titans/basic.png", 100, 10, 30, 1200, 1),
        STRONG("Strong Titan", "titans/strong.png", 200, 25, 35, 1800, 1),
        FAST("Fast Titan", "titans/fast.png", 70, 8, 25, 800, 2);
        private final String displayName, spritePath;
        private final int baseHealth, baseDamage, baseRange, baseAttackSpeed, baseMoveSpeed;
        TitanType(String n, String s, int hp, int dmg, int rng, int as, int ms) {
            displayName=n; spritePath=s; baseHealth=hp; baseDamage=dmg; baseRange=rng; baseAttackSpeed=as; baseMoveSpeed=ms;
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

        private final String displayName, spritePath;
        private final int health, damage, cost;
        private final long lifespanMillis;
        private final Class<? extends Entity> entityClass;

        ObstacleType(String n, String s, int hp, int dmg, int c, long life, Class<? extends Entity> clazz) {
            displayName=n; spritePath=s; health=hp; damage=dmg; cost=c; lifespanMillis=life; entityClass=clazz;
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
        @Override public String toString() {
            return displayName + " (Cost: " + cost + ")"; 
        }
    }
}
