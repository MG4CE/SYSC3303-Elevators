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

	// Should this include direction?
	protected void sendFloorSensorMessage() throws IOException {
		FloorSensorMessage msg = FloorSensorMessage.newBuilder()
				.setFloor(this.currentFloor)
				.setElevatorID(this.elevatorID)
				//TODO: ADD TIMESTAMP
				.build();
		sendMessage(msg, schedulerPort);
	}

	protected void sendDepartureMessage() throws IOException {
		ElevatorDepartureMessage msg = ElevatorDepartureMessage.newBuilder()
				.setDirection(this.currentDirection)
				.setInitialFloor(this.currentFloor)
				.setElevatorID(this.elevatorID)
				//TODO: ADD TIMESTAMP
				.build();
		sendMessage(msg, schedulerPort);
	}

	protected void sendElevatorArrivedMessage() throws IOException {
 		ElevatorArrivedMessage msg = ElevatorArrivedMessage.newBuilder()
				.setElevatorID(this.elevatorID)
				.setFloor(this.currentFloor)
				//TODO: ADD TIMESTAMP
				.build();
		sendMessage(msg, schedulerPort);
	}

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

	protected void motorUpdate() throws IOException {
		if(this.currentDirection == Direction.UP){
			this.currentFloor++;
		} else if(this.currentDirection == Direction.DOWN) {
			this.currentFloor--;
		}
		LOGGER.info("Elevator Passing floor " + Integer.toString(currentFloor));
		sendFloorSensorMessage();
		this.elevatorFSM.updateFSM(null); // poke with null message
	}

	protected void setDestinationFloor(int floor) {
		LOGGER.info("Elevator dispatched to floor " + Integer.toString(floor));
		this.destinationFloor = floor;
	}

	protected int getDestinationFloor() {
		return this.destinationFloor;
	}

	protected int getCurrentFloor() {
		return this.currentFloor;
	}

	protected void updateCurrentDirection() {
		if(this.currentFloor < this.destinationFloor) {
			this.currentDirection = Direction.UP;
		}else {
			this.currentDirection = Direction.DOWN;
		}
	}

	public void pushElevatorButton(int floor) throws IOException {
		sendInternalButtonMessage(floor);
	}

	protected Direction getCurrentDirection() {
		return this.currentDirection;
	}

	protected void openDoors() {
		this.currentDoorState = DoorState.OPEN;
	}

	protected void closeDoors() {
		this.currentDoorState = DoorState.OPEN;
	}

	protected Boolean isElevatorArriving() {
		if(this.currentDirection == Direction.UP && this.currentFloor == this.destinationFloor -1) {
			return true;
		}else if (this.currentDirection == Direction.DOWN && this.currentFloor == this.destinationFloor +1) {
			return true;
		}
		return false;
	}



}
