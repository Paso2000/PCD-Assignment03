package pcd.part2A;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.receptionist.Receptionist;
import pcd.part2A.messages.PlayerActorContext;

public class PlayerActor extends AbstractBehavior<PlayerActorContext> {
    private ActorContext<Receptionist.Listing> context;
    public PlayerActor(ActorContext<PlayerActorContext> context) {
        super(context);
        System.out.println("Frontend started");
    }

    public static Behavior<PlayerActor> create(){context-> return new PlayerActor(context);
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
    private Behavior<Receptionist.Listing> behavior() {
        return Behaviors.receive(Receptionist.Listing.class)
                .onMessage(Receptionist.Listing.class, this::onListing)
                .build();
    }

    private Behavior<Receptionist.Listing> onListing(Receptionist.Listing msg) {
        msg.getServiceInstances(GameActor.SERVICE_KEY)
                .forEach(context -> context.notifyAll());
        return Behaviors.same();
    }
}
