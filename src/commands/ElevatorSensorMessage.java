package commands;

import java.util.Calendar;

/**
 * Message sent by the elevator indicating that it has passed a floor sensor
 */
public class ElevatorSensorMessage extends Command {
	
	private int floor;
	
	/**
	 * ElevatorSensorMessage Constructor
	 * @param floor requested floor
	 * @param requestID unique ID assigned to the message
	 */
	public ElevatorSensorMessage(int floor) {
		super(Calendar.getInstance());
		this.floor = floor;
	}
	
	/**
	 * Get floor from message
	 * @return int
	 */
	public int getFloor() {
		return floor;
	}
}
