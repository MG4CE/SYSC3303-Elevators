package elevators;

import java.io.IOException;

import elevatorCommands.Direction;
import elevatorCommands.FaultType;
import elevatorCommands.SchedulerDispatchMessage;
import protoBufHelpers.ProtoBufMessage;
import stateMachine.State;

/**
 * Represents the moving state of an elevator
 */
public class MovingState implements State{
	private Elevator elevator;
	
	protected MovingState(Elevator elevator){
		this.elevator = elevator;
	}
	
	@Override
	public void entryActions() {
		if(elevator.isElevatorArriving(false)) { //if the dest floor is one away
			try {
				//send null message to fsm to go to arriving state
				elevator.elevatorFSM.updateFSM(null); 
			} catch (IOException e) {
				Elevator.LOGGER.error("Failed to send update FSM message, stopping elevator:" + e.getMessage());
				elevator.running = false;
			}
			elevator.elevatorMotor.moveOneFloor();
		} else {
			elevator.elevatorMotor.startMotor();
		}
	}

	@Override
	public void exitActions() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public State nextState(ProtoBufMessage message) throws IOException {
		if (message == null) { //internal motor triggering FSM (floor change!)
			if(elevator.isElevatorArriving(true)) {
				return new ArrivingState(elevator);
			} else {
				return this; //still moving to destination
			}
		} else if(message.isSchedulerDispatchMessage()) { //if message from scheduler
			SchedulerDispatchMessage msg = message.toSchedulerDispatchMessage();
			if (elevator.getCurrentFloor() > msg.getDestFloor() && elevator.getCurrentDirection() == Direction.UP) {
				elevator.sendFaultMessage(FaultType.SCHEDULE_FAULT);
			} else if (elevator.getCurrentFloor() < msg.getDestFloor() && elevator.getCurrentDirection() == Direction.DOWN) {
				elevator.sendFaultMessage(FaultType.SCHEDULE_FAULT);
			}
			elevator.setDestinationFloor(msg.getDestFloor());
			elevator.updateCurrentDirection(); //get elevator moving towards new dest
			return this;
		}
		throw new IOException("INVALID FSM STATE");
	}
}
