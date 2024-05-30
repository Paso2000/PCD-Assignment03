package pcd.part2A;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import pcd.part2A.messages.CoordinatorActorContext;
import pcd.part2A.messages.GridActorContext;
import pcd.part2A.messages.PlayerActorContext;

public class CoordinatorActor extends AbstractBehavior<CoordinatorActorContext> {
    public CoordinatorActor(ActorContext<CoordinatorActorContext> context) {
        super(context);
    }

    public static Behavior<CoordinatorActorContext> create() {
        return Behaviors.setup(CoordinatorActor::new);
    }

    @Override
    public Receive<CoordinatorActorContext> createReceive() {
        return newReceiveBuilder()
                .onMessage(CoordinatorActorContext.CreateGame.class, this::onCreateGame)
                .onMessage(CoordinatorActorContext.JoinGame.class, this::onJoinGame)
                .onMessage(CoordinatorActorContext.LeaveGame.class, this::onLeaveGame)
                .build();
    }

    private Behavior<CoordinatorActorContext> onLeaveGame(CoordinatorActorContext.LeaveGame leaveGame) {
        //rimuovo giocatore dalla partita
        return null;
    }

    private Behavior<CoordinatorActorContext> onJoinGame(CoordinatorActorContext.JoinGame joinGame) {
        //inserisco un giocatore nella partita
        return null;
    }

    private Behavior<CoordinatorActorContext> onCreateGame(CoordinatorActorContext.CreateGame createGame) {
        //creo un nuova partita con la mappa
        return null;
    }

}
