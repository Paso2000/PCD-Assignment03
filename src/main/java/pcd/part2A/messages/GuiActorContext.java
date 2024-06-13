package pcd.part2A.messages;

import akka.japi.Pair;
import jnr.ffi.annotations.In;

/**
 * Context about GridActor actor,
 * with list of message that receive
 */
public abstract class GuiActorContext {

    public static final class UpdateCell extends GuiActorContext {
        public int value;
        public Pair<Integer,Integer> coordinate;

        public UpdateCell(int value, Pair<Integer,Integer> coordinate){
            this.value=value;
            this.coordinate=coordinate;
        }
    }

}
