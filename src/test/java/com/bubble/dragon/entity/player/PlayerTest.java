package com.bubble.dragon.entity.player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class PlayerTest {

    @Test
    void startsWithExpectedState() {
        Player player = new Player(80, 550);

        assertEquals(3, player.getHp());
        assertEquals(PlayerState.IDLE, player.getState());
        assertTrue(player.isFacingRight());
        assertFalse(player.isInvulnerable());
    }

    @Test
    void firstDamageReducesHpAndStartsInvulnerability() {
        Player player = new Player(80, 550);

        player.damage();

        assertEquals(2, player.getHp());
        assertTrue(player.isInvulnerable());
    }

    @Test
    void damageDuringInvulnerabilityDoesNotReduceHp() {
        Player player = new Player(80, 550);
        player.damage();

        player.damage();

        assertEquals(2, player.getHp());
        assertTrue(player.isInvulnerable());
    }

    @Test
    void damageWorksAgainAfterInvulnerabilityExpires() {
        Player player = new Player(80, 550);
        player.damage();
        player.updateInvulnerability(2);

        player.damage();

        assertEquals(1, player.getHp());
        assertTrue(player.isInvulnerable());
    }

    @Test
    void reachingZeroHpChangesStateToDead() {
        Player player = new Player(80, 550);

        damageAfterInvulnerabilityExpires(player);
        damageAfterInvulnerabilityExpires(player);
        player.damage();

        assertEquals(0, player.getHp());
        assertEquals(PlayerState.DEAD, player.getState());
    }

    @Test
    void damageAfterDeathDoesNotReduceHpBelowZero() {
        Player player = new Player(80, 550);
        damageAfterInvulnerabilityExpires(player);
        damageAfterInvulnerabilityExpires(player);
        player.damage();
        player.updateInvulnerability(2);

        player.damage();

        assertEquals(0, player.getHp());
        assertEquals(PlayerState.DEAD, player.getState());
    }

    private static void damageAfterInvulnerabilityExpires(Player player) {
        player.damage();
        player.updateInvulnerability(2);
    }
}
