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

public class PlayerActor extends AbstractBehavior<PlayerActorContext> {
    private boolean isLeader;

    private ActorRef<PlayerActorContext> leader;
    private List<ActorRef<PlayerActorContext>> otherPlayers;
    private GUIGrid gui;
    private ActorRef<Receptionist.Listing> list;

    private int[][] grid;

    private ActorRef<Receptionist.Listing> messageAdapter;
    private ActorRef<GamesActorContext> games;

    public PlayerActor(ActorContext<PlayerActorContext> context, Boolean isLeader, ActorRef<GamesActorContext> games) {
        super(context);
        this.isLeader = isLeader;
        this.games = games;
        gui = new GUIGrid(context.getSelf());
        gui.setVisible(true);
        //messageAdapter=context.messageAdapter(Receptionist.Listing.class, ListingResponse::new);
        this.games.tell(new GamesActorContext.StartNewSudoku(context.getSelf()));
    }

    public static Behavior<PlayerActorContext> create(Boolean isLeader, ActorRef<GamesActorContext> games){
        return Behaviors.setup(ctx -> new PlayerActor(ctx, isLeader, games));
    }

    @Override
    public Receive<PlayerActorContext> createReceive() {
        return newReceiveBuilder()
                .onMessage(PlayerActorContext.SelectCell.class, this::onCellSelected)
                .onMessage(PlayerActorContext.LeaderSelect.class, this::onLeaderSelect)
                .onMessage(PlayerActorContext.SelectCellOfEveryone.class,this::onCellSelectedForEveryone)
                .onMessage(PlayerActorContext.ChangeCell.class, this::onValueChanged)
                .onMessage(PlayerActorContext.LeaderChange.class,this::onLeaderChange)
                .onMessage(PlayerActorContext.ChangeCellOfEveryone.class, this::onValueChangeForEveryone)
                .onMessage(PlayerActorContext.SolveSudoku.class, this::onSudokuSolved)
                .build();
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

    //lo riceve solo il leader
    private Behavior<PlayerActorContext> onValueChanged(PlayerActorContext.ChangeCell changeCell) {
        System.out.println("message value change received: ");
        System.out.println("row: " + changeCell.row +
                " col: " + changeCell.col + " value: " + changeCell.value);
        leader.tell(new PlayerActorContext.LeaderChange(changeCell.row, changeCell.col ,changeCell.value));
        return Behaviors.same();
    }

    private synchronized Behavior<PlayerActorContext> onLeaderChange(PlayerActorContext.LeaderChange leaderChange) {
        this.grid[leaderChange.row][leaderChange.col]= leaderChange.value;
        gui.render(grid);
        otherPlayers.forEach(player-> player.tell(new PlayerActorContext.ChangeCellOfEveryone(leaderChange.row, leaderChange.col ,leaderChange.value)));
        return Behaviors.same();
    }

    private Behavior<PlayerActorContext> onValueChangeForEveryone(PlayerActorContext.ChangeCellOfEveryone setCell) {
        this.grid[setCell.row][setCell.col]= setCell.value;
        gui.render(grid);
        return Behaviors.same();
    }

    private Behavior<PlayerActorContext> onCellSelected(PlayerActorContext.SelectCell selectCell) {
        System.out.println("message cell selected received");
        System.out.println("row: " + selectCell.row + " col: " + selectCell.col);
        leader.tell(new PlayerActorContext.LeaderSelect(selectCell.row, selectCell.col));
        return Behaviors.same();
    }
    private synchronized Behavior<PlayerActorContext> onLeaderSelect(PlayerActorContext.LeaderSelect leaderSelect) {
        gui.selectCell(leaderSelect.row,leaderSelect.col);
        otherPlayers.forEach(player-> player.tell(new PlayerActorContext.SelectCellOfEveryone(leaderSelect.row, leaderSelect.col)));
        return Behaviors.same();
    }

    private Behavior<PlayerActorContext> onCellSelectedForEveryone(PlayerActorContext.SelectCellOfEveryone selectCellOfEveryone) {
        gui.selectCell(selectCellOfEveryone.row,selectCellOfEveryone.col);
        return Behaviors.same();
    }

}
