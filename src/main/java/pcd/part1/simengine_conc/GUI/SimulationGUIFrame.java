package pcd.part1.simengine_conc.GUI;

import akka.actor.typed.ActorRef;
import pcd.part1.simengine_conc.message.ControllerContext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SimulationGUIFrame extends JFrame implements ActionListener {

	private JButton start;
	private JButton stop;
	private JTextField nSteps;

	private ActorRef<ControllerContext> actorConttroller;
	
	private SimulationController controller;
	
	public SimulationGUIFrame(int initialValue, ActorRef<ControllerContext> controller){
		this.actorConttroller =controller;
		setTitle("Simulation Dashboard");
		setSize(300,100);		
		nSteps = new JTextField(5);
		nSteps.setText(""+ initialValue);
		start = new JButton("start");
		stop  = new JButton("stop");
		stop.setEnabled(false);
		
		Container cp = getContentPane();
		JPanel panel = new JPanel();
		
		Box p0 = new Box(BoxLayout.X_AXIS);
		p0.add(new JLabel("Num Steps: "));
		p0.add(nSteps);
		Box p1 = new Box(BoxLayout.X_AXIS);
		p1.add(start);
		p1.add(stop);
		Box p2 = new Box(BoxLayout.Y_AXIS);
		p2.add(p0);
		p2.add(Box.createVerticalStrut(10));
		p2.add(p1);
		
		panel.add(p2);
		cp.add(panel);
		
		addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent ev){
				System.exit(-1);
			}
			public void windowClosed(WindowEvent ev){
				System.exit(-1);
			}
		});

		start.addActionListener(this);
		stop.addActionListener(this);
	}
	
	public void setController(SimulationController contr) {
		this.controller = contr;
	}
	
	public void actionPerformed(ActionEvent ev){
		Object src = ev.getSource();
		if (src==start){	
			try {
				int nSt = Integer.parseInt(nSteps.getText());
				start.setEnabled(false);
				stop.setEnabled(true);
				System.out.println("ciao");
				//controller.notifyStarted(nSt);
				actorConttroller.tell(new ControllerContext.InitSimulation(nSt,this));
			} catch (Exception ex) {}
		} else if (src == stop){
			start.setEnabled(true);
			stop.setEnabled(false);
			actorConttroller.tell(new ControllerContext.StopSimulation());
		}
	}
	
	public void reset() {
		SwingUtilities.invokeLater(()-> {
			start.setEnabled(true);
			stop.setEnabled(false);
		});
	}


	public void display() {
        SwingUtilities.invokeLater(() -> {
        	this.setVisible(true);
        });
    }
	
	
}
