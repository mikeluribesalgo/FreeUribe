package com.example;

import javax.swing.SwingUtilities;

public class HundirLaFlota {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameFrame().setVisible(true));
    }
}