package commands;

import java.util.Calendar;

/**
 * Message sent by the elevator indicating it has arrived at a floor
 */
public class ElevatorArrivedMessage extends Command {
	
	private int elevatorID;
	private int atFloor;
	
	/**
	 * ElevatorArrivedMessage Constructor
	 * @param elevatorID elevator who originated the message
	 * @param floor arrived at floor
	 */
	public ElevatorArrivedMessage(int elevatorID, int floor) {
		super(Calendar.getInstance());
		this.elevatorID = elevatorID;
		this.atFloor = floor;
	}
	
	/**
	 * Get elevator ID from message
	 * @return int 
	 */
	public int getElevatorID() {
		return elevatorID;
	}
	
	/**
	 * Get floor from message
	 * @return int
	 */
	public int getFloor() {
		return atFloor;
	}
}
