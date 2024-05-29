package pcd.part1.simengine_conc.cli;

import akka.actor.typed.ActorRef;
import akka.actor.typed.ActorSystem;
import pcd.part1.simengine_conc.Flag;
import pcd.part1.simengine_conc.MasterAgent;
import pcd.part1.simengine_conc.message.MasterContext;
import pcd.part1.simtraffic_conc_examples.*;

public class RunTrafficSimulationMassiveTest {

	public static void main(String[] args) {		

		int numCars = 50;
		int nSteps = 10;
		int nWorkers = Runtime.getRuntime().availableProcessors() + 1;

		var simulation = new TrafficSimulationSingleRoadMassiveNumberOfCars(numCars);
		simulation.configureNumWorkers(nWorkers);
		simulation.setup();


		/*RoadMassiveSimView view = new RoadMassiveSimView();
		view.display();
		simulation.addSimulationListener(view);
		 */

		
		log("Running the simulation: " + numCars + " cars, for " + nSteps + " steps ...");
		
		Flag stopFlag = new Flag();
		ActorRef<MasterContext> master = ActorSystem.create(MasterAgent.create(simulation,nSteps,true),"car_simulation");
		master.tell(new MasterContext.InitSimulation());

		long d = simulation.getSimulationDuration();
		log("Completed in " + d + " ms - average time per step: " + simulation.getAverageTimePerStep() + " ms");
	}
	
	private static void log(String msg) {
		System.out.println("[ SIMULATION ] " + msg);
	}
}
