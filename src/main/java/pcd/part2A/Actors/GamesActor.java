package pcd.part2A.Actors;

import akka.actor.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import pcd.part2A.messages.GamesActorContext;

import java.util.List;
import java.util.Map;

public class GamesActor extends AbstractBehavior<GamesActorContext> {
    Map<Integer, Map<ActorRef, List<ActorRef>>> game;
    public static final ServiceKey<GamesActorContext> SERVICE_KEY = ServiceKey.create(GamesActorContext.class, "ServiceKey");

    public GamesActor(ActorContext<GamesActorContext> context) {
        super(context);
        //registra se stesso al receptionist attraversio la servikey
        context.getSystem().receptionist().tell(Receptionist.register(SERVICE_KEY, context.getSelf()));
    }

    public static Behavior<GamesActorContext> create() {
        return Behaviors.setup(GamesActor::new);
    }

    @Override
    public Receive<GamesActorContext> createReceive() {
        return newReceiveBuilder()
                .onMessage(GamesActorContext.StartNewSudoku.class, this::onStartNewGame)
                .build();
    }

    private Behavior<GamesActorContext> onStartNewGame(GamesActorContext.StartNewSudoku startNewSudoku) {
        System.out.println("start new game");
        return null;
    }
    
}