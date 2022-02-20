
package elevators;

import commands.InteriorElevatorBtnCommand;

/**
 * Represents a single elevator floor button
 */
public class ElevatorButton {
	
	private int floor;
	private int elevatorId;
	
	/**
	 * Constructor 
	 * @param floor assigned floor for button
	 */
	public ElevatorButton(int floor, int elevatorId) {
		this.floor = floor;
		this.elevatorId = elevatorId;
	}
	
	/**
	 * Get floor
	 * @return int
	 */
	public int getFloor() {
		return floor;
	}
	
	/**
	 * This will be called from FloorSubSystem to simulate an inside button press
	 * @return command to be sent to scheduler
	 */
	public InteriorElevatorBtnCommand pushButton() {
		return new InteriorElevatorBtnCommand(floor, elevatorId);
	}
}
