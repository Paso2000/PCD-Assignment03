package pcd.part2A.messages;

import akka.japi.Pair;
import jnr.ffi.annotations.In;

/**
 * Context about Player actor,
 * with list of message that receive
 */
public abstract class PlayerActorContext {
    public static final class CellSelected extends PlayerActorContext{

    }
    public static final class CellUpdated extends PlayerActorContext{
        public Pair<Integer,Integer> coordinate;
        public int value;

        public CellUpdated(Pair<Integer,Integer> coordinate, int value){
            this.coordinate=coordinate;
            this.value=value;
        }
    }
    public static final class NotifyPlayerJoined extends PlayerActorContext{}
    public static final class NotifyPlayerLefted extends PlayerActorContext{}
}
