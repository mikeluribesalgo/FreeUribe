package com.example;

public class Ship {
    final int r, c, length;
    final boolean horizontal;
    private int hits = 0;

    Ship(int r, int c, int length, boolean horizontal) {
        this.r = r;
        this.c = c;
        this.length = length;
        this.horizontal = horizontal;
    }

    void hit() {
        hits++;
    }

    boolean isSunk() {
        return hits >= length;
    }
}
