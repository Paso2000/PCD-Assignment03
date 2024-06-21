package pcd.part2B.GUI;

import pcd.part2B.SudokuServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.util.Set;

public class StartGUI extends JFrame {
    public StartGUI(SudokuServer server) {
        this.setTitle("Sudoku Game");
        this.setLayout(new BorderLayout());

        JButton createGridButton = new JButton("Crea Griglia");
        createGridButton.addActionListener(new ActionListener() {
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

        JButton joinGridbutton = new JButton("Unisciti a Griglia");
        joinGridbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Set<String> grids = server.getGrids();
                    if (grids.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Non ci sono partite disponibili al momento.", "Attenzione", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    JList<String> list = new JList<>(grids.toArray(new String[0]));
                    JOptionPane.showMessageDialog(null, list, "Seleziona una partita", JOptionPane.PLAIN_MESSAGE);
                    String idGrid = list.getSelectedValue();
                    server.joinGrid(idGrid);
                    System.out.println("Client launched, joined grid " + idGrid + ".");
                    dispose();
                } catch (RemoteException ex) {
                    ex.printStackTrace();
                }
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.add(createGridButton);
        buttonPanel.add(joinGridbutton);

        this.add(buttonPanel, BorderLayout.CENTER);
        this.pack();

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(600, 600);
        this.setResizable(true);
        this.setVisible(true);
    }
}
