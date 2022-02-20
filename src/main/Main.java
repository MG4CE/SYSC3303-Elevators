package main;

import commands.ExternalFloorBtnCommand;
import elevators.Direction;
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
    	
    	Thread.sleep(100);
    	ExternalFloorBtnCommand cmd = new ExternalFloorBtnCommand(2,Direction.UP,1);
    	s.schedulerPutCommand(cmd);
    	Thread.sleep(100);
    	e.pushButton(4);
    	
    	cmd = new ExternalFloorBtnCommand(2,Direction.UP,1);
    	s.schedulerPutCommand(cmd);
    	Thread.sleep(100);
    	e.pushButton(6);
    	Thread.sleep(100);
    	
    	cmd = new ExternalFloorBtnCommand(4,Direction.UP,1);
    	s.schedulerPutCommand(cmd);
    	Thread.sleep(100);
    	e.pushButton(1);
    	Thread.sleep(100);
    	
    	cmd = new ExternalFloorBtnCommand(6,Direction.UP,1);
    	s.schedulerPutCommand(cmd);
    	Thread.sleep(100);
    	e.pushButton(3);
    	//Thread.sleep(100);
    	//e.pushButton(4);
    	
    }
}
