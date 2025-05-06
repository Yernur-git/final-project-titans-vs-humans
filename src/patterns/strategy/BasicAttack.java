package patterns.strategy;
import entities.Entity;
import gameobjects.Projectile;
import java.awt.Color;

public class BasicAttack implements AttackStrategy {
    @Override
    public void executeAttack(Entity attacker) {
        int projStartX = attacker.getX() + attacker.getWidth();
        int projStartY = attacker.getY() + attacker.getHeight() / 2 - 3;
        Projectile projectile = new Projectile(
                projStartX, projStartY,
                8, 4,
                5,
                attacker.getAttackDamage(),
                Color.YELLOW
        );
        attacker.getProjectiles().add(projectile);
    }
}