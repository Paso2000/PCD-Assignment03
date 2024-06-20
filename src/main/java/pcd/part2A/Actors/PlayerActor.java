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

import java.util.List;
import java.util.Optional;

public class PlayerActor extends AbstractBehavior<PlayerActorContext> {
    private boolean isLeader;
    private Optional<List<ActorRef>> otherPlayers;
    private GUIGrid gui;
    private ActorRef<Receptionist.Listing> list;
    private ActorRef<GamesActorContext> games;

    public PlayerActor(ActorContext<PlayerActorContext> context, Boolean isLeader, ActorRef<GamesActorContext> games, Optional<Integer> nGame) {
        super(context);
        this.isLeader = isLeader;
        this.games = games;
        gui = new GUIGrid(context.getSelf());
        gui.setVisible(true);
        notifyGamesActor(context, isLeader, nGame);
    }

    private void notifyGamesActor(ActorContext<PlayerActorContext> context, Boolean isLeader, Optional<Integer> nGame) {
    if (isLeader){
        this.games.tell(new GamesActorContext.StartNewSudoku(context.getSelf()));
    }
    else{
        this.games.tell(new GamesActorContext.JoinInGrid(context.getSelf(), nGame));
    }
    }

    public static Behavior<PlayerActorContext> create(Boolean isLeader, ActorRef<GamesActorContext> games, Optional<Integer> nGame){
        return Behaviors.setup(ctx -> new PlayerActor(ctx, isLeader, games, nGame));
    }

    @Override
    public Receive<PlayerActorContext> createReceive() {
        return newReceiveBuilder()
                .onMessage(PlayerActorContext.SelectCell.class, this::onCellSelected)
                .onMessage(PlayerActorContext.ChangeCell.class, this::onValueChanged)
                .onMessage(PlayerActorContext.SolveSudoku.class, this::onSudokuSolved)
                .onMessage(PlayerActorContext.NotifyNewPlayer.class, this::onNotifyNewPlayer)
                .onMessage(PlayerActorContext.SendData.class, this::onSendData)
                .build();
    }

    private Behavior<PlayerActorContext> onSendData(PlayerActorContext.SendData sendData) {
        System.out.println("Aggiunto un nuovo player alla partita");
        return Behaviors.same();
    }

    private Behavior<PlayerActorContext> onNotifyNewPlayer(PlayerActorContext.NotifyNewPlayer notifyNewPlayer) {
        //mando un messaggio al player di cui ai il ref
        notifyNewPlayer.newPlayer.tell(new PlayerActorContext.SendData());
        return Behaviors.same();
    }

    private Behavior<PlayerActorContext> onSudokuSolved(PlayerActorContext.SolveSudoku solveSudoku) {
        System.out.println("message solve received");
        return Behaviors.same();
    }

    private Behavior<PlayerActorContext> onValueChanged(PlayerActorContext.ChangeCell changeCell) {
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
