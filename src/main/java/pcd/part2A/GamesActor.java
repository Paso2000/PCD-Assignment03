package pcd.part2A;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.receptionist.Receptionist;
import akka.actor.typed.receptionist.ServiceKey;
import pcd.part2A.messages.GameActorContext;

public class GamesActor extends AbstractBehavior<GameActorContext> {


    private int[][] sudokuGrid;
    public static final ServiceKey<GameActorContext> SERVICE_KEY = ServiceKey.create(GameActorContext.class, "ServiceKey");
    public GamesActor(ActorContext<GameActorContext> context) {
        super(context);
        context.getSystem().receptionist().tell(Receptionist.register(SERVICE_KEY, context.getSelf()));
        System.out.println("backend started");
    }

    public static Behavior<GameActorContext> create() {
        return Behaviors.setup(GamesActor::new);
    }

    @Override
    public Receive<GameActorContext> createReceive() {
        return newReceiveBuilder()
             //   .onMessage(GuiActorContext.UpdateCell.class, this::onUpdateCell)
             //   .onMessage(GuiActorContext.SelectCell.class, this::onSelectCell)
              //  .onMessage(GuiActorContext.PlayerLefted.class, this::onPlayerLeft)
              //  .onMessage(GuiActorContext.UpdateCell.class, this::onPlayerJoin)
                .build();
    }


}