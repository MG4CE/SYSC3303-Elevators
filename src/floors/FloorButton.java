package floors;

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
	
   /* this should create and return a command that needs to be sent to the 
	* scheduler in the future
	* public Command pressButton() {
	* 	
	* }
	*/
}