package elevators;

import java.io.IOException;

import elevatorCommands.SchedulerDispatchMessage;
import protoBufHelpers.ProtoBufMessage;
import stateMachine.State;

/**
 * Represents the idle state of an elevator
 */
public class IdleState implements State {
	private Elevator elevator;

	/**
	 * Constructor
	 * 
	 * @param elevator instance of elevator
	 */
	protected IdleState(Elevator elevator){
		this.elevator = elevator;
	}

	@Override
	public void entryActions() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exitActions() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public State nextState(ProtoBufMessage message) throws IOException {
		if(message.isSchedulerDispatchMessage()) { // if message from scheduler
			SchedulerDispatchMessage msg = message.toSchedulerDispatchMessage();
			elevator.setDestinationFloor(msg.getDestFloor()); // update destination floor
			
			if(msg.getDestFloor() == elevator.getCurrentFloor()) {
				return new BoardingState(elevator); // return instance of next state
			} else {
				elevator.updateCurrentDirection(); // got a new floor to go to
				elevator.sendDepartureMessage();
				return new MovingState(elevator); // return instance of next state
			}
		} else {
			throw new IOException("ELEVATOR FSM IN INVALID STATE");
		}
	}
}
