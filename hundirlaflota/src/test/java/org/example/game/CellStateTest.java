package org.example.game;

import org.junit.jupiter.api.Test;

import com.example.game.CellState;

import static org.junit.jupiter.api.Assertions.*;

class CellStateTest {

    @Test
    void testEnumValuesExist() {
        // Comprobar que el enum tiene 4 estados
        CellState[] values = CellState.values();
        assertEquals(4, values.length, "4 estados");

        // Verificar los nombres
        assertArrayEquals(
                new CellState[] { CellState.EMPTY, CellState.SHIP, CellState.HIT, CellState.MISS },
                values,
                "Los valores del enum deben estar en el orden correcto");
    }

    @Test
    void testValueOfReturnsCorrectEnum() {
        assertEquals(CellState.EMPTY, CellState.valueOf("EMPTY"));
        assertEquals(CellState.SHIP, CellState.valueOf("SHIP"));
        assertEquals(CellState.HIT, CellState.valueOf("HIT"));
        assertEquals(CellState.MISS, CellState.valueOf("MISS"));
    }

    @Test
    void testEnumNames() {
        assertEquals("EMPTY", CellState.EMPTY.name());
        assertEquals("SHIP", CellState.SHIP.name());
        assertEquals("HIT", CellState.HIT.name());
        assertEquals("MISS", CellState.MISS.name());
    }
}
