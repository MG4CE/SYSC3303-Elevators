package elevators;

import java.io.IOException;

import elevatorCommands.Button;
import elevatorCommands.ElevatorRequestMessage;
import elevatorCommands.SchedulerDispatchMessage;
import pbHelpers.PbMessage;
import stateMachine.State;

public class BoardingState implements State {
	Elevator elevator; // hold ref to elevator
	
	BoardingState(Elevator elevator){
		this.elevator = elevator;
	}
	
	@Override
	public void entryActions() {
		elevator.openDoors();
		
	}
	
	@Override
	public void exitActions() {
		elevator.closeDoors();
		
	}
	
	@Override
	public State nextState(PbMessage message) throws IOException {
		if(message.isSchedulerDispatchMessage()) { // if message from scheduler
			SchedulerDispatchMessage msg = message.toSchedulerDispatchMessage();
			if(msg.getDestFloor() == elevator.getCurrentFloor()) {
				return this; // stay in current state
			} else {
				elevator.setDestinationFloor(msg.getDestFloor());
				elevator.updateCurrentDirection();
				return new MovingState(elevator);
			}
			// TODO ADD TIMEOUT
		}
		return this;
	}





}
