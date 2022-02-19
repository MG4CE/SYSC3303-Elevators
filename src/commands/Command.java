package commands;

import java.util.Calendar;
import elevators.Direction;

/**
 * Base command that only holds time
 */
public class Command {
		
	private Calendar time;
	
	/**
	 * Basic constructor (legacy)
	 * @param time custom time string
	 * @throws NumberFormatException if time string contains non digits
	 * @throws IllegalArgumentException bad string format
	 */
	public Command(String time) throws NumberFormatException, IllegalArgumentException {
		this.time = stringToCalendar(time);
	}
	
	/**
	 * Constructor
	 * @param time Calendar time object
	 */
	public Command(Calendar time) {
		this.time = time;
	}
	
	/**
	 * Parse input time into Calendar object format
	 * @param time the initial string time
	 * @return the Calendar object with the correct time set
	 * @throws NumberFormatException if time string contains non digits
	 * @throws IllegalArgumentException bad string format
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
	 * @param direction string
	 * @return Direction
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
