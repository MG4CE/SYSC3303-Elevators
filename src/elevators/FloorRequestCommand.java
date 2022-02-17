package elevators;

public class FloorRequestCommand extends Command {
	
	private int floor;
	private Direction direction;
	private int requestID;
	
	public FloorRequestCommand(String time, int floor, String direction, int requestID) {
		super(time);
		this.floor = floor;
		this.direction = stringToDirection(direction);
		this.requestID = requestID;
	}
	
	public int getFloor() {
		return floor;
	}
	
	public Direction getDirection() {
		return direction;
	}
	
	public int getRequestID() {
		return requestID;
	}
}
