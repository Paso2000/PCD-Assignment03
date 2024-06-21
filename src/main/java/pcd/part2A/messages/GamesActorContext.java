package pcd.part2A.messages;

import akka.actor.typed.ActorRef;

import java.util.Optional;

/**
 * Context about GridActor actor,
 * with list of message that receive
 */
public abstract class GamesActorContext {
    public static final class StartNewSudoku extends GamesActorContext{
        public ActorRef<PlayerActorContext> leader;
        public StartNewSudoku(ActorRef<PlayerActorContext> leader){
            this.leader = leader;
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
        public ActorRef<PlayerActorContext> leader;
        public DeletePlayer(ActorRef<PlayerActorContext> leader){
            this.leader = leader;
        }
    }


    public static final class ChangeLeader extends GamesActorContext{
        public ActorRef<PlayerActorContext> leader;
        public ChangeLeader(ActorRef<PlayerActorContext> leader){
            this.leader = leader;
        }
    }
}
