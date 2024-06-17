package pcd.part2A.GUI;
import pcd.part2A.Actors.GridActor;
import pcd.part2A.sudoku.SudokuSolver;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUIGrid extends JFrame {
    private static final int GRID_SIZE = GridActor.SIZE;
    private JTextField[][] cells;
    private GridActor grid;
    private SudokuSolver solver;


    public GUIGrid() {
        grid = new GridActor();
        solver = new SudokuSolver();
        cells = new JTextField[GRID_SIZE][GRID_SIZE];
        initUI();
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
        grid = new GridActor();
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                cells[row][col].setText("");
            }
        }
    }
}
