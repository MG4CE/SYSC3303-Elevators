package main;

import elevators.Elevator;
import floors.FloorSubsystem;
import scheduler.Scheduler;

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
    	Thread floorSubsystemThread = new Thread(f);
    	//Run threads
    	schedulerThread.start();
    	elevatorThread.start();
    	floorSubsystemThread.start();    	
    	
    	e.pushButton(5);
    	
    }
}
