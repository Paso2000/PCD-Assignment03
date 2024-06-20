package pcd.part2B.GUI;

import akka.japi.Pair;
import pcd.part2B.Grid;
import pcd.part2B.SudokuPlayer;
import pcd.part2B.SudokuServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class SudokuGUI extends JFrame {

    private static final int GRID_SIZE = 9;
    private JTextField[][] cells = new JTextField[GRID_SIZE][GRID_SIZE];
    private Pair<Integer, Integer> selectedCell;
    private int i = 0, j = 0;

    SudokuServer server = null;

    // se non va bene il server me lo prendo da player che a sua volta lo prende da server stesso
    // attenzione però
    public SudokuGUI(Grid grid, SudokuServer server) {
        /*try {
            // Ottiene il registro RMI
            Registry registry = LocateRegistry.getRegistry();

            // Ottiene il server
            server = (SudokuServer) registry.lookup(SudokuServer.SERVER_NAME);
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }

         */
        this.setTitle("Playing on grid with id " + grid.getId());
        this.setLayout(new GridLayout(9, 9));
        for (i = 0; i < GRID_SIZE; i++) {
            for (j = 0; j < GRID_SIZE; j++) {
                final int row = i;
                final int column = j;
                JTextField cell = new JTextField();
                if (grid.getValue(i, j) >= 1 && grid.getValue(i, j) <= 9) {
                    cell.setText(String.valueOf(grid.getValue(i, j)));
                }

                // Rileva l'evento di click
                cell.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        System.out.println("Evento di click su cella rilevato");
                        grid.selectCell(row, column);
                        updateGrid(grid);
                        try {
                            server.updatePlayers(grid);
                        } catch (RemoteException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                });

                // Rileva l'evento di de-click (perdita di focus)
                // Salva a posteriori le operazioni compiute sulle celle
                cell.addFocusListener(new FocusAdapter() {
                    public void focusLost(FocusEvent e) {
                        System.out.println("Evento di de-click su cella rilevato");
                        String text = cell.getText();
                        if (text.isEmpty()) {
                            System.out.println("Numero eliminato");
                            grid.deleteValue(row, column);
                        } else {
                            System.out.println("Numero inserito/modificato");
                            grid.changeValue(row, column, Integer.parseInt(text));
                        }
                        grid.deselectCell(row, column);
                        updateGrid(grid);
                        try {
                            server.updatePlayers(grid);
                        } catch (RemoteException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                });

                this.add(cell);
                cells[row][column] = cell;
            }
        }
        this.pack();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(300, 200);
        this.setResizable(true);
        this.setVisible(true);
    }

    public void updateGrid(Grid grid) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                for (int i = 0; i < 9; i++) {
                    for (int j = 0; j < 9; j++) {
                        if (grid.getValue(i, j) > 0) {
                            cells[i][j].setText(Integer.toString(grid.getValue(i, j)));
                        }
                        if (grid.isSelected(i, j)) {
                            cells[i][j].setBackground(Color.YELLOW);
                        } else {
                            cells[i][j].setBackground(Color.WHITE);
                        }
                    }
                }
            }
        });
    }
}

