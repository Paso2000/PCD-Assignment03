package pcd.part2A.GUI;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Optional;
import java.util.stream.IntStream;

import static pcd.part2A.Utils.startup;

public class App extends JFrame {
    private JButton newGameButton;
    private JButton joinGameButton;
    private JLabel labelJoinID;
    private static JComboBox fieldJoinID;
    private static int nGame  = 0;

    public App() {
        setTitle("Cooperative Sudoku");
        setSize(400, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(null); // Usa layout null per posizionare manualmente i componenti

        newGameButton = new JButton("New Game");
        joinGameButton = new JButton("Enter in a game");
        labelJoinID = new JLabel("Game ID:");
        fieldJoinID = new JComboBox<Integer>();
        IntStream.rangeClosed(1, 5).forEach(fieldJoinID::addItem);

        // Imposta le dimensioni e le posizioni dei componenti
        newGameButton.setBounds(10, 60, 150, 25);
        joinGameButton.setBounds(200, 60, 150, 25);
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
    public static void main(String[] args) {
        startup("backend", 0, Optional.empty());
        App frame = new App();
        frame.setVisible(true);
    }

    private static class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals("New Game")) {
                nGame++;
                startup("playerNewGame", 0, Optional.of(nGame));

            } else if (command.equals("Enter in a game")) {
                //passo il valore della comboBox con la partita a cui voglio partecipare
                startup("playerJoinGame", 0, Optional.ofNullable((Integer) fieldJoinID.getSelectedItem()));
            }
        }
    }
}