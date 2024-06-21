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
import scala.Int;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayerActor extends AbstractBehavior<PlayerActorContext> {
    private static final int GRID_SIZE = 9;
    private boolean isLeader;
    private ActorRef<PlayerActorContext> leader;
    private Optional<List<ActorRef<PlayerActorContext>>> otherPlayers = Optional.of(new ArrayList<>());
    private GUIGrid gui;
    private ActorRef<Receptionist.Listing> list;
    private int[][] grid = new int[GRID_SIZE][GRID_SIZE];

    private ActorRef<Receptionist.Listing> messageAdapter;
    private ActorRef<GamesActorContext> games;

    public PlayerActor(ActorContext<PlayerActorContext> context, Boolean isLeader, ActorRef<GamesActorContext> games, Optional<Integer> nGame) {
        super(context);
        this.isLeader = isLeader;
        this.games = games;
        gui = new GUIGrid(context.getSelf());
        gui.setVisible(true);
        notifyGamesActor(context,isLeader,nGame);
    }

    private void notifyGamesActor(ActorContext<PlayerActorContext> context, Boolean isLeader, Optional<Integer> nGame) {
        if (this.isLeader) {
            this.leader = context.getSelf();
            this.games.tell(new GamesActorContext.StartNewSudoku(context.getSelf()));
        } else {
            this.games.tell(new GamesActorContext.JoinInGrid(context.getSelf(), nGame));
        }
    }

    public static Behavior<PlayerActorContext> create(Boolean isLeader, ActorRef<GamesActorContext> games, Optional<Integer> nGame) {
        return Behaviors.setup(ctx -> new PlayerActor(ctx, isLeader, games, nGame));
    }

    @Override
    public Receive<PlayerActorContext> createReceive() {
        return newReceiveBuilder()
                //GUI -> player
                .onMessage(PlayerActorContext.SelectCell.class, this::onCellSelected)
                // player -> leader
                .onMessage(PlayerActorContext.LeaderSelect.class, this::onLeaderSelect)
                //leader -> players
                .onMessage(PlayerActorContext.SelectCellOfEveryone.class, this::onCellSelectedForEveryone)
                //GUI -> player
                .onMessage(PlayerActorContext.ChangeCell.class, this::onValueChanged)
                .onMessage(PlayerActorContext.LeaderChange.class, this::onLeaderChange)
                .onMessage(PlayerActorContext.ChangeCellOfEveryone.class, this::onValueChangeForEveryone)
                .onMessage(PlayerActorContext.SolveSudoku.class, this::onSudokuSolved)
                .onMessage(PlayerActorContext.NotifyNewPlayer.class, this::onNotifyNewPlayer)
                .onMessage(PlayerActorContext.SendData.class, this::onSendData)
                .build();
    }
    //LEADER METHODS
    //il leader riceve onNotifyNewPlayer e manda un messaggio al nuovo player aggiornandolo
    private Behavior<PlayerActorContext> onNotifyNewPlayer(PlayerActorContext.NotifyNewPlayer notifyNewPlayer) {
        //aggiorno la mappa players
        otherPlayers.get().add(notifyNewPlayer.newPlayer);
        //gli devo mandare leader, stato della cella e lista players
        notifyNewPlayer.newPlayer.tell(new PlayerActorContext.SendData(leader, otherPlayers));
        return Behaviors.same();
    }
    private synchronized Behavior<PlayerActorContext> onLeaderSelect(PlayerActorContext.LeaderSelect leaderSelect) {
        gui.cleanGUI();
        gui.selectCell(leaderSelect.row, leaderSelect.col);
        otherPlayers.ifPresent(actorRefs
                -> actorRefs.forEach(player
                -> player.tell(new PlayerActorContext.SelectCellOfEveryone(leaderSelect.row, leaderSelect.col))));
        return Behaviors.same();
    }
    //JOINED PLAYER METHODS
    private Behavior<PlayerActorContext> onSendData(PlayerActorContext.SendData sendData) {
        this.leader = sendData.leader;
        this.otherPlayers = sendData.otherPlayers;
        System.out.println("onSendData");
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
        leader.tell(new PlayerActorContext.LeaderChange(changeCell.row, changeCell.col, changeCell.value));
        return Behaviors.same();
    }

    private synchronized Behavior<PlayerActorContext> onLeaderChange(PlayerActorContext.LeaderChange leaderChange) {
        System.out.println("leader received message value changed");
        //this.grid[leaderChange.row][leaderChange.col] = leaderChange.value;
        gui.render(leaderChange.row, leaderChange.col, leaderChange.value);
        otherPlayers.ifPresent(actorRefs
                -> actorRefs.forEach(player
                -> player.tell(new PlayerActorContext.ChangeCellOfEveryone(leaderChange.row, leaderChange.col, leaderChange.value))));
        return Behaviors.same();
    }

    private Behavior<PlayerActorContext> onValueChangeForEveryone(PlayerActorContext.ChangeCellOfEveryone setCell) {
        //this.grid[setCell.row][setCell.col] = setCell.value;
        gui.render(setCell.row, setCell.col, setCell.value);
        System.out.println("qualcuno ha vambiato");
        return Behaviors.same();
    }

    private Behavior<PlayerActorContext> onCellSelected(PlayerActorContext.SelectCell selectCell) {
        //System.out.println("message cell selected received");
        //System.out.println("row: " + selectCell.row + " col: " + selectCell.col);
        leader.tell(new PlayerActorContext.LeaderSelect(selectCell.row, selectCell.col));
        return Behaviors.same();
    }

    private Behavior<PlayerActorContext> onCellSelectedForEveryone(PlayerActorContext.SelectCellOfEveryone selectCellOfEveryone) {
        gui.cleanGUI();
        gui.selectCell(selectCellOfEveryone.row, selectCellOfEveryone.col);
        return Behaviors.same();
    }

}

