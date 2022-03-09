package elevators;

import java.io.IOException;
import java.net.SocketException;

import elevatorCommands.Button;
import elevatorCommands.Direction;
import elevatorCommands.ElevatorArrivedMessage;
import elevatorCommands.ElevatorDepartureMessage;
import elevatorCommands.ElevatorRequestMessage;
import elevatorCommands.FloorSensorMessage;
import pbHelpers.UdpPBHelper;
import stateMachine.StateMachine;

public class Elevator extends UdpPBHelper {
	int currentFloor;
	int destinationFloor;
	Direction currentDirection;
	int elevatorID;
	DoorState currentDoorState = DoorState.CLOSE;
	Motor elevatorMotor;
	StateMachine elevatorFSM;

	enum DoorState{
		OPEN,
		CLOSE,
	}
	
	Elevator(int listenPort, int schedulerPort) throws SocketException {
		super(schedulerPort, listenPort); // create pb interface
		this.currentFloor = 0;
		this.elevatorID = 0;
		this.elevatorFSM = new StateMachine(new IdleState(this));
	}

	void sendInternalButtonMessage() throws IOException {
		ElevatorRequestMessage msg = ElevatorRequestMessage.newBuilder()
				.setFloor(this.currentFloor)
				.setButton(Button.INTERIOR)
				.setElevatorID(this.elevatorID)
				.setDirection(Direction.STATIONARY)
				//TODO ADD TIMESTAMP
				.build();
		sendMessage(msg);
	}

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

	void motorUpdate() throws IOException {
		if(this.currentDirection == Direction.UP){
			this.currentFloor++;
		} else {
			this.currentFloor--;
		}
		this.elevatorFSM.fireFSM(null); // poke with null message
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
