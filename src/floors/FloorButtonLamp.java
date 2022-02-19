package floors;

import components.LightStatus;

/**
 * Represents a floor button lamp  
 */
public class FloorButtonLamp {
	private int floor;
	private LightStatus lightStatus;
	
	/**
	 * Constructor 
	 * @param floor assigned floor for button lamp
	 */
	public FloorButtonLamp(int floor) {
		this.floor = floor;
		this.lightStatus = LightStatus.OFF;
	}
	
	/**
	 * Return light status of lamp
	 * @return LightStatus
	 */
	public LightStatus getLightStatus() {
		return lightStatus;
	}
	
	/**
	 * Get floor
	 * @return int
	 */
	public int getFloor() {
		return floor;
	}
	
	/**
	 * Turn button lamp on
	 */
	public void turnOn() {
		this.lightStatus = LightStatus.ON;
	}
	
	/**
	 * Turn button lamp off
	 */
	public void turnOff() {
		this.lightStatus = LightStatus.OFF;
	}
}
