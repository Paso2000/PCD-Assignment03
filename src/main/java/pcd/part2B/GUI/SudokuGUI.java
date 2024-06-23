package pcd.part2B.GUI;

import pcd.part2B.Grid;
import pcd.part2B.SudokuGenerator;
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
    private static final int SUB_GRID_SIZE = 3;
    private final JTextField[][] cells;
    private JTextArea console;
    private JButton checkButton;
    String idPlayer;
    Grid startingGrid;
    Grid grid = null;

    public SudokuGUI(String idPlayer) {
        this.idPlayer = idPlayer;
        this.cells = new JTextField[GRID_SIZE][GRID_SIZE];
    }

    public void startGUI() {
        try {
            // Ottiene il registro RMI
            Registry registry = LocateRegistry.getRegistry();

            // Ottiene il server
            SudokuServer server = (SudokuServer) registry.lookup(SudokuServer.SERVER_NAME);

            // Ottiene il player
            SudokuPlayer player = (SudokuPlayer) registry.lookup(idPlayer);

            grid = player.getGrid();
            String idGrid = grid.getId();

            this.setTitle("Cooperative Sudoku - " + player.getId() + " on " + idGrid);

            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BorderLayout());

            // Sudoku Grid
            JPanel gridPanel = new JPanel();
            gridPanel.setLayout(new GridLayout(SUB_GRID_SIZE, SUB_GRID_SIZE));
            for (int block = 0; block < GRID_SIZE; block++) {
                JPanel subGrid = new JPanel();
                subGrid.setLayout(new GridLayout(SUB_GRID_SIZE, SUB_GRID_SIZE));
                subGrid.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
                subGrid.setPreferredSize(new Dimension(150, 150));
                for (int cell = 0; cell < GRID_SIZE; cell++) {
                    final int row = block / SUB_GRID_SIZE * SUB_GRID_SIZE + cell / SUB_GRID_SIZE;
                    final int column = block % SUB_GRID_SIZE * SUB_GRID_SIZE + cell % SUB_GRID_SIZE;
                    JTextField textField = new JTextField();
                    textField.setFont(new Font("Arial", Font.PLAIN, 20));
                    textField.setHorizontalAlignment(JTextField.CENTER);

                    if (grid.getValue(row, column) >= 1 && grid.getValue(row, column) <= 9) {
                        textField.setText(String.valueOf(grid.getValue(row, column)));
                        if (grid.isInitial(row, column)) {
                            textField.setEditable(false); // Disabilita la cella
                            textField.setBackground(Color.GRAY); // Cambia lo sfondo della cella
                            textField.setForeground(Color.WHITE);
                        }
                    }

                    // Rileva l'evento di click
                    textField.addMouseListener(new MouseAdapter() {
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
                    textField.addFocusListener(new FocusAdapter() {
                        public void focusLost(FocusEvent e) {
                            System.out.println("Evento di de-click su cella rilevato");
                            String text = textField.getText();
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

                    subGrid.add(textField);
                    cells[row][column] = textField;
                }
                gridPanel.add(subGrid);
            }

            // Console
            JPanel consolePanel = new JPanel();
            consolePanel.setLayout(new BorderLayout());
            console = new JTextArea();
            console.setEditable(false);
            consolePanel.add(new JScrollPane(console), BorderLayout.CENTER);

            // Buttons
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout());
            checkButton = new JButton("Check Sudoku");
            checkButton.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    try {
                        if (SudokuGenerator.isSudokuValid(grid.getGrid())) {
                            server.notifyGameOver(idGrid, player.getId());
                        } else {
                            server.notifyCheckSudokuNotCorrect(idGrid, player.getId());
                        }
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            JButton exitButton = new JButton("Exit Game");
            exitButton.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    System.out.println("Exit player from game");
                    try {
                        server.notifyPlayerExited(idGrid, player.getId());
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                    dispose();
                }
            });
            buttonPanel.add(checkButton);
            buttonPanel.add(exitButton);

            this.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.out.println("Exit player from game");
                    try {
                        server.notifyPlayerExited(idGrid, player.getId());
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            // Add to main panel
            mainPanel.add(gridPanel, BorderLayout.NORTH);
            mainPanel.add(consolePanel, BorderLayout.CENTER);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);

            this.add(mainPanel);
            this.pack();
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setSize(600, 600);
            this.setResizable(true);
            this.setVisible(true);
        } catch (RemoteException | NotBoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateGrid(Grid grid) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    if (grid.isInitial(i, j)) {
                        cells[i][j].setEditable(false); // Disabilita la cella
                        cells[i][j].setBackground(Color.GRAY); // Cambia lo sfondo della cella
                        cells[i][j].setForeground(Color.WHITE);
                    } else {
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
    public void appendToConsole(String message) {
        console.append(message + "\n");
    }

    public void notifyPlayerEntered(String idPlayer) {
        appendToConsole("New gamer " + idPlayer + " joins the game! :)");
    }

    public void notifyPlayerExited(String idPlayer) {
        appendToConsole("Gamer " + idPlayer + " exited from the game :(");
    }

    public void notifyNewAuthor(String idPlayer) {
        appendToConsole("Gamer author " + idPlayer + " exited, you're the new author!!! :D");
    }

    public void notifyGameOver(String idPlayer) {
        appendToConsole("Player " + idPlayer + " check sudoku, IT'S CORRECT!!! WELL DONE :)");
        checkButton.setEnabled(false);
    }

    public void notifyCheckSudokuNotCorrect(String idPlayer) {
        appendToConsole("Player " + idPlayer + " check sudoku, it's wrong :((( let's keep playing, don't give up!!! :)");
    }
}
