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

public class SimulationController extends AbstractBehavior<ControllerContext> {

	//controller MVC
	//convertibile in attore
	private Flag stopFlag;
	private ActorRef<MasterContext> master;
	private AbstractSimulation simulation;
	private RoadSimView view;
	private RoadSimStatistics stat;
	 
	public SimulationController(ActorContext<ControllerContext> context, AbstractSimulation simulation) {
		super(context);
		this.simulation = simulation;
		this.stopFlag = new Flag();

	}

	public static Behavior<ControllerContext> create(AbstractSimulation simulation) {
		return Behaviors.setup(context -> new SimulationController(context,simulation));
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
			view = new RoadSimView();
			stat = new RoadSimStatistics();
			simulation.addSimulationListener(stat);
			simulation.addSimulationListener(view);
			simulation.setup();
			view.display();
			stopFlag.reset();
			master = context.spawn(MasterAgent.create(simulation,initSimulation.nStep,true),"car_simulation");
			master.tell(new MasterContext.InitSimulation());
			return Behaviors.same();
		});
	}

}
