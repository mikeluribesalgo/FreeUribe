package com.example;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.GridLayout;
import java.awt.BorderLayout;

public class GameFrame extends JFrame {
    private static final int SIZE = 10;
    private BoardPanel playerPanel;
    private BoardPanel enemyPanel;
    private GameEngine engine;
    private JLabel statusLabel;

    public GameFrame() {
        super("Hundir la Flota - Paso 1");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        engine = new GameEngine(SIZE);

        playerPanel = new BoardPanel(engine.getPlayerBoard(), false);
        enemyPanel = new BoardPanel(engine.getEnemyBoard(), true);

        enemyPanel.setCellClickListener((r, c) -> {
            if (engine.isGameOver())
                return;
            boolean valid = engine.playerShoot(r, c);
            if (!valid)
                return;
            refreshBoards();

            if (engine.isGameOver()) {
                statusLabel.setText("¡Has ganado!");
                return;
            }

            // CPU turn
            engine.cpuTurn();
            refreshBoards();

            if (engine.isGameOver()) {
                statusLabel.setText("Has perdido. La CPU ha ganado.");
                return;
            }

            statusLabel.setText("Dispara al tablero enemigo. " + engine.getStats());
        });

        JPanel center = new JPanel(new GridLayout(1, 2, 10, 10));
        center.add(wrapWithTitle(playerPanel, "Tu tablero"));
        center.add(wrapWithTitle(enemyPanel, "Tablero enemigo"));

        statusLabel = new JLabel("Colocando barcos...", SwingConstants.CENTER);

        JButton restart = new JButton("Reiniciar");
        restart.addActionListener(e -> {
            engine.reset();
            playerPanel.setBoard(engine.getPlayerBoard());
            enemyPanel.setBoard(engine.getEnemyBoard());
            refreshBoards();
            statusLabel.setText("Partida reiniciada. Dispara al tablero enemigo.");
        });

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(statusLabel, BorderLayout.CENTER);
        bottom.add(restart, BorderLayout.EAST);

        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().add(center, BorderLayout.CENTER);
        getContentPane().add(bottom, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);

        // Inicializar estado
        refreshBoards();
        statusLabel.setText("Dispara al tablero enemigo. " + engine.getStats());
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
