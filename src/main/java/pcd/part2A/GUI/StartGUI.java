package pcd.part2A.GUI;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import akka.cluster.typed.Cluster;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import pcd.part2A.GamesActor;
import pcd.part2A.PlayerActor;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
                startup("frontend", 0);
                startup("backend", (0));
            } else if (command.equals("Enter in a game")) {
                System.out.println("Enter in a game");
            }
        }
    }

    private static class RootBehavior {
        static Behavior<Void> create() {

            return Behaviors.setup(context -> {
                //inserisce il nodo nel cluster
                Cluster cluster = Cluster.get(context.getSystem());

                if (cluster.selfMember().hasRole("backend")) {
                    context.spawn(GamesActor.create(), "Backend");
                }
                if (cluster.selfMember().hasRole("frontend")) {
                    context.spawn(PlayerActor.create(), "Frontend");


                }
                return Behaviors.empty();
            });
        }
    }
    public static void startup(String role, int port) {

        //System.out.println(role  + ":" + port + " started");
        // Override the configuration of the port
        Map<String, Object> overrides = new HashMap<>();
        overrides.put("akka.remote.artery.canonical.port", port);
        overrides.put("akka.cluster.roles", Collections.singletonList(role));

        Config config = ConfigFactory.parseMap(overrides)
                .withFallback(ConfigFactory.load("transformation"));

        ActorSystem<Void> system = ActorSystem.create(RootBehavior.create(), "ClusterSystem", config);

    }
}