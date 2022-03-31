package utils;

import java.util.Calendar;

import message.Direction;

/**
 * Class holding static utility methods used through out the system
 */
public class Utils {
	/**
	 * Parse input time into Calendar object format
	 * 
	 * @param time the initial string time
	 * @return the Calendar object with the correct time set
	 * @throws NumberFormatException if time string contains non digits
	 * @throws IllegalArgumentException bad string format
	 */
	public static Calendar stringToCalendar(String time) throws NumberFormatException, IllegalArgumentException {
		String timeParts[] = time.split("[:.]");
		
		if (timeParts.length != 4) {
			throw new IllegalArgumentException("Time string is improperly formated");
		}
		
		Calendar cal = Calendar.getInstance();
		int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		cal.set(Calendar.HOUR, Integer.parseInt(timeParts[0]));
		cal.set(Calendar.MINUTE, Integer.parseInt(timeParts[1]));
		cal.set(Calendar.SECOND, Integer.parseInt(timeParts[2]));
		cal.set(Calendar.MILLISECOND, Integer.parseInt(timeParts[3])*100);
		cal.set(Calendar.DAY_OF_WEEK,dayOfWeek);
		cal.getTime();
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		return cal;
	}
	
	/**
	 * Setting the direction from string
	 * 
	 * @param direction string
	 * @return Direction
	 */
	public static Direction stringToDirection(String direction) {
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
	 * 
	 * @return string representation of the Calendar object
	 */
	public static String getTimeStamp(Calendar time) {
		return String.format("%d:%d:%d.%d", 
				time.get(Calendar.HOUR),
				time.get(Calendar.MINUTE),
				time.get(Calendar.SECOND), 
				time.get(Calendar.MILLISECOND));
	}
}
