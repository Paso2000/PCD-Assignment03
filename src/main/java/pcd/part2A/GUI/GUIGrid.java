package pcd.part2A.GUI;

import akka.actor.typed.ActorRef;
import pcd.part2A.messages.PlayerActorContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUIGrid extends JFrame {
    private static final int GRID_SIZE = 9;
    private JTextField[][] cells;
    private ActorRef<PlayerActorContext> player;

    public GUIGrid(ActorRef<PlayerActorContext> player) {
        this.player = player;
        cells = new JTextField[GRID_SIZE][GRID_SIZE];
        initUI();
    }

    private void initUI() {
        setTitle("Cooperative Sudoku");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridLayout(GRID_SIZE, GRID_SIZE));
        Font font = new Font("Arial", Font.BOLD, 20);

        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                cells[row][col] = new JTextField();
                cells[row][col].setHorizontalAlignment(JTextField.CENTER);
                cells[row][col].setFont(font);
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

        JButton selectButton = new JButton("Select");
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectCell();
            }
        });

        JButton changeButton = new JButton("Change");
        changeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeCell();
            }
        });

        JPanel controlPanel = new JPanel();
        controlPanel.add(solveButton);
        controlPanel.add(selectButton);
        controlPanel.add(changeButton);

        add(panel, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);
    }

    private void solveSudoku() {
        // TODO
        //test messages
        player.tell(new PlayerActorContext.SolveSudoku());
        System.out.println("solve sudoku");
    }

    private void selectCell(){
        //TODO
        player.tell(new PlayerActorContext.SelectCell());
        System.out.println("select cell");
    }
    private void changeCell(){
        //TODO
        player.tell(new PlayerActorContext.ChangeCell());
        System.out.println("change value cell");
    }

}
