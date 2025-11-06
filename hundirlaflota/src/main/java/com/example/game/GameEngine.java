package com.example.game;

import java.awt.Point;
import java.util.*;

public class GameEngine {
    final int size;
    private Board playerBoard;
    private Board enemyBoard;
    private Random rand = new Random();
    private Set<Point> cpuTried = new HashSet<>();
    private GameMode mode = GameMode.VS_CPU;
    private NetworkManager network;

    private static final int[] SHIP_SIZES = {5, 4, 3, 3, 2};

    public GameEngine(int size) {
        this.size = size;
        reset();
    }

    public void reset() {
        playerBoard = new Board(size);
        enemyBoard = new Board(size);
        if (mode == GameMode.VS_CPU) {
            placeShipsRandomly(enemyBoard);
        }
        cpuTried.clear();
    }

    public void setNetworkManager(NetworkManager net) {
        this.network = net;
        this.mode = GameMode.VS_PLAYER;
    }

    public NetworkManager getNetworkManager() {
        return network;
    }

    public GameMode getMode() {
        return mode;
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
                throw new IllegalArgumentException("No se pudo colocar un barco después de muchos intentos");
        }
    }

    public boolean playerShoot(int row, int col) {
        Cell cell = enemyBoard.cells[row][col];
        if (cell.getState() == CellState.HIT || cell.getState() == CellState.MISS) return false;
        if (cell.getState() == CellState.SHIP) {
            cell.setState(CellState.HIT);
            cell.getShip().hit();
            return true;
        } else {
            cell.setState(CellState.MISS);
            return true;
        }
    }

    public void cpuTurn() {
        if (mode == GameMode.VS_PLAYER) return;
        List<Point> options = new ArrayList<>();
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++) {
                Cell cell = playerBoard.cells[r][c];
                if (cell.getState() != CellState.HIT && cell.getState() != CellState.MISS)
                    options.add(new Point(r, c));
            }
        if (options.isEmpty()) return;
        Point p = options.get(rand.nextInt(options.size()));
        Cell target = playerBoard.cells[p.x][p.y];
        if (target.getState() == CellState.SHIP) {
            target.setState(CellState.HIT);
            target.getShip().hit();
        } else {
            target.setState(CellState.MISS);
        }
    }

    public boolean isGameOver() {
        boolean playerShips = playerBoard.hasRemainingShips();
        boolean enemyShips = enemyBoard.hasRemainingShips();
        return !playerShips || !enemyShips;
    }

    public String getStats() {
        return String.format("Barcos tuyos: %d | Barcos enemigos: %d",
                playerBoard.remainingShipsCount(), enemyBoard.remainingShipsCount());
    }
}
