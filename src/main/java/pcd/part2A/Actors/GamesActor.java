package pcd.part2A.Actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import akka.japi.Pair;
import pcd.part2A.messages.GamesActorContext;
import pcd.part2A.messages.PlayerActorContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GamesActor extends AbstractBehavior<GamesActorContext> {

    private Map<Integer, Pair<ActorRef<PlayerActorContext>, List<ActorRef<PlayerActorContext>>>> games = new HashMap<>();
    private int gamesNumber=0;
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
                .onMessage(GamesActorContext.JoinInGrid.class, this::onJoinInGrid)
                .build();
    }

    private Behavior<GamesActorContext> onJoinInGrid(GamesActorContext.JoinInGrid joinInGrid) {
        //aggiugne il player corrente nella lista di quella partita
        games.get(joinInGrid.nGame.get()).second().add(joinInGrid.player);
        //System.out.println(games);
        //manda un messaggio al leader, con il riferimento del player
        games.get(joinInGrid.nGame.get()).first().tell(new PlayerActorContext.NotifyNewPlayer(joinInGrid.player));
        return Behaviors.same();
    }

    private Behavior<GamesActorContext> onStartNewGame(GamesActorContext.StartNewSudoku startNewSudoku) {

        gamesNumber++;
        Pair<ActorRef<PlayerActorContext>, List<ActorRef<PlayerActorContext>>> allPlayers  = new Pair<>(startNewSudoku.leader, new ArrayList<ActorRef<PlayerActorContext>>());
        this.games.put(gamesNumber, allPlayers);
        System.out.println(games);
        System.out.println("start new game");
        return Behaviors.same();
    }
    
}