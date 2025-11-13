package com.example.game;

import javax.swing.SwingUtilities;

import com.example.gui.GameFrame;



public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameFrame().setVisible(true));
    }
}