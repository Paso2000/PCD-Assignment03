package pcd.part1.simengine_conc.message;

import pcd.part1.simengine_conc.GUI.SimulationGUI;
import pcd.part1.simengine_conc.GUI.SimulationGUIFrame;

public abstract class ControllerContext {
    public static final class InitSimulation extends ControllerContext {
        public int nStep;
        public SimulationGUIFrame gui;
        public InitSimulation(int nStep, SimulationGUIFrame gui){
            this.nStep=nStep;
            this.gui=gui;
        }

    }
    public static final class StopSimulation extends ControllerContext {

    }
}
