package elevators;

import static java.util.Objects.isNull;

public class Elevator implements Runnable {
	
	private int floor; 
	private volatile static boolean finished;
	private final Scheduler theScheduler;
	
	
	/**
	 * Basic constructor
	 * @param theScheduler
	 */
	public Elevator(Scheduler scheduler) {
		this.floor = 0;
		this.theScheduler = scheduler;
		Elevator.finished = false;
	}
	/**
	 * Constructor for tests
	 * @param floor
	 * @param scheduler
	 * @param finished
	 */
	public Elevator(Scheduler scheduler, int floor, boolean finished) {
		this.floor = floor;
		this.theScheduler = scheduler;
		Elevator.finished = finished;
	}
	
	
	@Override
	public void run() {
		
		while(!Elevator.finished) {
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
			goToFloorForPickup(command);
		}
	}
	
	/**
	 * Once a command is gotten, go to the next floor
	 * @param command
	 */
	private void goToFloorForPickup(Command command) {
		
		System.out.printf("Floor %d for pickup",command.getFloor());
		if(this.getFloor() != command.getFloor()) {
		//This is to simulate the elevator moving
			try {
				setFloor(command.getFloor());
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		goToFloorFromFloorSelected(command);
		

		System.out.printf(" Floor %d for dropoff the time is %s\n",command.getSelectedFloor(),command.getTimestamp());

		
	}
	
	private void goToFloorFromFloorSelected(Command command) {
		
		if(this.getFloor() != command.getSelectedFloor()) {
		//This is to simulate the elevator moving
			try {
				setFloor(command.getSelectedFloor());
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int getFloor() {
		return floor;
	}
	public void setFloor(int floor) {
		this.floor = floor;
	}
	
	public static void elevatorFinished() {
		Elevator.finished = true;
	}
	

}
