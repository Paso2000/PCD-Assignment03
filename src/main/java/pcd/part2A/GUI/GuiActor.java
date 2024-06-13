package pcd.part2A.GUI;

import akka.actor.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import pcd.part2A.PlayerActor;
import pcd.part2A.messages.GuiActorContext;
import pcd.part2A.messages.PlayerActorContext;
import pcd.part2A.sudoku.SudokuGrid;
import pcd.part2A.sudoku.SudokuSolver;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GuiActor extends JFrame {
    private static final int GRID_SIZE = SudokuGrid.SIZE;
    private JTextField[][] cells;
    private SudokuGrid grid;
    private SudokuSolver solver;

    ActorContext<PlayerActorContext> player;

    public GuiActor(ActorContext player) {
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
                cells[row][col].getDocument().addDocumentListener(new DocumentListener() {
                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        //madno mess
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {

                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        //mando il messaggio agli altri
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

    private Behavior<GuiActorContext.UpdateCell> behavior() {
        return Behaviors.receive(GuiActorContext.UpdateCell.class).onMessage(GuiActorContext.UpdateCell.class, this::onChangeGrid).build();
    }

    private Behavior<GuiActorContext.UpdateCell> onChangeGrid(GuiActorContext.UpdateCell updateCell) {
       player.getSelf().tell(new PlayerActorContext.CellUpdated(updateCell.coordinate, updateCell.value));
       return Behaviors.same();
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
