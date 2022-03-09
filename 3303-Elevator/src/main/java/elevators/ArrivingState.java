package elevators;

import java.io.IOException;
import java.util.logging.Logger;

import pbHelpers.PbMessage;
import stateMachine.State;

public class ArrivingState implements State {
	private Elevator elevator; // hold ref to elevator
	
	protected ArrivingState(Elevator elevator){
		this.elevator = elevator;
	}
	
	@Override
	public void entryActions() {}

	@Override
	public void exitActions() {		this.elevator.elevatorMotor.stopMotor();
		elevator.LOGGER.info("Elevator has arrived at floor " + elevator.getCurrentFloor());
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
