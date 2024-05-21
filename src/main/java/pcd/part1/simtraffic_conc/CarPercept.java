package pcd.part1.simtraffic_conc;

import pcd.part1.simengine_conc.*;

import java.util.Optional;

/**
 * 
 * Percept for Car Agents
 * 
 * - position on the road
 * - nearest car, if present (distance)
 * - nearest semaphore, if present (distance)
 * 
 */
public record CarPercept(double roadPos, Optional<AbstractCar> nearestCarInFront, Optional<TrafficLightInfo> nearestSem) implements Percept { }