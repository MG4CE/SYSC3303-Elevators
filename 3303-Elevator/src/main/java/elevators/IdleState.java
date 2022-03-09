package elevators;

import java.io.IOException;
import java.util.logging.Logger;

import elevatorCommands.SchedulerDispatchMessage;
import pbHelpers.PbMessage;
import stateMachine.State;

public class IdleState implements State {
	Elevator elevator; // hold ref to elevator
	private final Logger LOGGER = Logger.getLogger(IdleState.class.getName());

	IdleState(Elevator elevator){
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
	public State nextState(PbMessage message) throws IOException {
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
		} else { // or return to current state?
			throw new IOException("ELEVATOR FSM IN INVALID STATE");
		}
	}
}
