package com.example;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.awt.Point;

public class GameEngine {
    final int size;
    private Board playerBoard;
    private Board enemyBoard;
    private Random rand = new Random();
    private Set<Point> cpuTried = new HashSet<>();

    // simple ship sizes: portaaviones(5), acorazado(4), crucero(3), submarino(3),
    // destructor(2)
    private static final int[] SHIP_SIZES = { 5, 4, 3, 3, 2 };

    public GameEngine(int size) {
        this.size = size;
        reset();
    }

    public void reset() {
        playerBoard = new Board(size);
        enemyBoard = new Board(size);
        placeShipsRandomly(playerBoard);
        placeShipsRandomly(enemyBoard);
        cpuTried.clear();
    }

    public Board getPlayerBoard() {
        return playerBoard;
    }

    public Board getEnemyBoard() {
        return enemyBoard;
    }

    private void placeShipsRandomly(Board board) {
        for (int s : SHIP_SIZES) {
            boolean placed = false;
            int attempts = 0;
            while (!placed && attempts < 500) {
                attempts++;
                boolean horizontal = rand.nextBoolean();
                int r = rand.nextInt(size);
                int c = rand.nextInt(size);
                if (board.canPlaceShip(r, c, s, horizontal)) {
                    board.placeShip(new Ship(r, c, s, horizontal));
                    placed = true;
                }
            }
            if (!placed)
                throw new RuntimeException("No se pudo colocar un barco después de muchos intentos");
        }
    }

    public boolean playerShoot(int row, int col) {
        Cell cell = enemyBoard.cells[row][col];
        if (cell.state == CellState.HIT || cell.state == CellState.MISS)
            return false; // ya probado
        if (cell.state == CellState.SHIP) {
            cell.state = CellState.HIT;
            cell.ship.hit();
            return true;
        } else {
            cell.state = CellState.MISS;
            return true;
        }
    }

    public void cpuTurn() {
        // CPU dispara aleatoriamente entre celdas no probadas
        List<Point> options = new ArrayList<>();
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++) {
                Cell cell = playerBoard.cells[r][c];
                if (cell.state != CellState.HIT && cell.state != CellState.MISS)
                    options.add(new Point(r, c));
            }
        if (options.isEmpty())
            return;
        Point p = options.get(rand.nextInt(options.size()));
        Cell target = playerBoard.cells[p.x][p.y];
        if (target.state == CellState.SHIP) {
            target.state = CellState.HIT;
            target.ship.hit();
        } else {
            target.state = CellState.MISS;
        }
    }

    public boolean isGameOver() {
        boolean playerShips = playerBoard.hasRemainingShips();
        boolean enemyShips = enemyBoard.hasRemainingShips();
        return !playerShips || !enemyShips;
    }

    public String getStats() {
        return String.format("Barcos tuyos: %d Barcos enemigos: %d",
                playerBoard.remainingShipsCount(), enemyBoard.remainingShipsCount());
    }

}
