package pcd.part1.simengine_conc.GUI;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import pcd.part1.simengine_conc.*;
import pcd.part1.simengine_conc.message.ControllerContext;
import pcd.part1.simengine_conc.message.MasterContext;

public class SimulationActor extends AbstractBehavior<ControllerContext> {

	//controller MVC
	//convertibile in attore
	private Flag stopFlag;
	private ActorRef<MasterContext> master;
	private AbstractSimulation simulation;
	private SimulationGUIFrame gui;
	private RoadSimView view;
	private RoadSimStatistics stat;
	 
	public SimulationActor(ActorContext<ControllerContext> context, AbstractSimulation simulation) {
		super(context);
		this.simulation = simulation;
		this.stopFlag = new Flag();

	}

	public static Behavior<ControllerContext> create(AbstractSimulation simulation) {
		return Behaviors.setup(context -> new SimulationActor(context,simulation));
	}

	public void notifyStarted(int nSteps) {
		new Thread(() -> {

			simulation.setup();			
			view.display();
			stopFlag.reset();
			ActorRef<MasterContext> master = ActorSystem.create(MasterActor.create(simulation,nSteps,true),"car_simulation");
			master.tell(new MasterContext.InitSimulation());
			gui.reset();
			
		}).start();
	}
	
	public void notifyStopped() {
		stopFlag.set();
	}

	@Override
	public Receive<ControllerContext> createReceive() {
		return newReceiveBuilder()
				.onMessage(ControllerContext.InitSimulation.class, this::onInit)
				.onMessage(ControllerContext.StopSimulation.class, this::onStop)
				.build();
	}

	private Behavior<ControllerContext> onStop(ControllerContext.StopSimulation stopSimulation) {
		return Behaviors.setup(context -> {
			master.tell(new MasterContext.StopSimulation());
			return Behaviors.same();
		});
	}

	private Behavior<ControllerContext> onInit(ControllerContext.InitSimulation initSimulation) {
		return Behaviors.setup(context ->{
			this.gui = initSimulation.gui;
			view = new RoadSimView();
			stat = new RoadSimStatistics();
			simulation.addSimulationListener(stat);
			simulation.addSimulationListener(view);
			simulation.setup();
			view.display();
			stopFlag.reset();
			master = context.spawn(MasterActor.create(simulation,initSimulation.nStep,true),"car_simulation");
			master.tell(new MasterContext.InitSimulation());
			return Behaviors.same();
		});
	}

}
