package elevator;

import java.io.IOException;

import communication.ProtoBufMessage;
import statemachine.State;

/**
 * Represents the arriving state of an elevator
 */
public class ArrivingState implements State {
	private Elevator elevator;
	
	/**
	 * Constructor
	 * 
	 * @param elevator instance of elevator
	 */
	protected ArrivingState(Elevator elevator){
		this.elevator = elevator;
	}
	
	@Override
	public void entryActions() {
		// TODO Auto-generated method stub
	}

	@Override
	public void exitActions() {
		this.elevator.elevatorMotor.stopMotor();
		Elevator.LOGGER.info("Elevator has arrived at floor " + elevator.getCurrentFloor());
	}
	
	@Override
	public State nextState(ProtoBufMessage message) throws IOException {
		if(message == null) { //internal message from motors
			if(elevator.isElevatorArrived()) {
				elevator.sendElevatorArrivedMessage(); //let scheduler know arrived
				return new BoardingState(elevator); //Arrived!
			}
		}
		//bug prone here we might get a message here
		throw new IOException("INVALID FSM STATE");
	}
}
