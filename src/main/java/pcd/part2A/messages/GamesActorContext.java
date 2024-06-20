package pcd.part2A.messages;

import akka.actor.typed.ActorRef;

/**
 * Context about GridActor actor,
 * with list of message that receive
 */
public abstract class GamesActorContext {
    public static final class StartNewSudoku extends GamesActorContext{
        public ActorRef<PlayerActorContext> leader;
        public StartNewSudoku(ActorRef<PlayerActorContext> leader){
            super();
            this.leader = leader;
        }
    }
}
