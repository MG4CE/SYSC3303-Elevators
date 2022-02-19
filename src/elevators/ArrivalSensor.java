package elevators;

/**
 * Sensors on a floor used by the elevator to determine its location
 */
public class ArrivalSensor {
	
	private static final int DISTANCE_BETWEEN_FLOORS_METERS = 4;
	private int floorLocation;
	private int floor;
	
	/**
	 * Constructor
	 * @param floor assigned floor for sensor
	 */
	public ArrivalSensor(int floor) {
		this.floor = floor;
		this.floorLocation = DISTANCE_BETWEEN_FLOORS_METERS * this.floor;
	}
	
	/**
	 * Get floor
	 * @return int
	 */
	public int getFloor() {
		return floor;
	}
	
	/**
	 * Get location
	 * @return int
	 */
	public int getLocation() {
		return floorLocation;
	}
}
