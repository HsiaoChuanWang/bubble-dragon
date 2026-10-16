package com.bubble.dragon.entity.enemy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class EnemyTest {

    @Test
    void constructorNormalizesDirection() {
        assertEquals(1, new Enemy(0, 0, 1).getDirection());
        assertEquals(1, new Enemy(0, 0, 0).getDirection());
        assertEquals(1, new Enemy(0, 0, 8).getDirection());
        assertEquals(-1, new Enemy(0, 0, -1).getDirection());
        assertEquals(-1, new Enemy(0, 0, -8).getDirection());
    }

    @Test
    void reverseChangesDirection() {
        Enemy enemy = new Enemy(0, 0, 1);

        enemy.reverse();
        assertEquals(-1, enemy.getDirection());

        enemy.reverse();
        assertEquals(1, enemy.getDirection());
    }

    @Test
    void faceTurnsTowardTargetRelativeToCenter() {
        Enemy enemy = new Enemy(100, 0, 1);

        enemy.face(129);
        assertEquals(-1, enemy.getDirection());

        enemy.face(130);
        assertEquals(1, enemy.getDirection());
    }

    @Test
    void jumpAndFlyStatesCanBeTrapped() {
        Enemy jumpingEnemy = new Enemy(0, 0, 1);
        Enemy flyingEnemy = new Enemy(0, 0, 1);
        flyingEnemy.startFly();

        assertTrue(jumpingEnemy.canBeTrapped());
        assertTrue(flyingEnemy.canBeTrapped());

        jumpingEnemy.trap();
        flyingEnemy.trap();

        assertEquals(EnemyState.TRAPPED, jumpingEnemy.getState());
        assertEquals(EnemyState.TRAPPED, flyingEnemy.getState());
    }

    @Test
    void trapStopsMovementAndStartsEscapeTimer() {
        Enemy enemy = new Enemy(0, 0, 1);
        enemy.setVelocityX(120);
        enemy.setVelocityY(-80);

        enemy.trap();

        assertEquals(EnemyState.TRAPPED, enemy.getState());
        assertEquals(0, enemy.getVelocityX());
        assertEquals(0, enemy.getVelocityY());
        assertTrue(enemy.getTrapTimer().isRunning());
        assertFalse(enemy.getTrapTimer().isFinished());
    }

    @Test
    void trappedStoppedAndDefeatedEnemiesCannotBeTrapped() {
        Enemy trappedEnemy = new Enemy(0, 0, 1);
        trappedEnemy.trap();
        assertFalse(trappedEnemy.canBeTrapped());

        Enemy stoppedEnemy = new Enemy(0, 0, 1);
        stoppedEnemy.startStop();
        stoppedEnemy.trap();
        assertEquals(EnemyState.STOP, stoppedEnemy.getState());
        assertFalse(stoppedEnemy.getTrapTimer().isRunning());

        Enemy defeatedEnemy = new Enemy(0, 0, 1);
        defeatedEnemy.defeat();
        defeatedEnemy.trap();
        assertEquals(EnemyState.DEFEATED, defeatedEnemy.getState());
        assertFalse(defeatedEnemy.getTrapTimer().isRunning());
    }

    @Test
    void escapeReturnsToJumpAndResetsTemporaryState() {
        Enemy enemy = new Enemy(0, 0, 1);
        enemy.setChasing(true);
        enemy.trap();
        enemy.getTrapTimer().update(1);

        enemy.escape();

        assertEquals(EnemyState.JUMP, enemy.getState());
        assertFalse(enemy.isChasing());
        assertFalse(enemy.getTrapTimer().isRunning());
        assertFalse(enemy.getTrapTimer().isFinished());
        assertEquals(0, enemy.getStateElapsed());
    }

    @Test
    void defeatChangesStateAndStopsTrapTimer() {
        Enemy enemy = new Enemy(0, 0, 1);
        enemy.trap();

        enemy.defeat();

        assertEquals(EnemyState.DEFEATED, enemy.getState());
        assertFalse(enemy.getTrapTimer().isRunning());
        assertFalse(enemy.canBeTrapped());
    }
}
