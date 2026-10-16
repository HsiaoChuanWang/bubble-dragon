package com.bubble.dragon.map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LevelLoaderTest {
    private static final double EPSILON = 0.000_001;
    private final LevelLoader loader = new LevelLoader();

    @Test
    void loadsLevelOneWithExpectedPlayerAndEntityData() {
        GameMap map = loader.load("/maps/level1.json");

        assertNotNull(map);
        assertEquals(80, map.playerX, EPSILON);
        assertEquals(550, map.playerY, EPSILON);
        assertEquals(7, map.tiles.size());
        assertEquals(3, map.enemies.size());
    }

    @Test
    void loadsLevelTwoWithExpectedPlayerAndEntityData() {
        GameMap map = loader.load("/maps/level2.json");

        assertNotNull(map);
        assertEquals(70, map.playerX, EPSILON);
        assertEquals(560, map.playerY, EPSILON);
        assertEquals(7, map.tiles.size());
        assertEquals(4, map.enemies.size());
    }

    @Test
    void levelOneContainsExpectedTextureAndTileData() {
        GameMap map = loader.load("/maps/level1.json");

        assertFalse(map.tileTextures.isEmpty());
        assertTrue(map.tileTextures.contains("/images/earth01.png"));
        assertTrue(map.tileTextures.contains("/images/earth02.png"));
        assertFalse(map.tiles.isEmpty());
        assertTrue(map.tiles.stream().allMatch(tile -> tile.width > 0));
        assertTrue(map.tiles.stream().allMatch(tile -> tile.texturePattern != null));
        assertTrue(map.tiles.stream().allMatch(tile -> !tile.texturePattern.isEmpty()));
    }

    @Test
    void levelTwoContainsExpectedTextureAndTileData() {
        GameMap map = loader.load("/maps/level2.json");

        assertFalse(map.tileTextures.isEmpty());
        assertTrue(map.tileTextures.contains("/images/stone01.png"));
        assertTrue(map.tileTextures.contains("/images/stone02.png"));
        assertFalse(map.tiles.isEmpty());
        assertTrue(map.tiles.stream().allMatch(tile -> tile.width > 0));
        assertTrue(map.tiles.stream().allMatch(tile -> tile.texturePattern != null));
        assertTrue(map.tiles.stream().allMatch(tile -> !tile.texturePattern.isEmpty()));
    }

    @Test
    void missingLevelThrowsIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> loader.load("/maps/missing-level.json"));

        assertTrue(exception.getMessage().contains("/maps/missing-level.json"));
    }
}
