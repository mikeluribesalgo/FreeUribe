package org.example.game;

import com.example.game.GameEngine;
import com.example.game.NetworkGameHandler;
import com.example.game.NetworkManager;
import com.example.gui.GameFrame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.io.IOException;

import static org.mockito.Mockito.*;

class NetworkGameHandlerTest {

    private GameEngine engine;
    private GameFrame ui;
    private NetworkManager net;
    private NetworkGameHandler handler;

    @BeforeEach
    void setUp() {
        engine = mock(GameEngine.class);
        ui = mock(GameFrame.class);
        net = mock(NetworkManager.class);

        // Simular que el engine devuelve nuestro mock NetworkManager
        when(engine.getNetworkManager()).thenReturn(net);
        when(engine.isGameOver()).thenReturn(false);

        handler = new NetworkGameHandler(engine, ui, true);
    }

    // 🧪 Caso 1: Disparo con resultado "AGUA"
    @Test
    void testPlayerShotMissesAndEndsTurn() throws IOException {
        when(net.receive()).thenReturn("AGUA");

        handler.playerShot(2, 3);

        // Verificar flujo correcto
        InOrder order = inOrder(net, ui);
        order.verify(net).send("2,3");
        order.verify(net).receive();
        order.verify(ui).applyEnemyResult("2,3", "AGUA");
        order.verify(ui).enableEnemyBoard(false);
        order.verify(ui).setStatus("💤 Turno del oponente...");
    }

    // 🧪 Caso 2: Disparo con resultado "TOCADO"
    @Test
    void testPlayerShotHitsAndKeepsTurn() throws IOException {
        when(net.receive()).thenReturn("TOCADO");

        handler.playerShot(1, 1);

        InOrder order = inOrder(net, ui);
        order.verify(net).send("1,1");
        order.verify(net).receive();
        order.verify(ui).applyEnemyResult("1,1", "TOCADO");
        order.verify(ui).setStatus("🔥 ¡Tocado! Dispara de nuevo.");
    }

    // 🧪 Caso 3: No debe disparar si no es mi turno
    @Test
    void testPlayerShotIgnoredWhenNotMyTurn() {
        // Crear handler con turno falso
        handler = new NetworkGameHandler(engine, ui, false);

        handler.playerShot(0, 0);

        // No debe enviar ni recibir nada
        verifyNoInteractions(net);
        verifyNoInteractions(ui);
    }

    // 🧪 Caso 4: Manejo de IOException en playerShot
    @Test
    void testPlayerShotIOExceptionShowsError() throws IOException {
        when(net.receive()).thenThrow(new IOException("Simulado"));

        handler.playerShot(0, 0);

        verify(ui).showError(contains("Error de comunicación"));
    }

    // 🧪 Caso 5: run() - simula recibir disparo de oponente
    @Test
    void testRunReceivesOpponentShot() throws Exception {
        handler = new NetworkGameHandler(engine, ui, false);

        when(net.receive()).thenReturn("1,1", (String) null); // segundo null = desconexión
        when(ui.applyEnemyShot("1,1")).thenReturn("AGUA");

        Thread t = new Thread(handler::run);
        t.start();

        
        t.interrupt(); // detener el hilo si sigue

        verify(ui, atLeastOnce()).setStatus(contains("Esperando"));
        verify(ui).applyEnemyShot("1,1");
        verify(net).send("AGUA");
       
    }
}
