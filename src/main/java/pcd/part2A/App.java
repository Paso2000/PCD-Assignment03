package pcd.part2A;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import akka.cluster.typed.Cluster;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import pcd.part2A.GUI.StartGUI;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
public class App{
    private static class RootBehavior {
        static Behavior<Void> create() {

            return Behaviors.setup(context -> {
                //inserisce il nodo nel cluster
                Cluster cluster = Cluster.get(context.getSystem());

                if (cluster.selfMember().hasRole("backend")) {
                    context.spawn(GridActor.create(), "Backend");
                    }
                if (cluster.selfMember().hasRole("frontend")) {
                    context.spawn(PlayerActor.create(), "Frontend");
                }
                return Behaviors.empty();
            });
        }
    }

    public static void main(String[] args) {
        StartGUI frame = new StartGUI();
        frame.setVisible(true);
        //  startup("backend", 25251);
        // startup("backend", 25252);
        // startup("frontend", 0);
        // startup("frontend", 0);
        startup("frontend", 0);
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
