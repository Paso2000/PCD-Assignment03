package pcd.part1.simengine_conc.GUI;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import pcd.part1.simengine_conc.MasterAgent;
import pcd.part1.simengine_conc.message.ControllerContext;
import pcd.part1.simengine_conc.message.MasterContext;
import pcd.part1.simtraffic_conc_examples.*;

/**
 * 
 * Main class to create and run a simulation - with GUI
 * 
 */
public class RunTrafficSimulation {

	private static final int DEFAULT_STEPS = 10000;
	
	public static void main(String[] args) {		

		int nWorkers = Runtime.getRuntime().availableProcessors() + 1;

		// var simulation = new TrafficSimulationSingleRoadTwoCars();
		// var simulation = new TrafficSimulationSingleRoadSeveralCars();
		// var simulation = new TrafficSimulationSingleRoadWithTrafficLightTwoCars();
		
		var simulation = new TrafficSimulationWithCrossRoads();
		
		simulation.configureNumWorkers(nWorkers);

		ActorRef<ControllerContext> controller = ActorSystem.create(SimulationController.create(simulation),"controller");
		SimulationGUI gui = new SimulationGUI(DEFAULT_STEPS,controller);
        gui.display();
	}
}
