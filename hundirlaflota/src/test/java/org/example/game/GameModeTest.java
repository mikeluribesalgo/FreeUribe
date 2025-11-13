package org.example.game;

import org.junit.jupiter.api.Test;

import com.example.game.GameMode;

import static org.junit.jupiter.api.Assertions.*;

class GameModeTest {

    @Test
    void testEnumValues() {
        GameMode[] values = GameMode.values();
        assertEquals(2, values.length, "Debe haber dos modos de juego");
        assertArrayEquals(new GameMode[]{GameMode.VS_CPU, GameMode.VS_PLAYER}, values);
    }

    @Test
    void testValueOf() {
        assertEquals(GameMode.VS_CPU, GameMode.valueOf("VS_CPU"));
        assertEquals(GameMode.VS_PLAYER, GameMode.valueOf("VS_PLAYER"));
    }

    @Test
    void testToString() {
        assertEquals("VS_CPU", GameMode.VS_CPU.toString());
        assertEquals("VS_PLAYER", GameMode.VS_PLAYER.toString());
    }
}
