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
    //

    @Override
    public void run() {
        try {
            // 🔹 Sincronización inicial: ambos jugadores confirman "READY"
            net.send("READY");
            String msg = net.receive();
            if (!"READY".equals(msg)) {
                ui.showError("Error de sincronización inicial con el otro jugador.");
                return;
            }
        } catch (IOException e) {
            ui.showError("Error al iniciar partida: " + e.getMessage());
            return;
        }

        // 🔹 Bucle principal de juego
        while (running) {
            try {
                // Si termina el juego, avisamos al UI (donde se ofrece revancha)
                if (engine.isGameOver()) {
                    ui.handleNetworkGameEnd(this);
                    return;
                }

                if (myTurn) {
                    ui.enableEnemyBoard(true);
                    ui.setStatus("🎯 Tu turno: dispara al tablero enemigo.");
                    Thread.sleep(200); // pequeña pausa para evitar bucles ocupados
                } else {
                    ui.enableEnemyBoard(false);
                    ui.setStatus("⌛ Esperando disparo del oponente...");

                    String shot = net.receive(); // formato "r,c"
                    if (shot == null)
                        break; // desconexión

                    String result = ui.applyEnemyShot(shot);
                    net.send(result);

                    // 🔹 Si el oponente falló, ahora te toca
                    if (result.equals("AGUA")) {
                        myTurn = true;
                    }
                    // Si acierta, sigue él (no cambiamos myTurn)
                }

            } catch (IOException e) {
                running = false;
                ui.showError("Error de red: " + e.getMessage());
            } catch (InterruptedException ignored) {
            }
        }
    }

    public void stopHandler() {
        running = false;
    }

    // 🔹 Cuando tú haces clic en el tablero enemigo
    public void playerShot(int r, int c) {
        if (!myTurn || engine.isGameOver())
            return;

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
