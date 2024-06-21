package pcd.part2A.messages;

import akka.japi.Pair;

/**
 * Context about GridActor actor,
 * with list of message that receive
 */
public abstract class GameActorContext {

    public static final class UpdateCell extends GameActorContext {
        public int value;
        public Pair<Integer,Integer> coordinate;

        public UpdateCell(int value, Pair<Integer,Integer> coordinate){
            this.value=value;
            this.coordinate=coordinate;
        }
    }

}