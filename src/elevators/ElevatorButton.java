
package elevators;

/**
 * Represents a single elevator floor button
 */
public class ElevatorButton {
	
	private int floor;
	
	/**
	 * Constructor 
	 * @param floor assigned floor for button
	 */
	public ElevatorButton(int floor) {
		this.floor = floor;
	}
	
	/**
	 * Get floor
	 * @return int
	 */
	public int getFloor() {
		return floor;
	}
	
   /* this should create and return a command that needs to be sent to the 
	* scheduler in the future
	* public Command pressButton() {
	* 	
	* }
	*/
}
