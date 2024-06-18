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
        public int row;
        public int col;
        public SelectCell(int row, int col) {
            super();
            this.row = row;
            this.col = col;
        }
    }

    public static final class ChangeCell extends PlayerActorContext{
        public int row;
        public int col;
        public int value;
        public ChangeCell(int row, int col, int value) {
            super();
            this.row = row;
            this.col = col;
            this.value = value;
        }
    }
}
