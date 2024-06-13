package pcd.part2A;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import pcd.part2A.messages.GuiActorContext;

public class GameActor extends AbstractBehavior<GuiActorContext> {


    private int[][] sudokuGrid;
    public static final ServiceKey<GuiActorContext> SERVICE_KEY = ServiceKey.create(GuiActorContext.class, "ServiceKey");
    public GameActor(ActorContext<GuiActorContext> context) {
        super(context);
        context.getSystem().receptionist().tell(Receptionist.register(SERVICE_KEY, context.getSelf()));
        System.out.println("backend started");
    }

    public static Behavior<GuiActorContext> create() {
        return Behaviors.setup(GameActor::new);
    }

    @Override
    public Receive<GuiActorContext> createReceive() {
        return newReceiveBuilder()
                .onMessage(GuiActorContext.UpdateCell.class, this::onUpdateCell)
                .onMessage(GuiActorContext.SelectCell.class, this::onSelectCell)
                .onMessage(GuiActorContext.PlayerLefted.class, this::onPlayerLeft)
                .onMessage(GuiActorContext.UpdateCell.class, this::onPlayerJoin)
                .build();
    }

    private Behavior<GuiActorContext> onPlayerJoin(GuiActorContext.UpdateCell playerJoined) {
        //notify all player che un altro player è entrato
        return null;
    }

    private Behavior<GuiActorContext> onPlayerLeft(GuiActorContext.PlayerLefted playerLefted) {
        //notify all player che un altro player è uscito
        return null;
    }

    private Behavior<GuiActorContext> onSelectCell(GuiActorContext.SelectCell selectCell) {
        //fa la select a tutti i player della cella
        return null;
    }

    private Behavior<GuiActorContext> onUpdateCell(GuiActorContext.UpdateCell updateCell) {

    return Behaviors.setup(context -> {
        sudokuGrid[updateCell.coord.first()][updateCell.coord.second()] = updateCell.value;
        updateCell.replyTo.tell();
        return Behaviors.same();
    });
    }

}