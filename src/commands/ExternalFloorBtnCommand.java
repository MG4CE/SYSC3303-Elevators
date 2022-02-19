package commands;

import java.util.Calendar;

import elevators.Direction;

/**
 * Command sent by the floor indicating that a button has been pressed
 */
public class ExternalFloorBtnCommand extends Command {
	
	private int floor;
	private Direction direction;
	private int requestID;
	
	/**
	 * FloorRequestCommand Constructor
	 * @param floor the floor that created the request
	 * @param direction the direction pressed
	 * @param requestID unique ID assigned to the message
	 */
	public ExternalFloorBtnCommand(int floor, Direction direction, int requestID) {
		super(Calendar.getInstance());
		this.floor = floor;
		this.direction = direction;
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
	 * Get direction from message
	 * @return Direction
	 */
	public Direction getDirection() {
		return direction;
	}
	
	/**
	 * Get request ID from message
	 * @return int
	 */
	public int getRequestID() {
		return requestID;
	}
}
