package elevators;

import java.io.IOException;

import elevatorCommands.SchedulerDispatchMessage;
import protoBufHelpers.ProtoBufMessage;
import stateMachine.State;

public class MovingState implements State{
	private Elevator elevator; // hold ref to elevator
	
	protected MovingState(Elevator elevator){
		this.elevator = elevator;
	}
	
	@Override
	public void entryActions() {
		// TODO Auto-generated method stub
		if(elevator.isElevatorArriving(false)) {
			try {
				elevator.elevatorFSM.updateFSM(null);
			} catch (IOException e) {
				e.printStackTrace();
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
		if (message == null) { // internal motor triggering FSM (floor change!)
			if(elevator.isElevatorArriving(true)) {
				System.out.println("moving to arriving");
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
