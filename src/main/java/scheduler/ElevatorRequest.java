package scheduler;

import message.Button;
import message.Direction;

/**
 * The ElevatorRequest class if used to facilitate requests from the scheduler to the ElevatorControl
 */
public class ElevatorRequest implements Comparable<ElevatorRequest> {
	private int destinationFloor;
	private int requestID;
	private Direction direction;
	private Button requestType;

	/**
	 * The constructor for the ElevatorRequest class
	 * @param destinationFloor The destination floor
	 * @param requestID The id of the request
	 */
	public ElevatorRequest(int destinationFloor, int requestID, Button requestType) {
		this.destinationFloor = destinationFloor;
		this.direction = null;
		this.requestID = requestID;
		this.requestType = requestType;
	}

	/**
	 * Another constructor
	 * @param destinationFloor the destination floor
	 * @param requestID the id of the request
	 * @param direction the direction of the elevator
	 */
	public ElevatorRequest(int destinationFloor, int requestID, Direction direction, Button requestType) {
		this.destinationFloor = destinationFloor;
		this.direction = direction;
		this.requestID = requestID;
		this.requestType = requestType;
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
	 * Get the request type of the elevator request
	 * @return ButtonType
	 */
	public Button getRequestType() {
		return this.requestType;
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
