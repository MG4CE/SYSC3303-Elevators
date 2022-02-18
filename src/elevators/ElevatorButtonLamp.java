package elevators;

import components.LightStatus;

public class ElevatorButtonLamp {
	private int floor;
	private LightStatus lightStatus;
	
	public ElevatorButtonLamp(int floor) {
		this.floor = floor;
		this.lightStatus = LightStatus.OFF;
	}
	
	public LightStatus getLightStatus() {
		return lightStatus;
	}
	
	public int getFloor() {
		return floor;
	}
	
	public void turnOn() {
		this.lightStatus = LightStatus.ON;
	}
	
	public void turnOff() {
		this.lightStatus = LightStatus.OFF;
	}
}
