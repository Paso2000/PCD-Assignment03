package pcd.part2A.GUI;

import akka.actor.typed.ActorRef;
import pcd.part2A.messages.GridActorContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUIGrid extends JFrame {
    private static final int GRID_SIZE = 9;
    private JTextField[][] cells;
    private ActorRef<GridActorContext> gridActorRef;

    public GUIGrid(ActorRef<GridActorContext> gridActorRef) {
        this.gridActorRef = gridActorRef;
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
        // TODO
        //test messages
        gridActorRef.tell(new GridActorContext.genericMessage());
        System.out.println("solve sudoku");
    }
    private void clearGrid(){
        // TODO
        System.out.println("clear grid");
    }

}
