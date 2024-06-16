package pcd.part2A.GUI;

import akka.actor.typed.javadsl.ActorContext;
import akka.japi.Pair;
import pcd.part2A.messages.PlayerActorContext;
import pcd.part2A.sudoku.SudokuGrid;
import pcd.part2A.sudoku.SudokuSolver;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Gui extends JFrame {
    private static final int GRID_SIZE = SudokuGrid.SIZE;
    private final ActorContext player;
    private JTextField[][] cells;
    private SudokuGrid grid;
    private SudokuSolver solver;


    public Gui(ActorContext player) {
        grid = new SudokuGrid();
        solver = new SudokuSolver();
        cells = new JTextField[GRID_SIZE][GRID_SIZE];
        initUI();
        this.player=player;
    }

    private void initUI() {
        setTitle("Cooperative Sudoku");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        Font font = new Font("Arial", Font.BOLD, 20);

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                cells[row][col] = new JTextField();
                cells[row][col].setHorizontalAlignment(JTextField.CENTER);
                cells[row][col].setFont(font);
                panel.add(cells[row][col]);
                int finalRow = row;
                int finalCol = col;
                cells[row][col].getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        for (int r = 0; r < finalRow; r++) {
                            for (int c = 0; c < finalCol; c++) {
                                if (cells[r][c].getDocument() == e.getDocument()) {
                                    System.out.println("ciaooooooooooooo");
                                    player.getSelf().tell(new PlayerActorContext.CellUpdated(new Pair<>(r,c),Integer.parseInt(cells[r][c].getText())));
                                    return;
                                }
                            }
                        }
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {

                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        for (int r = 0; r < finalRow; r++) {
                            for (int c = 0; c < finalCol; c++) {
                                if (cells[r][c].getDocument() == e.getDocument()) {
                                    System.out.println("ciaooooooooooooo");
                                    player.getSelf().tell(new PlayerActorContext.CellUpdated(new Pair<>(r,c),Integer.parseInt(cells[r][c].getText())));
                                    return;
                                }
                            }
                        }
                    }
                });
            }
        }
        JButton solveButton = new JButton("Solve");
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                solveSudoku();
            }
        });

        JButton clearButton = new JButton("Clear");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearGrid();
            }
        });

        JPanel controlPanel = new JPanel();
        controlPanel.add(solveButton);
        controlPanel.add(clearButton);

        add(panel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void solveSudoku() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                String text = cells[row][col].getText();
                if (!text.isEmpty()) {
                    int value = Integer.parseInt(text);
                    grid.setCell(row, col, value, true);
                } else {
                    grid.setCell(row, col, 0, false);
                }
            }
        }

        if (solver.solve(grid)) {
            for (int row = 0; row < GRID_SIZE; row++) {
                for (int col = 0; col < GRID_SIZE; col++) {
                    cells[row][col].setText(String.valueOf(grid.getCell(row, col).getValue()));
                }
            }
            JOptionPane.showMessageDialog(this, "Sudoku solved successfully!");
        } else {
            JOptionPane.showMessageDialog(this, "Unable to solve the Sudoku.");
        }
    }

    private void clearGrid() {
        grid = new SudokuGrid();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                cells[row][col].setText("");
            }
        }
    }
}
