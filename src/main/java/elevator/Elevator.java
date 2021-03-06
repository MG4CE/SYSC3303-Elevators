package elevator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;

import com.google.protobuf.InvalidProtocolBufferException;

import communication.ProtoBufMessage;
import communication.UDPHelper;
import message.Button;
import message.Direction;
import message.ElevatorArrivedMessage;
import message.ElevatorDepartureMessage;
import message.ElevatorRegisterMessage;
import message.ElevatorRequestMessage;
import message.FaultMessage;
import message.FaultType;
import message.FloorSensorMessage;
import message.SimulateFaultMessage;
import statemachine.StateMachine;

/**
 * Represents a moving elevator that functions using a state machine, which
 * listens for control messages from the scheduler.
 */
public class Elevator extends UDPHelper implements Runnable {
	protected static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(Elevator.class);

	private int schedulerPort;
	private InetAddress schedulerAddress;
	private int currentFloor;
	private int destinationFloor;
	private Direction currentDirection;

	protected int elevatorID;
	protected Motor elevatorMotor;
	protected StateMachine elevatorFSM;
	protected Boolean running;
	protected Boolean isDoorAtFault;

	/**
	 * Constructor takes in scheduler connection information and a set receive port
	 * for testing
	 * 
	 * @param schedulerPort    scheduler send port
	 * @param schedulerAddress target address to send packets to
	 * @param receivePort      sets the internal socket port
	 */
	public Elevator(int schedulerPort, InetAddress schedulerAddress, int receivePort) throws SocketException {
		super(receivePort); // takes in a receive port just for testing
		this.schedulerPort = schedulerPort;
		this.schedulerAddress = schedulerAddress;
		this.currentFloor = 0;
		this.elevatorID = 0;
		this.elevatorMotor = new Motor(this);
		this.elevatorFSM = new StateMachine(new IdleState(this));
		this.running = true;
		this.isDoorAtFault = false;
	}

	/**
	 * Constructor takes in scheduler connection information
	 * 
	 * @param schedulerPort    scheduler send port
	 * @param schedulerAddress target address to send packets to
	 */
	public Elevator(int schedulerPort, InetAddress schedulerAddress) throws SocketException {
		super();
		this.schedulerPort = schedulerPort;
		this.schedulerAddress = schedulerAddress;
		this.currentFloor = 0;
		this.elevatorID = 0;
		this.elevatorMotor = new Motor(this);
		this.elevatorFSM = new StateMachine(new IdleState(this));
		this.running = true;
		this.isDoorAtFault = false;
	}

	/**
	 * Send a UDP message indicating elevator had internal button pressed
	 * 
	 * @param floor number of button pressed
	 */
	protected void sendInternalButtonMessage(int floor) throws IOException {
		ElevatorRequestMessage msg = ElevatorRequestMessage.newBuilder().setFloor(floor).setButton(Button.INTERIOR)
				.setElevatorID(this.elevatorID).setDirection(Direction.STATIONARY)
				// TODO ADD TIMESTAMP
				.build();
		sendMessage(msg, schedulerPort, schedulerAddress);
	}

	/**
	 * Send a UDP message indicating elevator is passing a floor
	 */
	protected void sendFloorSensorMessage() throws IOException {
		FloorSensorMessage msg = FloorSensorMessage.newBuilder().setFloor(this.currentFloor)
				.setElevatorID(this.elevatorID)
				// TODO: ADD TIMESTAMP
				.build();
		sendMessage(msg, schedulerPort, schedulerAddress);
	}

	/**
	 * Send a UDP message indicating elevator is departing a floor towards a
	 * destination
	 */
	protected void sendDepartureMessage() throws IOException {
		ElevatorDepartureMessage msg = ElevatorDepartureMessage.newBuilder().setDirection(this.currentDirection)
				.setInitialFloor(this.currentFloor).setElevatorID(this.elevatorID)
				// TODO: ADD TIMESTAMP
				.build();
		sendMessage(msg, schedulerPort, schedulerAddress);
	}

