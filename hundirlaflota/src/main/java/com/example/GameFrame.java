package com.example;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class GameFrame extends JFrame {
    private static final int SIZE = 10;
    private BoardPanel playerPanel;
    private BoardPanel enemyPanel;
    private GameEngine engine;
    private JLabel statusLabel;

    // NUEVO: lista de tamaños de barcos a colocar
    private Queue<Integer> shipsToPlace = new LinkedList<>(Arrays.asList(5, 4, 3, 3, 2));
    private boolean placementPhase = true;
    private boolean horizontal = true;

    public GameFrame() {
        super("Hundir la Flota");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        engine = new GameEngine(SIZE);
        // Tablero del jugador vacío (sin barcos)
        engine.getPlayerBoard().clearBoard();

        playerPanel = new BoardPanel(engine.getPlayerBoard(), false);
        enemyPanel = new BoardPanel(engine.getEnemyBoard(), true);

        // --- COLOCACIÓN MANUAL ---
        playerPanel.setCellClickListener((r, c) -> {
            if (!placementPhase) return;

            int shipSize = shipsToPlace.peek(); // siguiente barco
            if (shipSize == 0) return;

            Board board = engine.getPlayerBoard();
            if (board.canPlaceShip(r, c, shipSize, horizontal)) {
                board.placeShip(new Ship(r, c, shipSize, horizontal));
                shipsToPlace.poll(); // quitar el que ya se colocó
                playerPanel.repaintGrid();

                if (shipsToPlace.isEmpty()) {
                    placementPhase = false;
                    statusLabel.setText("¡Barcos colocados! Empieza el juego. Dispara al enemigo.");
                } else {
                    statusLabel.setText("Coloca un barco de tamaño " + shipsToPlace.peek() +
                            " (" + (horizontal ? "horizontal" : "vertical") + ")");
                }
            } else {
                statusLabel.setText("No se puede colocar el barco ahí.");
            }
        });

        // --- DISPAROS ---
        enemyPanel.setCellClickListener((r, c) -> {
            if (placementPhase) return; // aún colocando barcos
            if (engine.isGameOver()) return;

            boolean valid = engine.playerShoot(r, c);
            if (!valid) return;
            refreshBoards();

            if (engine.isGameOver()) {
                statusLabel.setText("¡Has ganado!");
                return;
            }

            engine.cpuTurn();
            refreshBoards();

            if (engine.isGameOver()) {
                statusLabel.setText("Has perdido. La CPU ha ganado.");
                return;
            }

            statusLabel.setText("Dispara al tablero enemigo. " + engine.getStats());
        });

        // --- BOTONES Y UI ---
        JButton rotate = new JButton("Rotar (H/V)");
        rotate.addActionListener(e -> {
            horizontal = !horizontal;
            if (placementPhase)
                statusLabel.setText("Modo: " + (horizontal ? "Horizontal" : "Vertical"));
        });

        JButton restart = new JButton("Reiniciar");
        restart.addActionListener(e -> restartGame());

        statusLabel = new JLabel("Coloca un barco de tamaño " + shipsToPlace.peek(), SwingConstants.CENTER);

        JPanel controls = new JPanel();
        controls.add(rotate);
        controls.add(restart);

        JPanel center = new JPanel(new GridLayout(1, 2, 10, 10));
        center.add(wrapWithTitle(playerPanel, "Tu tablero"));
        center.add(wrapWithTitle(enemyPanel, "Tablero enemigo"));

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(statusLabel, BorderLayout.CENTER);
        bottom.add(controls, BorderLayout.EAST);

        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().add(center, BorderLayout.CENTER);
        getContentPane().add(bottom, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    private void restartGame() {
        engine.reset();
        engine.getPlayerBoard().clearBoard();
        shipsToPlace = new LinkedList<>(Arrays.asList(5, 4, 3, 3, 2));
        placementPhase = true;
        horizontal = true;
        playerPanel.setBoard(engine.getPlayerBoard());
        enemyPanel.setBoard(engine.getEnemyBoard());
        refreshBoards();
        statusLabel.setText("Coloca un barco de tamaño " + shipsToPlace.peek());
    }

    private JPanel wrapWithTitle(JComponent comp, String title) {
        JPanel p = new JPanel(new BorderLayout());
        JLabel l = new JLabel(title, SwingConstants.CENTER);
        l.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        p.add(l, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private void refreshBoards() {
        playerPanel.repaintGrid();
        enemyPanel.repaintGrid();
    }
}
