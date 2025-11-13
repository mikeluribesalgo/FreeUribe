package com.example.game;

public class Ship {
    final int r; 
    final int c;
    final int length;
    final boolean horizontal;
    private int hits = 0;

    public Ship(int r, int c, int length, boolean horizontal) {
        this.r = r;
        this.c = c;
        this.length = length;
        this.horizontal = horizontal;
    }

    public void hit() {
        hits++;
    }

    public boolean isSunk() {
        return hits >= length;
    }
}