	/**
	 * Send a UDP message indicating elevator has arrived at a floor
	 */
	protected void sendElevatorArrivedMessage() throws IOException {
		ElevatorArrivedMessage msg = ElevatorArrivedMessage.newBuilder().setElevatorID(this.elevatorID)
				.setFloor(this.currentFloor)
				// TODO: ADD TIMESTAMP
				.build();
		sendMessage(msg, schedulerPort, schedulerAddress);
	}
	
	protected void sendFaultMessage(FaultType f) throws IOException {
		FaultMessage msg = FaultMessage.newBuilder()
				.setElevatorID(this.elevatorID)
				.setFault(f)
				// TODO: ADD TIMESTAMP
				.build();
		sendMessage(msg, schedulerPort, schedulerAddress);
	}

	/**
	 * Send a UDP message to register the elevator with the scheduler
	 */
	protected DatagramPacket sendElevatorRegisterMessage() throws IOException {
		ElevatorRegisterMessage msg = ElevatorRegisterMessage.newBuilder().setFloor(this.currentFloor)
				// TODO: ADD TIMESTAMP
				.build();
		return rpcSendMessage(msg, schedulerPort, schedulerAddress);
	}

	/**
	 * Run method to start an elevator
	 */
	@Override
	public void run() {
		// Send a register message to the elevator and wait for a respone
		DatagramPacket resp = null;
		try {
			resp = sendElevatorRegisterMessage();
		} catch (IOException e) {
			LOGGER.error("Failed to send registration message, stopping elevator: " + e.getMessage());
			return;
		}

		// Convert data to protobuf
		ProtoBufMessage r = null;
		try {
			r = new ProtoBufMessage(resp);
		} catch (InvalidProtocolBufferException e) {
			LOGGER.error(
					"Failed to convert received packet into protobuf message, stopping elevator: " + e.getMessage());
			return;
		}

		// If the data is of type ElevatorRegisterMessage fetch the assigned id and set
		// it
		if (r.isElevatorRegisterMessage()) {
			ElevatorRegisterMessage regResp = r.toElevatorRegisterMessage();
			elevatorID = regResp.getElevatorID();
		} else {
			LOGGER.error("Unknown message type, stopping elevator!");
			return;
		}
		
		LOGGER.info("Registered with scheduler, assigned ID is " + elevatorID);

		// Forward any messages from the elevator into the state machine
		while (this.running) {
			try {
				DatagramPacket recvMessage = receiveMessage(); // wait for message from scheduler
				ProtoBufMessage msg = new ProtoBufMessage(recvMessage);
				if (msg.isElevatorSimulateFaultMessage()) {
					SimulateFaultMessage eFault = msg.toElevatorSimulateFaultMessage();
					if(eFault.getFault() == FaultType.DOORFAULT && eFault.getTimeout() != 0) {
						LOGGER.info("Setting door fault to trigger!");
						isDoorAtFault = true;
					} else if(eFault.getFault() == FaultType.DOORFAULT && eFault.getTimeout() == 0) {
						LOGGER.info("Disabling door fault to trigger!");
						isDoorAtFault = false;
					} else if (eFault.getFault() == FaultType.ELEVATOR_UNRESPONSIVE) {
						LOGGER.warn("Elevator UNRESPONSIVE simulation condition received, stopping elevator!");
						System.exit(1);
					}
				} else {
					this.elevatorFSM.updateFSM(msg);
				}
			} catch (IOException e) {
				LOGGER.error("FSM update failed, stopping elevator:" + e.getMessage());
				this.running = false;
				break;
			}
		}
	}

	/**
	 * To be called by elevator motor, updates floor after an interval has passed
	 */
	protected void motorUpdate() throws IOException {
		if (this.currentDirection == Direction.UP) {
			this.currentFloor++;
		} else if (this.currentDirection == Direction.DOWN) {
			this.currentFloor--;
		}
		LOGGER.info("Elevator " + this.elevatorID + ": passing floor " + this.currentFloor);
		sendFloorSensorMessage();
		this.elevatorFSM.updateFSM(null); // poke with null message
	}

