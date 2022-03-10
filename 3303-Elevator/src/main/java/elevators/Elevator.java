package elevators;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.SocketException;
import java.util.logging.Logger;

import elevatorCommands.Button;
import elevatorCommands.Direction;
import elevatorCommands.ElevatorArrivedMessage;
import elevatorCommands.ElevatorDepartureMessage;
import elevatorCommands.ElevatorRequestMessage;
import elevatorCommands.FloorSensorMessage;
import protoBufHelpers.ProtoBufMessage;
import protoBufHelpers.UDPHelper;
import stateMachine.StateMachine;

public class Elevator extends UDPHelper implements Runnable {
	protected final Logger LOGGER = Logger.getLogger(Elevator.class.getName());
	
	private int schedulerPort;
	private int currentFloor;
	private int destinationFloor;
	private Direction currentDirection;
	private int elevatorID;
	private DoorState currentDoorState;
	
	protected Motor elevatorMotor;
	protected StateMachine elevatorFSM;
	protected Boolean running;

	private enum DoorState{
		OPEN,
		CLOSE,
	}

	/*
	 * Constructor for elevator class, takes in the scheduler to send to, as well as the port it should listen to
	 */
	public Elevator(int schedulerPort, int receivePort) throws SocketException {
		super(receivePort); //takes in a receive port just for testing
		this.schedulerPort = schedulerPort;
		this.currentFloor = 0;
		this.elevatorID = 0;
		this.elevatorMotor = new Motor(this);
		this.elevatorFSM = new StateMachine(new IdleState(this));
		this.currentDoorState = DoorState.CLOSE;
		this.running = true;
	}

	/*
	 * Send a UDP message indicating elevator had internal button pressed
	 * @param floor number of button pressed
	 */
	protected void sendInternalButtonMessage(int floor) throws IOException {
		ElevatorRequestMessage msg = ElevatorRequestMessage.newBuilder()
				.setFloor(floor)
				.setButton(Button.INTERIOR)
				.setElevatorID(this.elevatorID)
				.setDirection(Direction.STATIONARY)
				//TODO ADD TIMESTAMP
				.build();
		sendMessage(msg, schedulerPort);
	}

	/*
	 * Send a UDP message indicating elevator is passing a floor
	 */
	protected void sendFloorSensorMessage() throws IOException {
		FloorSensorMessage msg = FloorSensorMessage.newBuilder()
				.setFloor(this.currentFloor)
				.setElevatorID(this.elevatorID)
				//TODO: ADD TIMESTAMP
				.build();
		sendMessage(msg, schedulerPort);
	}

	/*
	 * Send a UDP message indicating elevator is departing a floor towards a destination
	 */
	protected void sendDepartureMessage() throws IOException {
		ElevatorDepartureMessage msg = ElevatorDepartureMessage.newBuilder()
				.setDirection(this.currentDirection)
				.setInitialFloor(this.currentFloor)
				.setElevatorID(this.elevatorID)
				//TODO: ADD TIMESTAMP
				.build();
		sendMessage(msg, schedulerPort);
	}

	/*
	 * Send a UDP message indicating elevator has arrived at a floor
	 */
	protected void sendElevatorArrivedMessage() throws IOException {
 		ElevatorArrivedMessage msg = ElevatorArrivedMessage.newBuilder()
				.setElevatorID(this.elevatorID)
				.setFloor(this.currentFloor)
				//TODO: ADD TIMESTAMP
				.build();
		sendMessage(msg, schedulerPort);
	}

	/*
	 * Run method to start an elevator after creating a new instance of it
	 */
	@Override
	public void run() {
		while(this.running){
			try {
				DatagramPacket recvMessage = receiveMessage(); // wait for message from scheduler
				this.elevatorFSM.updateFSM(new ProtoBufMessage(recvMessage)); // update fsm
			}catch (IOException e){
				LOGGER.severe(e.getMessage());
				this.running = false;
				break;
			}
		}
	}

	/*
	 * To be called by elevator motor, updates floor after an interval has passed
	 */
	protected void motorUpdate() throws IOException {
		if(this.currentDirection == Direction.UP){
			this.currentFloor++;
		} else if(this.currentDirection == Direction.DOWN) {
			this.currentFloor--;
		}
		System.out.printf("Elevator %d passing floor %d", this.elevatorID, this.currentFloor);
		sendFloorSensorMessage();
		this.elevatorFSM.updateFSM(null); // poke with null message
	}

	/*
	 * Set the destination floor to a new floor
	 */
	protected void setDestinationFloor(int floor) {
		LOGGER.info("Elevator dispatched to floor " + Integer.toString(floor));
		this.destinationFloor = floor;
	}

	/*
	 * Get the current destination floor of the elevator
	 * @return destination floor
	 */
	protected int getDestinationFloor() {
		return this.destinationFloor;
	}

	/*
	 * Get the current floor of the elevator
	 * @return floor
	 */
	protected int getCurrentFloor() {
		return this.currentFloor;
	}

	/*
	 * Update the direction of the elevator based on its new destination floor
	 */
	protected void updateCurrentDirection() {
		if(this.currentFloor < this.destinationFloor) {
			this.currentDirection = Direction.UP;
		}else if (this.currentFloor > this.destinationFloor) {
			this.currentDirection = Direction.DOWN;
		} else{
			this.currentDirection = Direction.STATIONARY;
		}
	}

	/*
	 * Simulates a button press from within the elevator
	 * @param destination floor (i.e. button pressed)
	 */
	public void pushElevatorButton(int floor) throws IOException {
		System.out.printf("Elevator %d had button %d pressed\n", this.elevatorID, floor);
		sendInternalButtonMessage(floor);
	}

	/*
	 * Get current direction of elevator
	 * @return Direction
	 */
	protected Direction getCurrentDirection() {
		return this.currentDirection;
	}

	/*
	 * Open the doors
	 */
	protected void openDoors() {
		System.out.println("Doors opening on elevator " + Integer.toString(this.elevatorID));
		this.currentDoorState = DoorState.OPEN;
	}

	/*
	 * Close the doors
	 */
	protected void closeDoors() {
		System.out.printf("Doors closing on elevator %d\n",  this.elevatorID);
		this.currentDoorState = DoorState.OPEN;
	}

	/*
	 * Check if elevator is 1 floor away from its current destination
	 * @return true or false
	 */
	protected Boolean isElevatorArriving() {
		System.out.printf("Elevator %d arriving at floor %d\n", this.elevatorID, this.destinationFloor);
		if(this.currentDirection == Direction.UP && this.currentFloor == this.destinationFloor -1) {
			return true;
		}else if (this.currentDirection == Direction.DOWN && this.currentFloor == this.destinationFloor +1) {
			return true;
		}
		return false;
	}



}
