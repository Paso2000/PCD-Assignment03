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

        JButton solveButton = new JButton("Solve");
        solveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                solveSudoku();
            }
        });

        JPanel controlPanel = new JPanel();
        controlPanel.add(solveButton);

        add(panel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void solveSudoku() {
        // TODO
        //test messages
        player.tell(new PlayerActorContext.SolveSudoku());
        System.out.println("solve sudoku");
    }

    public void render(int row, int col, int value) {
       cells[row][col].setText(String.valueOf(value));
    }
    public void selectCell(int row, int col) {
        if (cells[row][col].getBackground() != Color.CYAN) {
            cells[row][col].setBackground(Color.CYAN);
        }
    }

    /*private class CellDocumentListener implements DocumentListener {
        private int row, col;

        public CellDocumentListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            int value = Integer.parseInt(cells[row][col].getText());
            player.tell(new PlayerActorContext.ChangeCell(row, col, value));
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            //Chiamato quando viene rimosso del testo.
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            //Chiamato quando viene cambiato un attributo del documento
        }
    }*/

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
