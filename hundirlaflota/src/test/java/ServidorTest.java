import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.example.Servidor;

public class ServidorTest {

    @Test
    void testCrearTableroInicialVacio() {
        char[][] tablero = Servidor.crearTablero();
        assertEquals(3, tablero.length, "El tablero debe tener 3 filas");
        for (char[] fila : tablero) {
            for (char c : fila) {
                assertEquals('-', c, "Todas las casillas deben estar vacías inicialmente");
            }
        }
    }

    @Test
    void testProcesarDisparoAgua() {
        char[][] tablero = Servidor.crearTablero();
        // No hay barcos colocados
        String resultado = Servidor.procesarDisparo(tablero, "A1");
        assertEquals("AGUA", resultado, "Debe devolver AGUA cuando no hay barco");
    }

    @Test
    void testProcesarDisparoHundido() {
        char[][] tablero = Servidor.crearTablero();
        // Colocar un barco manualmente
        tablero[0][0] = 'B'; // A1
        String resultado = Servidor.procesarDisparo(tablero, "A1");
        assertEquals("HUNDIDO", resultado, "Debe devolver HUNDIDO al acertar un barco");
        assertEquals('X', tablero[0][0], "La casilla del barco debe marcarse con X tras hundirse");
    }
}
