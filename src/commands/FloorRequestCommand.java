package commands;

import java.util.Calendar;

import elevators.Direction;

public class FloorRequestCommand extends Command {
	
	private int floor;
	private Direction direction;
	private int requestID;
	
	public FloorRequestCommand(int floor, String direction, int requestID) {
		super(Calendar.getInstance());
		this.floor = floor;
		this.direction = stringToDirection(direction);
		this.requestID = requestID;
	}
	
	public int getFloor() {
		return floor;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public int getRequestID() {
		return requestID;
	}
}
