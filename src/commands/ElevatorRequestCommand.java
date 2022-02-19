package commands;

import java.util.Calendar;

/**
 * Command sent by the elevator indicating that a button has been pressed
 */
public class ElevatorRequestCommand extends Command {
	
	private int floor;
	private int requestID; //used to match floor request with elevator request
	

	/**
	 * ElevatorRequestCommand Constructor
	 * @param floor requested floor
	 * @param requestID unique ID assigned to the message
	 */
	public ElevatorRequestCommand(int floor, int requestID) {
		super(Calendar.getInstance());
		this.floor = floor;
		this.requestID = requestID;
	}
	
	/**
	 * Get floor from message
	 * @return int
	 */
	public int getFloor() {
		return floor;
	}
	
	/**
	 * Get request ID from message
	 * @return int 
	 */
	public int getRequestID() {
		return requestID;
	}
}
