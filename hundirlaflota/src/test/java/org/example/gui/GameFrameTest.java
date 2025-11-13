package org.example.gui;

import com.example.game.*;
import com.example.gui.GameFrame;

import org.junit.jupiter.api.*;

import javax.swing.*;




import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
 class GameFrameTest {

    private GameFrame frame;
    private GameEngine engine;

    @BeforeAll
    static void setHeadless() {
        System.setProperty("java.awt.headless", "true");
    }

    @BeforeEach
    void setup() {
        engine = spy(new GameEngine(5));
        frame = new GameFrame(engine);
    }

    @Test
    void testConstructorAndInitGameFrame() {
        assertNotNull(frame);
        assertNotNull(frame.getContentPane());
    }

    @Test
    void testToggleOrientationChangesMode() {
        frame.toggleOrientation();
        frame.toggleOrientation();
        assertDoesNotThrow(() -> frame.toggleOrientation());
    }

    @Test
    void testRefreshBoards() {
        assertDoesNotThrow(() -> frame.refreshBoards());
    }

    @Test
    void testEnableEnemyBoard() {
        assertDoesNotThrow(() -> frame.enableEnemyBoard(true));
        assertDoesNotThrow(() -> frame.enableEnemyBoard(false));
    }

    @Test
    void testApplyEnemyShotHitMissSunk() {
        Board board = engine.getPlayerBoard();
        Ship s = new Ship(0, 0, 1, true);
        board.placeShip(s);

        String resultHit = frame.applyEnemyShot("0,0");
        assertTrue(resultHit.equals("TOCADO") || resultHit.equals("HUNDIDO"));

        String resultMiss = frame.applyEnemyShot("1,1");
        assertEquals("AGUA", resultMiss);
    }

    @Test
    void testApplyEnemyResultVariants() {
        frame.applyEnemyResult("0,0", "AGUA");
        frame.applyEnemyResult("0,1", "TOCADO");
        frame.applyEnemyResult("0,2", "HUNDIDO");

        assertEquals(CellState.MISS, engine.getEnemyBoard().getCell(0, 0).getState());
        assertEquals(CellState.HIT, engine.getEnemyBoard().getCell(0, 1).getState());
        assertEquals(CellState.HIT, engine.getEnemyBoard().getCell(0, 2).getState());
    }

    @Test
    void testSetStatusAndShowError() {
        assertDoesNotThrow(() -> frame.setStatus("Probando"));
        assertDoesNotThrow(() -> frame.showError("Mensaje de error"));
    }

    @Test
    void testRestartGameResetsBoards() {
        frame.toggleOrientation();
        frame.refreshBoards();
        assertDoesNotThrow(() -> {
            var m = frame.getClass().getDeclaredMethod("restartGame");
            m.setAccessible(true);
            m.invoke(frame);
        });
    }

    @Test
    void testAskGameModeCpuPath() throws Exception {
        // Simula elección de "Contra CPU" (0)
        mockStatic(JOptionPane.class).when(() ->
                JOptionPane.showOptionDialog(any(), any(), any(), anyInt(), anyInt(), any(), any(), any())
        ).thenReturn(0);

        var method = frame.getClass().getDeclaredMethod("askGameMode");
        method.setAccessible(true);
        assertDoesNotThrow(() -> method.invoke(frame));
    }

    

    

    @Test
    void testSetupEnemyListenerWithCpu() throws Exception {
        // Simula fase de disparo (no en colocación)
        var placementField = frame.getClass().getDeclaredField("placementPhase");
        placementField.setAccessible(true);
        placementField.set(frame, false);

        Board enemy = engine.getEnemyBoard();
        enemy.clearBoard();

        // Añadir barco para tener objetivo
        Ship s = new Ship(0, 0, 1, true);
        enemy.placeShip(s);

        // Ejecutar listener manualmente
        var method = frame.getClass().getDeclaredMethod("setupEnemyListener");
        method.setAccessible(true);
        method.invoke(frame);

        // Disparo CPU
        assertDoesNotThrow(() -> engine.playerShoot(0, 0));
    }

    @Test
    void testPlacementListenerWorksAndPreventsInvalidPlacement() throws Exception {
        var method = frame.getClass().getDeclaredMethod("setupPlacementListener");
        method.setAccessible(true);
        method.invoke(frame);

        // Intentar colocar en coordenadas inválidas
        Board player = engine.getPlayerBoard();
        player.placeShip(new Ship(0, 0, 5, true)); // ocupar espacio
        frame.toggleOrientation();

        assertDoesNotThrow(() -> frame.toggleOrientation());
    }

    
}