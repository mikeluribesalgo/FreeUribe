package com.example.gui;

import javax.swing.JButton;

public class CellButton extends JButton {
    final int row;
    final int col;

    CellButton(int r, int c) {
        super();
        row = r;
        col = c;
    }
}
