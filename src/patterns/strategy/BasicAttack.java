package patterns.strategy;
import entities.Entity;
import gameobjects.Projectile;
import java.awt.Color;

public class BasicAttack implements AttackStrategy {
    @Override
    public void executeAttack(Entity attacker) {

        Projectile projectile = new Projectile(
                attacker,
                attacker.getX() + attacker.getWidth(),
                attacker.getY() + attacker.getHeight() / 2 - 3,
                8, 4,
                5,
                attacker.getAttackDamage(),
                attacker.getAttackRange(),
                Color.YELLOW
        );

        attacker.getProjectiles().add(projectile);
    }
}
