package com.example.game;

public class Cell {
    private CellState state = CellState.EMPTY;
    private Ship ship = null;

    public CellState getState() {
        return state;
    }

    public void setState(CellState state) {
        this.state = state;
    }

    public Ship getShip() {
        return ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }
}