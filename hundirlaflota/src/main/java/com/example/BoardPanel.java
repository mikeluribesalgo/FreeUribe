package com.example;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.GridLayout;
import java.awt.Color;
import java.awt.Dimension;

public class BoardPanel extends JPanel {

    
    private CellButton[][] buttons;
    private transient  Board board;
    private boolean hideShips;
    private transient  CellClickListener listener;

    public BoardPanel (Board board, boolean hideShips)  {
        
        this.board = board;
        this.hideShips = hideShips;
        setLayout(new GridLayout(board.size, board.size));
        buttons = new CellButton[board.size][board.size];
        buildGrid();
    }

    public void setBoard(Board b) {
        this.board = b;
        removeAll();
        setLayout(new GridLayout(board.size, board.size));
        buttons = new CellButton[board.size][board.size];
        buildGrid();
        revalidate();
        repaint();
    }

    private void buildGrid() {
        for (int r = 0; r < board.size; r++) {
            for (int c = 0; c < board.size; c++) {
                CellButton btn = new CellButton(r, c);
                btn.setPreferredSize(new Dimension(36, 36));
                btn.setOpaque(true);
                btn.setBorder(BorderFactory.createLineBorder(Color.GRAY));
                final int rr = r;
                final int cc = c;
                btn.addActionListener(e -> {
                    if (listener != null)
                        listener.cellClicked(rr, cc);
                });
                buttons[r][c] = btn;
                add(btn);
            }
        }
        repaintGrid();
    }

    public void setCellClickListener(CellClickListener l) {
        this.listener = l;
    }

    public void repaintGrid() {
        for (int r = 0; r < board.size; r++) {
            for (int c = 0; c < board.size; c++) {
                Cell cell = board.cells[r][c];
                CellButton btn = buttons[r][c];
                switch (cell.state) {
                    case EMPTY:
                        btn.setText("");
                        btn.setBackground(new Color(173, 216, 230)); // agua
                        break;
                    case SHIP:
                        btn.setText("");
                        if (hideShips) {
                            btn.setBackground(new Color(173, 216, 230));
                        } else {
                            btn.setBackground(new Color(100, 149, 237)); // barco visible
                        }
                        break;
                    case HIT:
                        btn.setText("X");
                        btn.setBackground(new Color(220, 20, 60)); // rojo
                        break;
                    case MISS:
                        btn.setText("•");
                        btn.setBackground(new Color(240, 248, 255)); // claro
                        break;
                }
            }
        }
        revalidate();
        repaint();
    }

    interface CellClickListener {
        void cellClicked(int row, int col);
    }
}
