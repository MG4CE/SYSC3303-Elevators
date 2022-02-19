package main;

import elevators.Elevator;
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
    	Thread schedulerThread = new Thread(s);
    	Thread elevatorThread = new Thread(e);
    	s.setElevator(e);
    	//Run threads
    	schedulerThread.start();
    	elevatorThread.start();
    	//cheese
    	e.pushButton(5);
    	Thread.sleep(1000);
    	e.pushButton(2);
    }
}
