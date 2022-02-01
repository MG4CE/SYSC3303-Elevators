package elevators;

import static java.util.Objects.isNull;

public class Elevator implements Runnable {
	
	private int floor; 
	private boolean finished;
	private final Scheduler theScheduler;
	
	
	/**
	 * Basic constructor
	 * @param theScheduler
	 */
	public Elevator(Scheduler theScheduler) {
		this.floor = 0;
		this.theScheduler = theScheduler;
		this.finished = false;
	}
	/**
	 * Constructor for tests
	 * @param floor
	 * @param scheduler
	 * @param finished
	 */
	public Elevator(int floor, Scheduler scheduler,  boolean finished) {
		this.floor = floor;
		this.theScheduler = scheduler;
		this.finished = finished;
	}
	
	
	@Override
	public void run() {
		
		while(!finished) {
			runElevator();
		}
		
	}
	
	/**
	 * Main function for the elevator to run with
	 */
	private void runElevator() {
			
		Command command = theScheduler.getCommand();
		if(!isNull(command)) {
			//
			goToFloor(command);
		}
	}
	
	/**
	 * Once a command is gotten, go to the next floor
	 * @param command
	 */
	private void goToFloor(Command command) {
		setFloor(command.getFloor());
		
		//This is to simulate the elevator moving
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.printf("I am Elevator and am going to floor %d and the time is %s",
														command.getFloor(), 
														command.getTimestamp());
		
	}
	
	public int getFloor() {
		return floor;
	}
	public void setFloor(int floor) {
		this.floor = floor;
	}
	

}
