package main;

import commands.ExternalFloorBtnCommand;
import elevators.Direction;
import elevators.Elevator;
import scheduler.Scheduler;
import floors.FloorSubsystem;

public class Main {
	/**
	 * Main method to be ran
	 * @param args
	 * @throws InterruptedException
	 */
    public static void main(String[] args) throws InterruptedException {
    	Scheduler s = new Scheduler();
    	Elevator e = new Elevator(s, 0);
    	FloorSubsystem f = new FloorSubsystem(s, "data/input.txt");
    	Thread schedulerThread = new Thread(s);
    	Thread elevatorThread = new Thread(e);
    	Thread floorSubSystem = new Thread(f);
    	s.setElevator(e);
    	s.setFloorSubSystem(f);
    	
    	//Run threads
    	schedulerThread.start();
    	elevatorThread.start();
    	floorSubSystem.start();   	

    }
}
