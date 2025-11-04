package com.example;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.Queue;



public class GameFrame extends JFrame {
    private static final int SIZE = 10;
    private BoardPanel playerPanel;
    private BoardPanel enemyPanel;
    private transient  GameEngine engine;
    private JLabel statusLabel;

    private Queue<Integer> shipsToPlace;
    private boolean placementPhase;
    private boolean horizontal;

    // 🔹 Constructor por defecto
    public GameFrame() {
        this(new GameEngine(SIZE)); // Llama al otro constructor
    }

    // 🔹 Constructor sobrecargado (permite pasar un GameEngine externo)
    public GameFrame(GameEngine engine) {
        super("Hundir la Flota");
        this.engine = engine;
        initGameFrame();
    }

    // --- Inicialización general ---
    private void initGameFrame() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        setupInitialState();
        setupBoards();
        setupListeners();
        setupUI();

        pack();
        setLocationRelativeTo(null);
    }

    // --- Estado inicial ---
    private void setupInitialState() {
        shipsToPlace = new LinkedList<>(Arrays.asList(5, 4, 3, 3, 2));
        placementPhase = true;
        horizontal = true;
        engine.getPlayerBoard().clearBoard();
    }

    // --- Crear paneles ---
    private void setupBoards() {
        playerPanel = new BoardPanel(engine.getPlayerBoard(), false);
        enemyPanel = new BoardPanel(engine.getEnemyBoard(), true);
    }

    // --- Listeners ---
    private void setupListeners() {
        setupPlacementListener();
        setupEnemyListener();
    }

    private void setupPlacementListener() {
        playerPanel.setCellClickListener((r, c) -> {
            if (!placementPhase) return;

            Integer shipSize = shipsToPlace.peek();
            if (shipSize == null) return;

            Board board = engine.getPlayerBoard();
            if (board.canPlaceShip(r, c, shipSize, horizontal)) {
                board.placeShip(new Ship(r, c, shipSize, horizontal));
                shipsToPlace.poll();
                playerPanel.repaintGrid();
                updatePlacementStatus();
            } else {
                statusLabel.setText("No se puede colocar el barco ahí.");
            }
        });
    }

    private void updatePlacementStatus() {
        if (shipsToPlace.isEmpty()) {
            placementPhase = false;
            statusLabel.setText("¡Barcos colocados! Empieza el juego. Dispara al enemigo.");
        } else {
            statusLabel.setText("Coloca un barco de tamaño " + shipsToPlace.peek() +
                    " (" + (horizontal ? "horizontal" : "vertical") + ")");
        }
    }

    private void setupEnemyListener() {
        enemyPanel.setCellClickListener((r, c) -> {
            if (placementPhase || engine.isGameOver()) return;
            if (!engine.playerShoot(r, c)) return;

            refreshBoards();
            if (checkGameEnd()) return;

            engine.cpuTurn();
            refreshBoards();
            if (checkGameEnd()) return;

            statusLabel.setText("Dispara al tablero enemigo. " + engine.getStats());
        });
    }

    // 🔹 Solo mensajes básicos de fin de juego
    private boolean checkGameEnd() {
        if (engine.isGameOver()) {
            statusLabel.setText("El juego ha terminado.");
            return true;
        }
        return false;
    }

    // --- UI ---
    private void setupUI() {
        JButton rotate = new JButton("Rotar (H/V)");
        rotate.addActionListener(e -> toggleOrientation());

        JButton restart = new JButton("Reiniciar");
        restart.addActionListener(e -> restartGame());

        statusLabel = new JLabel("Coloca un barco de tamañ " + shipsToPlace.peek(), SwingConstants.CENTER);

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
    }

    private void toggleOrientation() {
        horizontal = !horizontal;
        if (placementPhase) {
            statusLabel.setText("Modo: " + (horizontal ? "Horizontal" : "Vertical"));
        }
    }

    private void restartGame() {
        engine.reset();
        setupInitialState();
        playerPanel.setBoard(engine.getPlayerBoard());
        enemyPanel.setBoard(engine.getEnemyBoard());
        refreshBoards();
        statusLabel.setText("Coloca un barco de tam " + shipsToPlace.peek());
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
