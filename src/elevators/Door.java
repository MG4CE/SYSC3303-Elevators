package elevators;

/**
 * Elevator door
 */
public class Door {
	private Boolean isDoorOpen;
	
	/**
	 * Constructor
	 */
	public Door() {
		this.isDoorOpen = false;
	}
	
	/**
	 * Return true if the door is open
	 * @return Boolean
	 */
	public Boolean isDoorOpen() {
		return isDoorOpen;
	}
	
	/**
	 * Open door
	 */
	public void openDoor() {
		this.isDoorOpen = true;
	}
	
	/**
	 * Close door
	 */
	public void closeDoor() {
		this.isDoorOpen = false;
	}
}
