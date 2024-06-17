package pcd.part2A.Actors;

import akka.actor.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import pcd.part2A.messages.PlayerActorContext;

import java.util.List;
import java.util.Optional;

public class PlayerActor extends AbstractBehavior<PlayerActorContext> {

    private boolean isLeader;
    Optional<List<ActorRef>> otherPlayers;

    public PlayerActor(ActorContext<PlayerActorContext> context) {
        super(context);
    }


    public static Behavior<PlayerActorContext> create(){
        return Behaviors.setup(PlayerActor::new);
    }

    @Override
    public Receive<PlayerActorContext> createReceive() {
        return newReceiveBuilder()
                //   .onMessage(PlayerActorContext.CellSelected.class, this::onCellSelected)
                //  .onMessage(PlayerActorContext.NotifyPlayerJoined.class, this::onNotifyPlayerJoined)
              //    .onMessage(PlayerActorContext.CellUpdated.class, this::onCellUpdated)
              //  .onMessage(PlayerActorContext.NotifyPlayerLefted.class, this::onNotifyPlayerLefted)
                .build();
    }

}
