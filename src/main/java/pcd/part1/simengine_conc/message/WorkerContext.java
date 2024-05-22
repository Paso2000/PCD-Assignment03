package pcd.part1.simengine_conc.message;

import akka.actor.typed.ActorRef;

public class WorkerContext {
    public static final class DoStep extends WorkerContext {
        public final ActorRef<MasterContext> replyTo;

        public int dt;

        public DoStep(ActorRef<MasterContext> replyTo, int dt){
            this.replyTo=replyTo;this.dt=dt;
        }
    }
}
