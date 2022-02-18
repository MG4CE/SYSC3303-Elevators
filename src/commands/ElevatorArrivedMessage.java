package commands;

import java.util.Calendar;

public class ElevatorArrivedMessage extends Command {
	
	private int elevatorID;
	private int atFloor;
	
	public ElevatorArrivedMessage(int elevatorID, int floor) {
		super(Calendar.getInstance());
		this.elevatorID = elevatorID;
		this.atFloor = floor;
	}
	
	public int getElevatorID() {
		return elevatorID;
	}
	
	public int getFloor() {
		return atFloor;
	}
}
