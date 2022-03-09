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
import pbHelpers.PbMessage;
import pbHelpers.UdpPBHelper;
import stateMachine.StateMachine;


public class Elevator extends UdpPBHelper implements  Runnable {
	private final Logger LOGGER = Logger.getLogger(Elevator.class.getName());
	int currentFloor;
	int destinationFloor;
	Direction currentDirection;
	int elevatorID;
	DoorState currentDoorState = DoorState.CLOSE;
	Motor elevatorMotor;
	StateMachine elevatorFSM;
	Boolean running = true;

	enum DoorState{
		OPEN,
		CLOSE,
	}
	
	Elevator(int listenPort, int schedulerPort) throws SocketException {
		super(schedulerPort, listenPort); // create pb interface
		this.currentFloor = 0;
		this.elevatorID = 0;
		this.elevatorMotor = new Motor(this);
		this.elevatorFSM = new StateMachine(new IdleState(this));
	}

	void sendInternalButtonMessage(int floor) throws IOException {
		ElevatorRequestMessage msg = ElevatorRequestMessage.newBuilder()
				.setFloor(floor)
				.setButton(Button.INTERIOR)
				.setElevatorID(this.elevatorID)
				.setDirection(Direction.STATIONARY)
				//TODO ADD TIMESTAMP
				.build();
		sendMessage(msg);
	}

	// Should this include direction?
	void sendFloorSensorMessage() throws IOException {
		FloorSensorMessage msg = FloorSensorMessage.newBuilder()
				.setFloor(this.currentFloor)
				.setElevatorID(this.elevatorID)
				//TODO: ADD TIMESTAMP
				.build();
		sendMessage(msg);
	}

	void sendDepartureMessage() throws IOException {
		ElevatorDepartureMessage msg = ElevatorDepartureMessage.newBuilder()
				.setDirection(this.currentDirection)
				.setInitialFloor(this.currentFloor)
				.setElevatorID(this.elevatorID)
				//TODO: ADD TIMESTAMP
				.build();
		sendMessage(msg);
	}

	void sendElevatorArrivedMessage() throws IOException {
 		ElevatorArrivedMessage msg = ElevatorArrivedMessage.newBuilder()
				.setElevatorID(this.elevatorID)
				.setFloor(this.currentFloor)
				//TODO: ADD TIMESTAMP
				.build();
		sendMessage(msg);
	}

	@Override
	public void run() {
		while(this.running){
			try {
				DatagramPacket recvMessage = receiveMessage(); // wait for message from scheduler
				this.elevatorFSM.updateFSM(new PbMessage(recvMessage)); // update fsm
			}catch (IOException e){
				this.running = false;
				break;
			}
		}
	}
	void motorUpdate() throws IOException {
		if(this.currentDirection == Direction.UP){
			this.currentFloor++;
		} else {
			this.currentFloor--;
		}
		sendFloorSensorMessage();
		this.elevatorFSM.updateFSM(null); // poke with null message
	}

	protected void setDestinationFloor(int floor) {
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
		return true;
	}



}
