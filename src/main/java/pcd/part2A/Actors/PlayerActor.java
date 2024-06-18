package pcd.part2A.Actors;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import pcd.part2A.GUI.GUIGrid;
import pcd.part2A.messages.PlayerActorContext;

import java.util.List;
import java.util.Optional;

public class PlayerActor extends AbstractBehavior<PlayerActorContext> {

    private boolean isLeader;
    private Optional<List<ActorRef>> otherPlayers;
    private GUIGrid gui;


    public PlayerActor(ActorContext<PlayerActorContext> context) {
        super(context);
        gui = new GUIGrid(context.getSelf());
        gui.setVisible(true);
        //context.getSystem().receptionist().tell(Receptionist.subscribe(GamesActor.SERVICE_KEY, context));
    }


    public static Behavior<PlayerActorContext> create(){
        return Behaviors.setup(ctx -> new PlayerActor(ctx));
    }

    @Override
    public Receive<PlayerActorContext> createReceive() {
        return newReceiveBuilder()
                .onMessage(PlayerActorContext.SelectCell.class, this::onCellSelected)
                .onMessage(PlayerActorContext.ChangeCell.class, this::onValueChanged)
                .onMessage(PlayerActorContext.SolveSudoku.class, this::onSudokuSolved)
                .build();
    }

    private Behavior<PlayerActorContext> onSudokuSolved(PlayerActorContext.SolveSudoku solveSudoku) {
        System.out.println("message solve received");
        return Behaviors.same();
    }

    private Behavior<PlayerActorContext> onValueChanged(PlayerActorContext.ChangeCell changeCell) {
        System.out.println("message value change received");
        return Behaviors.same();
    }

    private Behavior<PlayerActorContext> onCellSelected(PlayerActorContext.SelectCell selectCell) {
        System.out.println("message cell selected received");
        return Behaviors.same();
    }

}
