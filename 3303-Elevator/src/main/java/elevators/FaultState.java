package elevators;

import java.io.IOException;
import java.util.Timer;

import protoBufHelpers.ProtoBufMessage;
import stateMachine.State;

public class FaultState implements State {
	private Elevator elevator;
	private Timer timer;
	private final static int TIMEOUT = 4;

	protected FaultState(Elevator elevator) {
		this.elevator = elevator;
		this.timer = new Timer();
	}

	@Override
	public void entryActions() {
		if(elevator.doorStatus == DoorStatus.OPEN) {
			elevator.closeDoors();
			if(elevator.doorStatus != DoorStatus.CLOSED) {
				//Out of service call here since door won't close
				try {
					this.elevator.elevatorFSM.updateFSM(null);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		}	
	}

	@Override
	public void exitActions() {
		// TODO Auto-generated method stub
	}

	@Override
	public State nextState(ProtoBufMessage message) throws IOException {
		if(message == null) {
			return new OutOfServiceState(elevator);
		}
		
		
		//Elevator send scheduler a error correction message
		//Tell scheduler current
		//Floor going to -> to send back after correction
		//Current state 
		//Moving -> resend dispatch after correction with old dest floor
		//Idle -> do nothing 
		//Arriving -> do nothing
	
		elevator.sendCorrectionMessage();
		
		//If we never changed destination try to reset values
		if(elevator.getCurrentFloor() == elevator.getDestinationFloor()) {
			//Try reseting elevator values
			if(elevator.doorStatus == DoorStatus.OPEN) {
				elevator.closeDoors();
				if(elevator.doorStatus == DoorStatus.OPEN) {
					return new OutOfServiceState(elevator);
				}
			}
			elevator.setDestinationFloor(elevator.getCurrentFloor());
			elevator.updateCurrentDirection();
			
			return new IdleState(elevator);
		}
		
		//Reset elevator to first floor
		elevator.setDestinationFloor(0);
		elevator.updateCurrentDirection();
		
		
		return new MovingState(elevator);
	}

}
