package elevators;

public class Elevator implements Runnable {
	
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
	public void run() {
		System.out.println("Elevator has started");
		Command command;
		while((command = theScheduler.getCommand()) != null) {
			goToFloorForPickup(command);
		}
		System.out.println("Elevator done");
		
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
	 * This method is to simulate going to another floor after a passengar is in the elevator
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
