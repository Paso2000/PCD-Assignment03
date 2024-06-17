package pcd.part2A.Actors;

import akka.actor.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import pcd.part2A.messages.GamesActorContext;

import java.util.List;
import java.util.Map;

public class GamesActor extends AbstractBehavior<GamesActorContext> {
    Map<Integer, Map<ActorRef, List<ActorRef>>> game;

    public GamesActor(ActorContext<GamesActorContext> context) {
        super(context);
    }

    public static Behavior<GamesActorContext> create() {
        return Behaviors.setup(GamesActor::new);
    }

    @Override
    public Receive<GamesActorContext> createReceive() {
        return newReceiveBuilder()
                .onMessage(GamesActorContext.startNewSudoku.class, this::onStartNewGame)
                .build();
    }

    private Behavior<GamesActorContext> onStartNewGame(GamesActorContext.startNewSudoku startNewSudoku) {
        return null;
    }
    
}