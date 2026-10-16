package com.bubble.dragon.entity.boss;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class BossTest {
    private static final double EPSILON = 0.000_001;

    @Test
    void startsInactiveWithFullHp() {
        Boss boss = new Boss();

        assertEquals(5, boss.getHp());
        assertEquals(BossState.INACTIVE, boss.getState());
        assertFalse(boss.isActive());
        assertFalse(boss.isDefeated());
        assertFalse(boss.isInvulnerable());
    }

    @Test
    void setDirectionNormalizesToOneOrNegativeOne() {
        Boss boss = new Boss();

        boss.setDirection(0);
        assertEquals(1, boss.getDirection());

        boss.setDirection(12);
        assertEquals(1, boss.getDirection());

        boss.setDirection(-12);
        assertEquals(-1, boss.getDirection());
    }

    @Test
    void reverseChangesDirection() {
        Boss boss = new Boss();

        boss.reverse();
        assertEquals(-1, boss.getDirection());

        boss.reverse();
        assertEquals(1, boss.getDirection());
    }

    @Test
    void faceTurnsTowardTargetRelativeToCenter() {
        Boss boss = new Boss();
        boss.setX(100);

        boss.face(129);
        assertEquals(-1, boss.getDirection());

        boss.face(130);
        assertEquals(1, boss.getDirection());
    }

    @Test
    void firstDamageReducesHpAndStartsInvulnerability() {
        Boss boss = new Boss();
        boss.setState(BossState.PATROL);
        boss.setChasing(true);

        boolean damaged = boss.damage();

        assertTrue(damaged);
        assertEquals(4, boss.getHp());
        assertTrue(boss.isInvulnerable());
        assertFalse(boss.isChasing());
        assertFalse(boss.isDefeated());
    }

    @Test
    void damageDuringInvulnerabilityIsIgnored() {
        Boss boss = new Boss();
        boss.setState(BossState.PATROL);
        boss.damage();

        boolean damagedAgain = boss.damage();

        assertFalse(damagedAgain);
        assertEquals(4, boss.getHp());
        assertTrue(boss.isInvulnerable());
    }

    @Test
    void damageWorksAgainAfterInvulnerabilityExpires() {
        Boss boss = new Boss();
        boss.setState(BossState.PATROL);
        boss.damage();
        boss.updateInvulnerability(10);

        boolean damagedAgain = boss.damage();

        assertTrue(damagedAgain);
        assertEquals(3, boss.getHp());
        assertTrue(boss.isInvulnerable());
    }

    @Test
    void fifthEffectiveDamageDefeatsBossAndStopsMovement() {
        Boss boss = new Boss();
        boss.setState(BossState.PATROL);
        damageAndExpireInvulnerability(boss);
        damageAndExpireInvulnerability(boss);
        damageAndExpireInvulnerability(boss);
        damageAndExpireInvulnerability(boss);
        boss.setVelocityX(120);
        boss.setVelocityY(-80);

        boolean damaged = boss.damage();

        assertTrue(damaged);
        assertEquals(0, boss.getHp());
        assertEquals(BossState.DEFEATED, boss.getState());
        assertTrue(boss.isDefeated());
        assertEquals(0, boss.getVelocityX(), EPSILON);
        assertEquals(0, boss.getVelocityY(), EPSILON);
    }

    @Test
    void damageAfterDefeatIsIgnoredAndHpDoesNotBecomeNegative() {
        Boss boss = new Boss();
        boss.setState(BossState.PATROL);
        damageAndExpireInvulnerability(boss);
        damageAndExpireInvulnerability(boss);
        damageAndExpireInvulnerability(boss);
        damageAndExpireInvulnerability(boss);
        boss.damage();

        boolean damagedAfterDefeat = boss.damage();

        assertFalse(damagedAfterDefeat);
        assertEquals(0, boss.getHp());
        assertEquals(BossState.DEFEATED, boss.getState());
    }

    private static void damageAndExpireInvulnerability(Boss boss) {
        assertTrue(boss.damage());
        boss.updateInvulnerability(10);
    }
}
