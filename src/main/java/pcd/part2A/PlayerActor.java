package pcd.part2A;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.receptionist.Receptionist;
import pcd.part2A.messages.GuiActorContext;
import pcd.part2A.messages.PlayerActorContext;

public class PlayerActor extends AbstractBehavior<PlayerActorContext> {

    private int[][] grid;
    private ActorContext<Receptionist.Listing> context;

    public PlayerActor(ActorContext<PlayerActorContext> context) {
        super(context);
        System.out.println("Frontend started");
    }

    public static Behavior<PlayerActorContext> create(){
        return Behaviors.setup(PlayerActor::new);
    }

    @Override
    public Receive<PlayerActorContext> createReceive() {
        return newReceiveBuilder()
             //   .onMessage(PlayerActorContext.CellSelected.class, this::onCellSelected)
              //  .onMessage(PlayerActorContext.NotifyPlayerJoined.class, this::onNotifyPlayerJoined)
                  .onMessage(PlayerActorContext.CellUpdated.class, this::onCellUpdated)
              //  .onMessage(PlayerActorContext.NotifyPlayerLefted.class, this::onNotifyPlayerLefted)
                .build();
    }

    private Behavior<PlayerActorContext> onCellUpdated(PlayerActorContext.CellUpdated cellUpdated) {
        return Behaviors.setup(context -> {
            grid[cellUpdated.coordinate.first()][cellUpdated.coordinate.second()] = cellUpdated.value;
            System.out.println(cellUpdated.value);
            return Behaviors.same();
        });
    }


    private Behavior<Receptionist.Listing> behavior() {
        return Behaviors.receive(Receptionist.Listing.class)
                .onMessage(Receptionist.Listing.class, this::onListing)
                .build();
    }

    private Behavior<Receptionist.Listing> onListing(Receptionist.Listing msg) {
        msg.getServiceInstances(GameActor.SERVICE_KEY)
                .forEach(context -> context.notifyAll());
        return Behaviors.same();
    }
}
