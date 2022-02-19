package commands;

import java.util.Calendar;

public class ElevatorDispatchCommand extends Command {
	private int destFloor;
	
	public ElevatorDispatchCommand(int floor) {
		super(Calendar.getInstance());
		this.destFloor = floor;
	}
	
	public int getDestFloor() {
		return this.destFloor;
	}
	
}
