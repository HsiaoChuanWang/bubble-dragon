package com.bubble.dragon.entity.weapon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.bubble.dragon.entity.enemy.Enemy;
import com.bubble.dragon.util.Constants;

class BubbleTest {
    private static final double EPSILON = 0.000_001;

    @Test
    void startsHorizontalActiveAndWithoutTrappedEnemy() {
        Bubble bubble = new Bubble(10, 20, 150);

        assertEquals(BubbleMovementState.HORIZONTAL, bubble.getMovementState());
        assertTrue(bubble.isActive());
        assertFalse(bubble.hasTrappedEnemy());
        assertNull(bubble.getTrappedEnemy());
        assertEquals(150, bubble.getVelocityX(), EPSILON);
    }

    @Test
    void updateAgeAccumulatesElapsedTime() {
        Bubble bubble = new Bubble(10, 20, 150);

        bubble.updateAge(0.25);
        bubble.updateAge(0.75);

        assertEquals(1.0, bubble.getAge(), EPSILON);
    }

    @Test
    void deactivateMarksBubbleInactive() {
        Bubble bubble = new Bubble(10, 20, 150);

        bubble.deactivate();

        assertFalse(bubble.isActive());
    }

    @Test
    void startRisingChangesStateAndVelocity() {
        Bubble bubble = new Bubble(10, 20, 150);

        bubble.startRising();

        assertEquals(BubbleMovementState.RISING, bubble.getMovementState());
        assertEquals(0, bubble.getVelocityX(), EPSILON);
        assertEquals(-Constants.BUBBLE_RISE_SPEED, bubble.getVelocityY(), EPSILON);
    }

    @Test
    void stopAtTopStopsMovement() {
        Bubble bubble = new Bubble(10, 20, 150);
        bubble.startRising();

        bubble.stopAtTop();

        assertEquals(BubbleMovementState.STOPPED_AT_TOP, bubble.getMovementState());
        assertEquals(0, bubble.getVelocityX(), EPSILON);
        assertEquals(0, bubble.getVelocityY(), EPSILON);
    }

    @Test
    void trapStoresEnemyAndStartsRising() {
        Bubble bubble = new Bubble(10, 20, 150);
        Enemy enemy = new Enemy(100, 100, 1);

        bubble.trap(enemy);

        assertSame(enemy, bubble.getTrappedEnemy());
        assertTrue(bubble.hasTrappedEnemy());
        assertEquals(BubbleMovementState.RISING, bubble.getMovementState());
        assertEquals(0, bubble.getVelocityX(), EPSILON);
        assertEquals(Constants.TRAPPED_BUBBLE_VERTICAL_SPEED, bubble.getVelocityY(), EPSILON);
    }

    @Test
    void trapResizesBubbleWithoutMovingItsCenter() {
        Bubble bubble = new Bubble(10, 20, 150);
        Enemy enemy = new Enemy(100, 100, 1);
        double centerXBeforeTrap = bubble.getX() + bubble.getWidth() / 2;
        double centerYBeforeTrap = bubble.getY() + bubble.getHeight() / 2;

        bubble.trap(enemy);

        assertEquals(Constants.TRAPPED_BUBBLE_SIZE, bubble.getWidth(), EPSILON);
        assertEquals(Constants.TRAPPED_BUBBLE_SIZE, bubble.getHeight(), EPSILON);
        assertEquals(centerXBeforeTrap, bubble.getX() + bubble.getWidth() / 2, EPSILON);
        assertEquals(centerYBeforeTrap, bubble.getY() + bubble.getHeight() / 2, EPSILON);
    }

    @Test
    void shakeOffsetUsesAbsoluteOffsetWithoutAccumulatingDrift() {
        Bubble bubble = new Bubble(10, 20, 150);

        bubble.setShakeOffsetX(3);
        assertEquals(13, bubble.getX(), EPSILON);

        bubble.setShakeOffsetX(-2);
        assertEquals(8, bubble.getX(), EPSILON);

        bubble.setShakeOffsetX(-2);
        assertEquals(8, bubble.getX(), EPSILON);

        bubble.setShakeOffsetX(0);
        assertEquals(10, bubble.getX(), EPSILON);
    }
}
