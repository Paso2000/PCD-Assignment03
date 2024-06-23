package pcd.part2B.GUI;

import pcd.part2B.SudokuServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.util.Set;

public class StartGUI extends JFrame {
    public StartGUI(SudokuServer server) {
        setTitle("Cooperative Sudoku");
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(null); // Usa layout null per posizionare manualmente i componenti

        JButton newGameButton = new JButton("New Game");
        JButton joinGameButton = new JButton("Enter in a game");
        JLabel labelJoinID = new JLabel("Game ID:");
        JComboBox<String> fieldJoinID = new JComboBox<>();
        Set<String> grids = null;
        try {
            grids = server.getGrids();
            if (!grids.isEmpty()) {
                grids.forEach(fieldJoinID::addItem);
            }
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        // Imposta le dimensioni e le posizioni dei componenti
        newGameButton.setBounds(10, 60, 150, 25);
        joinGameButton.setBounds(200, 60, 150, 25);
        labelJoinID.setBounds(10, 20, 80, 25);
        fieldJoinID.setBounds(100, 20, 165, 25);

        // Aggiungi ActionListener ai pulsanti
        newGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    server.createGrid();
                    System.out.println("Client launched, new grid created.");
                    dispose();
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });
        joinGameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if (server.getGrids().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Non ci sono partite disponibili al momento.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    String idGrid = (String) fieldJoinID.getSelectedItem();
                    server.joinGrid(idGrid);
                    System.out.println("Client launched, joined grid " + idGrid + ".");
                    dispose();
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });

        panel.add(newGameButton);
        panel.add(joinGameButton);
        panel.add(labelJoinID);
        panel.add(fieldJoinID);
        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }
}
