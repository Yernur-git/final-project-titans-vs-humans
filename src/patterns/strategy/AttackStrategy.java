package patterns.strategy;
import entities.Entity;

public interface AttackStrategy {
    void executeAttack(Entity attacker);
}