package pcd.part2A.Actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.receptionist.Receptionist;
import pcd.part2A.GUI.GUIGrid;
import pcd.part2A.SudokuGenerator;
import pcd.part2A.messages.GamesActorContext;
import pcd.part2A.messages.PlayerActorContext;

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
    private Optional<Integer> nGame;

    public PlayerActor(ActorContext<PlayerActorContext> context, Boolean isLeader, ActorRef<GamesActorContext> games, Optional<Integer> nGame) {
        super(context);
        this.isLeader = isLeader;
        this.games = games;
        gui = new GUIGrid(context.getSelf());
        gui.setVisible(true);
        notifyGamesActor(context, isLeader, nGame);
        this.nGame = nGame;
    }
    public static Behavior<PlayerActorContext> create(Boolean isLeader, ActorRef<GamesActorContext> games, Optional<Integer> nGame) {
        return Behaviors.setup(ctx -> new PlayerActor(ctx, isLeader, games, nGame));
    }
    @Override
    public Receive<PlayerActorContext> createReceive() {
        return newReceiveBuilder()
                .onMessage(PlayerActorContext.SimulateCrash.class, this::onCrash)
                .onMessage(PlayerActorContext.SelectCell.class, this::onCellSelected)
                .onMessage(PlayerActorContext.LeaderSelect.class, this::onLeaderSelect)
                .onMessage(PlayerActorContext.SelectCellOfEveryone.class, this::onCellSelectedForEveryone)
                .onMessage(PlayerActorContext.ChangeCell.class, this::onValueChanged)
                .onMessage(PlayerActorContext.LeaderChange.class, this::onLeaderChange)
                .onMessage(PlayerActorContext.ChangeCellOfEveryone.class, this::onValueChangeForEveryone)
                .onMessage(PlayerActorContext.SolveSudoku.class, this::onSudokuSolved)
                .onMessage(PlayerActorContext.LeaderSolve.class, this::onLeaderSolve)
                .onMessage(PlayerActorContext.SolveOfEveryone.class, this::onSolveOfEveryone)
                .onMessage(PlayerActorContext.NotifyNewPlayer.class, this::onNotifyNewPlayer)
                .onMessage(PlayerActorContext.SendData.class, this::onSendData)
                .onMessage(PlayerActorContext.LeaveGame.class, this::onLeaveGame)
                .onMessage(PlayerActorContext.DeletePlayer.class, this::onExitPlayer)
                .onMessage(PlayerActorContext.ChangeLeader.class, this::onExitLeader)
                .build();
    }

    //mando un'eccezzione per siulare il crash
    private Behavior<PlayerActorContext> onCrash(PlayerActorContext.SimulateCrash simulateCrash) {
        throw new IllegalArgumentException();
    }

    private void notifyGamesActor(ActorContext<PlayerActorContext> context, Boolean isLeader, Optional<Integer> nGame) {
        if (this.isLeader) {
            this.grid = SudokuGenerator.generateSudoku();
            gui.render(grid);
            this.leader = context.getSelf();
            this.games.tell(new GamesActorContext.StartNewSudoku(context.getSelf(), this.nGame));
        } else {
            this.games.tell(new GamesActorContext.JoinInGrid(context.getSelf(), nGame));
        }
    }

    private Behavior<PlayerActorContext> onExitLeader(PlayerActorContext.ChangeLeader changeLeader) {
        otherPlayers.get().remove(changeLeader.player);
        this.leader = changeLeader.player;
        return Behaviors.same();
    }

    private Behavior<PlayerActorContext> onExitPlayer(PlayerActorContext.DeletePlayer deletePlayer) {
        otherPlayers.get().remove(deletePlayer.player);
        return Behaviors.same();
    }

    //metodo per capire chi sta uscendo dalla partita e gestirlo
    private Behavior<PlayerActorContext> onLeaveGame(PlayerActorContext.LeaveGame leaveGame) {
        //bisogna controllare che l'utente non sia il leader
        //se non è rimasto solo questo giocatore nella list
        if (!otherPlayers.get().isEmpty()) {
            //se è il leader va cambiato il leader
            if (this.leader.equals(leaveGame.player)) {
                ActorRef<PlayerActorContext> newLeader = otherPlayers.get().getFirst();
                games.tell(new GamesActorContext.ChangeLeader(newLeader, leaveGame.player, nGame.get()));
                otherPlayers.get().forEach(player -> player.tell(new PlayerActorContext.ChangeLeader(newLeader)));

                //manda un messaggio dove dici a tutti di cambiare il leader con quello che gli passo cavandolo da otherPlayers
            } else {
                games.tell(new GamesActorContext.DeletePlayer(leaveGame.player, nGame.get()));
                otherPlayers.get().forEach(player -> player.tell(new PlayerActorContext.DeletePlayer(leaveGame.player)));
                this.leader.tell(new PlayerActorContext.DeletePlayer(leaveGame.player));
            }
        }else{
            //se sono l'ultimo della partita la elimino
            games.tell(new GamesActorContext.DeleteMatch(nGame.get()));
        }
        return Behaviors.same();
    }

    //messaggio ricevuto solo dal leader
    private Behavior<PlayerActorContext> onNotifyNewPlayer(PlayerActorContext.NotifyNewPlayer notifyNewPlayer) {
        //aggiorno la mappa players
        otherPlayers.get().add(notifyNewPlayer.newPlayer);
        //faccio controllare al leader se i giocatori che entrano vanno in crash
        getContext().watchWith(notifyNewPlayer.newPlayer, new PlayerActorContext.LeaveGame(notifyNewPlayer.newPlayer));
        //gli devo mandare leader, stato della cella e lista players
        notifyNewPlayer.newPlayer.tell(new PlayerActorContext.SendData(leader, otherPlayers, this.grid));
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

    private Behavior<PlayerActorContext> onSendData(PlayerActorContext.SendData sendData) {
        this.leader = sendData.leader;
        this.grid = sendData.grid;
        gui.render(grid);
        this.otherPlayers = sendData.otherPlayers;
        System.out.println("onSendData");
        getContext().watchWith(this.leader, new PlayerActorContext.LeaveGame(this.leader));//faccio controllare che il leader no vada in crash da chi entra
        return Behaviors.same();
    }

    private Behavior<PlayerActorContext> onSudokuSolved(PlayerActorContext.SolveSudoku solveSudoku) {
        int[][] solvedGrid = SudokuGenerator.solveSudoku(this.grid);
        gui.render(solvedGrid);
        leader.tell(new PlayerActorContext.LeaderSolve(solvedGrid));
        return Behaviors.same();
    }

    private Behavior<PlayerActorContext> onLeaderSolve(PlayerActorContext.LeaderSolve leaderSolve) {
        this.grid = leaderSolve.solvedGrid;
        gui.render(this.grid);
        otherPlayers.ifPresent(actorRefs
                -> actorRefs.forEach(player
                -> player.tell(new PlayerActorContext.SolveOfEveryone(leaderSolve.solvedGrid))));
        return Behaviors.same();
    }

    private Behavior<PlayerActorContext> onSolveOfEveryone(PlayerActorContext.SolveOfEveryone solveOfEveryone) {
        this.grid = solveOfEveryone.solvedGrid;
        gui.render(this.grid);
        return Behaviors.same();
    }

    private Behavior<PlayerActorContext> onValueChanged(PlayerActorContext.ChangeCell changeCell) {
        //System.out.println("message value change received: ");
        //System.out.println("row: " + changeCell.row +" col: " + changeCell.col + " value: " + changeCell.value);
        this.grid[changeCell.row][changeCell.col] = changeCell.value;
        leader.tell(new PlayerActorContext.LeaderChange(changeCell.row, changeCell.col, changeCell.value));
        return Behaviors.same();
    }

    private synchronized Behavior<PlayerActorContext> onLeaderChange(PlayerActorContext.LeaderChange leaderChange) {
        //System.out.println("leader received message value changed");
        this.grid[leaderChange.row][leaderChange.col] = leaderChange.value;
        gui.render(this.grid);
        otherPlayers.ifPresent(actorRefs
                -> actorRefs.forEach(player
                -> player.tell(new PlayerActorContext.ChangeCellOfEveryone(leaderChange.row, leaderChange.col, leaderChange.value))));
        return Behaviors.same();
    }

    private Behavior<PlayerActorContext> onValueChangeForEveryone(PlayerActorContext.ChangeCellOfEveryone setCell) {
        this.grid[setCell.row][setCell.col] = setCell.value;
        gui.render(grid);
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

