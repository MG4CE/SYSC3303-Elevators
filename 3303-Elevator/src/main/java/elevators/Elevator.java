package elevators;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Logger;

import com.google.protobuf.InvalidProtocolBufferException;

import elevatorCommands.Button;
import elevatorCommands.Direction;
import elevatorCommands.ElevatorArrivedMessage;
import elevatorCommands.ElevatorDepartureMessage;
import elevatorCommands.ElevatorRegisterMessage;
import elevatorCommands.ElevatorRequestMessage;
import elevatorCommands.FloorSensorMessage;
import protoBufHelpers.ProtoBufMessage;
import protoBufHelpers.UDPHelper;
import stateMachine.StateMachine;

public class Elevator extends UDPHelper implements Runnable {
	protected final Logger LOGGER = Logger.getLogger(Elevator.class.getName());
	
	private int schedulerPort;
	private InetAddress schedulerAddress;
	private int currentFloor;
	private int destinationFloor;
	private Direction currentDirection;
	private int elevatorID;
	
	protected Motor elevatorMotor;
	protected StateMachine elevatorFSM;
	protected Boolean running;

	/*
	 * Constructor for elevator class, takes in the scheduler to send to, as well as the port it should listen to
	 */
	public Elevator(int schedulerPort, InetAddress schedulerAddress, int receivePort) throws SocketException {
		super(receivePort); //takes in a receive port just for testing
		this.schedulerPort = schedulerPort;
		this.schedulerAddress = schedulerAddress;
		this.currentFloor = 0;
		this.elevatorID = 0;
		this.elevatorMotor = new Motor(this);
		this.elevatorFSM = new StateMachine(new IdleState(this));
		this.running = true;
	}
	
	public Elevator(int schedulerPort, InetAddress schedulerAddress) throws SocketException {
		super();
		this.schedulerPort = schedulerPort;
		this.schedulerAddress = schedulerAddress;
		this.currentFloor = 0;
		this.elevatorID = 0;
		this.elevatorMotor = new Motor(this);
		this.elevatorFSM = new StateMachine(new IdleState(this));
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
		sendMessage(msg, schedulerPort, schedulerAddress);
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
		sendMessage(msg, schedulerPort, schedulerAddress);
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
		sendMessage(msg, schedulerPort, schedulerAddress);
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
		sendMessage(msg, schedulerPort, schedulerAddress);
	}
	
	protected DatagramPacket sendElevatorRequestMessage() throws IOException {
 		ElevatorArrivedMessage msg = ElevatorArrivedMessage.newBuilder()
				.setFloor(this.currentFloor)
				//TODO: ADD TIMESTAMP
				.build();
		return rpcSendMessage(msg, schedulerPort, schedulerAddress);
	}

	/*
	 * Run method to start an elevator after creating a new instance of it
	 */
	@Override
	public void run() {
		DatagramPacket resp = null;
		try {
			resp = sendElevatorRequestMessage();
		} catch (IOException e) {
			e.printStackTrace();
			LOGGER.severe("No message received, stopping elevator: " + e.getMessage());
			return;
		}
		
		ProtoBufMessage r;
		try {
			r = new ProtoBufMessage(resp);
		} catch (InvalidProtocolBufferException e) {
			LOGGER.severe(e.getMessage());
			return;
		}
		
		if (r.isElevatorRegisterMessage()) {
			ElevatorRegisterMessage regResp = r.toElevatorRegisterMessage();
			elevatorID = regResp.getElevatorID();
		} else {
			LOGGER.severe("Unkownn message type, stopping elevator!");
			return;
		}
		
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
		System.out.printf("Elevator %d passing floor %d\n", this.elevatorID, this.currentFloor);
		sendFloorSensorMessage();
		this.elevatorFSM.updateFSM(null); // poke with null message
	}

	/*
	 * Set the destination floor to a new floor
	 */
	protected void setDestinationFloor(int floor) {
		this.destinationFloor = floor;
		System.out.printf("Elevator %d: Dispatched to floor %d\n", this.elevatorID, this.destinationFloor);

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
		System.out.printf("Elevator %d: Had button %d pressed\n", this.elevatorID, floor);
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
		System.out.printf("Elevator %d: Doors opening\n",  this.elevatorID);
	}

	/*
	 * Close the doors
	 */
	protected void closeDoors() {
		System.out.printf("Elevator %d: Doors closing\n",  this.elevatorID);
	}

	/*
	 * Check if elevator is 1 floor away from its current destination
	 * @return true or false
	 */
	protected Boolean isElevatorArriving() {
		if(this.currentDirection == Direction.UP && this.currentFloor == this.destinationFloor -1) {
			System.out.printf("Elevator %d: Arriving at floor %d\n", this.elevatorID, this.destinationFloor);
			return true;
		}else if (this.currentDirection == Direction.DOWN && this.currentFloor == this.destinationFloor +1) {
			System.out.printf("Elevator %d: Arriving at floor %d\n", this.elevatorID, this.destinationFloor);
			return true;
		}
		return false;
	}

	protected Boolean isElevatorArrived() {
		if (this.currentFloor == this.destinationFloor) {
			System.out.printf("Elevator %d: Arrived at floor %d\n", this.elevatorID, this.destinationFloor);
			return true;
		}
		return false;
	}
	
	public InetAddress getSchedulerAddress() {
		return this.schedulerAddress;
	}
	
	public void setSchedulerAddress(InetAddress address) {
		this.schedulerAddress = address;
	}
}
