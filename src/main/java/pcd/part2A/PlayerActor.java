package pcd.part2A;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import pcd.part2A.messages.GridActorContext;
import pcd.part2A.messages.PlayerActorContext;

public class PlayerActor extends AbstractBehavior<PlayerActorContext> {
    public PlayerActor(ActorContext<PlayerActorContext> context) {
        super(context);
    }

    public static Behavior<PlayerActorContext> create() {
        return Behaviors.setup(PlayerActor::new);
    }

    @Override
    public Receive<PlayerActorContext> createReceive() {
        return newReceiveBuilder()
                .onMessage(PlayerActorContext.CellSelected.class, this::onCellSelected)
                .onMessage(PlayerActorContext.NotifyPlayerJoined.class, this::onNotifyPlayerJoined)
                .onMessage(PlayerActorContext.CellUpdated.class, this::onCellUpdated)
                .onMessage(PlayerActorContext.NotifyPlayerLefted.class, this::onNotifyPlayerLefted)
                .build();
    }

    private Behavior<PlayerActorContext> onNotifyPlayerLefted(PlayerActorContext.NotifyPlayerLefted notifyPlayerLefted) {
        //visualizza che un giocatore ha lasciato
        return null;
    }

    private Behavior<PlayerActorContext> onCellUpdated(PlayerActorContext.CellUpdated cellUpdated) {
        //viusalizza il nuovo valore della cella
        return null;
    }

    private Behavior<PlayerActorContext> onNotifyPlayerJoined(PlayerActorContext.NotifyPlayerJoined notifyPlayerJoined) {
        //visualizza che un player si è unito
        return null;
    }

    private Behavior<PlayerActorContext> onCellSelected(PlayerActorContext.CellSelected cellSelected) {
        //visualizza che una cella è selezionata
        return null;
    }
}
