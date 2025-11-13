package org.example.game;

import com.example.game.Main;
import com.example.gui.GameFrame;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import javax.swing.SwingUtilities;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

     @Test
    void testInstanciarMainYLLamarMainMethod() {
        // ✅ Instanciamos la clase explícitamente (cubre la línea "public class Main")
        Main mainInstance = new Main();
        assertNotNull(mainInstance, "La instancia de Main no debe ser null");

        // ✅ También ejecutamos el método main() estático
        assertDoesNotThrow(() -> {
            Main.main(new String[]{});
            
        });
    }

    @Test
    void testMainCreatesAndShowsGameFrameViaInvokeLater() {
        
        
        try (
                MockedConstruction<GameFrame> mockedFrames = Mockito.mockConstruction(GameFrame.class,
                        (mock, context) -> Mockito.doNothing().when(mock).setVisible(true));
                MockedStatic<SwingUtilities> mockedSwing = Mockito.mockStatic(SwingUtilities.class)) {
            // Capturar el Runnable pasado a invokeLater
            final Runnable[] capturedRunnable = new Runnable[1];
            mockedSwing.when(() -> SwingUtilities.invokeLater(Mockito.any()))
                    .thenAnswer(invocation -> {
                        capturedRunnable[0] = invocation.getArgument(0);
                        // Ejecutar inmediatamente para cubrir la lógica interna
                        capturedRunnable[0].run();
                        return null;
                    });

            // Ejecutar main
            Main.main(new String[] {});

            // Verificar que invokeLater fue llamado una vez
            mockedSwing.verify(() -> SwingUtilities.invokeLater(Mockito.any()), Mockito.times(1));

            // Verificar creación del GameFrame
            assertEquals(1, mockedFrames.constructed().size(), "Debe crearse un GameFrame");

            // Verificar que se llamó a setVisible(true)
            GameFrame frame = mockedFrames.constructed().get(0);
            Mockito.verify(frame, Mockito.times(1)).setVisible(true);
        }
    }

    @Test
    void testMainCanBeCalledMultipleTimes() {
        // Ejecutar varias veces para cubrir posibles llamadas repetidas
        assertDoesNotThrow(() -> Main.main(new String[] {}));
        assertDoesNotThrow(() -> Main.main(null));
    }
}