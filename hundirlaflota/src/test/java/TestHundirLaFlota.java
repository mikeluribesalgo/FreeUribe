import javax.swing.SwingUtilities;

import org.junit.Test;
import com.example.GameFrame;
import static org.junit.jupiter.api.Assertions.*;


public class TestHundirLaFlota {

    @Test
    public void testGameFrameLaunch() {
        SwingUtilities.invokeLater(() -> {
            try {
                GameFrame frame = new GameFrame();
                frame.setVisible(true);

                // Comprobamos que el frame se haya creado correctamente
                assertNotNull(frame);
                assertTrue(frame.isDisplayable());

                // Cerramos después de probar
                frame.dispose();
            } catch (Exception e) {
                fail("Error al iniciar el juego: " + e.getMessage());
            }
        });
    }
}
