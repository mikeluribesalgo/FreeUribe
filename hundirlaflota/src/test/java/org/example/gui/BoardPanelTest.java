package org.example.gui;

import com.example.game.*;
import com.example.gui.BoardPanel;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class BoardPanelTest {

    private Board board;
    private BoardPanel panel;

    @BeforeEach
    void setup() {
        board = new Board(5);
        panel = new BoardPanel(board, false);
    }

    @Test
    void testPanelBuildsGridCorrectly() {
        assertEquals(25, panel.getComponentCount());
    }

    @Test
    void testRepaintGridUpdatesColors() {
        board.getCell(0, 0).setState(CellState.SHIP);
        panel.repaintGrid();
        Component comp = panel.getComponent(0);
        assertTrue(comp instanceof JButton);
    }

    @Test
    void testCellClickListenerInvoked() {
        final boolean[] clicked = {false};
        panel.setCellClickListener((r, c) -> clicked[0] = true);
        JButton btn = (JButton) panel.getComponent(0);
        btn.doClick();
        assertTrue(clicked[0]);
    }

    @Test
    void testSetBoardRebuildsGrid() {
        Board newBoard = new Board(3);
        panel.setBoard(newBoard);
        assertEquals(9, panel.getComponentCount());
    }
}
