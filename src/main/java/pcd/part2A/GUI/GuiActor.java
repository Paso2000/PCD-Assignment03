package pcd.part2A.GUI;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import pcd.part2A.PlayerActor;
import pcd.part2A.messages.GuiActorContext;
import pcd.part2A.messages.PlayerActorContext;

public class GuiActor extends AbstractBehavior<GuiActorContext> {

    ActorContext<PlayerActorContext> player;
    public GuiActor(ActorContext<GuiActorContext> context,ActorContext<PlayerActorContext> player){
        super(context);
        this.player=player;
    }

    @Override
    public Receive<GuiActorContext> createReceive() {
        return newReceiveBuilder()
           .onMessage(GuiActorContext.UpdateCell.class, this::onUpdateCell)
                .build();
    }

    private Behavior<GuiActorContext> onUpdateCell(GuiActorContext.UpdateCell updateCell) {
        player.getSelf().tell(new PlayerActorContext.CellUpdated(updateCell.coordinate, updateCell.value));
        return Behaviors.same();
    }


    public static Behavior<GuiActorContext> create(ActorContext player) {
      return ctx-> Behaviors.setup(new GuiActor(ctx,player));
    }
}
