package elevators;

import java.util.Calendar;

public class Message {
	private Calendar time;
	
	public Message() {
		this.time = Calendar.getInstance();
	}
	
	/**
	 * Getter for time
	 * @return the time as a Calendar object
	 */
	public Calendar getTime() {
		return time;
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
