package pcd.part2A.GUI;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static pcd.part2A.App.startup;

public class View extends JFrame {

    private JButton button1;
    int i =0;
    private JButton button2;

    public View() {
        setTitle("Swing Example");

        setSize(300, 200);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();

        button1 = new JButton("New Game");
        button2 = new JButton("Enter in a game");

        // Aggiungi ActionListener ai pulsanti
        button1.addActionListener(new ButtonClickListener());
        button2.addActionListener(new ButtonClickListener());

        panel.add(button1);
        panel.add(button2);

        add(panel, BorderLayout.CENTER);
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (command.equals("New Game")) {
                i++;
                SudokuGUI ex = new SudokuGUI();
                ex.setVisible(true);

                startup("backend", (25251+i));

            } else if (command.equals("Enter in a game")) {
                System.out.println("Enter in a game");
            }
        }
    }
}