package pcd.part2A.Actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.receptionist.Receptionist;
import pcd.part2A.GUI.GUIGrid;
import pcd.part2A.messages.GridActorContext;
import pcd.part2A.messages.PlayerActorContext;

import java.util.List;
import java.util.Optional;

public class PlayerActor extends AbstractBehavior<PlayerActorContext> {

    private boolean isLeader;
    private Optional<List<ActorRef>> otherPlayers;
    private ActorRef<GridActorContext> gridActorRef;
    private GUIGrid gui;


    public PlayerActor(ActorContext<PlayerActorContext> context) {
        super(context);
        gridActorRef = context.spawnAnonymous(GridActor.create(context.getSelf()));
        gui = new GUIGrid(gridActorRef);
        gui.setVisible(true);
        //context.getSystem().receptionist().tell(Receptionist.subscribe(GamesActor.SERVICE_KEY, context));
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
