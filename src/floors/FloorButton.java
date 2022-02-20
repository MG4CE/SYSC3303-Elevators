package floors;

import commands.ExternalFloorBtnCommand;
import elevators.Direction;

/**
 * Represents a single floor button
 */
public class FloorButton {
	private int floor;
	private Direction direction;
	
	/**
	 * Constructor 
	 * @param floor assigned floor for button
	 */
	public FloorButton(int floor, Direction direction) {
		this.direction = direction;
		this.floor = floor;
	}
	
	/**
	 * Get floor
	 * @return int
	 */
	public int getFloor() {
		return floor;
	}
	
	/**
	 * Get direction
	 * @return Direction
	 */
	public Direction getDirection() {
		return direction;
	}
	
	/**
	 * Simulate a button press
	 * @return command to be sent to scheduler
	 */
	public ExternalFloorBtnCommand pushButton() {
		return new ExternalFloorBtnCommand(floor, direction, 1);
	}
}