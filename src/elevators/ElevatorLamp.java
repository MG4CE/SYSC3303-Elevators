package elevators;

public class ElevatorLamp {
	private int floor;
	private Boolean isOn;
	
	public ElevatorLamp(int floor) {
		this.floor = floor;
		this.isOn = false;
	}
	
	public Boolean isOn() {
		return isOn;
	}
	
	public int getFloor() {
		return floor;
	}
	
	public void turnOn() {
		this.isOn = true;
	}
	
	public void turnOff() {
		this.isOn = false;
	}
}
