package org.example;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.Board;
import com.example.CellState;
import com.example.GameEngine;
import com.example.Ship;

class GameEngineTest {

    private GameEngine engine;

    @BeforeEach
    void setup() {
        engine = new GameEngine(10);
    }

    @Test
    void testBoardsInitialized() {
        assertNotNull(engine.getPlayerBoard(), "El tablero del jugador debe inicializarse");
        assertNotNull(engine.getEnemyBoard(), "El tablero enemigo debe inicializarse");
        assertEquals(10, engine.getPlayerBoard().size, "El tamaño del tablero debe coincidir con el valor pasado");
        assertEquals(10, engine.getEnemyBoard().size, "El tamaño del tablero debe coincidir con el valor pasado");
    }

    @Test
    void testResetCreatesNewBoards() {
        Board oldPlayerBoard = engine.getPlayerBoard();
        Board oldEnemyBoard = engine.getEnemyBoard();
        engine.reset();
        assertNotSame(oldPlayerBoard, engine.getPlayerBoard(), "El tablero del jugador debe recrearse en reset()");
        assertNotSame(oldEnemyBoard, engine.getEnemyBoard(), "El tablero enemigo debe recrearse en reset()");
    }

    @Test
    void testPlayerShootMarksHitOrMiss() {
        Board enemy = engine.getEnemyBoard();
        // Forzamos una celda a contener un barco para garantizar un HIT
        Ship ship = new Ship(0, 0, 2, true);
        enemy.placeShip(ship);

        boolean result = engine.playerShoot(0, 0);
        assertTrue(result, "Debe devolver true al disparar");
        assertTrue(enemy.cells[0][0].getState() == CellState.HIT || enemy.cells[0][0].getState() ==CellState.SHIP,
                "Debe marcar la celda como golpe o fallo");
    }

    

    @Test
    void testCpuTurnMarksAHitOrMiss() {
        Board player = engine.getPlayerBoard();
        int hitsBefore = countCells(player, CellState.HIT) + countCells(player, CellState.MISS);
        engine.cpuTurn();
        int hitsAfter = countCells(player, CellState.HIT) + countCells(player, CellState.MISS);
        assertTrue(hitsAfter > hitsBefore, "CPU debe marcar al menos una celda como probada");
    }

    @Test
    void testGetStatsReturnsExpectedFormat() {
        String stats = engine.getStats();
        assertTrue(stats.contains("Barcos tuyos"), "Debe contener el texto de barcos tuyos");
        assertTrue(stats.contains("Barcos enemigos"), "Debe contener el texto de barcos enemigos");
    }

    // --- Helper method ---
    private int countCells(Board board, CellState state) {
        int count = 0;
        for (int r = 0; r < board.size; r++)
            for (int c = 0; c < board.size; c++)
                if (board.cells[r][c].getState() == state)
                    count++;
        return count;
    }
}
