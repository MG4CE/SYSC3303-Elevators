package commands;

import java.util.Calendar;
import components.LightStatus;

import elevators.Direction;

public class FloorDirectionMessage extends Command {
	private Direction direction;
	private int floorNum;
	private LightStatus lightStatus;
	
	public FloorDirectionMessage(Direction direction, int floorNum, LightStatus lightStatus) {
		super(Calendar.getInstance());
		this.direction = direction;
		this.floorNum = floorNum;
		this.lightStatus = lightStatus;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public int getFloor() {
		return floorNum;
	}
	
	public LightStatus getLightStatus() {
		return lightStatus;
	}
}
