package elevators;

import java.util.Calendar;

public class ElevatorSensorMessage extends Command {
	
	private int floor;
	
	public ElevatorSensorMessage(int floor) {
		super(Calendar.getInstance());
		this.floor = floor;
	}
	
	public int getFloor() {
		return floor;
	}
}
