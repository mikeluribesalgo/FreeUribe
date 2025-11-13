package org.example.game;

import org.junit.jupiter.api.*;

import com.example.game.NetworkManager;

import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.util.concurrent.*;

 class NetworkManagerTest {

    private static final int TEST_PORT = 5555;
    private static final String LOCALHOST = "127.0.0.1";

    @Test
    void testClientServerCommunication() throws Exception {
        // Usamos un executor para lanzar el servidor en segundo plano
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Iniciar el servidor asíncronamente
        Future<NetworkManager> serverFuture = executor.submit(() -> {
            try {
                return new NetworkManager(true, LOCALHOST, TEST_PORT);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Pequeño retardo para que el servidor empiece a escuchar
        

        // Crear cliente (esto disparará la conexión)
        NetworkManager client = new NetworkManager(false, LOCALHOST, TEST_PORT);
        NetworkManager server = serverFuture.get(3, TimeUnit.SECONDS);

        // Verificar flags
        assertTrue(server.isServer());
        assertFalse(client.isServer());

        // Enviar mensaje desde cliente -> servidor
        client.send("Hola Servidor");
        String received = server.receive();
        assertEquals("Hola Servidor", received);

        // Enviar mensaje desde servidor -> cliente
        server.send("Hola Cliente");
        String received2 = client.receive();
        assertEquals("Hola Cliente", received2);

        // Cerrar conexiones
        client.close();
        server.close();
        executor.shutdownNow();
    }

    @Test
    void testCloseDoesNotThrowException() {
        // Un socket sin conexión no debe lanzar excepción al cerrar
        NetworkManager nm = null;
        try {
            nm = new NetworkManager(false, "localhost", 0);
        } catch (IOException e) {
            // esperado: puerto inválido
        }

        if (nm != null) {
            assertDoesNotThrow(nm::close);
        }
    }
    

    @Test
    void testIsServerFlag() throws IOException {
        NetworkManager nmClient = null;
        try {
            nmClient = new NetworkManager(false, LOCALHOST, TEST_PORT);
        } catch (IOException ignored) {
            // Puede fallar si el puerto no está abierto — solo probamos el flag
        }
        if (nmClient != null) {
            assertFalse(nmClient.isServer());
            nmClient.close();
        }
    }
}
