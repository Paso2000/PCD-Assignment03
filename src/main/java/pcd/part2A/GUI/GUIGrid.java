package pcd.part2A.GUI;

import akka.actor.typed.ActorRef;
import pcd.part2A.messages.PlayerActorContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class GUIGrid extends JFrame {
    private static final int GRID_SIZE = 9;
    private JTextField[][] cells;
    private ActorRef<PlayerActorContext> player;

    public GUIGrid(ActorRef<PlayerActorContext> player) {
        this.player = player;
        cells = new JTextField[GRID_SIZE][GRID_SIZE];
        initUI();
    }
    public void cleanGUI(){
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                cells[row][col].setBackground(Color.WHITE);
            }
        }
    }
    private void initUI() {
        setTitle("Cooperative Sudoku");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        Font font = new Font("Arial", Font.BOLD, 20);

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                JTextField textField = new JTextField();
                cells[row][col] = textField;
                cells[row][col].setHorizontalAlignment(JTextField.CENTER);
                cells[row][col].setFont(font);
                int finalRow = row;
                int finalCol = col;
                textField.addFocusListener(new FocusAdapter() {
                    public void focusLost(FocusEvent e) {
                        System.out.println("Evento di de-click su cella rilevato");
                        String text = textField.getText();
                        if (text.isEmpty()) {
                            System.out.println("Numero eliminato");
                            //grid.deleteValue(row, column);
                        } else {
                            System.out.println("Numero inserito/modificato");
                            player.tell(new PlayerActorContext.ChangeCell(finalRow, finalCol, Integer.parseInt(textField.getText())));
                        }
                    }
                });
                cells[row][col].addFocusListener(new CellFocusListener(row, col));
                panel.add(cells[row][col]);
            }
        }
        JButton exitButton = new JButton("Exit Game");
        JButton crashButton = new JButton("Test Crush");

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exitGame();
            }
        });

        JButton solveButton = new JButton("Solve");


        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                solveSudoku();
            }
        });

        crashButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                player.tell(new PlayerActorContext.SimulateCrash());
            }
        });

        JPanel controlPanel = new JPanel();
        controlPanel.add(solveButton);
        controlPanel.add(exitButton);
        controlPanel.add(crashButton);

        add(panel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void exitGame() {
        player.tell(new PlayerActorContext.LeaveGame(player));
        dispose();
    }

    private void solveSudoku() {
        player.tell(new PlayerActorContext.SolveSudoku());
        //System.out.println("solve sudoku");
    }

    public void render(int[][] grid) {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int value = grid[row][col];
                if (value != 0) {
                    cells[row][col].setText(String.valueOf(value));
                }
            }
        }
    }
    public void selectCell(int row, int col) {
        if (cells[row][col].getBackground() != Color.CYAN) {
            cells[row][col].setBackground(Color.CYAN);
        }
    }

   //per focus
    private class CellFocusListener implements FocusListener {
        private int row, col;

        public CellFocusListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void focusGained(FocusEvent e) {
            cells[row][col].setBackground(Color.CYAN);
            player.tell(new PlayerActorContext.SelectCell(row, col));
        }
        
        @Override
        public void focusLost(FocusEvent e) {
            cells[row][col].setBackground(Color.WHITE);
        }
    }

}
