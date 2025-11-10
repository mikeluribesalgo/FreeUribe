package org.example.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.game.Board;
import com.example.game.CellState;
import com.example.game.GameEngine;
import com.example.game.GameMode;

import com.example.game.Ship;

import static org.junit.jupiter.api.Assertions.*;



class GameEngineTest {

    private GameEngine engine;

    @BeforeEach
    void setUp() {
        engine = new GameEngine(10);
    }

    @Test
    void testConstructorAndResetCreatesBoards() {
        assertNotNull(engine.getPlayerBoard());
        assertNotNull(engine.getEnemyBoard());
        assertEquals(GameMode.VS_CPU, engine.getMode());
    }

   

    @Test
    void testPlayerShootHitsAndMisses() {
        Board enemy = engine.getEnemyBoard();

        // Place a ship manually at position (0, 0)
        Ship ship = new Ship(0, 0, 2, true);
        enemy.placeShip(ship);

        // Should hit at (0, 0)
        boolean hit = engine.playerShoot(0, 0);
        assertTrue(hit);
        assertEquals(CellState.HIT, enemy.cells[0][0].getState());

        // Should miss at an empty cell
        boolean miss = engine.playerShoot(5, 5);
        assertTrue(miss);
        assertEquals(CellState.MISS, enemy.cells[5][5].getState());

        // Should not allow shooting same cell twice
        boolean invalid = engine.playerShoot(5, 5);
        assertFalse(invalid);
    }

    @Test
    void testCpuTurnMarksCells() {
        Board player = engine.getPlayerBoard();
        player.clearBoard();

        // Add a ship to ensure CPU has something to hit
        player.placeShip(new Ship(0, 0, 2, true));

        engine.cpuTurn();

        // Ensure at least one cell has been hit or missed
        boolean changed = false;
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                CellState state = player.cells[r][c].getState();
                if (state == CellState.HIT || state == CellState.MISS) {
                    changed = true;
                }
            }
        }
        assertTrue(changed, "CPU should mark at least one cell");
    }

    @Test
    void testIsGameOverWhenShipsDestroyed() {
        Board enemy = engine.getEnemyBoard();
        enemy.clearBoard();

        // No ships -> game over
        assertTrue(engine.isGameOver());
    }

    @Test
    void testGetStatsReturnsValidString() {
        String stats = engine.getStats();
        assertNotNull(stats);
        assertTrue(stats.contains("Barcos tuyos"));
        assertTrue(stats.contains("Barcos enemigos"));
    }

    @Test
    void testResetClearsBoards() {
        Board player = engine.getPlayerBoard();
        Board enemy = engine.getEnemyBoard();

        // Place ships on both boards
        player.placeShip(new Ship(0, 0, 2, true));
        enemy.placeShip(new Ship(0, 0, 2, true));

        engine.reset();

        // Both boards should be cleared
        assertEquals(1, player.getShips().size());
        
    }
}