	/**
	 * Set the destination floor to a new floor
	 * 
	 * @param floor destination floor
	 */
	protected void setDestinationFloor(int floor) {
		this.destinationFloor = floor;
		LOGGER.info("Elevator " + this.elevatorID + ": Dispatched to floor " + this.destinationFloor);
	}

	/**
	 * Get the current destination floor of the elevator
	 * 
	 * @return destination floor
	 */
	protected int getDestinationFloor() {
		return this.destinationFloor;
	}

	/**
	 * Get the current floor of the elevator
	 * 
	 * @return current floor
	 */
	public int getCurrentFloor() {
		return this.currentFloor;
	}

	/**
	 * Update the direction of the elevator based on its new destination floor
	 */
	protected void updateCurrentDirection() {
		if (this.currentFloor < this.destinationFloor) {
			this.currentDirection = Direction.UP;
		} else if (this.currentFloor > this.destinationFloor) {
			this.currentDirection = Direction.DOWN;
		} else {
			this.currentDirection = Direction.STATIONARY;
		}
	}

	/**
	 * Simulates a button press from within the elevator
	 * 
	 * @param floor (i.e. button pressed)
	 */
	public void pushElevatorButton(int floor) throws IOException {
		LOGGER.info("Elevator " + this.elevatorID + ": Had button " + floor + " pressed");
		sendInternalButtonMessage(floor);
	}

	/**
	 * Get current direction of elevator
	 * 
	 * @return Direction
	 */
	public Direction getCurrentDirection() {
		return this.currentDirection;
	}

	/**
	 * Open the doors
	 */
	protected void openDoors() {
		LOGGER.info("Elevator " + this.elevatorID + ": Doors opening");
	}

	/**
	 * Close the doors
	 */
	protected void closeDoors() {
		LOGGER.info("Elevator " + this.elevatorID + ": Doors closing");
	}

	/**
	 * Check if elevator is 1 floor away from its current destination
	 * 
	 * @return true or false
	 */
	protected Boolean isElevatorArriving(Boolean print) {
		if (this.currentDirection == Direction.UP && this.currentFloor == this.destinationFloor - 1) {
			if (print) {
				LOGGER.info("Elevator " + this.elevatorID + ": Arriving at floor " + this.destinationFloor);
			}
			return true;
		} else if (this.currentDirection == Direction.DOWN && this.currentFloor == this.destinationFloor + 1) {
			if (print) {
				LOGGER.info("Elevator " + this.elevatorID + ": Arriving at floor " + this.destinationFloor);
			}
			return true;
		}
		return false;
	}

	/**
	 * Check if elevator is at the destination floor
	 * 
	 * @return true or false
	 */
	protected Boolean isElevatorArrived() {
		if (this.currentFloor == this.destinationFloor) {
			LOGGER.info("Elevator " + this.elevatorID + ": Arrived at floor " + this.destinationFloor);
			return true;
		}
		return false;
	}

	/**
	 * Get the schedulers address
	 * 
	 * @return schedulers address
	 */
	public InetAddress getSchedulerAddress() {
		return this.schedulerAddress;
	}

	/**
	 * Set the schedulers address
	 * 
	 * @param address schedulers address
	 */
	public void setSchedulerAddress(InetAddress address) {
		this.schedulerAddress = address;
	}

	/**
	 * Get the current elevator fsm
	 * 
	 * @return elevatorFSM
	 */
	public StateMachine getEFSM() {
		return this.elevatorFSM;
	}

	/**
	 * Get the elevators ID
	 * 
	 * @return
	 */
	public int getElevatorId() {
		return this.elevatorID;
	}
	
	public void killElevator() {
		this.running = false;
	}

	/**
	 * Create an instance of elevator and start it.
	 */
	public static void main(String[] args) {
		Elevator e = null;
		try {
			e = new Elevator(6969, InetAddress.getLocalHost());
		} catch (SocketException e1) {
			e1.printStackTrace();
			return;
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
			return;
		}
		Thread tE = new Thread(e);
		tE.start();
	}
}
