package patterns.strategy;

import entities.Entity;
import gameobjects.Projectile;

import java.awt.*;

public class FastAttack implements AttackStrategy {
    @Override
    public void executeAttack(Entity attacker) {

        Projectile projectile = new Projectile(
                attacker,
                attacker.getX() + attacker.getWidth(),
                attacker.getY() + attacker.getHeight() / 2 - 2,
                6, 3,
                8,
                attacker.getAttackDamage(),
                attacker.getAttackRange(),
                Color.CYAN
        );
        attacker.getProjectiles().add(projectile);
    }
}
