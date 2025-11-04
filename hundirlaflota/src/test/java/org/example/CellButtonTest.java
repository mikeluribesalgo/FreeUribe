package org.example;

import org.junit.jupiter.api.Test;

import com.example.CellButton;

import static org.junit.jupiter.api.Assertions.*;
import javax.swing.JButton;

class CellButtonTest {

    @Test
    void testConstructorInitializesRowAndCol() {
        CellButton button = new CellButton(2, 3);
        assertEquals(2, button.row, "La fila debe ser 2");
        assertEquals(3, button.col, "La columna debe ser 3");
    }

    @Test
    void testCellButtonIsJButton() {
        CellButton button = new CellButton(0, 0);
        assertTrue(button instanceof JButton, "CellButton debe ser una subclase de JButton");
    }

    @Test
    void testButtonHasNoTextInitially() {
        CellButton button = new CellButton(1, 1);
        assertEquals("", button.getText(), "El texto del boton debe estar vacio inicialmente");
    }

    @Test
    void testSetAndGetText() {
        CellButton button = new CellButton(1, 1);
        button.setText("X");
        assertEquals("X", button.getText(), "El texto debe actualizarse correctamente");
    }
}
