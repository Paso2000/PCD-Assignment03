package pcd.part2A.Actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.receptionist.Receptionist;
import pcd.part2A.GUI.GUIGrid;
import pcd.part2A.messages.GamesActorContext;
import pcd.part2A.messages.PlayerActorContext;

import java.util.List;
import java.util.Optional;

public class PlayerActor extends AbstractBehavior<PlayerActorContext> {
    private boolean isLeader;

    private ActorRef<PlayerActorContext> leader;
    private List<ActorRef<PlayerActorContext>> otherPlayers;
    private GUIGrid gui;
    private ActorRef<Receptionist.Listing> list;

    private ActorRef<Receptionist.Listing> messageAdapter;
    private ActorRef<GamesActorContext> games;

    private static class ListingResponse extends PlayerActorContext {
        final Receptionist.Listing listing;

        private ListingResponse(Receptionist.Listing listing) {
            this.listing = listing;
        }
    }


    public PlayerActor(ActorContext<PlayerActorContext> context, Boolean isLeader) {
        super(context);
        this.isLeader = isLeader;
        gui = new GUIGrid(context.getSelf());
        gui.setVisible(true);
        messageAdapter=context.messageAdapter(Receptionist.Listing.class, ListingResponse::new);
    }

    public static Behavior<PlayerActorContext> create(Boolean isLeader){
        return Behaviors.setup(ctx -> new PlayerActor(ctx, isLeader));
    }

    @Override
    public Receive<PlayerActorContext> createReceive() {
        return newReceiveBuilder()
                .onMessage(PlayerActorContext.SelectCell.class, this::onCellSelected)
                .onMessage(PlayerActorContext.ChangeCell.class, this::onValueChanged)
                .onMessage(PlayerActorContext.SolveSudoku.class, this::onSudokuSolved)
                .onMessage(PlayerActorContext.SetCell.class, this::onValueSet)
                .build();
    }

    private synchronized Behavior<PlayerActorContext> onValueSet(PlayerActorContext.SetCell setCell) {

    }

    private Behavior<Receptionist.Listing> onList(Receptionist.Listing msg) {
        //games = msg.allServiceInstances(GamesActor.SERVICE_KEY).iterator().next();
        //games.tell(new GamesActorContext.startNewSudoku());
       return Behaviors.same();
    }

    private Behavior<PlayerActorContext> onSudokuSolved(PlayerActorContext.SolveSudoku solveSudoku) {
        System.out.println("message solve received");
        return Behaviors.same();
    }

    private Behavior<PlayerActorContext> onValueChanged(PlayerActorContext.ChangeCell changeCell) {
        leader.tell(new PlayerActorContext.SetCell(changeCell.row,changeCell.col,changeCell.value));
        System.out.println("message value change received: ");
        System.out.println("row: " + changeCell.row +
                " col: " + changeCell.col + " value: " + changeCell.value);
        return Behaviors.same();
    }

    private Behavior<PlayerActorContext> onCellSelected(PlayerActorContext.SelectCell selectCell) {
        System.out.println("message cell selected received");
        System.out.println("row: " + selectCell.row + " col: " + selectCell.col);
        return Behaviors.same();
    }

}
