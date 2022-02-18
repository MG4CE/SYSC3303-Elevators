package commands;

import java.util.Calendar;

import elevators.Direction;

/**
 * This class is designed to be encapsulate a elevator event
 * It holds 
 * 1. The floor the elevator needs to pick up
 * 2. The direction the elevator is going
 * 3. The time at which the floor button was pressed
 * 4. The floor the elevator needs to drop off
 *
 */
public class Command {
		
	private Calendar time;
	
	/**
	 * Basic constructor
	 * @param time when elevator button was pressed
	 * @param floor the floor the elevator button was pressed
	 * @param direction which direction the elevator is going
	 * @param selectedFloor the floor to drop off
	 * @throws NumberFormatException when the time is no properly formatted
	 * @throws IllegalArgumentException When there is no input file / bad format
	 */
	public Command(String time) throws NumberFormatException, IllegalArgumentException {
		this.time = stringToCalendar(time);
	}
	
	/**
	 * Constructor for testing
	 * @param time Calendar properly set up
	 * @param floor int the floor that called the elevator
	 * @param direction Direction the direction of the elevator
	 * @param selectedFloor the floor to go to
	 */
	public Command(Calendar time) {
		this.time = time;
	}
	
	/**
	 * Parse input time into Calendar object format
	 * @param time the initial string time
	 * @return the Calendar object with the correct time set
	 * @throws NumberFormatException
	 * @throws IllegalArgumentException
	 */
	private Calendar stringToCalendar(String time) throws NumberFormatException, IllegalArgumentException {
		String timeParts[] = time.split("[:.]");
		
		if (timeParts.length != 4) {
			throw new IllegalArgumentException("Time string is improperly formated");
		}
		
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.HOUR, Integer.parseInt(timeParts[0]));
		cal.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
		cal.set(Calendar.SECOND, Integer.parseInt(timeParts[2]));
		cal.set(Calendar.MILLISECOND, Integer.parseInt(timeParts[3]));
		return cal;
	}
	
	/**
	 * Setting the direction from string
	 * @param direction the direction string
	 * @return the enum for direction
	 */
	protected Direction stringToDirection(String direction) {
		direction = direction.toLowerCase();
		if (direction.equals("up")) {
			return Direction.UP;
		} else if (direction.equals("down"))  {
			return Direction.DOWN;
		}
		
		throw new IllegalArgumentException("Invalid direction string");
	}
	
	/**
	 * Converting Calendar into string to be printed
	 * @return string representation of the Calendar object
	 */
	public String getTimestamp() {
		return String.format("%d:%d:%d.%d", 
				time.get(Calendar.HOUR),
				time.get(Calendar.MINUTE),
				time.get(Calendar.SECOND), 
				time.get(Calendar.MILLISECOND));
	}
}
