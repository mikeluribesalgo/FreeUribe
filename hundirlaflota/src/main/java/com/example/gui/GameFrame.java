package com.example.gui;

import javax.swing.*;

import com.example.game.Board;
import com.example.game.Cell;
import com.example.game.CellState;
import com.example.game.GameEngine;
import com.example.game.NetworkGameHandler;
import com.example.game.NetworkManager;
import com.example.game.Ship;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.Queue;

public class GameFrame extends JFrame {
    private static final int SIZE = 10;
    private BoardPanel playerPanel;
    private BoardPanel enemyPanel;
    private transient GameEngine engine;
    private JLabel statusLabel;
    private Queue<Integer> shipsToPlace;
    private boolean placementPhase;
    private boolean horizontal;
    private static final String ACTION_1 = "TOCADO";

    // 🔹 NUEVO
    private transient NetworkGameHandler netHandler;

    // --- Constructor principal ---
    public GameFrame() {
        this(new GameEngine(SIZE));
    }

    public GameFrame(GameEngine engine) {
        super("Hundir la Flota");
        this.engine = engine;
        initGameFrame();
    }

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

    private void setupInitialState() {
        shipsToPlace = new LinkedList<>(Arrays.asList(5, 4, 3, 3, 2));
        placementPhase = true;
        horizontal = true;
        engine.getPlayerBoard().clearBoard();
    }

    private void setupBoards() {
        playerPanel = new BoardPanel(engine.getPlayerBoard(), false);
        enemyPanel = new BoardPanel(engine.getEnemyBoard(), true);
    }

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
            // 🔹 Modo de conexión
            askGameMode();
        } else {
            statusLabel.setText("Coloca un barco de tam " + shipsToPlace.peek() +
                    " (" + (horizontal ? "horizontal" : "vertical") + ")");
        }
    }

    // 🔹 NUEVO: elegir modo (CPU o red)
    private void askGameMode() {
        String[] options = {"Contra CPU", "Jugador vs Jugador"};
        int choice = JOptionPane.showOptionDialog(this,
                "Selecciona modo de juego:",
                "Modo de juego",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, options, options[0]);

        if (choice == 1) setupNetworkMode();
        else startVsCpu();
    }

    private void startVsCpu() {
        placementPhase = false;
        statusLabel.setText("¡Barcos colocados! Dispara al enemigo.");
    }

    // 🔹 NUEVO: configurar conexión red
    private void setupNetworkMode() {
        try {
            String[] roles = {"Servidor (Jugador 1)", "Cliente (Jugador 2)"};
            int role = JOptionPane.showOptionDialog(this,
                    "¿Quieres ser el servidor o el cliente?",
                    "Modo red",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null, roles, roles[0]);

            boolean isServer = (role == 0);
            String ip = "127.0.0.1";
            if (!isServer)
                ip = JOptionPane.showInputDialog(this, "Introduce la IP del servidor:", "127.0.0.1");

            NetworkManager net = new NetworkManager(isServer, ip, 5000);
            engine.setNetworkManager(net);

            boolean startFirst = isServer;
            netHandler = new NetworkGameHandler(engine, this, startFirst);
            netHandler.start();

            statusLabel.setText(isServer ? "Eres el jugador 1. Comienzas." : "Eres el jugador 2. Espera tu turno...");
            placementPhase = false;
        } catch (IOException e) {
            showError("Error al iniciar conexión: " + e.getMessage());
        }
    }

    private void setupEnemyListener() {
        enemyPanel.setCellClickListener((r, c) -> {
            if (placementPhase || engine.isGameOver()) return;

            if (engine.getNetworkManager() == null) { // modo CPU
                if (!engine.playerShoot(r, c)) return;
                refreshBoards();
                if (checkGameEnd()) return;
                engine.cpuTurn();
                refreshBoards();
                checkGameEnd();
            } else {
                // 🔹 Modo red
                if (netHandler != null)
                    netHandler.playerShot(r, c);
            }
        });
    }

    private boolean checkGameEnd() {
        if (engine.isGameOver()) {
            statusLabel.setText("El juego ha terminado.");
            return true;
        }
        return false;
    }

    private void setupUI() {
        JButton rotate = new JButton("Rotar (H/V)");
        rotate.addActionListener(e -> toggleOrientation());

        JButton restart = new JButton("Reiniciar");
        restart.addActionListener(e -> restartGame());

        statusLabel = new JLabel("Coloca un barco de tama " + shipsToPlace.peek(), SwingConstants.CENTER);

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

    public void toggleOrientation() {
        horizontal = !horizontal;
        if (placementPhase)
            statusLabel.setText("Modo: " + (horizontal ? "Horizontal" : "Vertical"));
    }

    private void restartGame() {
        engine.reset();
        setupInitialState();
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

    public void refreshBoards() {
        playerPanel.repaintGrid();
        enemyPanel.repaintGrid();
    }

    // 🔹 NUEVOS MÉTODOS AUXILIARES

    public void enableEnemyBoard(boolean enabled) {
        enemyPanel.setEnabled(enabled);
    }

    public String applyEnemyShot(String coord) {
    String[] p = coord.split(",");
    int r = Integer.parseInt(p[0]);
    int c = Integer.parseInt(p[1]);
    Cell cell = engine.getPlayerBoard().getCell(r, c);
    String result;

    
    if (cell.getState() == CellState.HIT) {
        return ACTION_1;
    } else if (cell.getState() == CellState.MISS) {
        return "AGUA";
    }

    
    if (cell.getState() == CellState.SHIP) {
        cell.setState(CellState.HIT);
        cell.getShip().hit();
        result = cell.getShip().isSunk() ? "HUNDIDO" : ACTION_1 ;
    } else {
        cell.setState(CellState.MISS);
        result = "AGUA";
    }

    refreshBoards();
    return result;
}

    public void applyEnemyResult(String coord, String result) {
    String[] p = coord.split(",");
    int r = Integer.parseInt(p[0]);
    int c = Integer.parseInt(p[1]);
    Cell cell = engine.getEnemyBoard().getCell(r, c);

    if (result.equals("AGUA")) {
        cell.setState(CellState.MISS);
    } else if (result.equals(ACTION_1) || result.equals("HUNDIDO")) {
        cell.setState(CellState.HIT);
    }
    refreshBoards();
}

    public void showError(String msg) {
        SwingUtilities.invokeLater(() ->
                JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE));
    }

    public void setStatus(String msg) {
        SwingUtilities.invokeLater(() -> statusLabel.setText(msg));
    }

}
