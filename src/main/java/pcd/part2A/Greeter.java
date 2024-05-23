package pcd.part2A;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.Behaviors;

public class Greeter {
    public static class Greet {
        public final String message;

        public Greet(String message) {
            this.message = message;
        }
    }

    public static Behavior<Greet> create() {
        System.out.println("GREETER CREATE");
        return Behaviors.setup(context ->
                Behaviors.receiveMessage(msg -> {
                    context.getLog().info(msg.message);
                    return Behaviors.same();
                })
        );
    }
}
