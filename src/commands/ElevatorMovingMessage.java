package commands;

import java.util.Calendar;

/**
 * Message sent by the elevator indicating that it has started moving
 */
public class ElevatorMovingMessage extends Command {
	private int elevatorID;
	private int fromFloor;
	
	/**
	 * ElevatorMovingMessage Constructor
	 * @param elevatorID elevator who originated the message
	 * @param floor arrived at floor
	 */
	public ElevatorMovingMessage(int elevatorID, int fromFloor) {
		super(Calendar.getInstance());
		this.elevatorID = elevatorID;
		this.fromFloor = fromFloor;
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
		return fromFloor;
	}
}
