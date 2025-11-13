package com.example.game;

import java.io.IOException;

import com.example.gui.GameFrame;

public class NetworkGameHandler extends Thread {
    private final GameEngine engine;
    private final GameFrame ui;
    private final NetworkManager net;
    private boolean myTurn;
    private boolean running = true;

    public NetworkGameHandler(GameEngine engine, GameFrame ui, boolean startFirst) {
        this.engine = engine;
        this.ui = ui;
        this.net = engine.getNetworkManager();
        this.myTurn = startFirst;
    }

    @Override
    public void run() {
        boolean disconnected = false;

while (running && !engine.isGameOver() && !disconnected) {
    try {
        ui.enableEnemyBoard(myTurn);

        if (myTurn) {
            ui.setStatus("🎯 Tu turno: dispara al tablero enemigo.");
            
        } else {
            ui.setStatus("⌛ Esperando disparo del oponente...");
            String shot = net.receive(); // formato "r,c"

            if (shot == null) {
                disconnected = true; // en lugar de break
            } else {
                String result = ui.applyEnemyShot(shot);
                net.send(result);
                myTurn = result.equals("AGUA");
            }
        }

    } catch (IOException e) {
        running = false;
        ui.showError("Error de red: " + e.getMessage());
    }
}




        ui.setStatus("El juego ha terminado.");
    }

    // 🔹 Cuando tú haces clic en el tablero enemigo
    public void playerShot(int r, int c) {
        if (!myTurn || engine.isGameOver()) return;

        try {
            String coord = r + "," + c;
            net.send(coord); // envío del disparo
            String response = net.receive(); // respuesta: "AGUA", "TOCADO", "HUNDIDO"

            ui.applyEnemyResult(coord, response);

            // 🔹 Si fallas → turno del oponente
            if (response.equals("AGUA")) {
                myTurn = false;
                ui.enableEnemyBoard(false);
                ui.setStatus("💤 Turno del oponente...");
            } else {
                // 🔹 Si aciertas, repites
                myTurn = true;
                ui.setStatus("🔥 ¡Tocado! Dispara de nuevo.");
            }

        } catch (IOException e) {
            ui.showError("Error de comunicación: " + e.getMessage());
        }
    }
}
