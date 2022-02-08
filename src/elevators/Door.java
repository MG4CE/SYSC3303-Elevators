package elevators;

public class Door {
	private Boolean isDoorOpen;
	
	public Door() {
		this.isDoorOpen = false;
	}
	
	public Boolean isDoorOpen() {
		return isDoorOpen;
	}
	
	public void open() {
		this.isDoorOpen = true;
	}
	
	public void close() {
		this.isDoorOpen = false;
	}
}
