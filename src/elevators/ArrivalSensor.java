package elevators;

public class ArrivalSensor {
	private static final int DISTANCE_BETWEEN_FLOORS_METERS = 4;
	private int floorLocation;
	private int floor;
	
	public ArrivalSensor(int floor) {
		this.floor = floor;
		this.floorLocation = DISTANCE_BETWEEN_FLOORS_METERS * this.floor;
	}
	
	public int getFloor() {
		return floor;
	}
	
	public int getLocation() {
		return floorLocation;
	}
}
