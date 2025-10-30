package com.example;

import java.util.ArrayList;
import java.util.List;

public class Board {
    final int size;
    final Cell[][] cells;
    final List<Ship> ships = new ArrayList<>();

    public Board(int size) {
        this.size = size;
        cells = new Cell[size][size];
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++)
                cells[r][c] = new Cell();
    }

    public int getSize() {
        return size;
    }

    public Cell getCell(int r, int c) {
        return cells[r][c];
    }

    public List<Ship> getShips() {
        return ships;
    }

    public boolean canPlaceShip(int r, int c, int length, boolean horizontal) {
        if (horizontal) {
            if (c + length > size)
                return false;
            for (int i = 0; i < length; i++)
                if (cells[r][c + i].state != CellState.EMPTY)
                    return false;
        } else {
            if (r + length > size)
                return false;
            for (int i = 0; i < length; i++)
                if (cells[r + i][c].state != CellState.EMPTY)
                    return false;
        }
        return true;
    }

    public void placeShip(Ship ship) {
        ships.add(ship);
        int r = ship.r, c = ship.c;
        for (int i = 0; i < ship.length; i++) {
            Cell cell = horizontalCell(ship, i);
            cell.state = CellState.SHIP;
            cell.ship = ship;
        }
    }

    public void clearBoard() {
        ships.clear();
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                cells[r][c].state = CellState.EMPTY;
                cells[r][c].ship = null;
            }
        }
    }

    private Cell horizontalCell(Ship ship, int i) {
        return ship.horizontal ? cells[ship.r][ship.c + i] : cells[ship.r + i][ship.c];
    }

    public boolean hasRemainingShips() {
        for (Ship s : ships)
            if (!s.isSunk())
                return true;
        return false;
    }

    public int remainingShipsCount() {
        int c = 0;
        for (Ship s : ships)
            if (!s.isSunk())
                c++;
        return c;
    }
}
