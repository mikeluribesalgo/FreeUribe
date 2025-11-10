package com.example.gui;

import javax.swing.JButton;

public class CellButton extends JButton {
    public final int row;
    public final int col;

    public CellButton(int r, int c) {
        super();
        row = r;
        col = c;
    }
}
