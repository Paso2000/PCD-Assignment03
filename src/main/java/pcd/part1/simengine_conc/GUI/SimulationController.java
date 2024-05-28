package pcd.part1.simengine_conc.GUI;

import pcd.part1.simengine_conc.*;

public class SimulationController {

	//controller MVC
	//convertibile in attore
	private Flag stopFlag;
	private AbstractSimulation simulation;
	private SimulationGUI gui;
	private RoadSimView view;
	private RoadSimStatistics stat;
	 
	public SimulationController(AbstractSimulation simulation) {
		this.simulation = simulation;
		this.stopFlag = new Flag();
	}
	
	public void attach(SimulationGUI gui) {
		this.gui = gui;		
		view = new RoadSimView();
		stat = new RoadSimStatistics();
		simulation.addSimulationListener(stat);
		simulation.addSimulationListener(view);		
		gui.setController(this);
	}

	public void notifyStarted(int nSteps) {
		new Thread(() -> {
			simulation.setup();			
			view.display();
		
			stopFlag.reset();
			simulation.run(nSteps, stopFlag, true);
			gui.reset();
			
		}).start();
	}
	
	public void notifyStopped() {
		stopFlag.set();
	}

}
