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
	
	private AbstractAgent assignedSimAgents;
	private Trigger canDoStep;
	private int dt;
	private Flag stopFlag;
	private CyclicBarrier jobDone;
	
	public WorkerAgent(ActorContext<WorkerContext> context, AbstractAgent assignedSimAgents, int dt) {
		super(context);
		this.assignedSimAgents = assignedSimAgents;
		this.dt = dt;
		this.canDoStep = canDoStep;
		this.jobDone = jobDone;
	}

    public static Behavior<WorkerContext> create(AbstractAgent agent, int dt) {
		return Behaviors.setup(context -> new WorkerAgent(context,agent,dt));
    }





	@Override
	public Receive<WorkerContext> createReceive() {
		return newReceiveBuilder()
				.onMessage(WorkerContext.DoStep.class, this::onStep)
				.build();
	}

	private Behavior<WorkerContext> onStep(WorkerContext.DoStep doStep) {
		return Behaviors.setup(context->{
			assignedSimAgents.step(dt);
			System.out.println("finished step");
			doStep.replyTo.tell(new MasterContext.FinishStep());
			return Behaviors.same();
		});
	}
}
