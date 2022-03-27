package elevators;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Timer;
import java.util.TimerTask;

import com.google.protobuf.InvalidProtocolBufferException;

import elevatorCommands.FaultType;
import elevatorCommands.SchedulerDispatchMessage;
import elevatorCommands.SimulateFaultMessage;
import protoBufHelpers.ProtoBufMessage;
import stateMachine.State;

/**
 * Represents the boarding state of an elevator, on timeout moves to the idle state
 */
public class BoardingState extends TimerTask implements State {
	private Elevator elevator;
	private Timer timer;
	private final static int TIMEOUT = 4;
	
	/**
	 * Constructor
	 * 
	 * @param elevator instance of elevator
	 */
	protected BoardingState(Elevator elevator){
		this.elevator = elevator;
		this.timer = new Timer();
	}
	
	@Override
	public void run() {
		try {
			elevator.elevatorFSM.updateFSM(null);
		} catch (IOException e) {
			Elevator.LOGGER.error("Failed to send update FSM message, stopping elevator:" + e.getMessage());
			elevator.running = false;
		}
	}
	
	@Override
	public void entryActions() {
		elevator.openDoors();
		timer.schedule(this, TIMEOUT * 1000, TIMEOUT * 1000);
	}
	
	@Override
	public void exitActions() {
		timer.cancel();
		if (elevator.isDoorAtFault) {
			try {
				elevator.sendFaultMessage(FaultType.DOORFAULT);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			while(elevator.isDoorAtFault) {
				DatagramPacket recvMessage;
				try {
					recvMessage = elevator.receiveMessage();
				} catch (IOException e1) {
					e1.printStackTrace();
					continue;
				}
				ProtoBufMessage msg;
				try {
					msg = new ProtoBufMessage(recvMessage);
				} catch (InvalidProtocolBufferException e) {
					e.printStackTrace();
					continue;
				}
				if (msg.isElevatorSimulateFaultMessage()) {
					SimulateFaultMessage eFault = msg.toElevatorSimulateFaultMessage();
					if(eFault.getFault() == FaultType.DOORFAULT && eFault.getTimeout() == 0) {
						Elevator.LOGGER.info("Removing door fault condition!");
						elevator.isDoorAtFault = false;
					}
				}
			}
			try {
				elevator.sendFaultMessage(FaultType.RESOLVED);
			} catch (IOException e) {
				e.printStackTrace();
			}
			elevator.closeDoors();
		} else {
			elevator.closeDoors();
		}
	}
	
	@Override
	public State nextState(ProtoBufMessage message) throws IOException {
		if (message == null) { //timeout trigger
			return new IdleState(elevator);
		} else if(message.isSchedulerDispatchMessage()) { //if message from scheduler
			SchedulerDispatchMessage msg = message.toSchedulerDispatchMessage();
			if(msg.getDestFloor() == elevator.getCurrentFloor()) {
				Elevator.LOGGER.info("Elevator " + elevator.elevatorID + ": Dispatched to floor current floor " + elevator.getCurrentFloor());
				elevator.sendElevatorArrivedMessage();
				return this; //stay in current state
			} else {
				elevator.setDestinationFloor(msg.getDestFloor());
				elevator.updateCurrentDirection();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return new MovingState(elevator);
			}
		}
		throw new IOException("INVALID FSM STATE");
	}
}
