package elevators;

import java.io.IOException;

import protoBufHelpers.ProtoBufMessage;
import stateMachine.State;

public class OutOfServiceState implements State {
	private Elevator elevator;
	protected OutOfServiceState(Elevator elevator) {
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
		
		return null;
	}

}
