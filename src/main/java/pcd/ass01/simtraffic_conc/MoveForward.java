package part1.simtraffic_conc;

import pcd.ass01.simengine_conc.*;

import java.util.Optional;

/**
 * Car agent move forward action
 */
public record MoveForward(String agentId, double distance) implements Action {
	
	public void exec(AbstractEnvironment env) {
		AbstractCar car = (AbstractCar) env.getAgent(agentId);
		Road road = car.getRoad();
		Optional<AbstractCar> nearestCar = 
				((RoadsEnv) env).getNearestCarInFront(road, car.getPos(), RoadsEnv.CAR_DETECTION_RANGE);
		
		if (!nearestCar.isEmpty()) {
			double dist = nearestCar.get().getPos() - car.getPos();
			if (dist > this.distance() + RoadsEnv.MIN_DIST_ALLOWED) {
				car.updatePos(car.getPos() + this.distance());
			}
		} else {
			car.updatePos(car.getPos() + this.distance());
		}

		if (car.getPos() > road.getLen()) {
			car.updatePos(0);
		}
	}
}
