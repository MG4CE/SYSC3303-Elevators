package elevators;

import java.util.Calendar;

public class Command {
	
	public enum Direction {
		UP,
		DOWN;
	}
	
	private Calendar time;
	private int floor;
	private Direction direction;
	private int selectedFloor;
	
	public Command(String time, int floor, String direction, int selectedFloor) throws NumberFormatException, IllegalArgumentException {
		this.time = stringToCalendar(time);
		this.floor = floor;
		this.direction = stringToDirection(direction);
		this.selectedFloor = selectedFloor;
	}
	
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
	
	private Direction stringToDirection(String direction) {
		if (direction.equals("Up")) {
			return Direction.UP;
		} else if (direction.equals("Down"))  {
			return Direction.DOWN;
		}
		
		throw new IllegalArgumentException("Invalid direction string");
	}
	
	public Calendar getTime() {
		return time;
	}
	
	public String getTimestamp() {
		return String.format("%i:%i:%i.%i", 
				time.get(Calendar.HOUR),
				time.get(Calendar.MINUTE),
				time.get(Calendar.SECOND),
				time.get(Calendar.MILLISECOND));
	}
	
	public int getFloor() {
		return floor;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public int getSelectedFloor() {
		return selectedFloor;
	}
}
