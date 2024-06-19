package pcd.part2A.GUI;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static pcd.part2A.Utils.startup;

public class App extends JFrame {
    private JButton newGameButton;
    private JButton joinGameButton;
    private JLabel labelJoinID;
    private JTextField fieldJoinID;

    public App() {
        setTitle("Cooperative Sudoku");
        setSize(400, 150);
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
    public static void main(String[] args) {
        startup("backend", 0);
        App frame = new App();
        frame.setVisible(true);
    }

    private static class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals("New Game")) {
                startup("playerNewGame", 0);
                //mando un messaggio a GamesActor per dirgli start partita e il leader
                //GamesActorRef.tell(new GamesActorContext.newGame(leader))

            } else if (command.equals("Enter in a game")) {
                System.out.println("Enter in a game");
            }
        }
    }
}