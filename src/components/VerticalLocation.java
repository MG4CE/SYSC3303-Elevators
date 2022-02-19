package components;

/**
 * Holds information about vertical location, required for some synchronization
 */
public class VerticalLocation {
	private int location; //Distance from ground floor in meters
	
	/**
	 * Constructor
	 */
	public VerticalLocation() {
		this.location = 0;
	}
	
	/**
	 * Constructor allowing selection of starting location
	 * @param setLocation
	 */
	public VerticalLocation(int setLocation) {
		this.location = setLocation;
	}
	
	/**
	 * Increments location by a given amount
	 * @param increment 
	 */
	public void increment(int increment) {
		this.location += increment;
	}
	
	/**
	 * Get current location
	 * @return int
	 */
	public int getLocation() {
		return location;
	}
}
