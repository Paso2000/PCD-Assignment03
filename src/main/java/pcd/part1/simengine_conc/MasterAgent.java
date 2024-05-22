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
	
	private boolean toBeInSyncWithWallTime;
	private int nStepsPerSec;
	private int numSteps;

	private long currentWallTime;
	
	private AbstractSimulation sim;
	private Flag stopFlag;
	private Semaphore done;
	private int nWorkers;

	private List<ActorRef<WorkerContext>> childrens;
	
	public MasterAgent(ActorContext<MasterContext> context, AbstractSimulation sim, int nWorkers, int numSteps, Flag stopFlag, Semaphore done, boolean syncWithTime) {
		super(context);
		childrens = new ArrayList<ActorRef<WorkerContext>>();
		toBeInSyncWithWallTime = false;
		this.sim = sim;
		this.stopFlag = stopFlag;
		this.numSteps = numSteps;
		this.done = done;
		this.nWorkers = nWorkers;
		
		if (syncWithTime) {
			this.syncWithTime(25);
		}
	}

	public void run() {
		
		log("booted");
		
		var simEnv = sim.getEnvironment();
		var simAgents = sim.getAgents();
		
		simEnv.init();
		for (var a: simAgents) {
			a.init(simEnv);
		}

		int t = sim.getInitialTime();
		int dt = sim.getTimeStep();
		
		sim.notifyReset(t, simAgents, simEnv);
		
		//Trigger canDoStep = new Trigger(nWorkers);
		//CyclicBarrier jobDone = new CyclicBarrier(nWorkers + 1);
		
		log("creating workers...");
		
		//int nAssignedAgentsPerWorker = simAgents.size()/nWorkers;

//		int index = 0;
//		List<WorkerAgent> workers = new ArrayList<>();
//		for (int i = 0; i < nWorkers - 1; i++) {
//			List<AbstractAgent> assignedSimAgents = new ArrayList<>();
//			for (int j = 0; j < nAssignedAgentsPerWorker; j++) {
//				assignedSimAgents.add(simAgents.get(index));
//				index++;
//			}
//
//			WorkerAgent worker = new WorkerAgent("worker-"+i, assignedSimAgents, dt, canDoStep, jobDone, stopFlag);
//			worker.start();
//			workers.add(worker);
//		}
//
//		List<AbstractAgent> assignedSimAgents = new ArrayList<>();
//		while (index < simAgents.size()) {
//			assignedSimAgents.add(simAgents.get(index));
//			index++;
//		}
//
//		WorkerAgent worker = new WorkerAgent("worker-"+(nWorkers-1), assignedSimAgents, dt, canDoStep, jobDone, stopFlag);
//		worker.start();
//		workers.add(worker);

		log("starting the simulation loop.");

		int step = 0;
		currentWallTime = System.currentTimeMillis();

		try {
			while (!stopFlag.isSet() &&  step < numSteps) {
				
				simEnv.step(dt);
				simEnv.cleanActions();

				/* trigger workers to do their work in this step */	
				//canDoStep.trig();
				
				/* wait for workers to complete */
				//jobDone.await();

				/* executed actions */
				simEnv.processActions();
								
				sim.notifyNewStep(t, simAgents, simEnv);
	
				if (toBeInSyncWithWallTime) {
					syncWithWallTime();
				}
				
				/* updating logic time */
				
				t += dt;
				step++;
			}	
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		log("done");
		stopFlag.set();
		//canDoStep.trig();

		done.release();
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
	
	private void log(String msg) {
		System.out.println("[MASTER] " + msg);
	}


	@Override
	public Receive<MasterContext> createReceive() {
		return newReceiveBuilder()
				.onMessage(MasterContext.InitSimulation.class, this::onInit)
				.onMessage(MasterContext.FinishStep.class, this::ExecuteStep)
				.onMessage(MasterContext.FinishSimulation.class, this::onFinish)
				.onMessage(MasterContext.StopSimulation.class, this::onStop)
				.build();
	}

	private Behavior<MasterContext> onStop(MasterContext.StopSimulation stopSimulation) {
		return null;
	}

	private Behavior<MasterContext> onInit(MasterContext.InitSimulation initSimulation) {
		return Behaviors.setup(context -> {
			var simEnv = sim.getEnvironment();
			var simAgents = sim.getAgents();
			System.out.println("start simulation");
			simEnv.init();
			for (var a: simAgents) {
				a.init(simEnv);
			}
			int t = sim.getInitialTime();
			int dt = sim.getTimeStep();
			sim.notifyReset(t, simAgents, simEnv);
			for (int i = 0; i < simAgents.size(); i++) {
				ActorRef<WorkerContext> child = context.spawn(WorkerAgent.create(simAgents.get(i),dt), "child" + i);
				childrens.add(child);
			}
			ActorRef<MasterContext> self = getContext().getSelf();
			for (var children : childrens) {
				children.tell(new WorkerContext.DoStep(self));
			}

			return Behaviors.same();

		});
	}

	private Behavior<MasterContext> onFinish(MasterContext.FinishSimulation finishSimulation) {
		return null;
	}

	private Behavior<MasterContext> ExecuteStep(MasterContext.FinishStep finishStep) {
		return null;
	}


	public static Behavior<MasterContext> create(AbstractSimulation sim, int nWorker, int numSteps, Flag stopFlag, Semaphore done,boolean syncWithTime) {
		return Behaviors.setup(context -> new MasterAgent(context,sim,nWorker,numSteps,stopFlag,done,syncWithTime));
	}
}
