package elevators;

import java.util.Calendar;

public class ElevatorMovingMessage extends Command {
	private int elevatorID;
	private int fromFloor;
	
	public ElevatorMovingMessage(int elevatorID, int fromFloor) {
		super(Calendar.getInstance());
		this.elevatorID = elevatorID;
		this.fromFloor = fromFloor;
	}
	
	public int getElevatorID() {
		return elevatorID;
	}
	
	public int getFloor() {
		return fromFloor;
	}
}
