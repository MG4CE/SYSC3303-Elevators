package commands;

import java.util.Calendar;

/**
 * Message sent by the elevator indicating that it has passed a floor sensor
 */
public class ElevatorFloorSensorMessage extends Command {
	
	private int floor;
	private int elevatorID;
	
	/**
	 * ElevatorFloorSensorMessage Constructor
	 * @param floor requested floor
	 * @param elevatorID elevator who originated the message
	 */
	public ElevatorFloorSensorMessage(int floor, int elevatorID) {
		super(Calendar.getInstance());
		this.elevatorID = elevatorID;
		this.floor = floor;
	}
	
	/**
	 * Get floor from message
	 * @return int
	 */
	public int getFloor() {
		return floor;
	}
	
	/**
	 * Get elevator ID from message
	 * @return int 
	 */
	public int getElevatorID() {
		return elevatorID;
	}
}
