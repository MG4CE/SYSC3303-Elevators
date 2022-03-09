package elevators;

import java.io.IOException;

import pbHelpers.PbMessage;
import stateMachine.State;

public class ArrivingState implements State {

	Elevator elevator; // hold ref to elevator
	
	ArrivingState(Elevator elevator){
		this.elevator = elevator;
	}
	
	@Override
	public void entryActions() {
		
	}

	@Override
	public void exitActions() {
		System.out.printf("Elevator has arrived at floor %d\n", elevator.getCurrentFloor());
		// stop motors
	}
	
	@Override
	public State nextState(PbMessage message) throws IOException {
		if(message == null) { // internal message from motors
			if(elevator.getCurrentFloor() == elevator.getDestinationFloor()) {
				elevator.sendElevatorArrivedMessage(); // let scheduler know arrived
				return new BoardingState(elevator); // Arrived!
			}
		}
		return null;
	}
}
