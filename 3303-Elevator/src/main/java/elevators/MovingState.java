package elevators;

import java.io.IOException;

import elevatorCommands.Direction;
import elevatorCommands.SchedulerDispatchMessage;
import pbHelpers.PbMessage;
import stateMachine.State;

public class MovingState implements State{
	Elevator elevator; // hold ref to elevator
	
	MovingState(Elevator elevator){
		this.elevator = elevator;
	}
	
	@Override
	public void entryActions() {
		// TODO Auto-generated method stub
		elevator.elevatorMotor.startMotor();

	}

	@Override
	public void exitActions() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public State nextState(PbMessage message) throws IOException {
		if (message == null) { // internal motor triggering FSM (floor change!)
			if(elevator.isElevatorArriving()) {
				return new ArrivingState(elevator);
			} else {
				return this; // still moving to destination
			}
		} else if(message.isSchedulerDispatchMessage()) { // if message from scheduler
			SchedulerDispatchMessage msg = message.toSchedulerDispatchMessage();
			elevator.setDestinationFloor(msg.getDestFloor());
			elevator.updateCurrentDirection(); // get elevator moving towards new dest
			return this;
		}
		throw new IOException("INVALID FSM STATE");
	}
}
