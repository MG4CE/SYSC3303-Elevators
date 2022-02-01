package elevators;

import java.util.Calendar;

public class Command {
	
	enum Direction {
		UP,
		DOWN;
	}
	
	private Calendar time;
	private int floor;
	private Direction direction;
	private int elevatorID;
	
	public Command(Calendar time, int floor, Direction direction, int elevatorID) {
		this.time = time;
		this.floor = floor;
		this.direction = direction;
		this.elevatorID = elevatorID;
	}
	
	public String getTimestamp() {
		return String.format("%i:%i:%i:%i", 
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
	
	public int getElevatorID() {
		return elevatorID;
	}
}
