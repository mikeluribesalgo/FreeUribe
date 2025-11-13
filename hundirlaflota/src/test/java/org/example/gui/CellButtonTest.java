package org.example.gui;

import org.junit.jupiter.api.Test;

import com.example.gui.CellButton;

import static org.junit.jupiter.api.Assertions.*;

class CellButtonTest {

    @Test
    void testButtonStoresCoordinates() {
        CellButton btn = new CellButton(2, 3);
        assertEquals(2, btn.row);
        assertEquals(3, btn.col);
    }
}
