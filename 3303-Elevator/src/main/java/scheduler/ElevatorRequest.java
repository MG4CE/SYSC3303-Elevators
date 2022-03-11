package scheduler;

import elevatorCommands.Direction;

public class ElevatorRequest implements Comparable<ElevatorRequest> {
	private int destinationFloor;
	private int requestID;
	private Direction direction;
	
	public ElevatorRequest(int destinationFloor, int requestID) {
		this.destinationFloor = destinationFloor;
		this.direction = null;
		this.requestID = requestID;
	}
	
	public ElevatorRequest(int destinationFloor, int requestID, Direction direction) {
		this.destinationFloor = destinationFloor;
		this.direction = direction;
		this.requestID = requestID;
	}
	
	public int getFloor() {
		return this.destinationFloor;
	}
	
	public int getRequestID() {
		return this.requestID;
	}

	public Direction getDirection() {
		return this.direction;
	}
	
	@Override
	public int compareTo(ElevatorRequest o) {
		return this.destinationFloor - o.destinationFloor;
	}
}
