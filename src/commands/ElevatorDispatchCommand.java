package commands;

import java.util.Calendar;

/**
 * Command sent by the scheduler to command an elevator to go to and stop at a 
 * certain floor.
 */
public class ElevatorDispatchCommand extends Command {
	
	private int destFloor;
	private int requestId;
	
	/**
	 * Constructor
	 * @param floor destination floor
	 */
	public ElevatorDispatchCommand(int floor, int requestId) {
		super(Calendar.getInstance());
		this.destFloor = floor;
		this.requestId = requestId;
	}
	
	/**
	 * Get destination floor from message
	 * @return int
	 */
	public int getDestFloor() {
		return this.destFloor;
	}
	
	public int getRequestId() {
		return this.requestId;
	}
}
