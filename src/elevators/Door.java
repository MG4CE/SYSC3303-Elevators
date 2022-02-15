package elevators;

public class Door {
	private Boolean isDoorOpen;
	
	public Door() {
		this.isDoorOpen = false;
	}
	
	public Boolean isDoorOpen() {
		return isDoorOpen;
	}
	
	public void openDoor() {
		this.isDoorOpen = true;
	}
	
	public void closeDoor() {
		this.isDoorOpen = false;
	}
}
