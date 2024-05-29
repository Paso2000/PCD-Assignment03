package pcd.part2A;

import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;
import pcd.part2A.messages.CoordinatorActorContext;

public class CoordinatorActor extends AbstractBehavior<CoordinatorActorContext> {
    public CoordinatorActor(ActorContext<CoordinatorActorContext> context) {
        super(context);
    }

    @Override
    public Receive<CoordinatorActorContext> createReceive() {
        return null;
    }
}
