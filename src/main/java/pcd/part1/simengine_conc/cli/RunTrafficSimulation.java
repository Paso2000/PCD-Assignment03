package pcd.part1.simengine_conc.cli;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import pcd.part1.simengine_conc.*;
import pcd.part1.simengine_conc.message.MasterContext;
import pcd.part1.simtraffic_conc_examples.*;

/**
 * 
 * Main class to create and run a simulation - CLI
 * 
 */
public class RunTrafficSimulation {

	private static final int DEFAULT_STEPS = 1000;

	public static void main(String[] args) {		

		int nWorkers = Runtime.getRuntime().availableProcessors() + 1;
		
		 //var simulation = new TrafficSimulationSingleRoadTwoCars();
		 //var simulation = new TrafficSimulationSingleRoadSeveralCars();
		 var simulation = new TrafficSimulationSingleRoadWithTrafficLightTwoCars();

		//var simulation = new TrafficSimulationWithCrossRoads();
		simulation.configureNumWorkers(nWorkers);
		simulation.setup();
		
		RoadSimStatistics stat = new RoadSimStatistics();
		RoadSimView view = new RoadSimView();
		view.display();
		
		simulation.addSimulationListener(stat);
		simulation.addSimulationListener(view);		
		
		Flag stopFlag = new Flag();
		ActorRef<MasterContext> master = ActorSystem.create(MasterAgent.create(simulation,DEFAULT_STEPS,true),"car_simulation");
		master.tell(new MasterContext.InitSimulation());
	}
}
