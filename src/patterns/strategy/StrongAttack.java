package patterns.strategy;

import entities.Entity;
import gameobjects.Projectile;

import java.awt.*;


public class StrongAttack implements AttackStrategy {
    @Override
    public void executeAttack(Entity attacker) {

        Projectile projectile = new Projectile(
                attacker,
                attacker.getX() + attacker.getWidth(),
                attacker.getY() + attacker.getHeight() / 2 - 5,
                12, 8,
                3,
                attacker.getAttackDamage(),
                attacker.getAttackRange(),
                Color.RED
        );
        attacker.getProjectiles().add(projectile);
    }
}
