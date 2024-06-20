
package pcd.part2A;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;
import akka.cluster.Cluster;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import pcd.part2A.Actors.GamesActor;
import pcd.part2A.Actors.PlayerActor;
import pcd.part2A.messages.GamesActorContext;
import scala.Int;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Utils {
    public static void startup(String role, int port, Optional<Integer> nGame) {
        Map<String, Object> overrides = new HashMap<>();
        overrides.put("akka.remote.artery.canonical.port", port);
        overrides.put("akka.cluster.roles", Collections.singletonList(role));
        Config config = ConfigFactory.parseMap(overrides)
                .withFallback(ConfigFactory.load("transformation"));
        ActorSystem<Void> system = ActorSystem.create(RootBehavior.create(nGame), "ClusterSystem", config);
    }

    public static class RootBehavior {
        static ActorRef<GamesActorContext> games;

        public static Behavior<Void> create(Optional<Integer> nGame) {
            return Behaviors.setup(context -> {
                //inserisce il nodo nel cluster
                Cluster cluster = Cluster.get(context.getSystem());
                if (cluster.selfMember().hasRole("backend")) {
                    games = context.spawn(GamesActor.create(), "Backend");
                }
                if (cluster.selfMember().hasRole("playerNewGame")) {
                    context.spawn(PlayerActor.create(true, games, nGame), "Player");
                }
                if (cluster.selfMember().hasRole("playerJoinGame")) {
                    context.spawn(PlayerActor.create(false, games, nGame), "Player");
                }
                return Behaviors.empty();
            });
        }
    }

}
