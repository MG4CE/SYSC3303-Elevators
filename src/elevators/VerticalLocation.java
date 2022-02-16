package elevators;

public class VerticalLocation {
	private int location; //Distance from ground floor in meters
	
	public VerticalLocation() {
		this.location = 0;
	}
	
	public VerticalLocation(int setLocation) {
		this.location = setLocation;
	}
	
	public void increment(int increment) {
		this.location += increment;
	}
	
	public int getLocation() {
		return location;
	}
}
