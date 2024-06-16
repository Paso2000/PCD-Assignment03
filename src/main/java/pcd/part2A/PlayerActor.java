package pcd.part2A;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.receptionist.Receptionist;
import akka.japi.Pair;
import pcd.part2A.GUI.Gui;
import pcd.part2A.messages.GameActorContext;
import pcd.part2A.messages.PlayerActorContext;

import java.util.HashMap;
import java.util.Map;

public class PlayerActor extends AbstractBehavior<PlayerActorContext> {

    private ActorRef<GameActorContext> lobby;
    private ActorRef<PlayerActorContext> host;
    private String hostPlayerId= "";
    private Map<String,ActorRef<PlayerActorContext>> activePlayer = new HashMap<>();
    private Map<String,ActorRef<PlayerActorContext>> activeGames = new HashMap<>();
    private Gui gui;

    //var onGamesUpdated: (List[(String, ActorRef[PlayerMessage])] => Unit) = _ => ()
    private int[][] grid;

    public PlayerActor(ActorContext<PlayerActorContext> context) {
        super(context);
        System.out.println("Frontend started");
        this.gui = new Gui(context);
        gui.setVisible(true);

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
        msg.getServiceInstances(GamesActor.SERVICE_KEY)
                .forEach(context -> context.notifyAll());
        return Behaviors.same();
    }

    public void changeAll(Pair<Integer,Integer> coordinate, int value){

    }
}
