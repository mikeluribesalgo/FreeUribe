package org.example.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.game.Board;
import com.example.game.CellState;
import com.example.game.Ship;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    private Board board;

    @BeforeEach
    void setUp() {
        board = new Board(10);
    }

    @Test
    void testCanPlaceShipInsideBounds() {
        boolean result = board.canPlaceShip(0, 0, 3, true);
        assertTrue(result, "El barco debería caber horizontalmente en (0,0)");
    }

    @Test
    void testCannotPlaceShipOutsideBounds() {
        boolean result = board.canPlaceShip(0, 8, 3, true);
        assertFalse(result, "El barco no debería caber fuera del límite del tablero");
    }

    @Test
    void testCannotPlaceShipOverAnother() {
        Ship s1 = new Ship(0, 0, 3, true);
        board.placeShip(s1);
        boolean result = board.canPlaceShip(0, 1, 3, true);
        assertFalse(result, "No debería poder colocar un barco sobre otro");
    }

    @Test
    void testPlaceShipMarksCellsAsShip() {
        Ship ship = new Ship(1, 1, 2, true);
        board.placeShip(ship);
        assertEquals(CellState.SHIP, board.getCell(1, 1).getState());
        assertEquals(CellState.SHIP, board.getCell(1, 2).getState());
    }

    @Test
    void testClearBoardResetsCells() {
        Ship ship = new Ship(2, 2, 2, false);
        board.placeShip(ship);
        board.clearBoard();

        for (int r = 0; r < board.getSize(); r++) {
            for (int c = 0; c < board.getSize(); c++) {
                assertEquals(CellState.EMPTY, board.getCell(r, c).getState(),
                        "Todas las celdas deben estar vacías tras limpiar el tablero");
            }
        }
        assertEquals(0, board.getShips().size(), "No deberían quedar barcos tras limpiar");
    }

    @Test
    void testHasRemainingShipsAndCount() {
        Ship ship1 = new Ship(0, 0, 2, true);
        Ship ship2 = new Ship(5, 5, 3, false);

        board.placeShip(ship1);
        board.placeShip(ship2);

        // Ambos están vivos
        assertTrue(board.hasRemainingShips());
        assertEquals(2, board.remainingShipsCount());

        // Hundimos uno
        ship1.hit();
        ship1.hit();

        assertTrue(board.hasRemainingShips(), "Aún queda un barco activo");
        assertEquals(1, board.remainingShipsCount());

        // Hundimos el segundo
        ship2.hit();
        ship2.hit();
        ship2.hit();

        assertFalse(board.hasRemainingShips(), "No deberían quedar barcos activos");
        assertEquals(0, board.remainingShipsCount());
    }
}