package commands;

import java.util.Calendar;

/**
 * Command sent by the scheduler to command an elevator to go to and stop at a 
 * certain floor.
 */
public class ElevatorDispatchCommand extends Command {
	
	private int destFloor;
	
	/**
	 * Constructor
	 * @param floor destination floor
	 */
	public ElevatorDispatchCommand(int floor) {
		super(Calendar.getInstance());
		this.destFloor = floor;
	}
	
	/**
	 * Get destination floor from message
	 * @return int
	 */
	public int getDestFloor() {
		return this.destFloor;
	}
}
