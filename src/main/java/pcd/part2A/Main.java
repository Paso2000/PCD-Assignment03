package pcd.part2A;

import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

public class Main {
    public static Behavior<Void> create() {
        System.out.println("CREATE");
        return Behaviors.setup(context -> {
            var clusterListener = context.spawn(ClusterListener.create(), "ClusterListener");
            var greeter = context.spawn(Greeter.create(), "Greeter");
            greeter.tell(new Greeter.Greet("Hello, Akka Cluster!"));
            return Behaviors.empty();
        });
    }

    public static void main(String[] args) {
        System.out.println("START");
        ActorSystem<Void> system = ActorSystem.create(Main.create(), "HelloWorldCluster");
    }
}
