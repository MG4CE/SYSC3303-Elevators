package scheduler;

import elevatorCommands.Direction;

/**
 * The ElevatorRequest class if used to facilitate requests from the scheduler to the ElevatorControl
 */
public class ElevatorRequest implements Comparable<ElevatorRequest> {
	private int destinationFloor;
	private int requestID;
	private Direction direction;

	/**
	 * The constructor for the ElevatorRequest class
	 * @param destinationFloor The destination floor
	 * @param requestID The id of the request
	 */
	public ElevatorRequest(int destinationFloor, int requestID) {
		this.destinationFloor = destinationFloor;
		this.direction = null;
		this.requestID = requestID;
	}

	/**
	 * Another constructor
	 * @param destinationFloor the destination floor
	 * @param requestID the id of the request
	 * @param direction the direction of the elevator
	 */
	public ElevatorRequest(int destinationFloor, int requestID, Direction direction) {
		this.destinationFloor = destinationFloor;
		this.direction = direction;
		this.requestID = requestID;
	}

	/**
	 * Get the destination floor
	 * @return destination floor
	 */
	public int getFloor() {
		return this.destinationFloor;
	}


	/**
	 * Get the request ID
	 * @return the ID of the request
	 */
	public int getRequestID() {
		return this.requestID;
	}

	/**
	 * The direction of the elevator request
	 * @return the direction
	 */
	public Direction getDirection() {
		return this.direction;
	}

	/**
	 * Used to comapre two elevator requests
	 * @param o the other elevator request
	 * @return the difference between the two requests
	 */
	@Override
	public int compareTo(ElevatorRequest o) {
		return this.destinationFloor - o.destinationFloor;
	}
}
