package pcd.part1.simengine_conc;

import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import pcd.part1.simengine_conc.message.MasterContext;
import pcd.part1.simengine_conc.message.WorkerContext;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class MasterAgent extends AbstractBehavior<MasterContext> {

	private AbstractEnvironment simEnv;
	private int t;
	private int dt;
	private boolean toBeInSyncWithWallTime;
	private int nStepsPerSec;
	private final int numSteps;
	private int countFinish;
	private int step=0;

	private List<AbstractAgent> agentList;

	private long currentWallTime;
	
	private final AbstractSimulation sim;

	private final List<ActorRef<WorkerContext>> childrens;
	
	public MasterAgent(ActorContext<MasterContext> context, AbstractSimulation sim, int numSteps, boolean syncWithTime) {
		super(context);
		childrens = new ArrayList<>();
		toBeInSyncWithWallTime = false;
		this.sim = sim;
		this.numSteps = numSteps;

		if (syncWithTime) {
			this.syncWithTime(25);
		}
	}

	private void syncWithTime(int nStepsPerSec) {
		this.toBeInSyncWithWallTime = true;
		this.nStepsPerSec = nStepsPerSec;
	}

	private void syncWithWallTime() {
		try {
			long newWallTime = System.currentTimeMillis();
			long delay = 1000 / this.nStepsPerSec;
			long wallTimeDT = newWallTime - currentWallTime;
			currentWallTime = System.currentTimeMillis();
			if (wallTimeDT < delay) {
				Thread.sleep(delay - wallTimeDT);
			}
		} catch (Exception ex) {}
		
	}

	@Override
	public Receive<MasterContext> createReceive() {
		return newReceiveBuilder()
				.onMessage(MasterContext.InitSimulation.class, this::onInit)
				.onMessage(MasterContext.FinishStep.class, this::ExecuteStep)
				.onMessage(MasterContext.StopSimulation.class, this::onStop)
				.build();
	}

	private Behavior<MasterContext> onStop(MasterContext.StopSimulation stopSimulation) {
		return Behaviors.stopped();
	}

	private Behavior<MasterContext> onInit(MasterContext.InitSimulation initSimulation) {
		return Behaviors.setup(context -> {
			simEnv = sim.getEnvironment();
			agentList = sim.getAgents();
			System.out.println("start simulation");
			simEnv.init();
			for (var a: agentList) {
				a.init(simEnv);
			}
			t = sim.getInitialTime();
			dt = sim.getTimeStep();
			sim.notifyReset(t, agentList, simEnv);
			simEnv.step(dt);
			simEnv.cleanActions();
			for (int i = 0; i < agentList.size(); i++) {
				ActorRef<WorkerContext> child = context.spawn(WorkerAgent.create(agentList.get(i),dt), "child" + i);
				childrens.add(child);
			}
			ActorRef<MasterContext> self = getContext().getSelf();
			for (var children : childrens) {
				children.tell(new WorkerContext.DoStep(self,dt));
			}

			return Behaviors.same();

		});
	}

	private Behavior<MasterContext> ExecuteStep(MasterContext.FinishStep finishStep) {
		countFinish++;
		if(numSteps<step){
			return Behaviors.stopped();
		}
		if(countFinish==agentList.size()){
			countFinish=0;
			return Behaviors.setup(context->{
				simEnv.processActions();

				sim.notifyNewStep(t, agentList, simEnv);
				if (toBeInSyncWithWallTime) {
					syncWithWallTime();
				}
				/* updating logic time */
				t += dt;
				step++;

				simEnv.step(dt);
				simEnv.cleanActions();
				ActorRef<MasterContext> self = getContext().getSelf();
				for (var children : childrens) {
					children.tell(new WorkerContext.DoStep(self,dt));
				}
				return Behaviors.same();
			});
		}
		return Behaviors.same();
	}


	public static Behavior<MasterContext> create(AbstractSimulation sim, int numSteps,boolean syncWithTime) {
		return Behaviors.setup(context -> new MasterAgent(context,sim,numSteps,syncWithTime));
	}
}
