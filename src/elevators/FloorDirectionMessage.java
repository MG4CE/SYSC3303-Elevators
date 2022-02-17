package elevators;

import java.util.Calendar;

public class FloorDirectionMessage extends Command {
	private Direction direction;
	private int floorNum;
	
	public FloorDirectionMessage(Direction direction, int floorNum) {
		super(Calendar.getInstance());
		this.direction = direction;
		this.floorNum = floorNum;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public int getFloor() {
		return floorNum;
	}
}
