package pcd.part2A;

import akka.actor.typed.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import pcd.part2A.GUI.StartGUI;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class utils {
    public static void startup(String role, int port) {

        //System.out.println(role  + ":" + port + " started");
        // Override the configuration of the port
        Map<String, Object> overrides = new HashMap<>();
        overrides.put("akka.remote.artery.canonical.port", port);
        overrides.put("akka.cluster.roles", Collections.singletonList(role));

        Config config = ConfigFactory.parseMap(overrides)
                .withFallback(ConfigFactory.load("transformation"));

        ActorSystem<Void> system = ActorSystem.create(StartGUI.RootBehavior.create(), "ClusterSystem", config);

    }
}
