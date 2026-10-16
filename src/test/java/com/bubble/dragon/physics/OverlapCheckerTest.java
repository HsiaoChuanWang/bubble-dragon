package com.bubble.dragon.physics;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class OverlapCheckerTest {
    // 發生重疊時，應回傳 true
    @Test
    void detectsOverlap() {
        assertTrue(OverlapChecker.overlaps(0, 0, 10, 10, 5, 5, 10, 10));
    }

    // 一個矩形完全位於另一個矩形內部時，仍應視為重疊
    @Test
    void detectsContainedRectangle() {
        assertTrue(OverlapChecker.overlaps(0, 0, 20, 20, 5, 5, 5, 5));
    }

    // 從四個方向局部進入另一個矩形時都應視為重疊
    @Test
    void detectsOverlapFromEveryDirection() {
        assertTrue(OverlapChecker.overlaps(0, 0, 10, 10, -5, 2, 10, 5)); // 左側
        assertTrue(OverlapChecker.overlaps(0, 0, 10, 10, 5, 2, 10, 5)); // 右側
        assertTrue(OverlapChecker.overlaps(0, 0, 10, 10, 2, -5, 5, 10)); // 上方
        assertTrue(OverlapChecker.overlaps(0, 0, 10, 10, 2, 5, 5, 10)); // 下方
    }

    // 完全分離時，應回傳 false
    @Test
    void rejectsSeparatedObjects() {
        assertFalse(OverlapChecker.overlaps(0, 0, 10, 10, 11, 0, 10, 10));
    }

    // 僅接觸邊界時，應回傳 false
    @Test
    void touchingEdgesIsNotOverlap() {
        assertFalse(OverlapChecker.overlaps(0, 0, 10, 10, 10, 0, 10, 10));
    }

    // 只有角落相接時，兩個矩形沒有實際重疊面積
    @Test
    void touchingCornersIsNotOverlap() {
        assertFalse(OverlapChecker.overlaps(0, 0, 10, 10, 10, 10, 5, 5));
    }

    // AABB 判定也必須支援遊戲中可能出現的負數與小數座標
    @Test
    void supportsNegativeAndDecimalCoordinates() {
        assertTrue(OverlapChecker.overlaps(-5.5, -4.5, 3.0, 3.0,
                -3.0, -3.0, 2.25, 2.25));
        assertFalse(OverlapChecker.overlaps(-5.5, -4.5, 2.5, 2.5,
                -3.0, -2.0, 1.5, 1.5));
    }

    // 交換 A、B 的輸入順序，不應改變碰撞判定結果
    @Test
    void resultIsSymmetricWhenRectanglesAreSwapped() {
        boolean firstOrder = OverlapChecker.overlaps(1.5, 2.5, 4.0, 5.0,
                4.5, 5.5, 3.0, 2.0);
        boolean swappedOrder = OverlapChecker.overlaps(4.5, 5.5, 3.0, 2.0,
                1.5, 2.5, 4.0, 5.0);

        assertTrue(firstOrder);
        assertTrue(swappedOrder);
    }
}
