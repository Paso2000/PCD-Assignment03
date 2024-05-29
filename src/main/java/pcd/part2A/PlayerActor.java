package pcd.part2A;

import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;
import pcd.part2A.messages.PlayerActorContext;

public class PlayerActor extends AbstractBehavior<PlayerActorContext> {
    public PlayerActor(ActorContext<PlayerActorContext> context) {
        super(context);
    }

    @Override
    public Receive<PlayerActorContext> createReceive() {
        return null;
    }
}
