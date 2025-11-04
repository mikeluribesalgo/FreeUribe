package com.example;

import javax.swing.SwingUtilities;

//mikel

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameFrame().setVisible(true));
    }
}