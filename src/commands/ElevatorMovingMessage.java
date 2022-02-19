package commands;

import java.util.Calendar;

import elevators.Direction;

/**
 * Message sent by the elevator indicating that it has started moving
 */
public class ElevatorMovingMessage extends Command {
	private int elevatorID;
	private int fromFloor;
	private Direction currDirection;
	/**
	 * ElevatorMovingMessage Constructor
	 * @param elevatorID elevator who originated the message
	 * @param fromFloor arrived at floor
	 */
	public ElevatorMovingMessage(int elevatorID, int fromFloor, Direction direction) {
		super(Calendar.getInstance());
		this.elevatorID = elevatorID;
		this.fromFloor = fromFloor;
		this.currDirection = direction;
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
	
	/**
	 * get current direction
	 * @return current direction of the motor
	 */
	public Direction getDirection() {
		return this.currDirection;
	}
}
