package elevators;

public class FloorButtonLamp {
	private int floor;
	private LightStatus lightStatus;
	
	public FloorButtonLamp(int floor) {
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
