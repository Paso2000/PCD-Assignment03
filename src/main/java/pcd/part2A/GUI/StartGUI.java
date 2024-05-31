package pcd.part2A.GUI;
import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static pcd.part2A.App.startup;

public class StartGUI extends JFrame {
    private JButton newGameButton;
    private JButton joinGameButton;
    private JLabel labelJoinID;
    private JTextField fieldJoinID;

    public StartGUI() {
        setTitle("Cooperative Sudoku");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();

        newGameButton = new JButton("New Game");
        joinGameButton = new JButton("Enter in a game");
        labelJoinID = new JLabel("ID:");
        fieldJoinID = new JTextField(20);

        labelJoinID.setBounds(10, 20, 80, 25);
        fieldJoinID.setBounds(100, 20, 165, 25);

        // Aggiungi ActionListener ai pulsanti
        newGameButton.addActionListener(new ButtonClickListener());
        joinGameButton.addActionListener(new ButtonClickListener());

        panel.add(newGameButton);
        panel.add(joinGameButton);
        panel.add(labelJoinID);
        panel.add(fieldJoinID);
        add(panel, BorderLayout.CENTER);
    }

    private static class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals("New Game")) {
                SudokuGUI ex = new SudokuGUI();
                ex.setVisible(true);
                startup("backend", (25251));
            } else if (command.equals("Enter in a game")) {
                System.out.println("Enter in a game");
            }
        }
    }
}