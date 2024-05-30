package pcd.part2A;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import pcd.part1.simengine_conc.message.MasterContext;
import pcd.part2A.messages.GridActorContext;

public class GridActor extends AbstractBehavior<GridActorContext> {
    public GridActor(ActorContext<GridActorContext> context) {
        super(context);
    }

    public static Behavior<GridActorContext> create() {
        return Behaviors.setup(GridActor::new);
    }

    @Override
    public Receive<GridActorContext> createReceive() {
        return newReceiveBuilder()
                .onMessage(GridActorContext.UpdateCell.class, this::onUpdateCell)
                .onMessage(GridActorContext.SelectCell.class, this::onSelectCell)
                .onMessage(GridActorContext.PlayerLefted.class, this::onPlayerLeft)
                .onMessage(GridActorContext.PlayerJoined.class, this::onPlayerJoin)
                .build();
    }

    private Behavior<GridActorContext> onPlayerJoin(GridActorContext.PlayerJoined playerJoined) {
        //notify all player che un altro player è entrato
        return null;
    }

    private Behavior<GridActorContext> onPlayerLeft(GridActorContext.PlayerLefted playerLefted) {
        //notify all player che un altro player è uscito
        return null;
    }

    private Behavior<GridActorContext> onSelectCell(GridActorContext.SelectCell selectCell) {
        //fa la select a tutti i player della cella
        return null;
    }

    private Behavior<GridActorContext> onUpdateCell(GridActorContext.UpdateCell updateCell) {
        //fa l'update del valore della cella per tutti i player
    return null;
    }

}
