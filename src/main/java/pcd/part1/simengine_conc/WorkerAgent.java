package pcd.part1.simengine_conc;

import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import pcd.part1.simengine_conc.message.MasterContext;
import pcd.part1.simengine_conc.message.WorkerContext;

import java.util.List;
import java.util.concurrent.CyclicBarrier;

public class WorkerAgent extends AbstractBehavior<WorkerContext> {
	
	private final AbstractAgent assignedSimAgents;
	
	public WorkerAgent(ActorContext<WorkerContext> context, AbstractAgent assignedSimAgents) {
		super(context);
		this.assignedSimAgents = assignedSimAgents;
	}

    public static Behavior<WorkerContext> create(AbstractAgent agent, int dt) {
		return Behaviors.setup(context -> new WorkerAgent(context,agent));
    }


	@Override
	public Receive<WorkerContext> createReceive() {
		return newReceiveBuilder()
				.onMessage(WorkerContext.DoStep.class, this::onStep)
				.build();
	}

	private Behavior<WorkerContext> onStep(WorkerContext.DoStep doStep) {
		return Behaviors.setup(context->{
			assignedSimAgents.step(doStep.dt);
			System.out.println("finished step");
			doStep.replyTo.tell(new MasterContext.FinishStep());
			return Behaviors.same();
		});
	}
}
