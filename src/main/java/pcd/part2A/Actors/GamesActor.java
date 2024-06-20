package pcd.part2A.Actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import pcd.part2A.messages.GamesActorContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamesActor extends AbstractBehavior<GamesActorContext> {
    private Map<Integer, Map<ActorRef, List<ActorRef>>> games;
    private int gamesNumber;
    public static final ServiceKey<GamesActorContext> SERVICE_KEY = ServiceKey.create(GamesActorContext.class, "ServiceKey");

    public GamesActor(ActorContext<GamesActorContext> context) {
        super(context);
        gamesNumber = 0;
        games = new HashMap<>();
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
        gamesNumber++;
        Map<ActorRef, List<ActorRef>> allPlayers = new HashMap<>();
        allPlayers.put(startNewSudoku.leader, new ArrayList<ActorRef>());
        games.put(gamesNumber, allPlayers);
        System.out.println(games);
        System.out.println("start new game");
        return Behaviors.same();
    }
    
}