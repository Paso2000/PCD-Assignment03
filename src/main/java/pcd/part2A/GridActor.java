package pcd.part2A;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Receive;
import pcd.part1.simengine_conc.message.MasterContext;
import pcd.part2A.messages.GridActorContext;

public class GridActor extends AbstractBehavior<GridActorContext> {
    public GridActor(ActorContext<GridActorContext> context) {
        super(context);
    }

    @Override
    public Receive<GridActorContext> createReceive() {
        return newReceiveBuilder()
                .onMessage(GridActorContext.class, this::onInit)
                .build();
    }

    private Behavior<GridActorContext> onInit(GridActorContext gridActorContext) {
        return null;
    }
}
