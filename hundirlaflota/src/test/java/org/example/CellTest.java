package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.Cell;
import com.example.CellState;
import com.example.Ship;

import static org.junit.jupiter.api.Assertions.*;

class CellTest {

    private Cell cell;

    @BeforeEach
    void setUp() {
        cell = new Cell();
    }

    @Test
    void testInitialStateIsEmpty() {
        assertEquals(CellState.EMPTY, cell.getState(), "El estado inicial debe ser EMPTY");
        assertNull(cell.getShip(), "Inicialmente no debe tener un barco asignado");
    }

    @Test
    void testSetAndGetState() {
        cell.setState(CellState.HIT);
        assertEquals(CellState.HIT, cell.getState(), "El estado deberia cambiar a HIT");

        cell.setState(CellState.MISS);
        assertEquals(CellState.MISS, cell.getState(), "El estado deberia cambiar a MISS");
    }

    @Test
    void testSetAndGetShip() {
        Ship ship = new Ship(0, 0, 2, true);
        cell.setShip(ship);
        assertEquals(ship, cell.getShip(), "El barco asignado debe ser el mismo que el obtenido");
    }

    @Test
    void testReplaceShipWithNull() {
        Ship ship = new Ship(1, 1, 3, false);
        cell.setShip(ship);
        cell.setShip(null);
        assertNull(cell.getShip(), "Despues de establecer null, no debe haber barco en la celda");
    }

    @Test
    void testChangeStateMultipleTimes() {
        cell.setState(CellState.SHIP);
        cell.setState(CellState.HIT);
        cell.setState(CellState.MISS);
        assertEquals(CellState.MISS, cell.getState(), "Debe reflejar el ultimo estado establecido");
    }
}
