package elevators;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import elevatorCommands.SchedulerDispatchMessage;
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
		elevator.closeDoors();
		timer.cancel();
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
					Thread.sleep(1000);
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
