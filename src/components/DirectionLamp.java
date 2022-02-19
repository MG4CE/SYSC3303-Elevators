package components;

import elevators.Direction;

/**
 * Class representation of direction lamp used by the elevators and each floor
 */
public class DirectionLamp {
	
	private Direction direction;
	private LightStatus lightStatus;
	
	/**
	 * Constructor
	 * @param direction selected direction to be represented by light
	 */
	public DirectionLamp(Direction direction) {
		this.direction = direction;
		this.lightStatus = LightStatus.OFF;
	}
	
	/**
	 * Get direction
	 * @return Direction
	 */
	public Direction getDirection() {
		return direction;
	}
	
	/**
	 * Get light status
	 * @return LightStatus
	 */
	public LightStatus getLightStatus() {
		return lightStatus;
	}
	
	/**
	 * Turns on direction light
	 */
	public void turnOnLight() {
		this.lightStatus = LightStatus.ON;
	}
	
	/**
	 * Turns off direction light
	 */
	public void turnOffLight() {
		this.lightStatus = LightStatus.OFF;
	}
}
