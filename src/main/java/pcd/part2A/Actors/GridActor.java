package pcd.part2A.Actors;


import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import pcd.part2A.GUI.GUIGrid;
import pcd.part2A.messages.GamesActorContext;
import pcd.part2A.messages.GridActorContext;
import pcd.part2A.messages.PlayerActorContext;


public class GridActor extends AbstractBehavior<GridActorContext> {
    ActorRef<PlayerActorContext> playerActorContextActorRef;

    public GridActor(ActorContext<GridActorContext> context, ActorRef<PlayerActorContext> playerActorContextActorRef) {
        super(context);
        this.playerActorContextActorRef = playerActorContextActorRef;
    }

    public static Behavior<GridActorContext> create(ActorRef<PlayerActorContext> playerActorContextActorRef){
        System.out.println("GridActor created");
        return Behaviors.setup(ctx -> new GridActor(ctx, playerActorContextActorRef));
    }

    @Override
    public Receive<GridActorContext> createReceive() {
        return newReceiveBuilder()
                .onMessage(GridActorContext.genericMessage.class, this::printMessage)
                .build();
    }

    private Behavior<GridActorContext> printMessage(GridActorContext.genericMessage genericMessage) {
        System.out.println("hai premuto solve");
        return Behaviors.same();
    }
}

