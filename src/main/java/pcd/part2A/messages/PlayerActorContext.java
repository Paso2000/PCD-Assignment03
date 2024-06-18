package pcd.part2A.messages;

import akka.japi.Pair;
import jnr.ffi.annotations.In;
import pcd.part2A.Actors.PlayerActor;

/**
 * Context about Player actor,
 * with list of message that receive
 */
public abstract class PlayerActorContext {
    public static final class SolveSudoku extends PlayerActorContext {
        //add campi
        //add construttore
    }
    public static final class SelectCell extends PlayerActorContext{
        //add campi
        //add construttore
    }
    public static final class ChangeCell extends PlayerActorContext{
        //add campi
        //add construttore
    }
}
