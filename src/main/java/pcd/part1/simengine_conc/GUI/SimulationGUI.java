package pcd.part1.simengine_conc.GUI;

import akka.actor.typed.ActorRef;
import pcd.part1.simengine_conc.message.ControllerContext;

/**
 * 
 * View designed as a monitor.
 * 
 * @author aricci
 *
 */
public class SimulationGUI {

	//view di MVC
	private SimulationGUIFrame gui;
	
	public SimulationGUI(int initialValue, ActorRef<ControllerContext> controller){
		gui = new SimulationGUIFrame(initialValue, controller);
	}
	
	public void setController(SimulationController contr) {
		gui.setController(contr);
	}

	public  void display() {
		gui.display();
    }
	
	public void reset() {
		gui.reset();
	}
}
