package com.example;

import javax.swing.JButton;

public class CellButton extends JButton {
    final int row, col;

    CellButton(int r, int c) {
        super();
        row = r;
        col = c;
    }
}
