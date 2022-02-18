package elevators;

import commands.Command;

/**
 * This class is to represent an Elevator which will do Three things
 * 1. Read commands from the Scheduler Thread
 * 2. Read and go to the floor which has called the elevator
 * 3. Read and go to the floor that was selected in the elevator
 *
 */
public class Elevator implements Runnable {
	
	//Instance Variables
	private int floor; 
	private final Scheduler theScheduler;
	
	/**
	 * Basic constructor 
	 * @param theScheduler the scheduler shared resource
	 */
	public Elevator(Scheduler scheduler) {
		this.floor = 0;
		this.theScheduler = scheduler;
	}
	/**
	 * Constructor for tests
	 * @param floor
	 * @param scheduler
	 */
	public Elevator(Scheduler scheduler, int floor) {
		this.floor = floor;
		this.theScheduler = scheduler;
	}
	
	
	@Override
	/**
	 * Overriden run command to run the ELevators main loop
	 */
	public void run() {
		Command command;
		while(true) {
			command = theScheduler.getCommand();
			
			//This is the from the stop command
			if (command.getFloor() == -1) {
				break;
			}
			goToFloorForPickup(command);
		}
		System.out.println("Elevator terminated");
		
	}
	
	
	/**
	 * Once a command had been received, go to the next floor
	 * @param command the event object
	 */
	private void goToFloorForPickup(Command command) {
		
		System.out.printf("Elevator at Floor %d going to Floor %d for pickup\n",this.getFloor(),command.getFloor());
		if(this.getFloor() != command.getFloor()) {
		//This is to simulate the elevator moving
			try {
				setFloor(command.getFloor());
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.printf("Elevator is at Floor %d going to Floor %d for dropoff at time - %s\n\n",this.getFloor(),command.getSelectedFloor(),command.getTimestamp());
		goToFloorFromFloorSelected(command);
		
	}
	
	/**
	 * This method is to simulate going to another floor after a passenger is in the elevator
	 * @param command the event object
	 */
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
	
	/**
	 * Getter for floor
	 * @return int the current floor of the elevator
	 */
	public int getFloor() {
		return floor;
	}
	/**
	 * Setter for floor
	 * @param floor the floor the elevator is going to
	 */
	public void setFloor(int floor) {
		this.floor = floor;
	}

}
