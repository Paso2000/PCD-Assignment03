package pcd.part2A.messages;

import akka.actor.typed.ActorRef;
import jnr.ffi.annotations.In;

import java.util.Optional;

/**
 * Context about GridActor actor,
 * with list of message that receive
 */
public abstract class GamesActorContext {
    public static final class StartNewSudoku extends GamesActorContext{
        public ActorRef<PlayerActorContext> leader;
        public Optional<Integer> nGame;
        public StartNewSudoku(ActorRef<PlayerActorContext> leader, Optional<Integer> nGame){
            this.leader = leader;
            this.nGame = nGame;
        }
    }
    public static final class JoinInGrid extends GamesActorContext{
        public ActorRef<PlayerActorContext> player;
        public Optional<Integer> nGame;
        public JoinInGrid(ActorRef<PlayerActorContext> player, Optional<Integer> nGame){
            this.player = player;
            this.nGame = nGame;
        }
    }

    public static final class DeletePlayer extends GamesActorContext{

        public ActorRef<PlayerActorContext> playerToDelete;

        public Integer nGames;


        public DeletePlayer(ActorRef<PlayerActorContext> playerToDelete, Integer nGames){
            this.playerToDelete=playerToDelete;
            this.nGames=nGames;
        }
    }


    public static final class ChangeLeader extends GamesActorContext{
        public ActorRef<PlayerActorContext> leader;
        public ActorRef<PlayerActorContext> player;

        public Integer nGames;
        public ChangeLeader(ActorRef<PlayerActorContext> leader, ActorRef<PlayerActorContext> player, Integer nGames){
            this.leader = leader;
            this.player=player;
            this.nGames=nGames;
        }
    }
}
