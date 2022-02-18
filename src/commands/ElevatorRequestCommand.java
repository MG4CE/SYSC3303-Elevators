package commands;

import java.util.Calendar;

public class ElevatorRequestCommand extends Command {
	
	private int floor;
	private int requestID; //used to match floor request with elevator request
	
	public ElevatorRequestCommand(int floor, int requestID) {
		super(Calendar.getInstance());
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
