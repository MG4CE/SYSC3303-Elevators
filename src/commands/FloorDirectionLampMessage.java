package commands;

import java.util.Calendar;
import components.LightStatus;

import elevators.Direction;

/**
 * Message sent to the floor commanding the direction lights
 */
public class FloorDirectionLampMessage extends Command {
	
	private Direction direction;
	private int floorNum;
	private LightStatus lightStatus;
	
	/**
	 * FloorDirectionLampMessage Constructor
	 * @param direction which direction light to command
	 * @param floorNum floor
	 * @param lightStatus indicate light status
	 */
	public FloorDirectionLampMessage(Direction direction, int floorNum, LightStatus lightStatus) {
		super(Calendar.getInstance());
		this.direction = direction;
		this.floorNum = floorNum;
		this.lightStatus = lightStatus;
	}
	
	/**
	 * Get direction from message
	 * @return Direction
	 */
	public Direction getDirection() {
		return direction;
	}
	
	/**
	 * Get floor from message
	 * @return int
	 */
	public int getFloor() {
		return floorNum;
	}
	
	/**
	 * Get light status from message
	 * @return LightStatus
	 */
	public LightStatus getLightStatus() {
		return lightStatus;
	}
}
