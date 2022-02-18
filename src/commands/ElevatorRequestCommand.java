package commands;

import java.util.Calendar;

public class ElevatorRequestCommand extends Command {
	
	private int floor;
	private int requestID; //used to match floor request with elevator request
	
	public ElevatorRequestCommand(Calendar time, int floor, int requestID) {
		super(time);
		this.floor = floor;
		this.requestID = requestID;
	}
	
	public int getFloor() {
		return floor;
	}
	
	public int getRequestID() {
		return requestID;
	}
}
